package com.github.herrschwarz.status4spring.inspectors;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public abstract class Health {

  public abstract boolean isHealthy();
  public abstract List<InspectionResult> getDetails();

}
