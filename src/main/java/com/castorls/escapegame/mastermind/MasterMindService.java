package com.castorls.escapegame.mastermind;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.castorls.escapegame.SSeService;

@Path("/mastermind")
public class MasterMindService {

  @Inject
  private SSeService sseService;

  public MasterMindService() {
  }

  @GET
  @Path("/getState")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getState() {
    Map<String, Object> responseMap = new HashMap<>();
    Status status = Status.OK;
    try {
      MasterMindManager mgr = MasterMindManager.getInstance();
      String token = mgr.getResultToken();
      responseMap.put("token", token);
      if (token != null && "".equals(token.trim())) {
        this.sseService.sendMessage("event", "masterMindEvent", "solved", null);
        responseMap.put("solution",mgr.getTarget());
      }
    } catch (Exception e) {
      responseMap.put("errorClass", e.getClass().getName());
      responseMap.put("errorMessage", e.getMessage());
      status = Response.Status.INTERNAL_SERVER_ERROR;
    }
    return Response.status(status).entity(responseMap).build();
  }

  @POST
  @Path("/sendChoice")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response consumeChoice(Choice choice) {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      MasterMindManager masterMindMgr = MasterMindManager.getInstance();
      Result result = masterMindMgr.checkChoice(choice);
      String token = result.getSolvedToken();
      if (token != null && "".equals(token.trim())) {
        this.sseService.sendMessage("event", "masterMindEvent", "solved", null);
      }
      return Response.status(200).entity(result).build();
    } catch (Exception e) {
      responseMap.put("errorClass", e.getClass().getName());
      responseMap.put("errorMessage", e.getMessage());
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseMap).build();
    }
  }

  @POST
  @Path("/reset")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response consumeReset() {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      MasterMindManager.getInstance().resetTarget();
      return Response.status(200).build();
    } catch (Exception e) {
      responseMap.put("errorClass", e.getClass().getName());
      responseMap.put("errorMessage", e.getMessage());
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseMap).build();
    }
  }
}
