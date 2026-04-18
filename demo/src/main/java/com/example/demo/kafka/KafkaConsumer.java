package com.example.demo.kafka;

import com.example.demo.question.Question;
import com.example.demo.question.QuestionRepository;
import com.example.demo.question.QuestionSaveEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {
    private final ObjectMapper objectMapper;
    private final QuestionRepository questionRepository;

    @KafkaListener(topics = "${spring.kafka.template.default-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, String> record) {
        try {
            QuestionSaveEvent event = objectMapper.readValue(record.value(), QuestionSaveEvent.class);
            questionRepository.save(new Question(event.getEmail(), event.getUsername(), event.getQuestion()));
            log.info("consumer: success >>> message: {}, offset: {}", record.value(), record.offset());
        } catch (JsonProcessingException e) {
            log.error("consumer: json parse failure >>> message: {}", record.value(), e);
        } catch (Exception e) {
            log.error("consumer: save failure >>> message: {}", record.value(), e);
        }
    }
}
