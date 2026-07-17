package io.camunda.connector.chameleon;

import com.google.gson.Gson;
import io.camunda.connector.api.error.ConnectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;

public class ExecuteOperations {
    private final Logger logger = LoggerFactory.getLogger(ExecuteOperations.class.getName());

    private static final String CST_NOW = "now";
    private static final String CST_ISODATE = "yyyy-MM-dd";
    private static final String CST_ISODATETIME = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String CST_FUNCTION_DATE = "date";
    public static final String CST_FUNCTION_DATETIME = "datetime";
    public static final String CST_FUNCTION_LOCALDATE = "localdate";
    public static final String CST_FUNCTION_LOCAL_TIME = "Localtime";
    public static final String CST_FUNCTION_ZONED_DATE_TIME = "zoneddatetime";
    private static final String CST_FUNCTION_JSON = "JSON";

    public void execute(ChameleonInput chameleonInput, ChameleonOutput chameleonOutput) throws Exception {
        if (!Boolean.TRUE.equals(chameleonInput.getOperationSimulation())) {
            return;
        }
        Object operations = chameleonInput.getOperations();
        if (operations == null) {
            logger.error("Simulate operation is required but operation details is not provided");
            throw new ConnectorException(ChameleonError.INCOMPLETE_PARAMETERS);
        }

        if (operations instanceof Map operationsMap) {
            executeOperations(operationsMap, chameleonInput, chameleonOutput);
        }
        if (operations instanceof List operationsList) {
            int random = (int) (Math.random() * 100);
            int base = 0;
            int count = 0;
            for (Object dataSet : operationsList) {
                count++;
                if (dataSet == null || !(dataSet instanceof Map)) {
                    logger.error("ExecuteOperation: dataSet [{}] is not a map : {}", count,
                            dataSet == null ? "null" : dataSet.getClass().getName());
                    throw new ConnectorException(ChameleonError.INCOMPLETE_PARAMETERS);
                }
                Map<String, Object> dataSetMap = (Map) dataSet;

                int percent = getPercent(dataSetMap.get("%"), count);
                logger.info("Check error random [{}] Range:[{}-{}] last? {}", random, base, percent + base, count == operationsList.size());
                if (random <= base + percent || count == operationsList.size()) {
                    logger.info("ExecuteOperation: Found range ");
                    if (dataSetMap.get("data") instanceof Map operationDataMap) {
                        executeOperations(operationDataMap, chameleonInput, chameleonOutput);
                    } else {
                        logger.error("ExecuteOperation: dataSet.get(\"data\") [{}] is not a map : {}", count,
                                dataSetMap.get("data") == null ? "null" : dataSetMap.get("data").getClass().getName());
                        throw new ConnectorException(ChameleonError.INCOMPLETE_PARAMETERS);
                    }
                }
                base += percent;
            }
        }
    }


    /**
     * Execute all operations
     *
     * @param operations      set of operation to execute
     * @param chameleonInput  input (to access all process variables
     * @param chameleonOutput produce the result
     * @throws Exception in case of any errors
     */
    private void executeOperations(Map<String, Object> operations, ChameleonInput chameleonInput, ChameleonOutput chameleonOutput) throws Exception {
        try {
            logger.info("ExecuteOperations: [{}]", operations);
            Map<String, Object> operationResult = new HashMap<>();
            for (Map.Entry<String, Object> operation : operations.entrySet()) {
                String variableName = operation.getKey();
                Object content = operation.getValue();
                // check the content: a String? A integer? A variable?
                Object contentVariable;
                FunctionDescription function = null;
                if (content != null && content instanceof String contentSt) {
                    function = getFunction(contentSt);
                }
                if (function != null)
                    contentVariable = getValueFunction(function, chameleonInput);
                else
                    contentVariable = getValue(content, chameleonInput);

                // ok, now update the variable
                operationResult.put(variableName, contentVariable);
            }
            chameleonOutput.operationValue = operationResult;
            logger.info("Operation value populated[{}]", chameleonOutput.operationValue);
        } catch (ConnectorException ce) {
            throw ce;
        } catch (Exception e) {
            throw new ConnectorException(ChameleonError.OPERATION_EXECUTION, "Syntax error on operation[" + operations + "] : " + e);
        }

    }

    /**
     * get, if we can detect a function, the function, else null
     *
     * @param content content which describe a function. Example : date(now)
     * @return the function description
     */
    private FunctionDescription getFunction(String content) {
        int begParenthesis = content.indexOf("(");
        int endParenthesis = content.lastIndexOf(")");
        if (begParenthesis == -1 || endParenthesis == -1) {
            // not a function
            if (begParenthesis != -1)
                logger.error("ExecuteOperation: Incorrect function? function is <name>(<parameters>*) [" + content + "]");
            return null;
        }
        FunctionDescription function = new FunctionDescription();
        function.name = content.substring(0, begParenthesis);
        function.allParams = content.substring(0, endParenthesis).substring(begParenthesis + 1);
        StringTokenizer st = new StringTokenizer(function.allParams, ",");
        while (st.hasMoreTokens()) {
            function.parameters.add(st.nextToken());
        }
        return function;
    }

