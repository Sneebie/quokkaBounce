package com.quokkabounce.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BooleanArray;
import com.quokkabounce.game.QuokkaBounce;
import com.quokkabounce.game.sprites.Button;

/**
 * Created by Eric on 8/28/2017.
 */

public class MenuState extends State implements InputProcessor{
    private Texture levelSelectBackground;
    private Array<Button> buttons;
    private Array<Texture> numbers;
    private Button backButton;
    private static final double VIEWPORT_SCALER = 1.6;
    private int permaLevel, currentWorld, permaWorld;
    private boolean[] collectedQuokkas;

    public MenuState(GameStateManager gsm, int world, int level, boolean collectedQuokka) {
        super(gsm, world, level);
        collectedQuokkas = new boolean[11];
        Gdx.input.setInputProcessor(this);
        Preferences prefs = Gdx.app.getPreferences("saveData");
        permaWorld = prefs.getInteger("world", 1);
        if(world > permaWorld){
            prefs.putInteger("world", world);
            prefs.flush();
            permaWorld = world;
        }
        if(world > 6){
            world = 6;
        }
        permaLevel = prefs.getInteger("level" + world, 1);
        currentWorld = world;
        if(level > permaLevel){
            prefs.putInteger("level" + world, level);
            prefs.flush();
            permaLevel = level;
        }
        boolean currentQuokka;
        for(int i = 0; i < level; i++) {
            currentQuokka = prefs.getBoolean("collectedQuokka" + world + i, false);
            if((i == level - 2) && collectedQuokka){
                collectedQuokkas[i] = true;
                prefs.putBoolean("collectedQuokka" + world + i, true);
                prefs.flush();
            }
            else{
                collectedQuokkas[i] = currentQuokka;
            }
        }
        backButton = new Button(new Texture("back.png"), 15, 703, 0);
        buttons = new Array<Button>();
        numbers = new Array<Texture>();
        cam.setToOrtho(false, Math.round(QuokkaBounce.WIDTH * VIEWPORT_SCALER), Math.round(QuokkaBounce.HEIGHT * VIEWPORT_SCALER));
        for(int i = 1; i<=10; i++){
            numbers.add(new Texture("numbers/number" + i + ".png"));
        }
        buttons.add(new Button(new Texture("levelButton" + world + ".png"), 100, 50, 1));
        if(permaLevel >= 2){
            buttons.add(new Button(new Texture("levelButton" + world + ".png"), 100, 300, 2));
            if(permaLevel >= 3){
                buttons.add(new Button(new Texture("levelButton" + world + ".png"), 100, 550, 3));
                if(permaLevel >= 4){
                    buttons.add(new Button(new Texture("levelButton" + world + ".png"), 400, 50, 4));
                    if(permaLevel >= 5){
                        buttons.add(new Button(new Texture("levelButton" + world + ".png"), 400, 300, 5));
                        if(permaLevel >= 6){
                            buttons.add(new Button(new Texture("levelButton" + world + ".png"), 400, 550, 6));
                            if(permaLevel >= 7){
                                buttons.add(new Button(new Texture("levelButton" + world + ".png"), 700, 50, 7));
                                if(permaLevel >= 8){
                                    buttons.add(new Button(new Texture("levelButton" + world + ".png"), 700, 300, 8));
                                    if(permaLevel >= 9){
                                        buttons.add(new Button(new Texture("levelButton" + world + ".png"), 700, 550, 9));
                                        if(permaLevel >= 10){
                                            buttons.add(new Button(new Texture("levelButton" + world + ".png"), 1000, 50, 10));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        levelSelectBackground = new Texture("levelSelectBackground" + world + ".png");
    }

    @Override
    public void update(float dt) {
    }

    @Override
    public void pause() {

    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(levelSelectBackground, cam.position.x - cam.viewportWidth / 2, 0, cam.viewportWidth, cam.viewportHeight);
        sb.draw(backButton.getTexture(), backButton.getPosButton().x, backButton.getPosButton().y);
        for (int i = 0; i < (permaLevel > 10 ? permaLevel - 1 : permaLevel); i++){
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
