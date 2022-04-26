package com.mysite.sbb.answer;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.question.QuestionDto;
import com.mysite.sbb.user.SiteUserDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final ModelMapper modelMapper;

    private Answer of(AnswerDto answerDto) {
        return this.modelMapper.map(answerDto, Answer.class);
    }

    private AnswerDto of(Answer answer) {
        return this.modelMapper.map(answer, AnswerDto.class);
    }

    public AnswerDto create(QuestionDto questionDto, String content, SiteUserDto author) {
        AnswerDto answerDto = new AnswerDto();
        answerDto.setContent(content);
        answerDto.setCreateDate(LocalDateTime.now());
        answerDto.setQuestion(questionDto);
        answerDto.setAuthor(author);
        answerDto = of(this.answerRepository.save(of(answerDto)));
        return answerDto;
    }

    public AnswerDto getAnswer(Integer id) {
        Optional<Answer> answer = this.answerRepository.findById(id);
        if (answer.isPresent()) {
            return of(answer.get());
        } else {
            throw new DataNotFoundException("answer not found");
        }
    }

    public AnswerDto modify(AnswerDto answerDto, String content) {
        answerDto.setContent(content);
        answerDto.setLastModified(LocalDateTime.now());
        this.answerRepository.save(of(answerDto));
        return answerDto;
    }

    public void delete(AnswerDto answerDto) {
        this.answerRepository.delete(of(answerDto));
    }

    public void vote(AnswerDto answerDto, SiteUserDto siteUserDto) {
        if (answerDto.getVoter().stream().anyMatch(n -> n.getUsername().equals(siteUserDto.getUsername()))) {
            answerDto.getVoter().removeIf(n -> n.getUsername().equals(siteUserDto.getUsername()));
        } else {
            answerDto.getVoter().add(siteUserDto);
        }
        this.answerRepository.save(of(answerDto));
    }
}