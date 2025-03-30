package OpenRocketExtensions;
import net.sf.openrocket.models.wind.WindModel;
import net.sf.openrocket.simulation.SimulationConditions;
import net.sf.openrocket.simulation.exception.SimulationException;
import net.sf.openrocket.simulation.extension.AbstractSimulationExtension;
import net.sf.openrocket.util.Coordinate;
import net.sf.openrocket.util.MathUtil;

import java.util.List;

public class TabulatedWind extends AbstractSimulationExtension {
    private final TabulatedWindModel model;

    public TabulatedWind(double[] altitudes, double[] ugrd, double[] vgrd) {
        this.model = new TabulatedWindModel(altitudes, ugrd, vgrd);
    }

    @Override
    public void initialize(SimulationConditions conditions) throws SimulationException {
        conditions.setWindModel(this.model);
    }
}


class TabulatedWindModel implements WindModel {
    private final List<Double> altitudes;
    private final List<Double> ugrd;
    private final List<Double> vgrd;

    /**
     * Create tabulated wind model
     * @param altitudes Altitudes [m ASL]
     * @param ugrd Eastward component of wind [m/s]
     * @param vgrd Northward component of wind [m/s]
     */
    public TabulatedWindModel(double[] altitudes, double[] ugrd, double[] vgrd) {
        Common.mustBeSorted(altitudes);
        Common.mustBeFinite(altitudes);
        Common.mustBeFinite(ugrd);
        Common.mustBeFinite(vgrd);

        this.altitudes = Common.asArrayList(altitudes);
        this.ugrd = Common.asArrayList(ugrd);
        this.vgrd = Common.asArrayList(vgrd);
    }

    @Override
    public Coordinate getWindVelocity(double time, double altitude) {
        double usample = MathUtil.interpolate(this.altitudes, this.ugrd, altitude);
        double vsample = MathUtil.interpolate(this.altitudes, this.vgrd, altitude);

        // the OpenRocket convention is to specify where the wind blows *to*
        // (so that +x blows the rocket West), but the NWP data specify how
        // the air parcels move, so where the wind blows *to*, so that
        return new Coordinate(-usample, -vsample);
    }

    @Override
    public int getModID() {
        return 0;
    }
}
