package dev.deploy4j.scripts;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.io.InputStream;

public class Deploy4j {

  public static void main(String[] args) throws JSchException, IOException {

    System.out.println("Hello, Deploy4j!");

    // what's the image
    String image = "teggr/deploy4j-demo:latest";

    // ssh onto the the box
    String root = "root";
    String host = "localhost";
    int port = 2222;

    // identity
    String privateKey = System.getenv("PRIVATE_KEY");
    String privateKeyPassphrase = System.getenv("PRIVATE_KEY_PASSPHRASE");

    // setup ssh environment
    JSch jsch = new JSch();
    jsch.addIdentity(privateKey, privateKeyPassphrase);

    // create a session
    Session session = jsch.getSession(root, host, port);
    session.setConfig("StrictHostKeyChecking", "no");
    session.connect();

    ChannelExec channel = (ChannelExec) session.openChannel("exec");
    channel.setCommand("pwd");
    channel.setInputStream(null);
    channel.setErrStream(System.err);

    InputStream in = channel.getInputStream();
    channel.connect();

    byte[] tmp = new byte[1024];
    while (true) {
        int i = in.read(tmp, 0, 1024);
        if (i <= 0) break;
        System.out.print(new String(tmp, 0, i));
    }

    channel.disconnect();

    

    session.disconnect();
    
    // install docker

    // add supporting containers

    // run the container

    // update the proxy

  }

}
