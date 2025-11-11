package com.malaka.aat.internal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableJpaAuditing
@ComponentScan(basePackages = {
        "com.malaka.aat.internal",    // Scan internal module components
        "com.malaka.aat.core"          // Scan core module components (includes GlobalExceptionHandler)
})
public class MalakaInternalApplication {

    public static void main(String[] args) {
        SpringApplication.run(MalakaInternalApplication.class, args);
    }
}
