package dev.deploy4j.scripts.configuration;

public record Deploy4jConfiguration(
  String service,
  String image,
  String host,
  SshOptions sshOptions,
  String runDirectory
) {

  public String absoluteImage() {
    // "#{repository}:#{version}"
    return this.image;
  }

}
