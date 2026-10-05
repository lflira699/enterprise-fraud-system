CREATE SCHEMA IF NOT EXISTS risk;

CREATE TABLE risk.event_risk_assessment (
    event_risk_assessment_id UUID NOT NULL DEFAULT uuidv7(),
    fraud_event_id UUID NOT NULL,
    organization_id UUID NOT NULL,
    tenant_id UUID NOT NULL,
    correlation_id UUID NOT NULL,

    model_id VARCHAR(100) NOT NULL,
    model_version VARCHAR(40) NOT NULL,

    overall_risk_score NUMERIC(8,2),
    risk_level VARCHAR(20),
    risk_category VARCHAR(40),
    assessment_result VARCHAR(40) NOT NULL,

    assessment_details JSONB,

    assessment_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processing_time_ms BIGINT,

    CONSTRAINT pk_event_risk_assessment
        PRIMARY KEY (event_risk_assessment_id),

    CONSTRAINT fk_event_risk_assessment_fraud_event
        FOREIGN KEY (fraud_event_id)
        REFERENCES event_management.fraud_event (fraud_event_id),

    CONSTRAINT fk_event_risk_assessment_organization
        FOREIGN KEY (organization_id)
        REFERENCES administration.organization (organization_id),

    CONSTRAINT fk_event_risk_assessment_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES administration.tenant (tenant_id),

    CONSTRAINT ck_event_risk_assessment_score
        CHECK (
            overall_risk_score IS NULL
            OR overall_risk_score >= 0
        )
);

CREATE INDEX idx_event_risk_assessment_fraud_event
    ON risk.event_risk_assessment (fraud_event_id);

CREATE INDEX idx_event_risk_assessment_organization_tenant
    ON risk.event_risk_assessment (
        organization_id,
        tenant_id
    );

CREATE INDEX idx_event_risk_assessment_correlation
    ON risk.event_risk_assessment (correlation_id);

CREATE INDEX idx_event_risk_assessment_model
    ON risk.event_risk_assessment (
        model_id,
        model_version
    );

CREATE INDEX idx_event_risk_assessment_timestamp
    ON risk.event_risk_assessment (assessment_timestamp);

GRANT USAGE ON SCHEMA risk TO efs_app;

GRANT SELECT, INSERT, UPDATE
    ON TABLE risk.event_risk_assessment
    TO efs_app;
