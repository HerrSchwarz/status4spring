package com.github.herrschwarz.status4spring.inspectors;

/**
 * Implement this interface if you want a custom health check. Just add your health inspector to the
 * {@link com.github.herrschwarz.status4spring.StatusController} and the result will be included on
 * the status page and in the health check result /internal/health
 */
public interface HealthInspector {

  /**
   * runs the inspection and returns an {@link com.github.herrschwarz.status4spring.inspectors.InspectionResult}
   * @return {@link com.github.herrschwarz.status4spring.inspectors.InspectionResult}
   */
  public InspectionResult inspect();

  /**
   * @return name of the inspector, will be displayed on status page and be used for health check result
   */
  public String getName();

}
