import {
  useMemo,
  useState,
} from 'react'
import {
  Alert as MuiAlert,
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Paper,
  TextField,
  Typography,
} from '@mui/material'
import {
  DataGrid,
  type GridColDef,
} from '@mui/x-data-grid'
import { useTranslation } from 'react-i18next'

import {
  useCaseEvidenceQuery,
  useCreateCaseEvidenceMutation,
  useDeleteCaseEvidenceMutation,
  useUpdateCaseEvidenceMutation,
} from '../hooks/useEvidence'
import type {
  CaseEvidence,
  CaseEvidenceCreateRequest,
  CaseEvidenceUpdateRequest,
} from '../types/evidence'

type CaseEvidenceSectionProps = {
  caseId: string
}

type EvidenceCreateDraft = {
  transactionId: string
  evidenceType: string
  sourceSystem: string
  storageUri: string
  checksumSha256: string
}

type EvidenceUpdateDraft = {
  evidenceType: string
  evidenceCategory: string
  evidenceName: string
  evidenceDescription: string
  validationStatus: string
  confidentialityLevel: string
}

const EMPTY_CREATE_DRAFT: EvidenceCreateDraft = {
  transactionId: '',
  evidenceType: '',
  sourceSystem: '',
  storageUri: '',
  checksumSha256: '',
}

const EMPTY_UPDATE_DRAFT: EvidenceUpdateDraft = {
  evidenceType: '',
  evidenceCategory: '',
  evidenceName: '',
  evidenceDescription: '',
  validationStatus: '',
  confidentialityLevel: '',
}

function normalizeOptional(
  value: string,
) {
  const normalized =
    value.trim()

  return normalized || undefined
}

function formatDateTime(
  value: string | null,
) {
  if (!value) {
    return '—'
  }

  const date =
    new Date(value)

  if (Number.isNaN(date.getTime())) {
    return value
  }

  return new Intl.DateTimeFormat(
    'es-GT',
    {
      dateStyle: 'short',
      timeStyle: 'short',
    },
  ).format(date)
}

