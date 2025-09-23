package dev.deploy4j.scripts.configuration;

import java.util.List;
import java.util.Map;

import static dev.deploy4j.scripts.Commands.argumentize;

public record EnvTag(String name, Map<String, String> env) {
  public void merge(EnvTag envTag) {
    this.env.putAll(envTag.env());
  }

  public String[] args() {
    return argumentize("--env", this.env);
  }
}
