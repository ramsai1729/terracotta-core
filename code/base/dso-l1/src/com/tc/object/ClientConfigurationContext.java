/*
 * All content copyright (c) 2003-2008 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package com.tc.object;

import com.tc.async.api.StageManager;
import com.tc.async.impl.ConfigurationContextImpl;
import com.tc.object.handshakemanager.ClientHandshakeManager;
import com.tc.object.locks.ClientLockManager;
import com.tc.object.tx.ClientTransactionManager;

public class ClientConfigurationContext extends ConfigurationContextImpl {

  public final static String             LOCK_RESPONSE_STAGE                  = "lock_response_stage";
  public final static String             LOCK_RECALL_STAGE                    = "lock_recall_stage";
  public final static String             RECEIVE_ROOT_ID_STAGE                = "receive_root_id_stage";
  public final static String             RECEIVE_OBJECT_STAGE                 = "receive_object_stage";
  public final static String             RECEIVE_TRANSACTION_STAGE            = "receive_transaction_stage";
  public final static String             OBJECT_ID_REQUEST_RESPONSE_STAGE     = "object_id_request_response_stage";
  public final static String             RECEIVE_TRANSACTION_COMPLETE_STAGE   = "receive_transaction_complete_stage";
  public final static String             HYDRATE_MESSAGE_STAGE                = "hydrate_message_stage";
  public final static String             BATCH_TXN_ACK_STAGE                  = "batch_txn_ack_stage";
  public static final String             CONFIG_MESSAGE_STAGE                 = "config_message_stage";
  public static final String             CLIENT_COORDINATION_STAGE            = "client_coordination_stage";
  public static final String             JMXREMOTE_TUNNEL_STAGE               = "jmxremote_tunnel_stage";
  public static final String             DMI_STAGE                            = "dmi_stage";
  public static final String             LOCK_STATISTICS_RESPONSE_STAGE       = "lock_statistics_response_stage";
  public static final String             LOCK_STATISTICS_ENABLE_DISABLE_STAGE = "lock_statistics_enable_disable_stage";
  public static final String             OOO_NET_SEND_STAGE                   = "ooo_net_send_stage";
  public static final String             OOO_NET_RECEIVE_STAGE                = "ooo_net_receive_stage";
  public static final String             CLUSTER_METADATA_STAGE               = "cluster_metadata_stage";
  public static final String             CLUSTER_MEMBERSHIP_EVENT_STAGE       = "cluster_membership_event_stage";

  private final ClientLockManager        lockManager;
  private final RemoteObjectManager      remoteObjectManager;
  private final ClientTransactionManager txManager;
  private final ClientHandshakeManager   clientHandshakeManager;
  private final ClusterMetaDataManager   clusterMetaDataManager;

  public ClientConfigurationContext(final StageManager stageManager, final ClientLockManager lockManager,
                                    final RemoteObjectManager remoteObjectManager, final ClientTransactionManager txManager,
                                    final ClientHandshakeManager clientHandshakeManager, final ClusterMetaDataManager clusterMetaDataManager) {
    super(stageManager);
    this.lockManager = lockManager;
    this.remoteObjectManager = remoteObjectManager;
    this.txManager = txManager;
    this.clientHandshakeManager = clientHandshakeManager;
    this.clusterMetaDataManager = clusterMetaDataManager;
  }

  public ClientLockManager getLockManager() {
    return lockManager;
  }

  public RemoteObjectManager getObjectManager() {
    return remoteObjectManager;
  }

  public ClientTransactionManager getTransactionManager() {
    return txManager;
  }

  public ClientHandshakeManager getClientHandshakeManager() {
    return clientHandshakeManager;
  }

  public ClusterMetaDataManager getClusterMetaDataManager() {
    return clusterMetaDataManager;
  }

}
