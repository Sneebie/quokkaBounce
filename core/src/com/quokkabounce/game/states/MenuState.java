package com.quokkabounce.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.quokkabounce.game.QuokkaBounce;
import com.quokkabounce.game.sprites.Button;

/**
 * Created by Eric on 8/28/2017.
 */

public class MenuState extends State{
    private Texture levelSelectBackground;
    private Array<Button> buttons;
    private static final double VIEWPORT_SCALER = 1.6;
    private int permaLevel = 1;

    public MenuState(GameStateManager gsm, int level) {
        super(gsm, level);
        if(level > permaLevel){
            permaLevel = level;
        }
        buttons = new Array<Button>();
        cam.setToOrtho(false, Math.round(QuokkaBounce.WIDTH * VIEWPORT_SCALER), Math.round(QuokkaBounce.HEIGHT * VIEWPORT_SCALER));
        buttons.add(new Button(new Texture("level1Button.png"), 50, 100, 1));
        if(permaLevel >= 2){
            buttons.add(new Button(new Texture("level2Button.png"), 200, 400, 2));
        }
        if(permaLevel >= 3){
            buttons.add(new Button(new Texture("level3Button.png"), 400, 100, 3));
        }
        if(permaLevel >= 4){
            buttons.add(new Button(new Texture("level4Button.png"), 600, 450, 4));
        }
        levelSelectBackground = new Texture("levelSelectBackground.png");
    }

    public void handleInput() {
        if(Gdx.input.justTouched()){
            Vector3 touchInput = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            touchInput.set(cam.unproject(touchInput));
            for(Button button : buttons){
                if(button.getButtonBounds().contains(touchInput.x, touchInput.y)){
                    gsm.set(new PlayState(gsm, button.getLevel()));
                    break;
                }
            }
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
    }

}
