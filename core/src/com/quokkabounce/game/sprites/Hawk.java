package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Eric on 9/6/2017.
 */

public class Hawk {
    private static final double SPEED = 0.06;
    private static final double RADIUS = 300;
    private static final double ATTACKSPEED = 1.5;
    private static final int ATTACKDELAY = 10;

    private int t, loopTime;
    private boolean alreadySpotted;

    private Rectangle hawkBounds;
    private Animation hawkAnimation;
    private Vector2 posHawk, drawPosHawk, velHawk, previousPos;

    public Hawk(float x, float y){
        loopTime = 0;
        t = 0;
        hawkAnimation = new Animation("hawkIdle", "hawk", 10, 0.5f);
        alreadySpotted = false;
        previousPos = new Vector2(Math.round(RADIUS * Math.cos(SPEED * t)) + x, Math.round(RADIUS * Math.sin(SPEED * t)) + y);
        posHawk = new Vector2(Math.round(RADIUS * Math.cos(SPEED * t)) + x, Math.round(RADIUS * Math.sin(SPEED * t)) + y);
        drawPosHawk =  new Vector2(Math.round(RADIUS * Math.cos(SPEED * t)) + x, Math.round(RADIUS * Math.sin(SPEED * t)) + y);
        velHawk = new Vector2(Math.round(SPEED * RADIUS * -1 * Math.sin(SPEED * t)), Math.round(SPEED * RADIUS * Math.cos(SPEED * t)));
        hawkBounds = new Rectangle(posHawk.x, posHawk.y + hawkAnimation.getFrame().getHeight() / 3, hawkAnimation.getFrame().getWidth(), hawkAnimation.getFrame().getHeight() + hawkAnimation.getFrame().getHeight() / 3);
    }

    public Rectangle getHawkBounds() {
        return hawkBounds;
    }

    public Vector2 getPosHawk() {
        return posHawk;
    }

    public void setDrawPosHawk(Vector2 posHawk) {
        this.drawPosHawk = posHawk;
    }

    public Vector2 getDrawPosHawk() {
        return drawPosHawk;
    }

    public Vector2 getPreviousPos() {
        return previousPos;
    }

    public Texture getTexture() {
        return hawkAnimation.getFrame();
    }

    public boolean collides(Rectangle player) {
        return player.overlaps(hawkBounds);
    }

    public void dispose() {
        for(Texture frame : hawkAnimation.getFrames()) {
            frame.dispose();
        }
    }

    public void move(boolean spotsQuokka, float dt, Vector3 posQuokka){
        previousPos.set(posHawk);
        hawkAnimation.update(dt);
        if(!spotsQuokka){
            alreadySpotted = false;
            t += 1;
            velHawk.set(Math.round(RADIUS * -1 * Math.sin(SPEED * t)), Math.round(RADIUS * Math.cos(SPEED * t)));
            velHawk.scl(dt);
            posHawk.add(velHawk);
        }
        else{
            if(!alreadySpotted) {
                velHawk.set(Math.round((posQuokka.x - posHawk.x) * ATTACKSPEED), Math.round((posQuokka.y - posHawk.y) * ATTACKSPEED));
                alreadySpotted = true;
                loopTime = 0;
            }
            if (loopTime >=ATTACKDELAY) {
                velHawk.scl(dt);
                posHawk.set(posHawk.x + velHawk.x, posHawk.y + velHawk.y);
                velHawk.scl(1 / dt);
            }
            else{
                loopTime++;
            }
        }
        hawkBounds.set(posHawk.x, posHawk.y, hawkAnimation.getFrame().getWidth(), hawkAnimation.getFrame().getHeight());
    }

}
