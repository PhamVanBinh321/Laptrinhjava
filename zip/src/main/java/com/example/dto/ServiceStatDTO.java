// src/main/java/com/example/dto/ServiceStatDTO.java
package com.example.dto;

public record ServiceStatDTO(
        String serviceName,
        long   times,
        double revenue
        ) { }
        
