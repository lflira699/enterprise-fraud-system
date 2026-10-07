import { httpClient } from '../../../services/httpClient'
import type {
  PlaybookExecution,
  PlaybookExecutionStep,
} from '../types/playbook'

const PLAYBOOK_EXECUTIONS_PATH =
  '/playbook-executions'

const PLAYBOOK_EXECUTION_STEPS_PATH =
  '/playbook-execution-steps'

export function getPlaybookExecutionsByAlertId(
  alertId: string,
): Promise<PlaybookExecution[]> {
  return httpClient.get<PlaybookExecution[]>(
    `${PLAYBOOK_EXECUTIONS_PATH}/alert/${alertId}`,
  )
}

export function getPlaybookExecutionSteps(
  playbookExecutionId: string,
): Promise<PlaybookExecutionStep[]> {
  return httpClient.get<PlaybookExecutionStep[]>(
    `${PLAYBOOK_EXECUTION_STEPS_PATH}/execution/${playbookExecutionId}`,
  )
}
