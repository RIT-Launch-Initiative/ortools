package OpenRocketExtensions;
import net.sf.openrocket.aerodynamics.AerodynamicForces;
import net.sf.openrocket.aerodynamics.BarrowmanCalculator;
import net.sf.openrocket.aerodynamics.FlightConditions;
import net.sf.openrocket.logging.WarningSet;
import net.sf.openrocket.rocketcomponent.FlightConfiguration;
import net.sf.openrocket.simulation.SimulationConditions;
import net.sf.openrocket.simulation.exception.SimulationException;
import net.sf.openrocket.simulation.extension.AbstractSimulationExtension;
import net.sf.openrocket.util.MathUtil;

import java.util.List;

public class TabulatedDrag extends AbstractSimulationExtension {
    private final TabulatedDragCalculator calculator;

    /**
     * Create a simulation extension to change the aerodynamic calculator to
     * the tabulated drag calculator
     *
     * @param mach  Mach numbers
     * @param coeff Drag coefficient samples
     */
    public TabulatedDrag(double[] mach, double[] coeff) {
        this.calculator = new TabulatedDragCalculator(mach, coeff);
    }

    @Override
    public void initialize(SimulationConditions conditions) throws SimulationException {
        conditions.setAerodynamicCalculator(this.calculator);
    }
}


class TabulatedDragCalculator extends BarrowmanCalculator {
    /**
     * Override the Barrowman drag coefficient calculation with a linear
     * interpolation
     */
    private final List<Double> mach;
    private final List<Double> coeff;

    /**
     * Create a tabulated drag calculator
     * @param mach  Mach numbers
     * @param coeff Drag coefficient samples
     */
    public TabulatedDragCalculator(double[] mach, double[] coeff) {
        if (mach.length != coeff.length) {
            throw new IllegalArgumentException("Mach number and drag " +
                    "coefficient must have the same length");
        }

        Common.mustBeSorted(mach);
        Common.mustBeNonNegative(mach);
        Common.mustBeFinite(mach);
        Common.mustBePositive(coeff);
        Common.mustBeFinite(coeff);

        this.mach = Common.asArrayList(mach);
        this.coeff = Common.asArrayList(coeff);
    }

    @Override
    public AerodynamicForces getAerodynamicForces(FlightConfiguration configuration,
                                                  FlightConditions conditions, WarningSet warnings) {
        AerodynamicForces forces = super.getAerodynamicForces(configuration, conditions, warnings);
        double drag = MathUtil.interpolate(this.mach, this.coeff, conditions.getMach());
        forces.setCD(drag);
        return forces;
    }
}
