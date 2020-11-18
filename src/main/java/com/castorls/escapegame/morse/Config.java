package com.castorls.escapegame.morse;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Config {
  @JsonProperty
  public String[] targets;

  @JsonProperty
  public int speed;

  public String[] getTargets() {
    return targets;
  }

  public void setTargets(String[] targets) {
    this.targets = targets;
  }

  public int getSpeed() {
    return speed;
  }

  public void setSpeed(int speed) {
    this.speed = speed;
  }
}
