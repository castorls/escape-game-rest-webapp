package com.castorls.escapegame.morse;

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
import com.castorls.escapegame.Util;

@Path("/morse")
public class MorseService {

  @Inject
  private SSeService sseService;

  private String[] targets = new String[] {
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

  public MorseService() {
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
        this.sseService.sendMessage("event", "morseEvent", "solved", null);
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
  public Response consumeChoice(Proposition proposition) {
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
        responseMap.put("solution",solution);
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
    solution = targets[(int) Math.round(targets.length * Math.random())];
    challenge = convertStringToMorse(solution);
    this.token = null;
  }

  private String convertStringToMorse(String orig) {
    if (orig == null) {
      return null;
    }
    String uc_orig = Util.protectString(orig);
    StringBuilder builder = new StringBuilder();
    char[] chars = uc_orig.toCharArray();
    boolean isSpace = false;
    for (char car : chars) {
      String carS = String.valueOf(car);
      if (!isSpace && carS.equals(" ")) {
        isSpace = true;
        builder.append("       ");
      } else {
        isSpace = false;
        String morseCode = morseCodeMap.get(carS);
        if (morseCode == null) {
          morseCode = morseCodeMap.get("?");
        }
        builder.append(morseCode).append("   ");
      }
    }
    return builder.toString();
  }

  private static Map<String, String> morseCodeMap = new HashMap<>();

  static {
    morseCodeMap.put("A", ".-");
    morseCodeMap.put("B", "-...");
    morseCodeMap.put("C", "-.-.");
    morseCodeMap.put("D", "-..");
    morseCodeMap.put("E", ".");
    morseCodeMap.put("F", "..-.");
    morseCodeMap.put("G", "--.");
    morseCodeMap.put("H", "....");
    morseCodeMap.put("I", "..");
    morseCodeMap.put("J", ".---");
    morseCodeMap.put("K", "-.-");
    morseCodeMap.put("L", ".-..");
    morseCodeMap.put("M", "--");
    morseCodeMap.put("N", "-.");
    morseCodeMap.put("O", "---");
    morseCodeMap.put("P", ".--.");
    morseCodeMap.put("Q", "--.-");
    morseCodeMap.put("R", ".-.");
    morseCodeMap.put("S", "...");
    morseCodeMap.put("T", "-");
    morseCodeMap.put("U", "..-");
    morseCodeMap.put("V", "...-");
    morseCodeMap.put("W", ".--");
    morseCodeMap.put("X", "-..-");
    morseCodeMap.put("Y", "-.--");
    morseCodeMap.put("Z", "--..");
    morseCodeMap.put("0", "-----");
    morseCodeMap.put("1", ".----");
    morseCodeMap.put("2", "..---");
    morseCodeMap.put("3", "...--");
    morseCodeMap.put("4", "....-");
    morseCodeMap.put("5", ".....");
    morseCodeMap.put("6", "-....");
    morseCodeMap.put("7", "--...");
    morseCodeMap.put("8", "---..");
    morseCodeMap.put("9", "----.");
    morseCodeMap.put(".", ".-.-.-");
    morseCodeMap.put(",", "--..--");
    morseCodeMap.put("/", "-..-.");
    morseCodeMap.put("?", "..--..");
    morseCodeMap.put(" ", "      ");
  }

}
