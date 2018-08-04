package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Eric on 3/14/2018.
 */

public class TallDino{
    private Texture tallDinoTexture, tallDinoTexture2;
    private Vector2 posTallDino, velTallDino;
    private Rectangle tallDinoBounds;
    private float endPos, startPos;
    private int dir;
    private boolean flipped;

    public TallDino(float x, float y, float endX, float speed){
        setTexture("tallDino.png");
        setTexture2("tallDino2.png");
        endPos = endX;
        startPos = x;
        dir = 1;
        posTallDino = new Vector2(x, y);
        velTallDino = new Vector2(speed, 0);
        flipped = false;
        tallDinoBounds = new Rectangle(posTallDino.x + tallDinoTexture.getWidth() / 4, posTallDino.y, tallDinoTexture.getWidth() / 2, tallDinoTexture.getHeight());
    }

    public Vector2 getPosTallDino() {
        return posTallDino;
    }

    public Texture getTexture() {
        return flipped? tallDinoTexture : tallDinoTexture2;
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

    public void setTexture2(String textureString) {
        tallDinoTexture2 = new Texture(textureString);
    }

    public void move(float dt){
        velTallDino.scl(dt);
        if(startPos < endPos) {
            if (getPosTallDino().x < startPos) {
                dir = 1;
                flipped = false;
            } else if (getPosTallDino().x >= endPos) {
                dir = -1;
                flipped = true;
            }
        }
        else{
            if (getPosTallDino().x < endPos) {
                dir = 1;
                flipped = false;
            } else if (getPosTallDino().x >= startPos) {
                dir = -1;
                flipped = true;
            }
        }
        posTallDino.add(velTallDino.x * dir, velTallDino.y * dir);
        velTallDino.scl(1/dt);
        tallDinoBounds.setPosition(posTallDino.x, posTallDino.y);
    }
}