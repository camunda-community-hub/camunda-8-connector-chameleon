package io.camunda.connector.chameleon;

public class ChameleonError {


    public static final String BAD_INPUTPARAMETER = "BAD_INPUTPARAMETER";
    public static final String BAD_INPUTPARAMETER_EXPLANATION = "During the bind, some input does not have the expected type";


    public static final String OPERATION_EXECUTION = "SYNTAXE_OPERATION_ERROR";
    public static final String OPERATION_EXECUTION_EXPLANATION = "Operations does not repect the expected format";


    public static final String DATEPARSE_OPERATION_ERROR = "DATEPARSE_OPERATION_ERROR";
    public static final String DATEPARSE_OPERATION_ERROR_EXPLANATION = "Impossible to parse the date. Format depend on the function used";

    public static final String UNKNOWN_FUNCTION = "UNKNOWN_FUNCTION";
    public static final String UNKNOWN_FUNCTION_EXPLANATION = "The function is unknown";

    public static final String WRITE_DOCUMENT = "WRITE_DOCUMENT";
    public static final String WRITE_DOCUMENT_EXPLANATION = "Error during the write document";

    public static final String READ_DOCUMENT = "READ_DOCUMENT";
    public static final String READ_DOCUMENT_EXPLANATION = "Impossible to read the document";

    public static final String INCOMPLETE_PARAMETERS = "INCOMPLETE_PARAMETERS";
    public static final String INCOMPLETE_PARAMETERS_EXPLANATION = "Parameter is incomplete";

}
