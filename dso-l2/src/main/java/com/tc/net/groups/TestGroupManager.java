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
package com.tc.net.groups;

import com.tc.async.api.Sink;
import com.tc.config.NodesStore;
import com.tc.exception.TCRuntimeException;
import com.tc.net.NodeID;
import com.tc.net.ServerID;
import com.tc.net.groups.GroupMessage;
import com.tc.text.PrettyPrinter;

import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class TestGroupManager implements GroupManager<GroupMessage> {

  public NodeID                            localNodeID         = new ServerID("Test-Server", new byte[] { 5, 6, 6 });
  public LinkedBlockingQueue<GroupMessage> broadcastedMessages = new LinkedBlockingQueue<GroupMessage>();
  public LinkedBlockingQueue<Object[]>     sentMessages        = new LinkedBlockingQueue<Object[]>();

  @Override
  public NodeID getLocalNodeID() {
    return localNodeID;
  }

  @Override
  public void closeMember(ServerID next) {
    // NOP
  }

  @Override
  public NodeID join(Node thisNode, NodesStore nodeStore) {
    return localNodeID;
  }

  @Override
  public void registerForGroupEvents(GroupEventsListener listener) {
    // NOP
  }

  @Override
  public <M extends GroupMessage> void registerForMessages(Class<? extends M> msgClass, GroupMessageListener<M> listener) {
    // NOP
  }

  @Override
  public <M extends GroupMessage> void routeMessages(Class<? extends M> msgClass, Sink<M> sink) {
    // NOP
  }

  @Override
  public void sendAll(GroupMessage msg) {
    try {
      broadcastedMessages.put(msg);
    } catch (InterruptedException e) {
      throw new TCRuntimeException(e);
    }
  }

  @Override
  public void sendAll(GroupMessage msg, Set<? extends NodeID> nodeIDs) {
    try {
      broadcastedMessages.put(msg);
    } catch (InterruptedException e) {
      throw new TCRuntimeException(e);
    }
  }

  @Override
  public GroupResponse sendAllAndWaitForResponse(GroupMessage msg) {
    try {
      broadcastedMessages.put(msg);
    } catch (InterruptedException e) {
      throw new TCRuntimeException(e);
    }
    return null;
  }

  @Override
  public GroupResponse sendAllAndWaitForResponse(GroupMessage msg, Set<? extends NodeID> nodeIDs) {
    try {
      broadcastedMessages.put(msg);
    } catch (InterruptedException e) {
      throw new TCRuntimeException(e);
    }
    return null;
  }

  @Override
  public void sendTo(NodeID node, GroupMessage msg) {
    try {
      sentMessages.put(new Object[] { node, msg });
    } catch (InterruptedException e) {
      throw new TCRuntimeException(e);
    }
  }

  @Override
  public GroupMessage sendToAndWaitForResponse(NodeID nodeID, GroupMessage msg) {
    return null;
  }

  @Override
  public void setZapNodeRequestProcessor(ZapNodeRequestProcessor processor) {
    // NOP
  }

  @Override
  public void zapNode(NodeID nodeID, int type, String reason) {
    // NOP
  }

  @Override
  public boolean isNodeConnected(NodeID sid) {
    return true;
  }

  @Override
  public boolean isServerConnected(String nodeName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public PrettyPrinter prettyPrint(PrettyPrinter out) {
    throw new UnsupportedOperationException();
  }
}
