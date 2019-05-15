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
package com.terracotta.config.latest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class Stripe {

  public static Builder builder(Server server, Server... servers) {
    return new Builder(server, servers);
  }

  public List<Server> getServers() {
    return Collections.unmodifiableList(servers);
  }

  public FailOverPriority getFailOverPriority() {
    return failOverPriority;
  }

  public Properties getProperties() {
    return properties;
  }

  public int getReconnectWindow() {
    return reconnectWindow;
  }

  private final List<Server> servers;
  private final FailOverPriority failOverPriority;
  private final Properties properties;
  private final int reconnectWindow;

  private Stripe(List<Server> servers, FailOverPriority failOverPriority, Properties properties, int reconnectWindow) {
    this.servers = servers;
    this.failOverPriority = failOverPriority;
    this.properties = properties;
    this.reconnectWindow = reconnectWindow;
  }

  public static class Builder {
    private List<Server> servers = new ArrayList<>();
    private FailOverPriority failoverPriority;
    private Properties properties = new Properties();
    private int reconnectWindow = 120;

    Builder(Server server, Server[] servers) {
      this.servers.add(server);
      this.servers.addAll(Arrays.asList(servers));
    }

    public Builder withFailOverPriority(FailOverPriority failoverPriority) {
      this.failoverPriority = failoverPriority;

      return this;
    }

    public Builder withProperties(Properties properties) {
      this.properties.putAll(properties);

      return this;
    }

    public Builder withReconnectWindow(int reconnectWindow) {
      this.reconnectWindow = reconnectWindow;

      return this;
    }

    public Stripe build() {
      return new Stripe(servers, failoverPriority, properties, reconnectWindow);
    }
  }
}
