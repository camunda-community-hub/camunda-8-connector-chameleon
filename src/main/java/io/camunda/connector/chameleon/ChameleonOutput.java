package io.camunda.connector.chameleon;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.camunda.connector.api.document.DocumentReference;
import io.camunda.connector.cherrytemplate.CherryOutput;
import io.camunda.connector.cherrytemplate.RunnerParameter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChameleonOutput implements CherryOutput {

    public static final String PINGTIMESTAMPMS = "pingTimeStampMS";
    public static final String PINGUDADDRESS = "pingIpAddress";
    public static final String PINGMESSAGE = "pingMessage";
    public static final String DOCUMENT_WRITE = "documentWrite";
    public static final String OPERATION_VALUE = "operationValue";

    public static final RunnerParameter parameterInternalTimeStamp = new RunnerParameter(PINGTIMESTAMPMS, // name
            "Ping Time stamp", // label
            String.class, // class
            RunnerParameter.Level.OPTIONAL, "if a Ping is required, then produce this information");
    public static final RunnerParameter parameterInternalIpAddress = new RunnerParameter(PINGUDADDRESS,
            // name
            "Ping IP Address", // label
            String.class, // class
            RunnerParameter.Level.OPTIONAL, "if a Ping is required, then produce this information");
    public static final RunnerParameter parameterPingMessage = new RunnerParameter(PINGMESSAGE,
            // name
            "Ping Message", // label
            String.class, // class
            RunnerParameter.Level.OPTIONAL, "if a Ping is required, then save the ping message");
    public static final RunnerParameter parameterDocumentWrite = new RunnerParameter(DOCUMENT_WRITE,
            "Document Write", // label
            DocumentReference.class, // class
            RunnerParameter.Level.OPTIONAL, "Reference to the document created by the connector");
    public static final RunnerParameter parameterOperationValue = new RunnerParameter(OPERATION_VALUE,
            "Operation Value", // label
            Map.class, // class
            RunnerParameter.Level.OPTIONAL, "if an Operation is required, result of all operations as a map");

    public long pingTimeStampMS;
    public String pingIpAddress;
    public String pingMessage;
    public DocumentReference documentWrite;
    public Map<String, Object> operationValue;


    public List<RunnerParameter> outputParametersList = List.of(
            parameterInternalTimeStamp,
            parameterInternalIpAddress,
            parameterPingMessage,
            parameterDocumentWrite,
            parameterOperationValue
    );


    @JsonIgnore
    @Override
    public List<Map<String, Object>> getOutputParameters() {
        return outputParametersList.stream().map(t -> t.toMap(null)).toList();
    }

}
