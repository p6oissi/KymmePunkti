import { useEffect, useState } from 'react'
import FullDecathlonCalculator from './FullDecathlonCalculator'
import SingleEventCalculator from './SingleEventCalculator'
import type { DecathlonEvent } from './types'
import './App.css'

type CalculatorMode = 'single' | 'full'

function App() {
  const [mode, setMode] = useState<CalculatorMode>('single')
  const [events, setEvents] = useState<DecathlonEvent[]>([])
  const [eventsLoading, setEventsLoading] = useState(true)
  const [eventsError, setEventsError] = useState('')

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

  return (
    <main className={`page page--${mode}`}>
      <section className={`calculator calculator--${mode}`}>
        <div className="mode-switch" role="group" aria-label="Calculator mode">
          <button
            type="button"
            className={mode === 'single' ? 'mode-switch__button mode-switch__button--active' : 'mode-switch__button'}
            aria-pressed={mode === 'single'}
            onClick={() => setMode('single')}
          >
            Single event
          </button>
          <button
            type="button"
            className={mode === 'full' ? 'mode-switch__button mode-switch__button--active' : 'mode-switch__button'}
            aria-pressed={mode === 'full'}
            onClick={() => setMode('full')}
          >
            Full decathlon
          </button>
        </div>

        {eventsLoading && <p className="status" role="status">Loading events...</p>}

        {!eventsLoading && eventsError && (
          <div className="load-error" role="alert">
            <p>{eventsError}</p>
            <button className="button button--secondary" type="button" onClick={loadEvents}>
              Try again
            </button>
          </div>
        )}

        {!eventsLoading && !eventsError && mode === 'single' && (
          <SingleEventCalculator events={events} />
        )}

        {!eventsLoading && !eventsError && mode === 'full' && (
          <FullDecathlonCalculator events={events} />
        )}
      </section>
    </main>
  )
}

export default App
