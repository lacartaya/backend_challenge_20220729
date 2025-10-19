package com.celonis.challenge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class ExecutorConfig {
    @Bean(destroyMethod = "shutdownNow")
    public ScheduledExecutorService scheduledExecutorService() {
        // pool compartido para todas las tareas
        return Executors.newScheduledThreadPool(4);
    }
}
