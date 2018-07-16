package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Airplane {
    private Rectangle airplaneBounds;
    private Texture airplaneTexture;
    private Vector2 posAirplane, velAirplane;
    private boolean startFall;

    public Airplane(float x, float y, float firstVelX, float firstVelY){
        posAirplane = new Vector2(x, y);
        velAirplane = new Vector2(firstVelX, firstVelY);
        airplaneTexture = new Texture("airplane.png");
        airplaneBounds = new Rectangle(posAirplane.x, posAirplane.y, airplaneTexture.getWidth(), airplaneTexture.getHeight());
    }

    public Vector2 getPosAirplane() {
        return posAirplane;
    }

    public Texture getTexture() {
        return airplaneTexture;
    }

    public boolean isStartFall() {
        return startFall;
    }

    public void setStartFall(boolean startFall) {
        this.startFall = startFall;
    }

    public boolean collides(Rectangle player) {
        return player.overlaps(airplaneBounds);
    }

    public void dispose() {
        airplaneTexture.dispose();
    }

    public void move(float dt){
        velAirplane.scl(dt);
        posAirplane.add(velAirplane);
        velAirplane.scl(1/dt);
    }
}