    /**
     * GetValueFunction get the value via a function
     *
     * @param function       function to use to get the value
     * @param chameleonInput job activated
     * @return the value by the function
     * @throws Exception any errors arrive during the execution
     */
    private Object getValueFunction(FunctionDescription function, final ChameleonInput chameleonInput)
            throws ConnectorException {
        if (function.isFunction(CST_FUNCTION_DATE)) {
            Object dateValue = getValue(function.getParameter(0), chameleonInput);
            if (CST_NOW.equals(dateValue) || dateValue == null)
                return new SimpleDateFormat(CST_ISODATE).format(new Date());
            else {
                try {
                    return new SimpleDateFormat(CST_ISODATE).parse(dateValue.toString());
                } catch (Exception e) {
                    throw new ConnectorException(ChameleonError.DATEPARSE_OPERATION_ERROR,
                            "Can't parse date[" + dateValue + "] pattern [" + CST_ISODATE + "]: " + e);
                }
            }
        } else if (function.isFunction(CST_FUNCTION_DATETIME)) {
            Object dateValue = getValue(function.getParameter(0), chameleonInput);
            if (CST_NOW.equals(dateValue) || dateValue == null)
                return new SimpleDateFormat(CST_ISODATETIME).format(new Date());
            else
                try {
                    return new SimpleDateFormat(CST_ISODATETIME).parse(dateValue.toString());
                } catch (Exception e) {
                    throw new ConnectorException(ChameleonError.DATEPARSE_OPERATION_ERROR,
                            "Can't parse date[" + dateValue + "] pattern [" + CST_ISODATETIME + "]: " + e);
                }
        } else if (function.isFunction(CST_FUNCTION_LOCALDATE)) {
            Object dateValue = getValue(function.getParameter(0), chameleonInput);
            if (CST_NOW.equals(dateValue) || dateValue == null)
                return LocalDate.now();
            else
                try {
                    return LocalDate.parse(dateValue.toString());
                } catch (Exception e) {
                    throw new ConnectorException(ChameleonError.DATEPARSE_OPERATION_ERROR,
                            "Can't parse date[" + dateValue + "] LocalDate pattern [yyyy-MM-dd]: " + e);
                }
        } else if (function.isFunction(CST_FUNCTION_LOCAL_TIME)) {
            Object dateValue = getValue(function.getParameter(0), chameleonInput);
            if (CST_NOW.equals(dateValue) || dateValue == null)
                return LocalDate.now();
            else
                try {
                    return LocalDateTime.parse(dateValue.toString());
                } catch (Exception e) {
                    throw new ConnectorException(ChameleonError.DATEPARSE_OPERATION_ERROR,
                            "Can't parse date[" + dateValue
                                    + "] LocalDate pattern [yyyy-MM-dd'T'HH:mm:ss'Z']: " + e);
                }
        } else if (function.isFunction(CST_FUNCTION_ZONED_DATE_TIME)) {
            Object dateValue = getValue(function.getParameter(0), chameleonInput);
            if (CST_NOW.equals(dateValue) || dateValue == null)
                return ZonedDateTime.now();
            else
                try {
                    return ZonedDateTime.parse(dateValue.toString());
                } catch (Exception e) {
                    throw new ConnectorException(ChameleonError.DATEPARSE_OPERATION_ERROR,
                            "Can't parse date[" + dateValue
                                    + "] LocalDate pattern [yyyy-MM-dd'T'HH:mm:ss[+-]hh:mm]: " + e);
                }

        } else if (function.isFunction(CST_FUNCTION_JSON)) {
            return new Gson().fromJson(function.allParams, Object.class);
        } else {
            logger.error("Unsupported function: [{}]", function.name);
            throw new ConnectorException(ChameleonError.UNKNOWN_FUNCTION,
                    "function[" + function.name + "] unknown");
        }
    }

    /**
     * Return the value. It may be a String, or a variable
     *
     * @param value          value to return
     * @param chameleonInput job to get variable
     * @return the value
     */
    private Object getValue(Object value, final ChameleonInput chameleonInput) {

        if (value instanceof String valueSt) {

            // try to parse different format
            try {
                return Integer.parseInt(valueSt);
            } catch (Exception e) {
                // do nothing, we try a different format
            }
            try {
                return Long.parseLong(valueSt);
            } catch (Exception e) {
                // do nothing, we try a different format
            }
            try {
                return Double.parseDouble(valueSt);
            } catch (Exception e) {
                // do nothing, we try a different format
            }

            return chameleonInput.getVariables().get(valueSt);
        }
        return value;
    }


    private int getPercent(Object percent, int count) {
        if (percent == null) {
            logger.error("ExecuteOperation: dataSet.get(\"%\") is null line {}]", count);
            throw new ConnectorException(ChameleonError.INCOMPLETE_PARAMETERS);
        }
        try {
            return Integer.parseInt(percent.toString());
        } catch (NumberFormatException e) {
            logger.error("ExecuteOperation: percent [{}] is not an integer line {}]", percent, count);
            throw new ConnectorException(ChameleonError.INCOMPLETE_PARAMETERS);
        }
    }
    /* ******************************************************************** */
    /*                                                                      */
    /*  Value                                                               */
    /*                                                                      */
    /* ******************************************************************** */

    /**
     * Function implementation
     */
    public static class FunctionDescription {
        public String name;
        /**
         * All parameters inside the ()
         */
        public String allParams;
        /**
         * Parameters are separate by ","
         */
        public List<String> parameters = new ArrayList<>();

        public boolean isFunction(String compareName) {
            return compareName.equalsIgnoreCase(name);
        }

        public String getParameter(int range) {
            return range < parameters.size() ? parameters.get(range) : null;
        }
    }

}
