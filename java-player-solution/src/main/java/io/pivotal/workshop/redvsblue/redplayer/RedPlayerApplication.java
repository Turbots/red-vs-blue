package io.pivotal.workshop.redvsblue.redplayer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties(RedPlayerProperties.class)
@EnableBinding(OutputChannels.class)
public class RedPlayerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedPlayerApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
