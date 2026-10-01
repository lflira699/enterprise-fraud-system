package com.efs.modules.administration.entity;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RoleLifecycleTest {

    @Test
    void exposesControlledRoleStatusLifecycleOperations()
            throws Exception {

        Method enable =
                Role.class.getDeclaredMethod(
                        "enable"
                );

        Method disable =
                Role.class.getDeclaredMethod(
                        "disable"
                );

        assertNotNull(enable);
        assertNotNull(disable);
    }

    @Test
    void enableTransitionsRoleStatusToActive() {

        Role role =
                new Role();

        role.setStatus(
                "INACTIVE"
        );

        role.enable();

        assertEquals(
                "ACTIVE",
                role.getStatus()
        );
    }

    @Test
    void disableTransitionsRoleStatusToInactive() {

        Role role =
                new Role();

        role.setStatus(
                "ACTIVE"
        );

        role.disable();

        assertEquals(
                "INACTIVE",
                role.getStatus()
        );
    }
}
