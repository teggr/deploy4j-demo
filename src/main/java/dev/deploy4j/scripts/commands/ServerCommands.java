package dev.deploy4j.scripts.commands;

import dev.deploy4j.scripts.Cmd;
import dev.deploy4j.scripts.configuration.Deploy4jConfiguration;

public class ServerCommands {

  private final Deploy4jConfiguration configuration;

  public ServerCommands(Deploy4jConfiguration configuration) {
    this.configuration = configuration;
  }

  public Cmd ensureRunDirectory() {
      return Cmd.cmd("mkdir", "-p", configuration.runDirectory() );
  }

}
