package org.dddjava.jig.domain.model.jigdocument.specification;

import org.dddjava.jig.domain.model.jigdocument.documentformat.DocumentName;
import org.dddjava.jig.domain.model.jigdocument.documentformat.JigDocument;
import org.dddjava.jig.domain.model.jigdocument.stationery.*;

import java.util.StringJoiner;

/**
 * アーキテクチャ図
 */
public class ArchitectureDiagram {

    private final RoundingPackageRelations architectureRelation;

    public ArchitectureDiagram(RoundingPackageRelations architectureRelation) {
        this.architectureRelation = architectureRelation;
    }

    public DiagramSources dotText(JigDocumentContext jigDocumentContext) {
        if (architectureRelation.worthless()) {
            return DiagramSource.empty();
        }

        DocumentName documentName = jigDocumentContext.documentName(JigDocument.ArchitectureDiagram);

        StringJoiner graph = new StringJoiner("\n", "digraph \"" + documentName.label() + "\" {", "}")
                .add("subgraph clusterArchitecture {")
                .add(Node.DEFAULT)
                .add(new Node("domain").asText())
                .add(new Node("presentation").asText())
                .add(new Node("application").asText())
                .add(new Node("infrastructure").asText())
                .add("}")
                .add("label=\"" + documentName.label() + "\";")
                .add("node [shape=box,style=filled,fillcolor=whitesmoke];");
        RelationText relationText = RelationText.fromPackageRelations(architectureRelation.packageRelations());
        graph.add(relationText.asText());
        return DiagramSource.createDiagramSource(documentName, graph.toString());
    }
}
