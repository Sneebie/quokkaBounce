package com.quokkabounce.game.sprites;

/**
 * Created by Eric on 3/14/2018.
 */

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Eric on 9/6/2017.
 */

public class Meteor {

    private Rectangle meteorBounds;
    private Texture meteorTexture;
    private Vector2 posMeteor, drawPosMeteor, velMeteor, originalPos, originalVel, gravity, previousPos;
    private boolean startFall;

    public Meteor(float x, float y, float firstVelX, float firstVelY){
        gravity = new Vector2(0, -13);
        posMeteor = new Vector2(x, y);
        originalPos = new Vector2(x, y);
        drawPosMeteor = new Vector2(x, y);
        velMeteor = new Vector2(firstVelX, firstVelY);
        originalVel = new Vector2(firstVelX, firstVelY);
        previousPos = new Vector2(x, y);
        meteorTexture = new Texture("wallSwitch.png");
        meteorBounds = new Rectangle(posMeteor.x, posMeteor.y, meteorTexture.getWidth(), meteorTexture.getHeight());
    }

    public Rectangle getMeteorBounds() {
        return meteorBounds;
    }

    public Vector2 getPosMeteor() {
        return posMeteor;
    }

    public Texture getTexture() {
        return meteorTexture;
    }

    public Vector2 getOriginalPos() {
        return originalPos;
    }

    public void setPosMeteor(Vector2 posMeteor) {
        this.posMeteor = posMeteor;
    }

    public void setVelMeteor(Vector2 velMeteor) {
        this.velMeteor = velMeteor;
    }

    public Vector2 getPreviousPos() {
        return previousPos;
    }

    public Vector2 getDrawPosMeteor() {
        return drawPosMeteor;
    }

    public void setDrawPosMeteor(Vector2 drawPosMeteor) {
        this.drawPosMeteor = drawPosMeteor;
    }

    public Vector2 getOriginalVel() {
        return originalVel;
    }

    public boolean isStartFall() {
        return startFall;
    }

    public void setStartFall(boolean startFall) {
        this.startFall = startFall;
    }

    public boolean collides(Rectangle player) {
        return player.overlaps(meteorBounds);
    }

    public void dispose() {
        meteorTexture.dispose();
    }

    public void move(float dt){
        previousPos.set(posMeteor);
        velMeteor.add(gravity);
        velMeteor.scl(dt);
        posMeteor.add(velMeteor.x, velMeteor.y);
        velMeteor.scl(1/dt);
        meteorBounds.setPosition(posMeteor.x, posMeteor.y);
    }

}
