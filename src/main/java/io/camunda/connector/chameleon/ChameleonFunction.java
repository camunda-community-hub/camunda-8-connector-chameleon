package io.camunda.connector.chameleon;

/* ******************************************************************** */
/*                                                                      */
/*  ChameleonFunction                                                         */
/*                                                                      */
/*  This connector is the main connector, doing the distribution on     */
/*  specific function                                                   */
/* ******************************************************************** */

import io.camunda.connector.api.annotation.OutboundConnector;
import io.camunda.connector.api.error.ConnectorException;
import io.camunda.connector.api.outbound.OutboundConnectorContext;
import io.camunda.connector.api.outbound.OutboundConnectorFunction;
import io.camunda.connector.cherrytemplate.CherryConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Do not register any InputVariables: we want to get all
 */
@OutboundConnector(name = ChameleonInput.CHAMELONFUNCTION, type = "c-chameleon-function")

public class ChameleonFunction implements OutboundConnectorFunction, CherryConnector {

    private static final String WORKER_LOGO = "data:image/png;base64,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";


    private final Logger logger = LoggerFactory.getLogger(ChameleonFunction.class.getName());

    @Override
    public ChameleonOutput execute(OutboundConnectorContext outboundConnectorContext) throws Exception {
        ChameleonInput chameleonInput;
        ChameleonOutput chameleonOutput = new ChameleonOutput();
        try {
            chameleonInput = outboundConnectorContext.bindVariables(ChameleonInput.class);
            Map<String, Object> variables = outboundConnectorContext.bindVariables(Map.class);
            chameleonInput.registerAllVariables(variables);

            if (chameleonInput.getSleep() != null) {
                Thread.sleep(chameleonInput.getSleep());
            }

            ExecutePing executePing = new ExecutePing();
            executePing.execute(chameleonInput, chameleonOutput);

            ExecuteOperations executeOperations = new ExecuteOperations();
            executeOperations.execute(chameleonInput, chameleonOutput);

            ExecuteThrowError executeThrowError = new ExecuteThrowError();
            executeThrowError.execute(chameleonInput, chameleonOutput);

            ExecuteDocument executeDocument = new ExecuteDocument();
            executeDocument.execute(chameleonInput, chameleonOutput, outboundConnectorContext);
        } catch (ConnectorException ce) {
            throw ce;
        } catch (Exception e) {
            throw new ConnectorException(ChameleonError.BAD_INPUTPARAMETER, "Chameleon can't bind variable " + e.getMessage() + "]");
        }

        return chameleonOutput;
    }

    @Override
    public String getDescription() {
        return "Simulate any connector";
    }

    @Override
    public String getLogo() {
        return WORKER_LOGO;
    }

    @Override
    public String getCollectionName() {
        return "";
    }

    @Override
    public Map<String, String> getListBpmnErrors() {
        Map<String, String> allErrors = Map.of(ChameleonError.BAD_INPUTPARAMETER, ChameleonError.BAD_INPUTPARAMETER_EXPLANATION,
                ChameleonError.OPERATION_EXECUTION, ChameleonError.OPERATION_EXECUTION_EXPLANATION,
                ChameleonError.DATEPARSE_OPERATION_ERROR, ChameleonError.DATEPARSE_OPERATION_ERROR_EXPLANATION,
                ChameleonError.UNKNOWN_FUNCTION, ChameleonError.UNKNOWN_FUNCTION_EXPLANATION,
                ChameleonError.WRITE_DOCUMENT, ChameleonError.WRITE_DOCUMENT_EXPLANATION,
                ChameleonError.READ_DOCUMENT, ChameleonError.READ_DOCUMENT_EXPLANATION,
                ChameleonError.INCOMPLETE_PARAMETERS, ChameleonError.INCOMPLETE_PARAMETERS_EXPLANATION);


        return allErrors;
    }

    @Override
    public Class<?> getInputParameterClass() {
        return ChameleonInput.class;
    }

    @Override
    public Class<?> getOutputParameterClass() {
        return ChameleonOutput.class;
    }

    /**
     * Only task at this moment (no InboundConnector)
     *
     * @return list of items where the function applies
     */
    @Override
    public List<String> getAppliesTo() {
        return List.of("bpmn:ServiceTask");
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public String getRelease() {
        try (InputStream is = ChameleonFunction.class.getResourceAsStream(
                "/META-INF/maven/io.camunda.connector/pdf-function/pom.properties")) {
            if (is != null) {
                java.util.Properties props = new java.util.Properties();
                props.load(is);
                String version = props.getProperty("version");
                if (version != null)
                    return version;
            }
        } catch (Exception e) {
            // fall through
        }
        return "";
    }


}
