package com.github.herrschwarz.status4spring;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

public class SessionUtil {

    public static Map<String, String> generateSessionAttributeMap(HttpSession session) {
        Enumeration<String> attributeNames = session.getAttributeNames();
        HashMap<String, String> sessionAttributes = new HashMap<>();
        while (attributeNames.hasMoreElements()) {
            String attribute = attributeNames.nextElement();
            sessionAttributes.put(attribute, session.getAttribute(attribute).toString());
        }
        return sessionAttributes;
    }
}
