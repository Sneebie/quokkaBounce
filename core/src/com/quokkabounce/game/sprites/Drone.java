package com.quokkabounce.game.sprites;

/**
 * Created by Eric on 3/14/2018.
 */

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;


/**
 * Created by Eric on 9/6/2017.
 */

public class Drone {

    private Rectangle droneBounds;
    private Texture droneTexture;
    private TextureRegion droneRegion;
    private Vector2 posDrone, velDrone;
    private Polygon polygon;
    private boolean startMove;
    private static final float DRONESPEED = 200f;
    private float droneAngle;

    public Drone(float x, float y){
        posDrone = new Vector2(x, y);
        velDrone = new Vector2();
        droneTexture = new Texture("drone.png");
        droneRegion = new TextureRegion(droneTexture);
        droneBounds = new Rectangle(posDrone.x, posDrone.y, droneTexture.getWidth(), droneTexture.getHeight());
        startMove = false;
        polygon = new Polygon(new float[]{0,0,droneBounds.width,0,droneBounds.width,droneBounds.height,0,droneBounds.height});
        polygon.setOrigin(droneBounds.width/2, droneBounds.height/2);
        if(50 < posDrone.x + droneBounds.getWidth() / 2) {
            droneAngle = (float) ((Math.atan((650- posDrone.y - droneBounds.getWidth() / 2) / (50 - posDrone.x - droneBounds.getWidth() / 2)) * 180 / Math.PI) + 90);
        }
        else{
            droneAngle = (float) ((Math.atan((650 - posDrone.y - droneBounds.getWidth() / 2) / (50 - posDrone.x - droneBounds.getWidth() / 2)) * 180 / Math.PI) - 90);
        }
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

    public TextureRegion getDroneRegion() {
        return droneRegion;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public float getDroneAngle() {
        return droneAngle;
    }

    public boolean collides(Rectangle player) {
        return player.overlaps(droneBounds);
    }

    public void dispose() {
        droneTexture.dispose();
    }

    public void move(float dt, Vector3 quokkaPos){
        boolean angleFlipped;
        if(quokkaPos.x < posDrone.x + droneBounds.getWidth() / 2) {
            droneAngle = (float) ((Math.atan((quokkaPos.y - posDrone.y - droneBounds.getHeight() / 2) / (quokkaPos.x - posDrone.x - droneBounds.getWidth() / 2)) * 180 / Math.PI));
            angleFlipped = false;
        }
        else{
            droneAngle = (float) ((Math.atan((quokkaPos.y - posDrone.y - droneBounds.getHeight() / 2) / (quokkaPos.x - posDrone.x - droneBounds.getWidth() / 2)) * 180 / Math.PI) + 180);
            angleFlipped = true;
        }
        if(!angleFlipped) {
            velDrone.y = (float) Math.sin((droneAngle + 180) * Math.PI / 180);
            velDrone.x = (float) Math.cos((droneAngle + 180) * Math.PI / 180);
        }
        else{
            velDrone.y = (float) Math.sin((droneAngle - 180) * Math.PI / 180);
            velDrone.x = (float) Math.cos((droneAngle - 180) * Math.PI / 180);
        }
        velDrone.scl(DRONESPEED * dt);
        posDrone.add(velDrone);
        droneBounds.setPosition(posDrone);
        polygon.setPosition(posDrone.x, posDrone.y);
        polygon.setRotation(droneAngle);
    }

}