package com.github.herrschwarz.status4spring;

import com.github.herrschwarz.status4spring.groups.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.web.servlet.ModelAndView;

import static com.github.herrschwarz.status4spring.StatusViewNames.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@Category(UnitTest.class)
public class StatusControllerTest {

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

}
