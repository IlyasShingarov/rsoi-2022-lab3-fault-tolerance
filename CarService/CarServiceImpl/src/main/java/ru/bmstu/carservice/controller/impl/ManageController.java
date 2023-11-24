package ru.bmstu.carservice.controller.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manage")
public class ManageController {
    @GetMapping("/health")
    ResponseEntity<?> health() {
        return ResponseEntity.ok().build();
    }
}
