package io.camunda.connector.chameleon;

import io.camunda.connector.api.document.Document;
import io.camunda.connector.api.document.DocumentCreationRequest;
import io.camunda.connector.api.document.DocumentReference;
import io.camunda.connector.api.error.ConnectorException;
import io.camunda.connector.api.outbound.OutboundConnectorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ExecuteDocument {
    private final Logger logger = LoggerFactory.getLogger(ExecuteDocument.class.getName());

    public void execute(ChameleonInput chameleonInput, ChameleonOutput chameleonOutput, OutboundConnectorContext context) throws ConnectorException {
        if (! Boolean.TRUE.equals(chameleonInput.getDocumentSimulation())) {
            return;
        }
        String readDocumentName = chameleonInput.getReadDocumentName();
        String writeDocumentName = chameleonInput.getWriteDocumentName();
        String writeDocumentContent = chameleonInput.getWriteDocumentContent();
        try {
            if (readDocumentName != null && ! readDocumentName.isEmpty()) {
                DocumentReference docRef = (DocumentReference) chameleonInput.getVariables().get(readDocumentName);
                Document document = context.resolve(docRef);
                logger.info("Read document [{}] size[{}]", readDocumentName, document.metadata().getSize());
            }
        } catch (Exception e) {
            logger.error("Cannot read document [{}]", readDocumentName);
            throw new ConnectorException(ChameleonError.READ_DOCUMENT);
        }


        try {
            if (writeDocumentName != null && ! writeDocumentName.isEmpty()) {
                OutputStream content = simulateTextDocument(writeDocumentContent);
                DocumentCreationRequest request = DocumentCreationRequest
                        .from(((ByteArrayOutputStream) content).toByteArray())
                        .fileName(writeDocumentName)
                        .build();
                Document createdDocument = context.create(request);
                chameleonOutput.documentWrite = createdDocument.reference();
            }
        } catch (Exception e) {
            logger.error("Cannot write document [{}] content[{}]", writeDocumentName, writeDocumentContent);
            throw new ConnectorException(ChameleonError.WRITE_DOCUMENT);
        }

    }

    private OutputStream simulateTextDocument(String textToPrint) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(textToPrint.getBytes(StandardCharsets.UTF_8));
        return outputStream;
    }
}
