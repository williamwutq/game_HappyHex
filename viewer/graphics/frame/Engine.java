package viewer.graphics.frame;

import hex.HexEngine;

import java.awt.*;

public class Engine extends Panel {
    HexEngine engine;
    public Engine(HexEngine engine){
        this.engine = engine;
    }
    public static void main(String[] args){
        viewer.Viewer.test(new Engine(new HexEngine(6)));
    }
}
