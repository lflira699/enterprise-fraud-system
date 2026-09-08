package com.efs.modules.rules.service;

import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.rules.mapper.RuleExecutionMapper;
import com.efs.modules.rules.repository.RuleExecutionRepository;
import com.efs.modules.rules.repository.RuleRepository;
import com.efs.shared.security.SecurityContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RuleExecutionServiceTest {

    private static final UUID RULE_ID =
            UUID.fromString(
                    "32323232-3232-3232-3232-323232323232"
            );

    private static final UUID USER_ID =
            UUID.fromString(
                    "33333333-3333-3333-3333-333333333333"
            );

    @Mock
    private RuleExecutionRepository ruleExecutionRepository;

    @Mock
    private RuleExecutionMapper ruleExecutionMapper;

    @Mock
    private RuleRepository ruleRepository;

    @Mock
    private AuditEventServiceInterface auditEventService;

    @InjectMocks
    private RuleExecutionService service;

    @Test
    void shouldAuditRuleExecutionHistoryRetrievalFailure() {

        IllegalStateException retrievalFailure =
                new IllegalStateException(
                        "Test rule execution history retrieval failure"
                );

        when(
                ruleRepository.existsById(
                        RULE_ID
                )
        ).thenReturn(
                true
        );

        when(
                ruleExecutionRepository
                        .findByRuleIdOrderByExecutedAtDesc(
                                RULE_ID
                        )
        ).thenThrow(
                retrievalFailure
        );

        IllegalStateException thrown =
                assertThrows(
                        IllegalStateException.class,
                        () -> service.getRuleExecutionsByRuleId(
                                RULE_ID,
                                authorizedSecurityContext()
                        )
                );

        assertSame(
                retrievalFailure,
                thrown
        );

        ArgumentCaptor<AuditEventRequest> auditCaptor =
                ArgumentCaptor.forClass(
                        AuditEventRequest.class
                );

        verify(
                auditEventService
        ).createAuditEvent(
                auditCaptor.capture()
        );

        AuditEventRequest audit =
                auditCaptor.getValue();

        assertEquals(
                USER_ID,
                audit.getUserId()
        );

        assertEquals(
                "RULE_EXECUTION_HISTORY_VIEW",
                audit.getEventType()
        );

        assertEquals(
                "RULE",
                audit.getEntityType()
        );

        assertEquals(
                RULE_ID,
                audit.getEntityId()
        );

        assertEquals(
                "VIEW",
                audit.getAction()
        );

        assertEquals(
                "RULE_ENGINE",
                audit.getSourceComponent()
        );

        assertEquals(
                "FAILURE",
                audit.getEventResult()
        );

        assertEquals(
                "HISTORY_RETRIEVAL_FAILED",
                audit.getEventDetails()
                        .get("reason")
        );

        assertEquals(
                "IllegalStateException",
                audit.getEventDetails()
                        .get("errorType")
        );
    }

    private SecurityContext authorizedSecurityContext() {

        return new SecurityContext(
                USER_ID,
                null,
                null,
                Set.of(),
                Set.of(
                        RuleExecutionServiceInterface
                                .RULE_EXECUTION_VIEW_PERMISSION
                ),
                Set.of()
        );
    }
}