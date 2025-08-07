// src/main/java/com/example/dto/TopCustomerDTO.java
package com.example.dto;

public record TopCustomerDTO(
        String customerName,
        String email,
        long   times,
        double spending) { }
