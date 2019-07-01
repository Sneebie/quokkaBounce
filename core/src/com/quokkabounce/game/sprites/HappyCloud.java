package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Eric on 9/1/2017.
 */

public class HappyCloud extends Cloud{
    private Texture happyCloud;
    private Vector2 posCloud;
    private Polygon cloudPoly;

    public HappyCloud(float x, float y){
        setTexture();

        posCloud = new Vector2(x, y);
        //260x152
        cloudPoly = new Polygon(new float[]{posCloud.x, posCloud.y + 71, posCloud.x + 16, posCloud.y + 25, posCloud.x + 65, posCloud.y + 3, posCloud.x + 101, posCloud.y + 14, posCloud.x + 125, posCloud.y + 9, posCloud.x + 185, posCloud.y + 7, posCloud.x + 214, posCloud.y + 23, posCloud.x + 235, posCloud.y + 32, posCloud.x + 253, posCloud.y + 49, posCloud.x + 251, posCloud.y + 73, posCloud.x + 213, posCloud.y + 124, posCloud.x + 152, posCloud.y + 143, posCloud.x + 132, posCloud.y + 140, posCloud.x + 96, posCloud.y + happyCloud.getHeight(), posCloud.x + 49, posCloud.y + 136, posCloud.x + 28, posCloud.y + 114, posCloud.x + 2, posCloud.y + 80});
    }

    @Override
    public Vector2 getPosCloud() {
        return posCloud;
    }

    public Polygon getCloudPoly() {
        return cloudPoly;
    }

    @Override
    public Texture getTexture() {
        return happyCloud;
    }

    @Override
    public void dispose() {
        happyCloud.dispose();
    }

    @Override
    public void setTexture() {
        happyCloud = new Texture("happyCloud.png");
    }
}
