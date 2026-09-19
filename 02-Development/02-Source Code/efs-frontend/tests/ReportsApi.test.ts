import {
  afterEach,
  describe,
  expect,
  it,
  vi,
} from 'vitest'

import {
  exportReport,
  generateReport,
  getReport,
  getReportDefinitions,
  getReportExportOptions,
} from '../src/modules/reports/api/reportsApi'
import { httpClient } from '../src/services/httpClient'

afterEach(() => {
  vi.restoreAllMocks()
})

describe('reportsApi', () => {
  it(
    'loads report definitions from the canonical endpoint',
    async () => {
      const getMock =
        vi.spyOn(
          httpClient,
          'get',
        )
          .mockResolvedValue([])

      await getReportDefinitions()

      expect(
        getMock,
      ).toHaveBeenCalledWith(
        '/reports/definitions',
      )
    },
  )

  it(
    'generates a report using the canonical request',
    async () => {
      const request = {
        reportCode:
          'OPERATIONAL_SUMMARY',
        criteria: {
          components: [
            'ALERT',
          ],
        },
      }

      const postMock =
        vi.spyOn(
          httpClient,
          'post',
        )
          .mockResolvedValue(
            {} as never,
          )

      await generateReport(request)

      expect(
        postMock,
      ).toHaveBeenCalledWith(
        '/reports',
        request,
      )
    },
  )

  it(
    'retrieves a generated report by id',
    async () => {
      const reportId =
        '00000000-0000-0000-0000-000000000041'

      const getMock =
        vi.spyOn(
          httpClient,
          'get',
        )
          .mockResolvedValue(
            {} as never,
          )

      await getReport(reportId)

      expect(
        getMock,
      ).toHaveBeenCalledWith(
        `/reports/${reportId}`,
      )
    },
  )

  it(
    'loads export options for a generated report',
    async () => {
      const reportId =
        '00000000-0000-0000-0000-000000000042'

      const getMock =
        vi.spyOn(
          httpClient,
          'get',
        )
          .mockResolvedValue(
            {} as never,
          )

      await getReportExportOptions(
        reportId,
      )

      expect(
        getMock,
      ).toHaveBeenCalledWith(
        `/reports/${reportId}/export-options`,
      )
    },
  )

  it(
    'exports the report using the selected backend format',
    async () => {
      const reportId =
        '00000000-0000-0000-0000-000000000043'

      const postBlobMock =
        vi.spyOn(
          httpClient,
          'postBlob',
        )
          .mockResolvedValue({
            blob:
              new Blob(
                ['report'],
              ),
            mediaType:
              'text/csv; charset=UTF-8',
            fileName:
              'report.csv',
          })

      await exportReport(
        reportId,
        'CSV',
      )

      expect(
        postBlobMock,
      ).toHaveBeenCalledWith(
        `/reports/${reportId}/exports`,
        {
          format: 'CSV',
        },
      )
    },
  )
})
