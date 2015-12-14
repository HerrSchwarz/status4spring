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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.herrschwarz.status4spring.StatusModelKeys.*;
import static com.github.herrschwarz.status4spring.StatusViewNames.*;
import static java.lang.String.format;
import static java.lang.invoke.MethodHandles.lookup;
import static java.util.Locale.ROOT;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

@Controller
public class StatusController {

    private String pageTitle = "Status";
    private String version;
    private String build;
    private String header;
    private List<HealthInspector> healthInspectors = new ArrayList<>();

    private Map<String, String> customHeaderEntries = ImmutableMap.of();
    private static final Logger LOG = getLogger(lookup().lookupClass());

    public StatusController() {
    }

    @RequestMapping(value = "/internal/status")
    public ModelAndView showStatus() {
        return new ModelAndView(INTERNAL_STATUS_VIEW_NAME);
    }

    @RequestMapping(value = "/internal/version")
    public ModelAndView showVersion() {
        return new ModelAndView(INTERNAL_VERSION_VIEW_NAME);
    }

    @RequestMapping(value = "/internal/build")
    public ModelAndView showBuild() {
        return new ModelAndView(INTERNAL_BUILD_VIEW_NAME);
    }

    @RequestMapping(value = "/internal/health")
    @ResponseBody
    public Object showHealth() {
        return inspectSystem();
    }

    @ModelAttribute("status")
    public ModelMap statusMap() {
        return StatusUtil.getStatusMap();
    }

    @ModelAttribute(PAGE_TITLE_MODEL_KEY)
    public String pageTitle() {
        return this.pageTitle;
    }

    @ModelAttribute(CUSTOM_HEADER_ENTRIES_MODEL_KEY)
    public Map customHeaderEntries() {
        return this.customHeaderEntries;
    }

    @ModelAttribute(VERSION_MODEL_KEY)
    public String version() {
        return this.version;
    }

    @ModelAttribute(BUILD_MODEL_KEY)
    public String build() {
        return this.build;
    }

    @ModelAttribute(HEADER_MODEL_KEY)
    public String header() {
        return this.header;
    }

    @ModelAttribute(HEALTH_MODEL_KEY)
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

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setBuild(String build) {
        this.build = build;
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
