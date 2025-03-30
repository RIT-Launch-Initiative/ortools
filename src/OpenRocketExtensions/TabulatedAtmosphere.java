package OpenRocketExtensions;

import net.sf.openrocket.models.atmosphere.AtmosphericConditions;
import net.sf.openrocket.models.atmosphere.AtmosphericModel;
import net.sf.openrocket.simulation.SimulationConditions;
import net.sf.openrocket.simulation.exception.SimulationException;
import net.sf.openrocket.simulation.extension.AbstractSimulationExtension;
import net.sf.openrocket.util.MathUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class TabulatedAtmosphere extends AbstractSimulationExtension {
    private final TabulatedAtmosphericModel atmosphere;

    public TabulatedAtmosphere(double[] altitudes, double[] temperatures,
                               double[] pressures) {
        this.atmosphere = new TabulatedAtmosphericModel(altitudes, temperatures,
                pressures);
    }

    @Override
    public String getName() {
        return String.format("Tabulated atmospheric model with %d layers between %.1f and %.1f km",
                atmosphere.getNumLayers(),
                atmosphere.getMinAltitude() / 1000.0, atmosphere.getMaxAltitude() / 1000);
    }

    @Override
    public void initialize(SimulationConditions conditions) throws SimulationException {
        conditions.setAtmosphericModel(this.atmosphere);
    }
}

class TabulatedAtmosphericModel implements AtmosphericModel {
    private final double max_altitude = Double.NEGATIVE_INFINITY;
    private final List<Double> altitudes;
    // AbstractSimulationStepper.modelAtmosphericConditions adds the
    // launch altitude to current vertical position to get sea-level
    // altitude
    private final List<Double> temperatures;
    private final List<Double> pressures;

    /**
     * Construct a tabulated atmospheric model that linearly interpolates
     * between the given layers.
     *
     * @param altitudes Altitudes ASL [m], must be sorted and start at zero
     * @param temperatures Absolute temperatures [K], strictly positive
     * @param pressures Absolute pressures [Pa], strictly positive
     */
    public TabulatedAtmosphericModel(double[] altitudes, double[] temperatures,
                               double[] pressures) {
        // Validate inputs
        if (altitudes.length != temperatures.length || altitudes.length != pressures.length) {
            throw new IllegalArgumentException("Atmospheric condition inputs " +
                    "must have equal length.");
        }

        Common.mustBeSorted(altitudes);
        Common.mustBeFinite(altitudes);

        Common.mustBePositive(temperatures);
        Common.mustBePositive(pressures);
        Common.mustBeFinite(temperatures);
        Common.mustBeFinite(pressures);

        this.altitudes = Common.asArrayList(altitudes);
        this.temperatures = Common.asArrayList(temperatures);
        this.pressures = Common.asArrayList(pressures);

    }

    @Override
    public AtmosphericConditions getConditions(double altitude) {
        double temperature = MathUtil.interpolate(this.altitudes,
                this.temperatures, altitude);
        double pressure = MathUtil.interpolate(this.altitudes,
                this.pressures, altitude);

        return new AtmosphericConditions(temperature, pressure);
    }

    @Override
    public int getModID() {
        return 0;
    }

    public int getNumLayers() {
        return this.altitudes.size();
    }

    public double getMinAltitude() {
        return Collections.min(this.altitudes);
    }
    public double getMaxAltitude() {
        return Collections.max(this.altitudes);
    }
}
