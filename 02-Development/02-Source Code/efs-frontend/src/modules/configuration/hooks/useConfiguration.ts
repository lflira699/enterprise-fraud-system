import {
  useMutation,
  useQuery,
  useQueryClient,
} from '@tanstack/react-query'

import {
  approveConfigurationChangeRequest,
  createConfigurationChangeRequest,
  getConfigurationChangeRequest,
  getConfigurationVersions,
  getEffectiveConfigurations,
  publishConfigurationChangeRequest,
  rejectConfigurationChangeRequest,
} from '../api/configurationApi'

export const configurationQueryKeys = {
  all:
    ['configuration'] as const,

  effective(
    tenantId?: string,
  ) {
    return [
      ...configurationQueryKeys.all,
      'effective',
      tenantId ?? null,
    ] as const
  },

  changeRequest(
    changeRequestId: string,
  ) {
    return [
      ...configurationQueryKeys.all,
      'change-request',
      changeRequestId,
    ] as const
  },

  versions(
    configurationKey: string,
    tenantId?: string,
  ) {
    return [
      ...configurationQueryKeys.all,
      'versions',
      configurationKey,
      tenantId ?? null,
    ] as const
  },
}

export function useEffectiveConfigurationsQuery(
  tenantId?: string,
) {
  return useQuery({
    queryKey:
      configurationQueryKeys.effective(
        tenantId,
      ),

    queryFn:
      () =>
        getEffectiveConfigurations(
          tenantId,
        ),
  })
}

export function useConfigurationChangeRequestQuery(
  changeRequestId: string | null,
) {
  return useQuery({
    queryKey:
      configurationQueryKeys.changeRequest(
        changeRequestId ?? '',
      ),

    queryFn:
      () =>
        getConfigurationChangeRequest(
          changeRequestId as string,
        ),

    enabled:
      changeRequestId !== null,
  })
}

export function useConfigurationVersionsQuery(
  configurationKey: string | null,
  tenantId?: string,
) {
  return useQuery({
    queryKey:
      configurationQueryKeys.versions(
        configurationKey ?? '',
        tenantId,
      ),

    queryFn:
      () =>
        getConfigurationVersions(
          configurationKey as string,
          tenantId,
        ),

    enabled:
      configurationKey !== null,
  })
}

export function useCreateConfigurationChangeRequestMutation() {
  const queryClient =
    useQueryClient()

  return useMutation({
    mutationFn:
      createConfigurationChangeRequest,

    onSuccess:
      (data) => {
        queryClient.setQueryData(
          configurationQueryKeys.changeRequest(
            data.changeRequestId,
          ),
          data,
        )
      },
  })
}

export function useApproveConfigurationChangeRequestMutation() {
  const queryClient =
    useQueryClient()

  return useMutation({
    mutationFn:
      approveConfigurationChangeRequest,

    onSuccess:
      (data) => {
        queryClient.setQueryData(
          configurationQueryKeys.changeRequest(
            data.changeRequestId,
          ),
          data,
        )
      },
  })
}

export function useRejectConfigurationChangeRequestMutation() {
  const queryClient =
    useQueryClient()

  return useMutation({
    mutationFn:
      rejectConfigurationChangeRequest,

    onSuccess:
      (data) => {
        queryClient.setQueryData(
          configurationQueryKeys.changeRequest(
            data.changeRequestId,
          ),
          data,
        )
      },
  })
}

export function usePublishConfigurationChangeRequestMutation() {
  const queryClient =
    useQueryClient()

  return useMutation({
    mutationFn:
      publishConfigurationChangeRequest,

    onSuccess:
      (data) => {
        queryClient.setQueryData(
          configurationQueryKeys.changeRequest(
            data.changeRequestId,
          ),
          data,
        )

        void queryClient.invalidateQueries({
          queryKey:
            configurationQueryKeys.all,
        })
      },
  })
}