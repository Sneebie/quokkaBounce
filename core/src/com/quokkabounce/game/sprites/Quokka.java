package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Eric on 8/29/2017.
 */

public class Quokka {
    private Vector2 bottomLeft, bottomRight, upperLeft, upperRight, bottomLeft2, bottomRight2, upperLeft2, upperRight2;
    private Vector3 position, velocity, gravity;
    private Texture quokka;
    private Rectangle quokkaBounds;

    public Vector3 getGravity() {
        return gravity;
    }

    public Quokka(float x, float y){
        quokka = new Texture("quokka.png");
        quokkaBounds = new Rectangle(x, y, quokka.getWidth(), quokka.getHeight());
        gravity = new Vector3(0, -13, 0);
        position = new Vector3(x,y,0);
        bottomLeft = new Vector2(x,y);
        bottomRight = new Vector2(x+quokkaBounds.getWidth(), y);
        upperLeft = new Vector2(x, y+quokkaBounds.getHeight());
        upperRight = new Vector2(x+quokkaBounds.getWidth(), y+quokkaBounds.getHeight());
        bottomLeft2 = new Vector2(x,y);
        bottomRight2 = new Vector2(x+quokkaBounds.getWidth(), y);
        upperLeft2 = new Vector2(x, y+quokkaBounds.getHeight());
        upperRight2 = new Vector2(x+quokkaBounds.getWidth(), y+quokkaBounds.getHeight());
        velocity = new Vector3(0,0,0);
    }

    public void update(float dt){
        bottomLeft.set(position.x,position.y);
        bottomRight.set(position.x + quokkaBounds.getWidth(), position.y);
        upperLeft.set(position.x, position.y + quokkaBounds.getHeight());
        upperRight.set(position.x + quokkaBounds.getWidth(),position.y + quokkaBounds.getHeight());
        if (position.y > 0) {
            velocity.add(gravity);
        }
        velocity.scl(dt);
        position.add(velocity.x, velocity.y, 0);
        velocity.scl(1/dt);
        quokkaBounds.setPosition(position.x, position.y);
        bottomLeft2.set(position.x,position.y);
        bottomRight2.set(position.x + quokkaBounds.getWidth(), position.y);
        upperLeft2.set(position.x, position.y + quokkaBounds.getHeight());
        upperRight2.set(position.x + quokkaBounds.getWidth(),position.y + quokkaBounds.getHeight());
    }

    public Vector2 getBottomLeft() {
        return bottomLeft;
    }

    public Vector2 getBottomRight() {
        return bottomRight;
    }

    public Vector2 getUpperLeft() {
        return upperLeft;
    }

    public Vector2 getUpperRight() {
        return upperRight;
    }

    public Vector2 getBottomLeft2() {
        return bottomLeft2;
    }

    public Vector2 getBottomRight2() {
        return bottomRight2;
    }

    public Vector2 getUpperLeft2() {
        return upperLeft2;
    }

    public Vector2 getUpperRight2() {
        return upperRight2;
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position.set(position,0);
    }
    public void setPosition(float x, float y) {
        this.position.set(x, y, 0);
    }

    public Texture getTexture() {
        return quokka;
    }

    public Rectangle getQuokkaBounds(){
        return quokkaBounds;
    }

    public void dispose(){
        quokka.dispose();
    }

    public Vector3 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3 velocity) {
        this.velocity.set(velocity);
    }

    public void setGravity(float x, float y, float z) {
        gravity.set(x, y, z);
    }
}
