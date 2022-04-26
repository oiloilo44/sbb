package com.mysite.sbb.question;

import com.mysite.sbb.answer.AnswerDto;
import com.mysite.sbb.user.SiteUserDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class QuestionDto {
    private Integer id;
    private String subject;
    private String content;
    private LocalDateTime createDate;
    private List<AnswerDto> answerList;
    private SiteUserDto author;
    private LocalDateTime lastModified;
    private Set<SiteUserDto> voter;
}