function CaseEvidenceSection({
  caseId,
}: CaseEvidenceSectionProps) {
  const { t } =
    useTranslation()

  const [
    isCreateOpen,
    setIsCreateOpen,
  ] = useState(false)

  const [
    createDraft,
    setCreateDraft,
  ] = useState<EvidenceCreateDraft>(
    EMPTY_CREATE_DRAFT,
  )

  const [
    editingEvidenceId,
    setEditingEvidenceId,
  ] = useState<string | null>(
    null,
  )

  const [
    updateDraft,
    setUpdateDraft,
  ] = useState<EvidenceUpdateDraft>(
    EMPTY_UPDATE_DRAFT,
  )

  const [
    pendingDeleteEvidenceId,
    setPendingDeleteEvidenceId,
  ] = useState<string | null>(
    null,
  )

  const evidenceQuery =
    useCaseEvidenceQuery(
      caseId,
    )

  const createMutation =
    useCreateCaseEvidenceMutation()

  const updateMutation =
    useUpdateCaseEvidenceMutation()

  const deleteMutation =
    useDeleteCaseEvidenceMutation()

  function updateCreateDraft(
    field: keyof EvidenceCreateDraft,
    value: string,
  ) {
    setCreateDraft(
      (current) => ({
        ...current,
        [field]: value,
      }),
    )
  }

  function updateEditDraft(
    field: keyof EvidenceUpdateDraft,
    value: string,
  ) {
    setUpdateDraft(
      (current) => ({
        ...current,
        [field]: value,
      }),
    )
  }

  function cancelCreate() {
    setIsCreateOpen(false)

    setCreateDraft(
      EMPTY_CREATE_DRAFT,
    )
  }

  async function createEvidence() {
    const evidenceType =
      createDraft.evidenceType.trim()

    const sourceSystem =
      createDraft.sourceSystem.trim()

    if (
      !evidenceType
      || !sourceSystem
    ) {
      return
    }

    const request:
      CaseEvidenceCreateRequest = {
        evidenceType,
        sourceSystem,
        transactionId:
          normalizeOptional(
            createDraft.transactionId,
          ),
        storageUri:
          normalizeOptional(
            createDraft.storageUri,
          ),
        checksumSha256:
          normalizeOptional(
            createDraft.checksumSha256,
          ),
      }

    try {
      await createMutation.mutateAsync({
        caseId,
        request,
      })
    }
    catch {
      return
    }

    setCreateDraft(
      EMPTY_CREATE_DRAFT,
    )

    setIsCreateOpen(false)

    await evidenceQuery.refetch()
  }

  function beginEdit(
    evidence: CaseEvidence,
  ) {
    setEditingEvidenceId(
      evidence.evidenceId,
    )

    setUpdateDraft({
      evidenceType:
        evidence.evidenceType,
      evidenceCategory:
        evidence.evidenceCategory
        ?? '',
      evidenceName:
        evidence.evidenceName
        ?? '',
      evidenceDescription:
        evidence.evidenceDescription
        ?? '',
      validationStatus:
        evidence.validationStatus
        ?? '',
      confidentialityLevel:
        evidence.confidentialityLevel
        ?? '',
    })
  }

  function cancelEdit() {
    setEditingEvidenceId(null)

    setUpdateDraft(
      EMPTY_UPDATE_DRAFT,
    )
  }

  async function updateEvidence() {
    if (!editingEvidenceId) {
      return
    }

    const request:
      CaseEvidenceUpdateRequest = {
        evidenceType:
          normalizeOptional(
            updateDraft.evidenceType,
          ),
        evidenceCategory:
          normalizeOptional(
            updateDraft.evidenceCategory,
          ),
        evidenceName:
          normalizeOptional(
            updateDraft.evidenceName,
          ),
        evidenceDescription:
          normalizeOptional(
            updateDraft.evidenceDescription,
          ),
        validationStatus:
          normalizeOptional(
            updateDraft.validationStatus,
          ),
        confidentialityLevel:
          normalizeOptional(
            updateDraft.confidentialityLevel,
          ),
      }

    try {
      await updateMutation.mutateAsync({
        caseId,
        evidenceId:
          editingEvidenceId,
        request,
      })
    }
    catch {
      return
    }

    cancelEdit()

    await evidenceQuery.refetch()
  }

  async function confirmDelete() {
    if (!pendingDeleteEvidenceId) {
      return
    }

    const evidenceId =
      pendingDeleteEvidenceId

    try {
      await deleteMutation.mutateAsync({
        caseId,
        evidenceId,
      })
    }
    catch {
      return
    }

    setPendingDeleteEvidenceId(
      null,
    )

    if (
      editingEvidenceId
      === evidenceId
    ) {
      cancelEdit()
    }

    await evidenceQuery.refetch()
  }

  const hasUpdateValues =
    Object.values(
      updateDraft,
    ).some(
      (value) =>
        value.trim() !== '',
    )

  const columns =
    useMemo<
      GridColDef<CaseEvidence>[]
    >(
      () => [
        {
          field:
            'evidenceType',
          headerName:
            t(
              'evidence.columns.evidenceType',
            ),
          minWidth: 160,
          flex: 1,
        },
        {
          field:
            'evidenceCategory',
          headerName:
            t(
              'evidence.columns.evidenceCategory',
            ),
          minWidth: 160,
          flex: 1,
          renderCell:
            (params) =>
              params.row
                .evidenceCategory
              ?? '—',
        },
        {
          field:
            'evidenceName',
          headerName:
            t(
              'evidence.columns.evidenceName',
            ),
          minWidth: 190,
          flex: 1,
          renderCell:
            (params) =>
              params.row
                .evidenceName
              ?? '—',
        },
        {
          field:
            'sourceSystem',
          headerName:
            t(
              'evidence.columns.sourceSystem',
            ),
          minWidth: 170,
          flex: 1,
        },
        {
          field:
            'validationStatus',
          headerName:
            t(
              'evidence.columns.validationStatus',
            ),
          minWidth: 160,
          flex: 1,
          renderCell:
            (params) =>
              params.row
                .validationStatus
              ?? '—',
        },
        {
          field:
            'uploadedAt',
          headerName:
            t(
              'evidence.columns.uploadedAt',
            ),
          minWidth: 190,
          flex: 1,
          renderCell:
            (params) =>
              formatDateTime(
                params.row.uploadedAt,
              ),
        },
        {
          field:
            'actions',
          headerName:
            t(
              'evidence.columns.actions',
            ),
          minWidth: 210,
          sortable: false,
          filterable: false,
          renderCell:
            (params) => (
              <Box
                sx={{
                  display: 'flex',
                  gap: 1,
                }}
              >
                <Button
                  size="small"
                  variant="outlined"
                  onClick={() =>
                    beginEdit(
                      params.row,
                    )
                  }
                >
                  {t(
                    'evidence.actions.edit',
                  )}
                </Button>

                <Button
                  size="small"
                  variant="outlined"
                  onClick={() =>
                    setPendingDeleteEvidenceId(
                      params.row
                        .evidenceId,
                    )
                  }
                  disabled={
                    deleteMutation
                      .isPending
                  }
                >
                  {t(
                    'evidence.actions.delete',
                  )}
                </Button>
              </Box>
            ),
        },
      ],
      [
        deleteMutation.isPending,
        t,
      ],
    )

  return (
    <Box
      sx={{
        mt: 3,
      }}
    >
      <Box
        sx={{
          display: 'flex',
          justifyContent:
            'space-between',
          alignItems: 'center',
          gap: 2,
        }}
      >
        <Typography
          component="h3"
          variant="h5"
        >
          {t(
            'evidence.list.title',
          )}
        </Typography>

        {!isCreateOpen && (
          <Button
            variant="contained"
            onClick={() =>
              setIsCreateOpen(
                true,
              )
            }
          >
            {t(
              'evidence.create.open',
            )}
          </Button>
        )}
      </Box>

      {isCreateOpen && (
        <Paper
          sx={{
            p: 2,
            mt: 2,
          }}
        >
          <Typography
            component="h4"
            variant="h6"
          >
            {t(
              'evidence.create.title',
            )}
          </Typography>

          <Box
            sx={{
              display: 'grid',
              gridTemplateColumns:
                'repeat(auto-fit, minmax(220px, 1fr))',
              gap: 2,
              mt: 2,
            }}
          >
            <TextField
              label={t(
                'evidence.fields.evidenceType',
              )}
              size="small"
              required
              value={
                createDraft
                  .evidenceType
              }
              onChange={(event) =>
                updateCreateDraft(
                  'evidenceType',
                  event.target.value,
                )
              }
            />

            <TextField
              label={t(
                'evidence.fields.sourceSystem',
              )}
              size="small"
              required
              value={
                createDraft
                  .sourceSystem
              }
              onChange={(event) =>
                updateCreateDraft(
                  'sourceSystem',
                  event.target.value,
                )
              }
            />

            <TextField
              label={t(
                'evidence.fields.transactionId',
              )}
              size="small"
              value={
                createDraft
                  .transactionId
              }
              onChange={(event) =>
                updateCreateDraft(
                  'transactionId',
                  event.target.value,
                )
              }
            />

            <TextField
              label={t(
                'evidence.fields.storageUri',
              )}
              size="small"
              value={
                createDraft
                  .storageUri
              }
              onChange={(event) =>
                updateCreateDraft(
                  'storageUri',
                  event.target.value,
                )
              }
            />

            <TextField
              label={t(
                'evidence.fields.checksumSha256',
              )}
              size="small"
              value={
                createDraft
                  .checksumSha256
              }
              onChange={(event) =>
                updateCreateDraft(
                  'checksumSha256',
                  event.target.value,
                )
              }
            />
          </Box>

          <Box
            sx={{
              display: 'flex',
              gap: 1,
              mt: 2,
            }}
          >
            <Button
              variant="contained"
              disabled={
                createMutation
                  .isPending
                || !createDraft
                  .evidenceType
                  .trim()
                || !createDraft
                  .sourceSystem
                  .trim()
              }
              onClick={() => {
                void createEvidence()
              }}
            >
              {t(
                'evidence.create.submit',
              )}
            </Button>

            <Button
              variant="outlined"
              onClick={
                cancelCreate
              }
            >
              {t(
                'evidence.create.cancel',
              )}
            </Button>
          </Box>

          {createMutation.isError && (
            <MuiAlert
              severity="error"
              sx={{
                mt: 2,
              }}
            >
              {t(
                'evidence.create.error',
              )}
            </MuiAlert>
          )}
        </Paper>
      )}

      {editingEvidenceId && (
        <Paper
          sx={{
            p: 2,
            mt: 2,
          }}
        >
          <Typography
            component="h4"
            variant="h6"
          >
            {t(
              'evidence.edit.title',
            )}
          </Typography>

          <Box
            sx={{
              display: 'grid',
              gridTemplateColumns:
                'repeat(auto-fit, minmax(220px, 1fr))',
              gap: 2,
              mt: 2,
            }}
          >
            <TextField
              label={t(
                'evidence.fields.evidenceType',
              )}
              size="small"
              value={
                updateDraft
                  .evidenceType
              }
              onChange={(event) =>
                updateEditDraft(
                  'evidenceType',
                  event.target.value,
                )
              }
            />

            <TextField
              label={t(
                'evidence.fields.evidenceCategory',
              )}
              size="small"
              value={
                updateDraft
                  .evidenceCategory
              }
              onChange={(event) =>
                updateEditDraft(
                  'evidenceCategory',
                  event.target.value,
                )
              }
            />

            <TextField
              label={t(
                'evidence.fields.evidenceName',
              )}
              size="small"
              value={
                updateDraft
                  .evidenceName
              }
              onChange={(event) =>
                updateEditDraft(
                  'evidenceName',
                  event.target.value,
                )
              }
            />

            <TextField
              label={t(
                'evidence.fields.evidenceDescription',
              )}
              size="small"
              value={
                updateDraft
                  .evidenceDescription
              }
              onChange={(event) =>
                updateEditDraft(
                  'evidenceDescription',
                  event.target.value,
                )
              }
            />

            <TextField
              label={t(
                'evidence.fields.validationStatus',
              )}
              size="small"
              value={
                updateDraft
                  .validationStatus
              }
              onChange={(event) =>
                updateEditDraft(
                  'validationStatus',
                  event.target.value,
                )
              }
            />

            <TextField
              label={t(
                'evidence.fields.confidentialityLevel',
              )}
              size="small"
              value={
                updateDraft
                  .confidentialityLevel
              }
              onChange={(event) =>
                updateEditDraft(
                  'confidentialityLevel',
                  event.target.value,
                )
              }
            />
          </Box>

          <Box
            sx={{
              display: 'flex',
              gap: 1,
              mt: 2,
            }}
          >
            <Button
              variant="contained"
              disabled={
                updateMutation
                  .isPending
                || !hasUpdateValues
              }
              onClick={() => {
                void updateEvidence()
              }}
            >
              {t(
                'evidence.edit.save',
              )}
            </Button>

            <Button
              variant="outlined"
              onClick={
                cancelEdit
              }
            >
              {t(
                'evidence.edit.cancel',
              )}
            </Button>
          </Box>

          {updateMutation.isError && (
            <MuiAlert
              severity="error"
              sx={{
                mt: 2,
              }}
            >
              {t(
                'evidence.edit.error',
              )}
            </MuiAlert>
          )}
        </Paper>
      )}

      {evidenceQuery.isError && (
        <MuiAlert
          severity="error"
          sx={{
            mt: 2,
          }}
        >
          {t(
            'evidence.list.error',
          )}
        </MuiAlert>
      )}

      {deleteMutation.isError && (
        <MuiAlert
          severity="error"
          sx={{
            mt: 2,
          }}
        >
          {t(
            'evidence.delete.error',
          )}
        </MuiAlert>
      )}

      <Paper
        sx={{
          mt: 2,
        }}
      >
        <Box
          sx={{
            height: 600,
            width: '100%',
          }}
        >
          <DataGrid<CaseEvidence>
            rows={
              evidenceQuery.data
              ?? []
            }
            columns={columns}
            getRowId={(row) =>
              row.evidenceId
            }
            loading={
              evidenceQuery
                .isFetching
            }
            disableRowSelectionOnClick
            localeText={{
              noRowsLabel:
                t(
                  'evidence.list.noRows',
                ),
            }}
            sx={{
              border: 0,
            }}
          />
        </Box>
      </Paper>

      <Dialog
        open={
          pendingDeleteEvidenceId
          !== null
        }
        onClose={() =>
          setPendingDeleteEvidenceId(
            null,
          )
        }
      >
        <DialogTitle>
          {t(
            'evidence.delete.title',
          )}
        </DialogTitle>

        <DialogContent>
          <DialogContentText>
            {t(
              'evidence.delete.message',
            )}
          </DialogContentText>
        </DialogContent>

        <DialogActions>
          <Button
            onClick={() =>
              setPendingDeleteEvidenceId(
                null,
              )
            }
          >
            {t(
              'evidence.delete.cancel',
            )}
          </Button>

          <Button
            onClick={() => {
              void confirmDelete()
            }}
            disabled={
              deleteMutation
                .isPending
            }
          >
            {t(
              'evidence.delete.confirm',
            )}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  )
}

export default CaseEvidenceSection
