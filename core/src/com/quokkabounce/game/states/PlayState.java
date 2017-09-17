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
import com.badlogic.gdx.utils.BooleanArray;
import com.quokkabounce.game.QuokkaBounce;
import com.quokkabounce.game.sprites.BonusQuokka;
import com.quokkabounce.game.sprites.EvilCloud;
import com.quokkabounce.game.sprites.HappyCloud;
import com.quokkabounce.game.sprites.Hawk;
import com.quokkabounce.game.sprites.MoveWall;
import com.quokkabounce.game.sprites.Obstacle;
import com.quokkabounce.game.sprites.Quokka;
import com.quokkabounce.game.sprites.Wall;

/**
 * Created by Eric on 8/29/2017.
 */

public class PlayState extends State implements InputProcessor{
    private static final int BACKGROUND_Y_OFFSET = 0;
    private static final int HAWKSIGHT = 400;
    private static final int GOODGRAV = -15;
    private static final int PLANETSCALER = 20;
    private static final double GRAVPOW = 0.5;
    private static final double VIEWPORT_SCALER = 1.6;

    private Quokka quokka;
    private Texture levelBackground;
    private Vector2 levelBackgroundPos1, levelBackgroundPos2;
    private Vector3 clickPos, clickPos2, velocityTemp, normal, clickPosTemp, planetDistance;
    private ShapeRenderer shapeRenderer;
    private HappyCloud happyCloud;
    private float currentDT;
    private boolean shouldFall, touchingWall, lineCheck, lineDraw, justHit, justHitTemp;

    private Array<EvilCloud> clouds;
    private Array<Hawk> hawks;
    private Array<Wall> walls;
    private Array<BonusQuokka> bonusQuokkas;
    private Array<Obstacle> gravitySwitches;
    private Array<Obstacle> switches;
    private Array<Obstacle> planets;
    private Array<MoveWall> moveWalls;
    private BooleanArray collectedQuokkas;

    public PlayState(GameStateManager gsm, int level) {
        super(gsm, level);
        levelBackground = new Texture("level2Background.png");
        clouds = new Array<EvilCloud>();
        walls = new Array<Wall>();
        hawks = new Array<Hawk>();
        bonusQuokkas = new Array<BonusQuokka>();
        collectedQuokkas = new BooleanArray();
        gravitySwitches = new Array<Obstacle>();
        switches = new Array<Obstacle>();
        planets = new Array<Obstacle>();
        moveWalls = new Array<MoveWall>();
        levelInit(level);
        shouldFall = false;
        lineDraw = false;
        lineCheck = false;
        touchingWall = false;
        justHit = false;
        Gdx.input.setInputProcessor(this);
        quokka = new Quokka(50,650);
        cam.setToOrtho(false, Math.round(QuokkaBounce.WIDTH*VIEWPORT_SCALER), Math.round(QuokkaBounce.HEIGHT*VIEWPORT_SCALER));
        shapeRenderer = new ShapeRenderer();
        levelBackgroundPos1= new Vector2(cam.position.x - cam.viewportWidth, BACKGROUND_Y_OFFSET);
        levelBackgroundPos2 = new Vector2((cam.position.x - cam.viewportWidth)+levelBackground.getWidth(), BACKGROUND_Y_OFFSET);
        clickPos = new Vector3(0,0,0);
        clickPos2 = new Vector3(0,-100,0);
        clickPosTemp = new Vector3(0,-100,0);
        velocityTemp = new Vector3(0,0,0);
        normal = new Vector3(0,0,0);
        planetDistance = new Vector3(0,0,0);
    }

