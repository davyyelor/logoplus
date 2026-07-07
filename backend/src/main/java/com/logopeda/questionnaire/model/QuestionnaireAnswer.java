package com.logopeda.questionnaire.model;

import com.logopeda.shared.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/** A single answer to a question within a {@link QuestionnaireResponse}. */
@Entity
@Table(name = "questionnaire_answers", indexes = {
        @Index(name = "idx_qanswer_response", columnList = "responseId"),
        @Index(name = "idx_qanswer_question", columnList = "questionId")
})
public class QuestionnaireAnswer extends BaseEntity {

    @Column(nullable = false, length = 36)
    private String clinicId;

    @Column(nullable = false, length = 36)
    private String responseId;

    @Column(nullable = false, length = 36)
    private String questionId;

    @Column(length = 4000)
    private String answerText;

    private Double answerNumber;

    @Column(length = 2000)
    private String answerJson;

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public Double getAnswerNumber() {
        return answerNumber;
    }

    public void setAnswerNumber(Double answerNumber) {
        this.answerNumber = answerNumber;
    }

    public String getAnswerJson() {
        return answerJson;
    }

    public void setAnswerJson(String answerJson) {
        this.answerJson = answerJson;
    }
}
