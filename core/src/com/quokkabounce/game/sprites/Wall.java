package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Eric on 9/2/2017.
 */

public class Wall{
    private Texture wallTexture;
    private Vector2 posWall;
    private Rectangle wallBounds;
    private Array<Obstacle> wallSwitches;
    private float wallMove;
    private boolean hasSwitch;

    public Wall(float x, float y, String textureString){
        setTexture(textureString);

        posWall = new Vector2(x, y);
        hasSwitch = false;
        wallBounds = new Rectangle(posWall.x, posWall.y, wallTexture.getWidth(), wallTexture.getHeight());
    }

    public Wall(float x, float y, Array<Obstacle> wallSwitches, float wallMove, String textureString){
        setTexture(textureString);

        posWall = new Vector2(x, y);
        hasSwitch = true;
        this.wallMove = wallMove;
        this.wallSwitches = wallSwitches;
        wallBounds = new Rectangle(posWall.x, posWall.y, wallTexture.getWidth(), wallTexture.getHeight());
    }

    public float getWallMove() {
        return wallMove;
    }

    public boolean hasSwitch() {
        return hasSwitch;
    }

    public void setWallBounds(float x, float y, float width, float height) {
        wallBounds.set(x, y, width, height);
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

    public Array<Obstacle> getWallSwitches() {
        return wallSwitches;
    }

    public boolean collides(Rectangle player) {
        return player.overlaps(wallBounds);
    }

    public void dispose() {
        wallTexture.dispose();
    }

    public void setTexture(String textureString) {
        wallTexture = new Texture(textureString);
    }

    public Rectangle getWallBounds() {
        return wallBounds;
    }
}
