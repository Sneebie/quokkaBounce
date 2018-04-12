package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Eric on 9/3/2017.
 */

public class Obstacle {
    private static int SPEED = 100;
    private Texture obstacleTexture;
    private Vector2 posObstacle, velObstacle;
    private Rectangle obstacleBounds;
    private Array<Vector2> moveSpots;
    private int moveTracker = 1;

    public Obstacle(float x, float y, String textureString){
        obstacleTexture = new Texture(textureString);

        posObstacle = new Vector2(x, y);

        obstacleBounds = new Rectangle(posObstacle.x, posObstacle.y, obstacleTexture.getWidth(), obstacleTexture.getHeight());
    }

    public Obstacle(float x, float y, float width, float height){
        posObstacle = new Vector2(x, y);

        obstacleBounds = new Rectangle(posObstacle.x, posObstacle.y, width, height);
    }

    public Obstacle(float x, float y, String textureString, Array<Vector2> moveSpots){
        obstacleTexture = new Texture(textureString);

        posObstacle = new Vector2(x, y);

        obstacleBounds = new Rectangle(posObstacle.x, posObstacle.y, obstacleTexture.getWidth(), obstacleTexture.getHeight());
        this.moveSpots = new Array<Vector2>(moveSpots);
        velObstacle = new Vector2(moveSpots.get(moveTracker).x - moveSpots.get(moveTracker - 1).x, moveSpots.get(moveTracker).y - moveSpots.get(moveTracker - 1).y);
        System.out.println(velObstacle);
        velObstacle.scl(SPEED / velObstacle.len());
        System.out.println(velObstacle);
    }

    public void move(float dt) {
        velObstacle.scl(dt);
        posObstacle.add(velObstacle.x, velObstacle.y);
        velObstacle.scl(1/dt);
        obstacleBounds.setPosition(posObstacle.x, posObstacle.y);
        if(velObstacle.x > 0 && posObstacle.x >= moveSpots.get(moveTracker).x) {
            if(velObstacle.y > 0 && posObstacle.y >= moveSpots.get(moveTracker).y) {
                if (moveTracker < moveSpots.size - 1) {
                    posObstacle.set(moveSpots.get(moveTracker));
                    moveTracker++;
                    velObstacle.set(moveSpots.get(moveTracker).x - moveSpots.get(moveTracker - 1).x, moveSpots.get(moveTracker).y - moveSpots.get(moveTracker - 1).y);
                    velObstacle.scl(SPEED / velObstacle.len());
                } else {
                    posObstacle.set(moveSpots.get(moveTracker));
                    moveTracker = 0;
                    velObstacle.set(moveSpots.get(moveTracker).x - moveSpots.get(moveSpots.size - 1).x, moveSpots.get(moveTracker).y - moveSpots.get(moveSpots.size - 1).y);
                    velObstacle.scl(SPEED / velObstacle.len());
                }
            }
            else if(velObstacle.y <= 0 && posObstacle.y <= moveSpots.get(moveTracker).y){
                if (moveTracker < moveSpots.size - 1) {
                    posObstacle.set(moveSpots.get(moveTracker));
                    moveTracker++;
                    velObstacle.set(moveSpots.get(moveTracker).x - moveSpots.get(moveTracker - 1).x, moveSpots.get(moveTracker).y - moveSpots.get(moveTracker - 1).y);
                    velObstacle.scl(SPEED / velObstacle.len());
                } else {
                    posObstacle.set(moveSpots.get(moveTracker));
                    moveTracker = 0;
                    velObstacle.set(moveSpots.get(moveTracker).x - moveSpots.get(moveSpots.size - 1).x, moveSpots.get(moveTracker).y - moveSpots.get(moveSpots.size - 1).y);
                    velObstacle.scl(SPEED / velObstacle.len());
                }
            }
        }
        else if(velObstacle.x < 0 && posObstacle.x <= moveSpots.get(moveTracker).x) {
            if(velObstacle.y > 0 && posObstacle.y >= moveSpots.get(moveTracker).y) {
                if (moveTracker < moveSpots.size - 1) {
                    posObstacle.set(moveSpots.get(moveTracker));
                    moveTracker++;
                    velObstacle.set(moveSpots.get(moveTracker).x - moveSpots.get(moveTracker - 1).x, moveSpots.get(moveTracker).y - moveSpots.get(moveTracker - 1).y);
                    velObstacle.scl(SPEED / velObstacle.len());
                } else {
                    posObstacle.set(moveSpots.get(moveTracker));
                    moveTracker = 0;
                    velObstacle.set(moveSpots.get(moveTracker).x - moveSpots.get(moveSpots.size - 1).x, moveSpots.get(moveTracker).y - moveSpots.get(moveSpots.size - 1).y);
                    velObstacle.scl(SPEED / velObstacle.len());
                }
            }
            else if(velObstacle.y <= 0 && posObstacle.y <= moveSpots.get(moveTracker).y){
                if (moveTracker < moveSpots.size - 1) {
                    posObstacle.set(moveSpots.get(moveTracker));
                    moveTracker++;
                    velObstacle.set(moveSpots.get(moveTracker).x - moveSpots.get(moveTracker - 1).x, moveSpots.get(moveTracker).y - moveSpots.get(moveTracker - 1).y);
                    velObstacle.scl(SPEED / velObstacle.len());
                } else {
                    posObstacle.set(moveSpots.get(moveTracker));
                    moveTracker = 0;
                    velObstacle.set(moveSpots.get(moveTracker).x - moveSpots.get(moveSpots.size - 1).x, moveSpots.get(moveTracker).y - moveSpots.get(moveSpots.size - 1).y);
                    velObstacle.scl(SPEED / velObstacle.len());
                }
            }
        }
    }

    public Vector2 getPosObstacle() {
        return posObstacle;
    }

    public Texture getTexture() {
        return obstacleTexture;
    }

    public boolean collides(Rectangle player) {
        return player.overlaps(obstacleBounds);
    }

    public void dispose() {
        obstacleTexture.dispose();
    }

    public Rectangle getObstacleBounds() {
        return obstacleBounds;
    }

}
