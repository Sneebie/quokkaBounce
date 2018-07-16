package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class JumpFish {
    private static float GRAVITY = 13;
    private static float VISION = 200;
    private Rectangle jumpFishBounds;
    private Texture jumpFishTexture;
    private Vector2 posJumpFish, velJumpFish, jumpVel, firstVel;
    private float jumpedDistance, jumpDistance;
    private boolean startFall, isJumping, hasJumped;

    public JumpFish(float x, float y, float firstVelX, float firstVelY, float jumpHeight, float jumpDistance){
        posJumpFish = new Vector2(x, y);
        velJumpFish = new Vector2(firstVelX, firstVelY);
        firstVel.set(velJumpFish);
        double velAngle = Math.atan(4.0 * jumpHeight / jumpDistance);
        double velMagnitude = Math.sqrt(GRAVITY * jumpDistance / Math.sin(2*velAngle));
        jumpVel = new Vector2((float) ((int) Math.signum(firstVelX) * velMagnitude * Math.cos(velAngle)), (float) (velMagnitude * Math.sin(velAngle)));
        hasJumped = false;
        jumpedDistance = 0;
        this.jumpDistance = jumpDistance;
        jumpFishTexture = new Texture("jumpFish.png");
        jumpFishBounds = new Rectangle(posJumpFish.x, posJumpFish.y, jumpFishTexture.getWidth(), jumpFishTexture.getHeight());
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
            jumpedDistance += velJumpFish.x;
            velJumpFish.y -= GRAVITY;
            velJumpFish.scl(1 / dt);
            if(jumpedDistance >= jumpDistance){
                isJumping = false;
                velJumpFish.set(firstVel);
            }
        }
        else{
            velJumpFish.scl(dt);
            posJumpFish.add(velJumpFish);
            velJumpFish.scl(1 / dt);
            if(Math.abs(quokkaPos.x - posJumpFish.x) < VISION){
                jump();
            }
        }
    }
}