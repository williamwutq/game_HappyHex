package GUI.animation;

import java.awt.*;

public class NullEffect extends Animation {
    public NullEffect() {
        super(0, 1);
    }
    protected void paintFrame(Graphics graphics, double progress) {
    }
}
