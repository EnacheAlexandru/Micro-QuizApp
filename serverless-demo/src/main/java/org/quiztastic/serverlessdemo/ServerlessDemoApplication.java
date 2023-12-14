package org.quiztastic.serverlessdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.Function;

@SpringBootApplication
public class ServerlessDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerlessDemoApplication.class, args);
    }

    @Bean
    public Function<String, String> reverse() {
        return s -> String.valueOf(new StringBuilder(s).reverse());
    }

}
