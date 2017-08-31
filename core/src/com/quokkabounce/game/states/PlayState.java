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
import com.quokkabounce.game.sprites.Quokka;

import java.util.Random;

/**
 * Created by Eric on 8/29/2017.
 */

public class PlayState extends State implements InputProcessor{
    private static final int CLOUD_SPACING = 125;
    private static final int CLOUD_COUNT = 4;
    private static final int BACKGROUND_Y_OFFSET = 0;
    private static final double VIEWPORT_SCALER = 1.6;

    private Quokka quokka;
    private Texture level1Background;
    private Random rand;
    private Vector2 level1BackgroundPos1, level1BackgroundPos2;
    private Vector3 clickPos, clickPos2, velocityTemp, point1Temp, point2Temp;
    private ShapeRenderer shapeRenderer;

    private Array<EvilCloud> clouds;

    public PlayState(GameStateManager gsm) {
        super(gsm);
        Gdx.input.setInputProcessor(this);
        quokka = new Quokka(50,300);
        level1Background = new Texture("level1Background.png");
        cam.setToOrtho(false, Math.round(QuokkaBounce.WIDTH*VIEWPORT_SCALER), Math.round(QuokkaBounce.HEIGHT*VIEWPORT_SCALER));
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setColor(Color.BROWN);
        level1BackgroundPos1= new Vector2(cam.position.x - cam.viewportWidth, BACKGROUND_Y_OFFSET);
        level1BackgroundPos2 = new Vector2((cam.position.x - cam.viewportWidth)+level1Background.getWidth(), BACKGROUND_Y_OFFSET);
        clouds = new Array<EvilCloud>();
        rand = new Random();
        clickPos = new Vector3(0,0,0);
        clickPos2 = new Vector3(0,-100,0);
        velocityTemp = new Vector3(0,0,0);
        point1Temp = new Vector3(0,0,0);
        point2Temp = new Vector3(0,0,0);
        for(int i=1; i<=CLOUD_COUNT; i++){
            clouds.add(new EvilCloud(i*(CLOUD_SPACING + EvilCloud.CLOUD_WIDTH), rand.nextInt(600)));
        }
    }

    @Override
    public void update(float dt) {
        updateBackground();
        quokka.update(dt);
        cam.position.x = quokka.getPosition().x + 80;
        if(((quokka.getPosition().x > clickPos.x) && (quokka.getPosition().x < clickPos2.x)) || ((quokka.getPosition().x < clickPos.x) && (quokka.getPosition().x > clickPos2.x))){
            if(quokka.getQuokkaBounds().contains(quokka.getPosition().x, lineY(quokka.getPosition().x))){
                System.out.println(quokka.getVelocity());
                quokka.setVelocity(resultVector(quokka.getVelocity(), clickPos, clickPos2));
                System.out.println(quokka.getVelocity());
            }
        }
        else if((((quokka.getPosition().x + quokka.getTexture().getWidth()) > clickPos.x) && ((quokka.getPosition().x + quokka.getTexture().getWidth()) < clickPos2.x)) || (((quokka.getPosition().x + quokka.getTexture().getWidth()) < clickPos.x) && ((quokka.getPosition().x + quokka.getTexture().getWidth()) > clickPos2.x))){
            if(quokka.getQuokkaBounds().contains(quokka.getPosition().x + quokka.getTexture().getWidth(), lineY(quokka.getPosition().x + quokka.getTexture().getWidth()))){
                quokka.setVelocity(resultVector(quokka.getVelocity(), clickPos, clickPos2));
            }
        }
        for (EvilCloud cloud : clouds){
            if((cam.position.x - (cam.viewportWidth/2))>(cloud.getPosCloud().x + cloud.getTexture().getWidth())){
                cloud.reposition(cloud.getPosCloud().x + ((EvilCloud.CLOUD_WIDTH  + CLOUD_SPACING) * CLOUD_COUNT), cloud.getPosCloud().y);
            }
            if(cloud.collides(quokka.getQuokkaBounds())){
                gsm.set(new PlayState(gsm));
                break;
            }
            if(quokka.getPosition().y==0){
                gsm.set(new PlayState(gsm));
                break;
            }
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
        sb.end();
        if(clickPos2.y!=-100) {
            shapeRenderer.setProjectionMatrix(cam.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.line(clickPos, clickPos2);
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
    }

    private Vector3 resultVector(Vector3 velocity, Vector3 point1, Vector3 point2) {
        velocityTemp.set(velocity);
        point1Temp.set(point1);
        point2Temp.set(point2);
        return velocityTemp.sub(point2Temp.sub(point1Temp).nor().scl(2*(velocityTemp.dot(point2Temp.sub(point1Temp).nor()))));
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
