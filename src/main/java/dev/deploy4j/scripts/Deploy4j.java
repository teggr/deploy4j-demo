package dev.deploy4j.scripts;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import dev.deploy4j.scripts.commands.BuilderCommands;
import dev.deploy4j.scripts.commands.DockerCommands;
import dev.deploy4j.scripts.commands.ServerCommands;
import dev.deploy4j.scripts.commands.TraefikCommands;
import dev.deploy4j.scripts.config.Traefik;
import dev.deploy4j.scripts.configuration.Deploy4jConfiguration;
import dev.deploy4j.scripts.configuration.SshOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Deploy4j {

  public static void main(String[] args) throws JSchException, IOException {

    System.out.println("Hello, Deploy4j!");

    Deploy4jConfiguration configuration = new Deploy4jConfiguration(
      "deploy4j-demo",
      "teggr/deploy4j-demo:0.0.1-SNAPSHOT",
      "localhost",
      new SshOptions(
        2222,
        System.getenv("PRIVATE_KEY"),
        System.getenv("PRIVATE_KEY_PASSPHRASE"),
        false
      ),
      ".deploy4j"
    );

    // setup ssh environment
    JSch jsch = new JSch();
    jsch.addIdentity(configuration.sshOptions().privateKey(), configuration.sshOptions().privateKeyPassphrase());

    // create a session
    Session session = jsch.getSession("root", configuration.host(), configuration.sshOptions().port());
    if(!configuration.sshOptions().strictHostChecking()) {
      session.setConfig("StrictHostKeyChecking", "no");
    }
    try {
      session.connect();

      Host hostSession = new Host(session);

      System.out.println("Connected to " + configuration.host() + ":" + configuration.sshOptions().port());


      long start = System.currentTimeMillis();

      try {

        DockerCommands dockerCommands = new DockerCommands();
        BuilderCommands builderCommands = new BuilderCommands(configuration);
        ServerCommands serverCommands = new ServerCommands(configuration);

        // bootstrap
        System.out.println("=================================");
        System.out.println("Checking if docker is installed...\n");

        Host.ExecResult exec = hostSession.exec(dockerCommands.installed());

        if (exec.exitStatus() != 0) {

          System.out.println("=================================");
          System.out.println("Testing super user...\n");

          exec = hostSession.exec(dockerCommands.isSuperUser());

          if (exec.exitStatus() != 0) {
            throw new IllegalStateException("You need to be root or have sudo/su installed to proceed with installing docker.");
          }

          System.out.println("=================================");
          System.out.println("Installing docker (may take a minute)...\n");

          exec = hostSession.exec( dockerCommands.install());

        }

        System.out.println("=================================");
        System.out.println("Ensuring run directory...\n");

        exec = hostSession.exec(serverCommands.ensureRunDirectory());

        // push env files?

        // accessory boot

        // deploy

        // login into registry if needed
        // pull the image
        System.out.println("=================================");
        System.out.println("Pulling image " + configuration.image() + " ...\n");

        // clean
        exec = hostSession.exec( builderCommands.clean() );

        // pull
        exec = hostSession.exec( builderCommands.pull() );

        // validate
        exec = hostSession.exec( builderCommands.validateImage() );

        // ensure traefik is runnning
        Traefik traefik = new Traefik();
        TraefikCommands traefikCommands = new TraefikCommands(traefik);

        exec = hostSession.exec(traefikCommands.startOrRun());

        // detect stale containers
        // app role + host + configuration
        // list+versions
//        command =
//          pipe(
//            Cmd.cmd("docker", "ps" )
//              .args( argumentize("--filter", filters()) ),
//            Cmd.cmd(
//              // Extract SHA from "service-role-dest-SHA"
//              "while read line; do echo ${line##"+containerPrefix()+"-}; done"
//            )
//          );
//        exec = exec(session, command);
//        List<String> versions = Stream.of(exec.execOutput.split("\n"))
//          .filter(s -> s != null && !s.isBlank())
//          .distinct()
//          .toList();
//
//        command = pipe(
//          pipe(
//            shell(
//              chain(
//                // latest container
//                Cmd.cmd(
//                  "docker", "ps", "--latest", format,
//                  filterArgs(statuses ACTIVE
//                )
//              )
//            ),
//            Cmd.cmd("head", "-1")
//          ),
//          Cmd.cmd(
//            "while read line; do " +
//              "  id=${line%% *}; " +
//              "  name=${line#* }; " +
//              "  version=${name##"+containerPrefix()+"-}; " +
//              "  if [[ ! \" " + String.join(" ", versions) + " \" =~ \" ${version} \" ]]; then " +
//              "    echo $id; " +
//              "  fi; " +
//              "done"
//          )
//        );
//        )

        // app boot


        // prune old contains and images

      } finally {

        long end = System.currentTimeMillis();

        System.out.println("=================================");
        System.out.println("Deployed in " + (end - start) / 1000 + " seconds");

      }

    } finally {
      try {
        session.disconnect();
      } catch (Exception e) {
      }
    }

  }


  private static String containerPrefix() {
    // [ config.service, name, config.destination ].compact.join("-")
    return "deploy4j-demo-web-prod";
  }

  private static List<String> filters() {
    List<String> filters = new ArrayList<>();
    filters.add("label=service=deploy4j-demo");
//    filters.add("label=destination=#");
//    filters.add("label=role=#");
    // List.of() statuses
    return filters;
  }

}
