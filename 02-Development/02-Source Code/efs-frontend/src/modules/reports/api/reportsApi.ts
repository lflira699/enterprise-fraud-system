import {
  httpClient,
  type BinaryHttpResponse,
} from '../../../services/httpClient'
import type {
  GeneratedReport,
  ReportDefinition,
  ReportExportOptions,
  ReportGenerationRequest,
} from '../types/report'

const REPORTS_PATH =
  '/reports'

export function getReportDefinitions():
Promise<ReportDefinition[]> {
  return httpClient.get<
    ReportDefinition[]
  >(
    `${REPORTS_PATH}/definitions`,
  )
}

export function generateReport(
  request: ReportGenerationRequest,
): Promise<GeneratedReport> {
  return httpClient.post<GeneratedReport>(
    REPORTS_PATH,
    request,
  )
}

export function getReport(
  reportId: string,
): Promise<GeneratedReport> {
  return httpClient.get<GeneratedReport>(
    `${REPORTS_PATH}/${reportId}`,
  )
}

export function getReportExportOptions(
  reportId: string,
): Promise<ReportExportOptions> {
  return httpClient.get<
    ReportExportOptions
  >(
    `${REPORTS_PATH}/${reportId}/export-options`,
  )
}

export function exportReport(
  reportId: string,
  format: string,
): Promise<BinaryHttpResponse> {
  return httpClient.postBlob(
    `${REPORTS_PATH}/${reportId}/exports`,
    {
      format,
    },
  )
}
