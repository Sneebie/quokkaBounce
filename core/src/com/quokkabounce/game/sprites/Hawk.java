package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Eric on 9/6/2017.
 */

public class Hawk {
    private static final double SPEED = 0.1;
    private static final double RADIUS = 10;
    private static final int ATTACKSPEED = 10;
    private static final int DTSCALE = 62;

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

        posHawk = new Vector2(Math.round(RADIUS * Math.cos(SPEED * t)) + x, Math.round(RADIUS * Math.sin(SPEED * t)) + y);

        velHawk = new Vector2(Math.round(SPEED * RADIUS * -1 * Math.sin(SPEED * t)), Math.round(SPEED * RADIUS * Math.cos(SPEED * t)));

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
            t += DTSCALE * dt;
            while(SPEED*t>=2*Math.PI){
                t-=2*Math.PI/SPEED;
            }
            System.out.println(posHawk.x);
            posHawk.set(Math.round(RADIUS * Math.cos(SPEED * t)) + posHawk.x, Math.round(RADIUS * Math.sin(SPEED * t)) + posHawk.y);
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
