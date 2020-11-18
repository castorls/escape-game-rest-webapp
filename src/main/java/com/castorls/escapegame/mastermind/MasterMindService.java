package com.castorls.escapegame.mastermind;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.castorls.escapegame.AbstractService;

@Path("/mastermind")
public class MasterMindService extends AbstractService{

  public MasterMindService() {
  }

  @Override
  public String getSseEventName() {
    return "masterMindEvent";
  }

  @GET
  @Path("/getState")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getState() {
    try {
      Map<String, Object> responseMap = new HashMap<>();
      MasterMindManager mgr = MasterMindManager.getInstance();
      String token = mgr.getResultToken();
      responseMap.put("token", token);
      if (token != null && "".equals(token.trim())) {
        sendSseSolvedEvent();
        responseMap.put("solution",mgr.getTarget());
      }
      return Response.status(Status.OK).entity(responseMap).build();
    } catch (Exception e) {
      return generateErrorResponse(e);
     }
  }

  @POST
  @Path("/sendChoice")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response consumeChoice(Choice choice) {
    try {
      MasterMindManager masterMindMgr = MasterMindManager.getInstance();
      Result result = masterMindMgr.checkChoice(choice);
      String token = result.getSolvedToken();
      if (token != null && "".equals(token.trim())) {
        sendSseSolvedEvent();
      }
      return Response.status(200).entity(result).build();
    } catch (Exception e) {
      return generateErrorResponse(e);
    }
  }

  @POST
  @Path("/reset")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response consumeReset() {
    try {
      MasterMindManager.getInstance().resetTarget();
      return Response.status(200).build();
    } catch (Exception e) {
      return generateErrorResponse(e);}
  }
}
