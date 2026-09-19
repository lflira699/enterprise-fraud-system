export type ReportDefinition = {
  reportCode: string
  category: string
  allowedCriteria: string[]
}

export type ReportCriteria = {
  tenantId?: string
  components?: string[]
  status?: string
  priority?: string
  assignedUser?: string
  assignedTeam?: string
}

export type ReportGenerationRequest = {
  reportCode: string
  criteria?: ReportCriteria
}

export type GeneratedReport = {
  reportId: string
  organizationId: string
  tenantId: string | null
  reportCode: string
  criteria: Record<string, unknown>
  content: Record<string, unknown>
  generatedBy: string
  generatedAt: string
}

export type ReportExportOptions = {
  reportId: string
  reportCode: string
  formats: string[]
}

export type ReportExportRequest = {
  reportId: string
  format: string
}
