/*
  MIT License

  Copyright (c) 2025 William Wu

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.
 */

package achievements.impl;

import achievements.AchievementJsonSerializer;
import achievements.DataSerializationException;
import achievements.GameAchievementTemplate;
import achievements.icon.AchievementIcon;
import achievements.icon.AchievementIconSerialHelper;
import hex.GameState;
import hex.Piece;
import io.JsonConvertible;

import javax.json.JsonObjectBuilder;
import java.util.List;

/**
 * The {@code QueueBasedAchievement} class represents a game achievement that is based on the pieces
 * present in the current piece queue and the length of the queue. It implements the {@link GameAchievementTemplate}
 * interface and provides functionality to check if the achievement has been achieved based on the current
 * {@link GameState}.
 * <p>
 * This class allows for specifying a list of required pieces that must be present in the queue, as well as
 * minimum and maximum length requirements for the queue. The achievement is considered achieved if all
 * required pieces are found in the queue and its length falls within the specified bounds.
 * <p>
 * Instances of this class are immutable, meaning that once created, their state cannot be changed.
 * This ensures that the achievement criteria remain consistent throughout its lifecycle.
 * <p>
 * The class also provides methods for serialization and deserialization to and from JSON format,
 * allowing for easy storage and retrieval of achievement data.
 *
 * @see GameAchievementTemplate
 * @see GameState
 * @see JsonConvertible
 * @author William Wu
 * @version 2.0
 * @since 2.0
 */
