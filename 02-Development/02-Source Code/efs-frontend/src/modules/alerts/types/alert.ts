export type Alert = {
  alertId: string
  alertReference: string | null
  customerId: string | null
  transactionId: string | null
  decisionId: string
  riskAssessmentId: string | null
  scenarioId: string | null
  ruleId: string | null
  alertType: string
  category: string | null
  severity: string | null
  priority: string
  priorityScore: number | null
  status: string
  title: string | null
  description: string | null
  riskScore: number | null
  correlationId: string | null
  assignedTo: string | null
  assignedTeam: string | null
  dueAt: string | null
  generatedAt: string | null
  closedAt: string | null
  closureReason: string | null
  createdAt: string | null
  updatedAt: string | null
}