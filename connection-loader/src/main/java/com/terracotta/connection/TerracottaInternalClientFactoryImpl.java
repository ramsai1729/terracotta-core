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
package com.terracotta.connection;

import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.Set;
import java.util.function.Supplier;


public class TerracottaInternalClientFactoryImpl implements TerracottaInternalClientFactory {
  @Override
  public TerracottaInternalClient createL1Client(Supplier<Set<InetSocketAddress>> serverAddressesSupplier,
                                                 Properties properties) {
    return new TerracottaInternalClientImpl(serverAddressesSupplier, properties);
  }
}