// src/main/java/com/example/controller/ServiceController.java
package com.example.controller;

import com.example.model.Service;
import com.example.service.ServiceCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceCatalogService serviceCatalogService;

    @GetMapping("/services")
    public String listServices(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "12") int size,
            @RequestParam(name = "sort", defaultValue = "name") String sort,
            Model model
    ) {
        Page<Service> services = serviceCatalogService.searchActive(q, page, size, sort);
        model.addAttribute("services", services);
        model.addAttribute("q", q);
        model.addAttribute("sort", sort);
        model.addAttribute("currentPath", "/services");
        return "services"; // templates/services.html
    }

    @GetMapping("/services/{id}")
    public String serviceDetail(@PathVariable Integer id, Model model) {
        Service s = serviceCatalogService.getActiveById(id);
        model.addAttribute("s", s);
        model.addAttribute("currentPath", "/services");
        return "service-detail"; // templates/service-detail.html
    }
}
