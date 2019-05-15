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

public class Server {
  private final String name;
  private final String host;
  private final String defaultBind;
  private final String bind;
  private final int port;
  private final String groupBind;
  private final int groupPort;
  private final String logs;

  private Server(String name,
                 String host,
                 String defaultBind,
                 String bind,
                 int port,
                 String groupBind,
                 int groupPort,
                 String logs) {
    this.name = name;
    this.host = host;
    this.defaultBind = defaultBind;
    this.bind = bind;
    this.port = port;
    this.groupBind = groupBind;
    this.groupPort = groupPort;
    this.logs = logs;
  }

  public static Builder builder() {
    return new Builder();
  }

  public String getName() {
    return name;
  }

  public String getHost() {
    return host;
  }

  public String getDefaultBind() {
    return defaultBind;
  }

  public String getTsaBind() {
    return bind;
  }

  public int getTsaPort() {
    return port;
  }

  public String getTsaGroupBind() {
    return groupBind;
  }

  public int getTsaGroupPort() {
    return groupPort;
  }

  public String getLogs() {
    return logs;
  }

  public static class Builder {
    private String logs;
    private String name;
    private String host;
    private String bind;
    private int port;
    private String groupBind;
    private int groupPort;
    private String defaultBind;

    public Builder withLogs(String logs) {
      this.logs = logs;

      return this;
    }

    public Builder withName(String name) {
      this.name = name;

      return this;
    }

    public Builder withHost(String host) {
      this.host = host;

      return this;
    }

    public Builder withTsaBind(String bind) {
      this.bind = bind;

      return this;
    }

    public Builder withTsaPort(int port) {
      this.port = port;

      return this;
    }

    public Builder withTsaGroupBind(String groupBind) {
      this.groupBind = groupBind;

      return this;
    }


    public Builder withTsaGroupPort(int groupPort) {
      this.groupPort = groupPort;

      return this;
    }

    public Builder withDefaultBind(String bind) {
      this.defaultBind = bind;

      return this;
    }

    public Server build() {
      return new Server(name, host, defaultBind, bind, port, groupBind, groupPort, logs);
    }
  }
}
