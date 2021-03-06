package org.dddjava.jig.domain.model.jigdocument.specification;

import org.dddjava.jig.domain.model.jigdocument.stationery.Node;
import org.dddjava.jig.domain.model.jigdocument.stationery.Nodes;
import org.dddjava.jig.domain.model.jigmodel.lowmodel.alias.AliasFinder;
import org.dddjava.jig.domain.model.jigmodel.lowmodel.alias.TypeAlias;
import org.dddjava.jig.domain.model.jigmodel.lowmodel.declaration.type.TypeIdentifier;
import org.dddjava.jig.domain.model.jigmodel.services.ServiceAngle;
import org.dddjava.jig.domain.model.jigmodel.services.Usecase;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ユースケースと愉快な仲間たち
 */
class CompositeUsecases {
    Usecase usecase;
    List<TypeIdentifier> controllerTypes;

    public CompositeUsecases(ServiceAngle serviceAngle) {
        this.usecase = new Usecase(serviceAngle);
        this.controllerTypes = serviceAngle.userControllerMethods().list().stream()
                .map(methodDeclaration -> methodDeclaration.declaringType())
                .distinct()
                .collect(Collectors.toList());
    }

    public String dotText(AliasFinder aliasFinder) {
        String usecaseIdentifier = usecase.usecaseIdentifier();

        StringBuilder sb = new StringBuilder()
                .append(Nodes.usecase(aliasFinder, usecase).asText());

        Set<TypeIdentifier> otherTypes = new HashSet<>();

        // 戻り値へのEdge
        Optional<TypeIdentifier> primaryType = usecase.primaryType();
        primaryType.ifPresent(typeIdentifier -> {
                    sb.append(String.format("\"%s\" -> \"%s\"[style=bold];\n", typeIdentifier.fullQualifiedName(), usecaseIdentifier));
                    otherTypes.add(typeIdentifier);
                }
        );

        // 引数へのEdge
        for (TypeIdentifier requireType : usecase.requireTypes()) {
            sb.append(String.format("\"%s\" -> \"%s\"[style=dashed];\n", usecaseIdentifier, requireType.fullQualifiedName()));
            otherTypes.add(requireType);
        }

        // 内部使用クラスへのEdge
        for (TypeIdentifier usingType : usecase.internalUsingTypes()) {
            sb.append(String.format("\"%s\" -> \"%s\"[style=dotted];\n", usecaseIdentifier, usingType.fullQualifiedName()));
            otherTypes.add(usingType);
        }

        // Usecaseが使用しているクラスのNode
        for (TypeIdentifier otherType : otherTypes) {
            TypeAlias typeAlias = aliasFinder.find(otherType);
            sb.append(
                    new Node(otherType.fullQualifiedName())
                            .label(typeAlias.asTextOrDefault(otherType.asSimpleText()))
                            .tooltip(otherType.asSimpleText())
                            .asText()
            );
        }

        // controllerのNodeおよびedge
        for (TypeIdentifier controllerType : controllerTypes) {
            TypeAlias typeAlias = aliasFinder.find(controllerType);
            sb.append(
                    new Node(controllerType.fullQualifiedName())
                            .label(typeAlias.asTextOrDefault(controllerType.asSimpleText()))
                            .tooltip(controllerType.asSimpleText())
                            .screenNode()
                            .asText()
            );

            // dotted, headあり
            sb.append(String.format("\"%s\" -> \"%s\"[style=dotted];\n", controllerType.fullQualifiedName(), usecaseIdentifier));
        }

        return sb.toString();
    }
}
