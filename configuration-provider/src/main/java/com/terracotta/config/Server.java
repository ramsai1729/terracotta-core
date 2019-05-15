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

public class Server {
  private String logs;
  private BindPort tsaPort;
  private BindPort tsaGroupPort;
  protected String host;
  protected String name;
  private String bind;

  public String getLogs() {
    return logs;
  }

  public void setLogs(String value) {
    this.logs = value;
  }

  public BindPort getTsaPort() {
    return tsaPort;
  }

  public void setTsaPort(BindPort value) {
    this.tsaPort = value;
  }

  public BindPort getTsaGroupPort() {
    return tsaGroupPort;
  }

  public void setTsaGroupPort(BindPort value) {
    this.tsaGroupPort = value;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String value) {
    this.host = value;
  }

  public String getName() {
    return name;
  }

  public void setName(String value) {
    this.name = value;
  }

  public String getBind() {
    if (bind == null) {
      return "0.0.0.0";
    } else {
      return bind;
    }
  }

  public void setBind(String value) {
    this.bind = value;
  }
}
