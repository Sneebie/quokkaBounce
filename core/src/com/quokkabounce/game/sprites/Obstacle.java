package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Eric on 9/3/2017.
 */

public class Obstacle {
    private Texture obstacleTexture;
    private Vector2 posObstacle, velObstacle;
    private Rectangle obstacleBounds;
    private Circle obstacleCircle;
    private Array<Vector2> moveSpots;
    private int moveTracker = 1;
    private int netDistance = 0;
    private int speed, lineback;
    private float totalDistance = 0;

    public Obstacle(float x, float y, String textureString){
        obstacleTexture = new Texture(textureString);
        posObstacle = new Vector2(x, y);
        if(textureString == "portal.png") {
            obstacleBounds = new Rectangle(posObstacle.x, posObstacle.y, obstacleTexture.getWidth(), obstacleTexture.getHeight());
            obstacleTexture.dispose();
        }
        else if(textureString == "blackHole.png"){
            obstacleBounds = new Rectangle(posObstacle.x, posObstacle.y, obstacleTexture.getWidth(), obstacleTexture.getHeight());
            //obstacleTexture.dispose();
        }
        else{
            obstacleBounds = new Rectangle(posObstacle.x, posObstacle.y, obstacleTexture.getWidth(), obstacleTexture.getHeight());
        }
        if(textureString=="greenPlanet.png") {
            obstacleCircle = new Circle(posObstacle.x + obstacleBounds.getWidth() / 2, posObstacle.y + obstacleBounds.getHeight() / 2, obstacleBounds.getWidth() * 0.45f);
        }
        else if(textureString == "blackHole.png"){
            obstacleCircle = new Circle(posObstacle.x + obstacleBounds.getWidth() / 2, posObstacle.y + obstacleBounds.getHeight() / 2, obstacleBounds.getWidth() * 0.38f);
        }
        else if(textureString == "portal.png"){
            obstacleCircle = new Circle(posObstacle.x + obstacleBounds.getWidth() / 2, posObstacle.y + obstacleBounds.getHeight() / 2, 59);
        }
        else{
            obstacleCircle = new Circle(posObstacle.x + obstacleBounds.getWidth() / 2, posObstacle.y + obstacleBounds.getHeight() / 2, obstacleBounds.getWidth() / 2);
        }
    }

    public Obstacle(float x, float y, float width, float height){
        posObstacle = new Vector2(x, y);

        obstacleBounds = new Rectangle(posObstacle.x, posObstacle.y, width, height);
    }

    public Obstacle(String textureString, Array<Vector2> moveSpots, int speed, int lineback){
        obstacleTexture = new Texture(textureString);
        posObstacle = new Vector2(moveSpots.get(0).x, moveSpots.get(0).y);
        obstacleBounds = new Rectangle(posObstacle.x, posObstacle.y, obstacleTexture.getWidth(), obstacleTexture.getHeight());
        this.moveSpots = new Array<Vector2>(moveSpots);
        this.speed = speed;
        this.lineback = lineback;
        double tempDist = 0;
        for(int i = 1; i < moveSpots.size - 1; i++){
            tempDist+=Math.sqrt(Math.pow(moveSpots.get(i).y-moveSpots.get(i-1).y, 2) + Math.pow(moveSpots.get(i).x-moveSpots.get(i-1).x, 2));
        }
        netDistance = (int) Math.round(tempDist < lineback ? tempDist : lineback);
        velObstacle = new Vector2(moveSpots.get(moveTracker).x - moveSpots.get(moveTracker - 1).x, moveSpots.get(moveTracker).y - moveSpots.get(moveTracker - 1).y);
        velObstacle.scl(speed / velObstacle.len());
    }

    public Circle getObstacleCircle() {
        return obstacleCircle;
    }

    public Array<Vector2> getMoveSpots() {
        return moveSpots;
    }

    public int getMoveTracker() {
        return moveTracker;
    }

    public float getTotalDistance() {
        return totalDistance;
    }

    public int getLineback() {
        return lineback;
    }

