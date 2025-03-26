package Launcher.IO;

import java.io.*;
import java.nio.file.*;
import java.time.Instant;

public final class LaunchLogger {
    private static final int[] SHIFTS = {31, 37, 41, 27, 23, 29, 33, 43};
    private static final long PRIME = 0x9E4739E97F4A7C15L;
    private static final int ID = (int)obfuscate(PRIME);

    // Hashing
    public static long generateHash(int value) {
        long time = Instant.now().toEpochMilli(); // Get current time
        time = obfuscate(time);
        long root = ((long) ID << 32) | (value & 0xFFFFFFFFL);
        System.out.println(time ^ root);
        return obfuscate(time ^ root);
    }
    public static long obfuscate(long input) {
        input ^= (input << SHIFTS[0]) | (input >>> SHIFTS[1]);
        input *= PRIME;
        input ^= (input << SHIFTS[2]) | (input >>> SHIFTS[3]);
        input *= PRIME;
        input ^= (input << SHIFTS[4]) | (input >>> SHIFTS[5]);
        input *= PRIME;
        input ^= (input << SHIFTS[6]) | (input >>> SHIFTS[7]);
        return input;
    }
    public static int getID(){
        return ID;
    }

    // Try to read json log
    private static String readJsonFile() {
        String[] possiblePaths = {
                "logs.json",
                "log/logs.json",
                "Game/logs.json",
                "Launcher/logs.json"
        };

        for (String path : possiblePaths) {
            File file = new File(path);
            if (file.exists()) {
                try {
                    return new String(Files.readAllBytes(file.toPath()));
                } catch (IOException e) {
                    System.err.println("Error reading file: " + path);
                    e.printStackTrace();
                }
            }
        }
        return null;  // No valid file is found
    }

    public static void main(String[] args){
        System.out.print(readJsonFile());
    }
}
