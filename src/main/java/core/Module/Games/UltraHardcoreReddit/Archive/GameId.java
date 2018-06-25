package network.reborn.core.Module.Games.UltraHardcoreReddit.Archive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by ethan on 5/23/2017.
 */
public class GameId {

    public static Integer getLastUsedID() {
        Integer i = -1;
        try {
            File f = new File("/home/minecraft/uhc.txt");
            Scanner scanner = new Scanner(f);
            while (scanner.hasNextLine()) {
                i = Integer.parseInt(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return i;
    }

    public static void wipeFile() {
        try {
            FileWriter f = new FileWriter("/home/minecraft/uhc.txt");
            f.write("");
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void incrementValue() {
        Integer i = getLastUsedID();
        wipeFile();
        try {
            FileWriter f = new FileWriter("/home/minecraft/uhc.txt");
            i++;
            f.write(String.valueOf(i));
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setValue(Integer i) {
        wipeFile();
        try {
            FileWriter f = new FileWriter("/home/minecraft/uhc.txt");
            f.write(String.valueOf(i));
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
