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
package com.tcclient.cluster;

import com.tc.async.api.Stage;
import com.tc.cluster.Cluster;


public interface ClusterInternal extends Cluster, ClusterInternalEventsGun, ClusterEventsNotifier {
  public static enum ClusterEventType {
    NODE_JOIN("Node Joined"), NODE_LEFT("Node Left"), OPERATIONS_ENABLED("Operations Enabled"), OPERATIONS_DISABLED(
        "Operations Disabled"), NODE_ERROR("NODE ERROR");

    private final String name;

    private ClusterEventType(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return this.name;
    }
  }

  public void init(Stage<ClusterInternalEventsContext> clusterEventsStage);

  public void shutdown();

  public void cleanup();
}