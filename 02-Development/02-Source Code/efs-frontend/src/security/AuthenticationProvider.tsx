import {
  createContext,
  type ReactNode,
  useContext,
  useEffect,
  useMemo,
  useState,
} from 'react'

import {
  httpClient,
  subscribeToAuthenticationFailure,
} from '../services/httpClient'
import {
  initializeKeycloak,
  login,
  logout as keycloakLogout,
} from './keycloak'

async function terminateSession(): Promise<void> {
  let efsLogoutFailure: unknown = null

  try {
    await httpClient.post<void>(
      '/authentication/logout',
    )
  } catch (error) {
    efsLogoutFailure = error
  }

  try {
    await keycloakLogout()
  } catch (error) {
    if (efsLogoutFailure !== null) {
      throw efsLogoutFailure
    }

    throw error
  }

  if (efsLogoutFailure !== null) {
    throw efsLogoutFailure
  }
}
type AuthenticationContextValue = {
  authenticated: boolean
  initialized: boolean
  login: () => Promise<void>
  logout: () => Promise<void>
}

const AuthenticationContext =
  createContext<AuthenticationContextValue | null>(
    null,
  )

type AuthenticationProviderProps = {
  children: ReactNode
}

export function AuthenticationProvider({
  children,
}: AuthenticationProviderProps) {
  const [initialized, setInitialized] =
    useState(false)

  const [authenticated, setAuthenticated] =
    useState(false)

  const [
    authenticationFailed,
    setAuthenticationFailed,
  ] = useState(false)

  useEffect(() => {
    let active = true

    void initializeKeycloak()
      .then((result) => {
        if (!active) {
          return
        }

        setAuthenticated(result)
        setInitialized(true)
      })
      .catch(() => {
        if (!active) {
          return
        }

        setAuthenticated(false)
        setAuthenticationFailed(true)
        setInitialized(true)
      })

    return () => {
      active = false
    }
  }, [])

  useEffect(() => {
    return subscribeToAuthenticationFailure(() => {
      setAuthenticated(false)
      setAuthenticationFailed(true)
    })
  }, [])
  const value =
    useMemo<AuthenticationContextValue>(
      () => ({
        authenticated,
        initialized,
        login,
        logout: async () => {
          setAuthenticated(false)
          await terminateSession()
        },
      }),
      [
        authenticated,
        initialized,
      ],
    )

  if (!initialized) {
    return null
  }

  if (authenticationFailed || !authenticated) {
    return null
  }

  return (
    <AuthenticationContext.Provider value={value}>
      {children}
    </AuthenticationContext.Provider>
  )
}

export function useAuthentication():
  AuthenticationContextValue {
  const context =
    useContext(AuthenticationContext)

  if (!context) {
    throw new Error(
      'Authentication context is not available',
    )
  }

  return context
}