package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Eric on 9/1/2017.
 */

public abstract class Cloud {

    public abstract Vector2 getPosCloud();

    public abstract Texture getTexture();

    public abstract void dispose();

    public  abstract void setTexture();
}
