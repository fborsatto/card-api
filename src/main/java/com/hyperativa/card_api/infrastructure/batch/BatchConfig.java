package com.hyperativa.card_api.infrastructure.batch;

import com.hyperativa.card_api.domain.Card;
import com.hyperativa.card_api.dto.CardInputDTO;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    public static final int CHUNK_SIZE = 1000;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    @StepScope
    public FlatFileItemReader<CardInputDTO> reader(@Value("#{jobParameters['fullPathFileName']}") String pathToFile) {
        return new FlatFileItemReaderBuilder<CardInputDTO>()
                .name("cardReader")
                .resource(new FileSystemResource(new File(pathToFile)))
                .linesToSkip(1)
                .lineMapper((line, lineNumber) -> {
                    if (line.startsWith("LOTE") || line.trim().isEmpty()) {
                        return null;
                    }

                    try {
                        if (line.length() < 8) {
                            log.warn("Line {} is too short: '{}'", lineNumber, line);
                            return null;
                        }

                        String panSection;
                        if (line.length() >= 26) {
                            panSection = line.substring(7, 26);
                        } else {
                            panSection = line.substring(7);
                        }

                        String cleanPan = panSection.trim().replaceAll("[^0-9]", "");
                        
                        if (cleanPan.isEmpty()) {
                            log.warn("Line {} is not a valid PAN: '{}'", lineNumber, line);
                            return null;
                        }

                        return new CardInputDTO(cleanPan);
                        
                    } catch (Exception e) {
                        log.error("Error processing line {}: '{}' - {}", lineNumber, line, e.getMessage());
                        return null;
                    }
                })
                .build();
    }

    @Bean
    public ItemProcessor<CardInputDTO, Card> processor() {
        return item -> {
            if (item.pan() == null) {
                return null;
            }
        
        String cleanPan = item.pan().trim();
        
        if (!cleanPan.matches("\\d+") || cleanPan.length() < 13 || cleanPan.length() > 19) {
            log.warn("Invalid Pan Ignored: '{}'", cleanPan);
            return null;
        }
        
        Card card = Card.builder().pan(cleanPan).build();
        card.setPanHash(cleanPan);
        
        return card;
    };
}

    @Bean
    public JpaItemWriter<Card> writer() {
        return new JpaItemWriterBuilder<Card>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    public Step importStep() {
        return new StepBuilder("importStep", jobRepository)
                .<CardInputDTO, Card>chunk(CHUNK_SIZE, transactionManager)
                .reader(reader(null))
                .processor(processor())
                .writer(writer())
                .faultTolerant()
                .skip(DataIntegrityViolationException.class)
                .skip(Exception.class)
                .skipLimit(10000)
                .build();
    }

    @Bean
    public Job importUserJob() {
        return new JobBuilder("importCardJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(importStep())
                .end()
                .build();
    }
}