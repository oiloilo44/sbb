package com.mysite.sbb.question;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.SiteUserDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final ModelMapper modelMapper;

    private QuestionDto of(Question question) {
        return this.modelMapper.map(question, QuestionDto.class);
    }

    private Question of(QuestionDto questionDto) {
        return this.modelMapper.map(questionDto, Question.class);
    }

    public Specification<Question> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
                return cb.or(cb.like(q.get("subject"), "%" + kw + "%"),
                        cb.like(q.get("content"), "%" + kw + "%"),
                        cb.like(u1.get("username"), "%" + kw + "%"),
                        cb.like(a.get("content"), "%" + kw + "%"),
                        cb.like(u2.get("username"), "%" + kw + "%"));
            }
        };
    }

    public Page<QuestionDto> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        sorts.add(Sort.Order.desc("subject"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Page<Question> questionList = this.questionRepository.findAllByKeyword(kw, pageable);
        return questionList.map(q -> of(q));
    }

//    public Page<QuestionDto> getList(int page, String kw) {
//        List<Sort.Order> sorts = new ArrayList<>();
//        sorts.add(Sort.Order.desc("createDate"));
//        sorts.add(Sort.Order.desc("subject"));
//        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
//        Specification<Question> spec = search(kw);
//        Page<Question> questionList = this.questionRepository.findAll(spec, pageable);
//        return questionList.map(q -> of(q));
//    }

    public QuestionDto getQuestion(Integer id) {
        Optional<Question> question = this.questionRepository.findById(id);
        if (question.isPresent()) {
            return of(question.get());
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    public QuestionDto create(String subject, String content, SiteUserDto user) {
        QuestionDto questionDto = new QuestionDto();
        questionDto.setSubject(subject);
        questionDto.setContent(content);
        questionDto.setCreateDate(LocalDateTime.now());
        questionDto.setAuthor(user);
        this.questionRepository.save(of(questionDto));
        return questionDto;
    }

    public QuestionDto modify(QuestionDto questionDto, String subject, String content) {
        questionDto.setSubject(subject);
        questionDto.setContent(content);
        questionDto.setLastModified(LocalDateTime.now());
        this.questionRepository.save(of(questionDto));
        return questionDto;
    }

    public void delete(QuestionDto questionDto) {
        this.questionRepository.delete(of(questionDto));
    }

    public void vote(QuestionDto questionDto, SiteUserDto siteUserDto) {
        if (questionDto.getVoter().stream().anyMatch(n -> n.getUsername().equals(siteUserDto.getUsername()))) {
            questionDto.getVoter().removeIf(n -> n.getUsername().equals(siteUserDto.getUsername()));
        } else {
            questionDto.getVoter().add(siteUserDto);
        }
        this.questionRepository.save(of(questionDto));
    }
}