package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Eric on 8/29/2017.
 */

public class EvilCloud {
    public  static final int CLOUD_WIDTH = 252;
    private Texture evilCloud;
    private Vector2 posCloud;
    private Rectangle cloudBounds;

    public EvilCloud(float x, float y){
        evilCloud = new Texture("evilCloud.png");

        posCloud = new Vector2(x, y);

        cloudBounds = new Rectangle(posCloud.x, posCloud.y, evilCloud.getWidth(), evilCloud.getHeight());
    }

    public Vector2 getPosCloud() {
        return posCloud;
    }

    public Texture getTexture() {
        return evilCloud;
    }

    public void reposition(float x, float y){
        posCloud.set(x,y);
        cloudBounds.setPosition(posCloud.x, posCloud.y);
    }

    public boolean collides(Rectangle player){
        return player.overlaps(cloudBounds);
    }

    public void dispose(){
        evilCloud.dispose();
    }
}
