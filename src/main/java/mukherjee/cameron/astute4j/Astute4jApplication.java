package mukherjee.cameron.astute4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class Astute4jApplication {

    public static void main(String[] args) {
        SpringApplication.run(Astute4jApplication.class, args);
    }

}
