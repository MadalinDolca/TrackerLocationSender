package com.madalin.trackerlocationsender.hivemq

import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.MqttClientState
import com.hivemq.client.mqtt.MqttGlobalPublishFilter
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import java.nio.charset.StandardCharsets

class TrackerMqttClient(var host: String, var port: Int) {
    /**
     * Creates an MQTT client that can connect to [host] at [port].
     */
    private var client = MqttClient.builder()
        .useMqttVersion5()
        .serverHost(host)
        .serverPort(port)
        .sslWithDefaultConfig()
        .buildBlocking()

    //.identifier(ClientCredentials.clientId)
    //.automaticReconnectWithDefaultConfig()

    /**
     * Connects to HiveMQ Cloud with TLS and username/password.
     */
    fun connectToBroker(username: String, password: String) {
        client.connectWith()
            .simpleAuth()
            .username(username)
            .password(StandardCharsets.UTF_8.encode(password))
            .applySimpleAuth()
            .send()
    }

    /**
     * Subscribes to the given topic and sets a callback that is called when a message is received.
     */
    fun subscribeToTopic(topic: String) {
        client.subscribeWith()
            .topicFilter(topic)
            .send()

        // set a callback that is called when a message is received (using the async API style)
        client.toAsync().publishes(MqttGlobalPublishFilter.ALL) { publish: Mqtt5Publish ->
            println("Received message: " + publish.topic + " -> " + StandardCharsets.UTF_8.decode(publish.payload.get()))
        }
    }

    /**
     * Publishes the given message to the given topic.
     * @param topic where to publish
     * @param message content to publish
     */
    fun publishToTopic(topic: String, message: String) {
        client.publishWith()
            .topic(topic)
            .payload(StandardCharsets.UTF_8.encode(message))
            .send()
    }

    /**
     * Checks if the [client] is connected to the broker.
     * @return true if the client is connected
     */
    fun isConnected() = client.state == MqttClientState.CONNECTED

    /**
     * Disconnects this [client] with the default disconnect message.
     */
    fun disconnect() {
        client.disconnect()
    }
}