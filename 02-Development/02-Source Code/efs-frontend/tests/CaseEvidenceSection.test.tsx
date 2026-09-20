import type { ReactNode } from 'react'
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
import CaseEvidenceSection from '../src/modules/evidence/components/CaseEvidenceSection'
import {
  useCaseEvidenceQuery,
  useCreateCaseEvidenceMutation,
  useDeleteCaseEvidenceMutation,
  useUpdateCaseEvidenceMutation,
} from '../src/modules/evidence/hooks/useEvidence'

type EvidenceRow = {
  evidenceId: string
}

vi.mock(
  '../src/modules/evidence/hooks/useEvidence',
  () => ({
    useCaseEvidenceQuery:
      vi.fn(),
    useCreateCaseEvidenceMutation:
      vi.fn(),
    useDeleteCaseEvidenceMutation:
      vi.fn(),
    useUpdateCaseEvidenceMutation:
      vi.fn(),
  }),
)

vi.mock(
  '@mui/x-data-grid',
  () => ({
    DataGrid: (
      props: {
        rows?: EvidenceRow[]
        columns?: Array<{
          field: string
          renderCell?: (
            params: {
              row: EvidenceRow
            },
          ) => ReactNode
        }>
      },
    ) => {
      const actions =
        props.columns?.find(
          (column) =>
            column.field
            === 'actions',
        )

      return (
        <div data-testid="evidence-grid">
          {props.rows?.map(
            (row) => (
              <div
                key={
                  row.evidenceId
                }
              >
                {
                  actions
                    ?.renderCell
                    ?.({
                      row,
                    })
                }
              </div>
            ),
          )}
        </div>
      )
    },
  }),
)

const useCaseEvidenceQueryMock =
  vi.mocked(
    useCaseEvidenceQuery,
  )

const useCreateMutationMock =
  vi.mocked(
    useCreateCaseEvidenceMutation,
  )

const useUpdateMutationMock =
  vi.mocked(
    useUpdateCaseEvidenceMutation,
  )

const useDeleteMutationMock =
  vi.mocked(
    useDeleteCaseEvidenceMutation,
  )

const caseId =
  '66666666-6666-6666-6666-666666666666'

const evidenceId =
  '77777777-7777-7777-7777-777777777777'

const refetchMock =
  vi.fn()

const createMutateMock =
  vi.fn()

const updateMutateMock =
  vi.fn()

const deleteMutateMock =
  vi.fn()

beforeEach(() => {
  useCaseEvidenceQueryMock
    .mockReset()

  useCreateMutationMock
    .mockReset()

  useUpdateMutationMock
    .mockReset()

  useDeleteMutationMock
    .mockReset()

  refetchMock.mockReset()
  createMutateMock.mockReset()
  updateMutateMock.mockReset()
  deleteMutateMock.mockReset()

  useCaseEvidenceQueryMock
    .mockReturnValue(
      {
        data: [
          {
            evidenceId,
            caseId,
            transactionId:
              null,
            evidenceType:
              'DEVICE_EVIDENCE',
            sourceSystem:
              'INTERNAL_CASE_TOOL',
            storageUri:
              null,
            checksumSha256:
              null,
            uploadedBy:
              null,
            uploadedAt:
              null,
            evidenceCategory:
              null,
            evidenceName:
              null,
            evidenceDescription:
              null,
            validationStatus:
              null,
            confidentialityLevel:
              null,
            updatedAt:
              null,
            updatedBy:
              null,
          },
        ],
        isError: false,
        isFetching: false,
        refetch:
          refetchMock,
      } as unknown as ReturnType<
        typeof useCaseEvidenceQuery
      >,
    )

  useCreateMutationMock
    .mockReturnValue(
      {
        isPending: false,
        isError: false,
        mutateAsync:
          createMutateMock,
      } as unknown as ReturnType<
        typeof useCreateCaseEvidenceMutation
      >,
    )

  useUpdateMutationMock
    .mockReturnValue(
      {
        isPending: false,
        isError: false,
        mutateAsync:
          updateMutateMock,
      } as unknown as ReturnType<
        typeof useUpdateCaseEvidenceMutation
      >,
    )

  useDeleteMutationMock
    .mockReturnValue(
      {
        isPending: false,
        isError: false,
        mutateAsync:
          deleteMutateMock,
      } as unknown as ReturnType<
        typeof useDeleteCaseEvidenceMutation
      >,
    )
})

afterEach(() => {
  cleanup()
  vi.restoreAllMocks()
})

describe('CaseEvidenceSection', () => {
  it(
    'loads Evidence directly from the selected Case context',
    () => {
      render(
        <CaseEvidenceSection
          caseId={caseId}
        />,
      )

      expect(
        useCaseEvidenceQueryMock,
      ).toHaveBeenCalledWith(
        caseId,
      )

      expect(
        screen.queryByLabelText(
          'ID de caso',
        ),
      ).toBeNull()

      expect(
        screen.getByTestId(
          'evidence-grid',
        ),
      ).toBeTruthy()
    },
  )

  it(
    'allows Evidence Registration to be cancelled before submission',
    () => {
      render(
        <CaseEvidenceSection
          caseId={caseId}
        />,
      )

      expect(
        screen.queryByLabelText(
          'Tipo de evidencia',
        ),
      ).toBeNull()

      fireEvent.click(
        screen.getByRole(
          'button',
          {
            name:
              'Registrar evidencia',
          },
        ),
      )

      expect(
        screen.getByLabelText(
          /Tipo de evidencia/,
        ),
      ).toBeTruthy()

      fireEvent.click(
        screen.getByRole(
          'button',
          {
            name:
              'Cancelar',
          },
        ),
      )

      expect(
        screen.queryByLabelText(
          'Tipo de evidencia',
        ),
      ).toBeNull()

      expect(
        createMutateMock,
      ).not.toHaveBeenCalled()
    },
  )

  it(
    'requires confirmation before Evidence Removal',
    async () => {
      deleteMutateMock
        .mockResolvedValue(
          undefined,
        )

      refetchMock
        .mockResolvedValue(
          undefined,
        )

      render(
        <CaseEvidenceSection
          caseId={caseId}
        />,
      )

      fireEvent.click(
        screen.getByRole(
          'button',
          {
            name:
              'Eliminar',
          },
        ),
      )

      expect(
        screen.getByRole(
          'dialog',
        ),
      ).toBeTruthy()

      expect(
        deleteMutateMock,
      ).not.toHaveBeenCalled()

      fireEvent.click(
        screen.getByRole(
          'button',
          {
            name:
              'Cancelar',
          },
        ),
      )

      expect(
        deleteMutateMock,
      ).not.toHaveBeenCalled()

      await waitFor(
        () => {
          expect(
            screen.queryByRole(
              'dialog',
            ),
          ).toBeNull()
        },
      )

      fireEvent.click(
        screen.getByRole(
          'button',
          {
            name:
              'Eliminar',
          },
        ),
      )

      fireEvent.click(
        screen.getByRole(
          'button',
          {
            name:
              'Confirmar eliminación',
          },
        ),
      )

      await waitFor(
        () => {
          expect(
            deleteMutateMock,
          ).toHaveBeenCalledWith({
            caseId,
            evidenceId,
          })
        },
      )
    },
  )
})
