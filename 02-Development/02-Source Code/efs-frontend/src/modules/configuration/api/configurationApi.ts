import { httpClient } from '../../../services/httpClient'
import type {
  ConfigurationChangeRequest,
  ConfigurationChangeRequestCreateRequest,
  ConfigurationEffective,
  ConfigurationVersion,
  RejectConfigurationChangeRequestCommand,
} from '../types/configuration'

const CONFIGURATION_PATH =
  '/admin/configuration'

export function getEffectiveConfigurations(
  tenantId?: string,
): Promise<ConfigurationEffective[]> {
  return httpClient.get<ConfigurationEffective[]>(
    CONFIGURATION_PATH + '/effective',
    {
      tenantId,
    },
  )
}

export function getEffectiveConfiguration(
  configurationKey: string,
  tenantId?: string,
): Promise<ConfigurationEffective> {
  return httpClient.get<ConfigurationEffective>(
    CONFIGURATION_PATH
      + '/effective/'
      + encodeURIComponent(configurationKey),
    {
      tenantId,
    },
  )
}

export function createConfigurationChangeRequest(
  request: ConfigurationChangeRequestCreateRequest,
): Promise<ConfigurationChangeRequest> {
  return httpClient.post<ConfigurationChangeRequest>(
    CONFIGURATION_PATH + '/change-requests',
    request,
  )
}

export function getConfigurationChangeRequest(
  changeRequestId: string,
): Promise<ConfigurationChangeRequest> {
  return httpClient.get<ConfigurationChangeRequest>(
    CONFIGURATION_PATH
      + '/change-requests/'
      + encodeURIComponent(changeRequestId),
  )
}

export function approveConfigurationChangeRequest(
  changeRequestId: string,
): Promise<ConfigurationChangeRequest> {
  return httpClient.post<ConfigurationChangeRequest>(
    CONFIGURATION_PATH
      + '/change-requests/'
      + encodeURIComponent(changeRequestId)
      + '/approve',
  )
}

export function rejectConfigurationChangeRequest(
  command: RejectConfigurationChangeRequestCommand,
): Promise<ConfigurationChangeRequest> {
  return httpClient.post<ConfigurationChangeRequest>(
    CONFIGURATION_PATH
      + '/change-requests/'
      + encodeURIComponent(command.changeRequestId)
      + '/reject',
    command.request,
  )
}

export function publishConfigurationChangeRequest(
  changeRequestId: string,
): Promise<ConfigurationChangeRequest> {
  return httpClient.post<ConfigurationChangeRequest>(
    CONFIGURATION_PATH
      + '/change-requests/'
      + encodeURIComponent(changeRequestId)
      + '/publish',
  )
}

export function getConfigurationVersions(
  configurationKey: string,
  tenantId?: string,
): Promise<ConfigurationVersion[]> {
  return httpClient.get<ConfigurationVersion[]>(
    CONFIGURATION_PATH
      + '/'
      + encodeURIComponent(configurationKey)
      + '/versions',
    {
      tenantId,
    },
  )
}