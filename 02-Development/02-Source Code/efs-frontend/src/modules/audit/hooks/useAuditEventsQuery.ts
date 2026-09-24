import { useQuery } from '@tanstack/react-query'

import { getAuditEvents } from '../api/auditEventsApi'
import type { AuditEventSearchParams } from '../types/auditEvent'

export const auditEventQueryKeys = {
  all: ['audit-events'] as const,

  list(
    params: AuditEventSearchParams,
  ) {
    return [
      ...auditEventQueryKeys.all,
      'list',
      params,
    ] as const
  },
}

export function useAuditEventsQuery(
  params: AuditEventSearchParams,
) {
  return useQuery({
    queryKey:
      auditEventQueryKeys.list(
        params,
      ),

    queryFn:
      () => getAuditEvents(
        params,
      ),
  })
}