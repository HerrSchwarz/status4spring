package com.github.herrschwarz.status4spring.inspectors;

public class InspectionResult {

  private String name;
  private boolean success;
  private String description;

  public InspectionResult(String name, boolean success, String description) {
    this.name = name;
    this.success = success;
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public boolean isSuccessful() {
    return success;
  }

  public String getDescription() {
    return description;
  }
}
