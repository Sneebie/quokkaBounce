package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Eric on 9/6/2017.
 */

public class LaserGun {
    private static final double ATTACKSPEED = 1.5;

    private Texture gunTexture;
    private LaserBeam myBeam;
    private Rectangle gunBounds;
    private Vector2 posGun, velGun;
    private boolean alreadyShot;

    public LaserGun(float x, float y){
        alreadyShot = false;
        gunTexture = new Texture("laserGun.png");
        myBeam = new LaserBeam(x, y);
        posGun = new Vector2(x, y);
        velGun = new Vector2();
        gunBounds = new Rectangle(posGun.x, posGun.y, gunTexture.getWidth(), gunTexture.getHeight());
    }

    public Rectangle getGunBounds() {
        return gunBounds;
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

    public void shoot(float dt, Vector3 posQuokka){
        if(!alreadyShot) {
            velGun.set(Math.round((posQuokka.x - posGun.x) * ATTACKSPEED), Math.round((posQuokka.y - posGun.y) * ATTACKSPEED));
        }
        myBeam.move(dt, velGun);
    }

    public void resetShot(){
        alreadyShot=false;
        myBeam.getPosBeam().set(posGun);
    }

}
