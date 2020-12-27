package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Eric on 9/6/2017.
 */

public class Hawk {
    private static final double SPEED = 0.06;
    private static final double RADIUS = 300;
    private static final double ATTACKSPEED = 1.5;
    private static final int ATTACKDELAY = 10;

    private int t, loopTime, firstWidth;
    private boolean alreadySpotted;

    private Rectangle hawkBounds;
    private Polygon hawkPolygon;
    private Polygon[] hawkPolygons;
    private Animation hawkAnimation;
    private Vector2 posHawk, velHawk;

    public Hawk(float x, float y){
        loopTime = 0;
        t = 0;
        hawkAnimation = new Animation("hawkIdle", "hawk", 11, 0.5f);
        alreadySpotted = false;
        posHawk = new Vector2(Math.round(RADIUS * Math.cos(SPEED * t)) + x, Math.round(RADIUS * Math.sin(SPEED * t)) + y);
        velHawk = new Vector2(Math.round(SPEED * RADIUS * -1 * Math.sin(SPEED * t)), Math.round(SPEED * RADIUS * Math.cos(SPEED * t)));
        hawkBounds = new Rectangle(posHawk.x, posHawk.y + hawkAnimation.getFrame().getHeight() / 3, hawkAnimation.getFrame().getWidth(), hawkAnimation.getFrame().getHeight() + hawkAnimation.getFrame().getHeight() / 3);
        hawkPolygons = new Polygon[11];
        for(int i = 0; i < 11; i++) {
            hawkPolygons[i] = new Polygon(new float[]{0, 0, hawkAnimation.getFrames().get(i).getWidth(), 0, hawkAnimation.getFrames().get(i).getWidth(), hawkAnimation.getFrames().get(i).getHeight(), 0, hawkAnimation.getFrames().get(i).getHeight()});
        }
        hawkPolygon = hawkPolygons[0];
        firstWidth = hawkAnimation.getFrame().getWidth();
    }

    public Rectangle getHawkBounds() {
        return hawkBounds;
    }

    public Polygon getHawkPolygon(){
        return hawkPolygon;
    }

    public Vector2 getPosHawk() {
        return posHawk;
    }

    public Texture getTexture() {
        return hawkAnimation.getFrame();
    }

    public int getFirstWidth() {
        return firstWidth;
    }

    public void dispose() {
        for(Texture frame : hawkAnimation.getFrames()) {
            frame.dispose();
        }
    }

    public void move(boolean spotsQuokka, float dt, Vector3 posQuokka){
        hawkAnimation.update(dt);
        if(!spotsQuokka){
            alreadySpotted = false;
            t += 1;
            velHawk.set(Math.round(RADIUS * -1 * Math.sin(SPEED * t)), Math.round(RADIUS * Math.cos(SPEED * t)));
            velHawk.scl(dt);
            posHawk.add(velHawk);
        }
        else{
            if(!alreadySpotted) {
                velHawk.set(Math.round((posQuokka.x - posHawk.x) * ATTACKSPEED), Math.round((posQuokka.y - posHawk.y) * ATTACKSPEED));
                alreadySpotted = true;
                loopTime = 0;
            }
            if (loopTime >=ATTACKDELAY) {
                velHawk.scl(dt);
                posHawk.set(posHawk.x + velHawk.x, posHawk.y + velHawk.y);
                velHawk.scl(1 / dt);
            }
            else{
                loopTime++;
            }
        }
        hawkBounds.set(posHawk.x, posHawk.y, hawkAnimation.getFrame().getWidth(), hawkAnimation.getFrame().getHeight());
        hawkPolygon = hawkPolygons[hawkAnimation.getFrameNumber()];
        hawkPolygon.setPosition(posHawk.x + (firstWidth - hawkAnimation.getFrame().getWidth()) / 2, posHawk.y);
    }

}
