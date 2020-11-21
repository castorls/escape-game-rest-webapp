package com.castorls.escapegame;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class Config {
  @JsonProperty
  public com.castorls.escapegame.timer.Config timer;

  @JsonProperty
  public com.castorls.escapegame.morse.Config morse;

  @JsonProperty
  public com.castorls.escapegame.mastermind.Config mastermind;

  @JsonProperty
  public com.castorls.escapegame.textcode.Config  textcode;

  @JsonProperty
  public com.castorls.escapegame.simpletext.Config  simpletext;

  public static Config parseFile(String file) {
    final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    try {
      return mapper.readValue(Config.class.getClassLoader().getResourceAsStream("./config.yml"), Config.class);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }
}
