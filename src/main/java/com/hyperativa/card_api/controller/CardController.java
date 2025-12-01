package com.hyperativa.card_api.controller;

import com.hyperativa.card_api.dto.CardInputDTO;
import com.hyperativa.card_api.dto.CardResponseDTO;
import com.hyperativa.card_api.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService service;

    @PostMapping
    public ResponseEntity<Map<String, UUID>> create(@RequestBody @Valid CardInputDTO dto) {
        var id = service.registerCard(dto.pan());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id));
    }

    @GetMapping("/{pan}")
    public ResponseEntity<CardResponseDTO> getCard(@PathVariable String pan) {
        return ResponseEntity.ok(service.getCardId(pan));
    }
}