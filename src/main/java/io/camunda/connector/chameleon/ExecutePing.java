package io.camunda.connector.chameleon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

public class ExecutePing {
    private final Logger logger = LoggerFactory.getLogger(ExecutePing.class.getName());

    public void execute(ChameleonInput chameleonInput, ChameleonOutput chameleonOutput) throws Exception {

        if (Boolean.TRUE.equals(chameleonInput.getPingSimulation())) {
            InetAddress ipAddress = InetAddress.getLocalHost();
            chameleonOutput.pingIpAddress = ipAddress.getHostAddress();
            chameleonOutput.pingTimeStampMS = System.currentTimeMillis();
            chameleonOutput.pingMessage = chameleonInput.getPingMessage();
            logger.info("ExecutePing: ipAddress[{}] timeStamps[{}] message[{}]",
                    chameleonOutput.pingIpAddress,
                    chameleonOutput.pingTimeStampMS,
                    chameleonOutput.pingMessage);
        }
    }
}
