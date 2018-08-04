package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Eric on 8/29/2017.
 */

public class EvilCloud extends Cloud{
    private Texture evilCloud;
    private Vector2 posCloud;
    private Rectangle cloudBounds;

    public EvilCloud(float x, float y){
        setTexture();

        posCloud = new Vector2(x, y);

        cloudBounds = new Rectangle(posCloud.x + 20, posCloud.y + 31, 222, 77);
    }

    @Override
    public Vector2 getPosCloud() {
        return posCloud;
    }

    @Override
    public Texture getTexture() {
        return evilCloud;
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
        evilCloud.dispose();
    }

    @Override
    public void setTexture() {
        evilCloud = new Texture("evilCloud.png");
    }

}
