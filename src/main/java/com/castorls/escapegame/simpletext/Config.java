package com.castorls.escapegame.simpletext;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Config {

  public enum Type {
   LETTRE, CHIFFRE;
  }

  @JsonProperty
  public Challenge[] challenges;

  public Challenge[] getChallenges() {
    return challenges;
  }

  public void setChallenges(Challenge[] challenges) {
    this.challenges = challenges;
  }
}
