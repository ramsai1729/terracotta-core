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
package com.tc.l2.state;

import com.tc.net.NodeID;
import com.tc.objectserver.impl.JMXSubsystem;
import com.tc.util.Assert;

import java.util.List;
import java.util.UUID;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import com.terracotta.config.FailoverPriority;
import com.terracotta.config.Servers;
import com.terracotta.config.TcConfig;
import com.terracotta.config.latest.FailOverPriority;
import com.terracotta.config.latest.Server;
import com.terracotta.config.latest.Stripe;

/**
 *
 */
public class ConsistencyManagerImplTest {

  @Rule
  public final ExpectedSystemExit exit = ExpectedSystemExit.none();

  public ConsistencyManagerImplTest() {
  }
  
  @BeforeClass
  public static void setUpClass() {
  }
  
  @AfterClass
  public static void tearDownClass() {
  }
  
  @Before
  public void setUp() {
  }
  
  @After
  public void tearDown() {
  }

  /**
   * Test of requestTransition method, of class ConsistencyManagerImpl.
   */
  @Test
  public void testVoteThreshold() throws Exception {
    String voter = UUID.randomUUID().toString();
    ConsistencyManagerImpl impl = new ConsistencyManagerImpl(1, 1);
    JMXSubsystem caller = new JMXSubsystem();
    caller.call(ServerVoterManager.MBEAN_NAME, "registerVoter", voter);
    long term = Long.parseLong(caller.call(ServerVoterManager.MBEAN_NAME, "heartbeat", voter));
    Assert.assertTrue(term == 0);
    new Thread(()->{
      long response = 0;
      while (response >= 0) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException ie) {
          throw new RuntimeException(ie);
        }
        response = Long.parseLong(caller.call(ServerVoterManager.MBEAN_NAME, "heartbeat", voter));
      }
    }).start();
    boolean allowed = impl.requestTransition(ServerMode.PASSIVE, mock(NodeID.class), ConsistencyManager.Transition.MOVE_TO_ACTIVE);
    term = Long.parseLong(caller.call(ServerVoterManager.MBEAN_NAME, "heartbeat", voter));
    Assert.assertTrue(term > 0);
    Assert.assertFalse(allowed);
    term = Long.parseLong(caller.call(ServerVoterManager.MBEAN_NAME, "vote", voter + ":" + term));
    Assert.assertTrue(term == 0);
    term = Long.parseLong(caller.call(ServerVoterManager.MBEAN_NAME, "registerVoter", UUID.randomUUID().toString()));
    Assert.assertTrue(term < 0);
    allowed = impl.requestTransition(ServerMode.PASSIVE, mock(NodeID.class), ConsistencyManager.Transition.MOVE_TO_ACTIVE);
    Assert.assertTrue(allowed);
    Assert.assertTrue(Boolean.parseBoolean(caller.call(ServerVoterManager.MBEAN_NAME, "deregisterVoter", voter)));
  }
  
  @Test
  public void testVoteConfig() throws Exception {
    List serverList = mock(List.class);
    when(serverList.size()).thenReturn(2);
    Servers servers = mock(Servers.class);
    when(servers.getServer()).thenReturn(serverList);
    TcConfig conf = mock(TcConfig.class);
    when(conf.getServers()).thenReturn(servers);
    FailoverPriority fail = mock(FailoverPriority.class);
    String avail = "Availability";
    when(fail.getAvailability()).thenReturn(avail);
    when(conf.getFailoverPriority()).thenReturn(fail);

    Stripe stripe = Stripe.builder(
                              Server.builder().build(),
                              Server.builder().build()
                          ).withFailOverPriority(new FailOverPriority.Availability())
                          .build();
    
    Assert.assertEquals(-1, ConsistencyManager.parseVoteCount(stripe));
    
    when(conf.getFailoverPriority()).thenReturn(fail);

    stripe = Stripe.builder(
                       Server.builder().build(),
                       Server.builder().build()
                   ).withFailOverPriority(new FailOverPriority.Consistency(1))
                   .build();
    
    Assert.assertEquals(1, ConsistencyManager.parseVoteCount(stripe));

    stripe = Stripe.builder(
                       Server.builder().build(),
                       Server.builder().build()
                   ).withFailOverPriority(new FailOverPriority.Consistency(2))
                   .build();

    Assert.assertEquals(2, ConsistencyManager.parseVoteCount(stripe));

  }
  
  @Test
  public void testAddClientIsNotPersistent() throws Exception {
    ConsistencyManagerImpl impl = new ConsistencyManagerImpl(1, 1);
    long cterm = impl.getCurrentTerm();
    boolean granted = impl.requestTransition(ServerMode.ACTIVE, mock(NodeID.class), ConsistencyManager.Transition.ADD_CLIENT);
    Assert.assertFalse(granted);
    Assert.assertFalse(impl.isVoting());
    Assert.assertFalse(impl.isBlocked());
    Assert.assertEquals(cterm, impl.getCurrentTerm());
  }

  @Test
  public void testVoteConfigMandatoryForMultiNode() throws Exception {
    exit.expectSystemExitWithStatus(-1);
    Stripe stripe = Stripe.builder(
                              Server.builder().build(),
                              Server.builder().build()
                          ).build();
    ConsistencyManager.parseVoteCount(stripe);
  }

  @Test
  public void testVoteConfigNotMandatoryForSingleNode() throws Exception {
    Stripe stripe = Stripe.builder(Server.builder().build()).build();
    Assert.assertEquals(-1, ConsistencyManager.parseVoteCount(stripe));
  }

}
