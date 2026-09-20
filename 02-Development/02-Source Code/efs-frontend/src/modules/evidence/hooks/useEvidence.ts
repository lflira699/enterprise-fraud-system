import {
  useMutation,
  useQuery,
} from '@tanstack/react-query'

import {
  createCaseEvidence,
  deleteCaseEvidence,
  getCaseEvidence,
  getCaseEvidenceById,
  updateCaseEvidence,
} from '../api/evidenceApi'
import type {
  CreateCaseEvidenceMutation,
  DeleteCaseEvidenceMutation,
  UpdateCaseEvidenceMutation,
} from '../types/evidence'

export const evidenceQueryKeys = {
  all: ['evidence'] as const,

  list(
    caseId: string,
  ) {
    return [
      ...evidenceQueryKeys.all,
      'case',
      caseId,
      'list',
    ] as const
  },

  detail(
    caseId: string,
    evidenceId: string,
  ) {
    return [
      ...evidenceQueryKeys.all,
      'case',
      caseId,
      evidenceId,
    ] as const
  },
}

export function useCaseEvidenceQuery(
  caseId: string | null,
) {
  return useQuery({
    queryKey:
      evidenceQueryKeys.list(
        caseId ?? '',
      ),

    queryFn:
      () => getCaseEvidence(
        caseId as string,
      ),

    enabled:
      caseId !== null
      && caseId !== '',
  })
}

export function useCaseEvidenceByIdQuery(
  caseId: string | null,
  evidenceId: string | null,
) {
  return useQuery({
    queryKey:
      evidenceQueryKeys.detail(
        caseId ?? '',
        evidenceId ?? '',
      ),

    queryFn:
      () => getCaseEvidenceById(
        caseId as string,
        evidenceId as string,
      ),

    enabled:
      caseId !== null
      && caseId !== ''
      && evidenceId !== null
      && evidenceId !== '',
  })
}

export function useCreateCaseEvidenceMutation() {
  return useMutation({
    mutationFn:
      ({
        caseId,
        request,
      }: CreateCaseEvidenceMutation) =>
        createCaseEvidence(
          caseId,
          request,
        ),
  })
}

export function useUpdateCaseEvidenceMutation() {
  return useMutation({
    mutationFn:
      ({
        caseId,
        evidenceId,
        request,
      }: UpdateCaseEvidenceMutation) =>
        updateCaseEvidence(
          caseId,
          evidenceId,
          request,
        ),
  })
}

export function useDeleteCaseEvidenceMutation() {
  return useMutation({
    mutationFn:
      ({
        caseId,
        evidenceId,
      }: DeleteCaseEvidenceMutation) =>
        deleteCaseEvidence(
          caseId,
          evidenceId,
        ),
  })
}
