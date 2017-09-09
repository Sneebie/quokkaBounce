package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Eric on 9/1/2017.
 */

public class Button {
    private Texture buttonTexture;
    private Vector2 posButton;
    private Rectangle buttonBounds;
    private int level;

    public Button(Texture texture, float x, float y, int level){
        this.level = level;

        setTexture(texture);

        posButton = new Vector2(x, y);

        System.out.println(posButton.y);
        buttonBounds = new Rectangle(posButton.x, posButton.y, buttonTexture.getWidth(), buttonTexture.getHeight());
    }

    public Texture getTexture() {
        return buttonTexture;
    }

    public void dispose() {
        buttonTexture.dispose();
    }

    public void setTexture(Texture texture) {
        buttonTexture = texture;
    }

    public Vector2 getPosButton() {
        return posButton;
    }

    public Rectangle getButtonBounds() {
        return buttonBounds;
    }

    public int getLevel() {
        return level;
    }
}
