import { useState, type SyntheticEvent } from 'react'
import type { ApiError, DecathlonEvent, FullDecathlonResponse } from './types'

type FullDecathlonCalculatorProps = {
  events: DecathlonEvent[]
}

function FullDecathlonCalculator({ events }: FullDecathlonCalculatorProps) {
  const [results, setResults] = useState<Record<string, string>>({})
  const [score, setScore] = useState<FullDecathlonResponse | null>(null)
  const [error, setError] = useState('')
  const [calculating, setCalculating] = useState(false)

  async function calculateTotal(event: SyntheticEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')

    const performances = events.map((decathlonEvent) => ({
      event: decathlonEvent.event,
      result: Number(results[decathlonEvent.event]),
    }))
    const hasInvalidResult = performances.some(
      (performance) => !Number.isFinite(performance.result) || performance.result <= 0,
    )

    if (hasInvalidResult) {
      setError('Enter a result greater than zero for every event.')
      return
    }

    setCalculating(true)

    try {
      const response = await fetch('/api/v1/decathlon/total', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ results: performances }),
      })
      const body = (await response.json()) as FullDecathlonResponse & ApiError

      if (!response.ok) {
        setError(body.message || 'Could not calculate the total score.')
        return
      }

      setScore(body)
    } catch {
      setError('Could not calculate the total score.')
    } finally {
      setCalculating(false)
    }
  }

  function updateResult(eventName: string, value: string) {
    setResults((currentResults) => ({
      ...currentResults,
      [eventName]: value,
    }))
    setError('')
  }

  function retry() {
    setResults({})
    setScore(null)
    setError('')
  }

  return (
    <div className="full-calculator">
      <header className="calculator__header calculator__header--full">
        <p className="eyebrow">FULL DECATHLON</p>
        <h1>Total calculator</h1>
        <p>Enter a performance for every event to calculate the grand total.</p>
      </header>

      {!score && (
        <form className="full-form" onSubmit={calculateTotal} noValidate>
          <div className="full-form__fields">
            {events.map((decathlonEvent, index) => {
              const unit = decathlonEvent.unit === 'SECONDS' ? 'seconds' : 'metres'

              return (
                <div className="field full-form__field" key={decathlonEvent.event}>
                  <label htmlFor={`result-${decathlonEvent.event}`}>
                    <span>{index + 1}. {decathlonEvent.displayName}</span>
                    <span className="field__unit">{unit}</span>
                  </label>
                  <input
                    id={`result-${decathlonEvent.event}`}
                    type="number"
                    inputMode="decimal"
                    min="0"
                    step="any"
                    value={results[decathlonEvent.event] ?? ''}
                    placeholder={`Enter ${unit}`}
                    onChange={(event) => updateResult(decathlonEvent.event, event.target.value)}
                    disabled={calculating}
                    aria-invalid={Boolean(error)}
                    aria-describedby={error ? 'full-calculation-error' : undefined}
                    required
                  />
                </div>
              )
            })}
          </div>

          <button className="button button--primary" type="submit" disabled={calculating}>
            {calculating ? 'Calculating...' : 'Calculate total'}
          </button>
        </form>
      )}

      {error && (
        <p id="full-calculation-error" className="form-error" role="alert">
          {error}
        </p>
      )}

      {score && (
        <div className="full-score" aria-live="polite">
          <p className="score__label">GRAND TOTAL</p>
          <output className="score__value">{score.totalPoints}</output>
          <p className="score__unit">POINTS</p>

          <ol className="score-breakdown">
            {score.results.map((eventScore) => (
              <li key={eventScore.event}>
                <span>{eventScore.displayName}</span>
                <strong>{eventScore.points}</strong>
              </li>
            ))}
          </ol>

          <button className="button button--secondary" type="button" onClick={retry}>
            Retry
          </button>
        </div>
      )}
    </div>
  )
}

export default FullDecathlonCalculator
