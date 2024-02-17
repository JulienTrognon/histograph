package up.visulog.util;

public class Logger {

    private final String prefix = "[histoGRAPH] ";

    public void info(String text){
        System.out.println(prefix + "INFO: " + text);
    }

    public void warn(String text){
        System.out.println(prefix + "WARN: " + text);
    }

    public void warnExit(String text){
        System.out.println(prefix + "WARN: " + text);
        System.out.println(prefix + "WARN: Please try again.");
        System.exit(0);
    }

    public void error(String text){
        System.err.println(prefix + "ERROR: " + text);
    }

    public void errorFatal(String text){
        System.err.println(prefix + "FATAL ERROR: " + text);
        System.err.println(prefix + "FATAL ERROR: Please try again.");
        System.exit(-1);
    }




}
