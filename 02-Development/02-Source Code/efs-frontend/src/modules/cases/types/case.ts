export type Case = {
  caseId: string
  caseNumber: string
  organizationId: string | null
  transactionId: string | null
  customerId: string | null
  caseType: string
  category: string | null
  severity: string | null
  priority: string
  currentStatus: string
  assignedTeam: string | null
  assignedUser: string | null
  createdAt: string | null
  updatedAt: string | null
  dueDate: string | null
  closedAt: string | null
  tenantId: string | null
}

export type CaseSortField =
  | 'createdAt'

export type CaseSortDirection =
  | 'ASC'
  | 'DESC'

export type CaseSearchParams = {
  status?: string
  priority?: string
  assignedUser?: string
  assignedTeam?: string
  page?: number
  size?: number
  sort?: CaseSortField
  direction?: CaseSortDirection
}