package Launcher.IO;

import Launcher.LaunchEssentials;

import javax.json.*;

public final class GameInfo implements JsonConvertible{
    private Username player;
    private int turn;
    private int score;
    private long playerID;
    private final GameTime time;
    private final long gameID;
    private GameMode gameMode;
    private final GameVersion gameVersion;

    public GameInfo(GameMode mode) {
        this.turn = 0;
        this.score = 0;
        this.playerID = -1;
        this.player = Username.getUsername("Guest");
        this.time = new GameTime();
        this.gameID = LaunchLogger.generateHash(0);
        this.gameMode = mode;
        this.gameVersion = LaunchEssentials.currentGameVersion;
    }
    public GameInfo(int turn, int score, GameMode mode, GameVersion version){
        this.turn = turn;
        this.score = score;
        this.playerID = -1;
        this.player = Username.getUsername("Guest");
        this.time = new GameTime();
        this.gameID = LaunchLogger.generateHash(~(turn * score));
        this.gameMode = mode;
        this.gameVersion = version;
    }
    public GameInfo(int turn, int score, String playerID, Username player, GameTime time, String gameID, GameMode mode, GameVersion version){
        long ID = Long.parseUnsignedLong(playerID, 16);
        this.setPlayer(player, ID);
        this.turn = turn;
        this.score = score;
        this.time = time;
        this.gameID = Long.parseUnsignedLong(gameID, 16);
        this.gameMode = mode;
        this.gameVersion = version;
    }

    public Username getPlayer() {return player;}
    public long getPlayerID(){return playerID;}
    public long getGameID(){return gameID;}
    public GameMode getGameMode(){return gameMode;}

    public void setPlayer(Username player, long ID) {
        if(player == null || player.equals("") || player.equals("Guest") || ID == 0 || ID == -1){
            this.player = Username.getUsername("Guest");
            this.playerID = -1;
        } else {
            this.player = player;
            this.playerID = ID;
        }
    }
    public void setGameMode(GameMode gameMode){
        this.gameMode = gameMode;
    }

    public int getTurn() {return turn;}
    public int getScore() {return score;}

    public void setTurn(int turns) {this.turn = turns;}
    public void setScore(int score) {this.score = score;}

    @Override
    public JsonObjectBuilder toJsonObjectBuilder() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("Player", player.toString());
        builder.add("PlayerID", Long.toHexString(playerID));
        builder.add("GameID", Long.toHexString(gameID));
        builder.add("EasyMode", GameMode.isEasy(gameMode));
        builder.add("Preset", GameMode.getChar(gameMode) + "");
        builder.add("Version", gameVersion.toJsonObject());
        builder.add("Time", time.toJsonObject());
        JsonObject scoreElement = Json.createObjectBuilder().add("Score", score).add("Turn", turn).build();
        builder.add("Result", scoreElement);
        return builder;
    }
}
