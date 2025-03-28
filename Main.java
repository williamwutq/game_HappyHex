import Launcher.IO.GameMode;
import Launcher.LaunchEssentials;

/**
 * Main file of the game
 * Run main
 *
 */

public class Main{
    public static void main(String[] args){
        int size = 2;
        int queueSize = 1;
        int delay = 100;
        int preset = -1;
        boolean easy = false;
        String player = "Guest";
        int i = 0;
        while (i < args.length) {
            if (args[i].charAt(0) != '-') {
                System.out.print("Invalid arguments, please try again");
                return;
            } else if (args[i].equals("-size") || args[i].equals("-radius") || args[i].equals("-s") || args[i].equals("-r")) {
                int input;
                try {
                    input = Integer.parseInt(args[i + 1]);
                } catch (NumberFormatException e) {
                    System.out.print("Argument for radius of hex grid is expected to be an integer");
                    return;
                }
                if (input < 2) {
                    System.out.print("Grid radius must be greater than 2");
                    return;
                } else size = input;
                i += 2;
            } else if (args[i].equals("-queue") || args[i].equals("-q")) {
                int input;
                try {
                    input = Integer.parseInt(args[i + 1]);
                } catch (NumberFormatException e) {
                    System.out.print("Argument for queue size is expected to be an integer");
                    return;
                }
                if (input < 1) {
                    System.out.print("Queue size must be greater than 0");
                    return;
                } else queueSize = input;
                i += 2;
            } else if (args[i].equals("-delay") || args[i].equals("-time") || args[i].equals("-interval") || args[i].equals("-ms") || args[i].equals("-d") || args[i].equals("-t")) {
                int input;
                try {
                    input = Integer.parseInt(args[i + 1]);
                } catch (NumberFormatException e) {
                    System.out.print("Argument for delay time is expected to be an integer between 10 and 100000");
                    return;
                }
                if (input < 10 || input > 100000) {
                    System.out.print("Delay must be between 10 and 100000");
                    return;
                } else delay = input;
                i += 2;
            } else if (args[i].equals("-easy") || args[i].equals("-e")) {
                easy = true;
                i++;
            } else if (args[i].equals("-small")) {
                preset = 1;
                i++;
            } else if (args[i].equals("-medium")) {
                preset = 2;
                i++;
            } else if (args[i].equals("-large")) {
                preset = 3;
                i++;
            } else if (args[i].equals("-preset") || args[i].equals("-p")) {
                String inputString = args[i + 1];
                if (inputString.equals("S") || inputString.equals("s") || inputString.equals("1")){
                    preset = 1;
                } else if (inputString.equals("M") || inputString.equals("m") || inputString.equals("2")){
                    preset = 2;
                } else if (inputString.equals("L") || inputString.equals("l") || inputString.equals("3")){
                    preset = 3;
                }
                i+=2;
            } else if (args[i].equals("-player") || args[i].equals("-user") || args[i].equals("-u")) {
                player = args[i + 1];
                i+=2;
            }
            else i++;
        }
        // Preset is not enabled
        if(preset == -1) {
            LaunchEssentials.initializeCurrentGame(GameMode.Medium); // Placeholder
            GUI.HappyHexGUI.play(size, queueSize, delay, easy);
            LaunchEssentials.setCurrentPlayer(player, player.hashCode());
        } else {
            if(easy) {
                if (preset == 1) {
                    LaunchEssentials.initializeCurrentGame(GameMode.SmallEasy);
                } else if (preset == 3) {
                    LaunchEssentials.initializeCurrentGame(GameMode.LargeEasy);
                } else {
                    LaunchEssentials.initializeCurrentGame(GameMode.MediumEasy);
                }
            } else {
                if (preset == 1) {
                    LaunchEssentials.initializeCurrentGame(GameMode.Small);
                } else if (preset == 3) {
                    LaunchEssentials.initializeCurrentGame(GameMode.Large);
                } else {
                    LaunchEssentials.initializeCurrentGame(GameMode.Medium);
                }
            }
            LaunchEssentials.setCurrentPlayer(player, player.hashCode());
            GUI.HappyHexGUI.play(3*preset + 2, 2*preset + 1, delay, easy);
        }
    }
}
