-- EFS Scenario Evaluation Security Contract v1.1

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'scenario.evaluation.view',
    'View Scenario Evaluations',
    'scenario_evaluation',
    'view'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'scenario.evaluation.view'
);

INSERT INTO administration.permission (
    permission_code,
    permission_name,
    resource,
    action
)
SELECT
    'scenario.evaluation.create',
    'Create Scenario Evaluations',
    'scenario_evaluation',
    'create'
WHERE NOT EXISTS (
    SELECT 1
    FROM administration.permission
    WHERE permission_code = 'scenario.evaluation.create'
);