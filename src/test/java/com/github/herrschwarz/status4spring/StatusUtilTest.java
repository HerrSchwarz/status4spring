package com.github.herrschwarz.status4spring;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

import org.junit.Test;
import org.springframework.ui.ModelMap;

public final class StatusUtilTest {

  @Test
  public void shouldGetStatusMap() {
    ModelMap model = StatusUtil.getStatusMap();
    assertThat(model.get("hostname"), not(nullValue()));
    assertThat(model.get("ip"), not(nullValue()));
    assertThat(model.get("starttime"), not(nullValue()));
    assertThat(model.get("classpath"), not(nullValue()));
    assertThat(model.get("freeMemory"), not(nullValue()));
    assertThat(model.get("maxMemory"), not(nullValue()));
    assertThat(model.get("usedMemory"), not(nullValue()));
    assertThat(model.get("memoryUsage"), not(nullValue()));
    assertThat(model.get("averageLoad"), not(nullValue()));
    assertThat(model.get("processors"), not(nullValue()));
    assertThat(model.get("threadCount"), not(nullValue()));
    assertThat(model.get("osArch"), not(nullValue()));
    assertThat(model.get("osName"), not(nullValue()));
    assertThat(model.get("osVersion"), not(nullValue()));
    assertThat(model.get("systemProperties"), not(nullValue()));
    assertThat(model.get("environmentVariables"), not(nullValue()));
    assertThat(model.get("uptime"), not(nullValue()));
  }
}