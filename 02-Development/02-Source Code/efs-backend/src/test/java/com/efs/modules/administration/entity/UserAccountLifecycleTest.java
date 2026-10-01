package com.efs.modules.administration.entity;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserAccountLifecycleTest {

    @Test
    void exposesControlledAccountStatusLifecycleOperations() throws Exception {
        Method enable = UserAccount.class.getDeclaredMethod("enable");
        Method disable = UserAccount.class.getDeclaredMethod("disable");

        assertNotNull(enable);
        assertNotNull(disable);
    }

    @Test
    void enableTransitionsAccountStatusToActive() {
        UserAccount userAccount = new UserAccount();
        userAccount.setAccountStatus("INACTIVE");

        userAccount.enable();

        assertEquals("ACTIVE", userAccount.getAccountStatus());
    }

    @Test
    void disableTransitionsAccountStatusToInactive() {
        UserAccount userAccount = new UserAccount();
        userAccount.setAccountStatus("ACTIVE");

        userAccount.disable();

        assertEquals("INACTIVE", userAccount.getAccountStatus());
    }
}