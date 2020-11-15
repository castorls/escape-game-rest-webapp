package com.castorls.escapegame.timer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Config {
  @JsonProperty
  public String endTime;

  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }
}
