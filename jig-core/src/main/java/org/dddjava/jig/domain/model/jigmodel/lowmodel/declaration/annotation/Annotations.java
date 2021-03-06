package org.dddjava.jig.domain.model.jigmodel.lowmodel.declaration.annotation;

import org.dddjava.jig.domain.model.jigmodel.lowmodel.declaration.type.TypeIdentifier;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * アノテーション一覧
 */
public class Annotations {
    List<Annotation> list;

    public Annotations(List<Annotation> list) {
        this.list = list;
    }

    public Annotations filterAny(TypeIdentifier... typeIdentifiers) {
        Collection<TypeIdentifier> annotations = Arrays.asList(typeIdentifiers);

        List<Annotation> list = this.list.stream()
                .filter(e -> annotations.contains(e.typeIdentifier))
                .collect(toList());
        return new Annotations(list);
    }

    public List<String> descriptionTextsOf(String name) {
        return list.stream()
                .map(e -> e.descriptionTextOf(name))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Annotation get(TypeIdentifier typeIdentifier) {
        return list.stream().filter(e -> e.typeIdentifier.equals(typeIdentifier))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(typeIdentifier.fullQualifiedName()));
    }

    public List<Annotation> list() {
        return list;
    }
}
