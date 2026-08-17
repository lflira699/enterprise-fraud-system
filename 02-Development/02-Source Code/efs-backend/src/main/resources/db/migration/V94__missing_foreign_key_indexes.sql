CREATE INDEX idx_feature_flag_created_by
    ON administration.feature_flag (created_by);

CREATE INDEX idx_role_permission_granted_by
    ON administration.role_permission (granted_by);

CREATE INDEX idx_system_configuration_updated_by
    ON administration.system_configuration (updated_by);

CREATE INDEX idx_user_account_business_unit_id
    ON administration.user_account (business_unit_id);

CREATE INDEX idx_user_account_tenant_id
    ON administration.user_account (tenant_id);

CREATE INDEX idx_user_role_assigned_by
    ON administration.user_role (assigned_by);

CREATE INDEX idx_audit_configuration_change_audit_event_id
    ON audit.audit_configuration_change (audit_event_id);

CREATE INDEX idx_audit_entity_change_audit_event_id
    ON audit.audit_entity_change (audit_event_id);

CREATE INDEX idx_audit_event_organization_id
    ON audit.audit_event (organization_id);

CREATE INDEX idx_audit_event_session_id
    ON audit.audit_event (session_id);

CREATE INDEX idx_audit_event_tenant_id
    ON audit.audit_event (tenant_id);

CREATE INDEX idx_audit_export_organization_id
    ON audit.audit_export (organization_id);

CREATE INDEX idx_audit_security_event_audit_event_id
    ON audit.audit_security_event (audit_event_id);

CREATE INDEX idx_audit_security_event_organization_id
    ON audit.audit_security_event (organization_id);

CREATE INDEX idx_case_organization_id
    ON case_management.case (organization_id);

CREATE INDEX idx_case_tenant_id
    ON case_management.case (tenant_id);

CREATE INDEX idx_case_assignment_assigned_from
    ON case_management.case_assignment (assigned_from);

CREATE INDEX idx_case_escalation_escalated_by
    ON case_management.case_escalation (escalated_by);

CREATE INDEX idx_case_evidence_uploaded_by
    ON case_management.case_evidence (uploaded_by);

CREATE INDEX idx_case_resolution_approved_by
    ON case_management.case_resolution (approved_by);

CREATE INDEX idx_case_resolution_resolved_by
    ON case_management.case_resolution (resolved_by);

CREATE INDEX idx_catalog_tenant_id
    ON catalog.catalog (tenant_id);

CREATE INDEX idx_integration_connector_endpoint_id
    ON integration.integration_connector (endpoint_id);

CREATE INDEX idx_integration_message_connector_id
    ON integration.integration_message (connector_id);

CREATE INDEX idx_rule_execution_rule_version_id
    ON rules.rule_execution (rule_version_id);

CREATE INDEX idx_rule_policy_tenant_id
    ON rules.rule_policy (tenant_id);

CREATE INDEX idx_rule_version_approved_by
    ON rules.rule_version (approved_by);

CREATE INDEX idx_rule_version_created_by
    ON rules.rule_version (created_by);

CREATE INDEX idx_transaction_attachment_uploaded_by
    ON transaction.transaction_attachment (uploaded_by);

CREATE INDEX idx_transaction_status_history_changed_by
    ON transaction.transaction_status_history (changed_by);