import {
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from 'vitest'

const {
  keycloakConstructorMock,
  keycloakInstance,
} = vi.hoisted(() => {
  const instance = {
    authenticated: false,
    token: undefined as
      | string
      | undefined,
    init: vi.fn(),
    login: vi.fn(),
    logout: vi.fn(),
    updateToken: vi.fn(),
  }

  const constructorMock =
    vi.fn(
      function KeycloakMock() {
        return instance
      },
    )

  return {
    keycloakInstance:
      instance,
    keycloakConstructorMock:
      constructorMock,
  }
})

vi.mock(
  'keycloak-js',
  () => ({
    default:
      keycloakConstructorMock,
  }),
)

async function loadIdentityClient() {
  return import(
    '../src/security/keycloak'
  )
}

beforeEach(() => {
  vi.resetModules()

  keycloakConstructorMock.mockClear()

  keycloakInstance.authenticated =
    false

  keycloakInstance.token =
    undefined

  keycloakInstance.init.mockReset()
  keycloakInstance.login.mockReset()
  keycloakInstance.logout.mockReset()
  keycloakInstance.updateToken.mockReset()

  vi.stubEnv(
    'VITE_EFS_KEYCLOAK_URL',
    'http://localhost:8081',
  )

  vi.stubEnv(
    'VITE_EFS_KEYCLOAK_REALM',
    'efs-development',
  )

  vi.stubEnv(
    'VITE_EFS_KEYCLOAK_CLIENT_ID',
    'efs-frontend',
  )
})

afterEach(() => {
  vi.unstubAllEnvs()
  vi.restoreAllMocks()
})

describe(
  'Keycloak identity client lifecycle',
  () => {
    it(
      'initializes the approved public OIDC client with login-required and PKCE S256',
      async () => {
        keycloakInstance.init
          .mockResolvedValue(true)

        const identity =
          await loadIdentityClient()

        const firstInitialization =
          identity.initializeKeycloak()

        const secondInitialization =
          identity.initializeKeycloak()

        await expect(
          firstInitialization,
        ).resolves.toBe(true)

        await expect(
          secondInitialization,
        ).resolves.toBe(true)

        expect(
          keycloakConstructorMock,
        ).toHaveBeenCalledTimes(1)

        expect(
          keycloakConstructorMock,
        ).toHaveBeenCalledWith({
          url:
            'http://localhost:8081',
          realm:
            'efs-development',
          clientId:
            'efs-frontend',
        })

        expect(
          keycloakInstance.init,
        ).toHaveBeenCalledTimes(1)

        expect(
          keycloakInstance.init,
        ).toHaveBeenCalledWith({
          onLoad:
            'login-required',
          pkceMethod:
            'S256',
          responseMode: 'query',
          checkLoginIframe:
            false,
        })
      },
    )

    it(
      'delegates login and logout using browser redirect locations',
      async () => {
        const identity =
          await loadIdentityClient()

        keycloakInstance.login
          .mockResolvedValue(undefined)

        keycloakInstance.logout
          .mockResolvedValue(undefined)

        await identity.login()

        await identity.logout()

        expect(
          keycloakInstance.login,
        ).toHaveBeenCalledWith({
          redirectUri:
            window.location.href,
        })

        expect(
          keycloakInstance.logout,
        ).toHaveBeenCalledWith({
          redirectUri:
            window.location.origin,
        })
      },
    )

    it(
      'returns no Access Token before an authenticated Keycloak session exists',
      async () => {
        const identity =
          await loadIdentityClient()

        await expect(
          identity.getValidAccessToken(),
        ).resolves.toBeNull()

        expect(
          keycloakInstance.updateToken,
        ).not.toHaveBeenCalled()
      },
    )

    it(
      'refreshes token validity before returning the current Access Token',
      async () => {
        keycloakInstance.init
          .mockResolvedValue(true)

        const identity =
          await loadIdentityClient()

        await identity
          .initializeKeycloak()

        keycloakInstance.authenticated =
          true

        keycloakInstance.token =
          'access-token'

        keycloakInstance.updateToken
          .mockResolvedValue(false)

        await expect(
          identity.getValidAccessToken(),
        ).resolves.toBe(
          'access-token',
        )

        expect(
          keycloakInstance.updateToken,
        ).toHaveBeenCalledTimes(1)

        expect(
          keycloakInstance.updateToken,
        ).toHaveBeenCalledWith(0)
      },
    )

    it(
      'fails closed when token refresh cannot be completed',
      async () => {
        keycloakInstance.init
          .mockResolvedValue(true)

        const identity =
          await loadIdentityClient()

        await identity
          .initializeKeycloak()

        keycloakInstance.authenticated =
          true

        keycloakInstance.token =
          'access-token'

        keycloakInstance.updateToken
          .mockRejectedValue(
            new Error(
              'Token refresh failed',
            ),
          )

        await expect(
          identity.getValidAccessToken(),
        ).resolves.toBeNull()
      },
    )

    it(
      'rejects initialization when mandatory identity configuration is absent',
      async () => {
        vi.stubEnv(
          'VITE_EFS_KEYCLOAK_URL',
          '',
        )

        const identity =
          await loadIdentityClient()

        expect(
          () =>
            identity.initializeKeycloak(),
        ).toThrow(
          'Missing required identity configuration: VITE_EFS_KEYCLOAK_URL',
        )

        expect(
          keycloakConstructorMock,
        ).not.toHaveBeenCalled()
      },
    )
  },
)