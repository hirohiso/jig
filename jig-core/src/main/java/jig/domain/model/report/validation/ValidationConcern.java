package jig.domain.model.report.validation;

import java.util.function.Function;

public enum ValidationConcern {
    クラス名(detail -> detail.declaringType().fullQualifiedName()),
    クラス和名(detail -> detail.japaneseName().value()),
    フィールドorメソッド(AnnotationDetail::annotateSimpleName),
    アノテーション名(detail -> detail.annotationType().asSimpleText()),
    記述(detail -> detail.description().asText());

    private final Function<AnnotationDetail, String> function;

    ValidationConcern(Function<AnnotationDetail, String> function) {
        this.function = function;
    }

    public String apply(AnnotationDetail typeDetail) {
        return function.apply(typeDetail);
    }
}