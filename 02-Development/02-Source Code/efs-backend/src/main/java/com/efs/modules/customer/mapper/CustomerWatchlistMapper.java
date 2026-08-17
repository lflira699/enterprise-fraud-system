package com.efs.modules.customer.mapper;

import com.efs.modules.customer.dto.CustomerWatchlistRequest;
import com.efs.modules.customer.dto.CustomerWatchlistResponse;
import com.efs.modules.customer.entity.CustomerWatchlist;
import org.springframework.stereotype.Component;

@Component
public class CustomerWatchlistMapper {

    public CustomerWatchlist toEntity(
            CustomerWatchlistRequest request) {

        CustomerWatchlist watchlist =
                new CustomerWatchlist();

        watchlist.setWatchlistType(request.getWatchlistType());
        watchlist.setWatchlistSource(request.getWatchlistSource());
        watchlist.setMatchStatus(request.getMatchStatus());
        watchlist.setMatchScore(request.getMatchScore());
        watchlist.setMatchedName(request.getMatchedName());
        watchlist.setReferenceId(request.getReferenceId());
        watchlist.setDetectedAt(request.getDetectedAt());
        watchlist.setLastCheckedAt(request.getLastCheckedAt());
        watchlist.setActive(request.getActive());
        watchlist.setCreatedBy(request.getCreatedBy());
        watchlist.setUpdatedBy(request.getUpdatedBy());

        return watchlist;
    }

    public void updateEntity(
            CustomerWatchlistRequest request,
            CustomerWatchlist watchlist) {

        watchlist.setWatchlistType(request.getWatchlistType());
        watchlist.setWatchlistSource(request.getWatchlistSource());
        watchlist.setMatchStatus(request.getMatchStatus());
        watchlist.setMatchScore(request.getMatchScore());
        watchlist.setMatchedName(request.getMatchedName());
        watchlist.setReferenceId(request.getReferenceId());
        watchlist.setDetectedAt(request.getDetectedAt());
        watchlist.setLastCheckedAt(request.getLastCheckedAt());
        watchlist.setActive(request.getActive());
        watchlist.setUpdatedBy(request.getUpdatedBy());
    }

    public CustomerWatchlistResponse toResponse(
            CustomerWatchlist watchlist) {

        CustomerWatchlistResponse response =
                new CustomerWatchlistResponse();

        response.setWatchlistId(watchlist.getWatchlistId());
        response.setCustomerId(watchlist.getCustomerId());
        response.setWatchlistType(watchlist.getWatchlistType());
        response.setWatchlistSource(watchlist.getWatchlistSource());
        response.setMatchStatus(watchlist.getMatchStatus());
        response.setMatchScore(watchlist.getMatchScore());
        response.setMatchedName(watchlist.getMatchedName());
        response.setReferenceId(watchlist.getReferenceId());
        response.setDetectedAt(watchlist.getDetectedAt());
        response.setLastCheckedAt(watchlist.getLastCheckedAt());
        response.setActive(watchlist.getActive());
        response.setCreatedAt(watchlist.getCreatedAt());
        response.setCreatedBy(watchlist.getCreatedBy());
        response.setUpdatedAt(watchlist.getUpdatedAt());
        response.setUpdatedBy(watchlist.getUpdatedBy());
        response.setDeletedAt(watchlist.getDeletedAt());
        response.setRecordVersion(watchlist.getRecordVersion());

        return response;
    }
}