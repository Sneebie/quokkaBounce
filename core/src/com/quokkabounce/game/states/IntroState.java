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

public class IntroState extends State implements InputProcessor{
    private Texture levelSelectBackground, A, B, C, E, K, N, O, Q, R, S, T, U, quokkbig, evilCloud, hawk, quokkaPrisoner;
    private Array<Button> buttons;
    private Button backButton;
    private static final double VIEWPORT_SCALER = 1.6;

    public IntroState(GameStateManager gsm, int world, int level) {
        super(gsm, world, level);
        Gdx.input.setInputProcessor(this);
        backButton = new Button(new Texture("back.png"), 15, 703, 0);
        buttons = new Array<Button>();
        cam.setToOrtho(false, Math.round(QuokkaBounce.WIDTH * VIEWPORT_SCALER), Math.round(QuokkaBounce.HEIGHT * VIEWPORT_SCALER));
        buttons.add(new Button(new Texture("PlayButton.png"), 555, 350, 1));
        levelSelectBackground = new Texture("level1Background.png");
        A = new Texture("letters/A.png");
        B = new Texture("letters/B.png");
        C = new Texture("letters/C.png");
        E = new Texture("letters/E.png");
        K = new Texture("letters/K.png");
        N = new Texture("letters/N.png");
        O = new Texture("letters/O.png");
        Q = new Texture("letters/Q.png");
        R = new Texture("letters/R.png");
        S = new Texture("letters/S.png");
        T = new Texture("letters/T.png");
        U = new Texture("letters/U.png");
        hawk = new Texture("hawkIdle/hawk5.png");
        quokkbig = new Texture("quokkbig.png");
        evilCloud = new Texture("evilCloud.png");
        quokkaPrisoner = new Texture("quokkaPrisoner.png");
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
        //sb.draw(backButton.getTexture(), backButton.getPosButton().x, backButton.getPosButton().y);
        for (Button button : buttons){
            sb.draw(button.getTexture(), button.getPosButton().x, button.getPosButton().y);
        }
        sb.draw(Q, 25, 460);
        sb.draw(U, 150, 548);
        sb.draw(O, 240, 583);
        sb.draw(K, 310, 583);
        sb.draw(K, 400, 595);
        sb.draw(A, 490, 600);
        sb.draw(B, 670, 600);
        sb.draw(O, 760, 605);
        sb.draw(U, 830, 590);
        sb.draw(N, 940, 550);
        sb.draw(C, 1040, 523);
        sb.draw(E, 1120, 465);
        sb.draw(quokkbig, 100, 50);
        sb.draw(hawk, 350, 70);
        sb.draw(evilCloud, 670, 160);
        sb.draw(quokkaPrisoner, 1000, 60);
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
        for(Button menuButton : buttons){
            if(menuButton.getButtonBounds().contains(touchInput.x, touchInput.y)){
                gsm.set(new WorldState(gsm, 1, 1));
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
