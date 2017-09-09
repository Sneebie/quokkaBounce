package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Eric on 9/6/2017.
 */

public class Hawk {
    private static final double SPEED = 0.1;
    private static final double RADIUS = 10;
    private static final double ATTACKSPEED = 1.5;
    private static final int ATTACKDELAY = 10;
    private static final int DTSCALE = 62;

    private int t, loopTime;
    private boolean alreadySpotted;

    private Rectangle hawkBounds;
    private Animation hawkAnimation;
    private Vector2 posHawk, velHawk;

    public Hawk(float x, float y){
        loopTime = 0;
        t = 0;
        hawkAnimation = new Animation("hawkIdle", "hawk", 10, 0.5f);
        alreadySpotted = false;
        posHawk = new Vector2(Math.round(RADIUS * Math.cos(SPEED * t)) + x, Math.round(RADIUS * Math.sin(SPEED * t)) + y);
        velHawk = new Vector2(Math.round(SPEED * RADIUS * -1 * Math.sin(SPEED * t)), Math.round(SPEED * RADIUS * Math.cos(SPEED * t)));
        hawkBounds = new Rectangle(posHawk.x, posHawk.y, hawkAnimation.getFrame().getWidth(), hawkAnimation.getFrame().getHeight());
    }

    public Rectangle getHawkBounds() {
        return hawkBounds;
    }

    public Vector2 getPosHawk() {
        return posHawk;
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
        hawkAnimation.update(dt);
        if(!spotsQuokka){
            alreadySpotted = false;
            t += DTSCALE * dt;
            posHawk.set(Math.round(RADIUS * Math.cos(SPEED * t)) + posHawk.x, Math.round(RADIUS * Math.sin(SPEED * t)) + posHawk.y);
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
