package de.herrschwarz.status4spring.inspectors;

public interface HealthInspector {

  public InspectionResult inspect();
  public String getName();

}