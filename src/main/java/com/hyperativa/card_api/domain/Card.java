package com.hyperativa.card_api.domain;

import com.hyperativa.card_api.infrastructure.security.EncryptionConverter;
import com.hyperativa.card_api.infrastructure.security.HashUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

@Entity
@Table(name = "cards", indexes = {@Index(name = "idx_card_pan_hash", columnList = "panHash", unique = true)})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Convert(converter = EncryptionConverter.class)
    private String pan;

    @Column(nullable = false, unique = true, length = 64, name = "pan_hash")
    private String panHash;

    public void setPanHash(String pan) {
        this.panHash = generateHash(pan);
    }

    private String generateHash(String pan) {
        return HashUtils.generateSha256Hash(pan);
    }


}