package com.wyy.iot;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Date;

/**
 * Hello world!
 */
@Slf4j
public class Mqtt {

    private String broker;
    private String clientId;
    private MemoryPersistence persistence = new MemoryPersistence();

    private MqttClient mqttClient;

    public Mqtt(String broker, String clientId) {
        this.broker = broker;
        this.clientId = clientId;
        init();
    }

    private void init() {
        try {
            mqttClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            mqttClient.connect(connOpts);

            mqttClient.subscribe(Constants.P2P_PREFIX + clientId, new IMqttMessageListener() {
                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    System.out.println(mqttMessage.toString());
                }
            });
        } catch (MqttException e) {
            log.error("init mqtt client error", e);
            System.exit(1);
        }
    }

    public void sendMsg(String topic, String msg) {
        MqttMessage message = new MqttMessage(msg.getBytes());
        message.setQos(2);
        try {
            mqttClient.publish(topic, message);
        } catch (MqttException e) {
            log.error("send {} to {} error={}", message, topic, e);
            e.printStackTrace();
        }
        log.debug("send {} to {} success", message, topic);
    }

    public void test() {


        String topic = Constants.P2P_PREFIX + clientId;
        String content = new Date() + "Message from MqttPublishSample";
        int qos = 2;

        try {
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            mqttClient.publish(topic, message);
            System.out.println("Message published");

        } catch (Exception me) {
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }

    public void shutdown() {
        if (mqttClient != null) {
            try {
                log.info("shutdown mqtt client");
                mqttClient.disconnectForcibly();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

}
