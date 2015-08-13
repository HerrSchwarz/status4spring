package de.herrschwarz.status4spring.inspectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.net.Socket;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SocketFactory.class)
public class HostInspectorTest {

  @Test
  public void shouldFail() throws Exception {
    mockStatic(SocketFactory.class);
    when(SocketFactory.createSocket(anyString(), anyInt())).thenThrow(new IOException());
    HostInspector testUnit = new HostInspector("test", "localhost", 0);
    InspectionResult result = testUnit.inspect();
    assertThat(result.isSuccessful(), is(false));
    assertThat(result.getName(), is("test"));
    assertThat(result.getDescription(), is("Could not reach host localhost on port 0"));
  }

  @Test
  public void shouldSucceed() throws Exception {
    mockStatic(SocketFactory.class);
    when(SocketFactory.createSocket(anyString(), anyInt())).thenReturn(mock(Socket.class));
    HostInspector testUnit = new HostInspector("test", "localhost", 0);
    InspectionResult result = testUnit.inspect();
    assertThat(result.isSuccessful(), is(true));
    assertThat(result.getName(), is("test"));
    assertThat(result.getDescription(), is("host: localhost:0"));
  }
}