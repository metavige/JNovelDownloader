package jNovel.kernel.utils;

import java.io.Console;

public class Logger {

    private final static Logger Instance = new Logger();
    private static IMessageLogger DefaultLogger = new ConsoleLogger();
    private static boolean IsLogWithConsole = false;

    private IMessageLogger _internalLogger;

    /**
     * 提供改變 logger 的方式，像是要把資料顯示在 JTextArea 上面
     * 
     * @param logger
     */
    public static void setupLogger(IMessageLogger logger) {

        Instance._internalLogger = logger;
        if (!(logger instanceof ConsoleLogger)) {
            IsLogWithConsole = true;
        }
    }

    public static void printf(String format, Object... args) {

        if (IsLogWithConsole) {
            DefaultLogger.printf(format, args);
        }
        GetLogger().printf(format, args);
    }

    public static void print(String message) {

        if (IsLogWithConsole) {
            DefaultLogger.printf(message);
        }
        GetLogger().print(message);
    }

    public static IMessageLogger GetLogger() {

        if (Instance._internalLogger == null) {
            Instance._internalLogger = DefaultLogger;
        }
        return Instance._internalLogger;
    }

    public static void DisableConsoleLog(boolean isDisableConsoleLog) {

        IsLogWithConsole = isDisableConsoleLog;
    }
}
