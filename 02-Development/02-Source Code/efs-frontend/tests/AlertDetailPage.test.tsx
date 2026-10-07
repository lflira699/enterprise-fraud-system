import {
  cleanup,
  fireEvent,
  render,
  screen,
} from '@testing-library/react'
import {
  MemoryRouter,
  Route,
  Routes,
  useLocation,
} from 'react-router-dom'
import {
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from 'vitest'

import '../src/configuration/i18n'
import { useAlertQuery } from '../src/modules/alerts/hooks/useAlertsQuery'
import AlertDetailPage from '../src/modules/alerts/pages/AlertDetailPage'
import { HttpError } from '../src/services/httpClient'

vi.mock(
  '../src/modules/alerts/hooks/useAlertsQuery',
  () => ({
    useAlertQuery: vi.fn(),
  }),
)

const useAlertQueryMock =
  vi.mocked(
    useAlertQuery,
  )

const ALERT_ID =
  '11111111-1111-1111-1111-111111111111'

const ALERT = {
  alertId: ALERT_ID,
  alertReference: 'ALT-001',
  customerId:
    '22222222-2222-2222-2222-222222222222',
  transactionId:
    '33333333-3333-3333-3333-333333333333',
  decisionId:
    '44444444-4444-4444-4444-444444444444',
  riskAssessmentId:
    '55555555-5555-5555-5555-555555555555',
  scenarioId:
    '66666666-6666-6666-6666-666666666666',
  ruleId:
    '77777777-7777-7777-7777-777777777777',
  alertType: 'FRAUD',
  category: 'ATO',
  severity: 'HIGH',
  priority: 'HIGH',
  priorityScore: 95,
  status: 'NEW',
  title: 'Alerta de prueba',
  description:
    'Descripción de prueba',
  riskScore: 88,
  correlationId:
    '88888888-8888-8888-8888-888888888888',
  assignedTo: null,
  assignedTeam:
    'FRAUD_INVESTIGATION',
  dueAt: null,
  generatedAt:
    '2026-10-07T10:00:00',
  closedAt: null,
  closureReason: null,
  createdAt:
    '2026-10-07T10:00:00',
  updatedAt:
    '2026-10-07T10:00:00',
  recordVersion: 1,
}

function renderAlertDetail() {
  return render(
    <MemoryRouter
      initialEntries={[
        `/alerts/${ALERT_ID}`,
      ]}
    >
      <Routes>
        <Route
          path="/alerts/:alertId"
          element={<AlertDetailPage />}
        />
      </Routes>
    </MemoryRouter>,
  )
}

beforeEach(() => {
  useAlertQueryMock.mockReset()
})

afterEach(() => {
  cleanup()
  vi.restoreAllMocks()
})

describe(
  'AlertDetailPage',
  () => {
    it(
      'retrieves Alert Detail using the stable alertId from the route',
      () => {
        useAlertQueryMock
          .mockReturnValue(
            {
              data: ALERT,
              isError: false,
              isFetching: false,
              error: null,
            } as unknown as ReturnType<
              typeof useAlertQuery
            >,
          )

        renderAlertDetail()

        expect(
          useAlertQueryMock,
        ).toHaveBeenCalledWith(
          ALERT_ID,
        )

        expect(
          screen.getByRole(
            'heading',
            {
              name:
                'Detalle de la alerta',
            },
          ),
        ).toBeTruthy()

        expect(
          screen.getByText(
            'ALT-001',
          ),
        ).toBeTruthy()

        expect(
          screen.getByText(
            'Alerta de prueba',
          ),
        ).toBeTruthy()
      },
    )

    it(
      'presents the canonical not-found state for a missing alert',
      () => {
        useAlertQueryMock
          .mockReturnValue(
            {
              data: undefined,
              isError: true,
              isFetching: false,
              error:
                new HttpError(
                  404,
                  'Not Found',
                  null,
                ),
            } as unknown as ReturnType<
              typeof useAlertQuery
            >,
          )

        renderAlertDetail()

        expect(
          screen.getByText(
            'La alerta solicitada no está disponible o no existe.',
          ),
        ).toBeTruthy()
      },
    )

    it(
      'presents the authorization state without exposing internal details',
      () => {
        useAlertQueryMock
          .mockReturnValue(
            {
              data: undefined,
              isError: true,
              isFetching: false,
              error:
                new HttpError(
                  403,
                  'Forbidden',
                  null,
                ),
            } as unknown as ReturnType<
              typeof useAlertQuery
            >,
          )

        renderAlertDetail()

        expect(
          screen.getByText(
            'No cuenta con autorización para consultar esta alerta.',
          ),
        ).toBeTruthy()
      },
    )

    it(
      'returns to Alerts List preserving the received search context',
      () => {
        const alertSearchContext = {
          filterDraft: {
            status:
              'IN_PROGRESS',
          },
          appliedFilters: {
            status:
              'IN_PROGRESS',
          },
          paginationModel: {
            page: 2,
            pageSize: 50,
          },
          sortModel: [
            {
              field:
                'riskScore',
              sort: 'asc',
            },
          ],
        }

        useAlertQueryMock
          .mockReturnValue(
            {
              data: ALERT,
              isError: false,
              isFetching: false,
              error: null,
            } as unknown as ReturnType<
              typeof useAlertQuery
            >,
          )

        let returnedState:
          unknown = null

        function AlertsProbe() {
          const location =
            useLocation()

          returnedState =
            location.state

          return (
            <div>
              Alerts List Probe
            </div>
          )
        }

        render(
          <MemoryRouter
            initialEntries={[
              {
                pathname:
                  `/alerts/${ALERT_ID}`,
                state: {
                  alertSearchContext,
                },
              },
            ]}
          >
            <Routes>
              <Route
                path="/alerts/:alertId"
                element={<AlertDetailPage />}
              />

              <Route
                path="/alerts"
                element={<AlertsProbe />}
              />
            </Routes>
          </MemoryRouter>,
        )

        fireEvent.click(
          screen.getByRole(
            'button',
            {
              name:
                'Volver al listado',
            },
          ),
        )

        expect(
          screen.getByText(
            'Alerts List Probe',
          ),
        ).toBeTruthy()

        expect(
          returnedState,
        ).toEqual({
          alertSearchContext,
        })
      },
    )
  },
)
