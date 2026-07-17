package io.camunda.connector.chameleon;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.camunda.connector.cherrytemplate.CherryInput;
import io.camunda.connector.cherrytemplate.RunnerParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * the JsonIgnoreProperties is mandatory: the template may contain additional widget to help the designer, especially on the OPTIONAL parameters
 * This avoids the MAPPING Exception
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChameleonInput implements CherryInput {
    /**
     * Attention, each Input here must be added in the ChameleonFunction, list of InputVariables
     */
    public static final String CHAMELONFUNCTION = "chameleonFunction";



    private final Logger logger = LoggerFactory.getLogger(ChameleonInput.class.getName());


    /**
     * Ping
     */
    private Boolean pingSimulation;
    protected final static String PING_SIMULATION = "pingSimulation";


    private Long sleep = 0L;
    public static final String SLEEP = "sleep";

    private String pingMessage;
    protected final static String PINGMESSAGE = "pingMessage";


    /**
     * Operation
     */
    private Boolean operationSimulation;
    protected final static String OPERATION_SIMULATION = "operationSimulation";

    /**
     * Can be a Map or a List<Map>
     */
    private Object operations;
    protected final static String OPERATIONS = "operations";


    /**
     * Throw errors
     */
    private Boolean errorSimulation;
    protected final static String ERROR_SIMULATION = "errorSimulation";

    /**
     * NPMN Throw errors simulations
     * [ { "code": "NULLPOINTER", "%": 23, "message":"this is the message}, { "code": "BADAMOUNT", "%": 10}]
     */
    private List<Map<String, Object>> throwErrors;
    protected final static String THROWERRORS = "throwErrors";

    /**
     * Throw errors
     */
    private Boolean documentSimulation;
    protected final static String DOCUMENT_SIMULATION = "documentSimulation";

    /**
     * Check documents and product documents
     */
    private String readDocumentName;
    protected final static String READ_DOCUMENT_NAME = "readDocumentName";

    private String writeDocumentName;
    protected final static String WRITE_DOCUMENT_NAME = "writeDocumentName";
    private String writeDocumentContent;
    protected final static String WRITE_DOCUMENT_CONTENT = "writeDocumentContent";

    public Boolean getPingSimulation() {
        return pingSimulation;
    }

    public Boolean getOperationSimulation() {
        return operationSimulation;
    }

    public Boolean getErrorSimulation() {
        return errorSimulation;
    }

    public Boolean getDocumentSimulation() {
        return documentSimulation;
    }

    public Object getOperations() {
        return operations;
    }

    public String getWriteDocumentName() {
        return writeDocumentName;
    }

    public String getWriteDocumentContent() {
        return writeDocumentContent;
    }

    public String getReadDocumentName() {
        return readDocumentName;
    }

    public List<Map<String, Object>> getThrowErrors() {
        return throwErrors;
    }

    public String getPingMessage() {
        return pingMessage;
    }

    public Long getSleep() {
        return sleep;
    }


    /* ******************************************************************** */
    /*                                                                      */
    /*  registerAllVariables                                                */
    /*                                                                      */
    /* ******************************************************************** */

    Map<String, Object> allVariables;

    @JsonIgnore
    public void registerAllVariables(Map<String, Object> variables) {
        allVariables = variables;
    }

    public Map<String, Object> getVariables() {
        return allVariables;
    }

    /* ******************************************************************** */
    /*                                                                      */
    /*  Cherry generation                                                   */
    /*                                                                      */
    /* ******************************************************************** */

    public static final RunnerParameter parameterSleep = new RunnerParameter(
            ChameleonInput.SLEEP,
            // name
            "Sleep time (ms)", // label
            Long.class, // class
            RunnerParameter.Level.REQUIRED, // level
            "Sleep time to simulate a real connector")
            .setDefaultValue(0);
    public static final RunnerParameter parameterPingSimulation = new RunnerParameter(
            ChameleonInput.PING_SIMULATION,
            // name
            "Ping simulation?", // label
            Boolean.class, // class
            RunnerParameter.Level.REQUIRED, // level
            "Ping simulation?")
            .setGroup("Ping");
    public static final RunnerParameter parameterMessage = new RunnerParameter(
            ChameleonInput.PINGMESSAGE,
            // name
            "Ping Message", // label
            String.class, // class
            RunnerParameter.Level.REQUIRED, // level
            "A message is get to log it, and return")
            .addCondition(ChameleonInput.PING_SIMULATION, List.of("true") )
            .setGroup("Ping");


    public static final RunnerParameter parameterErrorsOperation = new RunnerParameter(
            ChameleonInput.ERROR_SIMULATION,
            // name
            "Error simulation?", // label
            Boolean.class, // class
            RunnerParameter.Level.REQUIRED, // level
            "Error simulation?")
            .setGroup("errors");

    public static final RunnerParameter parameterThrowErrors = new RunnerParameter(
            ChameleonInput.THROWERRORS,
            // name
            "Throws error rule", // label
            List.class, // class
            RunnerParameter.Level.REQUIRED, // level
            "Give a list of error code/percentage of execution. Example [ { \"code\": \"NULLPOINTER\", \"%\": 23, \"message\":\"this is the message\"}, { \"code\": \"BADAMOUNT\", \"%\": 10}]")
            .addCondition(ChameleonInput.ERROR_SIMULATION, List.of("true") )
            .setGroup("errors");


    public static final RunnerParameter parameterOperationSimulation = new RunnerParameter(
            ChameleonInput.OPERATION_SIMULATION,
            // name
            "Operation simulation?", // label
            Boolean.class, // class
            RunnerParameter.Level.REQUIRED, // level
            "Operation simulation?")
            .setGroup("operations");

    public static final RunnerParameter parameterOperations = new RunnerParameter(
            ChameleonInput.OPERATIONS,
            // name
            "Operations", // label
            Map.class, // class
            RunnerParameter.Level.REQUIRED, // level
            "map of operation to execute. Two structure are accepted: {<data>} or [{\"%\":35,\"data\": {<data>}}, {\"%\":12,\"data\": {<data>}..]. "
                    + " <data> is a set of \"<variableName\"> : \"operation\". Operation is a value (string, or process variable reference) or <function>(<params>)."
                    + "Functions are " + ExecuteOperations.CST_FUNCTION_DATE
                    + "," + ExecuteOperations.CST_FUNCTION_DATETIME
                    + "," + ExecuteOperations.CST_FUNCTION_LOCALDATE
                    + "," + ExecuteOperations.CST_FUNCTION_LOCAL_TIME
                    + "," + ExecuteOperations.CST_FUNCTION_ZONED_DATE_TIME)
            .addCondition(ChameleonInput.OPERATION_SIMULATION, List.of("true") )
            .setGroup("operations");


    public static final RunnerParameter parameterDocumentSimulation = new RunnerParameter(
            ChameleonInput.DOCUMENT_SIMULATION,
            // name
            "Document simulation?", // label
            Boolean.class, // class
            RunnerParameter.Level.REQUIRED, // level
            "Document simulation?")
            .setGroup("documents");

    public static final RunnerParameter parameterReadDocumentName = new RunnerParameter(
            ChameleonInput.READ_DOCUMENT_NAME,
            // name
            "Read document name", // label
            String.class, // class
            RunnerParameter.Level.REQUIRED, // level
            "Read a document")
            .addCondition(ChameleonInput.DOCUMENT_SIMULATION, List.of("true") )
            .setGroup("documents");
    public static final RunnerParameter parameterWriteDocumentName = new RunnerParameter(
            ChameleonInput.WRITE_DOCUMENT_NAME,
            // name
            "Write document name", // label
            String.class, // class
            RunnerParameter.Level.REQUIRED, // level
            "Write a document with this name")
            .addCondition(ChameleonInput.DOCUMENT_SIMULATION, List.of("true") )
            .setGroup("documents");
    public static final RunnerParameter parameterWriteDocumentContent = new RunnerParameter(
            ChameleonInput.WRITE_DOCUMENT_CONTENT,
            // name
            "Write document content", // label
            String.class, // class
            RunnerParameter.Level.REQUIRED, // level
            "Content of the document to write")
            .addCondition(ChameleonInput.DOCUMENT_SIMULATION, List.of("true") )
            .setGroup("documents");



    public List<RunnerParameter> inputParametersList = List.of(
            parameterSleep,

            parameterPingSimulation,
            parameterMessage,

            parameterErrorsOperation,
            parameterThrowErrors,

            parameterOperationSimulation,
            parameterOperations,

            parameterDocumentSimulation,
            parameterReadDocumentName,
            parameterWriteDocumentName,
            parameterWriteDocumentContent);
    @JsonIgnore
    @Override
    public List<Map<String, Object>> getInputParameters() {
        return inputParametersList.stream().map(t -> t.toMap(null)).toList();
    }


}
