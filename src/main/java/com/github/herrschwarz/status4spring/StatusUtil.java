package com.github.herrschwarz.status4spring;

import org.springframework.ui.ModelMap;

import java.time.Duration;
import java.util.Date;

import static java.lang.Double.valueOf;
import static java.lang.Runtime.getRuntime;
import static java.lang.System.getenv;
import static java.lang.Thread.activeCount;
import static java.lang.management.ManagementFactory.getOperatingSystemMXBean;
import static java.lang.management.ManagementFactory.getRuntimeMXBean;

public class StatusUtil {

    static ModelMap getStatusMap() {
        ModelMap model = new ModelMap();
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
        model.put("uptime", uptime());
        return model;
    }

    private static String uptime() {
        long uptimeInMs = getRuntimeMXBean().getUptime();
        return  Duration.ofMillis(uptimeInMs).toString();
    }

}
