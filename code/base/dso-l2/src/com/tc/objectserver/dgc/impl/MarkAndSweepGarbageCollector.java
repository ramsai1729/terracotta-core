/*
 * All content copyright (c) 2003-2008 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package com.tc.objectserver.dgc.impl;

import com.tc.logging.TCLogger;
import com.tc.logging.TCLogging;
import com.tc.object.ObjectID;
import com.tc.objectserver.api.ObjectManager;
import com.tc.objectserver.context.GCResultContext;
import com.tc.objectserver.core.api.Filter;
import com.tc.objectserver.dgc.api.GarbageCollector;
import com.tc.objectserver.dgc.api.GarbageCollectorEventListener;
import com.tc.objectserver.impl.ObjectManagerConfig;
import com.tc.objectserver.l1.api.ClientStateManager;
import com.tc.text.PrettyPrinter;
import com.tc.util.ObjectIDSet;
import com.tc.util.State;
import com.tc.util.concurrent.LifeCycleState;
import com.tc.util.concurrent.NullLifeCycleState;
import com.tc.util.concurrent.StoppableThread;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 */
public class MarkAndSweepGarbageCollector implements GarbageCollector {

  static final TCLogger                            logger                     = TCLogging
                                                                                  .getLogger(MarkAndSweepGarbageCollector.class);

  private static final LifeCycleState              NULL_LIFECYCLE_STATE       = new NullLifeCycleState();

  private final AtomicInteger                      gcIterationCounter         = new AtomicInteger(0);
  private final GarbageCollectionInfoPublisherImpl gcPublisher                = new GarbageCollectionInfoPublisherImpl();
  private final ObjectManagerConfig                objectManagerConfig;
  private final ClientStateManager                 stateManager;
  private final ObjectManager                      objectManager;

  private State                                    state                      = GC_SLEEP;
  private volatile ChangeCollector                 referenceCollector         = ChangeCollector.NULL_CHANGE_COLLECTOR;
  private volatile YoungGenChangeCollector         youngGenReferenceCollector = YoungGenChangeCollector.NULL_YOUNG_CHANGE_COLLECTOR;
  private volatile LifeCycleState                  gcState                    = new NullLifeCycleState();
  private volatile boolean                         started                    = false;

  public MarkAndSweepGarbageCollector(ObjectManagerConfig objectManagerConfig, ObjectManager objectMgr,
                                      ClientStateManager stateManager) {
    this.objectManagerConfig = objectManagerConfig;
    this.objectManager = objectMgr;
    this.stateManager = stateManager;
    addListener(new GCLoggerEventPublisher(logger, objectManagerConfig.verboseGC()));
  }

  public void doGC(GCType type) {
    GCHook hook = null;
    switch (type) {
      case FULL_GC:
        hook = new FullGCHook(this, this.objectManager, this.stateManager);
        break;
      case YOUNG_GEN_GC:
        hook = new YoungGCHook(this, this.objectManager, this.stateManager, this.youngGenReferenceCollector);
        break;
    }
    MarkAndSweepGCAlgorithm gcAlgo = new MarkAndSweepGCAlgorithm(this, hook, this.gcPublisher, this.gcState,
                                                                 this.gcIterationCounter.incrementAndGet());
    gcAlgo.doGC();
  }

  public boolean deleteGarbage(GCResultContext gcResult) {
    if (requestGCDeleteStart()) {
      this.youngGenReferenceCollector.removeGarbage(gcResult.getGCedObjectIDs());
      this.objectManager.notifyGCComplete(gcResult);
      notifyGCComplete();
      return true;
    }
    return false;
  }

  public void startMonitoringReferenceChanges() {
    this.referenceCollector = new NewReferenceCollector();
    this.youngGenReferenceCollector.startMonitoringChanges();
  }

  public void stopMonitoringReferenceChanges() {
    this.referenceCollector = ChangeCollector.NULL_CHANGE_COLLECTOR;
    this.youngGenReferenceCollector.stopMonitoringChanges();
  }

