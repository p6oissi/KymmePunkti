import { useEffect, useState, type SyntheticEvent } from 'react'
import './App.css'

type MeasurementUnit = 'SECONDS' | 'METRES'

type DecathlonEvent = {
  event: string
  displayName: string
  unit: MeasurementUnit
}

type PointsResponse = {
  points: number
}

type ApiError = {
  message?: string
}

function App() {
  const [events, setEvents] = useState<DecathlonEvent[]>([])
  const [eventsLoading, setEventsLoading] = useState(true)
  const [eventsError, setEventsError] = useState('')
  const [selectedEventName, setSelectedEventName] = useState('')
  const [result, setResult] = useState('')
  const [score, setScore] = useState<number | null>(null)
  const [calculationError, setCalculationError] = useState('')
  const [calculating, setCalculating] = useState(false)

  const selectedEvent = events.find(
    (event) => event.event === selectedEventName,
  )
  const hasScore = score !== null

  useEffect(() => {
    void loadEvents()
  }, [])

  async function loadEvents() {
    setEventsLoading(true)
    setEventsError('')

    try {
      const response = await fetch('/api/v1/decathlon/events')
      if (!response.ok) {
        setEventsError('Could not load the decathlon events.')
        return
      }

      const responseEvents = (await response.json()) as DecathlonEvent[]
      setEvents(responseEvents)
    } catch {
      setEventsError('Could not load the decathlon events.')
    } finally {
      setEventsLoading(false)
    }
  }

  async function calculatePoints(event: SyntheticEvent<HTMLFormElement>) {
    event.preventDefault()
    setCalculationError('')

    const numericResult = Number(result)
    if (!selectedEvent || !Number.isFinite(numericResult) || numericResult <= 0) {
      setCalculationError('Choose an event and enter a result greater than zero.')
      return
    }

    setCalculating(true)

    try {
      const response = await fetch('/api/v1/decathlon/points', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          event: selectedEvent.event,
          result: numericResult,
        }),
      })
      const body = (await response.json()) as PointsResponse & ApiError

      if (!response.ok) {
        setCalculationError(body.message || 'Could not calculate the score.')
        return
      }

      setScore(body.points)
    } catch (error) {
      setCalculationError(
        error instanceof Error ? error.message : 'Could not calculate the score.',
      )
    } finally {
      setCalculating(false)
    }
  }

  function retry() {
    setSelectedEventName('')
    setResult('')
    setScore(null)
    setCalculationError('')
  }

  const resultPlaceholder = selectedEvent
    ? `Enter ${selectedEvent.unit === 'SECONDS' ? 'seconds' : 'metres'}`
    : 'Choose an event first'

  return (
    <main className="page">
      <section className={`calculator ${hasScore ? 'calculator--result' : ''}`}>
        <header className="calculator__header">
          <p className="eyebrow">DECATHLON</p>
          <h1>Points calculator</h1>
          <p>Choose an event, enter the performance, and get the score.</p>
        </header>

        {eventsLoading && <p className="status" role="status">Loading events...</p>}

        {!eventsLoading && eventsError && (
          <div className="load-error" role="alert">
            <p>{eventsError}</p>
            <button className="button button--secondary" type="button" onClick={loadEvents}>
              Try again
            </button>
          </div>
        )}

        {!eventsLoading && !eventsError && (
          <>
            <form className="calculator__form" onSubmit={calculatePoints} noValidate>
              <div className="field">
                <label htmlFor="event">Event</label>
                <select
                  id="event"
                  value={selectedEventName}
                  onChange={(event) => {
                    setSelectedEventName(event.target.value)
                    setCalculationError('')
                  }}
                  disabled={hasScore || calculating}
                  required
                >
                  <option value="">Choose an event</option>
                  {events.map((event) => (
                    <option key={event.event} value={event.event}>
                      {event.displayName}
                    </option>
                  ))}
                </select>
              </div>

              <div className="field">
                <label htmlFor="result">Result</label>
                <input
                  id="result"
                  type="number"
                  inputMode="decimal"
                  min="0"
                  step="any"
                  value={result}
                  placeholder={resultPlaceholder}
                  onChange={(event) => {
                    setResult(event.target.value)
                    setCalculationError('')
                  }}
                  disabled={!selectedEvent || hasScore || calculating}
                  aria-invalid={Boolean(calculationError)}
                  aria-describedby={calculationError ? 'calculation-error' : undefined}
                  required
                />
              </div>

              {!hasScore && (
                <button className="button button--primary" type="submit" disabled={calculating}>
                  {calculating ? 'Calculating...' : 'Calculate'}
                </button>
              )}
            </form>

            {calculationError && (
              <p id="calculation-error" className="form-error" role="alert">
                {calculationError}
              </p>
            )}

            {hasScore && (
              <div className="score" aria-live="polite">
                <p className="score__label">YOUR SCORE</p>
                <output className="score__value">{score}</output>
                <p className="score__unit">POINTS</p>
                <button className="button button--secondary" type="button" onClick={retry}>
                  Retry
                </button>
              </div>
            )}
          </>
        )}
      </section>
    </main>
  )
}

export default App
