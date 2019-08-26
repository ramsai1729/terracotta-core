package com.tc.objectserver.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Topology {
  private final Set<String> servers = new HashSet<>();
  private final int voters;

  public Topology(Set<String> servers, int voters) {
    this.servers.addAll(servers);
    this.voters = voters;
  }

  public Set<String> getServers() {
    return Collections.unmodifiableSet(servers);
  }

  public int getVoters() {
    return voters;
  }
}
