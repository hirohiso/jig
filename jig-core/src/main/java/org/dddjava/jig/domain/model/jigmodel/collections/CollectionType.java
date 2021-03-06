package org.dddjava.jig.domain.model.jigmodel.collections;

import org.dddjava.jig.domain.model.jigmodel.businessrules.BusinessRule;
import org.dddjava.jig.domain.model.jigmodel.lowmodel.declaration.method.MethodDeclarations;
import org.dddjava.jig.domain.model.jigmodel.lowmodel.declaration.type.TypeIdentifier;

/**
 * コレクション
 */
public class CollectionType {

    BusinessRule businessRule;
    CollectionField collectionField;

    public CollectionType(BusinessRule businessRule, CollectionField collectionField) {
        this.businessRule = businessRule;
        this.collectionField = collectionField;
    }

    public TypeIdentifier typeIdentifier() {
        return businessRule.type().identifier();
    }

    public MethodDeclarations methods() {
        MethodDeclarations methodDeclarations = businessRule.methodDeclarations();
        // TODO businessRule側でメソッドをまとめてしまっているのを分けておけばinstanceメソッドだけ取得すればよくなる。
        return methodDeclarations.list().stream()
                .filter(methodDeclaration -> !methodDeclaration.isConstructor())
                .collect(MethodDeclarations.collector());
    }

    public CollectionField field() {
        return collectionField;
    }
}
