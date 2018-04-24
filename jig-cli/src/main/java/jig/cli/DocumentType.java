package jig.cli;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum DocumentType {
    ServiceMethodCallHierarchy,
    PackageDependency;

    public static List<DocumentType> resolve(String diagramTypes) {
        return Arrays.stream(diagramTypes.split(","))
                .map(DocumentType::valueOf)
                .collect(Collectors.toList());
    }
}