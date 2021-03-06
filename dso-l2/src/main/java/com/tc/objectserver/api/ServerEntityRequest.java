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
package com.tc.objectserver.api;

import org.terracotta.entity.ClientDescriptor;
import org.terracotta.exception.EntityException;

import com.tc.net.NodeID;
import com.tc.object.tx.TransactionID;


public interface ServerEntityRequest {

  ServerEntityAction getAction();

  NodeID getNodeID();
  
  TransactionID getTransaction();
  
  TransactionID getOldestTransactionOnClient();
  /**
   * The descriptor referring to the specific client-side object instance which issued the request.
   */
  ClientDescriptor getSourceDescriptor();

  byte[] getPayload();

  void complete();

  void complete(byte[] value);

  void failure(EntityException e);

  void received();

  boolean requiresReplication();

  /**
   * NOTE:  This method is only used for one kind of sync message so we probably need to refactor this to avoid these 
   * tendrils of specialization.
   * 
   * @return The concurrency key set on the request
   */
  int getConcurrencyKey();
}
