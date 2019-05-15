/*
 *
 *  The contents of this file are subject to the Terracotta Public License Version
 *  2.0 (the "License"); You may not use this file except in compliance with the
 *  License. You may obtain a copy of the License at
 *
 *  http://terracotta.org/legal/terracotta-public-license.
 *
 *  Software distributed under the License is distributed on an "AS IS" basis,
 *  WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 *  the specific language governing rights and limitations under the License.
 *
 *  The Covered Software is Terracotta Core.
 *
 *  The Initial Developer of the Covered Software is
 *  Terracotta, Inc., a Software AG company
 *
 */
package com.tc.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terracotta.config.latest.Server;

import com.tc.net.TCSocketAddress;

import java.io.File;

public class ServerConfiguration {
  private static final Logger logger = LoggerFactory.getLogger(ServerConfiguration.class);

  private static final String LOCALHOST = "localhost";

  private final String bind;
  private final int port;
  private final String groupBind;
  private final int groupPort;
  private final String host;
  private final String serverName;
  private final String logs;
  private final int clientReconnectWindow;

  ServerConfiguration(Server server, int clientReconnectWindow) {
    String bindAddress = server.getDefaultBind();
    this.host = server.getHost();
    if (this.host.equalsIgnoreCase(LOCALHOST)) {
      logger.warn("The specified hostname \"" + this.host
                  + "\" may not work correctly if clients and operator console are connecting getTcConfig other hosts. " + "Replace \""
                  + this.host + "\" with an appropriate hostname in configuration.");
    }

    this.serverName = server.getName();

    this.bind = replaceWildcardIfPossible(server.getTsaBind(), bindAddress);
    this.port = server.getTsaPort();

    this.groupBind = replaceWildcardIfPossible(server.getTsaGroupBind(), bindAddress);
    this.groupPort = server.getTsaGroupPort();

    this.clientReconnectWindow = clientReconnectWindow;

    this.logs = server.getLogs();
  }

  public String getBind() {
    return this.bind;
  }

  public int getPort() {
    return port;
  }

  public String getGroupBind() {
    return this.groupBind;
  }

  public int getGroupPort() {
    return this.groupPort;
  }

  public String getHost() {
    return host;
  }

  public String getName() {
    return this.serverName;
  }

  public int getClientReconnectWindow() {
    return this.clientReconnectWindow;
  }

  public File getLogsLocation() {
    return new File(this.logs);
  }


  private String replaceWildcardIfPossible(String bind, String defaultBindAddress) {
    if (TCSocketAddress.WILDCARD_IP.equals(bind) && !TCSocketAddress.WILDCARD_IP.equals(defaultBindAddress)) {
      bind = defaultBindAddress;
    }

    return bind;
  }
}
