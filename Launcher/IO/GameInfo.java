package Launcher.IO;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public final class GameInfo implements JsonConvertible{
    private String player;
    private int turn;
    private int score;
    private long playerID;
    private final long gameID;
    private final GameMode gameMode;

    public GameInfo(GameMode mode) {
        this.turn = 0;
        this.score = 0;
        this.playerID = -1;
        this.player = "Guest";
        this.gameID = LaunchLogger.generateHash(0);
        this.gameMode = mode;
    }
    public GameInfo(int turn, int score, GameMode mode){
        this.turn = turn;
        this.score = score;
        this.playerID = -1;
        this.player = "Guest";
        this.gameID = LaunchLogger.generateHash(~(turn * score));
        this.gameMode = mode;
    }
    public GameInfo(int turn, int score, int playerID, String player, GameMode mode){
        this.setPlayer(player, playerID);
        this.turn = turn;
        this.score = score;
        this.gameID = LaunchLogger.generateHash((turn * score) ^ playerID);
        this.gameMode = mode;
    }

    public String getPlayer() {return player;}
    public long getPlayerID(){return playerID;}
    public long getGameID(){return gameID;}
    public GameMode getGameMode(){return gameMode;}

    public void setPlayer(String player, long ID) {
        if(player == null || player.equals("") || player.equals("Guest") || ID == 0 || ID == -1){
            this.player = "Guest";
            this.playerID = -1;
        } else {
            this.player = player;
            this.playerID = ID;
        }
    }

    public int getTurn() {return turn;}
    public int getScore() {return score;}

    public void setTurn(int turns) {this.turn = turns;}
    public void setScore(int score) {this.score = score;}

    @Override
    public JsonObjectBuilder toJsonObjectBuilder() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("Player", player);
        builder.add("PlayerID", playerID);
        builder.add("GameId", gameID);
        // Game mods
        if(gameMode == GameMode.Small){
            builder.add("EasyMode", false);
            builder.add("Preset", "S");
        } else if (gameMode == GameMode.Medium){
            builder.add("EasyMode", false);
            builder.add("Preset", "M");
        } else if (gameMode == GameMode.Large){
            builder.add("EasyMode", false);
            builder.add("Preset", "L");
        } else if (gameMode == GameMode.SmallEasy){
            builder.add("EasyMode", true);
            builder.add("Preset", "S");
        } else if (gameMode == GameMode.MediumEasy){
            builder.add("EasyMode", true);
            builder.add("Preset", "M");
        } else if (gameMode == GameMode.LargeEasy){
            builder.add("EasyMode", true);
            builder.add("Preset", "L");
        }
        //builder.add(version.toJsonObject()); version to be implemented
        builder.add("Time", LaunchLogger.fetchCurrentTimeJson());
        JsonObject scoreElement = Json.createObjectBuilder().add("Score", score).add("Turn", turn).build();
        builder.add("Result", scoreElement);
        return builder;
    }
}
