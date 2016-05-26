package com.github.herrschwarz.status4spring;

import com.github.herrschwarz.status4spring.groups.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpSession;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@Category(UnitTest.class)
public class SessionUtilTest {

    @Test
    public void shouldGenerateEmptySessionAttributeMap() {
        HttpSession session = new MockHttpSession();
        Map<String, String> result = SessionUtil.generateSessionAttributeMap(session);
        assertThat(result, not(nullValue()));
    }

    @Test
    public void shouldGenerateSessionAttributeMap() {
        HttpSession session = new MockHttpSession();
        session.setAttribute("meaningOfLive", 42);
        session.setAttribute("towel", true);
        session.setAttribute("thanks", "for the fish");
        Map<String, String> result = SessionUtil.generateSessionAttributeMap(session);
        assertThat(result.get("meaningOfLive"), is("42"));
        assertThat(result.get("towel"), is("true"));
        assertThat(result.get("thanks"), is("for the fish"));
    }
}