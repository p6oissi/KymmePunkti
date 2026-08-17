export type MeasurementUnit = 'SECONDS' | 'METRES'

export type DecathlonEvent = {
  event: string
  displayName: string
  unit: MeasurementUnit
}

export type PointsResponse = {
  points: number
}

export type DecathlonEventScore = {
  event: string
  displayName: string
  result: number
  unit: MeasurementUnit
  points: number
}

export type FullDecathlonResponse = {
  totalPoints: number
  results: DecathlonEventScore[]
}

export type ApiError = {
  message?: string
}
