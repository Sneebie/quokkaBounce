package com.quokkabounce.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.quokkabounce.game.QuokkaBounce;

public class CompleteState extends State implements InputProcessor {
    private Texture completeBackground;
    private boolean collectedQuokka;
    private int world, level;
    private static final double VIEWPORT_SCALER = 1.6;

    protected CompleteState(GameStateManager gsm, int world, int level, boolean collectedQuokka) {
        super(gsm, world, level);
        Gdx.input.setInputProcessor(this);
        this.collectedQuokka = collectedQuokka;
        this.world = world;
        this.level = level;
        Preferences prefs = Gdx.app.getPreferences("saveData");
        int permaWorld = prefs.getInteger("world", 1);
        if(world > permaWorld){
            prefs.putInteger("world", world);
            prefs.flush();
        }
        if(world > 5){
            world = 5;
        }
        int permaLevel = prefs.getInteger("level" + world, 1);
        if(level > permaLevel){
            prefs.putInteger("level" + world, level);
            prefs.flush();
        }
        for(int i = 0; i < ((permaLevel < 11) ? (permaLevel - 1) : 10); i++) {
            if((i == level - 2) && collectedQuokka){
                prefs.putBoolean("collectedQuokka" + world + i, true);
                prefs.flush();
            }
        }
        cam.setToOrtho(false, Math.round(QuokkaBounce.WIDTH * VIEWPORT_SCALER), Math.round(QuokkaBounce.HEIGHT * VIEWPORT_SCALER));
        /*if(level == 10){
            completeBackground = new Texture("");
        }
        else */if(world == 3){
            completeBackground = new Texture("levelCompletes/1.jpg");
        }
        else if(world == 5 && level == 11){
            completeBackground = new Texture("winBackground.png");
        }
        else{
            completeBackground = new Texture("levelCompletes/" + world + ".jpg");
        }
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(completeBackground, cam.position.x - cam.viewportWidth / 2, 0, cam.viewportWidth, cam.viewportHeight);
        sb.end();
    }

    @Override
    public void dispose() {
        completeBackground.dispose();
    }

    @Override
    public void pause() {

    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (level == 11) {
            if(world == 5){
                gsm.set(new IntroState(gsm, 1, 1));
            }
            else {
                gsm.set(new MenuState(gsm, world, level + 1, collectedQuokka));
                gsm.set(new MenuState(gsm, world + 1, 1, collectedQuokka));
            }
        }
        else {
            gsm.set(new MenuState(gsm, world, level + 1, collectedQuokka));
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
