package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class JumpFish {
    private static float GRAVITY = 13;
    private static float VISION = 200;
    private Rectangle jumpFishBounds;
    private Texture jumpFishTexture;
    private TextureRegion jumpFishRegion;
    private Polygon polygon;
    private Vector2 posJumpFish, velJumpFish, jumpVel, firstVel;
    private float jumpedDistance, jumpDistance, jumpFishAngle;
    private boolean startFall, isJumping, hasJumped;

    public JumpFish(float x, float y, float firstVelX, float firstVelY, float jumpHeight, float jumpDistance){
        posJumpFish = new Vector2(x, y);
        velJumpFish = new Vector2(firstVelX, firstVelY);
        firstVel = new Vector2(velJumpFish);
        double velAngle = Math.atan(4.0 * jumpHeight / jumpDistance);
        double velMagnitude = Math.sqrt(GRAVITY * jumpDistance / Math.sin(2*velAngle));
        jumpVel = new Vector2((float) ((int) Math.signum(firstVelX) * velMagnitude * Math.cos(velAngle)), (float) (velMagnitude * Math.sin(velAngle)));
        hasJumped = false;
        jumpedDistance = 0;
        this.jumpDistance = jumpDistance;
        jumpFishTexture = new Texture("jumpFish.png");
        jumpFishRegion = new TextureRegion(jumpFishTexture);
        jumpFishBounds = new Rectangle(posJumpFish.x, posJumpFish.y, jumpFishTexture.getWidth(), jumpFishTexture.getHeight());
        polygon = new Polygon(new float[]{0,0,jumpFishBounds.width,0,jumpFishBounds.width,jumpFishBounds.height,0,jumpFishBounds.height});
        polygon.setOrigin(jumpFishBounds.width/2, jumpFishBounds.height/2);
        jumpFishAngle = (float) Math.atan(velJumpFish.y / velJumpFish.x);
    }

    public Vector2 getPosJumpFish() {
        return posJumpFish;
    }

    public Texture getTexture() {
        return jumpFishTexture;
    }

    public boolean isStartFall() {
        return startFall;
    }

    public TextureRegion getJumpFishRegion() {
        return jumpFishRegion;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public float getJumpFishAngle() {
        return jumpFishAngle;
    }

    public void setStartFall(boolean startFall) {
        this.startFall = startFall;
    }

    public boolean collides(Rectangle player) {
        return player.overlaps(jumpFishBounds);
    }

    public void dispose() {
        jumpFishTexture.dispose();
    }

    public void jump(){
        jumpedDistance = 0;
        velJumpFish.set(jumpVel);
        isJumping = true;
        hasJumped = true;
    }

    public void move(float dt, Vector3 quokkaPos){
        if(isJumping) {
            velJumpFish.scl(dt);
            posJumpFish.add(velJumpFish);
            polygon.setPosition(posJumpFish.x, posJumpFish.y);
            jumpedDistance += velJumpFish.x;
            velJumpFish.y -= GRAVITY;
            velJumpFish.scl(1 / dt);
            jumpFishAngle = (float) Math.atan(velJumpFish.y / velJumpFish.x);
            if(jumpedDistance >= jumpDistance){
                isJumping = false;
                velJumpFish.set(firstVel);
                jumpFishAngle = (float) Math.atan(velJumpFish.y / velJumpFish.x);
            }
        }
        else{
            velJumpFish.scl(dt);
            posJumpFish.add(velJumpFish);
            polygon.setPosition(posJumpFish.x, posJumpFish.y);
            velJumpFish.scl(1 / dt);
            if(!hasJumped) {
                if (Math.abs(quokkaPos.x - posJumpFish.x) < VISION) {
                    jump();
                }
            }
        }
    }
}