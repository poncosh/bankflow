package com.multimatics.bankflow.transfer.config;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.restclient.autoconfigure.RestClientBuilderConfigurer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
@Configuration
@EnableConfigurationProperties(AccountClientProperties.class)
public class AccountClientConfiguration {
    @Bean
    @Primary
    RestClient.Builder restClientBuilder(
            RestClientBuilderConfigurer configurer) {
        return configurer.configure(RestClient.builder());
    }

    @Bean
    @LoadBalanced
    RestClient.Builder loadBalancedRestClientBuilder(
            RestClientBuilderConfigurer configurer) {
        return configurer.configure(RestClient.builder());
    }

    @Bean
    RestClient accountRestClient(
            @LoadBalanced RestClient.Builder builder,
            AccountClientProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.connectTimeout());
        factory.setReadTimeout(properties.readTimeout());
        RestClient.Builder accountBuilder = builder.requestFactory(factory);
        if (properties.labDelayMs() > 0) {
            accountBuilder.defaultHeader(
                    "X-BankFlow-Lab-Delay-Ms",
                    Long.toString(properties.labDelayMs()));
        }
        return accountBuilder.build();
    }
}
