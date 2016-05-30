package com.github.herrschwarz.status4spring.cache;

import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public abstract class CacheStats {

  public abstract String getName();
  public abstract String getType();
  public abstract Long getNumberOfEntries();
  public abstract Optional<Long> getHitCount();
  public abstract Optional<Double> getHitRate();
  public abstract Optional<Long> getMissCount();
  public abstract Optional<Double> getMissRate();
  public abstract Optional<Double> getAverageLoadPenalty();
  public abstract Optional<Long> getRequestCount();

}
