package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Stoplight {
    private Texture stoplightTexture;
    private Vector2 posStoplight;
    private Rectangle stoplightBounds;

    public Stoplight(float x, float y){
        stoplightTexture = new Texture("stoplight.png");

        posStoplight = new Vector2(x, y);

        stoplightBounds = new Rectangle(posStoplight.x, posStoplight.y, stoplightTexture.getWidth(), stoplightTexture.getHeight());
    }

    public Vector2 getPosStoplight() {
        return posStoplight;
    }

    public Texture getTexture() {
        return stoplightTexture;
    }

    public boolean collides(Rectangle player) {
        return player.overlaps(stoplightBounds);
    }

    public void dispose() {
        stoplightTexture.dispose();
    }

    public Rectangle getStoplightBounds() {
        return stoplightBounds;
    }
}
