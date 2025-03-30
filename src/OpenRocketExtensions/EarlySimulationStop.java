package OpenRocketExtensions;

import net.sf.openrocket.simulation.FlightEvent;
import net.sf.openrocket.simulation.SimulationConditions;
import net.sf.openrocket.simulation.SimulationStatus;
import net.sf.openrocket.simulation.exception.SimulationException;
import net.sf.openrocket.simulation.extension.AbstractSimulationExtension;
import net.sf.openrocket.simulation.listeners.AbstractSimulationListener;
import net.sf.openrocket.simulation.listeners.SimulationListener;

public class EarlySimulationStop extends AbstractSimulationExtension {
    private final SimulationListener listener;

    public EarlySimulationStop() {
        this.listener = new StopEventSimulationListener(FlightEvent.Type.IGNITION,
                Double.POSITIVE_INFINITY);
    }

    public EarlySimulationStop(FlightEvent.Type trig_event,
                               double delay_time) {
        this.listener = new StopEventSimulationListener(trig_event, delay_time);
    }

    @Override
    public void initialize(SimulationConditions conditions) throws SimulationException {
        conditions.getSimulationListenerList().add(this.listener);
    }
}

class StopEventSimulationListener extends AbstractSimulationListener {
    private final double delay_time;
    private final FlightEvent.Type trig_event;
    private double trig_time = Double.NaN;

    public StopEventSimulationListener(FlightEvent.Type trig_event, double delay_time) {
        this.trig_event = trig_event;
        this.delay_time = delay_time;
    }

    @Override
    public boolean handleFlightEvent(SimulationStatus status, FlightEvent event) {
        if (event.getType() == this.trig_event) {
            this.trig_time = status.getSimulationTime();
        }
        return true;
    }

    @Override
    public void postStep(SimulationStatus status) {
        if (status.getSimulationTime() - this.trig_time >= this.delay_time) {
            FlightEvent stop = new FlightEvent(FlightEvent.Type.SIMULATION_END,
                    status.getSimulationTime(), null);
            status.getEventQueue().add(stop);
        }
    }
}


