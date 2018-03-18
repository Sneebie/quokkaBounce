package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Eric on 3/14/2018.
 */

public class TallDino{
    private Texture tallDinoTexture;
    private Vector2 posTallDino, velTallDino;
    private Rectangle tallDinoBounds;
    private float endPos, startPos;
    private int dir;

    public TallDino(float x, float y, float endX, float speed){
        setTexture("wall.png");
        endPos = endX;
        startPos = x;
        dir = 1;
        posTallDino = new Vector2(x, y);
        velTallDino = new Vector2(speed, 0);
        tallDinoBounds = new Rectangle(posTallDino.x, posTallDino.y, tallDinoTexture.getWidth(), tallDinoTexture.getHeight());
    }

    public Vector2 getPosTallDino() {
        return posTallDino;
    }

    public Texture getTexture() {
        return tallDinoTexture;
    }

    public boolean collides(Rectangle player) {
        return player.overlaps(tallDinoBounds);
    }

    public void dispose() {
        tallDinoTexture.dispose();
    }

    public void setTexture(String textureString) {
        tallDinoTexture = new Texture(textureString);
    }

    public void move(float dt){
        velTallDino.scl(dt);
        if(getPosTallDino().x < startPos){
            dir = 1;
        }
        else if(getPosTallDino().x >= endPos){
            dir = -1;
        }
        posTallDino.add(velTallDino.x * dir, velTallDino.y * dir);
        velTallDino.scl(1/dt);
        tallDinoBounds.setPosition(posTallDino.x, posTallDino.y);
    }
}