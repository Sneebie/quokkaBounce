package com.quokkabounce.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.quokkabounce.game.QuokkaBounce;

/**
 * Created by Eric on 8/28/2017.
 */

public class MenuState extends State{
    private Texture levelSelectBackground;
    private Texture levelButton1;
    private static final double VIEWPORT_SCALER = 1.6;

    public MenuState(GameStateManager gsm) {
        super(gsm);
        levelSelectBackground = new Texture("levelSelectBackground.png");
        levelButton1 = new Texture("levelButton1.png");
        cam.setToOrtho(false, Math.round(QuokkaBounce.WIDTH * VIEWPORT_SCALER), Math.round(QuokkaBounce.HEIGHT * VIEWPORT_SCALER));
    }

    public void handleInput() {
        if(Gdx.input.justTouched()){
            gsm.set(new PlayState(gsm));
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(levelSelectBackground, cam.position.x - cam.viewportWidth / 2, 0, cam.viewportWidth, cam.viewportHeight);
        sb.draw(levelButton1, cam.viewportWidth / 2 - levelButton1.getWidth() / 2 , cam.viewportHeight / 2 - levelButton1.getHeight() / 2);
        sb.end();
    }

    @Override
    public void dispose() {
        levelSelectBackground.dispose();
        levelButton1.dispose();
    }

}
