import Keycloak from 'keycloak-js'

function requiredConfiguration(
  name: string,
  value: string | undefined,
): string {
  if (!value?.trim()) {
    throw new Error(
      `Missing required identity configuration: ${name}`,
    )
  }

  return value.trim()
}

let keycloak:
  Keycloak | null = null

let initialization:
  Promise<boolean> | null = null

function getKeycloak():
  Keycloak {
  if (!keycloak) {
    const keycloakUrl =
      requiredConfiguration(
        'VITE_EFS_KEYCLOAK_URL',
        import.meta.env.VITE_EFS_KEYCLOAK_URL,
      )

    const keycloakRealm =
      requiredConfiguration(
        'VITE_EFS_KEYCLOAK_REALM',
        import.meta.env.VITE_EFS_KEYCLOAK_REALM,
      )

    const keycloakClientId =
      requiredConfiguration(
        'VITE_EFS_KEYCLOAK_CLIENT_ID',
        import.meta.env.VITE_EFS_KEYCLOAK_CLIENT_ID,
      )

    keycloak =
      new Keycloak({
        url: keycloakUrl,
        realm: keycloakRealm,
        clientId: keycloakClientId,
      })
  }

  return keycloak
}

export function initializeKeycloak():
  Promise<boolean> {
  if (!initialization) {
    initialization =
      getKeycloak()
        .init({
          onLoad: 'login-required',
          pkceMethod: 'S256',
          responseMode: 'query',
          checkLoginIframe: false,
        })
  }

  return initialization
}

export function login():
  Promise<void> {
  return getKeycloak()
    .login({
      redirectUri:
        window.location.href,
    })
}

export function logout():
  Promise<void> {
  return getKeycloak()
    .logout({
      redirectUri:
        window.location.origin,
    })
}

export async function getValidAccessToken():
  Promise<string | null> {
  const authenticatedKeycloak =
    keycloak

  if (
    !authenticatedKeycloak
    || !authenticatedKeycloak.authenticated
  ) {
    return null
  }

  try {
    await authenticatedKeycloak
      .updateToken(0)
  } catch {
    return null
  }

  if (
    !authenticatedKeycloak.authenticated
    || !authenticatedKeycloak.token
  ) {
    return null
  }

  return authenticatedKeycloak.token
}