package com.madalin.trackerlocationsender.hivemq

import android.util.Log
import com.hivemq.client.internal.mqtt.MqttRxClient
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.MqttClientState
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import com.hivemq.client.mqtt.mqtt5.Mqtt5RxClient
import com.hivemq.client.mqtt.mqtt5.message.auth.Mqtt5SimpleAuth
import com.hivemq.client.mqtt.mqtt5.message.auth.Mqtt5SimpleAuthBuilder
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import io.reactivex.Flowable

class TrackerMqttClient {
    private lateinit var mqttClient: Mqtt5BlockingClient

    fun connectToBroker() {
        val simpleAuth = Mqtt5SimpleAuth.builder()
            .username(ClientCredentials.username)
            .password(ClientCredentials.password.toByteArray())
            .build()

        mqttClient = MqttClient.builder()
            .useMqttVersion5()
            .serverHost(BrokerCredentials.serverAddress)
            .identifier(ClientCredentials.clientId)
            .simpleAuth(simpleAuth)
            .buildBlocking()

        mqttClient.connect()

        if (mqttClient.state == MqttClientState.CONNECTED) {
            // Connection successful
            Log.d("CONNACK", "Success!")
        } else {
            Log.e("Fail", "Failed!")
            // Connection failed
        }
    }

    fun publishMessage(topic: String, message: String) {
        mqttClient.publishWith()
            .topic(topic)
            .payload(message.toByteArray())
            .send()
    }

    fun disconnect() {
        if (mqttClient.state === MqttClientState.CONNECTED) {
            mqttClient.disconnect()
        }
    }

    /*fun connectToBroker() {
        client = Mqtt5Client.builder()
            .identifier(ClientCredentials.clientId)
            .serverHost(BrokerCredentials.serverAddress)
            .automaticReconnectWithDefaultConfig()
            .sslWithDefaultConfig()
            .simpleAuth()
            .username(ClientCredentials.username)
            .password(ClientCredentials.password.toByteArray())
            .applySimpleAuth()
            .buildRx()

        client.connect().blockingGet()
    }*/

    /* fun publishMessage(topic: String, message: String) {
         val mqttMessage: Mqtt5Publish = Mqtt5Publish.builder()
             .topic(topic)
             .payload(message.toByteArray())
             .build()

        /* client.publish(mqttMessage).whenComplete { _, throwable ->
             if (throwable != null) {
                 // Handle the error
             } else {
                 // Message published successfully
             }
         }*/
     }*/
}