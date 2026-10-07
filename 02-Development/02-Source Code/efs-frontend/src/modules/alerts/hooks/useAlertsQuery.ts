import { useQuery } from '@tanstack/react-query'

import {
  getAlertById,
  getAlerts,
} from '../api/alertsApi'
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
  detail(
    alertId: string,
  ) {
    return [
      ...alertsQueryKeys.all,
      'detail',
      alertId,
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
export function useAlertQuery(
  alertId: string | null,
) {
  return useQuery({
    queryKey:
      alertsQueryKeys.detail(
        alertId ?? '',
      ),

    queryFn:
      () => getAlertById(
        alertId ?? '',
      ),

    enabled:
      alertId !== null
      && alertId !== '',
  })
}
