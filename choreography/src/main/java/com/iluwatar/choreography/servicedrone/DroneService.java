package com.iluwatar.choreography.servicedrone;

import static com.iluwatar.choreography.Util.performAction;

import com.iluwatar.choreography.MainService;
import com.iluwatar.choreography.SagaService;
import com.iluwatar.choreography.events.DeliveryFailureEvent;
import com.iluwatar.choreography.events.DroneEvent;
import com.iluwatar.choreography.events.PackageEvent;
import com.iluwatar.choreography.response.Response;
import java.util.concurrent.atomic.AtomicInteger;

public class DroneService implements SagaService {

  final AtomicInteger counter = new AtomicInteger();
  private final MainService mainService;

  public DroneService(MainService mainService) {
    this.mainService = mainService;
  }

  int getNextId() {
    return counter.getAndIncrement();
  }

  /**
   * Provisions a drone.
   *
   * @param e the event that was received.
   * @return a response with either the drone id or a failure.
   */
  public Response getDrone(PackageEvent e) {
    int id = getNextId();
    performAction(e, "Contacting drone " + id + " at base...");
    performAction(e, "Drone " + id + " is preparing for pickup...");
    return mainService.post(new DroneEvent(e.getSagaId(), new Drone(id), e.getLocalPackage()));
  }

  @Override
  public void onSagaFailure(DeliveryFailureEvent failureEvent) {
    failureEvent.getDrone().ifPresent(drone ->
        performAction(failureEvent, "Setting drone " + drone.getId() + " to standby..."));
  }
}
