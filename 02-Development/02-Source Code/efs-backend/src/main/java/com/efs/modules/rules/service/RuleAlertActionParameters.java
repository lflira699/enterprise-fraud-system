package com.efs.modules.rules.service;

public record RuleAlertActionParameters(
        String alertType,
        String priority) {
}