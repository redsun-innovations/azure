package org.redsun.AzureService;
import com.azure.core.amqp.AmqpTransportType;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzureServiceBus {
    //private static final Logger Logger LoggerFactory.getLogger(org.example.AzureServiceBus.class);
    public static void sendToAzureTopic (String jsonMessage, String serviceBusConnectionString, String topicName) {
        ServiceBusSenderClient senderClient = new ServiceBusClientBuilder()
                .connectionString(serviceBusConnectionString)
                .transportType (AmqpTransportType.AMQP_WEB_SOCKETS)
                .sender()
                .topicName(topicName)
                .buildClient();
        senderClient.sendMessage(new com.azure.messaging.servicebus. ServiceBusMessage (jsonMessage)); senderClient.close();
    }
}