import { useQuery } from '@tanstack/react-query'

import { getAlerts } from '../api/alertsApi'
import type { AlertSearchParams } from '../types/alert'

export const alertsQueryKeys = {
  all: ['alerts'] as const,

  list(
    params: AlertSearchParams,
  ) {
    return [
      ...alertsQueryKeys.all,
      'list',
      params,
    ] as const
  },
}

export function useAlertsQuery(
  params: AlertSearchParams,
) {
  return useQuery({
    queryKey:
      alertsQueryKeys.list(
        params,
      ),

    queryFn:
      () => getAlerts(
        params,
      ),
  })
}