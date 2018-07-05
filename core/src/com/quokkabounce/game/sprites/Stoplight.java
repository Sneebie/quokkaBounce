package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Stoplight {
    private static float LIGHTCHANGE = 4000f;
    private static float REDTIME = 1000f;

    private Texture stoplightTexture;
    private Vector2 posStoplight;
    private Rectangle stoplightBounds;
    private String lightColor;
    private float timeElapsed;

    public Stoplight(float x, float y){
        stoplightTexture = new Texture("stoplight.png");

        posStoplight = new Vector2(x, y);

        stoplightBounds = new Rectangle(posStoplight.x, posStoplight.y, stoplightTexture.getWidth(), stoplightTexture.getHeight());
        lightColor = "green";
        timeElapsed = 0;
    }

    public Vector2 getPosStoplight() {
        return posStoplight;
    }

    public Texture getTexture() {
        return stoplightTexture;
    }

    public boolean collides(Rectangle player) {
        return player.overlaps(stoplightBounds);
    }

    public void dispose() {
        stoplightTexture.dispose();
    }

    public String getLightColor() {
        return lightColor;
    }

    public void setLightColor(String lightColor) {
        this.lightColor = lightColor;
    }

    public Rectangle getStoplightBounds() {
        return stoplightBounds;
    }

    public void update(float dt){
        timeElapsed += dt;
        if(timeElapsed >= 0.9 * LIGHTCHANGE){
            lightColor = "yellow";
            if(timeElapsed >= LIGHTCHANGE){
                lightColor = "red";
                if(timeElapsed >= LIGHTCHANGE + REDTIME){
                    lightColor = "green";
                    timeElapsed = 0;
                }
            }
        }
    }
}
