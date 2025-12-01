package com.hyperativa.card_api.infrastructure.repository;

import com.hyperativa.card_api.domain.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {

     Optional<Card> findByPanHash(@Param("panHash") String panHash);

    default Optional<Card> findByPan(String pan) {
        String panHash = hashPan(pan);
        return findByPanHash(panHash);
    }

    @Query("SELECT COUNT(c) > 0 FROM Card c WHERE c.panHash = :panHash")
    boolean existsByPanHash(@Param("panHash") String panHash);

    default boolean existsByPan(String pan) {
        String panHash = hashPan(pan);
        return existsByPanHash(panHash);
    }


    private String hashPan(String pan) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(pan.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao calcular hash do PAN", e);
        }
    }

}