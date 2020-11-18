package com.castorls.escapegame.simpletext;

import com.castorls.escapegame.simpletext.Config.Type;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Challenge {

  @JsonProperty
  public Type type;

  @JsonProperty
  public String[] targets;

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public String[] getTargets() {
    return targets;
  }

  public void setTargets(String[] targets) {
    this.targets = targets;
  }

}
