package com.redsun.api.hierarchy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")

public class ReadinessController {

    private static final Logger logger = LoggerFactory.getLogger(ReadinessController.class);

    /**
     * Readiness check endpoint.
     *
     * @return a map containing the readiness status
     */
    @GetMapping("/readiness")
    public Map<String, String> readinessCheck() {
        Map<String, String> healthStatus = new HashMap<>();
        logger.info("readinessCheck called");
        try {
            healthStatus.put("status", "UP");
            healthStatus.put("message", "Readiness check successful");
        } catch (Exception e) {
            logger.error("Error in readinessCheck method: {}", e.getMessage(), e);
            healthStatus.put("status", "DOWN");
            healthStatus.put("message", "Readiness check failed");
        }
        return healthStatus;
    }

    @GetMapping("/liveness")
    public Map<String, String> livenessCheck() {
        logger.info("livenessCheck called");
        Map<String, String> healthStatus = new HashMap<>();
        try {
            healthStatus.put("status", "UP");
            healthStatus.put("message", "Liveness check successful");
        } catch (Exception e) {
            logger.error("Error in livenessCheck method: {}", e.getMessage(), e);
            healthStatus.put("status", "DOWN");
            healthStatus.put("message", "Liveness check failed");
        }
        return healthStatus;
    }
}
