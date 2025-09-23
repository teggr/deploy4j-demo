package dev.deploy4j.scripts.commands;

import dev.deploy4j.scripts.Cmd;
import dev.deploy4j.scripts.configuration.Deploy4jConfig;

import static dev.deploy4j.scripts.Commands.any;
import static dev.deploy4j.scripts.Commands.pipe;

public class BuilderCommands {

  private final Deploy4jConfig configuration;

  public BuilderCommands(Deploy4jConfig configuration) {
    this.configuration = configuration;
  }

  public  Cmd clean() {
    return Cmd.cmd("docker", "image", "rm", "--force", configuration.absoluteImage());
  }

  public  Cmd pull() {
    return Cmd.cmd("docker", "pull", configuration.absoluteImage());
  }

  public  Cmd validateImage() {
    return pipe(
      Cmd.cmd("docker", "inspect", "-f", "'{{ .Config.Labels.service }}'", configuration.absoluteImage()),
      any(
        Cmd.cmd("grep", "-x", configuration.absoluteImage()),
        Cmd.cmd("(echo \"Image " + configuration.absoluteImage() + " is missing the 'service' label\" && exit 1)")
      )
    );
  }
}
