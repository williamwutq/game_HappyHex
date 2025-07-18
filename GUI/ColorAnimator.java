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

/* Authors of mods should put their license and below. */

/* Authors of mods should put their license and above. */

package GUI;

import util.dynamic.CircularProperty;

import java.awt.Color;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ColorAnimator implements CircularProperty<Color>{
    private final AtomicBoolean isRunning;
    private final AtomicBoolean isForward;
    private final Object phaseLock;
    private final Object colorLock;
    private Color[] colors;
    private final AtomicInteger period;
    private double phase;
    private double defaultPhase;
    private final Runnable guiUpdater;
    private Thread animationThread;

    public ColorAnimator(Color[] colors, int period, Runnable guiUpdater) {
        if (colors == null || colors.length < 2) {
            throw new IllegalArgumentException("At least two colors are required.");
        }
        if (period <= 0) {
            throw new IllegalArgumentException("Period must be positive.");
        }
        this.isRunning = new AtomicBoolean(false);
        this.isForward = new AtomicBoolean(true);
        this.phaseLock = new Object();
        this.colorLock = new Object();
        this.colors = Arrays.copyOf(colors, colors.length);
        this.period = new AtomicInteger(period);
        this.phase = 0.0;
        this.defaultPhase = 0.0;
        this.guiUpdater = guiUpdater;
    }

    @Override
    public boolean direction() {
        return isForward.get();
    }
    @Override
    public void setDirection(boolean forward) {
        isForward.set(forward);
    }
    @Override
    public double phase() {
        synchronized (phaseLock) {
            return phase;
        }
    }
    @Override
    public void applyDefaultPhase(double phase) {
        if (phase < 0 || phase >= 1) throw new IllegalArgumentException("Phase must be between 0 and 1");
        synchronized (phaseLock) {
            this.defaultPhase = phase;
        }
    }
    @Override
    public double getDefaultPhase() {
        synchronized (phaseLock){
            return defaultPhase;
        }
    }
    @Override
    public int getPeriod() {
        return period.get();
    }
    @Override
    public void applyPeriod(int period) {
        this.period.set(period);
    }
    @Override
    public void applyPhase(double phase) throws IllegalArgumentException {
        if (phase < 0 || phase >= 1) throw new IllegalArgumentException("Phase must be between 0 and 1");
        synchronized (phaseLock) {
            this.phase = phase;
        }
    }
    @Override
    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            animationThread = new Thread(this::animate);
            animationThread.setDaemon(true);
            animationThread.start();
        }
    }
    @Override
    public void stop() {
        isRunning.set(false);
        if (animationThread != null) {
            animationThread.interrupt();
            try {
                animationThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            animationThread = null;
        }
    }
    @Override
    public void reset() {
        synchronized (phaseLock){
            phase = defaultPhase;
        }
        if (guiUpdater != null) {
            guiUpdater.run();
        }
    }
    @Override
    public Color get() {
        return get(0.0);
    }
    @Override
    public boolean isRunning() {
        return isRunning.get();
    }
    public Color get(double phaseShift) {
        double f;
        synchronized (phaseLock){
            f = (phase + phaseShift + 1.0) % 1.0;
        }
        return interpolateColor(f);
    }
    public void setColors(Color[] colors) {
        if (colors == null || colors.length < 2) {
            throw new IllegalArgumentException("At least two colors are required.");
        }
        synchronized (colorLock){
            this.colors = Arrays.copyOf(colors, colors.length);
        }
        if (guiUpdater != null) {
            guiUpdater.run();
        }
    }
    private void animate() {
        long lastUpdate = System.currentTimeMillis();
        while (isRunning.get()) {
            long currentTime = System.currentTimeMillis();
            long elapsed = currentTime - lastUpdate;
            lastUpdate = currentTime;
            double delta = (double) elapsed / period.get();
            if (!isForward.get()){
                delta = -delta;
            }
            synchronized (phaseLock){
                phase = (phase + delta + 1.0) % 1.0;
            }
            if (guiUpdater != null) {
                guiUpdater.run();
            }
            try {
                Thread.sleep(6);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    private Color interpolateColor(double phase) {
        final Color[] localColors;
        synchronized (colorLock) {
            localColors = Arrays.copyOf(colors, colors.length);
        }
        int numColors = localColors.length;
        double scaledPhase = phase * numColors;
        int index1 = (int) scaledPhase;
        int index2 = (index1 + 1) % numColors;
        double fraction = scaledPhase - index1;
        Color c1 = localColors[index1];
        Color c2 = localColors[index2];
        int r = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * fraction);
        int g = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * fraction);
        int b = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * fraction);
        int a = (int) (c1.getAlpha() + (c2.getAlpha() - c1.getAlpha()) * fraction);
        return new Color(r, g, b, a);
    }
}