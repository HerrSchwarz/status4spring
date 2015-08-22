package de.herrschwarz.status4spring;

import static de.herrschwarz.status4spring.StatusViewNames.INTERNAL_HEALTH_VIEW_NAME;
import static de.herrschwarz.status4spring.StatusViewNames.INTERNAL_STATUS_VIEW_NAME;
import static de.herrschwarz.status4spring.StatusViewNames.INTERNAL_VERSION_VIEW_NAME;
import static java.lang.Double.valueOf;
import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;
import static java.lang.System.getenv;
import static java.lang.Thread.activeCount;
import static java.lang.invoke.MethodHandles.lookup;
import static java.lang.management.ManagementFactory.getOperatingSystemMXBean;
import static java.lang.management.ManagementFactory.getRuntimeMXBean;
import static java.util.Locale.ROOT;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

import de.herrschwarz.status4spring.inspectors.HealthInspector;
import de.herrschwarz.status4spring.inspectors.InspectionResult;

import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static de.herrschwarz.status4spring.StatusViewNames.*;
import static java.lang.Double.valueOf;
import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;
import static java.lang.System.getenv;
import static java.lang.Thread.activeCount;
import static java.lang.invoke.MethodHandles.lookup;
import static java.lang.management.ManagementFactory.getOperatingSystemMXBean;
import static java.lang.management.ManagementFactory.getRuntimeMXBean;
import static java.util.Locale.ROOT;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

@Controller
public class StatusController {

  private static final String HEALTH_ATTRIBUTE = "health";
  private static final String VERSION_ATTRIBUTE = "version";
  private static final String HEADER_ATTRIBUTE = "header";
  private List<HealthInspector> healthInspectors = new ArrayList<>();
  private String version;
  private String header;

  private static final Logger LOG = getLogger(lookup().lookupClass());

  public StatusController() {
  }

  @RequestMapping(value = "/internal/status") //"${internal.status.url}")
  public ModelAndView showStatus() {
    return new ModelAndView(INTERNAL_STATUS_VIEW_NAME);
  }

  @RequestMapping(value = "/internal/version")
  public ModelAndView showVersion() {
    return new ModelAndView(INTERNAL_VERSION_VIEW_NAME);
  }

  @RequestMapping(value = "/internal/health")
  public ModelAndView showHealth() {
    return new ModelAndView(INTERNAL_HEALTH_VIEW_NAME);
  }

  @ModelAttribute("status")
  public ModelMap statusMap() {
    ModelMap model = new ModelMap();
    model.put("uptime", uptime());
    model.put("starttime", new Date(getRuntimeMXBean().getStartTime()).toString());
    model.put("classpath", getRuntimeMXBean().getClassPath());
    model.put("freeMemory", getRuntime().freeMemory());
    model.put("maxMemory", getRuntime().maxMemory());
    model.put("usedMemory", getRuntime().maxMemory() - getRuntime().freeMemory());
    model.put("memoryUsage",
        (getRuntime().maxMemory() - getRuntime().freeMemory()) / valueOf(getRuntime().maxMemory()));
    model.put("averageLoad", getOperatingSystemMXBean().getSystemLoadAverage());
    model.put("processors", getRuntime().availableProcessors());
    model.put("threadCount", activeCount());
    model.put("osArch", getOperatingSystemMXBean().getArch());
    model.put("osName", getOperatingSystemMXBean().getName());
    model.put("osVersion", getOperatingSystemMXBean().getVersion());
    model.put("systemProperties", getRuntimeMXBean().getSystemProperties());
    model.put("environmentVariables", getenv());
    model.put("systemCheck", inspectSystem());
    return model;
  }

  @ModelAttribute(VERSION_ATTRIBUTE)
  public String version() {
    return this.version;
  }

  @ModelAttribute(HEADER_ATTRIBUTE)
  public String header() {
    return this.header;
  }

  @ModelAttribute(HEALTH_ATTRIBUTE)
  private List<InspectionResult> inspectSystem() {
    return healthInspectors.stream().map(i -> executeInspector(i)).collect(toList());
  }

  private InspectionResult executeInspector(HealthInspector inspector) {
    try {
      return inspector.inspect();
    } catch (Throwable t) {
      LOG.error(format(ROOT, "error running healthInspector %s", inspector.getName()), t);
      return new InspectionResult(inspector.getName(), false, "execution of inspector failed");
    }
  }

  private String uptime() {
    long uptimeInMs = getRuntimeMXBean().getUptime();
    Duration duration = Duration.ofMillis(uptimeInMs);
    return duration.toString();
  }

  public void addHealthInspector(HealthInspector inspector) {
    healthInspectors.add(inspector);
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public void setHeader(String header) {
    this.header = header;
  }
}
