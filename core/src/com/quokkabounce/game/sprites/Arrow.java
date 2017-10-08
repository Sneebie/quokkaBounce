package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Eric on 10/7/2017.
 */
public class Arrow {
    private static final double SPEED = 0.06;
    private static final double ATTACKSPEED = 1.5;
    private static final int ATTACKDELAY = 10;

    private int t, loopTime;
    private boolean alreadySpotted, shouldShoot;

    private Rectangle arrowBounds;
    private Texture arrowTexture;
    private Vector2 posArrow, intialPos, velArrow;

    public Arrow(float x, float y) {
        loopTime = 0;
        t = 0;
        arrowTexture = new Texture("wallSwitch.png");
        alreadySpotted = false;
        posArrow = new Vector2(x, y);
        intialPos = new Vector2(x, y);
        velArrow = new Vector2();
        arrowBounds = new Rectangle(posArrow.x, posArrow.y, arrowTexture.getWidth(), arrowTexture.getHeight());
    }

    public Rectangle getArrowBounds() {
        return arrowBounds;
    }

    public Vector2 getPosArrow() {
        return posArrow;
    }

    public Texture getTexture() {
        return arrowTexture;
    }

    public Vector2 getIntialPos() {
        return intialPos;
    }

    public void setShouldShoot(boolean shouldShoot) {
        this.shouldShoot = shouldShoot;
    }

    public void setPosArrow(Vector2 posArrow) {
        this.posArrow = posArrow;
    }

    public boolean collides(Rectangle player) {
        return player.overlaps(arrowBounds);
    }

    public void dispose() {
        arrowTexture.dispose();
    }

    public void move(boolean spotsQuokka, float dt, Vector3 posQuokka) {
        if(spotsQuokka){
            shouldShoot = true;
        }
        if(shouldShoot) {
            if (!alreadySpotted) {
                velArrow.set(Math.round((posQuokka.x - posArrow.x) * ATTACKSPEED), Math.round((posQuokka.y - posArrow.y) * ATTACKSPEED));
                alreadySpotted = true;
                loopTime = 0;
            }
            if (loopTime >= ATTACKDELAY) {
                velArrow.scl(dt);
                posArrow.set(posArrow.x + velArrow.x, posArrow.y + velArrow.y);
                velArrow.scl(1 / dt);
            } else {
                loopTime++;
            }
        }
    }

}