package com.castorls.escapegame.mastermind;

public class Result {

  private Boolean[] results;
  private String errorMessage;
  private String solvedToken;

  public Result() {
    super();
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public Boolean[] getResults() {
    return results;
  }

  public void setResults(Boolean[] results) {
    this.results = results;
  }

  public String getSolvedToken() {
    return solvedToken;
  }

  public void setSolvedToken(String solvedToken) {
    this.solvedToken = solvedToken;
  }
}
