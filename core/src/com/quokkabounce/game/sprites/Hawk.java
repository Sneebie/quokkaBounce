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
    private static final int HAWKSIGHT = 400;
    private static final int LOCKDISTANCE = 300;
    private static final int SWITCHDISTANCE = 600;
    private static final float SPEED = 2;
    private static final float RADIUS = 200;
    private static final float ATTACKSPEED = 500;
    private static final int ATTACKDELAY = 17;

    private int t, loopTime, firstWidth, headHeight, relativePos;
    private boolean alreadySpotted, loopFirst, curSpotted, switchPast;

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
        loopFirst = false;
        curSpotted = false;
        switchPast = false;
        posHawk = new Vector2(x,y);
        velHawk = new Vector2(0,0);
        hawkBounds = new Rectangle(posHawk.x, posHawk.y + hawkAnimation.getFrame().getHeight() / 3, hawkAnimation.getFrame().getWidth(), hawkAnimation.getFrame().getHeight() + hawkAnimation.getFrame().getHeight() / 3);
        hawkPolygons = new Polygon[11];
        hawkPolygons[0] = hawkPolygons[1] = new Polygon(new float[]{12, 108, 13, 85, 124, 52, 128, 38, 104, 7, 187, 7, 155, 43, 159, 55, 266, 83, 281, 101, 162, 95, 158, 99, 166, 116, 165, 132, 157, 142, 139, 142, 124, 130, 123, 114, 129, 102, 121, 92});
        hawkPolygons[2] = new Polygon(new float[]{14, 115, 14, 94, 124, 52, 128, 38, 104, 7, 187, 7, 155, 43, 159, 55, 267, 101, 279, 119, 162, 95, 158, 99, 166, 116, 165, 132, 157, 142, 139, 142, 124, 130, 123, 114, 129, 102, 121, 92});
        hawkPolygons[3] = new Polygon(new float[]{17, 124, 14, 101, 124, 52, 128, 38, 104, 7, 187, 7, 155, 43, 159, 55, 264, 107, 278, 128, 162, 95, 158, 99, 166, 116, 165, 132, 157, 142, 139, 142, 124, 130, 123, 114, 129, 102, 121, 92});
        hawkPolygons[4] = new Polygon(new float[]{20, 131, 14, 111, 124, 52, 128, 38, 104, 7, 187, 7, 155, 43, 159, 55, 269, 114, 274, 132, 162, 95, 158, 99, 166, 116, 165, 132, 157, 142, 139, 142, 124, 130, 123, 114, 129, 102, 121, 92});
        hawkPolygons[5] = hawkPolygons[6] = new Polygon(new float[]{24, 143, 17, 124, 124, 52, 128, 38, 104, 7, 187, 7, 155, 43, 159, 55, 264, 121, 269, 142, 162, 95, 158, 99, 166, 116, 165, 132, 157, 142, 139, 142, 124, 130, 123, 114, 129, 102, 121, 92});
        hawkPolygons[7] = new Polygon(new float[]{32, 148, 22, 130, 124, 52, 128, 38, 104, 7, 187, 7, 155, 43, 159, 55, 261, 126, 267, 148, 162, 95, 158, 99, 166, 116, 165, 132, 157, 142, 139, 142, 124, 130, 123, 114, 129, 102, 121, 92});
        hawkPolygons[8] = new Polygon(new float[]{35, 154, 24, 135, 124, 52, 128, 38, 104, 7, 187, 7, 155, 43, 159, 55, 255, 132, 261, 153, 162, 95, 158, 99, 166, 116, 165, 132, 157, 142, 139, 142, 124, 130, 123, 114, 129, 102, 121, 92});
        hawkPolygons[9] = hawkPolygons[10] = new Polygon(new float[]{61, 165, 43, 154, 124, 52, 128, 38, 104, 7, 187, 7, 155, 43, 159, 55, 240, 141, 241, 161, 162, 95, 158, 99, 166, 116, 165, 132, 157, 142, 139, 142, 124, 130, 123, 114, 129, 102, 121, 92});
        hawkPolygon = hawkPolygons[0];
        firstWidth = hawkAnimation.getFrame().getWidth();
        headHeight = hawkAnimation.getFrame().getHeight();
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

    public void move(Rectangle quokkaBounds, float dt, Vector3 posQuokka){
        hawkAnimation.update(dt);
        boolean spotsQuokka = Math.sqrt(Math.pow(hawkBounds.x + hawkBounds.width / 2 - quokkaBounds.x - quokkaBounds.width / 2, 2) + Math.pow(hawkBounds.y + hawkBounds.height / 2 - quokkaBounds.y - quokkaBounds.height / 2, 2)) <= HAWKSIGHT;
        if((!spotsQuokka && !curSpotted) || switchPast){
            switchPast = false;
            if(alreadySpotted){
                if(posHawk.y < 325){
                    t = 0;
                }
                else{
                     t =  180;
                }
            }
            alreadySpotted = false;
            loopFirst = false;
            for(int i = 0; i < SPEED; i++) {
                t += 1;
                if (t > 360) {
                    t -= 360;
                }
                velHawk.set(RADIUS * (float) Math.cos(Math.toRadians(t)), RADIUS * (float) Math.sin(Math.toRadians(t)));
                velHawk.scl(dt);
                posHawk.add(velHawk);
            }
        }
        else{
            if(!alreadySpotted) {
                alreadySpotted = true;
                curSpotted = true;
                switchPast = false;
                loopTime = 0;
            }
            if (loopTime >=ATTACKDELAY) {
                if(Math.sqrt(Math.pow(hawkBounds.x + hawkBounds.width / 2 - quokkaBounds.x - quokkaBounds.width / 2, 2) + Math.pow(hawkBounds.y + hawkBounds.height / 2 - quokkaBounds.y - quokkaBounds.height / 2, 2)) > (LOCKDISTANCE + HAWKSIGHT)){
                    curSpotted = false;
                }
                if(!loopFirst){
                    if(posHawk.x > (posQuokka.x + quokkaBounds.getWidth())){
                        relativePos = 1;
                    }
                    else if(posHawk.x < posQuokka.x){
                        relativePos = -1;
                    }
                    velHawk.set(posQuokka.x - posHawk.x, posQuokka.y - posHawk.y);
                    velHawk.scl(ATTACKSPEED/velHawk.len());
                    loopFirst = true;
                }
                else {
                    if((posHawk.x < posQuokka.x && relativePos == 1) || (posHawk.x > (posQuokka.x + quokkaBounds.getWidth()) && relativePos == -1)){
                        if(Math.sqrt(Math.pow(hawkBounds.x + hawkBounds.width / 2 - quokkaBounds.x - quokkaBounds.width / 2, 2) + Math.pow(hawkBounds.y + hawkBounds.height / 2 - quokkaBounds.y - quokkaBounds.height / 2, 2)) > SWITCHDISTANCE){
                            switchPast = true;
                        }
                    }
                    if(posHawk.y < 0){
                        posHawk.y = 0;
                        t = 0;
                        alreadySpotted = false;
                        loopFirst = false;
                    }
                    else if(posHawk.y + headHeight > 750){
                        posHawk.y = 750 - headHeight;
                        t = 180;
                        alreadySpotted = false;
                        loopFirst = false;
                    }
                    else {
                        velHawk.scl(dt);
                        posHawk.set(posHawk.x + velHawk.x, posHawk.y + velHawk.y);
                        velHawk.scl(1 / dt);
                    }
                }
            }
            else{
                loopTime++;
            }
        }
        hawkBounds.set(posHawk.x, posHawk.y, hawkAnimation.getFrame().getWidth(), hawkAnimation.getFrame().getHeight());
        hawkPolygon = hawkPolygons[hawkAnimation.getFrameNumber()];
        hawkPolygon.setPosition(posHawk.x, posHawk.y);
    }

}
