package com.castorls.escapegame;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public abstract class AbstractService {

  @Inject
  protected SSeService sseService;

  @Context
  protected Application application;


  public abstract String getSseEventName();

  protected void sendSseSolvedEvent() {
    this.sseService.sendMessage("event",getSseEventName(), "solved", null);
  }

  protected Response generateErrorResponse(Exception e) {
    return generateErrorResponse(e.getClass().getName(), e.getMessage());
  }

  protected Response generateErrorResponse(String errorClass, String message) {
    return generateErrorResponse(errorClass, message, Response.Status.INTERNAL_SERVER_ERROR);
  }

  protected Response generateErrorResponse(String errorClass, String message, Status status) {
    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put("errorClass", errorClass);
    responseMap.put("errorMessage", message);
    return Response.status(status).entity(responseMap).build();
  }
}
