package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Eric on 9/1/2017.
 */

public class HappyCloud extends Cloud{
    private Texture happyCloud;
    private Vector2 posCloud;
    private Rectangle cloudBounds;

    public HappyCloud(float x, float y){
        setTexture();

        posCloud = new Vector2(x, y);

        cloudBounds = new Rectangle(posCloud.x, posCloud.y, happyCloud.getWidth(), happyCloud.getHeight());
    }

    @Override
    public Vector2 getPosCloud() {
        return posCloud;
    }

    @Override
    public Texture getTexture() {
        return happyCloud;
    }

    @Override
    public void reposition(float x, float y) {
        posCloud.set(x,y);
        cloudBounds.setPosition(posCloud.x, posCloud.y);
    }

    @Override
    public boolean collides(Rectangle player) {
        return player.overlaps(cloudBounds);
    }

    @Override
    public void dispose() {
        happyCloud.dispose();
    }

    @Override
    public void setTexture() {
        happyCloud = new Texture("happyCloud.png");
    }
}
