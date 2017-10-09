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
import com.quokkabounce.game.sprites.Arrow;
import com.quokkabounce.game.sprites.BonusQuokka;
import com.quokkabounce.game.sprites.Button;
import com.quokkabounce.game.sprites.EvilCloud;
import com.quokkabounce.game.sprites.HappyCloud;
import com.quokkabounce.game.sprites.Hawk;
import com.quokkabounce.game.sprites.MoveWall;
import com.quokkabounce.game.sprites.Obstacle;
import com.quokkabounce.game.sprites.Quokka;
import com.quokkabounce.game.sprites.Vine;
import com.quokkabounce.game.sprites.Wall;

/**
 * Created by Eric on 8/29/2017.
 */

public class PlayState extends State implements InputProcessor{
    private static final int HAWKSIGHT = 400;
    private static final int GOODGRAV = -150000;
    private static final int PLANETSCALER = 30;
    private static final float DEPTHSCALER = 0.0001f;
    private static final double OCEANSLOW = 0.98;
    private static final float GRAVPOW = 2f;
    private static final float VIEWPORT_SCALER = 1.6f;
    private static final int SHRINKRATE = 3;
    private static final int TOWERFALL = 100;

    private Quokka quokka;
    private Button backButton, pauseButton;
    private Texture levelBackground;
    private Vector2 levelBackgroundPos1, levelBackgroundPos2, levelBackgroundPos3, levelBackgroundPos4, intersectionPoint, intersectionPointTemp, circleCenter, quokkaSide, adjustedCenter, planetProj;
    private Vector3 clickPos, clickPos2, velocityTemp, velocityTemp2, normal, clickPosTemp, planetDistance, gradientVector, touchInput, towerVel;
    private ShapeRenderer shapeRenderer;
    private HappyCloud happyCloud;
    private float currentDT;
    private int layer, finalLayer;
    private boolean shouldFall, touchingWall, lineCheck, lineDraw, justHit, vineDraw, justHitTemp, outZone, justPlanet, justPlanetTemp, paused, justPaused, vineCheck;

