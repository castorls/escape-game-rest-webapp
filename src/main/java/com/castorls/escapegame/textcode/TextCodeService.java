package com.castorls.escapegame.textcode;

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
import com.castorls.escapegame.Util;
import com.castorls.escapegame.textcode.Config.Type;

@Path("/textcode")
public class TextCodeService extends AbstractService{

  private String challenge = null;
  private String solution = null;
  private String token = null;
  private char startChar = 0x41; // A
  private char endChar = 0x5A; // Z

  public TextCodeService() {
  }

  @Override
  public String getSseEventName() {
    return "texteCodeEvent";
  }


  @GET
  @Path("/getState")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getState() {
    try {
      Map<String, Object> responseMap = new HashMap<>();
      responseMap.put("token", token);
      if (token != null && !"".equals(token.trim())) {
        sendSseSolvedEvent();
        responseMap.put("solution", solution);
      }
      return Response.status(Status.OK).entity(responseMap).build();
    } catch (Exception e) {
      return generateErrorResponse(e);
    }
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
    try {
      this.challenge = null;
      this.solution = null;
      return Response.status(200).build();
    } catch (Exception e) {
      return generateErrorResponse(e);
    }
  }

  @POST
  @Path("/checkProposition")
  @Consumes("application/json")
  @Produces("application/json")
  public Response consumeProposition(Proposition proposition) {
    try {
      if (proposition == null) {
        return generateErrorResponse("InvalidProposition", "Invalid null proposition", Response.Status.BAD_REQUEST);
      }
      String value = proposition.getValue();
      if (value == null) {
        return generateErrorResponse("InvalidProposition", "Invalid null value in proposition", Response.Status.BAD_REQUEST);
      }
      value = Util.protectString(value);
      Map<String, Object> responseMap = new HashMap<>();
      if (solution != null && value.equals(Util.protectString(solution))) {
        token = "solvedToken";
        responseMap.put("token", token);
        responseMap.put("solution", solution);
        sendSseSolvedEvent();
      } else {
        responseMap.put("errorMessage", "La chaine proposée n'est pas correcte");
      }
      return Response.status(200).entity(responseMap).build();
    } catch (Exception e) {
      return generateErrorResponse(e);
    }
  }

  private void buildChallenge() {
    Config config = (Config) application.getProperties().get(this.getClass().getName());
    String[] targets = config.getTargets();
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
        builder.append(toNumber ? "0 " : " ");
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
