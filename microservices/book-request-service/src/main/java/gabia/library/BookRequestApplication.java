package gabia.library;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@EnableDiscoveryClient
@SpringBootApplication(exclude = { KafkaAutoConfiguration.class })
public class BookRequestApplication {

    private static final String APPLICATION_LOCATIONS = "spring.config.location="
            + "classpath:application.yml,"
            + "/root/gabia-library-config/book-request-service.yml";

    public static void main(String[] args) {
        new SpringApplicationBuilder(BookRequestApplication.class)
                .properties(APPLICATION_LOCATIONS)
                .run(args);
    }

}
