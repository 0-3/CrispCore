package network.reborn.core.Util;

import network.reborn.core.RebornCore;
import network.reborn.proxy.RebornProxy;

import java.io.*;

/**
 * Dump an error to a logged file.
 *
 * @author ElectronicWizard
 */
public class ErrorDump {
    private static String classOfOrigin;
    private static Exception e;

    /**
     * Create an ErrorDump that can be stored to file.
     *
     * @param classOfOrigin Name of class of origin of exception.
     * @param e             The exception that occurred.
     */
    public ErrorDump(String classOfOrigin, Exception e) {
        ErrorDump.classOfOrigin = classOfOrigin;
        ErrorDump.e = e;
    }

    /**
     * Create an empty ErrorDump that can be modified, then stored to file.
     */
    public ErrorDump() {
        super();
    }

    /**
     * Save the ErrorDump using Spigot protocols.
     */
    public void createSpigot() {
        String path = "plugins/" + RebornCore.getRebornCore().getDescription().getName()
                + "/errors/" + classOfOrigin + "/error_"
                + System.currentTimeMillis() + ".txt";
        File f = new File(path);
        f.getParentFile().mkdirs();
        try {
            f.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        PrintWriter writer;
        try {
            writer = new PrintWriter(path, "UTF-8");
            writer.println("MESSAGE:");
            writer.println(e.getMessage());
            writer.println("STACKTRACE:");
            e.printStackTrace(writer);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        System.out.println("An error has occured in class [" + classOfOrigin
                + "] and has been logged to [" + path + "].");
        ErrorDump.classOfOrigin = null;
        ErrorDump.e = null;

    }

    /**
     * Change the [stored] Exception.
     *
     * @param e The exception that occurred.
     */
    public void setException(Exception e) {
        ErrorDump.e = e;
    }

    /**
     * Change the [stored] Class of Origin.
     *
     * @param classOfOrigin Name of class of origin of exception.
     */
    public void setClassName(String classOfOrigin) {
        ErrorDump.classOfOrigin = classOfOrigin;
    }

    /**
     * Save the ErrorDump using Bungee protocols.
     */
    public void createBungee() {
        String path = "plugins/" + RebornProxy.getProxyInstance().getDescription().getName()
                + "/errors/" + classOfOrigin + "/error_"
                + System.currentTimeMillis() + ".txt";
        File f = new File(path);
        f.getParentFile().mkdirs();
        try {
            f.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        PrintWriter writer;
        try {
            writer = new PrintWriter(path, "UTF-8");
            writer.println("MESSAGE:");
            writer.println(e.getMessage());
            writer.println("STACKTRACE:");
            e.printStackTrace(writer);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        System.out.println("An error has occured in class [" + classOfOrigin
                + "] and has been logged to [" + path + "].");
        ErrorDump.classOfOrigin = null;
        ErrorDump.e = null;

    }

}
