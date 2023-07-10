package com.madalin.trackerlocationsender.hivemq

object BrokerCredentials {
    const val clusterUrl = "868b4e59e6c341379e82120b3e1d456e.s2.eu.hivemq.cloud"
    const val port = 8883
    const val websocketPort = 8884

    /**
     * Broker's address that is a combination of [clusterUrl] and [port].
     * Looks like this `tcp://<clusterUrl>:<port>`.
     */
    const val serverAddress = "tcp://$clusterUrl:$websocketPort"
}