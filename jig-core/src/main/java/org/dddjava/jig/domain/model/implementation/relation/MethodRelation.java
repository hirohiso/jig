package org.dddjava.jig.domain.model.implementation.relation;

import org.dddjava.jig.domain.model.declaration.method.MethodDeclaration;

/**
 * メソッドの使用しているメソッド
 */
public class MethodRelation {
    MethodDeclaration from;
    MethodDeclaration to;

    public MethodRelation(MethodDeclaration from, MethodDeclaration to) {
        this.from = from;
        this.to = to;
    }

    MethodDeclaration from() {
        return from;
    }

    MethodDeclaration to() {
        return to;
    }

    public boolean toIs(MethodDeclaration methodDeclaration) {
        return to.equals(methodDeclaration);
    }

    public boolean fromIs(MethodDeclaration methodDeclaration) {
        return from.equals(methodDeclaration);
    }
}