  public void changed(ObjectID changedObject, ObjectID oldReference, ObjectID newReference) {
    this.referenceCollector.changed(changedObject, oldReference, newReference);
  }

  public void notifyObjectCreated(ObjectID id) {
    this.youngGenReferenceCollector.notifyObjectCreated(id);
  }

  public void notifyNewObjectInitalized(ObjectID id) {
    this.youngGenReferenceCollector.notifyObjectInitalized(id);
  }

  public void notifyObjectsEvicted(Collection evicted) {
    this.youngGenReferenceCollector.notifyObjectsEvicted(evicted);
  }

  public void addNewReferencesTo(Set rescueIds) {
    this.referenceCollector.addNewReferencesTo(rescueIds);
  }

  /**
   * Used for Tests.
   */
  ObjectIDSet collect(GCHook hook, Filter filter, Collection rootIds, ObjectIDSet managedObjectIds) {
    return collect(hook, filter, rootIds, managedObjectIds, NULL_LIFECYCLE_STATE);
  }

  /**
   * Used for Tests.
   */
  ObjectIDSet collect(GCHook hook, Filter traverser, Collection roots, ObjectIDSet managedObjectIds,
                      LifeCycleState lstate) {
    MarkAndSweepGCAlgorithm gcAlgo = new MarkAndSweepGCAlgorithm(this, hook, this.gcPublisher, this.gcState,
                                                                 this.gcIterationCounter.incrementAndGet());
    return gcAlgo.collect(traverser, roots, managedObjectIds, lstate);
  }

  public void start() {
    if (this.objectManagerConfig.isYoungGenDGCEnabled()) {
      this.youngGenReferenceCollector = new YoungGenChangeCollectorImpl();
    }
    this.started = true;
    this.gcState.start();
  }

  public void stop() {
    this.started = false;
    int count = 0;
    while (!this.gcState.stopAndWait(5000) && (count < 6)) {
      count++;
      logger.warn("GC Thread did not stop");
    }
  }

  public boolean isStarted() {
    return this.started;
  }

  public void setState(StoppableThread st) {
    this.gcState = st;
  }

  public void addListener(GarbageCollectorEventListener listener) {
    this.gcPublisher.addListener(listener);
  }

  public synchronized boolean requestGCStart() {
    if (this.started && this.state == GC_SLEEP) {
      this.state = GC_RUNNING;
      return true;
    }
    // Can't start GC
    return false;
  }

  public synchronized void enableGC() {
    if (GC_DISABLED == this.state) {
      this.state = GC_SLEEP;
    } else {
      logger.warn("GC is already enabled : " + this.state);
    }
  }

  public synchronized boolean disableGC() {
    if (GC_SLEEP == this.state) {
      this.state = GC_DISABLED;
      return true;
    }
    // GC is already running, can't be disabled
    return false;
  }

  public synchronized void notifyReadyToGC() {
    if (this.state == GC_PAUSING) {
      this.state = GC_PAUSED;
    }
  }

  public synchronized void notifyGCComplete() {
    this.state = GC_SLEEP;
  }

  /**
   * In Active server, state transitions from GC_PAUSED to GC_DELETE and in the passive server, state transitions from
   * GC_SLEEP to GC_DELETE.
   */
  private synchronized boolean requestGCDeleteStart() {
    if (this.state == GC_SLEEP || this.state == GC_PAUSED) {
      this.state = GC_DELETE;
      return true;
    }
    return false;
  }

  public synchronized void requestGCPause() {
    this.state = GC_PAUSING;
  }

  public synchronized boolean isPausingOrPaused() {
    return GC_PAUSED == this.state || GC_PAUSING == this.state;
  }

  public synchronized boolean isPaused() {
    return this.state == GC_PAUSED;
  }

  public synchronized boolean isDisabled() {
    return GC_DISABLED == this.state;
  }

  public synchronized PrettyPrinter prettyPrint(PrettyPrinter out) {
    return out.print(getClass().getName()).print("[").print(this.state).print("]");
  }
}
