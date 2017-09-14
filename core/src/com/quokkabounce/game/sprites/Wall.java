package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Eric on 9/2/2017.
 */

public class Wall{
    private Texture wallTexture;
    private Vector2 posWall;
    private Rectangle wallBounds;

    public Wall(float x, float y){
        setTexture();

        posWall = new Vector2(x, y);

        wallBounds = new Rectangle(posWall.x, posWall.y, wallTexture.getWidth(), wallTexture.getHeight());
    }

    public void setPosWall(float x, float y) {
        posWall.set(x, y);
    }

    public Vector2 getPosWall() {
        return posWall;
    }

    public Texture getTexture() {
        return wallTexture;
    }

    public boolean collides(Rectangle player) {
        return player.overlaps(wallBounds);
    }

    public void dispose() {
        wallTexture.dispose();
    }

    public void setTexture() {
        wallTexture = new Texture("wall.png");
    }

    public Rectangle getWallBounds() {
        return wallBounds;
    }
}
