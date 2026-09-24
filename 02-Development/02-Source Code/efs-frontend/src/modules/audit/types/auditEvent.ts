export type AuditSortField =
  'eventTimestamp'

export type AuditSortDirection =
  | 'ASC'
  | 'DESC'

export type AuditEvent = {
  auditEventId: string
  eventTimestamp: string
  organizationId: string | null
  tenantId: string | null
  userId: string | null
  sessionId: string | null
  eventType: string
  entityType: string
  entityId: string | null
  action: string
  sourceComponent: string
  ipAddress: string | null
  correlationId: string | null
  eventResult: string
  eventDetails: Record<string, unknown> | null
}

export type AuditEventSearchParams = {
  userId?: string
  from?: string
  to?: string
  entityType?: string
  entityId?: string
  action?: string
  page?: number
  size?: number
  sort?: AuditSortField
  direction?: AuditSortDirection
}