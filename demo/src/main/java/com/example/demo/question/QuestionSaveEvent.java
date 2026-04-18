package com.example.demo.question;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionSaveEvent {
    private String email;
    private String username;
    private String question;

    public static QuestionSaveEvent from(QuestionRequestDto requestDto) {
        return new QuestionSaveEvent(
                requestDto.getEmail(),
                requestDto.getUsername(),
                requestDto.getQuestion()
        );
    }
}
