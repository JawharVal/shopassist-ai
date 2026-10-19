package com.shopassist.rag.controller;

import com.shopassist.rag.domain.AskRequest;
import com.shopassist.rag.domain.AskResponse;
import com.shopassist.rag.security.SecurityUtils;
import com.shopassist.rag.service.AnswerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/qa")
public class QaController {

    private final AnswerService answerService;
    private final SecurityUtils securityUtils;

    public QaController(AnswerService answerService, SecurityUtils securityUtils) {
        this.answerService = answerService;
        this.securityUtils = securityUtils;
    }

    @PostMapping("/ask")
    @ResponseStatus(HttpStatus.OK)
    public AskResponse ask(@Valid @RequestBody AskRequest request) {
        return answerService.ask(securityUtils.currentUser(), request);
    }
}
