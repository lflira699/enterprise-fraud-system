package com.efs.modules.transaction.mapper;

import com.efs.modules.transaction.dto.TransactionScoreRequest;
import com.efs.modules.transaction.dto.TransactionScoreResponse;
import com.efs.modules.transaction.entity.TransactionScore;
import org.springframework.stereotype.Component;

@Component
public class TransactionScoreMapper {

    public TransactionScore toEntity(
            TransactionScoreRequest request) {

        TransactionScore score =
                new TransactionScore();

        score.setScoreType(
                request.getScoreType()
        );

        score.setScoreValue(
                request.getScoreValue()
        );

        score.setScoreWeight(
                request.getScoreWeight()
        );

        score.setScoringModel(
                request.getScoringModel()
        );

        score.setModelVersion(
                request.getModelVersion()
        );

        score.setCalculatedAt(
                request.getCalculatedAt()
        );

        return score;
    }

    public TransactionScoreResponse toResponse(
            TransactionScore score) {

        TransactionScoreResponse response =
                new TransactionScoreResponse();

        response.setScoreId(
                score.getScoreId()
        );

        response.setTransactionId(
                score.getTransactionId()
        );

        response.setScoreType(
                score.getScoreType()
        );

        response.setScoreValue(
                score.getScoreValue()
        );

        response.setScoreWeight(
                score.getScoreWeight()
        );

        response.setScoringModel(
                score.getScoringModel()
        );

        response.setModelVersion(
                score.getModelVersion()
        );

        response.setCalculatedAt(
                score.getCalculatedAt()
        );

        return response;
    }
}