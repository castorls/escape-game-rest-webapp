package com.castorls.escapegame.timer;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.castorls.escapegame.SSeService;

@Path("/timer")
public class TimerService {

  private Timer endTimer = null;
  private Instant endInstant = null;

  @Inject
  private SSeService sseService;

  @Context
  private Application  application;

  public TimerService() {
  }

  @POST
  @Path("/resetCounter")
  public Response resetCounter() {
    String endTime = ((Config) application.getProperties().get(this.getClass().getName())).getEndTime();
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_TIME;
    LocalTime localTime = LocalTime.parse(endTime, formatter);
    endInstant = ZonedDateTime.of(LocalDate.now(), localTime, ZoneId.systemDefault()).toInstant();
    if (endTimer == null) {
      endTimer = new Timer();
    }
    final SSeService sseServiceInstance = this.sseService;
    endTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        sseServiceInstance.sendMessage("event", "timerEvent", "timer end", null);
      }
    }, Date.from(endInstant));
    return Response.status(200).build();
  }

  @GET
  @Path("/getCounter")
  @Produces("application/json")
  public Response getCounter() {
    Map<String, String> map = new HashMap<>();
    if (endInstant == null) {
      endInstant = Instant.now().plus(10, ChronoUnit.SECONDS);
      if (endTimer == null) {
        endTimer = new Timer();
        final SSeService sseServiceInstance = this.sseService;
        endTimer.schedule(new TimerTask() {
          @Override
          public void run() {
            sseServiceInstance.sendMessage("event", "timerEvent", "timer end", null);
          }
        }, Date.from(endInstant));
      }
    }
    long nbSeconds = Instant.now().until(endInstant, ChronoUnit.SECONDS);
    if (nbSeconds < 0) {
      nbSeconds = 0;
    }
    map.put("seconds", Long.toString(nbSeconds));
    return Response.status(200).entity(map).build();
  }
}