    public void move(float dt) {
        velObstacle.scl(dt);
        posObstacle.add(velObstacle.x, velObstacle.y);
        if(totalDistance < netDistance) {
            totalDistance += Math.sqrt(Math.pow(velObstacle.x, 2) + Math.pow(velObstacle.y, 2));
        }
        else{
            totalDistance = netDistance;
        }
        velObstacle.scl(1/dt);
        obstacleBounds.setPosition(posObstacle.x, posObstacle.y);
        if(velObstacle.x > 0 && posObstacle.x >= moveSpots.get(moveTracker).x) {
            if(velObstacle.y > 0 && posObstacle.y >= moveSpots.get(moveTracker).y) {
                if (moveTracker < moveSpots.size - 1) {
                    posObstacle.set(moveSpots.get(moveTracker));
                    moveTracker++;
                    velObstacle.set(moveSpots.get(moveTracker).x - moveSpots.get(moveTracker - 1).x, moveSpots.get(moveTracker).y - moveSpots.get(moveTracker - 1).y);
                    velObstacle.scl(speed / velObstacle.len());
                } else {
                    posObstacle.set(moveSpots.get(moveTracker));
                    moveTracker = 0;
                    velObstacle.set(moveSpots.get(moveTracker).x - moveSpots.get(moveSpots.size - 1).x, moveSpots.get(moveTracker).y - moveSpots.get(moveSpots.size - 1).y);
                    velObstacle.scl(speed / velObstacle.len());
                }
            }
            else if(velObstacle.y <= 0 && posObstacle.y <= moveSpots.get(moveTracker).y){
                if (moveTracker < moveSpots.size - 1) {
                    posObstacle.set(moveSpots.get(moveTracker));
                    moveTracker++;
                    velObstacle.set(moveSpots.get(moveTracker).x - moveSpots.get(moveTracker - 1).x, moveSpots.get(moveTracker).y - moveSpots.get(moveTracker - 1).y);
                    velObstacle.scl(speed / velObstacle.len());
                } else {
                    posObstacle.set(moveSpots.get(moveTracker));
                    moveTracker = 0;
                    velObstacle.set(moveSpots.get(moveTracker).x - moveSpots.get(moveSpots.size - 1).x, moveSpots.get(moveTracker).y - moveSpots.get(moveSpots.size - 1).y);
                    velObstacle.scl(speed / velObstacle.len());
                }
            }
        }
        else if(velObstacle.x <= 0 && posObstacle.x <= moveSpots.get(moveTracker).x) {
            if(velObstacle.y > 0 && posObstacle.y >= moveSpots.get(moveTracker).y) {
                if (moveTracker < moveSpots.size - 1) {
                    posObstacle.set(moveSpots.get(moveTracker));
                    moveTracker++;
                    velObstacle.set(moveSpots.get(moveTracker).x - moveSpots.get(moveTracker - 1).x, moveSpots.get(moveTracker).y - moveSpots.get(moveTracker - 1).y);
                    velObstacle.scl(speed / velObstacle.len());
                } else {
                    posObstacle.set(moveSpots.get(moveTracker));
                    moveTracker = 0;
                    velObstacle.set(moveSpots.get(moveTracker).x - moveSpots.get(moveSpots.size - 1).x, moveSpots.get(moveTracker).y - moveSpots.get(moveSpots.size - 1).y);
                    velObstacle.scl(speed / velObstacle.len());
                }
            }
            else if(velObstacle.y <= 0 && posObstacle.y <= moveSpots.get(moveTracker).y){
                if (moveTracker < moveSpots.size - 1) {
                    posObstacle.set(moveSpots.get(moveTracker));
                    moveTracker++;
                    velObstacle.set(moveSpots.get(moveTracker).x - moveSpots.get(moveTracker - 1).x, moveSpots.get(moveTracker).y - moveSpots.get(moveTracker - 1).y);
                    velObstacle.scl(speed / velObstacle.len());
                } else {
                    posObstacle.set(moveSpots.get(moveTracker));
                    moveTracker = 0;
                    velObstacle.set(moveSpots.get(moveTracker).x - moveSpots.get(moveSpots.size - 1).x, moveSpots.get(moveTracker).y - moveSpots.get(moveSpots.size - 1).y);
                    velObstacle.scl(speed / velObstacle.len());
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
