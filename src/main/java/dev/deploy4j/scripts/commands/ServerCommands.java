package dev.deploy4j.scripts.commands;

import dev.deploy4j.scripts.Cmd;
import dev.deploy4j.scripts.configuration.Deploy4jConfig;

public class ServerCommands {

  private final Deploy4jConfig configuration;

  public ServerCommands(Deploy4jConfig configuration) {
    this.configuration = configuration;
  }

  public Cmd ensureRunDirectory() {
      return Cmd.cmd("mkdir", "-p", configuration.runDirectory() );
  }

}
