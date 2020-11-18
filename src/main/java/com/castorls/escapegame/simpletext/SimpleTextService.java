package com.castorls.escapegame.simpletext;

import java.util.HashMap;
import java.util.List;
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
import com.castorls.escapegame.simpletext.Config.Type;

@Path("/simpletext")
public class SimpleTextService extends AbstractService {

  private List<String> challengeList = null;
  private List<String> solutionList = null;
  private List<String> tokenList = null;
  private char startChar = 0x41; // A
  private char endChar = 0x5A; // Z

  public SimpleTextService() {
  }

  @Override
  public String getSseEventName() {
    return "simpleTextEvent";
  }

  @GET
  @Path("/getState")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getState(int challengeIndex) {
    try {
      if (challengeIndex < 0 || challengeIndex >= getNbChallenge()) {
        return generateErrorResponse("InvalidChallengeIndex", "Invalid challenge index " + challengeIndex, Response.Status.BAD_REQUEST);
      } else {
        Map<String, Object> responseMap = new HashMap<>();
        String token = tokenList.get(challengeIndex);
        responseMap.put("token", token);
        if (token != null && !"".equals(token.trim())) {
          this.sseService.sendMessage("event", getSseEventName(), challengeIndex + " solved", null);
          responseMap.put("solution", solutionList.get(challengeIndex));
        }
        return Response.status(Status.OK).entity(responseMap).build();
      }
    } catch (Exception e) {
      return generateErrorResponse(e);
    }
  }

  @GET
  @Path("/getChallenge")
  @Produces("application/json")
  public Response getChallenge(int challengeIndex) {
    if (challengeIndex < 0 || challengeIndex >= getNbChallenge()) {
      return generateErrorResponse("InvalidChallengeIndex", "Invalid challenge index " + challengeIndex, Response.Status.BAD_REQUEST);
    } else {
      Map<String, Object> map = new HashMap<>();
      if (challengeList == null) {
        buildChallenges();
      }
      map.put("challenge", challengeList.get(challengeIndex));
      return Response.status(200).entity(map).build();
    }
  }

  @POST
  @Path("/reset")
  @Consumes("application/json")
  @Produces("application/json")
  public Response consumeReset(int challengeIndex) {
    try {
      if (challengeIndex < 0 || challengeIndex >= getNbChallenge()) {
        return generateErrorResponse("InvalidChallengeIndex", "Invalid challenge index " + challengeIndex, Response.Status.BAD_REQUEST);
      } else {
        buildChallenges(challengeIndex);
        return Response.status(200).build();
      }
    } catch (Exception e) {
      return generateErrorResponse(e);
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
      int challengeIndex = proposition.getChallengeIndex();
      if (challengeIndex < 0 || challengeIndex >= getNbChallenge()) {
        return generateErrorResponse("InvalidChallengeIndex", "Invalid challenge index " + challengeIndex, Response.Status.BAD_REQUEST);
      } else {
        String solution = solutionList.get(challengeIndex);
        if (solution != null && value.equals(Util.protectString(solution))) {
          String token = "solvedToken";
          tokenList.set(challengeIndex, token);
          responseMap.put("token", token);
          responseMap.put("solution", solution);
          responseMap.put("challengeIndex", challengeIndex);
          this.sseService.sendMessage("event", getSseEventName(), challengeIndex + " solved", null);
        } else {
          responseMap.put("errorMessage", "La chaine proposée n'est pas correcte");
        }
        return Response.status(200).entity(responseMap).build();
      }
    } catch (Exception e) {
      return generateErrorResponse(e);
    }
  }

  private void buildChallenges() {
    Config config = (Config) application.getProperties().get(this.getClass().getName());
    Challenge[] challenges = config.getChallenges();
    for (int i = 0; i < challenges.length; i++) {
      Challenge challenge = challenges[i];
      buildChallenge(challenge, i);
    }
  }

  private void buildChallenges(int nbChallenge) {
    if (nbChallenge < 0) {
      // nothing to do
      return;
    }
    Config config = (Config) application.getProperties().get(this.getClass().getName());
    Challenge[] challenges = config.getChallenges();
    if (nbChallenge >= challenges.length) {
      // nothing to do
      return;
    }
    Challenge challenge = challenges[nbChallenge];
    buildChallenge(challenge, nbChallenge);

  }

  private void buildChallenge(Challenge challenge, int index) {
    String[] targets = challenge.getTargets();
    Type type = challenge.getType();
    String solutionStr = null;
    String challengeStr = null;
    if (Type.LETTRE.equals(type)) {
      solutionStr = targets[(int) Math.round(targets.length * Math.random())];
      challengeStr = convertString(solutionStr, false);
    } else if (Type.CHIFFRE.equals(type)) {
      solutionStr = targets[(int) Math.floor(targets.length * Math.random())];
      challengeStr = convertString(solutionStr, true);
    }
    this.solutionList.set(index, solutionStr);
    this.challengeList.set(index, challengeStr);
    this.tokenList.set(index, null);
  }

  private String convertString(String orig, boolean toNumber) {
    if (orig == null) {
      return null;
    }
    String uc_orig = Util.protectString(orig);
    StringBuilder builder = new StringBuilder();
    char[] chars = uc_orig.toCharArray();
    for (char car : chars) {
      String carS = String.valueOf(car);
      if (carS.equals(" ")) {
        builder.append(toNumber ? "0 " : " ");
      } else if (car < startChar || car > endChar) {
        builder.append(car).append(" ");
      } else {
        int carInt = (car - startChar);
        int convertedCarInt = (carInt % (endChar - startChar));
        if (toNumber) {
          builder.append(Integer.toString(convertedCarInt)).append(" ");
        } else {
          builder.append((char) convertedCarInt);
        }
      }
    }
    return builder.toString();
  }

  private int getNbChallenge() {
    if (challengeList == null) {
      buildChallenges();
    }
    return challengeList.size();
  }
}
