package com.hyperativa.card_api.service;

import com.hyperativa.card_api.dto.CardResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface CardService {
    UUID registerCard(String pan);
    CardResponseDTO getCardId(String pan);
}