    @Override
    public void update(float dt) {
        currentDT = dt;
        updateBackground();
        cam.position.x = quokka.getPosition().x + 80;
        justHitTemp = false;
        if(lineCheck) {
            if(!justHit) {
                if (((quokka.getPosition().x > clickPos.x) && (quokka.getPosition().x < clickPos2.x)) || ((quokka.getPosition().x < clickPos.x) && (quokka.getPosition().x > clickPos2.x))) {
                    if (quokka.getQuokkaBounds().contains(quokka.getPosition().x, lineY(quokka.getPosition().x))) {
                        quokka.setVelocity(resultVector(quokka.getVelocity(), clickPos, clickPos2));
                        justHitTemp = true;
                    }
                } else if ((((quokka.getPosition().x + quokka.getTexture().getWidth()) > clickPos.x) && ((quokka.getPosition().x + quokka.getTexture().getWidth()) < clickPos2.x)) || (((quokka.getPosition().x + quokka.getTexture().getWidth()) < clickPos.x) && ((quokka.getPosition().x + quokka.getTexture().getWidth()) > clickPos2.x))) {
                    if (quokka.getQuokkaBounds().contains(quokka.getPosition().x + quokka.getTexture().getWidth(), lineY(quokka.getPosition().x + quokka.getTexture().getWidth()))) {
                        quokka.setVelocity(resultVector(quokka.getVelocity(), clickPos, clickPos2));
                        justHitTemp = true;
                    }
                }
            }
            else{
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
            }
        }
        justHit = justHitTemp;
        for(Hawk hawk : hawks){
            if(hawk.collides(quokka.getQuokkaBounds())){
                gsm.set(new PlayState(gsm, level));
                break;
            }
            if(Math.sqrt(Math.pow(hawk.getHawkBounds().x + hawk.getHawkBounds().width / 2 - quokka.getQuokkaBounds().x - quokka.getQuokkaBounds().width / 2, 2) + Math.pow(hawk.getHawkBounds().y + hawk.getHawkBounds().height / 2 - quokka.getQuokkaBounds().y - quokka.getQuokkaBounds().height / 2, 2)) <= HAWKSIGHT){
                hawk.move(true, dt, quokka.getPosition());
            }
            else{
                hawk.move(false, dt, quokka.getPosition());
            }
        }
        if(shouldFall) {
            quokka.update(dt);
        }
        for (EvilCloud cloud : clouds){
            if(cloud.collides(quokka.getQuokkaBounds())){
                gsm.set(new PlayState(gsm, level));
                break;
            }
        }
        for(Wall wall : walls){
            boolean moveWall = false;
            if(wall.hasSwitch()) {
                for (Obstacle wallSwitch : wall.getWallSwitches()) {
                    if (wallSwitch.collides(quokka.getQuokkaBounds())) {
                        switches.removeIndex(switches.indexOf(wallSwitch, false));
                        wallSwitch.dispose();
                        moveWall = true;
                    }
                }
            }
            if(moveWall){
                wall.setPosWall(wall.getPosWall().x, wall.getPosWall().y + wall.getWallMove());
                wall.setWallBounds(wall.getPosWall().x, wall.getPosWall().y, wall.getTexture().getWidth(), wall.getTexture().getHeight());
            }
            if(wall.collides(quokka.getQuokkaBounds())){
                if(!touchingWall) {
                    touchingWall = true;
                    Vector3 tempVelocity = new Vector3();
                    tempVelocity.set(quokka.getVelocity());
                    tempVelocity.scl(dt);
                    if (quokka.getQuokkaBounds().x + quokka.getTexture().getWidth() - tempVelocity.x < wall.getWallBounds().getX()) {
                        quokka.setVelocity(resultVector(quokka.getVelocity(), new Vector3(wall.getWallBounds().getX(), wall.getWallBounds().getY(), 0), new Vector3(new Vector3(wall.getWallBounds().getX(), wall.getWallBounds().getY() + wall.getWallBounds().getHeight(), 0))));
                    } else if (quokka.getQuokkaBounds().x - tempVelocity.x > wall.getWallBounds().getX() + wall.getTexture().getWidth()) {
                        quokka.setVelocity(resultVector(quokka.getVelocity(), new Vector3(wall.getWallBounds().getX() + wall.getTexture().getWidth(), wall.getWallBounds().getY(), 0), new Vector3(new Vector3(wall.getWallBounds().getX() + wall.getTexture().getWidth(), wall.getWallBounds().getY() + wall.getWallBounds().getHeight(), 0))));
                    } else if (quokka.getQuokkaBounds().y + quokka.getQuokkaBounds().getHeight() - tempVelocity.y < wall.getWallBounds().getY()) {
                        quokka.setVelocity(resultVector(quokka.getVelocity(), new Vector3(wall.getWallBounds().getX(), wall.getWallBounds().getY(), 0), new Vector3(new Vector3(wall.getWallBounds().getX() + wall.getTexture().getWidth(), wall.getWallBounds().getY(), 0))));
                    } else{
                        quokka.setVelocity(resultVector(quokka.getVelocity(), new Vector3(wall.getWallBounds().getX(), wall.getWallBounds().getY() + wall.getWallBounds().getHeight(), 0), new Vector3(wall.getWallBounds().getX() + wall.getWallBounds().getWidth(), wall.getWallBounds().getY() + wall.getWallBounds().getHeight(), 0)));
                    }
                }
            }
            else{
                touchingWall = false;
            }
        }
        for(MoveWall moveWall : moveWalls){
            if(moveWall.collides(quokka.getQuokkaBounds())){
                if(!touchingWall) {
                    touchingWall = true;
                    Vector3 tempVelocity = new Vector3();
                    tempVelocity.set(quokka.getVelocity());
                    tempVelocity.scl(dt);
                    if (quokka.getQuokkaBounds().x + quokka.getTexture().getWidth() - tempVelocity.x < moveWall.getWallBounds().getX()) {
                        quokka.setVelocity(resultVector(quokka.getVelocity(), new Vector3(moveWall.getWallBounds().getX(), moveWall.getWallBounds().getY(), 0), new Vector3(new Vector3(moveWall.getWallBounds().getX(), moveWall.getWallBounds().getY() + moveWall.getWallBounds().getHeight(), 0))));
                    } else if (quokka.getQuokkaBounds().x - tempVelocity.x > moveWall.getWallBounds().getX() + moveWall.getTexture().getWidth()) {
                        quokka.setVelocity(resultVector(quokka.getVelocity(), new Vector3(moveWall.getWallBounds().getX() + moveWall.getTexture().getWidth(), moveWall.getWallBounds().getY(), 0), new Vector3(new Vector3(moveWall.getWallBounds().getX() + moveWall.getTexture().getWidth(), moveWall.getWallBounds().getY() + moveWall.getWallBounds().getHeight(), 0))));
                    } else if (quokka.getQuokkaBounds().y + quokka.getQuokkaBounds().getHeight() - tempVelocity.y < moveWall.getWallBounds().getY()) {
                        quokka.setVelocity(resultVector(quokka.getVelocity(), new Vector3(moveWall.getWallBounds().getX(), moveWall.getWallBounds().getY(), 0), new Vector3(new Vector3(moveWall.getWallBounds().getX() + moveWall.getTexture().getWidth(), moveWall.getWallBounds().getY(), 0))));
                    } else{
                        quokka.setVelocity(resultVector(quokka.getVelocity(), new Vector3(moveWall.getWallBounds().getX(), moveWall.getWallBounds().getY() + moveWall.getWallBounds().getHeight(), 0), new Vector3(moveWall.getWallBounds().getX() + moveWall.getWallBounds().getWidth(), moveWall.getWallBounds().getY() + moveWall.getWallBounds().getHeight(), 0)));
                    }
                }
            }
            else{
                touchingWall = false;
            }
        }
        for(BonusQuokka bonusQuokka : bonusQuokkas){
            if(bonusQuokka.collides(quokka.getQuokkaBounds())){
                if(!collectedQuokkas.get(bonusQuokkas.indexOf(bonusQuokka, false))) {
                    collectedQuokkas.set(bonusQuokkas.indexOf(bonusQuokka, false), true);
                    bonusQuokka.dispose();
                }
            }
        }
        for(Obstacle gravitySwitch : gravitySwitches){
            if(gravitySwitch.collides(quokka.getQuokkaBounds())){
                quokka.setGravity(quokka.getGravity().x, -1 * quokka.getGravity().y, 0);
            }
        }
        quokka.getGravity().set(0,0,0);
        for(Obstacle planet : planets){
            planetDistance.set(quokka.getPosition().x + quokka.getTexture().getWidth() / 2 - planet.getPosObstacle().x - planet.getTexture().getWidth() / 2, quokka.getPosition().y + quokka.getTexture().getHeight() / 2- planet.getPosObstacle().y - planet.getTexture().getWidth() / 2, 0);
            double planetMagnitude = Math.sqrt(Math.pow(planetDistance.x, 2) + Math.pow(planetDistance.y, 2));
            if(planetMagnitude != 0) {
                planetDistance.scl(Math.round(GOODGRAV / Math.pow(planetMagnitude, GRAVPOW)));
            }
            quokka.getGravity().add(planetDistance.x, planetDistance.y, 0);
        }
        quokka.getGravity().set(quokka.getGravity().x / PLANETSCALER, quokka.getGravity().y / PLANETSCALER, 0);
        if(quokka.getGravity().x == 0 && quokka.getGravity().y == 0){
            quokka.getGravity().set(0, -13, 0);
        }
        if(quokka.getPosition().y==0){
            gsm.set(new PlayState(gsm, level));
        }
        if(happyCloud.collides(quokka.getQuokkaBounds())){
            gsm.set(new MenuState(gsm, level + 1));
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
        sb.draw(levelBackground, levelBackgroundPos1.x, levelBackgroundPos1.y);
        sb.draw(levelBackground, levelBackgroundPos2.x, levelBackgroundPos2.y);
        sb.draw(quokka.getTexture(), quokka.getPosition().x, quokka.getPosition().y);
        for(BonusQuokka bonusQuokka : bonusQuokkas){
            if(!collectedQuokkas.get(bonusQuokkas.indexOf(bonusQuokka, false))) {
                sb.draw(bonusQuokka.getTexture(), bonusQuokka.getPosQuokka().x, bonusQuokka.getPosQuokka().y);
            }
        }
        for(EvilCloud cloud: clouds) {
            sb.draw(cloud.getTexture(), cloud.getPosCloud().x, cloud.getPosCloud().y);
        }
        for(Obstacle gravitySwitch: gravitySwitches){
            sb.draw(gravitySwitch.getTexture(), gravitySwitch.getPosObstacle().x, gravitySwitch.getPosObstacle().y);
        }
        for(Obstacle planet : planets){
            sb.draw(planet.getTexture(), planet.getPosObstacle().x, planet.getPosObstacle().y);
        }
        for(Obstacle wallSwitch: switches){
            sb.draw(wallSwitch.getTexture(), wallSwitch.getPosObstacle().x, wallSwitch.getPosObstacle().y);
        }
        for(Wall wall : walls){
            sb.draw(wall.getTexture(), wall.getPosWall().x, wall.getPosWall().y);
        }
        for(MoveWall moveWall : moveWalls){
            sb.draw(moveWall.getTexture(), moveWall.getPosWall().x, moveWall.getPosWall().y);
        }
        for (Hawk hawk : hawks){
            sb.draw(hawk.getTexture(), hawk.getPosHawk().x, hawk.getPosHawk().y);
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
        levelBackground.dispose();
        quokka.dispose();
        for(EvilCloud cloud : clouds){
            cloud.dispose();
        }
        happyCloud.dispose();
        for(Wall wall : walls){
            wall.dispose();
        }
        for(BonusQuokka bonusQuokka : bonusQuokkas){
            bonusQuokka.dispose();
        }
        for(Obstacle gravitySwitch : gravitySwitches){
            gravitySwitch.dispose();
        }
        for(Obstacle planet : planets){
            planet.dispose();
        }
        for(Obstacle wallSwitch: switches){
            wallSwitch.dispose();
        }
        for(MoveWall moveWall : moveWalls){
            moveWall.dispose();
        }
        for(Hawk hawk : hawks){
            hawk.dispose();
        }
        shapeRenderer.dispose();
    }

    public void levelInit(int level){
        switch(level){
            case 1:
                levelBackground = new Texture("level1Background.png");
                switches.add(new Obstacle(200, 100, "wallSwitch.png"));
                walls.add(new Wall(400, -220, switches, -200));
                walls.add(new Wall(400, 375));
                happyCloud = new HappyCloud(10000,200);
                break;
            case 2:
                levelBackground = new Texture("level2Background.png");
                walls.add(new Wall(500,-180));
                walls.add(new Wall(1400,400));
                happyCloud = new HappyCloud(2060, 200);
                bonusQuokkas.add(new BonusQuokka(20,200));
                break;
            case 3:
                levelBackground = new Texture("level3Background.png");
                walls.add(new Wall(350, -80));
                walls.add(new Wall(850, -230));
                clouds.add(new EvilCloud(1200, 460));
                bonusQuokkas.add(new BonusQuokka(1200, 10));
                happyCloud = new HappyCloud(500, 50);
                break;
            case 4:
                levelBackground = new Texture("level1Background.png");
                walls.add(new Wall(300,500));
                walls.add(new Wall(300, -380));
                walls.add(new Wall (600, -380));
                bonusQuokkas.add(new BonusQuokka(600, 400));
                clouds.add(new EvilCloud(800, 500));
                walls.add(new Wall(1300, 600));
                clouds.add(new EvilCloud(1300, 50));
                happyCloud = new HappyCloud(1600, 300);
                break;
            case 5:
                levelBackground = new Texture("spaceBackground.png");
                planets.add(new Obstacle(200, 200, "greenPlanet.png"));
                planets.add(new Obstacle(800, 500, "greenPlanet.png"));
                happyCloud = new HappyCloud(5200, 300);
                break;
            /*case 3:
                levelBackground = new Texture("level3Background.png");
                clouds.add(new EvilCloud(200, 300));
                walls.add(new Wall(900, 450));
                clouds.add(new EvilCloud(1500, 150));
                happyCloud = new HappyCloud(1710,400);
                break;
            case 4:
                levelBackground = new Texture("level1Background.png");
                walls.add(new Wall(300, -80));
                clouds.add(new EvilCloud(450, 350));
                clouds.add(new EvilCloud(1200, 350));
                happyCloud = new HappyCloud(1650, 150);
                break;
            case 5:
                levelBackground = new Texture("level2Background.png");
                hawks.add(new Hawk(400,150));
                happyCloud = new HappyCloud(850, 300);
                break;
            case 6:
                levelBackground = new Texture("level3Background.png");
                clouds.add(new EvilCloud(200, 50));
                walls.add(new Wall (750, 300));
                walls.add(new Wall(1150, -130));
                happyCloud = new HappyCloud(1400, 200);
                break;
            case 7:
                levelBackground = new Texture("level1Background.png");
                clouds.add(new EvilCloud(250, 200));
                bonusQuokkas.add(new BonusQuokka(650, 0));
                walls.add(new Wall(900, -180));
                clouds.add(new EvilCloud(1050, 300));
                walls.add(new Wall(1500, 400));
                happyCloud = new HappyCloud(1700, 500);
                break;
            case 8:
                levelBackground = new Texture("level2Background.png");
                for(int i = 0; i < 10; i++){
                    walls.add(new Wall(400 + 123 * i, -280));
                    walls.add(new Wall(400 + 123 * i, 500));
                }
                happyCloud = new HappyCloud(1400, 5);
                break;*/
            case 6:
                levelBackground = new Texture("level3Background.png");
                walls.add(new Wall(350, 300));
                bonusQuokkas.add(new BonusQuokka(500, 400));
                walls.add(new Wall(750,300));
                happyCloud = new HappyCloud(1250, 200);
                break;
            case 9:
                levelBackground = new Texture("level1Background.png");
                walls.add(new Wall(300,600));
                walls.add(new Wall(300, -280));
                walls.add(new Wall(700, 700));
                walls.add(new Wall(700, -180));
                walls.add(new Wall(1100, 500));
                walls.add(new Wall(1100, -380));
                happyCloud = new HappyCloud(1300, 5);
                walls.add(new Wall(1700, 500));
                walls.add(new Wall(1700, -380));
                bonusQuokkas.add(new BonusQuokka(1900, 300));
                break;

        }
        collectedQuokkas.setSize(bonusQuokkas.size);
    }

    private void updateBackground(){
        if(cam.position.x - (cam.viewportWidth / 2) > levelBackgroundPos1.x + levelBackground.getWidth()) {
            levelBackgroundPos1.add(levelBackground.getWidth() * 2, 0);
        }
        if(cam.position.x - (cam.viewportWidth / 2) > levelBackgroundPos2.x + levelBackground.getWidth()){
            levelBackgroundPos2.add(levelBackground.getWidth() * 2, 0);
        }
        if((cam.position.x +(cam.viewportWidth / 2) < levelBackgroundPos1.x + levelBackground.getWidth())&&(cam.position.x + (cam.viewportWidth / 2) < levelBackgroundPos2.x + levelBackground.getWidth())) {
            if(levelBackgroundPos2.x > levelBackgroundPos1.x){
                levelBackgroundPos2.sub(levelBackground.getWidth()*2, 0);
            }
            else if(levelBackgroundPos1.x > levelBackgroundPos2.x){
                levelBackgroundPos1.sub(levelBackground.getWidth()*2, 0);
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
        lineDraw = true;
        shouldFall = true;
        lineCheck = false;
        clickPos2.set(screenX, -100, 0);
        clickPos.set(screenX, screenY, 0);
        clickPos.set(cam.unproject(clickPos));
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(lineDraw) {
            lineCheck = true;
            clickPos2.set(screenX, screenY, 0);
            clickPos2.set(cam.unproject(clickPos2));
            if (((quokka.getPosition().x > clickPos.x) && (quokka.getPosition().x < clickPos2.x)) || ((quokka.getPosition().x < clickPos.x) && (quokka.getPosition().x > clickPos2.x))) {
                while (quokka.getQuokkaBounds().contains(quokka.getPosition().x, lineY(quokka.getPosition().x))) {
                    clickPos.set(clickPos.x, clickPos.y - 10, 0);
                    clickPos2.set(clickPos2.x, clickPos2.y - 10, 0);
                }
                quokka.getVelocity().scl(currentDT);
                quokka.getQuokkaBounds().set(quokka.getQuokkaBounds().x + quokka.getVelocity().x, quokka.getQuokkaBounds().y + quokka.getVelocity().y, quokka.getQuokkaBounds().width, quokka.getQuokkaBounds().height);
                while (quokka.getQuokkaBounds().contains(quokka.getPosition().x + quokka.getVelocity().x, lineY(quokka.getPosition().x + quokka.getVelocity().y))) {
                    clickPos.set(clickPos.x, clickPos.y - 10, 0);
                    clickPos2.set(clickPos2.x, clickPos2.y - 10, 0);
                }
                quokka.getVelocity().scl(1 / currentDT);
            } else if ((((quokka.getPosition().x + quokka.getTexture().getWidth()) > clickPos.x) && ((quokka.getPosition().x + quokka.getTexture().getWidth()) < clickPos2.x)) || (((quokka.getPosition().x + quokka.getTexture().getWidth()) < clickPos.x) && ((quokka.getPosition().x + quokka.getTexture().getWidth()) > clickPos2.x))) {
                while (quokka.getQuokkaBounds().contains(quokka.getPosition().x + quokka.getTexture().getWidth(), lineY(quokka.getPosition().x + quokka.getTexture().getWidth()))) {
                    clickPos.set(clickPos.x, clickPos.y - 10, 0);
                    clickPos2.set(clickPos2.x, clickPos2.y - 10, 0);
                }
                quokka.getVelocity().scl(currentDT);
                quokka.getQuokkaBounds().set(quokka.getQuokkaBounds().x + quokka.getVelocity().x, quokka.getQuokkaBounds().y + quokka.getVelocity().y, quokka.getQuokkaBounds().width, quokka.getQuokkaBounds().height);
                while (quokka.getQuokkaBounds().contains(quokka.getPosition().x + quokka.getVelocity().x, lineY(quokka.getPosition().x + quokka.getVelocity().y))) {
                    clickPos.set(clickPos.x, clickPos.y - 10, 0);
                    clickPos2.set(clickPos2.x, clickPos2.y - 10, 0);
                }
                quokka.getVelocity().scl(1 / currentDT);
            }
            clickPosTemp.set(0, -100, 0);
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(lineDraw) {
            clickPosTemp.set(screenX, screenY, 0);
            clickPosTemp.set(cam.unproject(clickPosTemp));
        }
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
