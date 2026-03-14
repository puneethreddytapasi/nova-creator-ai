package com.example.novacreator.controller;

import com.example.novacreator.service.NovaService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class NovaController {

    private final NovaService novaService;

    public NovaController(NovaService novaService) {
        this.novaService = novaService;
    }

    @PostMapping("/generate")
    public String generate(@RequestBody String prompt) {
        return novaService.generate(prompt);
    }
}