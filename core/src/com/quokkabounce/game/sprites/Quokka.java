package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Eric on 8/29/2017.
 */

public class Quokka {
    private Vector3 position, velocity, gravity;
    private Texture quokka;
    private Rectangle quokkaBounds;

    public Vector3 getGravity() {
        return gravity;
    }

    public Quokka(float x, float y){
        gravity = new Vector3(0, -13, 0);
        position = new Vector3(x,y,0);
        velocity = new Vector3(0,0,0);
        quokka = new Texture("quokka.png");
        quokkaBounds = new Rectangle(x, y, quokka.getWidth(), quokka.getHeight());
    }

    public void update(float dt){
        if (position.y > 0) {
            velocity.add(gravity);
        }
        velocity.scl(dt);
        position.add(velocity.x, velocity.y, 0);
        if(position.y < 0){
            position.y=0;
        }
        velocity.scl(1/dt);
        quokkaBounds.setPosition(position.x, position.y);
    }

    public Vector3 getPosition() {
        return position;
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
