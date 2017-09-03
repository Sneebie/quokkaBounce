package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Eric on 9/2/2017.
 */

public class BonusQuokka {
    private Texture quokkaTexture;
    private Vector2 posQuokka;
    private Rectangle quokkaBounds;

    public BonusQuokka(float x, float y){
        setTexture();

        posQuokka = new Vector2(x, y);

        quokkaBounds = new Rectangle(posQuokka.x, posQuokka.y, quokkaTexture.getWidth(), quokkaTexture.getHeight());
    }

    public Vector2 getPosQuokka() {
        return posQuokka;
    }

    public Texture getTexture() {
        return quokkaTexture;
    }

    public boolean collides(Rectangle player) {
        return player.overlaps(quokkaBounds);
    }

    public void dispose() {
        quokkaTexture.dispose();
    }

    public void setTexture() {
        quokkaTexture = new Texture("quokkaPrisoner.png");
    }

}
