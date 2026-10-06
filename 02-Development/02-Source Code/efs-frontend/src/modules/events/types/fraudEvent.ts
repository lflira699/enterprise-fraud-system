export type FraudEvent = {
  fraudEventId: string
  organizationId: string
  tenantId: string
  transactionId: string | null
  eventType: string
  sourceType: string
  sourceReference: string | null
  idempotencyKey: string
  correlationId: string
  normalizedPayload: Record<string, unknown> | null
  occurredAt: string
  receivedAt: string
  createdAt: string
}

export type FraudEventSortField =
  | 'occurredAt'

export type FraudEventSortDirection =
  | 'ASC'
  | 'DESC'

export type FraudEventSearchParams = {
  transactionId?: string
  eventType?: string
  sourceType?: string
  sourceReference?: string
  correlationId?: string
  occurredFrom?: string
  occurredTo?: string
  receivedFrom?: string
  receivedTo?: string
  page?: number
  size?: number
  sort?: FraudEventSortField
  direction?: FraudEventSortDirection
}
