import {
  httpClient,
  type QueryParameters,
} from '../../../services/httpClient'
import type { PageResponse } from '../../../types/pageResponse'
import type {
  AuditEvent,
  AuditEventSearchParams,
} from '../types/auditEvent'

const AUDIT_EVENTS_PATH =
  '/audit/events'

export function getAuditEvents(
  params: AuditEventSearchParams,
): Promise<PageResponse<AuditEvent>> {
  const query: QueryParameters = {
    userId: params.userId,
    from: params.from,
    to: params.to,
    entityType: params.entityType,
    entityId: params.entityId,
    action: params.action,
    page: params.page ?? 0,
    size: params.size ?? 25,
    sort: params.sort ?? 'eventTimestamp',
    direction: params.direction ?? 'DESC',
  }

  return httpClient.get<PageResponse<AuditEvent>>(
    AUDIT_EVENTS_PATH,
    query,
  )
}