package dev.deploy4j.scripts;

import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Commands {

  public record cmd(String...cmds) {}

  public static void main(String[] args) {
    combine( new cmd[]{ new cmd( "ls" ), new cmd( "echo", "hi" ), new cmd( "pwd") }, "&&" );
  }

  public static cmd combine(cmd[] commands, String by) {

    List<String> list = Stream.of(commands).filter(Objects::nonNull)
      .map(cmd -> new cmd((String[]) ArrayUtils.add(cmd.cmds(), by)))
      .flatMap(cmd -> Stream.of(cmd.cmds()))
      .toList();

    if(!list.isEmpty()) {
      list = new ArrayList<>(list);
      list.removeLast();
    }

    return new cmd( list.toArray( new String[0] ) );

  }

  // Overload with default separator
  public static cmd combine(cmd... commands) {
    return combine(commands, "&&");
  }

  public static cmd chain(cmd... commands) {
    return combine(commands, ";");
  }

  public static cmd pipe(cmd... commands) {
    return combine(commands, "|");
  }

  public static cmd append(cmd... commands) {
    return combine(commands, ">>");
  }

  public static cmd write(cmd... commands) {
    return combine(commands, ">");
  }

  public static cmd any(cmd... commands) {
    return combine(commands, "||");
  }

  public static cmd substitute(cmd... commands) {
    return new cmd( "\\$\\(" + Stream.of( commands ).map( cmd -> cmd.cmds()[0] ).collect(Collectors.joining(" ")) + "\\)" );
  }

  public static cmd shell(cmd command) {
    return new cmd(
      "sh", "-c",
      "'" + Stream.of(command).flatMap( cmd -> Stream.of(cmd.cmds) ).collect(Collectors.joining(" ")).replace("'", "'\\\\''") + "'"
    );
  }

}
