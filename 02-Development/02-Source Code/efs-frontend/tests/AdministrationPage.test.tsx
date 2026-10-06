import {
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

import AdministrationPage from '../src/modules/administration/pages/AdministrationPage'
import { useSystemHealthQuery } from '../src/modules/administration/hooks/useSystemHealthQuery'

vi.mock(
  '../src/modules/administration/hooks/useSystemHealthQuery',
  () => ({
    useSystemHealthQuery: vi.fn(),
  }),
)

vi.mock(
  'react-i18next',
  () => ({
    useTranslation: () => ({
      t: (key: string) => {
        const translations: Record<string, string> = {
          'navigation.administration':
            'Administración',
          'systemHealth.title':
            'Estado del sistema',
          'systemHealth.loading':
            'Consultando estado del sistema',
          'systemHealth.error':
            'No fue posible consultar el estado del sistema.',
          'systemHealth.overallStatus':
            'Estado general',
          'systemHealth.informationStatus':
            'Estado de la información',
          'systemHealth.checkedAt':
            'Última verificación',
          'systemHealth.components':
            'Componentes',
          'systemHealth.noComponents':
            'No hay componentes disponibles para mostrar.',
        }

        return translations[key] ?? key
      },
    }),
  }),
)

describe('AdministrationPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })
afterEach(() => {
  cleanup()
})

  it('presents the canonical System Health response', () => {
    vi.mocked(
      useSystemHealthQuery,
    ).mockReturnValue({
      data: {
        status: 'UP',
        informationStatus: 'COMPLETE',
        components: [
          {
            name: 'database',
            status: 'UP',
          },
          {
            name: 'rabbit',
            status: 'UP',
          },
        ],
        checkedAt: '2026-09-17T07:00:00',
      },
      isLoading: false,
      isError: false,
    } as ReturnType<typeof useSystemHealthQuery>)

    render(
      <AdministrationPage />,
    )

    expect(
      screen.getByRole(
        'heading',
        {
          name: 'Administración',
        },
      ),
    ).toBeTruthy()

    expect(
      screen.getByText(
        'Estado del sistema',
      ),
    ).toBeTruthy()

    expect(
      screen.getByText(
        'COMPLETE',
      ),
    ).toBeTruthy()

    expect(
      screen.getByText(
        'database: UP',
      ),
    ).toBeTruthy()

    expect(
      screen.getByText(
        'rabbit: UP',
      ),
    ).toBeTruthy()
  })

  it('presents an error without inventing fallback health data', () => {
    vi.mocked(
      useSystemHealthQuery,
    ).mockReturnValue({
      data: undefined,
      isLoading: false,
      isError: true,
    } as ReturnType<typeof useSystemHealthQuery>)

    render(
      <AdministrationPage />,
    )

    expect(
      screen.getByText(
        'No fue posible consultar el estado del sistema.',
      ),
    ).toBeTruthy()

    expect(
      screen.queryByText(
        'COMPLETE',
      ),
    ).toBeNull()
  })
})
