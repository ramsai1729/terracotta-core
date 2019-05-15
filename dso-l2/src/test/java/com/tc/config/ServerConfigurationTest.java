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

import org.junit.Test;
import com.terracotta.config.latest.Server;

import com.tc.net.TCSocketAddress;

import java.io.File;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ServerConfigurationTest {

  private static final String SERVER_NAME = "server-1";
  private static final String LOCALHOST = "localhost";
  private static final String LOGS = "logs";
  private static final int    TSA_PORT = 1000;
  private static final int    GROUP_PORT = 1100;

  @Test
  public void testConfiguration() {
    int reconnectWindow = 100;
    ServerConfiguration serverConfiguration =
        new ServerConfiguration(createServer(false), reconnectWindow);

    assertThat(serverConfiguration.getBind(), is(LOCALHOST));
    assertThat(serverConfiguration.getGroupBind(), is(LOCALHOST));
    assertThat(serverConfiguration.getPort(), is(TSA_PORT));
    assertThat(serverConfiguration.getGroupPort(), is(GROUP_PORT));
    assertThat(serverConfiguration.getName(), is(SERVER_NAME));
    assertThat(serverConfiguration.getHost(), is(LOCALHOST));
    assertThat(serverConfiguration.getLogsLocation(), is(new File(LOGS)));
    assertThat(serverConfiguration.getClientReconnectWindow(), is(reconnectWindow));
  }

  @Test
  public void testConfigurationWithWildcards() {
    ServerConfiguration serverConfiguration =
        new ServerConfiguration(createServer(true), 100);

    assertThat(serverConfiguration.getBind(), is(LOCALHOST));
    assertThat(serverConfiguration.getGroupBind(), is(LOCALHOST));
  }

  private static Server createServer(boolean wildcards) {
    String bindAddress = wildcards ? TCSocketAddress.WILDCARD_IP : LOCALHOST;

    return Server.builder()
                 .withName(SERVER_NAME)
                 .withHost(LOCALHOST)
                 .withDefaultBind(LOCALHOST)
                 .withLogs(LOGS)
                 .withTsaBind(bindAddress)
                 .withTsaPort(TSA_PORT)
                 .withTsaGroupBind(bindAddress)
                 .withTsaGroupPort(GROUP_PORT)
                 .build();
  }
}