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
package com.tc.util.concurrent;

import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import com.tc.async.impl.Event;

public class QueueFactory {

  public static final int MAX_QUEUE_CAPACITY = 10240;


  public <E> BlockingQueue<Event> createInstance(Class<E> type) {
    return new LinkedBlockingQueue<>();
  }

  public <E> BlockingQueue<Event> createInstance(Class<E> type, int capacity) {
    if (capacity > MAX_QUEUE_CAPACITY) {
      LoggerFactory.getLogger(QueueFactory.class).info("Requested {} size but using only {} size", capacity, MAX_QUEUE_CAPACITY);
      capacity = MAX_QUEUE_CAPACITY;
    }
    return new ArrayBlockingQueue<>(capacity);
  }
}
