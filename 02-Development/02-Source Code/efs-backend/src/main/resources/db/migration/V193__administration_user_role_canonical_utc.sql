-- EFS V193 RECONCILED CANDIDATE
-- Historical conversion requires independently recorded
-- assignment and timezone-normalization evidence.
-- This file is not yet approved for production execution.

-- Preserve the historical evidence set during validation
-- and conversion. Execution requires a single transaction.
LOCK TABLE administration.user_role IN ACCESS EXCLUSIVE MODE;
LOCK TABLE administration.role IN SHARE MODE;
LOCK TABLE audit.audit_event IN SHARE MODE;
DO $verification$
DECLARE
    total_assignments bigint;
    verified_assignments bigint;
BEGIN
    SELECT COUNT(*)
    INTO total_assignments
    FROM administration.user_role;

    SELECT COUNT(*)
    INTO verified_assignments
    FROM administration.user_role ur
    JOIN administration.role r
      ON r.role_id = ur.role_id
    WHERE ur.effective_to IS NULL

      -- Current local value must represent the original
      -- UTC assignment instant documented by audit.
      AND (
          ur.effective_from
          AT TIME ZONE 'America/Guatemala'
      ) = (
          ur.assigned_at
          AT TIME ZONE 'UTC'
      )

      -- Require exactly one original successful assignment.
      AND (
          SELECT COUNT(*)
          FROM audit.audit_event original
          WHERE original.entity_id = ur.user_role_id
            AND original.entity_type = 'USER_ROLE'
            AND original.event_type = 'USER_ROLE_ASSIGNMENT'
            AND original.action = 'ASSIGN'
            AND original.source_component = 'ADMINISTRATION'
            AND original.event_result = 'SUCCESS'
            AND original.event_timestamp = ur.assigned_at
      ) = 1

      -- Require exactly one matching timezone correction.
      AND (
          SELECT COUNT(*)
          FROM audit.audit_event correction
          WHERE correction.entity_id = ur.user_role_id
            AND correction.entity_type = 'USER_ROLE'
            AND correction.event_type = 'USER_ROLE_ASSIGNMENT'
            AND correction.action = 'ASSIGN'
            AND correction.source_component = 'ADMINISTRATION'
            AND correction.event_result = 'SUCCESS'
            AND correction.event_details ->> 'operation'
                = 'EFFECTIVE_FROM_TIMEZONE_NORMALIZATION'
            AND correction.event_details
                    ->> 'correctionOfExistingAssignment'
                = 'true'
            AND correction.event_details ->> 'newGrant'
                = 'false'
            AND correction.event_details ->> 'sourceTimezone'
                = 'UTC'
            AND correction.event_details ->> 'targetTimezone'
                = 'America/Guatemala'
            AND correction.event_details ->> 'oldEffectiveFrom'
                = ur.assigned_at::text
            AND correction.event_details ->> 'newEffectiveFrom'
                = ur.effective_from::text
            AND correction.event_details ->> 'roleCode'
                = r.role_code
      ) = 1;

    IF total_assignments <> verified_assignments THEN
        RAISE EXCEPTION
            'EFS_V193_LEGACY_PROVENANCE_UNRESOLVED: total %, verified %',
            total_assignments,
            verified_assignments;
    END IF;
END;
$verification$;

-- The historical correction proves that effective_from
-- and assigned_at represent the same canonical instant.
-- Restore the original UTC wall-clock representation
-- before changing the PostgreSQL column type.

UPDATE administration.user_role
SET effective_from = assigned_at;

-- Avoid implicit default-expression conversion.

ALTER TABLE administration.user_role
    ALTER COLUMN effective_from DROP DEFAULT,
    ALTER COLUMN assigned_at DROP DEFAULT;

ALTER TABLE administration.user_role
    ALTER COLUMN effective_from
        TYPE TIMESTAMP WITH TIME ZONE
        USING (effective_from AT TIME ZONE 'UTC'),
    ALTER COLUMN effective_to
        TYPE TIMESTAMP WITH TIME ZONE
        USING (effective_to AT TIME ZONE 'UTC'),
    ALTER COLUMN assigned_at
        TYPE TIMESTAMP WITH TIME ZONE
        USING (assigned_at AT TIME ZONE 'UTC');

ALTER TABLE administration.user_role
    ALTER COLUMN effective_from SET DEFAULT CURRENT_TIMESTAMP,
    ALTER COLUMN assigned_at SET DEFAULT CURRENT_TIMESTAMP;