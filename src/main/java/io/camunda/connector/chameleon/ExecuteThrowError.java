package io.camunda.connector.chameleon;

import io.camunda.connector.api.error.ConnectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class ExecuteThrowError {
    private final Logger logger = LoggerFactory.getLogger(ExecuteThrowError.class.getName());

    public void execute(ChameleonInput chameleonInput, ChameleonOutput chameleonOutput) throws ConnectorException {
        if (! Boolean.TRUE.equals(chameleonInput.getErrorSimulation())) {
            return;
        }
        List<Map<String, Object>> errors = chameleonInput.getThrowErrors();
        if (errors == null) {
            logger.error("ExecuteError: Simulate operation is required but operation details is not provided");
            throw new ConnectorException(ChameleonError.INCOMPLETE_PARAMETERS);
        }
        try {
            int random = (int) (Math.random() * 100);
            int base = 0;
            int count=0;
            for (Map<String, Object> error : errors) {
                count++;
                int percent = getPercent(error.get("%"), count);
                logger.info("Check error random [{}] Range:[{}-{}]", random, base, percent + base);
                if (random <= base + percent) {
                    logger.info("ExecuteError: Found error to throws [{}] message[{}]", error.get("code"), error.get("message"));
                    // this is the error we want to throw
                    throw new ConnectorException(error.get("code").toString(), error.get("message") == null ? "" : error.get("message").toString());
                }
                base += percent;
            }
            logger.info("ExecuteError: No range check for random [{}] ", random);
            // no error
        } catch(ConnectorException ce) {
            throw ce;
        } catch (Exception e) {
            logger.error("ExecuteError: Error during execution ThrowBpmn [{}] ", errors, e);
        }
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
}
