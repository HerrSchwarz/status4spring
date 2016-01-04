package com.github.herrschwarz.status4spring;

import com.github.herrschwarz.status4spring.inspectors.HealthInspector;
import com.github.herrschwarz.status4spring.inspectors.InspectionResult;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.*;

import static com.github.herrschwarz.status4spring.SessionModelKeys.*;
import static com.github.herrschwarz.status4spring.SessionUtil.generateSessionAttributeMap;
import static com.github.herrschwarz.status4spring.StatusModelKeys.*;
import static com.github.herrschwarz.status4spring.ViewNames.*;
import static java.lang.String.format;
import static java.lang.invoke.MethodHandles.lookup;
import static java.util.Locale.ROOT;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

@Controller
public class StatusController {

    private static final Logger LOG = getLogger(lookup().lookupClass());
    private String pageTitle = "Status";
    private String version;
    private String build;
    private String header;
    private boolean sessionEnabled;
    private List<HealthInspector> healthInspectors = new ArrayList<>();
    private Map<String, String> customHeaderEntries = ImmutableMap.of();
    private Optional<CacheManager> cacheManager = Optional.empty();

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

    @RequestMapping(value = "/internal/session")
    public ModelAndView session(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView(INTERNAL_SESSION_VIEW_NAME);
        Map<String, String> sessionAttributes = generateSessionAttributeMap(session);
        modelAndView.addObject(SESSION_ATTRIBUTES_MODEL_KEY, sessionAttributes);
        modelAndView.addObject(SESSION_ID_MODEL_KEY, session.getId());
        modelAndView.addObject(SESSION_CREATION_TIME_MODEL_KEY, new Date(session.getCreationTime()).toString());
        return modelAndView;
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

    @ModelAttribute(SESSION_ENABLED_MODEL_KEY)
    private boolean sessionEnabled() {
        return sessionEnabled;
    }

    private InspectionResult executeInspector(HealthInspector inspector) {
        try {
            return inspector.inspect();
        } catch (Throwable t) {
            LOG.error(format(ROOT, "error running healthInspector %s", inspector.getName()), t);
            return new InspectionResult(inspector.getName(), false, "execution of inspector failed");
        }
    }

    @ModelAttribute("cacheNames")
    public Collection<String> cacheNames() {
        return cacheManager.map(cM -> cM.getCacheNames()).orElse(ImmutableList.of());
    }

    /**
     * Sets the page title of the status page
     *
     * @param pageTitle will be used as html title attribute
     */
    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    /**
     * version will be displayed on the status page and can be called via /internal/version
     * You can use this to check the currently installed version of your software and check
     * the version after your deployment (e.g. with puppet, chef or ansible)
     *
     * @param version will be shown as version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * build will be displayed on the status page and can be called via /internal/build
     * You can use this to check the currently installed build of your software and check
     * the build number after your deployment (e.g. with puppet, chef or ansible).
     *
     * @param build will be shown as build
     */
    public void setBuild(String build) {
        this.build = build;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * You can add health inspectors to check your system. It is possible to create your own inspectors.
     * Just implement the {@link com.github.herrschwarz.status4spring.inspectors.HealthInspector} interface.
     * @param inspector health inspector
     */
    public void addHealthInspector(HealthInspector inspector) {
        healthInspectors.add(inspector);
    }

    /**
     * If you set customHeaderEntries the statusController will render additional entries
     * in the header menu. You can place links to e.g. metric systems or log aggregation here.
     * @param customHeaderEntries   Map&lt;text,url&gt;
     */
    public void setCustomHeaderEntries(Map<String, String> customHeaderEntries) {
        this.customHeaderEntries = customHeaderEntries;
    }

    /**
     * @param sessionEnabled if true, a link to the session page will be shown in the header
     */
    public void setSessionEnabled(boolean sessionEnabled) {
        this.sessionEnabled = sessionEnabled;
    }

    /**
     * @param cacheManager will be used to display some information about the caches in the cache tab
     */
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = Optional.of(cacheManager);
    }
}
