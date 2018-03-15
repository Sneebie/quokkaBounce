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
    private Vector2 posMeteor, velMeteor, gravity;

    public Meteor(float x, float y, float firstVelX, float firstVelY){
        gravity = new Vector2(0, -13);
        posMeteor = new Vector2(x, y);
        velMeteor = new Vector2(firstVelX, firstVelY);
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

    public boolean collides(Rectangle player) {
        return player.overlaps(meteorBounds);
    }

    public void dispose() {
        meteorTexture.dispose();
    }

    public void move(float dt){
        if (posMeteor.y > 0) {
            velMeteor.add(gravity);
        }
        else{

        }
        velMeteor.scl(dt);
        posMeteor.add(velMeteor.x, velMeteor.y);
        velMeteor.scl(1/dt);
        meteorBounds.setPosition(posMeteor.x, posMeteor.y);
    }

}
