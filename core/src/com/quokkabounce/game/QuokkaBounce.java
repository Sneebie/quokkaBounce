package com.quokkabounce.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;
import com.quokkabounce.game.states.GameStateManager;
import com.quokkabounce.game.states.WorldState;

public class QuokkaBounce extends ApplicationAdapter {
	public static final int WIDTH = 800;
	public static final int HEIGHT = 480;
	private static final float TIMEDELTA = 1/60f;
	public static final String TITLE = "Quokka Bounce";
	private GameStateManager gsm;
	private SpriteBatch batch;
	private double currentTime, accumulator = 0;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		gsm = new GameStateManager();
		Gdx.gl.glClearColor(1, 0, 0, 1);
		gsm.push(new WorldState(gsm, 3, 1));
		currentTime = TimeUtils.millis();
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		double newTime = TimeUtils.millis();
		double frameTime = Math.min((newTime - currentTime)/1000, 0.25);
		currentTime = newTime;
		accumulator += frameTime;
		while(accumulator >= TIMEDELTA) {
			gsm.update(TIMEDELTA);
			accumulator -= TIMEDELTA;
		}
		gsm.interpolate(accumulator / TIMEDELTA);
		gsm.render(batch);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}

	@Override
	public void pause() {
		super.pause();
		gsm.peek().pause();
	}
}
