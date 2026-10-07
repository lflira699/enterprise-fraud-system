import { useQuery } from '@tanstack/react-query'

import {
  getPlaybookExecutionsByAlertId,
  getPlaybookExecutionSteps,
} from '../api/playbookApi'

export const playbookQueryKeys = {
  all: ['playbook'] as const,

  executionsByAlert(
    alertId: string,
  ) {
    return [
      ...playbookQueryKeys.all,
      'executions',
      'alert',
      alertId,
    ] as const
  },

  executionSteps(
    playbookExecutionId: string,
  ) {
    return [
      ...playbookQueryKeys.all,
      'execution-steps',
      playbookExecutionId,
    ] as const
  },
}

export function usePlaybookExecutionsByAlertQuery(
  alertId: string | null,
) {
  return useQuery({
    queryKey:
      playbookQueryKeys.executionsByAlert(
        alertId ?? '',
      ),

    queryFn:
      () => getPlaybookExecutionsByAlertId(
        alertId ?? '',
      ),

    enabled:
      alertId !== null
      && alertId !== '',
  })
}

export function usePlaybookExecutionStepsQuery(
  playbookExecutionId: string | null,
) {
  return useQuery({
    queryKey:
      playbookQueryKeys.executionSteps(
        playbookExecutionId ?? '',
      ),

    queryFn:
      () => getPlaybookExecutionSteps(
        playbookExecutionId ?? '',
      ),

    enabled:
      playbookExecutionId !== null
      && playbookExecutionId !== '',
  })
}
