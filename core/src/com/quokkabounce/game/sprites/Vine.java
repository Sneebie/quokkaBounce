package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Eric on 9/30/2017.
 */

public class Vine  {
    private Texture vineTexture;
    private Vector2 posVine;
    private Rectangle vineBounds;
    private int layer, quokkaX;

    public Vine(float x, float y, int layer, int quokkaX){
        this.quokkaX = quokkaX;
        this.layer = layer;
        vineTexture = new Texture("evilCloud.png");

        posVine = new Vector2(x, y);

        vineBounds = new Rectangle(posVine.x, posVine.y, vineTexture.getWidth(), vineTexture.getHeight());
    }

    public Vector2 getPosVine() {
        return posVine;
    }

    public Texture getTexture() {
        return vineTexture;
    }

    public boolean collides(Rectangle player) {
        return player.overlaps(vineBounds);
    }

    public void dispose() {
        vineTexture.dispose();
    }

    public Rectangle getVineBounds() {
        return vineBounds;
    }

    public int getLayer() {
        return layer;
    }

    public int getQuokkaX() {
        return quokkaX;
    }
}
