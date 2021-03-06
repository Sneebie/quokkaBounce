package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Eric on 10/7/2017.
 */
public class Arrow {
    private static final double ATTACKSPEED = 1.5;
    private static final float HORIZONTALSPEED = -600;
    private static final int ATTACKDELAY = 1;

    private int t, loopTime;
    private boolean alreadySpotted, shouldShoot, horizontal, flipped;

    private Rectangle arrowBounds;
    private Texture arrowTexture;
    private Vector2 posArrow, initialPos, velArrow;

    public Arrow(float x, float y, boolean horizontal) {
        loopTime = 0;
        t = 0;
        alreadySpotted = false;
        this.horizontal = horizontal;
        posArrow = new Vector2(x, y);
        initialPos = new Vector2(x, y);
        velArrow = new Vector2();
        if(horizontal){
            velArrow.set(HORIZONTALSPEED, 0);
        }
        arrowTexture = new Texture("arrow.png");
        arrowTexture = new Texture((this.getPosArrow().x + arrowTexture.getWidth() / 2) < 640 ? "arrow.png" : "arrow2.png");
        arrowBounds = new Rectangle(posArrow.x, posArrow.y, arrowTexture.getWidth(), arrowTexture.getHeight());
    }
    public Arrow(float x, float y) {
        loopTime = 0;
        t = 0;
        flipped = false;
        alreadySpotted = false;
        horizontal = true;
        posArrow = new Vector2(x, y);
        initialPos = new Vector2(x, y);
        velArrow = new Vector2();
        velArrow.set(HORIZONTALSPEED, 0);
        arrowTexture = new Texture("arrow.png");
        arrowTexture = new Texture((this.getPosArrow().x + arrowTexture.getWidth() / 2) < 640 ? "arrow.png" : "arrow2.png");
        arrowBounds = new Rectangle(posArrow.x + 7, posArrow.y + 19, arrowTexture.getWidth() - 7, 5);
    }

    public boolean isFlipped() {
        return flipped;
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

    public Vector2 getInitialPos() {
        return initialPos;
    }

    public void setShouldShoot(boolean shouldShoot) {
        this.shouldShoot = shouldShoot;
    }

    public void setPosArrow(Vector2 posArrow) {
        this.posArrow.set(posArrow);
    }

    public void setAlreadySpotted(boolean alreadySpotted) {
        this.alreadySpotted = alreadySpotted;
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
            if(horizontal){
                if (!alreadySpotted) {
                    if((this.getPosArrow().x + arrowTexture.getWidth() / 2) < 640){
                        velArrow.set(-1 * velArrow.x, -1 * velArrow.y);
                    }
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
            else {
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
        arrowBounds.set(posArrow.x + 7, posArrow.y + 19, arrowTexture.getWidth() - 7, 5);
    }
}