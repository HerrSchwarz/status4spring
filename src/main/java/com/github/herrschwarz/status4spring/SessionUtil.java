package com.github.herrschwarz.status4spring;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class SessionUtil {

    static Map<String, String> generateSessionAttributeMap(HttpSession session) {
        Enumeration<String> attributeNames = session.getAttributeNames();
        HashMap<String, String> sessionAttributes = new HashMap<>();
        while (attributeNames.hasMoreElements()) {
            String attribute = attributeNames.nextElement();
            sessionAttributes.put(attribute, session.getAttribute(attribute).toString());
        }
        return sessionAttributes;
    }
}
