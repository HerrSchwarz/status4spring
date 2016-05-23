package com.github.herrschwarz.status4spring;

import static com.github.herrschwarz.status4spring.SessionModelKeys.SESSION_ATTRIBUTES_MODEL_KEY;
import static com.github.herrschwarz.status4spring.SessionModelKeys.SESSION_CREATION_TIME_MODEL_KEY;
import static com.github.herrschwarz.status4spring.SessionModelKeys.SESSION_ID_MODEL_KEY;
import static com.github.herrschwarz.status4spring.ViewNames.INTERNAL_BUILD_VIEW_NAME;
import static com.github.herrschwarz.status4spring.ViewNames.INTERNAL_SESSION_VIEW_NAME;
import static com.github.herrschwarz.status4spring.ViewNames.INTERNAL_STATUS_VIEW_NAME;
import static com.github.herrschwarz.status4spring.ViewNames.INTERNAL_VERSION_VIEW_NAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.github.herrschwarz.status4spring.cache.CacheStatsProvider;
import com.github.herrschwarz.status4spring.groups.UnitTest;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpSession;

@Category(UnitTest.class)
public class StatusControllerTest {

    public static final String ATTRIBUTE_KEY = "meaningOfDeath";
    public static final String ATTRIBUTE_VALUE = "666";

    @Test
    public void shouldSelectStatusViewNameForStatusPage() throws Exception {
        // Given
        StatusController statusController = new StatusController();

        // When
        ModelAndView modelAndView = statusController.showStatus();

        // Then
        assertThat("View name is wrong", modelAndView.getViewName(), is(INTERNAL_STATUS_VIEW_NAME));
    }

    @Test
    public void shouldSelectVersionViewNameForVersionPage() throws Exception {
        // Given
        StatusController statusController = new StatusController();

        // When
        ModelAndView modelAndView = statusController.showVersion();

        // Then
        assertThat("View name is wrong", modelAndView.getViewName(), is(INTERNAL_VERSION_VIEW_NAME));
    }

    @Test
    public void shouldSelectBuildViewNameForBuildPage() throws Exception {
        // Given
        StatusController statusController = new StatusController();

        // When
        ModelAndView modelAndView = statusController.showBuild();

        // Then
        assertThat("View name is wrong", modelAndView.getViewName(), is(INTERNAL_BUILD_VIEW_NAME));
    }

    @Test
    public void shouldSelectSessionViewNameForSessionPage() throws Exception {
        // Given
        StatusController statusController = new StatusController();

        // When
        ModelAndView modelAndView = statusController.session(new MockHttpSession());

        // Then
        assertThat("View name is wrong", modelAndView.getViewName(), is(INTERNAL_SESSION_VIEW_NAME));
    }

    @Test
    public void shouldPutSessionAttributeInModelMap() throws Exception {
        // Given
        StatusController statusController = new StatusController();
        HttpSession session = new MockHttpSession();
        session.setAttribute(ATTRIBUTE_KEY, ATTRIBUTE_VALUE);

        // When
        ModelAndView modelAndView = statusController.session(session);

        // Then
        Map<String, String> sessionAttributes = (Map<String, String>) modelAndView.getModelMap().get(SESSION_ATTRIBUTES_MODEL_KEY);
        assertThat(sessionAttributes, not(nullValue()));
        assertThat(sessionAttributes.get(ATTRIBUTE_KEY), is(ATTRIBUTE_VALUE));
    }

    @Test
    public void shouldPutSessionIdInModelMap() throws Exception {
        // Given
        StatusController statusController = new StatusController();

        // When
        ModelAndView modelAndView = statusController.session(new MockHttpSession());

        // Then
        ModelMap modelMap = modelAndView.getModelMap();
        assertThat(modelMap, not(nullValue()));
        assertThat(modelMap, hasKey(SESSION_ID_MODEL_KEY));
    }

    @Test
    public void shouldPutSessionCreationTimestampInModelMap() throws Exception {
        // Given
        StatusController statusController = new StatusController();
        MockHttpSession session = new MockHttpSession();

        // When
        ModelAndView modelAndView = statusController.session(session);

        // Then
        ModelMap modelMap = modelAndView.getModelMap();
        assertThat(modelMap, not(nullValue()));
        assertThat(modelMap, hasKey(SESSION_CREATION_TIME_MODEL_KEY));
        assertThat(modelMap.get(SESSION_CREATION_TIME_MODEL_KEY), is(new Date(session.getCreationTime()).toString()));
    }

    @Test
    public void shouldCallCacheProviderOnClear() throws Exception {
        // Given
        StatusController statusController = new StatusController();
        CacheStatsProvider cacheStatsProviderMock = mock(CacheStatsProvider.class);
        statusController.setCacheStatsProvider(cacheStatsProviderMock);

        // When
        ModelAndView modelAndView = statusController.clearCache("test");

        // Then
        assertThat(modelAndView.getModelMap(), hasKey("info"));
        verify(cacheStatsProviderMock, times(1)).clearCache("test");
    }
}
