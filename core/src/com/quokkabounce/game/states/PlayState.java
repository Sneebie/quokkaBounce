package com.quokkabounce.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.quokkabounce.game.QuokkaBounce;
import com.quokkabounce.game.sprites.EvilCloud;
import com.quokkabounce.game.sprites.HappyCloud;
import com.quokkabounce.game.sprites.Quokka;

import java.awt.Menu;
import java.util.Random;

/**
 * Created by Eric on 8/29/2017.
 */

public class PlayState extends State implements InputProcessor{
    private static final int BACKGROUND_Y_OFFSET = 0;
    private static final double VIEWPORT_SCALER = 1.6;

    private Quokka quokka;
    private Texture level1Background;
    private Vector2 level1BackgroundPos1, level1BackgroundPos2;
    private Vector3 clickPos, clickPos2, velocityTemp, normal, clickPosTemp;
    private ShapeRenderer shapeRenderer;
    private HappyCloud happyCloud;

    private Array<EvilCloud> clouds;

    public PlayState(GameStateManager gsm) {
        super(gsm);
        Gdx.input.setInputProcessor(this);
        quokka = new Quokka(50,650);
        level1Background = new Texture("level1Background.png");
        cam.setToOrtho(false, Math.round(QuokkaBounce.WIDTH*VIEWPORT_SCALER), Math.round(QuokkaBounce.HEIGHT*VIEWPORT_SCALER));
        shapeRenderer = new ShapeRenderer();
        level1BackgroundPos1= new Vector2(cam.position.x - cam.viewportWidth, BACKGROUND_Y_OFFSET);
        level1BackgroundPos2 = new Vector2((cam.position.x - cam.viewportWidth)+level1Background.getWidth(), BACKGROUND_Y_OFFSET);
        clouds = new Array<EvilCloud>();
        clickPos = new Vector3(0,0,0);
        clickPos2 = new Vector3(0,-100,0);
        clickPosTemp = new Vector3(0,-100,0);
        velocityTemp = new Vector3(0,0,0);
        normal = new Vector3(0,0,0);
        clouds.add(new EvilCloud(400,400));
        happyCloud = new HappyCloud(1000, 200);
    }

    @Override
    public void update(float dt) {
        updateBackground();
        cam.position.x = quokka.getPosition().x + 80;
        if (((quokka.getPosition().x > clickPos.x) && (quokka.getPosition().x < clickPos2.x)) || ((quokka.getPosition().x < clickPos.x) && (quokka.getPosition().x > clickPos2.x))) {
            if (quokka.getQuokkaBounds().contains(quokka.getPosition().x, lineY(quokka.getPosition().x))) {
                quokka.setVelocity(resultVector(quokka.getVelocity(), clickPos, clickPos2));
            }
        } else if ((((quokka.getPosition().x + quokka.getTexture().getWidth()) > clickPos.x) && ((quokka.getPosition().x + quokka.getTexture().getWidth()) < clickPos2.x)) || (((quokka.getPosition().x + quokka.getTexture().getWidth()) < clickPos.x) && ((quokka.getPosition().x + quokka.getTexture().getWidth()) > clickPos2.x))) {
            if (quokka.getQuokkaBounds().contains(quokka.getPosition().x + quokka.getTexture().getWidth(), lineY(quokka.getPosition().x + quokka.getTexture().getWidth()))) {
                quokka.setVelocity(resultVector(quokka.getVelocity(), clickPos, clickPos2));
            }
        }
        quokka.update(dt);
        for (EvilCloud cloud : clouds){
            if(cloud.collides(quokka.getQuokkaBounds())){
                gsm.set(new PlayState(gsm));
                break;
            }
            if(quokka.getPosition().y==0){
                gsm.set(new PlayState(gsm));
                break;
            }
        }
        if(happyCloud.collides(quokka.getQuokkaBounds())){
            gsm.set(new MenuState(gsm));
        }
        cam.update();

    }

