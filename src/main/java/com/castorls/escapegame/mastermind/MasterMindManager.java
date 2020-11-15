package com.castorls.escapegame.mastermind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class MasterMindManager {
  private static MasterMindManager _INSTANCE = new MasterMindManager();

  public enum Color {
    RED, YELLOW, BLUE, ORANGE, GREEN, WHITE, PURPLE, ROSE;

    private static Map<String, Color> namesMap = new HashMap<String, Color>(8);

    static {
      namesMap.put("red", RED);
      namesMap.put("yellow", YELLOW);
      namesMap.put("blue", BLUE);
      namesMap.put("orange", ORANGE);
      namesMap.put("green", GREEN);
      namesMap.put("white", WHITE);
      namesMap.put("purple", PURPLE);
      namesMap.put("rose", ROSE);
    }

    @JsonCreator
    public static Color forValue(String value) {
      return namesMap.get(StringUtils.lowerCase(value));
    }

    @JsonValue
    public String toValue() {
      for (Entry<String, Color> entry : namesMap.entrySet()) {
        if (entry.getValue() == this)
          return entry.getKey();
      }
      return null; // or fail
    }
  }


  private int nbColor = 4;
  private String resultToken = null;
  private Color[] target = new Color[nbColor];
  private boolean initialized = false;

  private MasterMindManager() {
    // nothing to do
  }

  public static MasterMindManager getInstance() {
    return _INSTANCE;
  }

  public Color[] getTarget() {
    return target;
  }

  public void resetTarget() {
    List<Color> colorList = new ArrayList<Color>();
    for (int i = 0; i < nbColor; i++) {
      Color color = null;
      while (color == null || colorList.contains(color)) {
        color = Color.values()[(int) (Math.round(Math.random() * nbColor))];
      }
      colorList.add(color);
    }
    target = (Color[]) colorList.toArray(new Color[colorList.size()]);
    initialized = true;
    this.resultToken = null;
  }

  public String getResultToken() {
    return resultToken;
  }

  public Result checkChoice(Choice choice) {
    Color[] colors = choice.getValues();

    if (colors == null || colors.length != nbColor) {
      Result result = new Result();
      result.setErrorMessage("Error, the choice has not the correct length.");
      return result;
    }
    if(!initialized) {
      resetTarget();
    }
    // analyze choices
    Boolean[] values = new Boolean[nbColor];
    boolean isSolved = true;
    for (int i = 0; i < nbColor; i++) {
      if (colors[i] != null) {
        if (colors[i].equals(target[i])) {
          values[i] = true;
        } else {
          isSolved = false;
          for (int j = 0; j < nbColor; j++) {
            if (colors[i].equals(target[j])) {
              values[i] = false;
            }
          }
        }
      }
      else {
        isSolved = false;
      }
    }
    if(isSolved) {
       this.resultToken = "solvedToken";
    }
    Result result = new Result();
    result.setSolvedToken(this.resultToken);
    result.setResults(values);
    return result;
  }

}
