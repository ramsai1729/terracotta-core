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
package com.terracotta.config;

import java.util.ArrayList;
import java.util.List;

public class Servers {
  private List<Server> server;
  private Integer clientReconnectWindow;

  public List<Server> getServer() {
    if (server == null) {
      server = new ArrayList<>();
    }
    return this.server;
  }

  public void setServer(List<Server> server) {
    this.server = server;
  }

  public Integer getClientReconnectWindow() {
    return clientReconnectWindow;
  }

  public void setClientReconnectWindow(Integer value) {
    this.clientReconnectWindow = value;
  }
}
