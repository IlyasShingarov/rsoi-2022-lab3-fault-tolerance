package ru.bmstu.gatewayservice.controller.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manage")
public class ManageController {
    @GetMapping("/health")
    ResponseEntity<?> health() {
        return ResponseEntity.ok().build();
    }
}
