package dev.deploy4j.scripts.configuration;

import java.util.List;
import java.util.Map;

public record Deploy4jConfig(
  String service,
  String image,
  List<ServerConfig> servers,
  SshOptions sshOptions,
  Map<String, String> env,
  HealthCheckConfig healthCheck
) {

}
