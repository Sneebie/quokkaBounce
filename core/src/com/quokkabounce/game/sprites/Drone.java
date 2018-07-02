package com.quokkabounce.game.sprites;

/**
 * Created by Eric on 3/14/2018.
 */

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Eric on 9/6/2017.
 */

public class Drone {

    private Rectangle droneBounds;
    private Texture droneTexture;
    private Vector2 posDrone, velDrone;
    private boolean startMove;
    private static final float DRONESPEED = 15f;

    public Drone(float x, float y){
        posDrone = new Vector2(x, y);
        velDrone = new Vector2();
        droneTexture = new Texture("drone.png");
        droneBounds = new Rectangle(posDrone.x, posDrone.y, droneTexture.getWidth(), droneTexture.getHeight());
        startMove = false;
    }

    public Rectangle getDroneBounds() {
        return droneBounds;
    }

    public Vector2 getPosDrone() {
        return posDrone;
    }

    public Texture getTexture() {
        return droneTexture;
    }

    public boolean isStartMove() {
        return startMove;
    }

    public void setStartMove(boolean startMove) {
        this.startMove = startMove;
    }

    public void setPosDrone(Vector2 posDrone) {
        this.posDrone = posDrone;
    }

    public void setVelDrone(Vector2 velDrone) {
        this.velDrone = velDrone;
    }

    public boolean collides(Rectangle player) {
        return player.overlaps(droneBounds);
    }

    public void dispose() {
        droneTexture.dispose();
    }

    public void move(float dt, Vector3 quokkaPos){
        velDrone.y = (float) (DRONESPEED * Math.sin(Math.atan(Math.abs(posDrone.x-quokkaPos.x) / Math.abs(posDrone.y - quokkaPos.y))));
        velDrone.x = (float) (DRONESPEED * Math.cos(Math.atan(Math.abs(posDrone.x-quokkaPos.x) / Math.abs(posDrone.y - quokkaPos.y))));
        velDrone.scl(dt);
        posDrone.add(velDrone);
        droneBounds.setPosition(posDrone);
    }

}