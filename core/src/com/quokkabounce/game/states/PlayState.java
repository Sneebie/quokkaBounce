package com.quokkabounce.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
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
    private Vector3 clickPos, clickPos2;
    private ShapeRenderer shapeRenderer;

    private Array<EvilCloud> clouds;

    public PlayState(GameStateManager gsm) {
        super(gsm);
        Gdx.input.setInputProcessor(this);
        quokka = new Quokka(50,300);
        level1Background = new Texture("level1Background.png");
        cam.setToOrtho(false, Math.round(QuokkaBounce.WIDTH*VIEWPORT_SCALER), Math.round(QuokkaBounce.HEIGHT*VIEWPORT_SCALER));
        level1BackgroundPos1= new Vector2(cam.position.x - cam.viewportWidth, BACKGROUND_Y_OFFSET);
        level1BackgroundPos2 = new Vector2((cam.position.x - cam.viewportWidth)+level1Background.getWidth(), BACKGROUND_Y_OFFSET);
        clouds = new Array<EvilCloud>();
        rand = new Random();

        for(int i=1; i<=CLOUD_COUNT; i++){
            clouds.add(new EvilCloud(i*(CLOUD_SPACING + EvilCloud.CLOUD_WIDTH), rand.nextInt(600)));
        }
    }

    @Override
    public void update(float dt) {
        updateBackground();
        quokka.update(dt);
        cam.position.x = quokka.getPosition().x + 80;

        for (EvilCloud cloud : clouds){
            if((cam.position.x - (cam.viewportWidth/2))>(cloud.getPosCloud().x + cloud.getTexture().getWidth())){
                cloud.reposition(cloud.getPosCloud().x + ((EvilCloud.CLOUD_WIDTH  + CLOUD_SPACING) * CLOUD_COUNT), cloud.getPosCloud().y);
            }
            if(cloud.collides(quokka.getQuokkaBounds())){
                gsm.set(new PlayState(gsm));
            }
            if(quokka.getPosition().y==0){
                gsm.set(new PlayState(gsm));
            }
        }

        cam.update();

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
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 1, 0, 1);
        shapeRenderer.line(x, y, x2, y2);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.circle(x, y, radius);
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        level1Background.dispose();
        quokka.dispose();
        for(EvilCloud cloud : clouds){
            cloud.dispose();
        }
    }

    private void updateBackground(){
        if(cam.position.x - (cam.viewportWidth / 2) > level1BackgroundPos1.x + level1Background.getWidth()) {
            level1BackgroundPos1.add(level1Background.getWidth() * 2, 0);
        }
        if(cam.position.x - (cam.viewportWidth / 2) > level1BackgroundPos2.x + level1Background.getWidth()){
            level1BackgroundPos2.add(level1Background.getWidth() * 2, 0);
        }
    }

    private Vector3 resultVector(velocity, point1, point2) {
        if(point2.x - point1.x < 0) {
            Vector3 dir = point1.sub(point2 a_vec);
        } else {
            Vector3 dir = point2.sub(point1 a_vec);
        }
        float theta = Math.acos((velocity.dot(dir vector)) / (velocity.len() * dir.len()))
        if(theta == 0 || theta == (Math.PI /2) || theta == Math.PI) {
            theta = 0;
        } else if(theta > 0 && theta < (Math.PI /2)) {
            theta = Math.PI - 2*theta;
        } else {
            theta = Math.PI*3 - 2*theta;
        }
        velocity.rotateRad(theta, 0, 0, 0); //this should be counterclockwise
        return velocity;
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
        clickPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        cam.unproject(clickPos);
        quokka.jump();
        System.out.println("touchd down");
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        clickPos2 = new Vector3(screenX, screenY, 0);
        cam.unproject(clickPos2);
        System.out.println("touchup");
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
