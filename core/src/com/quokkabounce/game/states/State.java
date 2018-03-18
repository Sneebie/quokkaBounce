package com.quokkabounce.game.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Eric on 8/28/2017.
 */

public abstract class State {
    protected OrthographicCamera cam;
    protected Vector3 mouse;
    protected GameStateManager gsm;
    protected int world, level;

    protected State(GameStateManager gsm, int world, int level) {
        this.level = level;
        this.world = world;
        this.gsm = gsm;
        cam = new OrthographicCamera();
        mouse = new Vector3();
    }

    public abstract void update(float dt);
    public abstract void render(SpriteBatch sb);
    public abstract void dispose();
    public abstract void pause();
}
