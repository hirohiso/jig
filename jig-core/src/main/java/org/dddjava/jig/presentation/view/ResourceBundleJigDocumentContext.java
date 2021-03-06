package org.dddjava.jig.presentation.view;

import org.dddjava.jig.domain.model.jigdocument.documentformat.DocumentName;
import org.dddjava.jig.domain.model.jigdocument.documentformat.JigDocument;
import org.dddjava.jig.domain.model.jigdocument.stationery.JigDocumentContext;
import org.dddjava.jig.infrastructure.resourcebundle.Utf8ResourceBundle;
import org.dddjava.jig.presentation.view.report.ReportItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;

public class ResourceBundleJigDocumentContext implements JigDocumentContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceBundleJigDocumentContext.class);

    Properties jigProperties;
    ResourceBundle jigDocumentResource;

    ResourceBundleJigDocumentContext() {
        init();
    }

    private void init() {
        try {
            jigProperties = new Properties();
            try (InputStream is = ResourceBundleJigDocumentContext.class.getClassLoader().getResourceAsStream("jig.properties")) {
                jigProperties.load(is);
            }
            jigDocumentResource = Utf8ResourceBundle.documentBundle();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String version() {
        return jigProperties.getProperty("version");
    }

    public String jigDocumentLabel(JigDocument jigDocument) {
        return label(jigDocument.name());
    }

    public String diagramLabel(JigDocument jigDocument) {
        return jigDocumentLabel(jigDocument);
    }

    public static ResourceBundleJigDocumentContext getInstance() {
        return new ResourceBundleJigDocumentContext();
    }

    @Override
    public String label(String key) {
        if (jigDocumentResource.containsKey(key)) {
            return jigDocumentResource.getString(key);
        }
        // 取得できない場合はkeyをそのまま返す
        LOGGER.warn("Can't find resource for '{}'", key);
        return key;
    }

    public String reportLabel(ReportItem reportItem) {
        return label(reportItem.key);
    }

    @Override
    public DocumentName documentName(JigDocument jigDocument) {
        return DocumentName.of(jigDocument, diagramLabel(jigDocument));
    }
}
