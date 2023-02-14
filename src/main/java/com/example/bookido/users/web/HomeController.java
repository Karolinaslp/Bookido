package com.example.bookido.users.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
class HomeController {
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("OK");
    }
}
