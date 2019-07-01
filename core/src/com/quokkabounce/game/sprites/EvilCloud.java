package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Eric on 8/29/2017.
 */

public class EvilCloud extends Cloud{
    private Texture evilCloud;
    private Vector2 posCloud;
    private Polygon cloudPoly;

    public EvilCloud(float x, float y){
        setTexture();

        posCloud = new Vector2(x, y);

        cloudPoly = new Polygon(new float[]{posCloud.x, posCloud.y + 100, posCloud.x + 9, posCloud.y + 47, posCloud.x + 26, posCloud.y + 29, posCloud.x + 73, posCloud.y + 13, posCloud.x + 151, posCloud.y, posCloud.x + 222, posCloud.y + 15, posCloud.x + 248, posCloud.y + 45, posCloud.x + evilCloud.getWidth(), posCloud.y + 101, posCloud.x + 208, posCloud.y + 132, posCloud.x + 183, posCloud.y + 138, posCloud.x + 115, posCloud.y + evilCloud.getHeight(), posCloud.x + 59, posCloud.y + 140});
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
        return evilCloud;
    }

    @Override
    public void dispose() {
        evilCloud.dispose();
    }

    @Override
    public void setTexture() {
        evilCloud = new Texture("evilCloud.png");
    }

}
