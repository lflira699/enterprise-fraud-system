import {
  useMutation,
  useQuery,
} from '@tanstack/react-query'

import {
  exportReport,
  generateReport,
  getReport,
  getReportDefinitions,
  getReportExportOptions,
} from '../api/reportsApi'
import type {
  ReportExportRequest,
  ReportGenerationRequest,
} from '../types/report'

export const reportsQueryKeys = {
  all: ['reports'] as const,

  definitions: [
    'reports',
    'definitions',
  ] as const,

  exportOptions(
    reportId: string,
  ) {
    return [
      ...reportsQueryKeys.all,
      reportId,
      'export-options',
    ] as const
  },
}

export function useReportDefinitionsQuery() {
  return useQuery({
    queryKey:
      reportsQueryKeys.definitions,

    queryFn:
      getReportDefinitions,
  })
}

export function useGenerateReportMutation() {
  return useMutation({
    mutationFn:
      (
        request:
          ReportGenerationRequest,
      ) =>
        generateReport(request),
  })
}

export function useRetrieveReportMutation() {
  return useMutation({
    mutationFn:
      (reportId: string) =>
        getReport(reportId),
  })
}

export function useReportExportOptionsQuery(
  reportId: string | null,
) {
  return useQuery({
    queryKey:
      reportsQueryKeys.exportOptions(
        reportId ?? '',
      ),

    queryFn:
      () =>
        getReportExportOptions(
          reportId as string,
        ),

    enabled:
      reportId !== null,
  })
}

export function useExportReportMutation() {
  return useMutation({
    mutationFn:
      (
        request:
          ReportExportRequest,
      ) =>
        exportReport(
          request.reportId,
          request.format,
        ),
  })
}
