package com.github.herrschwarz.status4spring;

import com.github.herrschwarz.status4spring.inspectors.HealthInspector;
import com.github.herrschwarz.status4spring.inspectors.InspectionResult;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.github.herrschwarz.status4spring.StatusModelKeys.*;
import static com.github.herrschwarz.status4spring.StatusViewNames.INTERNAL_STATUS_VIEW_NAME;
import static com.github.herrschwarz.status4spring.StatusViewNames.INTERNAL_VERSION_VIEW_NAME;
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

    private String pageTitle = "Status";
    private String version;
    private String header;
    private List<HealthInspector> healthInspectors = new ArrayList<>();
    private Map<String, String> customHeaderEntries = ImmutableMap.of();

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
    public
    @ResponseBody
    List<InspectionResult> showHealth() {
        return inspectSystem();
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

    @ModelAttribute(PAGE_TITLE_KEY)
    public String pageTitle() {
        return this.pageTitle;
    }

    @ModelAttribute(CUSTOM_HEADER_ENTRIES_KEY)
    public Map customHeaderEntries() {
        return this.customHeaderEntries;
    }

    @ModelAttribute(VERSION_KEY)
    public String version() {
        return this.version;
    }

    @ModelAttribute(HEADER_KEY)
    public String header() {
        return this.header;
    }

    @ModelAttribute(HEALTH_KEY)
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

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void addHealthInspector(HealthInspector inspector) {
        healthInspectors.add(inspector);
    }

    public void setCustomHeaderEntries(Map<String, String> customHeaderEntries) {
        this.customHeaderEntries = customHeaderEntries;
    }
}
