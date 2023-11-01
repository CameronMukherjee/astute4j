# astute4j


## Setup:
1. Add the `@EnableDiscoveryClient` to your application.
```java

@EnableDiscoveryClient
@SpringBootApplication
public class Astute4jApplication {

    public static void main(String[] args) {
        SpringApplication.run(Astute4jApplication.class, args);
    }

}
```

2. Add the required configuration to your `application.yml` / `application.properties` file, choosing rather to use all namespaces or specific namespaces.
```json
spring:
  application:
    name: ${APPLICATION_NAME:astute4j}
  cloud:
    kubernetes:
      discovery:
        all-namespaces: true
        namespaces:
          - namespace-a
          - namespace-b
```
3. 