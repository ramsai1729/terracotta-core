package com.tc.objectserver.impl;

public class TopologyProvider {

  private volatile Topology topology;

  private TopologyProvider() {}

  private static final TopologyProvider INSTANCE = new TopologyProvider();

  public static TopologyProvider get() {
    return INSTANCE;
  }

  public Topology getTopology() {
    return topology;
  }

  public void setTopology(Topology newTopology) {
    this.topology = newTopology;
  }
}
