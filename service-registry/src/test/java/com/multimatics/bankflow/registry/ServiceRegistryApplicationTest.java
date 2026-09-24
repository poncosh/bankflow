package com.multimatics.bankflow.registry;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ServiceRegistryApplicationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private Environment environment;

    @Test
    void loadsStandaloneServiceRegistryConfiguration() {
        assertThat(environment.getProperty("spring.application.name"))
                .isEqualTo("service-registry");
        assertThat(environment.getProperty("eureka.client.register-with-eureka", Boolean.class))
                .isFalse();
        assertThat(environment.getProperty("eureka.client.fetch-registry", Boolean.class))
                .isFalse();
        assertThat(environment.getProperty("eureka.instance.hostname"))
                .isEqualTo("service-registry");
        assertThat(environment.getProperty("eureka.client.service-url.defaultZone"))
                .startsWith("http://localhost:")
                .endsWith("/eureka/");
    }

    @Test
    void exposesHealthyActuatorEndpoint() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/actuator/health"))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("\"status\":\"UP\"");
    }

    @Test
    void exposesConfiguredDomainInDsReplicas() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/"))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body())
                .contains("<h1>DS Replicas</h1>")
                .contains(">localhost</a>");
    }
}
