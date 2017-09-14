package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Eric on 9/14/2017.
 */

public class Switch {
    private Texture switchTexture;
    private Vector2 posSwitch;
    private Rectangle switchBounds;
    private Wall wall;
    private float wallMove;

    public Switch(float x, float y, Wall wall, float wallMove){
        setTexture();

        this.wallMove = wallMove;
        this.wall = wall;
        posSwitch = new Vector2(x, y);
        switchBounds = new Rectangle(posSwitch.x, posSwitch.y, switchTexture.getWidth(), switchTexture.getHeight());
    }

    public float getWallMove() {
        return wallMove;
    }

    public Wall getWall() {
        return wall;
    }

    public Vector2 getPosSwitch() {
        return posSwitch;
    }

    public Texture getTexture() {
        return switchTexture;
    }

    public boolean collides(Rectangle player) {
        return player.overlaps(switchBounds);
    }

    public void dispose() {
        switchTexture.dispose();
    }

    public void setTexture() {
        switchTexture = new Texture("switch.png");
    }

    public Rectangle getSwitchBounds() {
        return switchBounds;
    }
}
