package org.dddjava.jig.presentation.view.poi.report;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.dddjava.jig.domain.model.jigmodel.businessrules.BusinessRules;
import org.dddjava.jig.domain.model.jigmodel.collections.CollectionField;
import org.dddjava.jig.domain.model.jigmodel.lowmodel.alias.TypeAlias;
import org.dddjava.jig.domain.model.jigmodel.lowmodel.declaration.method.MethodDeclaration;
import org.dddjava.jig.domain.model.jigmodel.lowmodel.declaration.method.MethodDeclarations;
import org.dddjava.jig.domain.model.jigmodel.lowmodel.declaration.method.MethodIdentifier;
import org.dddjava.jig.domain.model.jigmodel.lowmodel.declaration.package_.PackageIdentifier;
import org.dddjava.jig.domain.model.jigmodel.lowmodel.declaration.text.Text;
import org.dddjava.jig.domain.model.jigmodel.lowmodel.declaration.type.TypeDeclaration;
import org.dddjava.jig.domain.model.jigmodel.lowmodel.declaration.type.TypeIdentifier;
import org.dddjava.jig.domain.model.jigmodel.lowmodel.declaration.type.TypeIdentifiers;
import org.dddjava.jig.domain.model.jigmodel.lowmodel.relation.method.CallerMethods;
import org.dddjava.jig.domain.model.jigmodel.lowmodel.relation.method.UsingFields;
import org.dddjava.jig.domain.model.jigmodel.lowmodel.richmethod.Method;
import org.dddjava.jig.domain.model.jigmodel.services.ServiceMethods;
import org.dddjava.jig.presentation.view.report.ReportItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * 一覧出力項目のフォーマッター
 */
public class ReportItemFormatters {
    static Logger logger = LoggerFactory.getLogger(ReportItemFormatters.class);

    private ConvertContext convertContext;

    public ReportItemFormatters(ConvertContext convertContext) {
        this.convertContext = convertContext;
    }

    void format(ReportItem reportItem, Object item, Cell cell) {
        switch (reportItem) {
            case パッケージ名:
                cell.setCellValue(toPackageIdentifier(item).asText());
                return;
            case パッケージ別名:
                cell.setCellValue(convertContext.aliasService.packageAliasOf(toPackageIdentifier(item)).asText());
                return;
            case クラス名:
            case 単純クラス名:
                cell.setCellValue(toTypeIdentifier(item).asSimpleText());
                return;
            case クラス別名:
                cell.setCellValue(convertContext.aliasService.typeAliasOf(toTypeIdentifier(item)).asText());
                return;
            case メソッドシグネチャ:
                cell.setCellValue(toMethodDeclaration(item).asSignatureSimpleText());
            case メソッド別名:
                cell.setCellValue(convertContext.aliasService.methodAliasOf(toMethodIdentifier(item)).asText());
                return;
            case メソッド戻り値の型:
                cell.setCellValue(toMethodDeclaration(item).methodReturn().asSimpleText());
                return;
            case メソッド戻り値の型の別名:
                cell.setCellValue(convertContext.aliasService.typeAliasOf(toMethodDeclaration(item).methodReturn().typeIdentifier()).asText());
                return;
            case メソッド引数の型の別名: {
                MethodDeclaration methodDeclaration = toMethodDeclaration(item);
                List<TypeAlias> list = methodDeclaration.methodSignature().arguments().stream()
                        .map(convertContext.aliasService::typeAliasOf)
                        .collect(toList());
                writeLongString(cell, Text.of(list, alias -> alias.asText()));
            }
            return;
            case 使用しているフィールドの型:
                writeLongString(cell, ((UsingFields) item).typeIdentifiers().asSimpleText());
                return;
            case フィールドの型: {
                String result = ((CollectionField) item).fieldType().asSimpleText();
                writeLongString(cell, result);
            }
            return;
            case 使用箇所数:
                if (item instanceof CallerMethods) {
                    cell.setCellValue(((CallerMethods) item).size());
                } else {
                    cell.setCellValue(((TypeIdentifiers) item).size());
                }
                return;
            case 使用箇所:
                cell.setCellValue(((TypeIdentifiers) item).asSimpleText());
                return;
            case クラス数:
                cell.setCellValue(((BusinessRules) item).list().size());
                return;
            case メソッド数:
                cell.setCellValue((toMethodDeclarations(item)).number().intValue());
                return;
            case メソッド一覧: {
                writeLongString(cell, toMethodDeclarations(item).asSignatureAndReturnTypeSimpleText());
            }
            return;
            case 分岐数:
                cell.setCellValue(((Method) item).decisionNumber().intValue());
                return;
            case 汎用文字列:
                cell.setCellValue((String) item);
                return;
            case イベントハンドラ:
            case 汎用真偽値:
                cell.setCellValue(((Boolean) item) ? "◯" : "");
                return;
            case 汎用数値:
                cell.setCellValue((int) item);
                return;
        }

        throw new IllegalArgumentException(reportItem.name());
    }

    private MethodDeclarations toMethodDeclarations(Object item) {
        if (item instanceof ServiceMethods) {
            return ((ServiceMethods) item).toMethodDeclarations();
        }
        return (MethodDeclarations) item;
    }

    private MethodIdentifier toMethodIdentifier(Object item) {
        return toMethodDeclaration(item).identifier();
    }

    private PackageIdentifier toPackageIdentifier(Object item) {
        if (item instanceof PackageIdentifier) {
            return (PackageIdentifier) item;
        }
        return toTypeIdentifier(item).packageIdentifier();
    }

    private TypeIdentifier toTypeIdentifier(Object item) {
        if (item instanceof TypeIdentifier) {
            return (TypeIdentifier) item;
        }
        if (item instanceof TypeDeclaration) {
            return ((TypeDeclaration) item).identifier();
        }

        MethodDeclaration methodDeclaration = toMethodDeclaration(item);
        return methodDeclaration.declaringType();
    }

    private MethodDeclaration toMethodDeclaration(Object item) {
        if (item instanceof Method) {
            return ((Method) item).declaration();
        }
        return (MethodDeclaration) item;
    }

    public void apply(Row row, ReportItemMethod reportItemMethod, Object methodReturnValue) {
        short lastCellNum = row.getLastCellNum();
        Cell cell = row.createCell(lastCellNum == -1 ? 0 : lastCellNum);

        format(reportItemMethod.value(), methodReturnValue, cell);
    }

    private void writeLongString(Cell cell, String result) {
        String value = result;
        if (result.length() > 10000) {
            logger.info("セル(row={}, column={})に出力する文字数が10,000文字を超えています。全ての文字は出力されません。",
                    cell.getRowIndex(), cell.getColumnIndex());
            value = result.substring(0, 10000) + "...(省略されました）";
        }
        cell.setCellValue(value);
    }
}
