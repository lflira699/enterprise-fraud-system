-- EFS Generated Report Aggregate v1
-- UC-041 - Report Generation
-- COMP-014 - Reporting

CREATE TABLE reporting.generated_report (
    report_id UUID NOT NULL DEFAULT uuidv7(),
    organization_id UUID NOT NULL,
    tenant_id UUID,
    report_code VARCHAR(80) NOT NULL,
    criteria JSONB NOT NULL,
    content JSONB NOT NULL,
    generated_by UUID NOT NULL,
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_generated_report
        PRIMARY KEY (report_id),

    CONSTRAINT fk_generated_report_generated_by
        FOREIGN KEY (generated_by)
        REFERENCES administration.user_account (user_id),

    CONSTRAINT ck_generated_report_code
        CHECK (
            report_code IN (
                'OPERATIONAL_SUMMARY',
                'INVESTIGATION_CASES'
            )
        )
);

CREATE INDEX idx_generated_report_scope
    ON reporting.generated_report (
        organization_id,
        tenant_id,
        generated_at DESC
    );

CREATE INDEX idx_generated_report_code
    ON reporting.generated_report (
        report_code
    );

CREATE INDEX idx_generated_report_generated_by
    ON reporting.generated_report (
        generated_by
    );
