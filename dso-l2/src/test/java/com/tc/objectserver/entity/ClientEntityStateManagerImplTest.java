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

package com.tc.objectserver.entity;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import com.tc.async.api.Sink;
import com.tc.entity.VoltronEntityMessage;
import com.tc.net.ClientID;
import com.tc.net.protocol.tcm.MessageChannel;
import com.tc.object.ClientInstanceID;
import com.tc.object.EntityDescriptor;
import com.tc.object.EntityID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class ClientEntityStateManagerImplTest {
  private ClientEntityStateManager clientEntityStateManager;
  private Sink<VoltronEntityMessage> requestSink;

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() throws Exception {
    requestSink = mock(Sink.class);
    clientEntityStateManager = new ClientEntityStateManagerImpl(requestSink);
  }

  @Test
  public void testAddForNewClient() throws Exception {
    assertTrue(clientEntityStateManager.addReference(new ClientID(1), new EntityDescriptor(new EntityID("foo", "bar"), new ClientInstanceID(1), 1)));
  }

  @Test
  public void testAddTwice() throws Exception {
    assertTrue(clientEntityStateManager.addReference(new ClientID(1), new EntityDescriptor(new EntityID("foo", "bar"), new ClientInstanceID(1), 1)));
    assertFalse(clientEntityStateManager.addReference(new ClientID(1), new EntityDescriptor(new EntityID("foo", "bar"), new ClientInstanceID(1), 1)));
  }

  @Test
  public void testRemoveUnknown() throws Exception {
    assertFalse(clientEntityStateManager.removeReference(new ClientID(1), new EntityDescriptor(new EntityID("foo", "bar"), new ClientInstanceID(1), 1)));
  }

  @Test
  public void testChannelRemoved() throws Exception {
    EntityID entityID = new EntityID("foo", "bar");
    ClientInstanceID clientInstanceID = new ClientInstanceID(1);
    long version = 1;
    ClientID clientID = new ClientID(1);
    MessageChannel messageChannel = mock(MessageChannel.class);
    when(messageChannel.getRemoteNodeID()).thenReturn(clientID);

    clientEntityStateManager.addReference(clientID, new EntityDescriptor(entityID, clientInstanceID, version));
    clientEntityStateManager.channelRemoved(messageChannel);

    verify(requestSink).addSingleThreaded(argThat(hasClientAndEntityIDs(clientID, entityID)));
  }

  private Matcher<VoltronEntityMessage> hasClientAndEntityIDs(final ClientID clientID, final EntityID entityID) {
    return new BaseMatcher<VoltronEntityMessage>() {
      @Override
      public boolean matches(Object o) {
        if (o instanceof VoltronEntityMessage) {
          VoltronEntityMessage message = (VoltronEntityMessage) o;
          return message.getEntityDescriptor().getEntityID().equals(entityID) && message.getSource().equals(clientID);
        }
        return false;
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("VoltronEntityMessage with (" + clientID + ", " + entityID + ")");
      }
    };
  }
}