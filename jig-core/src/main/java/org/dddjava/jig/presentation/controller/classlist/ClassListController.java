package org.dddjava.jig.presentation.controller.classlist;

import org.dddjava.jig.application.service.AngleService;
import org.dddjava.jig.application.service.GlossaryService;
import org.dddjava.jig.domain.basic.report.Report;
import org.dddjava.jig.domain.basic.report.Reports;
import org.dddjava.jig.domain.model.boolquerymethod.BoolQueryModelMethodAngle;
import org.dddjava.jig.domain.model.boolquerymethod.BoolQueryModelMethodAngles;
import org.dddjava.jig.domain.model.boolquerymethod.BoolQueryModelMethodReport;
import org.dddjava.jig.domain.model.categories.EnumAngles;
import org.dddjava.jig.domain.model.categories.EnumReport;
import org.dddjava.jig.domain.model.datasources.DatasourceAngles;
import org.dddjava.jig.domain.model.datasources.DatasourceReport;
import org.dddjava.jig.domain.model.decisions.DecisionAngles;
import org.dddjava.jig.domain.model.decisions.DecisionReport;
import org.dddjava.jig.domain.model.decisions.StringComparingAngle;
import org.dddjava.jig.domain.model.decisions.StringComparingReport;
import org.dddjava.jig.domain.model.declaration.annotation.ValidationAnnotatedMember;
import org.dddjava.jig.domain.model.declaration.annotation.ValidationAnnotatedMembers;
import org.dddjava.jig.domain.model.declaration.method.MethodDeclaration;
import org.dddjava.jig.domain.model.identifier.type.TypeIdentifier;
import org.dddjava.jig.domain.model.identifier.type.TypeIdentifierFormatter;
import org.dddjava.jig.domain.model.implementation.ProjectData;
import org.dddjava.jig.domain.model.japanese.JapaneseName;
import org.dddjava.jig.domain.model.services.ServiceAngles;
import org.dddjava.jig.domain.model.services.ServiceReport;
import org.dddjava.jig.domain.model.validations.ValidationReport;
import org.dddjava.jig.domain.model.values.ValueAngles;
import org.dddjava.jig.domain.model.values.ValueKind;
import org.dddjava.jig.domain.model.values.ValueReport;
import org.dddjava.jig.presentation.view.JigModelAndView;
import org.dddjava.jig.presentation.view.ViewResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
public class ClassListController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassListController.class);

    ViewResolver viewResolver;

    TypeIdentifierFormatter typeIdentifierFormatter;
    GlossaryService glossaryService;
    AngleService angleService;

    public ClassListController(ViewResolver viewResolver,
                               TypeIdentifierFormatter typeIdentifierFormatter,
                               GlossaryService glossaryService,
                               AngleService angleService) {
        this.viewResolver = viewResolver;
        this.typeIdentifierFormatter = typeIdentifierFormatter;
        this.glossaryService = glossaryService;
        this.angleService = angleService;
    }

    public JigModelAndView<Reports> applicationList(ProjectData projectData) {
        LOGGER.info("入出力リストを出力します");
        Reports reports = new Reports(Arrays.asList(
                serviceReport(projectData),
                datasourceReport(projectData)
        ));

        return new JigModelAndView<>(reports, viewResolver.applicationList());
    }

    public JigModelAndView<Reports> domainList(ProjectData projectData) {
        LOGGER.info("ビジネスルールリストを出力します");
        Reports reports = new Reports(Arrays.asList(
                valueObjectReport(ValueKind.IDENTIFIER, projectData),
                enumReport(projectData),
                valueObjectReport(ValueKind.NUMBER, projectData),
                valueObjectReport(ValueKind.COLLECTION, projectData),
                valueObjectReport(ValueKind.DATE, projectData),
                valueObjectReport(ValueKind.TERM, projectData),
                validateAnnotationReport(projectData),
                stringComparingReport(projectData),
                decisionReport(projectData),
                booleanReport(projectData)
        ));

        return new JigModelAndView<>(reports, viewResolver.domainList());
    }


    Report<?> serviceReport(ProjectData projectData) {
        ServiceAngles serviceAngles = angleService.serviceAngles(projectData);
        List<ServiceReport.Row> list = serviceAngles.list().stream().map(angle -> {
            Function<TypeIdentifier, JapaneseName> japaneseNameResolver = glossaryService::japaneseNameFrom;
            Function<MethodDeclaration, JapaneseName> methodJapaneseNameResolver = glossaryService::japaneseNameFrom;
            return new ServiceReport.Row(angle, japaneseNameResolver, methodJapaneseNameResolver, typeIdentifierFormatter);
        }).collect(Collectors.toList());
        return new ServiceReport(list).toReport();
    }

    Report<?> datasourceReport(ProjectData projectData) {
        DatasourceAngles datasourceAngles = angleService.datasourceAngles(projectData);
        List<DatasourceReport.Row> list = datasourceAngles.list().stream().map(angle -> {
            JapaneseName japaneseName = glossaryService.japaneseNameFrom(angle.method().declaringType());
            return new DatasourceReport.Row(angle, japaneseName, typeIdentifierFormatter);
        }).collect(Collectors.toList());
        return new DatasourceReport(list).toReport();
    }

    Report<?> stringComparingReport(ProjectData projectData) {
        StringComparingAngle stringComparingAngle = angleService.stringComparing(projectData);
        return new StringComparingReport(stringComparingAngle).toReport();
    }

    Report<?> valueObjectReport(ValueKind valueKind, ProjectData projectData) {
        ValueAngles valueAngles = angleService.valueAngles(valueKind, projectData);
        List<ValueReport.Row> list = valueAngles.list().stream().map(enumAngle -> {
            JapaneseName japaneseName = glossaryService.japaneseNameFrom(enumAngle.typeIdentifier());
            return new ValueReport.Row(enumAngle, japaneseName, typeIdentifierFormatter);
        }).collect(Collectors.toList());
        return new ValueReport(valueKind, list).toReport();
    }

    Report<?> enumReport(ProjectData projectData) {
        EnumAngles enumAngles = angleService.enumAngles(projectData);
        List<EnumReport.Row> list = enumAngles.list().stream().map(enumAngle -> {
            JapaneseName japaneseName = glossaryService.japaneseNameFrom(enumAngle.typeIdentifier());
            return new EnumReport.Row(enumAngle, japaneseName, typeIdentifierFormatter);
        }).collect(Collectors.toList());
        return new EnumReport(list).toReport();
    }

    Report<?> validateAnnotationReport(ProjectData projectData) {
        List<ValidationReport.Row> list = new ArrayList<>();
        ValidationAnnotatedMembers validationAnnotatedMembers = new ValidationAnnotatedMembers(projectData.annotatedFields(), projectData.annotatedMethods());
        for (ValidationAnnotatedMember annotationDeclaration : validationAnnotatedMembers.list()) {
            JapaneseName japaneseName = glossaryService.japaneseNameFrom(annotationDeclaration.declaringType());
            list.add(new ValidationReport.Row(annotationDeclaration, japaneseName, typeIdentifierFormatter));
        }
        return new ValidationReport(list).toReport();
    }

    Report<?> decisionReport(ProjectData projectData) {
        DecisionAngles decisionAngles = angleService.decision(projectData);
        return new DecisionReport(decisionAngles).toReport();
    }

    Report<?> booleanReport(ProjectData projectData) {
        BoolQueryModelMethodAngles angles = angleService.boolQueryModelMethodAngle(projectData);

        List<BoolQueryModelMethodReport.Row> list = new ArrayList<>();
        for (BoolQueryModelMethodAngle angle : angles.list()) {
            JapaneseName japaneseClassName = glossaryService.japaneseNameFrom(angle.declaringTypeIdentifier());
            JapaneseName japaneseMethodName = glossaryService.japaneseNameFrom(angle.method());
            list.add(new BoolQueryModelMethodReport.Row(angle, japaneseMethodName, japaneseClassName, typeIdentifierFormatter));
        }
        return new BoolQueryModelMethodReport(list).toReport();
    }
}
