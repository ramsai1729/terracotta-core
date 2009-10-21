/*
 * All content copyright (c) 2003-2008 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package com.tc.async.impl;

import com.tc.async.api.ConfigurationContext;
import com.tc.async.api.EventHandler;
import com.tc.async.api.PostInit;
import com.tc.async.api.Stage;
import com.tc.async.api.StageManager;
import com.tc.async.api.StageMonitor;
import com.tc.logging.DefaultLoggerProvider;
import com.tc.logging.TCLogger;
import com.tc.logging.TCLoggerProvider;
import com.tc.properties.TCPropertiesConsts;
import com.tc.properties.TCPropertiesImpl;
import com.tc.stats.Stats;
import com.tc.text.StringFormatter;
import com.tc.util.Assert;
import com.tc.util.concurrent.QueueFactory;
import com.tc.util.concurrent.ThreadUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author steve
 */
public class StageManagerImpl implements StageManager {

  private static final boolean MONITOR       = TCPropertiesImpl.getProperties()
                                                 .getBoolean(TCPropertiesConsts.TC_STAGE_MONITOR_ENABLED);
  private static final long    MONITOR_DELAY = TCPropertiesImpl.getProperties()
                                                 .getLong(TCPropertiesConsts.TC_STAGE_MONITOR_DELAY);

  private Map                  stages        = new HashMap();
  private TCLoggerProvider     loggerProvider;
  private final ThreadGroup    group;
  private String[]             stageNames    = new String[] {};
  private QueueFactory         queueFactory  = null;

  public StageManagerImpl(ThreadGroup threadGroup, QueueFactory queueFactory) {
    this.loggerProvider = new DefaultLoggerProvider();
    this.group = threadGroup;
    this.queueFactory = queueFactory;

    if (MONITOR) {
      startMonitor();
    }
  }

  private void startMonitor() {
    final TCLogger logger = loggerProvider.getLogger(getClass());
    Thread t = new Thread("SEDA Stage Monitor") {
      public void run() {
        while (true) {
          printStats();
          ThreadUtil.reallySleep(MONITOR_DELAY);
        }
      }

      private void printStats() {
        try {
          Stats stats[] = StageManagerImpl.this.getStats();
          logger.info("Stage Depths");
          logger.info("=================================");
          for (int i = 0; i < stats.length; i++) {
            stats[i].logDetails(logger);
          }
        } catch (Throwable th) {
          logger.error(th);
        }
      }
    };
    t.setDaemon(true);
    t.start();
  }

  public void setLoggerProvider(TCLoggerProvider loggerProvider) {
    this.loggerProvider = loggerProvider;
  }

  public synchronized Stage createStage(String name, EventHandler handler, int threads, int maxSize) {
    return createStage(name, handler, threads, threads, maxSize);
  }

  public synchronized Stage createStage(String name, EventHandler handler, int threads, int threadToQueueRatio,
                                        int maxSize) {
    int capacity = maxSize > 0 ? maxSize : Integer.MAX_VALUE;
    Stage s = new StageImpl(loggerProvider, name, handler, threads, threadToQueueRatio, group, this.queueFactory,
                            capacity);
    addStage(name, s);
    return s;
  }

  private synchronized void addStage(String name, Stage s) {
    Object prev = stages.put(name, s);
    Assert.assertNull(prev);
    s.getSink().enableStatsCollection(MONITOR);
    stageNames = (String[]) stages.keySet().toArray(new String[stages.size()]);
    Arrays.sort(stageNames);
  }

  public void startStage(Stage stage, ConfigurationContext context) {
    stage.start(context);
  }

  public synchronized void startAll(ConfigurationContext context, List<PostInit> toInit) {
    for (Iterator i = toInit.iterator(); i.hasNext();) {
      PostInit mgr = (PostInit) i.next();
      mgr.initializeContext(context);

    }
    for (Iterator i = stages.values().iterator(); i.hasNext();) {
      Stage s = (Stage) i.next();
      s.start(context);
    }
  }

  public void stopStage(Stage stage) {
    stage.destroy();
  }

  public synchronized void stopAll() {
    for (Iterator i = stages.values().iterator(); i.hasNext();) {
      Stage s = (Stage) i.next();
      s.destroy();
    }
  }

  public synchronized Stage getStage(String name) {
    return (Stage) stages.get(name);
  }

  public synchronized Stats[] getStats() {
    final String[] names = stageNames;
    final Stats[] stats = new Stats[names.length];

    for (int i = 0; i < names.length; i++) {
      stats[i] = getStage(names[i]).getSink().getStats(MONITOR_DELAY);
    }
    return stats;
  }

  public synchronized Collection getStages() {
    return stages.values();
  }

  static class StageMonitors {

    private final List            monitors  = Collections.synchronizedList(new LinkedList());
    private final StringFormatter formatter = new StringFormatter();

    StageMonitors(final TCLogger logger) {
      return;
    }

    public StageMonitor newStageMonitor(String name) {
      return new NullStageMonitor();
    }

    public String toString() {
      StringBuffer buf = new StringBuffer();
      buf.append("StageMonitors").append(formatter.newline());
      for (Iterator i = Collections.unmodifiableList(monitors).iterator(); i.hasNext();) {
        buf.append(((StageMonitorImpl) i.next()).dumpAndFlush()).append(formatter.newline());
      }
      return buf.toString();
    }
  }

}
