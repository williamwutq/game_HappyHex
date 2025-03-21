package Hex;

import java.awt.*;

public interface HexGrid {
    abstract int length();
    abstract Block[] blocks();
    abstract boolean inRange(int i, int k);
    abstract Block getBlock(int i, int k);
    abstract Block getBlock(int index); // Use this with length(); in a for loop for any grid
    default boolean getState(int i, int k){
        return getBlock(i, k).getState();
    }
    default Color getColor(int i, int k){
        return getBlock(i, k).color();
    }
    abstract void add(Hex origin, HexGrid other) throws IllegalArgumentException;
    default void add (HexGrid other) throws IllegalArgumentException{
        add(new Block(), other);
    }
}
