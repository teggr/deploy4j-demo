package dev.deploy4j.scripts.configuration;

public record SshOptions(
  int port,
  String privateKey,
  String privateKeyPassphrase,
  boolean strictHostChecking
) {
}
