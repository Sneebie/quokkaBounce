package com.quokkabounce.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
/**
 * Created by Eric on 8/28/2017.
 */

public class MenuState extends State{

    private Texture levelSelectBackground;
    private Texture levelButton1;
    public MenuState(GameStateManager gsm) {
        super(gsm);
        levelSelectBackground = new Texture("levelSelectBackground.png");
        levelButton1 = new Texture("levelButton1.png");
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
        sb.begin();
        sb.draw(levelSelectBackground, 0, 0, Gdx.graphics.getWidth() , Gdx.graphics.getHeight());
        sb.draw(levelButton1, Math.round(Gdx.graphics.getWidth() / 2) - (levelButton1.getWidth() / 2), Math.round(Gdx.graphics.getHeight() / 2) - (levelButton1.getHeight() / 2));
        sb.end();
    }

    @Override
    public void dispose() {
        levelSelectBackground.dispose();
        levelButton1.dispose();
    }
}
