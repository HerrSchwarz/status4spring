package com.github.herrschwarz.status4spring.inspectors;

public class InspectionResult {

  private String name;
  private boolean success;
  private String description;

  /**
   * @param name of the inspector, will be displayed on status page
   * @param success indicates, if the inspection was successful
   * @param description here you can describe the success or failure
   */
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
