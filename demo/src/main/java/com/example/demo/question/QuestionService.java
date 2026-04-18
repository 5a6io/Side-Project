package com.example.demo.question;

import com.example.demo.kafka.KafkaProducer;
import com.example.demo.user.jwt.JwtTokenProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class QuestionService {
    private final JwtTokenProvider jwtTokenProvider;
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    public QuestionService(JwtTokenProvider jwtTokenProvider, KafkaProducer kafkaProducer, ObjectMapper objectMapper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.kafkaProducer = kafkaProducer;
        this.objectMapper = objectMapper;
    }

    public QuestionResponse saveQuestion(QuestionRequestDto questionRequestDto) {
        if (!jwtTokenProvider.validateToken(questionRequestDto.getToken())) {
            return new QuestionResponse("토큰이 유효하지 않습니다.");
        }

        QuestionSaveEvent event = QuestionSaveEvent.from(questionRequestDto);

        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaProducer.sendMessage(payload);
        } catch (JsonProcessingException e) {
            log.error("질문 이벤트 직렬화에 실패했습니다.", e);
            return new QuestionResponse("질문 저장 요청 처리 중 오류가 발생했습니다.");
        }

        log.info("질문 저장 요청이 Kafka로 전송되었습니다. username='{}'", questionRequestDto.getUsername());

        return new QuestionResponse("질문 저장 요청이 접수되었습니다.", questionRequestDto.getQuestion());
    }
}
