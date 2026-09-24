package com.multimatics.bankflow.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest(
        webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties="spring.kafka.listener.auto-startup=false")
class NotificationServiceApplicationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private KafkaListenerEndpointRegistry listenerRegistry;

    @Test
    void startsWebServerAndRegistersKafkaListener() {
        assertTrue(port > 0);
        assertEquals(1,listenerRegistry.getListenerContainers().size());
    }

    @Test
    void exposesApplicationInfo() throws Exception {
        var request=HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:"+port+"/actuator/info"))
                .GET()
                .build();

        var response=HttpClient.newHttpClient().send(
                request,HttpResponse.BodyHandlers.ofString());

        assertEquals(200,response.statusCode());
        assertTrue(response.body().contains("notification-service"));
        assertTrue(response.body().contains("bankflow.transfer.completed.v1"));
    }
}
