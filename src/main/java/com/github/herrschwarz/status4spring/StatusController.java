package com.github.herrschwarz.status4spring;

import static com.github.herrschwarz.status4spring.SessionModelKeys.SESSION_ATTRIBUTES_MODEL_KEY;
import static com.github.herrschwarz.status4spring.SessionModelKeys.SESSION_CREATION_TIME_MODEL_KEY;
import static com.github.herrschwarz.status4spring.SessionModelKeys.SESSION_ID_MODEL_KEY;
import static com.github.herrschwarz.status4spring.SessionUtil.generateSessionAttributeMap;
import static com.github.herrschwarz.status4spring.StatusModelKeys.BUILD_MODEL_KEY;
import static com.github.herrschwarz.status4spring.StatusModelKeys.CACHE_STATS;
import static com.github.herrschwarz.status4spring.StatusModelKeys.CUSTOM_HEADER_ENTRIES_MODEL_KEY;
import static com.github.herrschwarz.status4spring.StatusModelKeys.HEADER_MODEL_KEY;
import static com.github.herrschwarz.status4spring.StatusModelKeys.HEALTH_MODEL_KEY;
import static com.github.herrschwarz.status4spring.StatusModelKeys.PAGE_TITLE_MODEL_KEY;
import static com.github.herrschwarz.status4spring.StatusModelKeys.SESSION_ENABLED_MODEL_KEY;
import static com.github.herrschwarz.status4spring.StatusModelKeys.VERSION_MODEL_KEY;
import static com.github.herrschwarz.status4spring.ViewNames.INTERNAL_BUILD_VIEW_NAME;
import static com.github.herrschwarz.status4spring.ViewNames.INTERNAL_SESSION_VIEW_NAME;
import static com.github.herrschwarz.status4spring.ViewNames.INTERNAL_STATUS_VIEW_NAME;
import static com.github.herrschwarz.status4spring.ViewNames.INTERNAL_VERSION_VIEW_NAME;
import static java.lang.String.format;
import static java.lang.invoke.MethodHandles.lookup;
import static java.util.Locale.ROOT;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import com.github.herrschwarz.status4spring.cache.CacheStats;
import com.github.herrschwarz.status4spring.cache.CacheStatsProvider;
import com.github.herrschwarz.status4spring.inspectors.Health;
import com.github.herrschwarz.status4spring.inspectors.HealthInspector;
import com.github.herrschwarz.status4spring.inspectors.ImmutableHealth;
import com.github.herrschwarz.status4spring.inspectors.InspectionResult;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

@Controller
public class StatusController {

    private static final Logger LOG = getLogger(lookup().lookupClass());
    private String pageTitle = "Status";
    private String version;
    private String build;
    private String header;
    private boolean sessionEnabled;
    private List<HealthInspector> healthInspectors = Collections.synchronizedList(new ArrayList<>());
    private Map<String, String> customHeaderEntries = ImmutableMap.of();
    private Optional<CacheStatsProvider> cacheStatsProvider = Optional.empty();

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

    @RequestMapping(value = "/internal/cache/clear/{cacheName}", method = POST)
    public ModelAndView clearCache(@PathVariable(value = "cacheName") String cacheName) {
        Optional<Long> deletedEntries = cacheStatsProvider.map(p -> p.clearCache(cacheName));
        ModelAndView modelAndView = new ModelAndView("redirect:/internal/status");
        modelAndView.addObject("info", "Cache " + cacheName + "cleared, " + deletedEntries + "deleted");
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
    public Health inspectSystem() {
        List<InspectionResult> details = healthInspectors.stream()
            .map(this::executeInspector)
            .collect(toList());
        boolean healthy = details.stream()
                        .filter(d -> !d.isSuccessful())
                        .count() == 0;
        return ImmutableHealth.builder()
            .isHealthy(healthy)
            .details((Iterable) details)
            .build();
    }

    @ModelAttribute(SESSION_ENABLED_MODEL_KEY)
    public boolean sessionEnabled() {
        return sessionEnabled;
    }

    @ModelAttribute(CACHE_STATS)
    public List<CacheStats> cacheNames() {
        return cacheStatsProvider.map(CacheStatsProvider::getCacheStats).orElse(ImmutableList.of());
    }

    private InspectionResult executeInspector(HealthInspector inspector) {
        try {
            return inspector.inspect();
        } catch (Throwable t) {
            LOG.error(format(ROOT, "error running healthInspector %s", inspector.getName()), t);
            return new InspectionResult(inspector.getName(), false, "execution of inspector failed");
        }
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
     * @param cacheStatsProvider will be used to display some information about the caches in the cache tab.
     *                           You can use the ConcurrentMapCacheStatsProvider, which can be used for
     *                           ConcurrentMapCaches as used with the @Cachable in SpringBoot.
     *                           The ConcurrentMapCacheStatsProvider provides Name and number of entries.
     *                           You can also implement your own CacheStatsProvider
     */
    public void setCacheStatsProvider(CacheStatsProvider cacheStatsProvider) {
        this.cacheStatsProvider = Optional.of(cacheStatsProvider);
    }
}
