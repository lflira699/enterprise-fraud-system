import {
  httpClient,
  type QueryParameters,
} from '../../../services/httpClient'
import type { PageResponse } from '../../../types/pageResponse'
import type {
  FraudEvent,
  FraudEventSearchParams,
} from '../types/fraudEvent'

const EVENTS_PATH =
  '/events'

export function getEvents(
  params: FraudEventSearchParams,
): Promise<PageResponse<FraudEvent>> {
  const query: QueryParameters = {
    transactionId: params.transactionId,
    eventType: params.eventType,
    sourceType: params.sourceType,
    sourceReference: params.sourceReference,
    correlationId: params.correlationId,
    occurredFrom: params.occurredFrom,
    occurredTo: params.occurredTo,
    receivedFrom: params.receivedFrom,
    receivedTo: params.receivedTo,
    page: params.page ?? 0,
    size: params.size ?? 25,
    sort: params.sort ?? 'occurredAt',
    direction: params.direction ?? 'DESC',
  }

  return httpClient.get<PageResponse<FraudEvent>>(
    EVENTS_PATH,
    query,
  )
}
