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
        cloudPoly = new Polygon(new float[]{posCloud.x + 5, posCloud.y + 71, posCloud.x + 18, posCloud.y + 25, posCloud.x + 60, posCloud.y + 4, posCloud.x + 98, posCloud.y + 16, posCloud.x + 126, posCloud.y + 12, posCloud.x + 188, posCloud.y + 10, posCloud.x + 214, posCloud.y + 25, posCloud.x + 237, posCloud.y + 35, posCloud.x + 252, posCloud.y + 52, posCloud.x + 250, posCloud.y + 75, posCloud.x + 218, posCloud.y + 120, posCloud.x + 153, posCloud.y + 139, posCloud.x + 132, posCloud.y + 136, posCloud.x + 92, posCloud.y + happyCloud.getHeight() - 4, posCloud.x + 44, posCloud.y + 131, posCloud.x + 26, posCloud.y + 112, posCloud.x + 5, posCloud.y + 80});
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
