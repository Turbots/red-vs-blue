package io.pivotal.workshop.redvsblue.game;

public class Ball {

    private Player thrower;
    private Player target;

    public Ball() {
    }

    public Ball(Player thrower, Player target) {
        this.thrower = thrower;
        this.target = target;
    }

    public Player getThrower() {
        return thrower;
    }

    public void setThrower(Player thrower) {
        this.thrower = thrower;
    }

    public Player getTarget() {
        return target;
    }

    public void setTarget(Player target) {
        this.target = target;
    }
}
