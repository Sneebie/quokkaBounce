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
 * Created by Eric on 3/18/2018.
 */

public class WorldState extends State implements InputProcessor {
    private Texture levelSelectBackground;
    private Array<Button> buttons;
    private Array<Texture> numbers;
    private static final double VIEWPORT_SCALER = 1.6;
    private int permaWorld;

    public WorldState(GameStateManager gsm, int world, int level) {
        super(gsm, world, level);
        Gdx.input.setInputProcessor(this);
        Preferences prefs = Gdx.app.getPreferences("saveData");
        permaWorld = prefs.getInteger("world", 1);
        if(world > permaWorld){
            prefs.putInteger("world", world);
            prefs.flush();
            permaWorld = world;
        }
        if(permaWorld > 6){
            permaWorld = 6;
        }
        buttons = new Array<Button>();
        numbers = new Array<Texture>();
        cam.setToOrtho(false, Math.round(QuokkaBounce.WIDTH * VIEWPORT_SCALER), Math.round(QuokkaBounce.HEIGHT * VIEWPORT_SCALER));
        for(int i = 1; i<=10; i++){
            if(i == 1){
                numbers.add(new Texture("numbers/number" + i + "Brown.png"));
            }
            else if(i == 6){
                numbers.add(new Texture("numbers/number" + i + "White.png"));
            }
            else if(i==4){
                numbers.add(new Texture("numbers/number" + i + "Small.png"));
            }
            else {
                numbers.add(new Texture("numbers/number" + i + ".png"));
            }
        }
        buttons.add(new Button(new Texture("levelButton1.png"), 200, 50, 1));
        if(permaWorld >= 2){
            buttons.add(new Button(new Texture("levelButton2.png"), 200, 300, 2));
            if(permaWorld >= 3){
                buttons.add(new Button(new Texture("levelButton3.png"), 200, 550, 3));
                if(permaWorld >= 4){
                    buttons.add(new Button(new Texture("levelButton4.png"), 800, 50, 4));
                    if(permaWorld >= 5){
                        buttons.add(new Button(new Texture("levelButton5.png"), 800, 300, 5));
                        if(permaWorld >= 6){
                            buttons.add(new Button(new Texture("levelButton6.png"), 800, 550, 6));
                        }
                    }
                }
            }
        }
        levelSelectBackground = new Texture("levelSelectBackground.png");
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
        for(Button menuButton : buttons){
            if(menuButton.getButtonBounds().contains(touchInput.x, touchInput.y)){
                gsm.set(new MenuState(gsm, menuButton.getLevel(), 1, false));
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

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(levelSelectBackground, cam.position.x - cam.viewportWidth / 2, 0, cam.viewportWidth, cam.viewportHeight);
        for (int i = 0; i < buttons.size; i++){
            Button button = buttons.get(i);
            sb.draw(button.getTexture(), button.getPosButton().x, button.getPosButton().y);
            sb.draw(numbers.get(i), button.getPosButton().x + button.getTexture().getWidth() / 2 - numbers.get(i).getWidth() / 2, button.getPosButton().y + button.getTexture().getHeight() / 2 - numbers.get(i).getHeight() / 2);
        }
        sb.end();
    }

    @Override
    public void dispose() {
        levelSelectBackground.dispose();
        for(Button button : buttons) {
            button.dispose();
        }
    }

    @Override
    public void pause() {

    }
}
