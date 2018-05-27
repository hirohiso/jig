package org.dddjava.jig.domain.model.decisions;

import org.dddjava.jig.domain.basic.report.ConvertibleItem;
import org.dddjava.jig.domain.basic.report.Report;

public class DecisionReport {

    enum Items implements ConvertibleItem<DecisionAngle> {
        レイヤー {
            @Override
            public String convert(DecisionAngle row) {
                return row.typeLayer().asText();
            }
        },
        クラス名 {
            @Override
            public String convert(DecisionAngle row) {
                return row.methodDeclaration().declaringType().fullQualifiedName();
            }
        },
        メソッド名 {
            @Override
            public String convert(DecisionAngle row) {
                return row.methodDeclaration().asSignatureSimpleText();
            }
        };
    }

    private final DecisionAngles decisionAngles;

    public DecisionReport(DecisionAngles decisionAngles) {
        this.decisionAngles = decisionAngles;
    }

    public Report<?> toReport() {
        return new Report<>("条件分岐箇所", decisionAngles.listOnlyLayer(), Items.values());
    }
}