    private Array<EvilCloud> clouds;
    private Array<Hawk> hawks;
    private Array<Wall> walls;
    private Array<BonusQuokka> bonusQuokkas;
    private Array<Obstacle> switches;
    private Array<Obstacle> planets;
    private Array<Obstacle> nullZones;
    private Array<Arrow> arrows;
    private Array<Array<Vine>> layerVines;
    private Array<Vine> vines;
    private Array<Texture> layerTextures;
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
        switches = new Array<Obstacle>();
        planets = new Array<Obstacle>();
        nullZones = new Array<Obstacle>();
        arrows = new Array<Arrow>();
        moveWalls = new Array<MoveWall>();
        vines = new Array<Vine>();
        layerTextures = new Array<Texture>();
        layerVines = new Array<Array<Vine>>();
        layer = 0;
        finalLayer = 0;
        if(planets.size == 0) {
            cam.setToOrtho(false, Math.round(QuokkaBounce.WIDTH * VIEWPORT_SCALER), Math.round(QuokkaBounce.HEIGHT * VIEWPORT_SCALER));
        }
        else{
            cam.setToOrtho(false, Math.round(QuokkaBounce.WIDTH * VIEWPORT_SCALER), Math.round(QuokkaBounce.HEIGHT * VIEWPORT_SCALER));
        }
        levelInit(level);
        shouldFall = false;
        lineDraw = false;
        vineDraw = true;
        lineCheck = false;
        touchingWall = false;
        justHit = false;
        justPlanet = false;
        paused = false;
        justPaused = false;
        vineCheck = true;
        Gdx.input.setInputProcessor(this);
        if(arrows.size < 1) {
            quokka = new Quokka(50, 650);
        }
        else{
            quokka = new Quokka(600, 650);
        }
        shapeRenderer = new ShapeRenderer();
        levelBackgroundPos1= new Vector2(cam.position.x - cam.viewportWidth, 0);
        levelBackgroundPos3 = new Vector2(cam.position.x - cam.viewportWidth, -1 * levelBackground.getHeight());
        levelBackgroundPos2 = new Vector2((cam.position.x - cam.viewportWidth) + levelBackground.getWidth(), 0);
        levelBackgroundPos4 = new Vector2((cam.position.x - cam.viewportWidth) + levelBackground.getWidth(), -1 * levelBackground.getHeight());
        intersectionPoint = new Vector2();
        intersectionPointTemp = new Vector2();
        circleCenter = new Vector2();
        quokkaSide = new Vector2();
        adjustedCenter = new Vector2();
        planetProj = new Vector2();
        gradientVector = new Vector3();
        touchInput = new Vector3();
        clickPos = new Vector3(0,0,0);
        clickPos2 = new Vector3(0,-100,0);
        clickPosTemp = new Vector3(0,-100,0);
        velocityTemp = new Vector3(0,0,0);
        velocityTemp2 = new Vector3(0,0,0);
        normal = new Vector3(0,0,0);
        planetDistance = new Vector3(0,0,0);
        towerVel = new Vector3(0, TOWERFALL, 0);
    }

    @Override
    public void pause(){
        paused = true;
    }

    @Override
    public void update(float dt) {
        if(!paused) {
            if (layerVines.size > 0) {
                levelBackground = (layerTextures.get(layer));
                vines.clear();
                vines.addAll(layerVines.get(layer));
            }
            currentDT = dt;
            updateBackground();
            if(arrows.size > 0 && shouldFall){
                towerVel.scl(dt);
                cam.position.y += towerVel.y;
                quokka.getPosition().y += towerVel.y;
                towerVel.scl(1/dt);
            }
            else {
                cam.position.x = quokka.getPosition().x + 80;
            }
            if (moveWalls.size != 0) {
                cam.position.y = quokka.getPosition().y;
            }
            backButton.getPosButton().x = cam.position.x - 800;
            pauseButton.getPosButton().x = cam.position.x - 800;
            backButton.getPosButton().y = cam.position.y + 100;
            pauseButton.getPosButton().y = cam.position.y - 200;
            backButton.getButtonBounds().set(backButton.getPosButton().x, backButton.getPosButton().y, backButton.getButtonBounds().getWidth(), backButton.getButtonBounds().getHeight());
            pauseButton.getButtonBounds().set(pauseButton.getPosButton().x, pauseButton.getPosButton().y, pauseButton.getButtonBounds().getWidth(), pauseButton.getButtonBounds().getHeight());
            justHitTemp = false;
            if (lineCheck) {
                outZone = false;
                if (!justHit) {
                    if (((quokka.getPosition().x > clickPos.x) && (quokka.getPosition().x < clickPos2.x)) || ((quokka.getPosition().x < clickPos.x) && (quokka.getPosition().x > clickPos2.x))) {
                        if (quokka.getQuokkaBounds().contains(quokka.getPosition().x, lineY(quokka.getPosition().x))) {
                            outZone = true;
                            for (Obstacle nullZone : nullZones) {
                                if (nullZone.getObstacleBounds().contains(quokka.getPosition().x, lineY(quokka.getPosition().x))) {
                                    outZone = false;
                                }
                            }
                        }
                    } else if ((((quokka.getPosition().x + quokka.getTexture().getWidth()) > clickPos.x) && ((quokka.getPosition().x + quokka.getTexture().getWidth()) < clickPos2.x)) || (((quokka.getPosition().x + quokka.getTexture().getWidth()) < clickPos.x) && ((quokka.getPosition().x + quokka.getTexture().getWidth()) > clickPos2.x))) {
                        if (quokka.getQuokkaBounds().contains(quokka.getPosition().x + quokka.getTexture().getWidth(), lineY(quokka.getPosition().x + quokka.getTexture().getWidth()))) {
                            outZone = true;
                            for (Obstacle nullZone : nullZones) {
                                if (nullZone.getObstacleBounds().contains(quokka.getPosition().x + quokka.getTexture().getWidth(), lineY(quokka.getPosition().x + quokka.getTexture().getWidth()))) {
                                    outZone = false;
                                }
                            }
                        }
                    }
                } else {
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
                if (outZone) {
                    quokka.setVelocity(resultVector(quokka.getVelocity(), clickPos, clickPos2));
                    justHitTemp = true;
                }
            }
            justHit = justHitTemp;
            for (Hawk hawk : hawks) {
                if (hawk.collides(quokka.getQuokkaBounds())) {
                    gsm.set(new PlayState(gsm, level));
                    break;
                }
                if (Math.sqrt(Math.pow(hawk.getHawkBounds().x + hawk.getHawkBounds().width / 2 - quokka.getQuokkaBounds().x - quokka.getQuokkaBounds().width / 2, 2) + Math.pow(hawk.getHawkBounds().y + hawk.getHawkBounds().height / 2 - quokka.getQuokkaBounds().y - quokka.getQuokkaBounds().height / 2, 2)) <= HAWKSIGHT) {
                    hawk.move(true, dt, quokka.getPosition());
                } else {
                    hawk.move(false, dt, quokka.getPosition());
                }
            }
            for(Arrow arrow: arrows){
                if(arrow.getPosArrow().x + arrow.getArrowBounds().getWidth() < cam.position.x - cam.viewportWidth / 2 || arrow.getPosArrow().x > cam.position.x + cam.viewportWidth / 2 || arrow.getPosArrow().y > cam.position.y + cam.viewportHeight / 2 || arrow.getPosArrow().y + arrow.getArrowBounds().getHeight() < cam.position.y - cam.viewportHeight / 2){
                    arrow.setPosArrow(arrow.getInitialPos());
                    arrow.setAlreadySpotted(false);
                    arrow.setShouldShoot(false);
                }
                if(arrow.collides(quokka.getQuokkaBounds())){
                    gsm.set(new PlayState(gsm, level));
                    break;
                }
                if (Math.sqrt(Math.pow(arrow.getArrowBounds().x + arrow.getArrowBounds().width / 2 - quokka.getQuokkaBounds().x - quokka.getQuokkaBounds().width / 2, 2) + Math.pow(arrow.getArrowBounds().y + arrow.getArrowBounds().height / 2 - quokka.getQuokkaBounds().y - quokka.getQuokkaBounds().height / 2, 2)) <= HAWKSIGHT) {
                    arrow.move(true, dt, quokka.getPosition());
                } else {
                    arrow.move(false, dt, quokka.getPosition());
                }
            }
            for (Wall wall : walls) {
                boolean moveWall = false;
                if (wall.hasSwitch()) {
                    for (Obstacle wallSwitch : wall.getWallSwitches()) {
                        if (wallSwitch.collides(quokka.getQuokkaBounds())) {
                            switches.removeIndex(switches.indexOf(wallSwitch, false));
                            wallSwitch.dispose();
                            moveWall = true;
                        }
                    }
                }
                if (moveWall) {
                    wall.setPosWall(wall.getPosWall().x, wall.getPosWall().y + wall.getWallMove());
                    wall.setWallBounds(wall.getPosWall().x, wall.getPosWall().y, wall.getTexture().getWidth(), wall.getTexture().getHeight());
                }
                if (wall.collides(quokka.getQuokkaBounds())) {
                    if (!touchingWall) {
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
                        } else {
                            quokka.setVelocity(resultVector(quokka.getVelocity(), new Vector3(wall.getWallBounds().getX(), wall.getWallBounds().getY() + wall.getWallBounds().getHeight(), 0), new Vector3(wall.getWallBounds().getX() + wall.getWallBounds().getWidth(), wall.getWallBounds().getY() + wall.getWallBounds().getHeight(), 0)));
                        }
                    }
                } else {
                    touchingWall = false;
                }
            }
            for (MoveWall moveWall : moveWalls) {
                if (moveWall.collides(quokka.getQuokkaBounds())) {
                    if (!touchingWall) {
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
                        } else {
                            quokka.setVelocity(resultVector(quokka.getVelocity(), new Vector3(moveWall.getWallBounds().getX(), moveWall.getWallBounds().getY() + moveWall.getWallBounds().getHeight(), 0), new Vector3(moveWall.getWallBounds().getX() + moveWall.getWallBounds().getWidth(), moveWall.getWallBounds().getY() + moveWall.getWallBounds().getHeight(), 0)));
                        }
                    }
                } else {
                    touchingWall = false;
                }
            }
            justPlanetTemp = false;
            for (Obstacle planet : planets) {
                if (planet.collides(quokka.getQuokkaBounds())) {
                    circleCenter.set(planet.getPosObstacle().x + planet.getObstacleBounds().getWidth() / 2, planet.getPosObstacle().y + planet.getObstacleBounds().getHeight() / 2);
                    adjustedCenter.set(circleCenter.x - quokka.getPosition().x, circleCenter.y - quokka.getPosition().y);
                    quokkaSide.set(quokka.getQuokkaBounds().getWidth(), 0);
                    planetProj.set(quokkaSide.scl(adjustedCenter.dot(quokkaSide) / quokkaSide.dot(quokkaSide)));
                    if (0 < planetProj.x && planetProj.x < quokka.getQuokkaBounds().getWidth()) {
                        intersectionPoint.set(planetProj.x, planetProj.y);
                    } else if (Math.abs(planetProj.x - quokka.getQuokkaBounds().getWidth()) < Math.abs(planetProj.x)) {
                        intersectionPoint.set(quokka.getQuokkaBounds().getWidth(), 0);
                    } else {
                        intersectionPoint.set(0, 0);
                    }
                    intersectionPoint.add(quokka.getPosition().x, quokka.getPosition().y);

                    adjustedCenter.set(circleCenter.x - quokka.getPosition().x, circleCenter.y - quokka.getPosition().y - quokka.getQuokkaBounds().getHeight());
                    quokkaSide.set(quokka.getQuokkaBounds().getWidth(), 0);
                    planetProj.set(quokkaSide.scl(adjustedCenter.dot(quokkaSide) / quokkaSide.dot(quokkaSide)));
                    if (0 < planetProj.x && planetProj.x < quokka.getQuokkaBounds().getWidth()) {
                        intersectionPointTemp.set(planetProj.x, planetProj.y);
                    } else if (Math.abs(planetProj.x - quokka.getQuokkaBounds().getWidth()) < Math.abs(planetProj.x)) {
                        intersectionPointTemp.set(quokka.getQuokkaBounds().getWidth(), 0);
                    } else {
                        intersectionPointTemp.set(0, 0);
                    }
                    intersectionPointTemp.add(quokka.getPosition().x, quokka.getPosition().y + quokka.getQuokkaBounds().getHeight());
                    if (Math.sqrt(Math.pow(intersectionPointTemp.y - circleCenter.y, 2) + Math.pow(intersectionPointTemp.x - circleCenter.x, 2)) < Math.sqrt(Math.pow(intersectionPoint.y - circleCenter.y, 2) + Math.pow(intersectionPoint.x - circleCenter.x, 2))) {
                        intersectionPoint.set(intersectionPointTemp);
                    }

                    adjustedCenter.set(circleCenter.x - quokka.getPosition().x, circleCenter.y - quokka.getPosition().y);
                    quokkaSide.set(0, quokka.getQuokkaBounds().getHeight());
                    planetProj.set(quokkaSide.scl(adjustedCenter.dot(quokkaSide) / quokkaSide.dot(quokkaSide)));
                    if (0 < planetProj.y && planetProj.y < quokka.getQuokkaBounds().getHeight()) {
                        intersectionPointTemp.set(planetProj.x, planetProj.y);
                    } else if (Math.abs(planetProj.y - quokka.getQuokkaBounds().getHeight()) < Math.abs(planetProj.y)) {
                        intersectionPointTemp.set(0, quokka.getQuokkaBounds().getHeight());
                    } else {
                        intersectionPointTemp.set(0, 0);
                    }
                    intersectionPointTemp.add(quokka.getPosition().x, quokka.getPosition().y);
                    if (Math.sqrt(Math.pow(intersectionPointTemp.y - circleCenter.y, 2) + Math.pow(intersectionPointTemp.x - circleCenter.x, 2)) < Math.sqrt(Math.pow(intersectionPoint.y - circleCenter.y, 2) + Math.pow(intersectionPoint.x - circleCenter.x, 2))) {
                        intersectionPoint.set(intersectionPointTemp);
                    }

                    adjustedCenter.set(circleCenter.x - quokka.getPosition().x - quokka.getQuokkaBounds().getWidth(), circleCenter.y - quokka.getPosition().y);
                    quokkaSide.set(0, quokka.getQuokkaBounds().getHeight());
                    planetProj.set(quokkaSide.scl(adjustedCenter.dot(quokkaSide) / quokkaSide.dot(quokkaSide)));
                    if (0 < planetProj.y && planetProj.y < quokka.getQuokkaBounds().getHeight()) {
                        intersectionPointTemp.set(planetProj.x, planetProj.y);
                    } else if (Math.abs(planetProj.y - quokka.getQuokkaBounds().getHeight()) < Math.abs(planetProj.y)) {
                        intersectionPointTemp.set(0, quokka.getQuokkaBounds().getHeight());
                    } else {
                        intersectionPointTemp.set(0, 0);
                    }
                    intersectionPointTemp.add(quokka.getPosition().x + quokka.getQuokkaBounds().getWidth(), quokka.getPosition().y);
                    if (Math.sqrt(Math.pow(intersectionPointTemp.y - circleCenter.y, 2) + Math.pow(intersectionPointTemp.x - circleCenter.x, 2)) < Math.sqrt(Math.pow(intersectionPoint.y - circleCenter.y, 2) + Math.pow(intersectionPoint.x - circleCenter.x, 2))) {
                        intersectionPoint.set(intersectionPointTemp);
                    }
                    if (Math.sqrt(Math.pow(intersectionPoint.y - circleCenter.y, 2) + Math.pow(intersectionPoint.x - circleCenter.x, 2)) < planet.getObstacleBounds().getWidth() / 2) {
                        justPlanetTemp = true;
                    }
                    if (!justPlanet) {
                        if (justPlanetTemp) {
                            velocityTemp.set(quokka.getVelocity());
                            gradientVector.set(2 * (intersectionPoint.x - circleCenter.x), 2 * (intersectionPoint.y - circleCenter.y), 0);
                            gradientVector.nor();
                            velocityTemp2.set(velocityTemp.sub((gradientVector).scl(2 * (velocityTemp.dot(gradientVector)))));
                            quokka.setVelocity(velocityTemp2);
                        }
                    }
                }
                planetDistance.set(quokka.getPosition().x + quokka.getTexture().getWidth() / 2 - planet.getPosObstacle().x - planet.getTexture().getWidth() / 2, quokka.getPosition().y + quokka.getTexture().getHeight() / 2 - planet.getPosObstacle().y - planet.getTexture().getWidth() / 2, 0);
                double planetMagnitude = Math.sqrt(Math.pow(planetDistance.x, 2) + Math.pow(planetDistance.y, 2));
                if (planetMagnitude != 0) {
                    planetDistance.scl(Math.round(GOODGRAV / Math.pow(planetMagnitude, GRAVPOW)));
                }
                quokka.getGravity().add(planetDistance.x, planetDistance.y, 0);
            }
            justPlanet = justPlanetTemp;
            quokka.getGravity().set(quokka.getGravity().x / PLANETSCALER, quokka.getGravity().y / PLANETSCALER, 0);
            if (quokka.getGravity().x == 0 && quokka.getGravity().y == 0) {
                quokka.getGravity().set(0, -13, 0);
            }
            if (shouldFall) {
                quokka.update(dt);
            }
            for (EvilCloud cloud : clouds) {
                if (cloud.collides(quokka.getQuokkaBounds())) {
                    gsm.set(new PlayState(gsm, level));
                    break;
                }
            }
            for (BonusQuokka bonusQuokka : bonusQuokkas) {
                if (bonusQuokka.collides(quokka.getQuokkaBounds())) {
                    if (!collectedQuokkas.get(bonusQuokkas.indexOf(bonusQuokka, false))) {
                        collectedQuokkas.set(bonusQuokkas.indexOf(bonusQuokka, false), true);
                        bonusQuokka.dispose();
                    }
                }
            }
            if(vineCheck) {
                for (Vine vine : vines) {
                    if (vine.collides(quokka.getQuokkaBounds())) {
                        vineDraw = false;
                        lineDraw = false;
                        shouldFall = false;
                        lineCheck = true;
                        vineCheck = false;
                        clickPos.set(-100, -100, 0);
                        clickPos2.set(-100, -100, 0);
                        quokka.getPosition().set(vine.getQuokkaX(), 650, 0);
                        quokka.getVelocity().set(0, 0, 0);
                        layer = vine.getLayer();
                    }
                }
            }
            quokka.getGravity().set(0, 0, 0);
            if (nullZones.size > 0 && lineCheck) {
                if (Math.abs(clickPos2.x - clickPos.x) < SHRINKRATE) {
                    clickPos2.set(-100, -100, 0);
                    clickPos.set(-100, -100, 0);
                } else {
                    double linAng = Math.atan((clickPos2.y - clickPos.y) / (clickPos2.x - clickPos.x));
                    if (clickPos2.x > clickPos.x) {
                        clickPos2.x -= SHRINKRATE * Math.cos(linAng);
                        clickPos2.y -= SHRINKRATE * Math.sin(linAng);
                        clickPos.x += SHRINKRATE * Math.cos(linAng);
                        clickPos.y += SHRINKRATE * Math.sin(linAng);
                    } else {
                        clickPos2.x += SHRINKRATE * Math.cos(linAng);
                        clickPos2.y += SHRINKRATE * Math.sin(linAng);
                        clickPos.x -= SHRINKRATE * Math.cos(linAng);
                        clickPos.y -= SHRINKRATE * Math.sin(linAng);
                    }
                }
            }
            if (quokka.getPosition().y <= cam.position.y - cam.viewportHeight / 2) {
                if (moveWalls.size == 0) {
                    gsm.set(new PlayState(gsm, level));
                }
            }
            if (layer == finalLayer) {
                if (happyCloud.collides(quokka.getQuokkaBounds())) {
                    gsm.set(new MenuState(gsm, level + 1));
                }
            }
            cam.update();
        }
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
        if(moveWalls.size!=0){
            sb.setColor(DEPTHSCALER * (quokka.getPosition().y - 650) + 1, DEPTHSCALER * (quokka.getPosition().y - 650) + 1, DEPTHSCALER * (quokka.getPosition().y - 650) + 1, 1f);
        }
        sb.draw(levelBackground, levelBackgroundPos1.x, levelBackgroundPos1.y);
        sb.draw(levelBackground, levelBackgroundPos2.x, levelBackgroundPos2.y);
        sb.draw(levelBackground, levelBackgroundPos3.x, levelBackgroundPos3.y);
        sb.draw(levelBackground, levelBackgroundPos4.x, levelBackgroundPos4.y);
        for(BonusQuokka bonusQuokka : bonusQuokkas){
            if(!collectedQuokkas.get(bonusQuokkas.indexOf(bonusQuokka, false))) {
                sb.draw(bonusQuokka.getTexture(), bonusQuokka.getPosQuokka().x, bonusQuokka.getPosQuokka().y);
            }
        }
        for(Obstacle wallSwitch: switches){
            sb.draw(wallSwitch.getTexture(), wallSwitch.getPosObstacle().x, wallSwitch.getPosObstacle().y);
        }
        for (Hawk hawk : hawks){
            sb.draw(hawk.getTexture(), hawk.getPosHawk().x, hawk.getPosHawk().y);
        }
        for(Arrow arrow : arrows){
            sb.draw(arrow.getTexture(), arrow.getPosArrow().x, arrow.getPosArrow().y);
        }
        if(layer == finalLayer) {
            sb.draw(happyCloud.getTexture(), happyCloud.getPosCloud().x, happyCloud.getPosCloud().y);
        }
        sb.draw(backButton.getTexture(), backButton.getPosButton().x, backButton.getPosButton().y);
        sb.draw(pauseButton.getTexture(), pauseButton.getPosButton().x, pauseButton.getPosButton().y);
        sb.end();
        if(vineDraw) {
            if (clickPos2.y != -100) {
                shapeRenderer.setColor(Color.BROWN);
                shapeRenderer.setProjectionMatrix(cam.combined);
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                shapeRenderer.line(clickPos, clickPos2);
                shapeRenderer.end();
            } else if (clickPosTemp.y != -100) {
                shapeRenderer.setColor(Color.YELLOW);
                shapeRenderer.setProjectionMatrix(cam.combined);
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                shapeRenderer.line(clickPos, clickPosTemp);
                shapeRenderer.end();
            }
        }
        sb.begin();
        for(Obstacle nullZone : nullZones){
            sb.draw(nullZone.getTexture(), nullZone.getPosObstacle().x, nullZone.getPosObstacle().y);
        }
        sb.draw(quokka.getTexture(), quokka.getPosition().x, quokka.getPosition().y);
        for(Wall wall : walls){
            sb.draw(wall.getTexture(), wall.getPosWall().x, wall.getPosWall().y);
        }
        for(MoveWall moveWall : moveWalls){
            sb.draw(moveWall.getTexture(), moveWall.getPosWall().x, moveWall.getPosWall().y);
        }
        for(EvilCloud cloud: clouds) {
            sb.draw(cloud.getTexture(), cloud.getPosCloud().x, cloud.getPosCloud().y);
        }
        for(Obstacle planet : planets){
            sb.draw(planet.getTexture(), planet.getPosObstacle().x, planet.getPosObstacle().y);
        }
        for(Vine vine : vines){
            sb.draw(vine.getTexture(), vine.getPosVine().x, vine.getPosVine().y);
        }
        sb.end();
        sb.setColor(1f, 1f, 1f, 1f);
    }

    @Override
    public void dispose() {
        levelBackground.dispose();
        quokka.dispose();
        for(EvilCloud cloud : clouds){
            cloud.dispose();
        }
        if(layer == finalLayer) {
            happyCloud.dispose();
        }
        for(Wall wall : walls){
            wall.dispose();
        }
        for(BonusQuokka bonusQuokka : bonusQuokkas){
            bonusQuokka.dispose();
        }
        for(Obstacle planet : planets){
            planet.dispose();
        }
        for(Obstacle nullZone : nullZones){
            nullZone.dispose();
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
        for(Array<Vine> vineArray : layerVines){
            for(Vine vine: vineArray){
                vine.dispose();
            }
        }
        for(Texture layerTexture : layerTextures){
            layerTexture.dispose();
        }
        for(Arrow arrow: arrows){
            arrow.dispose();
        }
        backButton.dispose();
        pauseButton.dispose();
        shapeRenderer.dispose();
    }

    public void levelInit(int level){
        backButton = new Button(new Texture("level4Button.png"), 0, 500, 0);
        pauseButton = new Button(new Texture("level4Button.png"), 0, 300, 0);
        walls.add(new Wall(-1000, -220, "wall.png"));
        walls.add(new Wall(-1000, 375, "wall.png"));
        switch(level){
            /*case 1:
                levelBackground = new Texture("level1Background.png");
                switches.add(new Obstacle(200, 100, "wallSwitch.png"));
                walls.add(new Wall(400, -220, switches, -200));
                walls.add(new Wall(400, 375));
                happyCloud = new HappyCloud(10000,200);
                break;*/
            case 1:
                levelBackground = new Texture("level1Background.png");
                nullZones.add(new Obstacle(300,0, "spaceBackground.png"));
                happyCloud = new HappyCloud(10000,200);
                break;
            case 2:
                levelBackground = new Texture("level2Background.png");
                walls.add(new Wall(500,-180, "wall.png"));
                walls.add(new Wall(1400,400, "wall.png"));
                happyCloud = new HappyCloud(2060, 200);
                bonusQuokkas.add(new BonusQuokka(20,200));
                break;
            case 3:
                levelBackground = new Texture("level3Background.png");
                walls.add(new Wall(350, -80, "wall.png"));
                walls.add(new Wall(850, -230, "wall.png"));
                clouds.add(new EvilCloud(1200, 460));
                bonusQuokkas.add(new BonusQuokka(1200, 10));
                happyCloud = new HappyCloud(500, 50);
                break;
            case 4:
                levelBackground = new Texture("level1Background.png");
                walls.add(new Wall(300,500, "wall.png"));
                walls.add(new Wall(300, -380, "wall.png"));
                walls.add(new Wall (600, -380, "wall.png"));
                bonusQuokkas.add(new BonusQuokka(600, 400));
                clouds.add(new EvilCloud(800, 500));
                walls.add(new Wall(1300, 600, "wall.png"));
                clouds.add(new EvilCloud(1300, 50));
                happyCloud = new HappyCloud(1600, 300);
                break;
            case 5:
                levelBackground = new Texture("spaceBackground.png");
                planets.add(new Obstacle(300, 200, "greenPlanet.png"));
                planets.add(new Obstacle(900, 400, "greenPlanet.png"));
                happyCloud = new HappyCloud(1500, 300);
                planets.add(new Obstacle(1900, 400, "greenPlanet.png"));
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
                walls.add(new Wall(350, 300, "wall.png"));
                bonusQuokkas.add(new BonusQuokka(500, 400));
                walls.add(new Wall(750,300, "wall.png"));
                happyCloud = new HappyCloud(1250, 200);
                break;
            case 7:
                levelBackground = new Texture("oceanBackground.png");
                moveWalls.add(new MoveWall(10000,10000,0,0,0));
                happyCloud = new HappyCloud(10000, 10000);
                break;
            case 8:
                layerTextures.add(new Texture("level1Background.png"));
                layerTextures.add(new Texture("level2Background.png"));
                layerTextures.add(new Texture("level3Background.png"));
                vines.add(new Vine(600, 500, 1, 50));
                layerVines.add(new Array<Vine>(vines));
                vines.clear();
                vines.add(new Vine (900, 600, 0, 50));
                layerVines.add(new Array<Vine>(vines));
                happyCloud = new HappyCloud(1300, 6);
                break;
            case 9:
                levelBackground = new Texture("level1Background.png");
                walls.add(new Wall(300,600, "wall.png"));
                walls.add(new Wall(300, -280, "wall.png"));
                walls.add(new Wall(700, 700, "wall.png"));
                walls.add(new Wall(700, -180, "wall.png"));
                walls.add(new Wall(1100, 500, "wall.png"));
                walls.add(new Wall(1100, -380, "wall.png"));
                happyCloud = new HappyCloud(1300, 5);
                walls.add(new Wall(1700, 500, "wall.png"));
                walls.add(new Wall(1700, -380, "wall.png"));
                bonusQuokkas.add(new BonusQuokka(1900, 300));
                break;
            case 10:
                levelBackground = new Texture("level3Background.png");
                arrows.add(new Arrow(1100, 1100));
                clouds.add(new EvilCloud(50, 1000));
                happyCloud = new HappyCloud(50, 2000);
                break;

        }
        if(arrows.size > 0){
            for(int i = -220; i < happyCloud.getPosCloud().y; i+=595){
                walls.add(new Wall(-30, i, "wall.png"));
                walls.add(new Wall(cam.viewportWidth - 10, i, "wall.png"));
            }
        }
        collectedQuokkas.setSize(bonusQuokkas.size);
    }

    private void updateBackground(){
        if(cam.position.x - (cam.viewportWidth / 2) > levelBackgroundPos1.x + levelBackground.getWidth()) {
            levelBackgroundPos1.add(levelBackground.getWidth() * 2, 0);
            levelBackgroundPos3.add(levelBackground.getWidth() * 2, 0);
        }
        if(cam.position.x - (cam.viewportWidth / 2) > levelBackgroundPos2.x + levelBackground.getWidth()){
            levelBackgroundPos2.add(levelBackground.getWidth() * 2, 0);
            levelBackgroundPos4.add(levelBackground.getWidth() * 2, 0);
        }
        if((cam.position.x +(cam.viewportWidth / 2) < levelBackgroundPos1.x + levelBackground.getWidth())&&(cam.position.x + (cam.viewportWidth / 2) < levelBackgroundPos2.x + levelBackground.getWidth())) {
            if(levelBackgroundPos2.x > levelBackgroundPos1.x){
                levelBackgroundPos2.sub(levelBackground.getWidth()*2, 0);
                levelBackgroundPos4.sub(levelBackground.getWidth()*2, 0);
            }
            else if(levelBackgroundPos1.x > levelBackgroundPos2.x){
                levelBackgroundPos1.sub(levelBackground.getWidth()*2, 0);
                levelBackgroundPos3.sub(levelBackground.getWidth()*2, 0);
            }
        }
        if(moveWalls.size!=0 || arrows.size != 0){
            if(cam.position.y - (cam.viewportHeight / 2) > levelBackgroundPos1.y + levelBackground.getHeight()) {
                levelBackgroundPos1.add(0, levelBackground.getHeight() * 2);
                levelBackgroundPos2.add(0, levelBackground.getHeight() * 2);
            }
            if(cam.position.y - (cam.viewportHeight / 2) > levelBackgroundPos3.y + levelBackground.getHeight()){
                levelBackgroundPos3.add(0, levelBackground.getHeight() * 2);
                levelBackgroundPos4.add(0, levelBackground.getHeight() * 2);
            }
            if((cam.position.y +(cam.viewportHeight / 2) < levelBackgroundPos1.y + levelBackground.getHeight())&&(cam.position.y + (cam.viewportHeight / 2) < levelBackgroundPos3.y + levelBackground.getHeight())) {
                if(levelBackgroundPos3.y > levelBackgroundPos1.y){
                    levelBackgroundPos3.sub(0, levelBackground.getHeight() * 2);
                    levelBackgroundPos4.sub(0, levelBackground.getHeight() * 2);
                }
                else if(levelBackgroundPos1.y > levelBackgroundPos3.y){
                    levelBackgroundPos1.sub(0, levelBackground.getHeight() * 2);
                    levelBackgroundPos2.sub(0, levelBackground.getHeight() * 2);
                }
            }
        }
    }

    private Vector3 resultVector(Vector3 velocity, Vector3 point1, Vector3 point2) {
        velocityTemp.set(velocity);
        normal.set(point1.y-point2.y,point2.x-point1.x,0);
        normal.nor();
        velocityTemp2.set(velocityTemp.sub((normal).scl(2*(velocityTemp.dot(normal)))));
        if(moveWalls.size!=0){
            velocityTemp2.set(Math.round(velocityTemp2.x * OCEANSLOW), Math.round(velocityTemp2.y * OCEANSLOW), 0);
        }
        return velocityTemp2;
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
        touchInput.set(screenX, screenY, 0);
        cam.unproject(touchInput);
        justPaused = false;
        if(backButton.getButtonBounds().contains(touchInput.x, touchInput.y)){
            gsm.set(new MenuState(gsm, level));
        }
        else if(pauseButton.getButtonBounds().contains(touchInput.x, touchInput.y)){
            paused = !paused;
            justPaused = true;
        }
        else{
            vineDraw = true;
            vineCheck = true;
            lineDraw = true;
            shouldFall = true;
            lineCheck = false;
            paused = false;
            clickPos2.set(screenX, -100, 0);
            clickPos.set(screenX, screenY, 0);
            clickPos.set(cam.unproject(clickPos));
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(!paused && !justPaused) {
            if (lineDraw) {
                vineDraw = true;
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
