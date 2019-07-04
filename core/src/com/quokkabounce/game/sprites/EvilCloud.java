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
        //255x151
        cloudPoly = new Polygon(new float[]{posCloud.x + 17, posCloud.y + 107, posCloud.x + 6, posCloud.y + 93, posCloud.x + 27, posCloud.y + 65, posCloud.x + 13, posCloud.y + 50, posCloud.x + 24, posCloud.y + 30, posCloud.x + 67, posCloud.y + 33, posCloud.x + 73, posCloud.y + 14, posCloud.x + 135, posCloud.y + 22, posCloud.x + 153, posCloud.y + 6, posCloud.x + 185, posCloud.y + 21, posCloud.x + 207, posCloud.y + 12, posCloud.x + 222, posCloud.y + 15, posCloud.x + 218, posCloud.y + 39, posCloud.x + 243, posCloud.y + 41, posCloud.x + 245, posCloud.y + 49, posCloud.x + 238, posCloud.y + 72, posCloud.x + evilCloud.getWidth() - 5, posCloud.y + 101, posCloud.x + 235, posCloud.y + 103, posCloud.x + 215, posCloud.y + 111, posCloud.x + 209, posCloud.y + 130, posCloud.x + 182, posCloud.y + 136, posCloud.x + 156, posCloud.y + 121, posCloud.x + 131, posCloud.y + 142, posCloud.x + 113, posCloud.y + evilCloud.getHeight() - 7, posCloud.x + 85, posCloud.y + 118, posCloud.x + 59, posCloud.y + 136, posCloud.x + 43, posCloud.y + 109});
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
