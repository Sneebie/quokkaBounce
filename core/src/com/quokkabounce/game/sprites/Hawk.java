package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Eric on 9/6/2017.
 */

public class Hawk {
    private static final int SPEED = 400;
    private static final int RADIUS = 30;
    private static final int ATTACKSPEED = 100;

    private Texture hawkTexture;
    private Rectangle hawkBounds;
    private int t;
    private boolean alreadySpotted;
    private Vector2 posHawk, velHawk;

    public Rectangle getHawkBounds() {
        return hawkBounds;
    }

    public Hawk(float x, float y){
        t = 0;

        hawkTexture = new Texture("hawk.png");

        alreadySpotted = false;

        posHawk = new Vector2(Math.round(RADIUS * Math.cos(t)) + x, Math.round(RADIUS * Math.sin(t)) + y);

        velHawk = new Vector2(Math.round(RADIUS * -1 * Math.sin(t)), Math.round(RADIUS * Math.cos(t)));

        hawkBounds = new Rectangle(posHawk.x, posHawk.y, hawkTexture.getWidth(), hawkTexture.getHeight());
    }

    public Vector2 getPosHawk() {
        return posHawk;
    }

    public Texture getTexture() {
        return hawkTexture;
    }

    public boolean collides(Rectangle player) {
        return player.overlaps(hawkBounds);
    }

    public void dispose() {
        hawkTexture.dispose();
    }

    public void move(boolean spotsQuokka, float dt, Vector3 posQuokka){
        if(!spotsQuokka){
            t += 1 / dt;
            if(t>=360){
                t=0;
            }
            System.out.println(posHawk.x);
            posHawk.set(Math.round(RADIUS * Math.cos(t)) + posHawk.x, Math.round(RADIUS * Math.sin(t)) + posHawk.y);
        }
        else{
            if(!alreadySpotted) {
                System.out.println("enemy spotted");
                velHawk.set((posHawk.x - posQuokka.x) * ATTACKSPEED, (posHawk.y - posQuokka.y) * ATTACKSPEED);
                alreadySpotted = true;
            }
        }
        hawkBounds.set(posHawk.x, posHawk.y, hawkTexture.getWidth(), hawkTexture.getHeight());
    }

}
