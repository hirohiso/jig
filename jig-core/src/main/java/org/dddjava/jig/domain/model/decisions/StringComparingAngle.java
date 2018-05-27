package org.dddjava.jig.domain.model.decisions;

import org.dddjava.jig.domain.model.declaration.method.MethodDeclaration;
import org.dddjava.jig.domain.model.declaration.method.MethodDeclarations;
import org.dddjava.jig.domain.model.declaration.method.MethodSignature;
import org.dddjava.jig.domain.model.identifier.type.TypeIdentifier;
import org.dddjava.jig.domain.model.implementation.relation.MethodRelations;

import java.util.Collections;

/**
 * 文字列比較の切り口
 *
 * 文字列比較を行なっているメソッドはビジネスルールの分類判定を行なっている可能性が高い。
 * サービスなどに登場した場合はかなり拙いし、そうでなくても列挙を使用するなど改善の余地がある。
 */
public class StringComparingAngle {

    private final MethodDeclarations stringComparingMethods;

    public StringComparingAngle(MethodDeclarations stringComparingMethods) {
        this.stringComparingMethods = stringComparingMethods;
    }

    public static StringComparingAngle of(MethodRelations methodRelations) {
        // String#equals(Object)
        MethodDeclaration equalsMethod = new MethodDeclaration(
                new TypeIdentifier(String.class),
                new MethodSignature(
                        "equals",
                        Collections.singletonList(new TypeIdentifier(Object.class))),
                new TypeIdentifier("boolean"));

        MethodDeclarations userMethods = methodRelations.stream()
                .filterTo(equalsMethod)
                .fromMethods();
        return new StringComparingAngle(userMethods);
    }

    public MethodDeclarations stringComparingMethods() {
        return stringComparingMethods;
    }
}