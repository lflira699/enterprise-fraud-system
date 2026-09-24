import {
  Alert,
  Button,
  Chip,
  CircularProgress,
  Divider,
  Paper,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  TextField,
  Typography,
} from '@mui/material'
import {
  type FormEvent,
  useState,
} from 'react'
import { useTranslation } from 'react-i18next'

import {
  useApproveConfigurationChangeRequestMutation,
  useConfigurationChangeRequestQuery,
  useConfigurationVersionsQuery,
  useCreateConfigurationChangeRequestMutation,
  useEffectiveConfigurationsQuery,
  usePublishConfigurationChangeRequestMutation,
  useRejectConfigurationChangeRequestMutation,
} from '../hooks/useConfiguration'

function optionalValue(
  value: string,
): string | undefined {
  const trimmed =
    value.trim()

  return trimmed === ''
    ? undefined
    : trimmed
}

function ConfigurationPage() {
  const { t } =
    useTranslation()

  const [
    tenantId,
    setTenantId,
  ] =
    useState('')

  const [
    changeRequestIdInput,
    setChangeRequestIdInput,
  ] =
    useState('')

  const [
    selectedChangeRequestId,
    setSelectedChangeRequestId,
  ] =
    useState<string | null>(null)

  const [
    rejectionReason,
    setRejectionReason,
  ] =
    useState('')

  const [
    versionKeyInput,
    setVersionKeyInput,
  ] =
    useState('')

  const [
    selectedVersionKey,
    setSelectedVersionKey,
  ] =
    useState<string | null>(null)

  const [
    justification,
    setJustification,
  ] =
    useState('')

  const [
    affectedEnvironment,
    setAffectedEnvironment,
  ] =
    useState('')

  const [
    riskAssessment,
    setRiskAssessment,
  ] =
    useState('')

  const [
    expectedResult,
    setExpectedResult,
  ] =
    useState('')

  const [
    rollbackPlan,
    setRollbackPlan,
  ] =
    useState('')

  const [
    configurationKey,
    setConfigurationKey,
  ] =
    useState('')

  const [
    proposedValue,
    setProposedValue,
  ] =
    useState('')

  const normalizedTenantId =
    optionalValue(tenantId)

  const effectiveQuery =
    useEffectiveConfigurationsQuery(
      normalizedTenantId,
    )

  const changeRequestQuery =
    useConfigurationChangeRequestQuery(
      selectedChangeRequestId,
    )

  const versionsQuery =
    useConfigurationVersionsQuery(
      selectedVersionKey,
      normalizedTenantId,
    )

  const createMutation =
    useCreateConfigurationChangeRequestMutation()

  const approveMutation =
    useApproveConfigurationChangeRequestMutation()

  const rejectMutation =
    useRejectConfigurationChangeRequestMutation()

  const publishMutation =
    usePublishConfigurationChangeRequestMutation()

  const effectiveConfigurations =
    effectiveQuery.data ?? []

  const versions =
    versionsQuery.data ?? []

  const changeRequest =
    changeRequestQuery.data

  const createDisabled =
    justification.trim() === ''
    || affectedEnvironment.trim() === ''
    || riskAssessment.trim() === ''
    || expectedResult.trim() === ''
    || rollbackPlan.trim() === ''
    || configurationKey.trim() === ''
    || proposedValue.trim() === ''
    || createMutation.isPending

  function submitChangeRequest(
    event: FormEvent<HTMLFormElement>,
  ) {
    event.preventDefault()

    if (createDisabled) {
      return
    }

    createMutation.mutate(
      {
        tenantId:
          normalizedTenantId,
        justification:
          justification.trim(),
        affectedEnvironment:
          affectedEnvironment.trim(),
        riskAssessment:
          riskAssessment.trim(),
        expectedResult:
          expectedResult.trim(),
        rollbackPlan:
          rollbackPlan.trim(),
        items: [
          {
            configurationKey:
              configurationKey.trim(),
            proposedValue,
          },
        ],
      },
      {
        onSuccess:
          (data) => {
            setChangeRequestIdInput(
              data.changeRequestId,
            )
            setSelectedChangeRequestId(
              data.changeRequestId,
            )
          },
      },
    )
  }

  return (
    <Stack spacing={3}>
      <Typography
        component="h1"
        variant="h4"
      >
        {t('systemConfiguration.title')}
      </Typography>

      <Paper sx={{ p: 2 }}>
        <Stack spacing={2}>
          <Typography
            component="h2"
            variant="h6"
          >
            {t(
              'systemConfiguration.effective.title',
            )}
          </Typography>

          <TextField
            label={t(
              'systemConfiguration.fields.tenantId',
            )}
            value={tenantId}
            onChange={(event) => {
              setTenantId(
                event.target.value,
              )
            }}
            fullWidth
          />

          {effectiveQuery.isLoading && (
            <CircularProgress
              aria-label={t(
                'systemConfiguration.loading',
              )}
              size={24}
            />
          )}

          {effectiveQuery.isError && (
            <Alert severity="error">
              {t(
                'systemConfiguration.effective.loadError',
              )}
            </Alert>
          )}

          {!effectiveQuery.isLoading
            && !effectiveQuery.isError
            && effectiveConfigurations.length === 0
            && (
              <Alert severity="info">
                {t(
                  'systemConfiguration.effective.noRows',
                )}
              </Alert>
            )}

          {effectiveConfigurations.length > 0 && (
            <Table
              size="small"
              aria-label={t(
                'systemConfiguration.effective.tableLabel',
              )}
            >
              <TableHead>
                <TableRow>
                  <TableCell>
                    {t(
                      'systemConfiguration.fields.configurationKey',
                    )}
                  </TableCell>
                  <TableCell>
                    {t(
                      'systemConfiguration.fields.configurationValue',
                    )}
                  </TableCell>
                  <TableCell>
                    {t(
                      'systemConfiguration.fields.configurationType',
                    )}
                  </TableCell>
                  <TableCell>
                    {t(
                      'systemConfiguration.fields.effectiveScope',
                    )}
                  </TableCell>
                  <TableCell>
                    {t(
                      'systemConfiguration.fields.critical',
                    )}
                  </TableCell>
                </TableRow>
              </TableHead>

              <TableBody>
                {effectiveConfigurations.map(
                  (configuration) => (
                    <TableRow
                      key={
                        configuration.configurationKey
                      }
                    >
                      <TableCell>
                        {
                          configuration.configurationKey
                        }
                      </TableCell>
                      <TableCell>
                        {
                          configuration.encrypted
                            ? '••••••••'
                            : (
                              configuration.configurationValue
                              ?? ''
                            )
                        }
                      </TableCell>
                      <TableCell>
                        {
                          configuration.configurationType
                        }
                      </TableCell>
                      <TableCell>
                        {
                          configuration.effectiveScope
                        }
                      </TableCell>
                      <TableCell>
                        {
                          configuration.critical
                            ? t(
                              'systemConfiguration.yes',
                            )
                            : t(
                              'systemConfiguration.no',
                            )
                        }
                      </TableCell>
                    </TableRow>
                  ),
                )}
              </TableBody>
            </Table>
          )}
        </Stack>
      </Paper>

      <Paper sx={{ p: 2 }}>
        <Stack
          component="form"
          spacing={2}
          onSubmit={submitChangeRequest}
        >
          <Typography
            component="h2"
            variant="h6"
          >
            {t(
              'systemConfiguration.changeRequest.createTitle',
            )}
          </Typography>

          <TextField
            label={t(
              'systemConfiguration.fields.justification',
            )}
            value={justification}
            onChange={(event) => {
              setJustification(
                event.target.value,
              )
            }}
            required
            fullWidth
          />

          <TextField
            label={t(
              'systemConfiguration.fields.affectedEnvironment',
            )}
            value={affectedEnvironment}
            onChange={(event) => {
              setAffectedEnvironment(
                event.target.value,
              )
            }}
            required
            fullWidth
          />

          <TextField
            label={t(
              'systemConfiguration.fields.riskAssessment',
            )}
            value={riskAssessment}
            onChange={(event) => {
              setRiskAssessment(
                event.target.value,
              )
            }}
            required
            multiline
            minRows={2}
            fullWidth
          />

          <TextField
            label={t(
              'systemConfiguration.fields.expectedResult',
            )}
            value={expectedResult}
            onChange={(event) => {
              setExpectedResult(
                event.target.value,
              )
            }}
            required
            multiline
            minRows={2}
            fullWidth
          />

          <TextField
            label={t(
              'systemConfiguration.fields.rollbackPlan',
            )}
            value={rollbackPlan}
            onChange={(event) => {
              setRollbackPlan(
                event.target.value,
              )
            }}
            required
            multiline
            minRows={2}
            fullWidth
          />

          <Divider />

          <TextField
            label={t(
              'systemConfiguration.fields.configurationKey',
            )}
            value={configurationKey}
            onChange={(event) => {
              setConfigurationKey(
                event.target.value,
              )
            }}
            required
            fullWidth
          />

          <TextField
            label={t(
              'systemConfiguration.fields.proposedValue',
            )}
            value={proposedValue}
            onChange={(event) => {
              setProposedValue(
                event.target.value,
              )
            }}
            required
            fullWidth
          />

          {createMutation.isError && (
            <Alert severity="error">
              {t(
                'systemConfiguration.changeRequest.createError',
              )}
            </Alert>
          )}

          <Button
            type="submit"
            variant="contained"
            disabled={createDisabled}
          >
            {t(
              'systemConfiguration.changeRequest.createAction',
            )}
          </Button>
        </Stack>
      </Paper>

      <Paper sx={{ p: 2 }}>
        <Stack spacing={2}>
          <Typography
            component="h2"
            variant="h6"
          >
            {t(
              'systemConfiguration.changeRequest.reviewTitle',
            )}
          </Typography>

          <Stack
            direction={{
              xs: 'column',
              md: 'row',
            }}
            spacing={2}
          >
            <TextField
              label={t(
                'systemConfiguration.fields.changeRequestId',
              )}
              value={changeRequestIdInput}
              onChange={(event) => {
                setChangeRequestIdInput(
                  event.target.value,
                )
              }}
              fullWidth
            />

            <Button
              variant="outlined"
              onClick={() => {
                const value =
                  changeRequestIdInput.trim()

                setSelectedChangeRequestId(
                  value === ''
                    ? null
                    : value,
                )
              }}
              disabled={
                changeRequestIdInput.trim() === ''
              }
            >
              {t(
                'systemConfiguration.changeRequest.lookupAction',
              )}
            </Button>
          </Stack>

          {changeRequestQuery.isLoading && (
            <CircularProgress size={24} />
          )}

          {changeRequestQuery.isError && (
            <Alert severity="error">
              {t(
                'systemConfiguration.changeRequest.loadError',
              )}
            </Alert>
          )}

          {changeRequest && (
            <Stack spacing={2}>
              <Stack
                direction="row"
                spacing={1}
                sx={{ alignItems: 'center' }}
              >
                <Typography variant="body1">
                  {
                    changeRequest.changeRequestId
                  }
                </Typography>

                <Chip
                  label={changeRequest.status}
                  size="small"
                />
              </Stack>

              <Typography variant="body2">
                {
                  changeRequest.justification
                }
              </Typography>

              <Stack
                direction={{
                  xs: 'column',
                  md: 'row',
                }}
                spacing={1}
              >
                <Button
                  variant="contained"
                  onClick={() => {
                    approveMutation.mutate(
                      changeRequest.changeRequestId,
                    )
                  }}
                  disabled={
                    approveMutation.isPending
                  }
                >
                  {t(
                    'systemConfiguration.changeRequest.approveAction',
                  )}
                </Button>

                <Button
                  variant="contained"
                  onClick={() => {
                    publishMutation.mutate(
                      changeRequest.changeRequestId,
                    )
                  }}
                  disabled={
                    publishMutation.isPending
                  }
                >
                  {t(
                    'systemConfiguration.changeRequest.publishAction',
                  )}
                </Button>
              </Stack>

              <TextField
                label={t(
                  'systemConfiguration.fields.rejectionReason',
                )}
                value={rejectionReason}
                onChange={(event) => {
                  setRejectionReason(
                    event.target.value,
                  )
                }}
                fullWidth
              />

              <Button
                variant="outlined"
                onClick={() => {
                  rejectMutation.mutate({
                    changeRequestId:
                      changeRequest.changeRequestId,
                    request: {
                      rejectionReason:
                        rejectionReason.trim(),
                    },
                  })
                }}
                disabled={
                  rejectionReason.trim() === ''
                  || rejectMutation.isPending
                }
              >
                {t(
                  'systemConfiguration.changeRequest.rejectAction',
                )}
              </Button>
            </Stack>
          )}
        </Stack>
      </Paper>

      <Paper sx={{ p: 2 }}>
        <Stack spacing={2}>
          <Typography
            component="h2"
            variant="h6"
          >
            {t(
              'systemConfiguration.versions.title',
            )}
          </Typography>

          <Stack
            direction={{
              xs: 'column',
              md: 'row',
            }}
            spacing={2}
          >
            <TextField
              label={t(
                'systemConfiguration.fields.configurationKey',
              )}
              value={versionKeyInput}
              onChange={(event) => {
                setVersionKeyInput(
                  event.target.value,
                )
              }}
              fullWidth
            />

            <Button
              variant="outlined"
              onClick={() => {
                const value =
                  versionKeyInput.trim()

                setSelectedVersionKey(
                  value === ''
                    ? null
                    : value,
                )
              }}
              disabled={
                versionKeyInput.trim() === ''
              }
            >
              {t(
                'systemConfiguration.versions.lookupAction',
              )}
            </Button>
          </Stack>

          {versionsQuery.isLoading && (
            <CircularProgress size={24} />
          )}

          {versionsQuery.isError && (
            <Alert severity="error">
              {t(
                'systemConfiguration.versions.loadError',
              )}
            </Alert>
          )}

          {selectedVersionKey !== null
            && !versionsQuery.isLoading
            && !versionsQuery.isError
            && versions.length === 0
            && (
              <Alert severity="info">
                {t(
                  'systemConfiguration.versions.noRows',
                )}
              </Alert>
            )}

          {versions.length > 0 && (
            <Table
              size="small"
              aria-label={t(
                'systemConfiguration.versions.tableLabel',
              )}
            >
              <TableHead>
                <TableRow>
                  <TableCell>
                    {t(
                      'systemConfiguration.fields.versionNumber',
                    )}
                  </TableCell>
                  <TableCell>
                    {t(
                      'systemConfiguration.fields.configurationValue',
                    )}
                  </TableCell>
                  <TableCell>
                    {t(
                      'systemConfiguration.fields.appliedBy',
                    )}
                  </TableCell>
                  <TableCell>
                    {t(
                      'systemConfiguration.fields.appliedAt',
                    )}
                  </TableCell>
                </TableRow>
              </TableHead>

              <TableBody>
                {versions.map(
                  (version) => (
                    <TableRow
                      key={
                        version.changeRequestId
                        + '-'
                        + version.versionNumber
                      }
                    >
                      <TableCell>
                        {version.versionNumber}
                      </TableCell>
                      <TableCell>
                        {
                          version.encrypted
                            ? '••••••••'
                            : (
                              version.configurationValue
                              ?? ''
                            )
                        }
                      </TableCell>
                      <TableCell>
                        {version.appliedBy}
                      </TableCell>
                      <TableCell>
                        {version.appliedAt}
                      </TableCell>
                    </TableRow>
                  ),
                )}
              </TableBody>
            </Table>
          )}
        </Stack>
      </Paper>
    </Stack>
  )
}

export default ConfigurationPage