    private float lineY(float x){
        if(clickPos2.x > clickPos.x) {
            final float slope = (clickPos2.y - clickPos.y) / (clickPos2.x - clickPos.x);
            return (slope * (x - clickPos.x) + clickPos.y);
        }
        else{
            final float slope = (clickPos.y - clickPos2.y) / (clickPos.x - clickPos2.x);
            return (slope * (x - clickPos2.x) + clickPos2.y);
        }
    }
    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(level1Background, level1BackgroundPos1.x, level1BackgroundPos1.y);
        sb.draw(level1Background, level1BackgroundPos2.x, level1BackgroundPos2.y);
        sb.draw(quokka.getTexture(), quokka.getPosition().x, quokka.getPosition().y);
        for(EvilCloud cloud: clouds) {
            sb.draw(cloud.getTexture(), cloud.getPosCloud().x, cloud.getPosCloud().y);
        }
        sb.draw(happyCloud.getTexture(), happyCloud.getPosCloud().x, happyCloud.getPosCloud().y);
        sb.end();
        if(clickPos2.y!=-100) {
            shapeRenderer.setColor(Color.BROWN);
            shapeRenderer.setProjectionMatrix(cam.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.line(clickPos, clickPos2);
            shapeRenderer.end();
        }
        else if (clickPosTemp.y!=-100){
            shapeRenderer.setColor(Color.YELLOW);
            shapeRenderer.setProjectionMatrix(cam.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.line(clickPos, clickPosTemp);
            shapeRenderer.end();
        }
    }

    @Override
    public void dispose() {
        level1Background.dispose();
        quokka.dispose();
        for(EvilCloud cloud : clouds){
            cloud.dispose();
        }
        shapeRenderer.dispose();
    }

    private void updateBackground(){
        if(cam.position.x - (cam.viewportWidth / 2) > level1BackgroundPos1.x + level1Background.getWidth()) {
            level1BackgroundPos1.add(level1Background.getWidth() * 2, 0);
        }
        if(cam.position.x - (cam.viewportWidth / 2) > level1BackgroundPos2.x + level1Background.getWidth()){
            level1BackgroundPos2.add(level1Background.getWidth() * 2, 0);
        }
        if((cam.position.x +(cam.viewportWidth / 2) < level1BackgroundPos1.x + level1Background.getWidth())&&(cam.position.x + (cam.viewportWidth / 2) < level1BackgroundPos2.x + level1Background.getWidth())) {
            if(level1BackgroundPos2.x > level1BackgroundPos1.x){
                level1BackgroundPos2.sub(level1Background.getWidth()*2, 0);
            }
            else if(level1BackgroundPos1.x > level1BackgroundPos2.x){
                level1BackgroundPos1.sub(level1Background.getWidth()*2, 0);
            }
        }
    }

    private Vector3 resultVector(Vector3 velocity, Vector3 point1, Vector3 point2) {
        velocityTemp.set(velocity);
        normal.set(point1.y-point2.y,point2.x-point1.x,0);
        normal.nor();
        return velocityTemp.sub((normal).scl(2*(velocityTemp.dot(normal))));
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
        clickPos2.set(screenX, -100, 0);
        clickPos.set(screenX, screenY, 0);
        clickPos.set(cam.unproject(clickPos));
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        clickPos2.set(screenX, screenY, 0);
        clickPos2.set(cam.unproject(clickPos2));
        if (((quokka.getPosition().x > clickPos.x) && (quokka.getPosition().x < clickPos2.x)) || ((quokka.getPosition().x < clickPos.x) && (quokka.getPosition().x > clickPos2.x))) {
            while (quokka.getQuokkaBounds().contains(quokka.getPosition().x, lineY(quokka.getPosition().x))) {
                clickPos.set(clickPos.x, clickPos.y - 10, 0);
                clickPos2.set(clickPos2.x, clickPos2.y - 10, 0);
            }
        } else if ((((quokka.getPosition().x + quokka.getTexture().getWidth()) > clickPos.x) && ((quokka.getPosition().x + quokka.getTexture().getWidth()) < clickPos2.x)) || (((quokka.getPosition().x + quokka.getTexture().getWidth()) < clickPos.x) && ((quokka.getPosition().x + quokka.getTexture().getWidth()) > clickPos2.x))) {
            while (quokka.getQuokkaBounds().contains(quokka.getPosition().x + quokka.getTexture().getWidth(), lineY(quokka.getPosition().x + quokka.getTexture().getWidth()))) {
                clickPos.set(clickPos.x, clickPos.y - 10, 0);
                clickPos2.set(clickPos2.x, clickPos2.y - 10, 0);
            }
        }
        clickPosTemp.set(0,-100,0);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        clickPosTemp.set(screenX, screenY, 0);
        clickPosTemp.set(cam.unproject(clickPosTemp));
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
