package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Eric on 9/14/2017.
 */

public class MoveWall {
    private Texture wallTexture;
    private Vector2 posWall, previousPos;
    private Rectangle wallBounds;
    private Vector2 velocity;
    private int direction, distance;

    public MoveWall(float x, float y, float speed, float distance, int direction){
        setTexture();
        switch(direction) {
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
        previousPos = new Vector2();
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
    
    public void update(float dt){
        previousPos.set(posWall);
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
    }
}
