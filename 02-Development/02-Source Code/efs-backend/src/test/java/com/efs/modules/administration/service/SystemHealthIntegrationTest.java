package com.efs.modules.administration.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        properties = "efs.security.enabled=true"
)
@AutoConfigureMockMvc
class SystemHealthIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void healthViewPermissionShouldExistWithoutRoleAssignment() {

        Map<String, Object> permission =
                jdbcTemplate.queryForMap(
                        """
                        SELECT permission_code,
                               permission_name,
                               resource,
                               action
                        FROM administration.permission
                        WHERE permission_code = 'health.view'
                        """
                );

        assertEquals(
                "health.view",
                permission.get(
                        "permission_code"
                )
        );

        assertEquals(
                "View System Health",
                permission.get(
                        "permission_name"
                )
        );

        assertEquals(
                "health",
                permission.get(
                        "resource"
                )
        );

        assertEquals(
                "view",
                permission.get(
                        "action"
                )
        );

        Long roleAssignments =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM administration.role_permission rp
                        JOIN administration.permission p
                          ON p.permission_id = rp.permission_id
                        WHERE p.permission_code = 'health.view'
                        """,
                        Long.class
                );

        assertEquals(
                0L,
                roleAssignments.longValue()
        );
    }

    @Test
    void functionalHealthApiShouldRequireAuthentication()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/health"
                        )
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    void actuatorHealthShouldRemainPublic()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/actuator/health"
                        )
                )
                .andExpect(
                        status().isOk()
                );
    }
}