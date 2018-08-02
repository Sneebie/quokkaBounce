package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class LaserBeam {
    private Texture beamTexture;
    private TextureRegion beamRegion;

    private Rectangle beamBounds;
    private Polygon polygon;
    private Vector2 posBeam;

    public LaserBeam(float x, float y){
        beamTexture = new Texture("laserBeam.png");
        beamRegion = new TextureRegion(beamTexture);

        posBeam = new Vector2(x, y);

        beamBounds = new Rectangle(posBeam.x, posBeam.y, beamTexture.getWidth(), beamTexture.getHeight());
        polygon = new Polygon(new float[]{0,0,beamBounds.width,0,beamBounds.width,beamBounds.height,0,beamBounds.height});
        polygon.setOrigin(beamBounds.width/2, beamBounds.height/2);
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygonAngle(float angle){
        polygon.setRotation(angle);
    }

    public Rectangle getBeamBounds() {
        return beamBounds;
    }

    public TextureRegion getBeamRegion() {
        return beamRegion;
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
        polygon.setPosition(posBeam.x, posBeam.y);
        velBeam.scl(1/dt);
    }
}