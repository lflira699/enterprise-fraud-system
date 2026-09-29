package com.efs.modules.administration.service;

import com.efs.modules.administration.entity.UserSession;
import com.efs.modules.administration.repository.UserSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UserSessionBindingService {

    private static final String ACTIVE_SESSION_STATUS =
            "ACTIVE";

    private final UserSessionRepository
            userSessionRepository;

    public UserSessionBindingService(
            UserSessionRepository userSessionRepository) {

        this.userSessionRepository =
                userSessionRepository;
    }

    @Transactional
    public void establishSession(
            UUID sessionId,
            UUID userId) {

        validateIdentifiers(
                sessionId,
                userId
        );

        Optional<UserSession> existingSession =
                userSessionRepository.findById(
                        sessionId
                );

        if (existingSession.isPresent()) {

            UserSession session =
                    existingSession.get();

            if (!userId.equals(
                    session.getUserId()
            )) {

                throw new IllegalStateException(
                        "EFS session is bound to a different user: "
                                + sessionId
                );
            }

            if (!ACTIVE_SESSION_STATUS.equals(
                    session.getSessionStatus()
            )) {

                throw new IllegalStateException(
                        "EFS session cannot be reactivated: "
                                + sessionId
                );
            }

            return;
        }

        LocalDateTime now =
                LocalDateTime.now();

        UserSession session =
                new UserSession(
                        sessionId,
                        userId,
                        now,
                        ACTIVE_SESSION_STATUS,
                        now
                );

        userSessionRepository.saveAndFlush(
                session
        );
    }

    public void requireActiveSession(
            UUID sessionId,
            UUID userId) {

        validateIdentifiers(
                sessionId,
                userId
        );

        if (userSessionRepository
                .findBySessionIdAndUserIdAndSessionStatus(
                        sessionId,
                        userId,
                        ACTIVE_SESSION_STATUS
                )
                .isEmpty()) {

            throw new IllegalStateException(
                    "Active EFS user session is not available: "
                            + sessionId
            );
        }
    }

    private void validateIdentifiers(
            UUID sessionId,
            UUID userId) {

        if (sessionId == null) {
            throw new IllegalStateException(
                    "Authenticated EFS sessionId is required"
            );
        }

        if (userId == null) {
            throw new IllegalArgumentException(
                    "userId is required"
            );
        }
    }
}
