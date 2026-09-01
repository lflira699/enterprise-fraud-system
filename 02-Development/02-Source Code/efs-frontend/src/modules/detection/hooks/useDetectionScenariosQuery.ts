import { useQuery } from '@tanstack/react-query'

import { getDetectionScenarios } from '../api/detectionScenariosApi'
import type { DetectionScenarioSearchParams } from '../types/detectionScenario'

export const detectionScenariosQueryKeys = {
  all: ['detectionScenarios'] as const,

  list(
    params: DetectionScenarioSearchParams,
  ) {
    return [
      ...detectionScenariosQueryKeys.all,
      'list',
      params,
    ] as const
  },
}

export function useDetectionScenariosQuery(
  params: DetectionScenarioSearchParams,
) {
  return useQuery({
    queryKey:
      detectionScenariosQueryKeys.list(
        params,
      ),

    queryFn:
      () => getDetectionScenarios(
        params,
      ),
  })
}