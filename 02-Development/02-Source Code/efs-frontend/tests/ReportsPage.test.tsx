import {
  cleanup,
  fireEvent,
  render,
  screen,
  waitFor,
} from '@testing-library/react'
import {
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from 'vitest'

import '../src/configuration/i18n'
import {
  useExportReportMutation,
  useGenerateReportMutation,
  useReportDefinitionsQuery,
  useReportExportOptionsQuery,
  useRetrieveReportMutation,
} from '../src/modules/reports/hooks/useReports'
import ReportsPage from '../src/modules/reports/pages/ReportsPage'

vi.mock(
  '../src/modules/reports/hooks/useReports',
  () => ({
    useReportDefinitionsQuery:
      vi.fn(),
    useGenerateReportMutation:
      vi.fn(),
    useRetrieveReportMutation:
      vi.fn(),
    useReportExportOptionsQuery:
      vi.fn(),
    useExportReportMutation:
      vi.fn(),
  }),
)

const definitionsQueryMock =
  vi.mocked(
    useReportDefinitionsQuery,
  )

const generateMutationMock =
  vi.mocked(
    useGenerateReportMutation,
  )

const retrieveMutationMock =
  vi.mocked(
    useRetrieveReportMutation,
  )

const exportOptionsQueryMock =
  vi.mocked(
    useReportExportOptionsQuery,
  )

const exportMutationMock =
  vi.mocked(
    useExportReportMutation,
  )

const generateAsync =
  vi.fn()

const retrieveAsync =
  vi.fn()

const exportAsync =
  vi.fn()

beforeEach(() => {
  definitionsQueryMock
    .mockReset()

  generateMutationMock
    .mockReset()

  retrieveMutationMock
    .mockReset()

  exportOptionsQueryMock
    .mockReset()

  exportMutationMock
    .mockReset()

  generateAsync.mockReset()
  retrieveAsync.mockReset()
  exportAsync.mockReset()

  definitionsQueryMock
    .mockReturnValue(
      {
        data: [
          {
            reportCode:
              'OPERATIONAL_SUMMARY',
            category:
              'OPERATIONAL',
            allowedCriteria: [
              'tenantId',
              'components',
            ],
          },
        ],
        isError: false,
        isFetching: false,
      } as unknown as ReturnType<
        typeof useReportDefinitionsQuery
      >,
    )

  generateMutationMock
    .mockReturnValue(
      {
        mutateAsync:
          generateAsync,
        isPending: false,
        isError: false,
      } as unknown as ReturnType<
        typeof useGenerateReportMutation
      >,
    )

  retrieveMutationMock
    .mockReturnValue(
      {
        mutateAsync:
          retrieveAsync,
        isPending: false,
        isError: false,
      } as unknown as ReturnType<
        typeof useRetrieveReportMutation
      >,
    )

  exportOptionsQueryMock
    .mockReturnValue(
      {
        data: undefined,
        isError: false,
        isFetching: false,
      } as unknown as ReturnType<
        typeof useReportExportOptionsQuery
      >,
    )

  exportMutationMock
    .mockReturnValue(
      {
        mutateAsync:
          exportAsync,
        isPending: false,
        isError: false,
      } as unknown as ReturnType<
        typeof useExportReportMutation
      >,
    )
})

afterEach(() => {
  cleanup()
})

describe('ReportsPage', () => {
  it(
    'presents the reports workspace and loads definitions',
    () => {
      render(
        <ReportsPage />,
      )

      expect(
        definitionsQueryMock,
      ).toHaveBeenCalledTimes(1)

      expect(
        screen.getByRole(
          'heading',
          {
            name: 'Reportes',
          },
        ),
      ).toBeTruthy()

      expect(
        screen.getByText(
          'Generación de reportes',
        ),
      ).toBeTruthy()

      expect(
        screen.getByText(
          'Consultar reporte generado',
        ),
      ).toBeTruthy()
    },
  )

  it(
    'retrieves a generated report by id',
    async () => {
      retrieveAsync
        .mockResolvedValue({
          reportId:
            '00000000-0000-0000-0000-000000000041',
          organizationId:
            '00000000-0000-0000-0000-000000000001',
          tenantId: null,
          reportCode:
            'INVESTIGATION_CASES',
          criteria: {},
          content: {
            recordCount: 1,
          },
          generatedBy:
            '00000000-0000-0000-0000-000000000002',
          generatedAt:
            '2026-09-19T12:00:00',
        })

      render(
        <ReportsPage />,
      )

      fireEvent.change(
        screen.getByLabelText(
          'ID de reporte',
        ),
        {
          target: {
            value:
              '00000000-0000-0000-0000-000000000041',
          },
        },
      )

      fireEvent.click(
        screen.getByRole(
          'button',
          {
            name: 'Cargar reporte',
          },
        ),
      )

      await waitFor(
        () => {
          expect(
            retrieveAsync,
          ).toHaveBeenCalledWith(
            '00000000-0000-0000-0000-000000000041',
          )
        },
      )
    },
  )
})
