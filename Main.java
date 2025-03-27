/**
 * Main file of the game
 * Run main
 *
 */

public class Main{
    public static void main(String[] args){
        // Scores of Dev and his friends
        // Dev -size 8 -queue 5 -> turn 860 point 18019
        // Dev -size 8 -queue 5 -> turn 297 point 5912
        // Dev -size 5 -queue 3 -> turn 220 point 4547
        // Dev -size 5 -queue 3 -> turn 140 point 2911
        // Dev -size 5 -queue 3 -> turn 135 point 2759
        // Dev -size 5 -queue 3 -> turn 99 point 2018
        // Dev -size 5 -queue 3 -> turn 70 point 1429
        // Dev -size 5 -queue 3 -> turn 66 point 1200
        // Yiguo -size 5 -queue 3 -> turn 130 point 2707
        // Mate -size 5 -queue 3 -> turn 37 point 590

        int size = 2;
        int queueSize = 1;
        int delay = 100;
        boolean easy = false;
        int i = 0;
        while (i < args.length - 1) {
            if (args[i].charAt(0) != '-') {
                System.out.print("Invalid arguments, please try again");
                return;
            } else switch (args[i]) {
                case "-size", "-radius", "-s", "-r" -> {
                    int input;
                    try {
                        input = Integer.parseInt(args[i+1]);
                    } catch (NumberFormatException e) {
                        System.out.print("Argument for radius of hex grid is expected to be an integer");
                        return;
                    } if (input < 2){
                        System.out.print("Grid radius must be greater than 2");
                        return;
                    } else size = input;
                    i += 2;
                }
                case "-queue", "-q" -> {
                    int input;
                    try {
                        input = Integer.parseInt(args[i+1]);
                    } catch (NumberFormatException e) {
                        System.out.print("Argument for queue size is expected to be an integer");
                        return;
                    } if (input < 1){
                        System.out.print("Queue size must be greater than 0");
                        return;
                    } else queueSize = input;
                    i += 2;
                }
                case "-delay", "-time", "-interval", "-ms", "-d", "-t" -> {
                    int input;
                    try {
                        input = Integer.parseInt(args[i+1]);
                    } catch (NumberFormatException e) {
                        System.out.print("Argument for delay time is expected to be an integer between 10 and 100000");
                        return;
                    } if (input < 10 || input > 100000){
                        System.out.print("Delay must be between 10 and 100000");
                        return;
                    } else delay = input;
                    i += 2;
                }
                case "-easy", "-e" -> {
                    easy = true;
                    i += 2;
                }
                default -> i++;
            }
        }
        GUI.HappyHexGUI.play(size, queueSize, delay, easy);
    }
}
