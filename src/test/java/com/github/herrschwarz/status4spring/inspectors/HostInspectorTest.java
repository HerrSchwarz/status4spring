package com.github.herrschwarz.status4spring.inspectors;

import com.github.herrschwarz.status4spring.groups.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.net.Socket;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SocketFactory.class)
@Category(UnitTest.class)
public class HostInspectorTest {

  @Test
  public void shouldFailIfSocketCannotBeCreated() throws IOException {
    // Given
    mockStatic(SocketFactory.class);
    when(SocketFactory.createSocket(anyString(), anyInt())).thenThrow(new IOException());
    HostInspector testUnit = new HostInspector("test", "localhost", 0);

    // When
    InspectionResult result = testUnit.inspect();

    // Then
    assertThat(result.isSuccessful(), is(false));
    assertThat(result.getName(), is("test"));
    assertThat(result.getDescription(), is("Could not reach host localhost on port 0"));
  }

  @Test
  public void shouldSucceedIfSocketCanBeCreated() throws IOException {
    // Given
    mockStatic(SocketFactory.class);
    when(SocketFactory.createSocket(anyString(), anyInt())).thenReturn(mock(Socket.class));
    HostInspector testUnit = new HostInspector("test", "localhost", 0);

    // When
    InspectionResult result = testUnit.inspect();

    // Then
    assertThat(result.isSuccessful(), is(true));
    assertThat(result.getName(), is("test"));
    assertThat(result.getDescription(), is("host: localhost:0"));
  }
}