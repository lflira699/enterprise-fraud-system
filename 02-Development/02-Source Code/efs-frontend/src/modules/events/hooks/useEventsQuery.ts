import { useQuery } from '@tanstack/react-query'

import { getEvents } from '../api/eventsApi'
import type { FraudEventSearchParams } from '../types/fraudEvent'

export const eventsQueryKeys = {
  all: ['events'] as const,

  list(
    params: FraudEventSearchParams,
  ) {
    return [
      ...eventsQueryKeys.all,
      'list',
      params,
    ] as const
  },
}

export function useEventsQuery(
  params: FraudEventSearchParams,
) {
  return useQuery({
    queryKey:
      eventsQueryKeys.list(
        params,
      ),

    queryFn:
      () => getEvents(
        params,
      ),
  })
}
