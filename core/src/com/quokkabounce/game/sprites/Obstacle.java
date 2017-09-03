package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Eric on 9/3/2017.
 */

public class Obstacle {
    private Texture obstacleTexture;
    private Vector2 posObstacle;
    private Rectangle obstacleBounds;

    public Obstacle(float x, float y, String textureString){
        obstacleTexture = new Texture(textureString);

        posObstacle = new Vector2(x, y);

        obstacleBounds = new Rectangle(posObstacle.x, posObstacle.y, obstacleTexture.getWidth(), obstacleTexture.getHeight());
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
}
