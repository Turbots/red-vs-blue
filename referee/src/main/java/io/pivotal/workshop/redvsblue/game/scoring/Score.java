package io.pivotal.workshop.redvsblue.game.scoring;

public class Score {

    private long red;
    private long blue;

    public Score() {
    }

    public Score(long red, long blue) {
        this.red = red;
        this.blue = blue;
    }

    public long getRed() {
        return red;
    }

    public void setRed(long red) {
        this.red = red;
    }

    public long getBlue() {
        return blue;
    }

    public void setBlue(long blue) {
        this.blue = blue;
    }
}
