package com.quokkabounce.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.quokkabounce.game.QuokkaBounce;
import com.quokkabounce.game.sprites.Button;

/**
 * Created by Eric on 8/28/2017.
 */

public class MenuState extends State implements InputProcessor{
    private Texture levelSelectBackground;
    private Array<Button> buttons;
    private Button backButton;
    private static final double VIEWPORT_SCALER = 1.6;
    private int permaLevel, currentWorld;

    public MenuState(GameStateManager gsm, int world, int level) {
        super(gsm, world, level);
        Gdx.input.setInputProcessor(this);
        Preferences prefs = Gdx.app.getPreferences("saveData");
        permaLevel = prefs.getInteger("level" + world, 1);
        currentWorld = world;
        if(level > permaLevel){
            prefs.putInteger("level" + world, level);
            prefs.flush();
            permaLevel = level;
        }
        backButton = new Button(new Texture("level4Button.png"), -175, 500, 0);
        buttons = new Array<Button>();
        cam.setToOrtho(false, Math.round(QuokkaBounce.WIDTH * VIEWPORT_SCALER), Math.round(QuokkaBounce.HEIGHT * VIEWPORT_SCALER));
        buttons.add(new Button(new Texture("level1Button.png"), 0, 100, 1));
        if(permaLevel >= 2){
            buttons.add(new Button(new Texture("level2Button.png"), 0, 300, 2));
            if(permaLevel >= 3){
                buttons.add(new Button(new Texture("level3Button.png"), 0, 500, 3));
                if(permaLevel >= 4){
                    buttons.add(new Button(new Texture("level4Button.png"), 300, 0, 4));
                    if(permaLevel >= 5){
                        buttons.add(new Button(new Texture("level4Button.png"), 300, 300, 5));
                        if(permaLevel >= 6){
                            buttons.add(new Button(new Texture("level4Button.png"), 300, 600, 6));
                            if(permaLevel >= 7){
                                buttons.add(new Button(new Texture("level4Button.png"), 600, 0, 7));
                                if(permaLevel >= 8){
                                    buttons.add(new Button(new Texture("level4Button.png"), 600, 300, 8));
                                    if(permaLevel >= 9){
                                        buttons.add(new Button(new Texture("level4Button.png"), 600, 600, 9));
                                        if(permaLevel >= 9){
                                            buttons.add(new Button(new Texture("level4Button.png"), 900, 0, 10));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        levelSelectBackground = new Texture("levelSelectBackground.png");
    }

    @Override
    public void update(float dt) {
    }

    @Override
    public void pause() {

    }

    @Override
    public void interpolate(double alpha) {

    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(levelSelectBackground, cam.position.x - cam.viewportWidth / 2, 0, cam.viewportWidth, cam.viewportHeight);
        sb.draw(backButton.getTexture(), backButton.getPosButton().x, backButton.getPosButton().y);
        for (Button button : buttons){
            sb.draw(button.getTexture(), button.getPosButton().x, button.getPosButton().y);
        }
        sb.end();
    }

    @Override
    public void dispose() {
        levelSelectBackground.dispose();
        for(Button button : buttons) {
            button.dispose();
        }
        backButton.dispose();
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
        Vector3 touchInput = new Vector3(screenX, screenY, 0);
        touchInput.set(cam.unproject(touchInput));
        if(backButton.getButtonBounds().contains(touchInput.x, touchInput.y)){
            gsm.set(new WorldState(gsm, world, level));
        }
        for(Button menuButton : buttons){
            if(menuButton.getButtonBounds().contains(touchInput.x, touchInput.y)){
                gsm.set(new PlayState(gsm, currentWorld, menuButton.getLevel()));
                break;
            }
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
