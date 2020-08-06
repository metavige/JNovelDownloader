package jNovel.kernel.utils;

/**
 * Console logger
 *
 * @author rickychiang
 */
public class ConsoleLogger implements IMessageLogger {

  @Override
  public void printf(String format, Object... params) {

    System.out.printf(format, params);
    System.out.println();
  }

  @Override
  public void print(String message) {

    System.out.println(message);
  }

}
