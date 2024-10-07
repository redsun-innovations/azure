package com.api.data_migrator.service;

import com.azure.core.amqp.AmqpTransportType;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AzureServiceBus {

    private static final Logger logger = LoggerFactory.getLogger(AzureServiceBus.class);

    public static void sendToAzureTopic(String jsonMessage, String serviceBusConnectionString, String topicName) {
        try {
            ServiceBusSenderClient senderClient = new ServiceBusClientBuilder()
                    .connectionString(serviceBusConnectionString)
                    .transportType(AmqpTransportType.AMQP_WEB_SOCKETS)
                    .sender()
                    .topicName(topicName)
                    .buildClient();

            senderClient.sendMessage(new com.azure.messaging.servicebus.ServiceBusMessage(jsonMessage));
            senderClient.close();

            logger.info("Message sent successfully.");
        } catch (Exception e) {
            logger.error("Error while sending message to Azure Service Bus", e);
        }
    }
}