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
package com.tc.object;

import com.tc.async.api.Sink;
import com.tc.logging.TCLogger;
import com.tc.management.EnterpriseL1Management;
import com.tc.management.L1Management;
import com.tc.management.TCClient;
import com.tc.management.beans.MBeanNames;
import com.tc.management.beans.TerracottaOperatorEventsMBean;
import com.tc.management.remote.protocol.terracotta.TunnelingEventHandler;
import com.tc.net.core.ConnectionAddressProvider;
import com.tc.net.protocol.tcm.ClientMessageChannel;
import com.tc.net.protocol.tcm.CommunicationsManager;
import com.tc.object.config.PreparedComponentsFromL2Connection;
import com.tc.object.context.PauseContext;
import com.tc.object.handshakemanager.ClientHandshakeCallback;
import com.tc.object.handshakemanager.ClientHandshakeManager;
import com.tc.object.handshakemanager.EnterpriseClientHandshakeManagerImpl;
import com.tc.object.msg.ClientHandshakeMessageFactory;
import com.tc.object.session.SessionManager;
import com.tc.object.session.SessionProvider;
import com.tc.operatorevent.EnterpriseLongGCLogger;
import com.tc.operatorevent.TerracottaOperatorEventCallbackLogger;
import com.tc.operatorevent.TerracottaOperatorEventLogger;
import com.tc.operatorevent.TerracottaOperatorEventLogging;
import com.tc.runtime.logging.LongGCLogger;
import com.tcclient.cluster.ClusterInternalEventsGun;

import java.io.IOException;
import java.util.Collection;

public class EnterpriseNonAAClientBuilder extends StandardClientBuilder {

  @Override
  public ClientMessageChannel createClientMessageChannel(CommunicationsManager commMgr,
                                                         PreparedComponentsFromL2Connection connComp,
                                                         SessionProvider sessionProvider,
                                                         int maxReconnectTries,
                                                         int socketConnectTimeout, TCClient client) {
    final ConnectionAddressProvider cap = createConnectionAddressProvider(connComp);
    client.addServerConfigurationChangedListeners(cap);
    return commMgr.createClientChannel(sessionProvider, maxReconnectTries, null, 0, socketConnectTimeout, cap);
  }

  @Override
  public L1Management createL1Management(TunnelingEventHandler teh, String rawConfigText,
                                         DistributedObjectClient distributedObjectClient) {
    return new EnterpriseL1Management(teh, rawConfigText, distributedObjectClient);
  }

  @Override
  public void registerForOperatorEvents(L1Management l1Management) {
    TerracottaOperatorEventsMBean mbean;
    try {
      mbean = (TerracottaOperatorEventsMBean) l1Management.findMBean(MBeanNames.OPERATOR_EVENTS_PUBLIC,
                                                                     TerracottaOperatorEventsMBean.class);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }

    final TerracottaOperatorEventLogger tcEventLogger = TerracottaOperatorEventLogging.getEventLogger();
    tcEventLogger.registerEventCallback(mbean);
    tcEventLogger.registerEventCallback(new TerracottaOperatorEventCallbackLogger());
  }

  @Override
  public ClientHandshakeManager createClientHandshakeManager(TCLogger logger,
                                                             ClientHandshakeMessageFactory chmf, Sink<PauseContext> pauseSink,
                                                             SessionManager sessionManager,
                                                             ClusterInternalEventsGun clusterEventsGun,
                                                             String clientVersion,
                                                             Collection<ClientHandshakeCallback> callbacks) {
    return new EnterpriseClientHandshakeManagerImpl(logger, chmf, pauseSink, sessionManager, clusterEventsGun,
                                                    clientVersion, callbacks);
  }

  @Override
  public LongGCLogger createLongGCLogger(long gcTimeOut) {
    return new EnterpriseLongGCLogger(gcTimeOut);
  }

}
