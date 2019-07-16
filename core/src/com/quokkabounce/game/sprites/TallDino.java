package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Eric on 3/14/2018.
 */

public class TallDino{
    private Texture tallDinoTexture, tallDinoTexture2;
    private Vector2 posTallDino, velTallDino;
    private Polygon tallDinoPolygon;
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
        tallDinoPolygon = new Polygon(new float[]{230, 2, 141, 350, 152, 406, 228, 476, 229, 504, 211, 540, 173, 567, 159, 580, 125, 592, 101, 593, 45, 574, 30, 559, 17, 518, 10, 468, 3, 441, 4, 370, 25, 321, 91, 176, 112, 71, 105, 17, 109, 2});
    }

    public Vector2 getPosTallDino() {
        return posTallDino;
    }

    public Texture getTexture() {
        return flipped? tallDinoTexture : tallDinoTexture2;
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

    public Polygon getTallDinoPolygon() {
        return tallDinoPolygon;
    }

    public void move(float dt){
        velTallDino.scl(dt);
        if(startPos < endPos) {
            if (getPosTallDino().x < startPos) {
                dir = 1;
                flipped = false;
                for(int i = 0; i < tallDinoPolygon.getVertices().length; i++){
                    if(i % 2 == 0){
                        tallDinoPolygon.getVertices()[i] = 232 - tallDinoPolygon.getVertices()[i];
                    }
                }
            } else if (getPosTallDino().x >= endPos) {
                dir = -1;
                flipped = true;
                for(int i = 0; i < tallDinoPolygon.getVertices().length; i++){
                    if(i % 2 == 0){
                        tallDinoPolygon.getVertices()[i] = 232 - tallDinoPolygon.getVertices()[i];
                    }
                }
            }
        }
        else{
            if (getPosTallDino().x < endPos) {
                dir = 1;
                flipped = false;
                for(int i = 0; i < tallDinoPolygon.getVertices().length; i++){
                    if(i % 2 == 0){
                        tallDinoPolygon.getVertices()[i] = 232 - tallDinoPolygon.getVertices()[i];
                    }
                }
            } else if (getPosTallDino().x >= startPos) {
                dir = -1;
                flipped = true;
                for(int i = 0; i < tallDinoPolygon.getVertices().length; i++){
                    if(i % 2 == 0){
                        tallDinoPolygon.getVertices()[i] = 232 - tallDinoPolygon.getVertices()[i];
                    }
                }
            }
        }
        posTallDino.add(velTallDino.x * dir, velTallDino.y * dir);
        velTallDino.scl(1/dt);
        tallDinoPolygon.setPosition(posTallDino.x, posTallDino.y);
    }
}