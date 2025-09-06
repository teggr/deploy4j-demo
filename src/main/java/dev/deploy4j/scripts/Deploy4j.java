package dev.deploy4j.scripts;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.deploy4j.scripts.Commands.*;

public class Deploy4j {

  public static void main(String[] args) throws JSchException, IOException {

    System.out.println("Hello, Deploy4j!");

    // what's the image
    String image = "teggr/deploy4j-demo:latest";

    // ssh onto the the box
    String host = "localhost";
    int port = 2222;

    // identity
    String privateKey = System.getenv("PRIVATE_KEY");
    String privateKeyPassphrase = System.getenv("PRIVATE_KEY_PASSPHRASE");

    // setup ssh environment
    JSch jsch = new JSch();
    jsch.addIdentity(privateKey, privateKeyPassphrase);

    // create a session
    Session session = jsch.getSession("root", host, port);
    session.setConfig("StrictHostKeyChecking", "no");
    session.connect();

    System.out.println("Connected to " + host + ":" + port);

    System.out.println("\n\nTesting super user...");

    cmd command = new cmd( "[ \"${EUID:-$(id -u)}\" -eq 0 ] || command -v sudo >/dev/null || command -v su >/dev/null" );

    ExecResult exec = exec(session, command);
    System.out.println("Exit status: " + exec.exitStatus);
    System.out.println("Output: " + exec.execOutput);
    System.out.println("Error Output: " + exec.execErrorOutput);

    System.out.println("\n\nChecking if docker is installed...");

     command = new cmd("docker", "-v");
     exec = exec(session, command);
    System.out.println("Exit status: " + exec.exitStatus);
    System.out.println("Output: " + exec.execOutput);
    System.out.println("Error Output: " + exec.execErrorOutput);

    if(exec.exitStatus != 0){

      System.out.println("\n\nInstalling docker (may take a minute)...");

      command = pipe( getDocker(), new cmd("sh") );
      exec = exec(session, command);
      System.out.println("Exit status: " + exec.exitStatus);
      System.out.println("Output: " + exec.execOutput);
      System.out.println("Error Output: " + exec.execErrorOutput);

    }

    // run our proxy
    // is the proxy running?
    // if not, run it

    // run the container

    // update the proxy

    session.disconnect();

  }

  private static cmd getDocker() {
    return shell(
      any(
        new cmd( "curl", "-fsSL", "https://get.docker.com" ),
        new cmd( "wget", "-O -", "https://get.docker.com" ),
        new cmd( "echo", "\"exit 1\"")
      )
    );
  }

  private static ExecResult exec(Session session, cmd command) throws JSchException, IOException {

    int exitStatus = -1;
    ByteArrayOutputStream capturedErrorStream = new ByteArrayOutputStream();
    ByteArrayOutputStream capturedInputStream = new ByteArrayOutputStream();

    ChannelExec channel = null;
    try {

      channel = (ChannelExec) session.openChannel("exec");
      String collect = Stream.of(command).flatMap(cmd -> Stream.of(cmd.cmds())).collect(Collectors.joining(" "));
      System.out.println("Executing: " + collect);
      channel.setCommand(collect);
      channel.setInputStream(null);
      channel.setErrStream(capturedErrorStream);

      InputStream in = channel.getInputStream();
      channel.connect();
      IOUtils.copy(in, capturedInputStream);

      exitStatus = channel.getExitStatus();

    } finally {
      if (channel != null) {
        try {
          channel.disconnect();
        } catch (Exception e) {
        }
      }
    }

    return new ExecResult(exitStatus, capturedInputStream.toString(StandardCharsets.UTF_8), capturedErrorStream.toString(StandardCharsets.UTF_8));

  }

  public record ExecResult(int exitStatus, String execOutput, String execErrorOutput) {
  }

}
