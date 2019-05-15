package com.tc.config;

import org.terracotta.config.FailoverPriority;
import org.terracotta.config.Servers;
import org.terracotta.config.TcConfig;
import org.terracotta.config.TcProperties;
import org.terracotta.config.Voter;

import com.terracotta.config.latest.FailOverPriority;
import com.terracotta.config.latest.Server;
import com.terracotta.config.latest.Stripe;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

class ConversionUtil {
  static Stripe getStripe(TcConfig tcConfig) {
    Stripe.Builder builder = getStripeBuilder(tcConfig);

    addFailOverPriority(builder, tcConfig);
    addProperties(builder, tcConfig);

    return builder.build();
  }

  private static Stripe.Builder getStripeBuilder(TcConfig tcConfig) {
    Servers servers = tcConfig.getServers();

    if (servers == null || servers.getServer() == null || servers.getServer().size() == 0) {
      throw new RuntimeException("No servers configured");
    }

    List<org.terracotta.config.Server> tcConfigServerList = servers.getServer();

    Server[] serverList = new Server[tcConfigServerList.size()];

    for (int i = 0; i < tcConfigServerList.size(); i++) {
      org.terracotta.config.Server server = tcConfigServerList.get(i);
      Server.Builder serverBuilder = Server.builder();

      if (server.getName() != null) {
        serverBuilder.withName(server.getName());
      }

      if (server.getHost() != null) {
        serverBuilder.withHost(server.getHost());
      }

      if (server.getBind() != null) {
        serverBuilder.withDefaultBind(server.getBind());
      }

      if (server.getTsaPort() != null) {
        String bind = server.getTsaPort().getBind();

        if (bind != null) {
          serverBuilder.withTsaBind(bind);
        }

        serverBuilder.withTsaPort(server.getTsaPort().getValue());
      }

      if (server.getTsaGroupPort() != null) {
        String bind = server.getTsaGroupPort().getBind();

        if (bind != null) {
          serverBuilder.withTsaGroupBind(bind);
        }

        serverBuilder.withTsaGroupPort(server.getTsaGroupPort().getValue());
      }

      if (server.getLogs() != null) {
        serverBuilder.withLogs(server.getLogs());
      }

      serverList[i] = serverBuilder.build();
    }

    Stripe.Builder builder = Stripe.builder(serverList[0], Arrays.copyOfRange(serverList, 1, serverList.length));

    if (servers.getClientReconnectWindow() != null) {
      builder.withReconnectWindow(servers.getClientReconnectWindow());
    }

    return builder;
  }

  private static void addProperties(Stripe.Builder builder, TcConfig tcConfig) {
    TcProperties tcProperties = tcConfig.getTcProperties();

    if (tcProperties == null) {
      return;
    }

    Properties properties = new Properties();

    for (org.terracotta.config.Property property : tcProperties.getProperty()) {
      properties.put(property.getName(), property.getValue());
    }

    builder.withProperties(properties);
  }

  private static void addFailOverPriority(Stripe.Builder builder, TcConfig tcConfig) {
    FailoverPriority failoverPriority = tcConfig.getFailoverPriority();

    if (failoverPriority == null) {
      return;
    }

    if (failoverPriority.getAvailability() != null) {
      builder.withFailOverPriority(new FailOverPriority.Availability());
    } else if (failoverPriority.getConsistency() != null) {
      Voter voter = failoverPriority.getConsistency().getVoter();
      int voterCount = voter == null ? 0 : voter.getCount();
      builder.withFailOverPriority(new FailOverPriority.Consistency(voterCount));
    }
  }
}
