package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Eric on 9/14/2017.
 */

public class MoveWall {
    private Texture wallTexture;
    private Vector2 posWall, bl, br, ul, ur;
    private Rectangle wallBounds;
    private Vector2 velocity;
    private int direction, distance;

    public MoveWall(float x, float y, float speed, float distance, int direction){
        setTexture();
        switch(direction){
            case 0:
                velocity = new Vector2(speed, 0);
                break;
            case 1:
                velocity = new Vector2(0, -1 * speed);
                break;
            case 2:
                velocity = new Vector2(-1 * speed, 0);
                break;
            case 3:
                velocity = new Vector2(0, speed);
                break;
        }

        posWall = new Vector2(x, y);
        wallBounds = new Rectangle(posWall.x, posWall.y, wallTexture.getWidth(), wallTexture.getHeight());
        bl = new Vector2(x,y);
        br = new Vector2(x + getWallBounds().getWidth(), y);
        ul = new Vector2(x, y + getWallBounds().getHeight());
        ur = new Vector2(x + getWallBounds().getWidth(), y + getWallBounds().getHeight());
    }

    public void setPosWall(float x, float y) {
        posWall.set(x, y);
        bl.set(x,y);
        br.set(x + getWallBounds().getWidth(), y);
        ul.set(x, y + getWallBounds().getHeight());
        ur.set(x + getWallBounds().getWidth(), y + getWallBounds().getHeight());
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

    public Vector2 getBl() {
        return bl;
    }

    public Vector2 getBr() {
        return br;
    }

    public Vector2 getUl() {
        return ul;
    }

    public Vector2 getUr() {
        return ur;
    }

    public void update(float dt){
        if(posWall.x >= distance || posWall.x <= -1 *distance){
            velocity.set(-1 * velocity.x, 0);
        }
        if(posWall.y >= distance || posWall.y <= -1 *distance){
            velocity.set(0, -1 * velocity.y);
        }
        velocity.scl(dt);
        posWall.add(velocity.x, velocity.y);
        velocity.scl(1/dt);
        wallBounds.set(posWall.x, posWall.y, wallTexture.getWidth(), wallTexture.getHeight());
        bl.set(posWall.x,posWall.y);
        br.set(posWall.x + getWallBounds().getWidth(), posWall.y);
        ul.set(posWall.x, posWall.y + getWallBounds().getHeight());
        ur.set(posWall.x + getWallBounds().getWidth(), posWall.y + getWallBounds().getHeight());
    }
}
