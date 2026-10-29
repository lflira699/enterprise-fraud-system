import {
  cleanup,
  fireEvent,
  render,
  screen,
  waitFor,
} from '@testing-library/react'
import {
  afterEach,
  describe,
  expect,
  it,
  vi,
} from 'vitest'

const logoutMock = vi.hoisted(() => vi.fn())

vi.mock('../src/security/AuthenticationProvider', () => ({
  useAuthentication: () => ({
    logout: logoutMock,
  }),
}))

import '../src/configuration/i18n'
import AppLayout from '../src/shared/layouts/AppLayout'

afterEach(() => {
  cleanup()
  vi.restoreAllMocks()
  logoutMock.mockReset()
})

describe('AppLayout logout interaction', () => {
  it('invokes the existing logout action from the top bar', async () => {
    logoutMock.mockResolvedValue(undefined)

    render(
      <AppLayout navigation={<span>Navigation</span>}>
        <div>Protected content</div>
      </AppLayout>,
    )

    expect(
      screen.getByText('Protected content'),
    ).toBeTruthy()

    fireEvent.click(
      screen.getByRole('button', {
        name: 'Cerrar sesión',
      }),
    )

    await waitFor(() => {
      expect(logoutMock)
        .toHaveBeenCalledTimes(1)
    })
  })

  it('reports logout failure without presenting success', async () => {
    logoutMock.mockRejectedValue(
      new Error('Logout failed'),
    )

    const alertMock =
      vi.spyOn(window, 'alert')
        .mockImplementation(() => undefined)

    render(
      <AppLayout navigation={<span>Navigation</span>}>
        <div>Protected content</div>
      </AppLayout>,
    )

    fireEvent.click(
      screen.getByRole('button', {
        name: 'Cerrar sesión',
      }),
    )

    await waitFor(() => {
      expect(alertMock).toHaveBeenCalledWith(
        'No fue posible completar el cierre de sesión.',
      )
    })

    expect(logoutMock)
      .toHaveBeenCalledTimes(1)
  })
})
