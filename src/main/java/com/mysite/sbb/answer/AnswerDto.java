package com.mysite.sbb.answer;

import com.mysite.sbb.question.QuestionDto;
import com.mysite.sbb.user.SiteUserDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class AnswerDto {
    private Integer id;
    private String content;
    private LocalDateTime createDate;
    private QuestionDto question;
    private SiteUserDto author;
    private LocalDateTime lastModified;
    private Set<SiteUserDto> voter;
}
