package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Eric on 9/6/2017.
 */

public class LaserGun {
    private static final float ATTACKSPEED = 400f;
    private static final float ANGLEADJUSTER = 0;
    private static final float CENTERX = 0;
    private static final float CENTERY = 30;
    private static final float YREMOVAL = 30;
    private static final float XREMOVAL = 30;

    private Texture gunTexture;
    private TextureRegion gunRegion;
    private LaserBeam myBeam;
    private Rectangle gunBounds;
    private Vector2 posGun, velGun, centerPoint, tempPos, tempBeam;
    private boolean alreadyShot, drawLaser;
    private float beamAngle, gunAngle, angleAdjuster;

    public LaserGun(float x, float y){
        alreadyShot = false;
        gunTexture = new Texture("laserGun.png");
        gunRegion = new TextureRegion(gunTexture);
        posGun = new Vector2(x, y);
        velGun = new Vector2();
        tempPos = new Vector2();
        tempBeam = new Vector2();
        gunBounds = new Rectangle(posGun.x, posGun.y, gunTexture.getWidth(), gunTexture.getHeight());
        centerPoint = new Vector2(posGun.x + gunBounds.getWidth() / 2 + CENTERX, posGun.y + gunBounds.getWidth() / 2 + CENTERY);
        if(50 < posGun.x + gunBounds.getWidth() / 2) {
            gunAngle = (float) ((Math.atan((650- posGun.y - gunBounds.getWidth() / 2) / (50 - posGun.x - gunBounds.getWidth() / 2)) * 180 / Math.PI) + 90);
        }
        else{
            gunAngle = (float) ((Math.atan((650 - posGun.y - gunBounds.getWidth() / 2) / (50 - posGun.x - gunBounds.getWidth() / 2)) * 180 / Math.PI) - 90);
        }
        myBeam = new LaserBeam(centerPoint.x, posGun.y + gunBounds.getHeight());
        gunAngle *= Math.PI / 180;
        tempPos.x = (float) (Math.cos(gunAngle + ANGLEADJUSTER) * (myBeam.getPosBeam().x - centerPoint.x) - Math.sin(gunAngle + ANGLEADJUSTER) * (myBeam.getPosBeam().y - centerPoint.y) + centerPoint.x) - XREMOVAL;
        tempPos.y = (float) (Math.sin(gunAngle + ANGLEADJUSTER) * (myBeam.getPosBeam().x - centerPoint.x) + Math.cos(gunAngle + ANGLEADJUSTER) * (myBeam.getPosBeam().y - centerPoint.y) + centerPoint.y) - YREMOVAL;
        gunAngle *= 180/ Math.PI;
        myBeam.getPosBeam().set(tempPos);
    }

    public LaserBeam getMyBeam() {
        return myBeam;
    }

    public Vector2 getPosGun() {
        return posGun;
    }

    public Texture getTexture() {
        return gunTexture;
    }

    public boolean collides(Rectangle player) {
        return player.overlaps(gunBounds);
    }

    public void dispose() {
        gunTexture.dispose();
        myBeam.dispose();
    }

    public float getBeamAngle() {
        return beamAngle;
    }

    public void shoot(float dt, Vector3 posQuokka){
        if(posQuokka.x < posGun.x + gunBounds.getWidth() / 2) {
            angleAdjuster = 90;
        }
        else{
            angleAdjuster = -90;
        }
        gunAngle = (float) ((Math.atan((posQuokka.y - posGun.y - gunBounds.getWidth() / 2) / (posQuokka.x - posGun.x - gunBounds.getWidth() / 2)) * 180 / Math.PI) + angleAdjuster);
        if(!alreadyShot) {
            velGun.set(Math.round((posQuokka.x - myBeam.getPosBeam().x)), Math.round((posQuokka.y - myBeam.getPosBeam().y))).scl(1/velGun.len());
            velGun.scl(ATTACKSPEED);
            beamAngle = (float) (Math.atan((posQuokka.y - myBeam.getPosBeam().y) / (posQuokka.x - myBeam.getPosBeam().x)) * 180 / Math.PI);
            myBeam.setPolygonAngle(beamAngle);
            alreadyShot = true;
            drawLaser = true;
        }
        myBeam.move(dt, velGun);
    }

    public boolean isDrawLaser() {
        return drawLaser;
    }

    public TextureRegion getGunRegion() {
        return gunRegion;
    }

    public float getGunAngle() {
        return gunAngle;
    }

    public void resetShot(){
        drawLaser = false;
        tempBeam.set(centerPoint.x, centerPoint.y+ gunBounds.getHeight() / 2);
        gunAngle *= Math.PI / 180;
        tempPos.x = (float) (Math.cos(gunAngle + ANGLEADJUSTER) * (tempBeam.x - centerPoint.x) - Math.sin(gunAngle + ANGLEADJUSTER) * (tempBeam.y - centerPoint.y) + centerPoint.x) - XREMOVAL;
        tempPos.y = (float) (Math.sin(gunAngle + ANGLEADJUSTER) * (tempBeam.x - centerPoint.x) + Math.cos(gunAngle + ANGLEADJUSTER) * (tempBeam.y - centerPoint.y) + centerPoint.y) - YREMOVAL;
        gunAngle *= 180/ Math.PI;
        myBeam.getPosBeam().set(tempPos);
        alreadyShot=false;
    }

}
