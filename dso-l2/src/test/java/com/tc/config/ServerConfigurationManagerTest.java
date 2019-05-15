/*
 * The contents of this file are subject to the Terracotta Public License Version
 * 2.0 (the "License"); You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://terracotta.org/legal/terracotta-public-license.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Covered Software is Terracotta Configuration.
 *
 * The Initial Developer of the Covered Software is
 * Terracotta, Inc., a Software AG company
 *
 */
package com.tc.config;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.tc.config.schema.setup.ConfigurationSetupException;
import com.tc.net.TCSocketAddress;
import com.tc.net.groups.Node;
import com.tc.properties.TCPropertiesImpl;
import com.terracotta.config.Configuration;
import com.terracotta.config.latest.Server;
import com.terracotta.config.latest.Stripe;

import java.util.Properties;

import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServerConfigurationManagerTest {

  private static final String LOCALHOST = "localhost";
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private static final String[] TEST_SERVER_NAMES = {"test-server-1", "test-server-2"};
  private static final int[] TEST_SERVER_PORTS = {9410, 9510};
  private static final int[] TEST_GROUP_PORTS = {9430, 9530};

  @Test
  public void testSingleServerValidConfiguration() throws Exception {
    Configuration configuration = mock(Configuration.class);

    Stripe stripe = Stripe.builder(createServer(0)).withReconnectWindow(100).build();
    when(configuration.getPlatformConfiguration()).thenReturn(stripe);

    boolean consistentStartup = true;
    String[] processArgs = new String[] {"arg1", "arg2"};
    ServerConfigurationManager manager = new ServerConfigurationManager(TEST_SERVER_NAMES[0],
                                                                        configuration,
                                                                        consistentStartup,
                                                                        Thread.currentThread().getContextClassLoader(),
                                                                        processArgs);

    GroupConfiguration groupConfiguration = manager.getGroupConfiguration();
    assertThat(groupConfiguration.getCurrentNode(), is(createNode(0)));
    assertThat(groupConfiguration.getNodes(),
               containsInAnyOrder(
                   createNode(0)
               )
    );
    assertThat(groupConfiguration.getMembers(), arrayContainingInAnyOrder(TEST_SERVER_NAMES[0]));

    ServerConfiguration serverConfiguration = manager.getServerConfiguration();
    assertThat(serverConfiguration.getName(), is(TEST_SERVER_NAMES[0]));

    assertThat(manager.getServiceLocator(), notNullValue());
    assertThat(manager.getProcessArguments(), arrayContainingInAnyOrder(processArgs));
    assertThat(manager.consistentStartup(), is(consistentStartup));
  }

  @Test
  public void testSingleServerWithServerNameNull() throws Exception {
    Configuration configuration = mock(Configuration.class);

    Stripe stripe = Stripe.builder(createServer(0)).withReconnectWindow(100).build();
    when(configuration.getPlatformConfiguration()).thenReturn(stripe);

    boolean consistentStartup = true;
    String[] processArgs = new String[] {"arg1", "arg2"};
    ServerConfigurationManager manager = new ServerConfigurationManager(null,
                                                                        configuration,
                                                                        consistentStartup,
                                                                        Thread.currentThread().getContextClassLoader(),
                                                                        processArgs);

    ServerConfiguration serverConfiguration = manager.getServerConfiguration();
    assertThat(serverConfiguration.getName(), is(TEST_SERVER_NAMES[0]));
  }

  @Test
  public void testSingleServerWithInvalidServerName() throws Exception {
    Configuration configuration = mock(Configuration.class);

    Stripe stripe = Stripe.builder(createServer(0)).withReconnectWindow(100).build();
    when(configuration.getPlatformConfiguration()).thenReturn(stripe);

    boolean consistentStartup = true;
    String[] processArgs = new String[] {"arg1", "arg2"};
    expectedException.expect(ConfigurationSetupException.class);
    expectedException.expectMessage("does not exist in the specified configuration");

    new ServerConfigurationManager("not-a-server-name",
                                   configuration,
                                   consistentStartup,
                                   Thread.currentThread().getContextClassLoader(),
                                   processArgs);
  }

  @Test
  public void testMultipleServersValidConfiguration() throws Exception {
    Configuration configuration = mock(Configuration.class);
    int currentServerIndex = 1;

    Stripe stripe = Stripe.builder(
                              createServer(0),
                              createServer(1)
                          ).withReconnectWindow(100).build();
    when(configuration.getPlatformConfiguration()).thenReturn(stripe);

    boolean consistentStartup = false;
    String[] processArgs = new String[] {"arg1", "arg2"};
    ServerConfigurationManager manager = new ServerConfigurationManager(TEST_SERVER_NAMES[currentServerIndex],
                                                                        configuration,
                                                                        consistentStartup,
                                                                        Thread.currentThread().getContextClassLoader(),
                                                                        processArgs);

    GroupConfiguration groupConfiguration = manager.getGroupConfiguration();
    assertThat(groupConfiguration.getCurrentNode(), is(createNode(currentServerIndex)));
    assertThat(groupConfiguration.getNodes(),
               containsInAnyOrder(
                 createNode(0), createNode(1)
               )
    );
    assertThat(groupConfiguration.getMembers(), arrayContainingInAnyOrder(TEST_SERVER_NAMES));

    ServerConfiguration serverConfiguration = manager.getServerConfiguration();
    assertThat(serverConfiguration.getName(), is(TEST_SERVER_NAMES[currentServerIndex]));

    assertThat(manager.getServiceLocator(), notNullValue());
    assertThat(manager.getProcessArguments(), arrayContainingInAnyOrder(processArgs));
    assertThat(manager.consistentStartup(), is(consistentStartup));
  }

  @Test
  public void testMultipleServersWithServerNameNull() throws Exception {
    Configuration configuration = mock(Configuration.class);

    Stripe stripe = Stripe.builder(
                              createServer(0),
                              createServer(1)
                          ).withReconnectWindow(100)
                          .build();
    when(configuration.getPlatformConfiguration()).thenReturn(stripe);

    String[] processArgs = new String[] {"arg1", "arg2"};
    expectedException.expect(ConfigurationSetupException.class);
    expectedException.expectMessage("The script can not automatically choose between the following server names");
    ServerConfigurationManager manager = new ServerConfigurationManager(null,
                                                                        configuration,
                                                                        true,
                                                                        Thread.currentThread().getContextClassLoader(),
                                                                        processArgs);
  }

  @Test
  public void testMultipleServersWithInvalidServerName() throws Exception {
    Configuration configuration = mock(Configuration.class);

    Stripe stripe = Stripe.builder(
                              createServer(0),
                              createServer(1)
                          ).withReconnectWindow(100).build();
    when(configuration.getPlatformConfiguration()).thenReturn(stripe);

    String[] processArgs = new String[] {"arg1", "arg2"};
    expectedException.expect(ConfigurationSetupException.class);
    expectedException.expectMessage("does not exist in the specified configuration");

    new ServerConfigurationManager("not-a-server-name",
                                   configuration,
                                   true,
                                   Thread.currentThread().getContextClassLoader(),
                                   processArgs);
  }

  @Test
  public void testTcProperties() throws Exception {
    Configuration configuration = mock(Configuration.class);
    int currentServerIndex = 1;

    String testKey = "some-tc-property-key";
    String testValue = "value";

    Properties properties = new Properties();
    properties.put(testKey, testValue);

    Stripe stripe = Stripe.builder(
                              createServer(0),
                              createServer(1)
                          ).withProperties(properties)
                          .withReconnectWindow(100).build();
    when(configuration.getPlatformConfiguration()).thenReturn(stripe);

    boolean consistentStartup = false;
    String[] processArgs = new String[] {"arg1", "arg2"};
    ServerConfigurationManager manager = new ServerConfigurationManager(TEST_SERVER_NAMES[currentServerIndex],
                                                                        configuration,
                                                                        consistentStartup,
                                                                        Thread.currentThread().getContextClassLoader(),
                                                                        processArgs);

    assertThat(TCPropertiesImpl.getProperties().getProperty(testKey), is(testValue));
  }


  private static Server createServer(int serverIndex) {
    return Server.builder()
                 .withName(TEST_SERVER_NAMES[serverIndex])
                 .withHost(LOCALHOST)
                 .withTsaBind(TCSocketAddress.WILDCARD_IP)
                 .withTsaPort(TEST_SERVER_PORTS[serverIndex])
                 .withTsaGroupBind(TCSocketAddress.WILDCARD_IP)
                 .withTsaGroupPort(TEST_SERVER_PORTS[serverIndex])
                 .build();
  }



  private static Node createNode(int serverIndex) {
    return new Node("localhost", TEST_SERVER_PORTS[serverIndex], TEST_GROUP_PORTS[serverIndex]);
  }
}