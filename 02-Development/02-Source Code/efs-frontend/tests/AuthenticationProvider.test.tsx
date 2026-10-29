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

const {
  initializeKeycloakMock,
  loginMock,
  logoutMock,
} = vi.hoisted(() => ({
  initializeKeycloakMock: vi.fn(),
  loginMock: vi.fn(),
  logoutMock: vi.fn(),
}))

vi.mock(
  '../src/security/keycloak',
  () => ({
    initializeKeycloak:
      initializeKeycloakMock,
    login:
      loginMock,
    logout:
      logoutMock,
  }),
)

const efsLogoutPostMock = vi.hoisted(
  () => vi.fn(),
)

vi.mock(
  '../src/services/httpClient',
  () => ({
    httpClient: {
      post: efsLogoutPostMock,
    },
    subscribeToAuthenticationFailure: () => () => {},
  }),
)
import {
  AuthenticationProvider,
  useAuthentication,
} from '../src/security/AuthenticationProvider'

function AuthenticationProbe() {
  const authentication =
    useAuthentication()

  return (
    <div>
      <div>
        authenticated:
        {String(authentication.authenticated)}
      </div>

      <div>
        initialized:
        {String(authentication.initialized)}
      </div>

      <button
        type="button"
        onClick={() => {
          void authentication.login()
        }}
      >
        Login
      </button>

      <button
        type="button"
        onClick={() => {
          void authentication.logout()
        }}
      >
        Logout
      </button>
    </div>
  )
}

beforeEach(() => {
  initializeKeycloakMock.mockReset()
  loginMock.mockReset()
  logoutMock.mockReset()
  efsLogoutPostMock.mockReset()

  loginMock.mockResolvedValue(undefined)
  logoutMock.mockResolvedValue(undefined)
  efsLogoutPostMock.mockResolvedValue(undefined)
})

afterEach(() => {
  cleanup()
  vi.restoreAllMocks()
})

describe(
  'AuthenticationProvider',
  () => {
    it(
      'keeps protected content unavailable until authentication initialization completes',
      async () => {
        let resolveInitialization:
          ((authenticated: boolean) => void)
          | null = null

        initializeKeycloakMock
          .mockImplementation(
            () =>
              new Promise<boolean>(
                (resolve) => {
                  resolveInitialization =
                    resolve
                },
              ),
          )

        render(
          <AuthenticationProvider>
            <div>
              Protected content
            </div>
          </AuthenticationProvider>,
        )

        expect(
          screen.queryByText(
            'Protected content',
          ),
        ).toBeNull()

        expect(
          initializeKeycloakMock,
        ).toHaveBeenCalledTimes(1)

        resolveInitialization?.(true)

        expect(
          await screen.findByText(
            'Protected content',
          ),
        ).toBeTruthy()
      },
    )

    it(
      'exposes authenticated context and delegates login and logout to the identity client',
      async () => {
        initializeKeycloakMock
          .mockResolvedValue(true)

        render(
          <AuthenticationProvider>
            <AuthenticationProbe />
          </AuthenticationProvider>,
        )

        expect(
          await screen.findByText(
            'authenticated:true',
          ),
        ).toBeTruthy()

        expect(
          screen.getByText(
            'initialized:true',
          ),
        ).toBeTruthy()

        fireEvent.click(
          screen.getByRole(
            'button',
            {
              name: 'Login',
            },
          ),
        )

        fireEvent.click(
          screen.getByRole(
            'button',
            {
              name: 'Logout',
            },
          ),
        )

        await waitFor(() => {
          expect(
            loginMock,
          ).toHaveBeenCalledTimes(1)

          expect(
            logoutMock,
          ).toHaveBeenCalledTimes(1)

          expect(
            efsLogoutPostMock,
          ).toHaveBeenCalledWith(
            '/authentication/logout',
          )

          expect(
            efsLogoutPostMock.mock.invocationCallOrder[0],
          ).toBeLessThan(
            logoutMock.mock.invocationCallOrder[0],
          )
        })
      },
    )

    it(
      'fails closed when the identity provider reports an unauthenticated result',
      async () => {
        initializeKeycloakMock
          .mockResolvedValue(false)

        render(
          <AuthenticationProvider>
            <div>
              Protected content
            </div>
          </AuthenticationProvider>,
        )

        await waitFor(() => {
          expect(
            initializeKeycloakMock,
          ).toHaveBeenCalledTimes(1)
        })

        expect(
          screen.queryByText(
            'Protected content',
          ),
        ).toBeNull()
      },
    )

    it(
      'fails closed when identity initialization fails',
      async () => {
        initializeKeycloakMock
          .mockRejectedValue(
            new Error(
              'Identity initialization failed',
            ),
          )

        render(
          <AuthenticationProvider>
            <div>
              Protected content
            </div>
          </AuthenticationProvider>,
        )

        await waitFor(() => {
          expect(
            initializeKeycloakMock,
          ).toHaveBeenCalledTimes(1)
        })

        expect(
          screen.queryByText(
            'Protected content',
          ),
        ).toBeNull()
      },
    )
  },
)

function LogoutPartialFailureProbe({
  onCapture,
}: {
  onCapture: (action: () => Promise<void>) => void
}) {
  const authentication = useAuthentication()

  onCapture(authentication.logout)

  return <div>Logout action ready</div>
}

describe('AuthenticationProvider partial logout failures', () => {
  async function obtainLogoutAction() {
    let action: (() => Promise<void>) | undefined

    initializeKeycloakMock.mockResolvedValue(true)

    render(
      <AuthenticationProvider>
        <LogoutPartialFailureProbe
          onCapture={(logoutAction) => {
            action = logoutAction
          }}
        />
      </AuthenticationProvider>,
    )

    await screen.findByText('Logout action ready')

    if (!action) {
      throw new Error('Logout action unavailable')
    }

    return action
  }

  it('attempts Keycloak logout when EFS logout fails', async () => {
    efsLogoutPostMock.mockRejectedValue(
      new Error('EFS logout failed'),
    )

    const action = await obtainLogoutAction()

    await expect(action()).rejects.toThrow(
      'EFS logout failed',
    )

    expect(efsLogoutPostMock).toHaveBeenCalledWith(
      '/authentication/logout',
    )
    expect(logoutMock).toHaveBeenCalledTimes(1)

    expect(
      efsLogoutPostMock.mock.invocationCallOrder[0],
    ).toBeLessThan(
      logoutMock.mock.invocationCallOrder[0],
    )
  })

  it('propagates Keycloak logout failure after EFS succeeds', async () => {
    logoutMock.mockRejectedValue(
      new Error('Keycloak logout failed'),
    )

    const action = await obtainLogoutAction()

    await expect(action()).rejects.toThrow(
      'Keycloak logout failed',
    )

    expect(efsLogoutPostMock).toHaveBeenCalledTimes(1)
    expect(logoutMock).toHaveBeenCalledTimes(1)
  })

  it('preserves the EFS failure when both logout operations fail', async () => {
    efsLogoutPostMock.mockRejectedValue(
      new Error('EFS logout failed'),
    )
    logoutMock.mockRejectedValue(
      new Error('Keycloak logout failed'),
    )

    const action = await obtainLogoutAction()

    await expect(action()).rejects.toThrow(
      'EFS logout failed',
    )

    expect(efsLogoutPostMock).toHaveBeenCalledTimes(1)
    expect(logoutMock).toHaveBeenCalledTimes(1)
  })
})
