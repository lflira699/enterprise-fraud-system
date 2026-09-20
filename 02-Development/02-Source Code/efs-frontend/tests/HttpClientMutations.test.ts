import {
  afterEach,
  describe,
  expect,
  it,
  vi,
} from 'vitest'

import { httpClient } from '../src/services/httpClient'

afterEach(() => {
  vi.unstubAllGlobals()
  vi.restoreAllMocks()
})

describe('httpClient mutation methods', () => {
  it(
    'posts JSON through the canonical API base path',
    async () => {
      const fetchMock =
        vi.fn()
          .mockResolvedValue(
            new Response(
              JSON.stringify({
                reportId: 'report-1',
              }),
              {
                status: 201,
                headers: {
                  'Content-Type':
                    'application/json',
                },
              },
            ),
          )

      vi.stubGlobal(
        'fetch',
        fetchMock,
      )

      const payload = {
        reportCode:
          'OPERATIONAL_SUMMARY',
      }

      await httpClient.post(
        '/reports',
        payload,
      )

      expect(
        fetchMock,
      ).toHaveBeenCalledTimes(1)

      const [
        url,
        init,
      ] =
        fetchMock.mock.calls[0] as [
          string,
          RequestInit,
        ]

      expect(url)
        .toBe(
          '/api/v1/reports',
        )

      expect(init.method)
        .toBe('POST')

      expect(init.body)
        .toBe(
          JSON.stringify(payload),
        )

      const headers =
        new Headers(
          init.headers,
        )

      expect(
        headers.get(
          'Content-Type',
        ),
      ).toBe(
        'application/json',
      )

      expect(
        headers.get(
          'X-Correlation-ID',
        ),
      ).toBeTruthy()
    },
  )

  it(
    'returns binary response metadata for report export',
    async () => {
      const fetchMock =
        vi.fn()
          .mockResolvedValue(
            new Response(
              'report',
              {
                status: 200,
                headers: {
                  'Content-Type':
                    'text/csv; charset=UTF-8',
                  'Content-Disposition':
                    'attachment; filename="efs-report.csv"',
                },
              },
            ),
          )

      vi.stubGlobal(
        'fetch',
        fetchMock,
      )

      const result =
        await httpClient.postBlob(
          '/reports/report-1/exports',
          {
            format: 'CSV',
          },
        )

      expect(result.fileName)
        .toBe(
          'efs-report.csv',
        )

      expect(result.mediaType)
        .toContain(
          'text/csv',
        )

      expect(
        await result.blob.text(),
      ).toBe(
        'report',
      )
    },
  )
  it(
    'patches JSON through the canonical API base path',
    async () => {
      const fetchMock =
        vi.fn()
          .mockResolvedValue(
            new Response(
              JSON.stringify({
                evidenceId:
                  'evidence-1',
              }),
              {
                status: 200,
                headers: {
                  'Content-Type':
                    'application/json',
                },
              },
            ),
          )

      vi.stubGlobal(
        'fetch',
        fetchMock,
      )

      const payload = {
        evidenceType:
          'DOCUMENT',
      }

      await httpClient.patch(
        '/cases/case-1/evidence/evidence-1',
        payload,
      )

      const [
        url,
        init,
      ] =
        fetchMock.mock.calls[0] as [
          string,
          RequestInit,
        ]

      expect(url)
        .toBe(
          '/api/v1/cases/case-1/evidence/evidence-1',
        )

      expect(init.method)
        .toBe('PATCH')

      expect(init.body)
        .toBe(
          JSON.stringify(payload),
        )
    },
  )

  it(
    'deletes through the canonical API base path without a request body',
    async () => {
      const fetchMock =
        vi.fn()
          .mockResolvedValue(
            new Response(
              null,
              {
                status: 204,
              },
            ),
          )

      vi.stubGlobal(
        'fetch',
        fetchMock,
      )

      await httpClient.delete(
        '/cases/case-1/evidence/evidence-1',
      )

      const [
        url,
        init,
      ] =
        fetchMock.mock.calls[0] as [
          string,
          RequestInit,
        ]

      expect(url)
        .toBe(
          '/api/v1/cases/case-1/evidence/evidence-1',
        )

      expect(init.method)
        .toBe('DELETE')

      expect(init.body)
        .toBeUndefined()
    },
  )
})
