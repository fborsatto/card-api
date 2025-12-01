package com.hyperativa.card_api.infrastructure.repository;

import com.hyperativa.card_api.domain.Card;
import com.hyperativa.card_api.infrastructure.security.HashUtils;
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
        String panHash = HashUtils.generateSha256Hash(pan);
        return findByPanHash(panHash);
    }

    @Query("SELECT COUNT(c) > 0 FROM Card c WHERE c.panHash = :panHash")
    boolean existsByPanHash(@Param("panHash") String panHash);

    default boolean existsByPan(String pan) {
        String panHash = HashUtils.generateSha256Hash(pan);
        return existsByPanHash(panHash);
    }
}