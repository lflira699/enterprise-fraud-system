import {
  act,
  cleanup,
  render,
  screen,
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
  keycloakLoginMock,
  keycloakLogoutMock,
  efsLogoutMock,
  subscribeMock,
  listeners,
} = vi.hoisted(() => ({
  initializeKeycloakMock: vi.fn(),
  keycloakLoginMock: vi.fn(),
  keycloakLogoutMock: vi.fn(),
  efsLogoutMock: vi.fn(),
  subscribeMock: vi.fn(),
  listeners: new Set<() => void>(),
}))

vi.mock('../src/security/keycloak', () => ({
  initializeKeycloak: initializeKeycloakMock,
  login: keycloakLoginMock,
  logout: keycloakLogoutMock,
}))

vi.mock('../src/services/httpClient', () => ({
  httpClient: {
    post: efsLogoutMock,
  },
  subscribeToAuthenticationFailure: subscribeMock,
}))

import {
  AuthenticationProvider,
} from '../src/security/AuthenticationProvider'

beforeEach(() => {
  listeners.clear()

  initializeKeycloakMock.mockReset()
  keycloakLoginMock.mockReset()
  keycloakLogoutMock.mockReset()
  efsLogoutMock.mockReset()
  subscribeMock.mockReset()

  initializeKeycloakMock.mockResolvedValue(true)

  subscribeMock.mockImplementation(
    (listener: () => void) => {
      listeners.add(listener)

      return () => {
        listeners.delete(listener)
      }
    },
  )
})

afterEach(() => {
  cleanup()
  listeners.clear()
  vi.restoreAllMocks()
})

describe('AuthenticationProvider session failure synchronization', () => {
  it('removes protected content after an authentication failure', async () => {
    render(
      <AuthenticationProvider>
        <div>Protected content</div>
      </AuthenticationProvider>,
    )

    expect(
      await screen.findByText('Protected content'),
    ).toBeTruthy()

    expect(listeners.size).toBe(1)

    act(() => {
      for (const listener of listeners) {
        listener()
      }
    })

    expect(
      screen.queryByText('Protected content'),
    ).toBeNull()

    expect(keycloakLoginMock).not.toHaveBeenCalled()
    expect(keycloakLogoutMock).not.toHaveBeenCalled()
    expect(efsLogoutMock).not.toHaveBeenCalled()
  })

  it('keeps authenticated content when no failure is notified', async () => {
    render(
      <AuthenticationProvider>
        <div>Protected content</div>
      </AuthenticationProvider>,
    )

    expect(
      await screen.findByText('Protected content'),
    ).toBeTruthy()

    expect(listeners.size).toBe(1)

    expect(keycloakLoginMock).not.toHaveBeenCalled()
    expect(keycloakLogoutMock).not.toHaveBeenCalled()
  })

  it('unsubscribes when the provider is unmounted', async () => {
    const view = render(
      <AuthenticationProvider>
        <div>Protected content</div>
      </AuthenticationProvider>,
    )

    expect(
      await screen.findByText('Protected content'),
    ).toBeTruthy()

    expect(listeners.size).toBe(1)

    view.unmount()

    expect(listeners.size).toBe(0)
  })
})
