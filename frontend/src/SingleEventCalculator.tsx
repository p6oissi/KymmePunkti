import { useState, type SyntheticEvent } from 'react'
import type { ApiError, DecathlonEvent, PointsResponse } from './types'

type SingleEventCalculatorProps = {
  events: DecathlonEvent[]
}

function SingleEventCalculator({ events }: SingleEventCalculatorProps) {
  const [selectedEventName, setSelectedEventName] = useState('')
  const [result, setResult] = useState('')
  const [score, setScore] = useState<number | null>(null)
  const [error, setError] = useState('')
  const [calculating, setCalculating] = useState(false)

  const selectedEvent = events.find(
    (event) => event.event === selectedEventName,
  )
  const hasScore = score !== null

  async function calculatePoints(event: SyntheticEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')

    const numericResult = Number(result)
    if (!selectedEvent || !Number.isFinite(numericResult) || numericResult <= 0) {
      setError('Choose an event and enter a result greater than zero.')
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
        setError(body.message || 'Could not calculate the score.')
        return
      }

      setScore(body.points)
    } catch {
      setError('Could not calculate the score.')
    } finally {
      setCalculating(false)
    }
  }

  function retry() {
    setSelectedEventName('')
    setResult('')
    setScore(null)
    setError('')
  }

  const resultPlaceholder = selectedEvent
    ? `Enter ${selectedEvent.unit === 'SECONDS' ? 'seconds' : 'metres'}`
    : 'Choose an event first'

  return (
    <div className={`calculator__content ${hasScore ? 'calculator__content--result' : ''}`}>
      <header className="calculator__header">
        <p className="eyebrow">DECATHLON</p>
        <h1>Points calculator</h1>
        <p>Choose an event, enter the performance, and get the score.</p>
      </header>

      <form className="calculator__form" onSubmit={calculatePoints} noValidate>
        <div className="field">
          <label htmlFor="event">Event</label>
          <select
            id="event"
            value={selectedEventName}
            onChange={(event) => {
              setSelectedEventName(event.target.value)
              setError('')
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
              setError('')
            }}
            disabled={!selectedEvent || hasScore || calculating}
            aria-invalid={Boolean(error)}
            aria-describedby={error ? 'single-calculation-error' : undefined}
            required
          />
        </div>

        {!hasScore && (
          <button className="button button--primary" type="submit" disabled={calculating}>
            {calculating ? 'Calculating...' : 'Calculate'}
          </button>
        )}
      </form>

      {error && (
        <p id="single-calculation-error" className="form-error" role="alert">
          {error}
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
    </div>
  )
}

export default SingleEventCalculator
