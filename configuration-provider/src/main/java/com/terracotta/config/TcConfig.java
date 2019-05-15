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

public class TcConfig {
  private TcProperties tcProperties;
  private FailoverPriority failoverPriority;
  private Servers servers;

  public TcProperties getTcProperties() {
    return tcProperties;
  }

  public void setTcProperties(TcProperties value) {
    this.tcProperties = value;
  }

  public FailoverPriority getFailoverPriority() {
    return failoverPriority;
  }

  public void setFailoverPriority(FailoverPriority value) {
    this.failoverPriority = value;
  }

  public Servers getServers() {
    return servers;
  }

  public void setServers(Servers value) {
    this.servers = value;
  }
}
