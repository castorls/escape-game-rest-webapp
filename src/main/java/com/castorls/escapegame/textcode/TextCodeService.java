package com.castorls.escapegame.textcode;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.castorls.escapegame.SSeService;
import com.castorls.escapegame.Util;
import com.castorls.escapegame.textcode.Config.Type;

@Path("/textcode")
public class TextCodeService {

  @Inject
  private SSeService sseService;

  @Context
  private Application application;

  private String[] targets = new String[] {
      "Là ou la Drouette se jette dans l’étang d’or, sous le pont se cache dans le décor"
  };

  private String[] test = new String[] {
      "a contre courant",
      "1 2 3 nous irons au bois",
      "toujours pret",
      "eclaireuses eclaireurs",
      "sir baden powell",
      "etes vous sur ?"
  };

  private String challenge = null;
  private String solution = null;
  private String token = null;
  private char startChar = 0x41; // A
  private char endChar = 0x5A; // Z

  public TextCodeService() {
  }

  @GET
  @Path("/getState")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getState() {
    Map<String, Object> responseMap = new HashMap<>();
    Status status = Status.OK;
    try {
      responseMap.put("token", token);
      if (token != null && !"".equals(token.trim())) {
        this.sseService.sendMessage("event", "texteCodeEvent", "solved", null);
        responseMap.put("solution", solution);
      }
    } catch (Exception e) {
      responseMap.put("errorClass", e.getClass().getName());
      responseMap.put("errorMessage", e.getMessage());
      status = Response.Status.INTERNAL_SERVER_ERROR;
    }
    return Response.status(status).entity(responseMap).build();
  }

  @GET
  @Path("/getChallenge")
  @Produces("application/json")
  public Response getChallenge() {
    Map<String, Object> map = new HashMap<>();
    if (challenge == null) {
      buildChallenge();
    }
    map.put("challenge", challenge);
    return Response.status(200).entity(map).build();
  }

  @POST
  @Path("/reset")
  @Consumes("application/json")
  @Produces("application/json")
  public Response consumeReset() {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      this.challenge = null;
      this.solution = null;
      return Response.status(200).build();
    } catch (Exception e) {
      responseMap.put("errorClass", e.getClass().getName());
      responseMap.put("errorMessage", e.getMessage());
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseMap).build();
    }
  }

  @POST
  @Path("/checkProposition")
  @Consumes("application/json")
  @Produces("application/json")
  public Response consumeProposition(Proposition proposition) {
    Map<String, Object> responseMap = new HashMap<>();
    try {
      if (proposition == null) {
        return null;
      }
      String value = proposition.getValue();
      if (value == null) {
        return null;
      }
      value = Util.protectString(value);
      if (solution != null && value.equals(Util.protectString(solution))) {
        token = "solvedToken";
        responseMap.put("token", token);
        responseMap.put("solution", solution);
        this.sseService.sendMessage("event", "morseEvent", "solved", null);
      } else {
        responseMap.put("errorMessage", "La chaine proposée n'est pas correcte");
      }
      return Response.status(200).entity(responseMap).build();
    } catch (Exception e) {
      responseMap.put("errorClass", e.getClass().getName());
      responseMap.put("errorMessage", e.getMessage());
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseMap).build();
    }
  }

  private void buildChallenge() {
    Config config = (Config) application.getProperties().get(this.getClass().getName());
    Type type = config.getType();
    if (Type.LETTRE.equals(type)) {
      int[] key = new int[1];
      String keyStr = config.getKey().trim();
      key[0] = keyStr.charAt(0) - startChar;
      solution = targets[(int) Math.round(targets.length * Math.random())];
      challenge = convertStringToTextCode(solution, key, false);
    }
    else if (Type.MOT.equals(type)) {
      String keyStr = config.getKey().trim();
      int[] key = new int[keyStr.length()];
      for(int i =0; i< keyStr.length(); i++) {
        key[i] = (int) keyStr.charAt(i) - startChar;
      }
      int index = (int) Math.floor(targets.length * Math.random());
      solution = targets[index];
      challenge = convertStringToTextCode(solution, key, false);
    }
    else if (Type.CHIFFRE.equals(type)) {
      int[] key = new int[1];
      String keyStr = config.getKey().trim();
      key[0] = (char) Integer.parseInt(keyStr);
      int index = (int) Math.floor(targets.length * Math.random());
      solution = targets[index];
      challenge = convertStringToTextCode(solution, key, true);
    }
    this.token = null;
  }

  private String convertStringToTextCode(String orig, int[] key, boolean toNumber) {
    if (orig == null) {
      return null;
    }
    String uc_orig = Util.protectString(orig);
    StringBuilder builder = new StringBuilder();
    char[] chars = uc_orig.toCharArray();
    int i = 0;
    for (char car : chars) {
      String carS = String.valueOf(car);
      if (carS.equals(" ")) {
        builder.append("0 ");
      }
      else if( car < startChar || car > endChar) {
        builder.append(car).append(" ");
      }
      else {
        int carInt = (car - startChar + key[i % (key.length)]);
        int convertedCarInt = (carInt % (endChar - startChar));
        if(toNumber) {
          builder.append(Integer.toString(convertedCarInt)).append(" ");
        }
        else {
          builder.append((char) convertedCarInt);
        }
        i++;
      }
    }
    return builder.toString();
  }
}
