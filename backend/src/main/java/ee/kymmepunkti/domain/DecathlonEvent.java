package ee.kymmepunkti.domain;

public enum DecathlonEvent {
    HUNDRED_METRES("100 Metres", 25.4347, 18.0, 1.81, CalculationType.TRACK, MeasurementUnit.SECONDS),
    LONG_JUMP("Long Jump", 0.14354, 220.0, 1.40, CalculationType.JUMP, MeasurementUnit.METRES),
    SHOT_PUT("Shot Put", 51.39, 1.5, 1.05, CalculationType.THROW, MeasurementUnit.METRES),
    HIGH_JUMP("High Jump", 0.8465, 75.0, 1.42, CalculationType.JUMP, MeasurementUnit.METRES),
    FOUR_HUNDRED_METRES("400 Metres", 1.53775, 82.0, 1.81, CalculationType.TRACK, MeasurementUnit.SECONDS),
    HUNDRED_TEN_METRES_HURDLES("110 Metres Hurdles", 5.74352, 28.5, 1.92, CalculationType.TRACK, MeasurementUnit.SECONDS),
    DISCUS_THROW("Discus Throw", 12.91, 4.0, 1.10, CalculationType.THROW, MeasurementUnit.METRES),
    POLE_VAULT("Pole Vault", 0.2797, 100.0, 1.35, CalculationType.JUMP, MeasurementUnit.METRES),
    JAVELIN_THROW("Javelin Throw", 10.14, 7.0, 1.08, CalculationType.THROW, MeasurementUnit.METRES),
    FIFTEEN_HUNDRED_METRES("1500 Metres", 0.03768, 480.0, 1.85, CalculationType.TRACK, MeasurementUnit.SECONDS);

    private final String displayName;
    private final double coefficientA;
    private final double coefficientB;
    private final double coefficientC;
    private final CalculationType calculationType;
    private final MeasurementUnit measurementUnit;

    DecathlonEvent(String displayName,
                   double coefficientA,
                   double coefficientB,
                   double coefficientC,
                   CalculationType calculationType,
                   MeasurementUnit measurementUnit
    ) {
        this.displayName = displayName;
        this.coefficientA = coefficientA;
        this.coefficientB = coefficientB;
        this.coefficientC = coefficientC;
        this.calculationType = calculationType;
        this.measurementUnit = measurementUnit;
    }

    public String displayName() {
        return displayName;
    }

    public double coefficientA() {
        return coefficientA;
    }

    public double coefficientB() {
        return coefficientB;
    }

    public double coefficientC() {
        return coefficientC;
    }

    public CalculationType calculationType() {
        return calculationType;
    }

    public MeasurementUnit measurementUnit() {
        return measurementUnit;
    }
}