public class QueueBasedAchievement implements GameAchievementTemplate, JsonConvertible {
    private final List<Piece> requiredPieces;
    private final int requiredLeastLength;
    private final int requiredMostLength;
    private final String name;
    private final String description;
    private final AchievementIcon icon;
    public static void load() {
        AchievementJsonSerializer.registerAchievementClass("QueueBasedAchievement", json -> {
            try {
                return fromJsonObject(json);
            } catch (DataSerializationException e) {
                throw new RuntimeException(e.getMessage(), e.getCause());
            }
        });
    }
    /**
     * Creates a new QueueBasedAchievement with the specified requirements, name, and description.
     * The achievement is achieved if the current piece queue contains at least the required pieces
     * and its length is within the specified bounds.
     * @param requiredPieces the pieces required to be in the queue
     * @param requiredLeastLength the minimum length of the queue
     * @param requiredMostLength the maximum length of the queue
     * @param name the name of the achievement
     * @param description the description of the achievement
     */
    public QueueBasedAchievement(List<Piece> requiredPieces, int requiredLeastLength, int requiredMostLength, String name, String description, AchievementIcon icon) {
        this.requiredPieces = List.copyOf(requiredPieces);
        this.requiredLeastLength = requiredLeastLength;
        this.requiredMostLength = requiredMostLength;
        this.name = name;
        this.description = description;
        this.icon = icon;
    }
    /**
     * Creates a new QueueBasedAchievement with the specified requirements, name, and description.
     * The achievement is achieved if the current piece queue contains at least the required pieces
     * and its length is exactly the specified length.
     * @param requiredPieces the pieces required to be in the queue
     * @param requiredLength the exact length of the queue
     * @param name the name of the achievement
     * @param description the description of the achievement
     */
    public QueueBasedAchievement(List<Piece> requiredPieces, int requiredLength, String name, String description, AchievementIcon icon) {
        this(requiredPieces, requiredLength, requiredLength, name, description, icon);
    }
    /**
     * {@inheritDoc}
     * @return the name of the achievement
     */
    @Override
    public String name() {
        return name;
    }
    /**
     * {@inheritDoc}
     * @return the description of the achievement
     */
    @Override
    public String description() {
        return description;
    }
    /**
     * {@inheritDoc}
     * @return the icon of the achievement
     */
    @Override
    public AchievementIcon icon() {
        return icon;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Test whether the achievement has been achieved based on the provided game state.
     * The achievement is considered achieved if the current piece queue contains at least the required pieces
     * and its length is within the specified bounds.
     * @param state the current game state
     * @return {@code true} if the achievement has been achieved, {@code false} otherwise
     */
    @Override
    public boolean test(GameState state) {
        Piece[] queue = state.getQueue();
        if (queue == null || queue.length < requiredLeastLength || queue.length > requiredMostLength) {
            return false;
        }
        // Make a copy of the required pieces to track which have been found. This allows for duplicates.
        List<Piece> toFind = new java.util.ArrayList<>(requiredPieces);
        for (Piece p : queue) {
            for (int i = 0; i < toFind.size(); i++) {
                if (p.equals(toFind.get(i))) {
                    toFind.remove(i);
                    break;
                }
            }
            if (toFind.isEmpty()) {
                break; // All required pieces found
            }
        }
        return toFind.isEmpty(); // Not all required pieces were found
    }
    /**
     * Compares this achievement to the specified object. The result is {@code true} if and only if
     * the argument is not {@code null} and is a {@code QueueBasedAchievement} object that has the same
     * required pieces, length requirements, name, and description as this object.
     * @param o the object to compare this {@code QueueBasedAchievement} against
     * @return {@code true} if the given object represents a {@code QueueBasedAchievement}
     * equivalent to this achievement, {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QueueBasedAchievement)) return false;
        QueueBasedAchievement that = (QueueBasedAchievement) o;
        return requiredLeastLength == that.requiredLeastLength &&
                requiredMostLength == that.requiredMostLength &&
                requiredPieces.equals(that.requiredPieces) &&
                name.equals(that.name) &&
                description.equals(that.description);
    }
    /**
     * Returns a hash code value for the object.
     * This method is supported for the benefit of hash tables such as those provided by {@link java.util.HashMap}.
     * The hash code is computed based on the required pieces, length requirements, name, and description.
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return java.util.Objects.hash(requiredPieces, requiredLeastLength, requiredMostLength, name, description);
    }
    /**
     * {@inheritDoc}
     * Converts this achievement to a JSON object builder.
     * @return a {@link JsonObjectBuilder} representing the achievement
     */
    @Override
    public JsonObjectBuilder toJsonObjectBuilder() {
        JsonObjectBuilder job = javax.json.Json.createObjectBuilder();
        job.add("type", "QueueBasedAchievement");
        job.add("name", name);
        job.add("description", description);
        job.add("icon", AchievementIconSerialHelper.serialize(icon));
        job.add("requiredLeastLength", requiredLeastLength);
        job.add("requiredMostLength", requiredMostLength);
        javax.json.JsonArrayBuilder jab = javax.json.Json.createArrayBuilder();
        for (Piece p : requiredPieces) {
            jab.add(p.toByte());
        }
        job.add("requiredPieces", jab);
        return job;
    }
    /**
     * Creates a QueueBasedAchievement from a JSON object.
     * The JSON object must contain the fields "name", "description", "requiredLeastLength",
     * "requiredMostLength", and "requiredPieces".
     * @param obj the JSON object representing the achievement
     * @return a QueueBasedAchievement instance
     * @throws DataSerializationException if the JSON object is invalid or missing required fields
     */
    public static QueueBasedAchievement fromJsonObject(javax.json.JsonObject obj) throws DataSerializationException {
        try {
            String name = obj.getString("name");
            String description = obj.getString("description");
            int requiredLeastLength = obj.getInt("requiredLeastLength");
            int requiredMostLength = obj.getInt("requiredMostLength");
            javax.json.JsonArray pieceArray = obj.getJsonArray("requiredPieces");
            List<Piece> requiredPieces = new java.util.ArrayList<>();
            AchievementIcon icon = AchievementIconSerialHelper.deserialize(obj);
            for (javax.json.JsonValue v : pieceArray) {
                requiredPieces.add(Piece.pieceFromByte((byte) ((javax.json.JsonNumber) v).intValue(), -2));
            }
            return new QueueBasedAchievement(requiredPieces, requiredLeastLength, requiredMostLength, name, description, icon);
        } catch (Exception e) {
            throw new DataSerializationException("Invalid JSON object for QueueBasedAchievement", e);
        }
    }
}
