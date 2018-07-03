package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class LaserBeam {
    private Texture beamTexture;

    private Rectangle beamBounds;
    private Vector2 posBeam;

    public LaserBeam(float x, float y){
        beamTexture = new Texture("laserBeam.png");

        posBeam = new Vector2(x, y);

        beamBounds = new Rectangle(posBeam.x, posBeam.y, beamTexture.getWidth(), beamTexture.getHeight());
    }

    public Rectangle getBeamBounds() {
        return beamBounds;
    }

    public Vector2 getPosBeam() {
        return posBeam;
    }

    public Texture getTexture() {
        return beamTexture;
    }

    public boolean collides(Rectangle player) {
        return player.overlaps(beamBounds);
    }

    public void dispose() {
        beamTexture.dispose();
    }

    public void move(float dt, Vector2 velBeam){
        velBeam.scl(dt);
        posBeam.add(velBeam);
        beamBounds.setPosition(posBeam.x, posBeam.y);
    }
}