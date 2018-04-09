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
import com.quokkabounce.game.sprites.Meteor;
import com.quokkabounce.game.sprites.MoveWall;
import com.quokkabounce.game.sprites.Obstacle;
import com.quokkabounce.game.sprites.Quokka;
import com.quokkabounce.game.sprites.TallDino;
import com.quokkabounce.game.sprites.Vine;
import com.quokkabounce.game.sprites.Wall;

/**
 * Created by Eric on 8/29/2017.
 */

public class PlayState extends State implements InputProcessor{
    private static final int HAWKSIGHT = 400;
    private static final int ARROWHEIGHT = 125;
    private static final int GOODGRAV = -150000;
    private static final int PLANETSCALER = 30;
    private static final float DEPTHSCALER = 0.0001f;
    private static final double OCEANSLOW = 0.98;
    private static final float GRAVPOW = 2f;
    private static final float VIEWPORT_SCALER = 1.6f;
    private static final int SHRINKRATE = 3;
    private static final int TOWERFALL = 100;
    private static final int WALLSPEED = 3;

    private Quokka quokka;
    private Button backButton, pauseButton;
    private Texture levelBackground;
    private Vector2 levelBackgroundPos1, levelBackgroundPos2, levelBackgroundPos3, levelBackgroundPos4, intersectionPoint, intersectionPointTemp, circleCenter, quokkaSide, adjustedCenter, planetProj, clickPos2d, clickPos2d2, xdiff, ydiff, tempDet, tempWall;
    private Vector3 clickPos, clickPos2, velocityTemp, velocityTemp2, normal, clickPosTemp, planetDistance, gradientVector, touchInput, towerVel;
    private ShapeRenderer shapeRenderer;
    private HappyCloud happyCloud;
    private float currentDT, iniPot, shortestDistance;
    private int layer, finalLayer;
    private boolean shouldFall, touchingWall, lineCheck, lineDraw, justHit, vineDraw, justHitTemp, outZone, justPlanet, justPlanetTemp, paused, justPaused, vineCheck, hasCollided, smallMove, smallBounce, hitWall, firstSide, shouldMove, hasEdgeCollided;

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
    private Array<Meteor> meteors;
    private Array<TallDino> tallDinos;
    private BooleanArray collectedQuokkas;
    private boolean hitLeft[], hitRight[], hitBottom[], hitTop[];
    private Vector2 hitSide[];
    private String hitCorner;

    public PlayState(GameStateManager gsm, int world, int level) {
        super(gsm, world, level);
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
        meteors = new Array<Meteor>();
        tallDinos = new Array<TallDino>();
        layerTextures = new Array<Texture>();
        layerVines = new Array<Array<Vine>>();
        hitLeft = new boolean[4];
        hitRight = new boolean[4];
        hitBottom = new boolean[4];
        hitTop = new boolean[4];
        hitSide = new Vector2[2];
        layer = 0;
        finalLayer = 0;
        if(planets.size == 0) {
            cam.setToOrtho(false, Math.round(QuokkaBounce.WIDTH * VIEWPORT_SCALER), Math.round(QuokkaBounce.HEIGHT * VIEWPORT_SCALER));
        }
        else{
            cam.setToOrtho(false, Math.round(QuokkaBounce.WIDTH * VIEWPORT_SCALER), Math.round(QuokkaBounce.HEIGHT * VIEWPORT_SCALER));
        }
        levelInit(world, level);
        shouldFall = false;
        lineDraw = false;
        vineDraw = true;
        lineCheck = false;
        touchingWall = false;
        justHit = false;
        justPlanet = false;
        paused = false;
        smallBounce = false;
        shouldMove = false;
        justPaused = false;
        hasCollided = false;
        hasEdgeCollided = false;
        smallMove = false;
        vineCheck = true;
        Gdx.input.setInputProcessor(this);
        if(world!= 3) {
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
        xdiff = new Vector2();
        ydiff = new Vector2();
        tempDet = new Vector2();
        tempWall = new Vector2();
        clickPos = new Vector3(0,0,0);
        clickPos2d = new Vector2(0,0);
        clickPos2 = new Vector3(0,-100,0);
        clickPos2d2 = new Vector2(0,-100);
        clickPosTemp = new Vector3(0,-100,0);
        velocityTemp = new Vector3(0,0,0);
        velocityTemp2 = new Vector3(0,0,0);
        normal = new Vector3(0,0,0);
        planetDistance = new Vector3(0,0,0);
        towerVel = new Vector3(0, TOWERFALL, 0);
        iniPot = 0f;
        if(planets.size>0){
            for(Obstacle planet : planets){
                planetDistance.set(quokka.getPosition().x + quokka.getTexture().getWidth() / 2 - planet.getPosObstacle().x - planet.getTexture().getWidth() / 2, quokka.getPosition().y + quokka.getTexture().getHeight() / 2 - planet.getPosObstacle().y - planet.getTexture().getWidth() / 2, 0);
                double planetMagnitude = Math.sqrt(Math.pow(planetDistance.x, 2) + Math.pow(planetDistance.y, 2));
                iniPot+=(1 / planetMagnitude);
            }
            iniPot*=(GOODGRAV * PLANETSCALER);
        }
        else{
            iniPot = 13 * quokka.getPosition().y;
        }
    }

    @Override
    public void pause(){
        paused = true;
    }

    @Override
    public void update(float dt) {
        if(!paused) {
            smallBounce = false;
            if (layerVines.size > 0) {
                levelBackground = (layerTextures.get(layer));
                vines.clear();
                vines.addAll(layerVines.get(layer));
            }
            currentDT = dt;
            updateBackground();
            if (moveWalls.size != 0) {
                cam.position.y = quokka.getPosition().y;
                if(lineDraw) {
                    clickPosTemp.set(clickPosTemp.x, clickPosTemp.y + quokka.getBottomLeft2().y - quokka.getBottomLeft().y, 0);
                }
            }
            justHitTemp = false;
            if (lineCheck && !hitWall) {
                outZone = false;
                if(!justHit && !hasEdgeCollided) {
                    if (((quokka.getPosition().x > clickPos.x) && (quokka.getPosition().x < clickPos2.x)) || ((quokka.getPosition().x < clickPos.x) && (quokka.getPosition().x > clickPos2.x))) {
                        if (quokka.getQuokkaBounds().contains(quokka.getPosition().x, lineY(quokka.getPosition().x))) {
                            outZone = true;
                            for (Obstacle nullZone : nullZones) {
                                if (nullZone.getObstacleBounds().contains(quokka.getPosition().x, lineY(quokka.getPosition().x))) {
                                    outZone = false;
                                }
                            }
                        }
                    }
                    if(!outZone) {
                        if ((((quokka.getPosition().x + quokka.getTexture().getWidth()) > clickPos.x) && ((quokka.getPosition().x + quokka.getTexture().getWidth()) < clickPos2.x)) || (((quokka.getPosition().x + quokka.getTexture().getWidth()) < clickPos.x) && ((quokka.getPosition().x + quokka.getTexture().getWidth()) > clickPos2.x))) {
                            if (quokka.getQuokkaBounds().contains(quokka.getPosition().x + quokka.getTexture().getWidth(), lineY(quokka.getPosition().x + quokka.getTexture().getWidth()))) {
                                outZone = true;
                                for (Obstacle nullZone : nullZones) {
                                    if (nullZone.getObstacleBounds().contains(quokka.getPosition().x + quokka.getTexture().getWidth(), lineY(quokka.getPosition().x + quokka.getTexture().getWidth()))) {
                                        outZone = false;
                                    }
                                }
                            }
                        }
                    }
                    if(!outZone) {
                        if (quokka.getQuokkaBounds().contains(clickPos.x, clickPos.y)) {
                            outZone = true;
                            for (Obstacle nullZone : nullZones) {
                                if (nullZone.getObstacleBounds().contains(clickPos.x, clickPos.y)) {
                                    outZone = false;
                                }
                            }
                        }
                        else if (quokka.getQuokkaBounds().contains(clickPos2.x, clickPos2.y)) {
                            outZone = true;
                            for (Obstacle nullZone : nullZones) {
                                if (nullZone.getObstacleBounds().contains(clickPos2.x, clickPos2.y)) {
                                    outZone = false;
                                }
                            }
                        }
                    }
                    if(!outZone) {
                        if (((quokka.getPosition().x < clickPos.x) && (clickPos.x < (quokka.getPosition().x + quokka.getQuokkaBounds().getWidth()))) && ((quokka.getPosition().x < clickPos2.x) && (clickPos2.x < (quokka.getPosition().x + quokka.getQuokkaBounds().getWidth())))) {
                            if (doIntersect(quokka.getBottomLeft(), quokka.getBottomRight(), clickPos2d, clickPos2d2)) {
                                outZone = true;
                                smallMove = true;
                                for (Obstacle nullZone : nullZones) {
                                    if (nullZone.getObstacleBounds().contains(intersectionPoint(quokka.getBottomLeft(), quokka.getBottomRight(), clickPos2d, clickPos2d2))) {
                                        outZone = false;
                                        smallMove = false;
                                    }
                                }
                            } else if (doIntersect(quokka.getUpperLeft(), quokka.getUpperRight(), clickPos2d, clickPos2d2)) {
                                outZone = true;
                                smallMove = true;
                                for (Obstacle nullZone : nullZones) {
                                    if (nullZone.getObstacleBounds().contains(intersectionPoint(quokka.getUpperLeft(), quokka.getUpperRight(), clickPos2d, clickPos2d2))) {
                                        outZone = false;
                                        smallMove = false;
                                    }
                                }
                            }
                        }
                    }
                }
                else{
                    if (((quokka.getPosition().x > clickPos.x) && (quokka.getPosition().x < clickPos2.x)) || ((quokka.getPosition().x < clickPos.x) && (quokka.getPosition().x > clickPos2.x))) {
                        if (quokka.getQuokkaBounds().contains(quokka.getPosition().x, lineY(quokka.getPosition().x))) {
                            justHitTemp = true;
                            for (Obstacle nullZone : nullZones) {
                                if (nullZone.getObstacleBounds().contains(quokka.getPosition().x, lineY(quokka.getPosition().x))) {
                                    justHitTemp = false;
                                }
                            }
                        }
                    }
                    if(!justHitTemp) {
                        if ((((quokka.getPosition().x + quokka.getTexture().getWidth()) > clickPos.x) && ((quokka.getPosition().x + quokka.getTexture().getWidth()) < clickPos2.x)) || (((quokka.getPosition().x + quokka.getTexture().getWidth()) < clickPos.x) && ((quokka.getPosition().x + quokka.getTexture().getWidth()) > clickPos2.x))) {
                            if (quokka.getQuokkaBounds().contains(quokka.getPosition().x + quokka.getTexture().getWidth(), lineY(quokka.getPosition().x + quokka.getTexture().getWidth()))) {
                                justHitTemp = true;
                                for (Obstacle nullZone : nullZones) {
                                    if (nullZone.getObstacleBounds().contains(quokka.getPosition().x + quokka.getTexture().getWidth(), lineY(quokka.getPosition().x + quokka.getTexture().getWidth()))) {
                                        justHitTemp = false;
                                    }
                                }
                            }
                        }
                    }
                    if(!justHitTemp) {
                        if (quokka.getQuokkaBounds().contains(clickPos.x, clickPos.y)) {
                            justHitTemp = true;
                            for (Obstacle nullZone : nullZones) {
                                if (nullZone.getObstacleBounds().contains(clickPos.x, clickPos.y)) {
                                    justHitTemp = false;
                                }
                            }
                        }
                        else if (quokka.getQuokkaBounds().contains(clickPos2.x, clickPos2.y)) {
                            justHitTemp = true;
                            for (Obstacle nullZone : nullZones) {
                                if (nullZone.getObstacleBounds().contains(clickPos2.x, clickPos2.y)) {
                                    justHitTemp = false;
                                }
                            }
                        }
                    }
                    if(!justHitTemp) {
                        if (((quokka.getPosition().x < clickPos.x) && (clickPos.x < (quokka.getPosition().x + quokka.getQuokkaBounds().getWidth()))) && ((quokka.getPosition().x < clickPos2.x) && (clickPos2.x < (quokka.getPosition().x + quokka.getQuokkaBounds().getWidth())))) {
                            if (doIntersect(quokka.getBottomLeft(), quokka.getBottomRight(), clickPos2d, clickPos2d2)) {
                                justHitTemp = true;
                                smallMove = true;
                                for (Obstacle nullZone : nullZones) {
                                    if (nullZone.getObstacleBounds().contains(intersectionPoint(quokka.getBottomLeft(), quokka.getBottomRight(), clickPos2d, clickPos2d2))) {
                                        justHitTemp = false;
                                        smallMove = false;
                                    }
                                }
                            } else if (doIntersect(quokka.getUpperLeft(), quokka.getUpperRight(), clickPos2d, clickPos2d2)) {
                                justHitTemp = true;
                                smallMove = true;
                                for (Obstacle nullZone : nullZones) {
                                    if (nullZone.getObstacleBounds().contains(intersectionPoint(quokka.getUpperLeft(), quokka.getUpperRight(), clickPos2d, clickPos2d2))) {
                                        justHitTemp = false;
                                        smallMove = false;
                                    }
                                }
                            }
                        }
                    }
                }
                hasEdgeCollided = false;
                if (outZone) {
                    quokka.setVelocity(resultVector(quokka.getVelocity(), clickPos, clickPos2));
                    System.out.println("boing bong");
                    hasCollided = true;
                    justHitTemp = true;
                }
                else if(!hasCollided){
                    clickPos2d.set(clickPos.x, clickPos.y);
                    clickPos2d2.set(clickPos2.x, clickPos2.y);
                    if(doIntersect(quokka.getBottomLeft(), quokka.getBottomLeft2(), clickPos2d, clickPos2d2)){
                        boolean shouldBounce = true;
                        for (Obstacle nullZone : nullZones) {
                            if (nullZone.getObstacleBounds().contains(intersectionPoint(quokka.getBottomLeft(), quokka.getBottomLeft2(), clickPos2d, clickPos2d2))) {
                                shouldBounce = false;
                            }
                        }
                        if(shouldBounce && !doIntersect(quokka.getUpperLeft2(), quokka.getUpperRight2(), clickPos2d, clickPos2d2)) {
                            hasCollided = true;
                            hasEdgeCollided = true;
                            tempWall.set(intersectionPoint(quokka.getBottomLeft(), quokka.getBottomLeft2(), clickPos2d, clickPos2d2));
                            quokka.setPosition(quokka.getPosition().x, tempWall.y);
                            quokka.setVelocity(resultVector(quokka.getVelocity(), clickPos, clickPos2));
                            System.out.println("boing bong 2");
                        }
                    }
                    else if(doIntersect(quokka.getBottomRight(), quokka.getBottomRight2(), clickPos2d, clickPos2d2)){
                        boolean shouldBounce = true;
                        for (Obstacle nullZone : nullZones) {
                            if (nullZone.getObstacleBounds().contains(intersectionPoint(quokka.getBottomRight(), quokka.getBottomRight2(), clickPos2d, clickPos2d2))) {
                                shouldBounce = false;
                            }
                        }
                        if(shouldBounce && !doIntersect(quokka.getUpperLeft2(), quokka.getUpperRight2(), clickPos2d, clickPos2d2)) {
                            hasCollided = true;
                            hasEdgeCollided = true;
                            tempWall.set(intersectionPoint(quokka.getBottomRight(), quokka.getBottomRight2(), clickPos2d, clickPos2d2));
                            quokka.setPosition(quokka.getPosition().x, tempWall.y);
                            quokka.setVelocity(resultVector(quokka.getVelocity(), clickPos, clickPos2));
                            System.out.println("boing bong 3");
                        }
                    }
                    else if(doIntersect(quokka.getUpperLeft(), quokka.getUpperLeft2(), clickPos2d, clickPos2d2)){
                        boolean shouldBounce = true;
                        for (Obstacle nullZone : nullZones) {
                            if (nullZone.getObstacleBounds().contains(intersectionPoint(quokka.getUpperLeft(), quokka.getUpperLeft2(), clickPos2d, clickPos2d2))) {
                                shouldBounce = false;
                            }
                        }
                        if(shouldBounce) {
                            hasCollided = true;
                            hasEdgeCollided = true;
                            tempWall.set(intersectionPoint(quokka.getUpperLeft(), quokka.getUpperLeft2(), clickPos2d, clickPos2d2));
                            quokka.setPosition(quokka.getPosition().x, tempWall.y - quokka.getQuokkaBounds().getHeight());
                            quokka.setVelocity(resultVector(quokka.getVelocity(), clickPos, clickPos2));
                            System.out.println("boing bong 4");
                        }
                    }
                    else if(doIntersect(quokka.getUpperRight(), quokka.getUpperRight2(), clickPos2d, clickPos2d2)){
                        boolean shouldBounce = true;
                        for (Obstacle nullZone : nullZones) {
                            if (nullZone.getObstacleBounds().contains(intersectionPoint(quokka.getUpperRight(), quokka.getUpperRight2(), clickPos2d, clickPos2d2))) {
                                shouldBounce = false;
                            }
                        }
                        if(shouldBounce) {
                            hasCollided = true;
                            hasEdgeCollided = true;
                            tempWall.set(intersectionPoint(quokka.getUpperRight(), quokka.getUpperRight2(), clickPos2d, clickPos2d2));
                            quokka.setPosition(quokka.getPosition().x, tempWall.y - quokka.getQuokkaBounds().getHeight());
                            quokka.setVelocity(resultVector(quokka.getVelocity(), clickPos, clickPos2));
                            System.out.println("boing bong 5");
                        }
                    }
                }
                justHit = justHitTemp;
            }
            if(shouldMove) {
                for (Hawk hawk : hawks) {
                    if (hawk.collides(quokka.getQuokkaBounds())) {
                        gsm.set(new PlayState(gsm, world, level));
                        break;
                    }
                    if (Math.sqrt(Math.pow(hawk.getHawkBounds().x + hawk.getHawkBounds().width / 2 - quokka.getQuokkaBounds().x - quokka.getQuokkaBounds().width / 2, 2) + Math.pow(hawk.getHawkBounds().y + hawk.getHawkBounds().height / 2 - quokka.getQuokkaBounds().y - quokka.getQuokkaBounds().height / 2, 2)) <= HAWKSIGHT) {
                        hawk.move(true, dt, quokka.getPosition());
                    } else {
                        hawk.move(false, dt, quokka.getPosition());
                    }
                }
                for (TallDino tallDino : tallDinos) {
                    if (tallDino.collides(quokka.getQuokkaBounds())) {
                        gsm.set(new PlayState(gsm, world, level));
                        break;
                    }
                    tallDino.move(dt);
                }
                for (Arrow arrow : arrows) {
                    if (arrow.collides(quokka.getQuokkaBounds())) {
                        gsm.set(new PlayState(gsm, world, level));
                        break;
                    }
                    if ((arrow.getArrowBounds().y + arrow.getArrowBounds().height / 2 - quokka.getQuokkaBounds().y - quokka.getQuokkaBounds().height / 2) <= ARROWHEIGHT) {
                        arrow.move(true, dt, quokka.getPosition());
                    } else {
                        arrow.move(false, dt, quokka.getPosition());
                    }
                }
                firstSide = true;
                hitWall = false;
                for (Wall wall : walls) {
                    if (wall.hasSwitch()) {
                        for (Obstacle wallSwitch : wall.getWallSwitches()) {
                            if (wallSwitch.collides(quokka.getQuokkaBounds())) {
                                for (int i = 0; i < walls.size; i++) {
                                    if (walls.get(i).getWallSwitches() != null) {
                                        if (walls.get(i).getWallSwitches().contains(wallSwitch, true)) {
                                            walls.get(i).setMoveWall(true);
                                        }
                                    }
                                }
                                switches.removeIndex(switches.indexOf(wallSwitch, false));
                                wallSwitch.dispose();
                            }
                        }
                    }
                    if (wall.isMoveWall() && (Math.abs(wall.getWallMove() - wall.getPosWall().y) > 0)) {
                        wall.setPosWall(wall.getPosWall().x, wall.getPosWall().y + (wall.getWallMove() - wall.getPosWall().y) / WALLSPEED);
                        wall.setWallBounds(wall.getPosWall().x, wall.getPosWall().y, wall.getTexture().getWidth(), wall.getTexture().getHeight());
                    }
                    hitBottom[0] = false;
                    hitBottom[1] = false;
                    hitTop[0] = false;
                    hitTop[1] = false;
                    hitLeft[0] = false;
                    hitLeft[1] = false;
                    hitRight[0] = false;
                    hitRight[1] = false;
                    if (wall.collides(quokka.getQuokkaBounds())) {
                        Vector3 tempVelocity = new Vector3();
                        tempVelocity.set(quokka.getVelocity());
                        tempVelocity.scl(dt);
                        if (doIntersect(quokka.getUpperLeft(), quokka.getUpperLeft2(), wall.getBl(), wall.getBr())) {
                            hitWall = true;
                            hitBottom[0] = true;
                        }
                        if (doIntersect(quokka.getUpperRight(), quokka.getUpperRight2(), wall.getBl(), wall.getBr())) {
                            hitWall = true;
                            hitBottom[1] = true;
                        }
                        if (doIntersect(quokka.getBottomLeft(), quokka.getBottomLeft2(), wall.getUl(), wall.getUr())) {
                            hitWall = true;
                            hitTop[0] = true;
                        }
                        if (doIntersect(quokka.getBottomRight(), quokka.getBottomRight2(), wall.getUl(), wall.getUr())) {
                            hitWall = true;
                            hitTop[1] = true;
                        }
                        if (!(hitBottom[0] && hitBottom[1]) && !(hitTop[0] && hitTop[1])) {
                            if (doIntersect(quokka.getBottomRight(), quokka.getBottomRight2(), wall.getBl(), wall.getUl())) {
                                hitWall = true;
                                hitLeft[0] = true;
                            }
                            if (doIntersect(quokka.getUpperRight(), quokka.getUpperRight2(), wall.getBl(), wall.getUl())) {
                                hitWall = true;
                                hitLeft[1] = true;
                            }
                            if (doIntersect(quokka.getBottomLeft(), quokka.getBottomLeft2(), wall.getBr(), wall.getUr())) {
                                hitWall = true;
                                hitRight[0] = true;
                            }
                            if (doIntersect(quokka.getUpperLeft(), quokka.getUpperLeft2(), wall.getBr(), wall.getUr())) {
                                hitWall = true;
                                hitRight[1] = true;
                            }
                        }
                        if ((hitLeft[0] && hitLeft[1]) || (hitRight[0] && hitRight[1])) {
                            hitBottom[0] = false;
                            hitBottom[1] = false;
                            hitTop[0] = false;
                            hitTop[1] = false;
                        }
                        for (int i = 0; i < 2; i++) {
                            if (hitBottom[i]) {
                                if (firstSide) {
                                    firstSide = false;
                                    if (i == 0) {
                                        shortestDistance = (float) distance(quokka.getUpperLeft(), intersectionPoint(quokka.getUpperLeft(), quokka.getUpperLeft2(), wall.getBl(), wall.getBr()));
                                        hitCorner = "topLeft";
                                        hitSide[0] = wall.getBl();
                                        hitSide[1] = wall.getBr();
                                    } else if (i == 1) {
                                        shortestDistance = (float) distance(quokka.getUpperRight(), intersectionPoint(quokka.getUpperRight(), quokka.getUpperRight2(), wall.getBl(), wall.getBr()));
                                        hitCorner = "topRight";
                                        hitSide[0] = wall.getBl();
                                        hitSide[1] = wall.getBr();
                                    }
                                } else {
                                    if (i == 0) {
                                        if (distance(quokka.getUpperLeft(), intersectionPoint(quokka.getUpperLeft(), quokka.getUpperLeft2(), wall.getBl(), wall.getBr())) < shortestDistance) {
                                            shortestDistance = (float) distance(quokka.getUpperLeft(), intersectionPoint(quokka.getUpperLeft(), quokka.getUpperLeft2(), wall.getBl(), wall.getBr()));
                                            hitCorner = "topLeft";
                                            hitSide[0] = wall.getBl();
                                            hitSide[1] = wall.getBr();
                                        }
                                    } else if (i == 1) {
                                        if (distance(quokka.getUpperRight(), intersectionPoint(quokka.getUpperRight(), quokka.getUpperRight2(), wall.getBl(), wall.getBr())) < shortestDistance) {
                                            shortestDistance = (float) distance(quokka.getUpperRight(), intersectionPoint(quokka.getUpperRight(), quokka.getUpperRight2(), wall.getBl(), wall.getBr()));
                                            hitCorner = "topRight";
                                            hitSide[0] = wall.getBl();
                                            hitSide[1] = wall.getBr();
                                        }
                                    }
                                }
                            }
                            if (hitLeft[i]) {
                                if (firstSide) {
                                    firstSide = false;
                                    if (i == 0) {
                                        shortestDistance = (float) distance(quokka.getBottomRight(), intersectionPoint(quokka.getBottomRight(), quokka.getBottomRight2(), wall.getBl(), wall.getUl()));
                                        hitCorner = "bottomRight";
                                        hitSide[0] = wall.getBl();
                                        hitSide[1] = wall.getUl();
                                    } else if (i == 1) {
                                        shortestDistance = (float) distance(quokka.getUpperRight(), intersectionPoint(quokka.getUpperRight(), quokka.getUpperRight2(), wall.getBl(), wall.getUl()));
                                        hitCorner = "topRight";
                                        hitSide[0] = wall.getBl();
                                        hitSide[1] = wall.getUl();
                                    }
                                } else {
                                    if (i == 0) {
                                        if (distance(quokka.getBottomRight(), intersectionPoint(quokka.getBottomRight(), quokka.getBottomRight2(), wall.getBl(), wall.getUl())) < shortestDistance) {
                                            shortestDistance = (float) distance(quokka.getBottomRight(), intersectionPoint(quokka.getBottomRight(), quokka.getBottomRight2(), wall.getBl(), wall.getUl()));
                                            hitCorner = "bottomRight";
                                            hitSide[0] = wall.getBl();
                                            hitSide[1] = wall.getUl();
                                        }
                                    } else if (i == 1) {
                                        if (distance(quokka.getUpperRight(), intersectionPoint(quokka.getUpperRight(), quokka.getUpperRight2(), wall.getBl(), wall.getUl())) < shortestDistance) {
                                            shortestDistance = (float) distance(quokka.getUpperRight(), intersectionPoint(quokka.getUpperRight(), quokka.getUpperRight2(), wall.getBl(), wall.getUl()));
                                            hitCorner = "topRight";
                                            hitSide[0] = wall.getBl();
                                            hitSide[1] = wall.getUl();
                                        }
                                    }
                                }
                            }
                            if (hitTop[i]) {
                                if (firstSide) {
                                    firstSide = false;
                                    if (i == 0) {
                                        shortestDistance = (float) distance(quokka.getBottomLeft(), intersectionPoint(quokka.getBottomLeft(), quokka.getBottomLeft2(), wall.getUl(), wall.getUr()));
                                        hitCorner = "bottomLeft";
                                        hitSide[0] = wall.getUl();
                                        hitSide[1] = wall.getUr();
                                    } else if (i == 1) {
                                        shortestDistance = (float) distance(quokka.getBottomRight(), intersectionPoint(quokka.getBottomRight(), quokka.getBottomRight2(), wall.getUl(), wall.getUr()));
                                        hitCorner = "bottomRight";
                                        hitSide[0] = wall.getUl();
                                        hitSide[1] = wall.getUr();
                                    }
                                } else {
                                    if (i == 0) {
                                        if (distance(quokka.getBottomLeft(), intersectionPoint(quokka.getBottomLeft(), quokka.getBottomLeft2(), wall.getUl(), wall.getUr())) < shortestDistance) {
                                            shortestDistance = (float) distance(quokka.getBottomLeft(), intersectionPoint(quokka.getBottomLeft(), quokka.getBottomLeft2(), wall.getUl(), wall.getUr()));
                                            hitCorner = "bottomLeft";
                                            hitSide[0] = wall.getUl();
                                            hitSide[1] = wall.getUr();
                                        }
                                    } else if (i == 1) {
                                        if (distance(quokka.getBottomRight(), intersectionPoint(quokka.getBottomRight(), quokka.getBottomRight2(), wall.getUl(), wall.getUr())) < shortestDistance) {
                                            shortestDistance = (float) distance(quokka.getBottomRight(), intersectionPoint(quokka.getBottomRight(), quokka.getBottomRight2(), wall.getUl(), wall.getUr()));
                                            hitCorner = "bottomRight";
                                            hitSide[0] = wall.getUl();
                                            hitSide[1] = wall.getUr();
                                        }
                                    }
                                }
                            }
                            if (hitRight[i]) {
                                if (firstSide) {
                                    firstSide = false;
                                    if (i == 0) {
                                        shortestDistance = (float) distance(quokka.getBottomLeft(), intersectionPoint(quokka.getBottomLeft(), quokka.getBottomLeft2(), wall.getBr(), wall.getUr()));
                                        hitCorner = "bottomLeft";
                                        hitSide[0] = wall.getBr();
                                        hitSide[1] = wall.getUr();
                                    } else if (i == 1) {
                                        shortestDistance = (float) distance(quokka.getUpperLeft(), intersectionPoint(quokka.getUpperLeft(), quokka.getUpperLeft2(), wall.getBr(), wall.getUr()));
                                        hitCorner = "topLeft";
                                        hitSide[0] = wall.getBr();
                                        hitSide[1] = wall.getUr();
                                    }
                                } else {
                                    if (i == 0) {
                                        if (distance(quokka.getBottomLeft(), intersectionPoint(quokka.getBottomLeft(), quokka.getBottomLeft2(), wall.getBr(), wall.getUr())) < shortestDistance) {
                                            shortestDistance = (float) distance(quokka.getBottomLeft(), intersectionPoint(quokka.getBottomLeft(), quokka.getBottomLeft2(), wall.getBr(), wall.getUr()));
                                            hitCorner = "bottomLeft";
                                            hitSide[0] = wall.getBr();
                                            hitSide[1] = wall.getUr();
                                        }
                                    } else if (i == 1) {
                                        if (distance(quokka.getUpperLeft(), intersectionPoint(quokka.getUpperLeft(), quokka.getUpperLeft2(), wall.getBr(), wall.getUr())) < shortestDistance) {
                                            shortestDistance = (float) distance(quokka.getUpperLeft(), intersectionPoint(quokka.getUpperLeft(), quokka.getUpperLeft2(), wall.getBr(), wall.getUr()));
                                            hitCorner = "topLeft";
                                            hitSide[0] = wall.getBr();
                                            hitSide[1] = wall.getUr();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }// wall loop
                if (walls.size > 0 && hitWall) {
                    if (hitCorner.equals("topLeft")) {
                        tempWall.set(intersectionPoint(quokka.getUpperLeft(), quokka.getUpperLeft2(), hitSide[0], hitSide[1]));
                        quokka.setPosition(quokka.getPosition().x, tempWall.y - quokka.getQuokkaBounds().getHeight());
                    } else if (hitCorner.equals("topRight")) {
                        tempWall.set(intersectionPoint(quokka.getUpperRight(), quokka.getUpperRight2(), hitSide[0], hitSide[1]));
                        quokka.setPosition(quokka.getPosition().x, tempWall.y - quokka.getQuokkaBounds().getHeight());
                    } else if (hitCorner.equals("bottomRight")) {
                        tempWall.set(intersectionPoint(quokka.getBottomRight(), quokka.getBottomRight2(), hitSide[0], hitSide[1]));
                        quokka.setPosition(quokka.getPosition().x, tempWall.y);
                    } else if (hitCorner.equals("bottomLeft")) {
                        tempWall.set(intersectionPoint(quokka.getBottomLeft(), quokka.getBottomLeft2(), hitSide[0], hitSide[1]));
                        quokka.setPosition(quokka.getPosition().x, tempWall.y);
                    }
                    quokka.setVelocity(resultVector(quokka.getVelocity(), hitSide[0], hitSide[1]));
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
                    hitBottom[0] = false;
                    hitBottom[1] = false;
                    hitTop[0] = false;
                    hitTop[1] = false;
                    hitLeft[0] = false;
                    hitLeft[1] = false;
                    hitRight[0] = false;
                    hitRight[1] = false;
                    if (moveWall.collides(quokka.getQuokkaBounds())) {
                        Vector3 tempVelocity = new Vector3();
                        tempVelocity.set(quokka.getVelocity());
                        tempVelocity.scl(dt);
                        if (doIntersect(quokka.getUpperLeft(), quokka.getUpperLeft2(), moveWall.getBl(), moveWall.getBr())) {
                            hitWall = true;
                            hitBottom[0] = true;
                        }
                        if (doIntersect(quokka.getUpperRight(), quokka.getUpperRight2(), moveWall.getBl(), moveWall.getBr())) {
                            hitWall = true;
                            hitBottom[1] = true;
                        }
                        if (doIntersect(quokka.getBottomLeft(), quokka.getBottomLeft2(), moveWall.getUl(), moveWall.getUr())) {
                            hitWall = true;
                            hitTop[0] = true;
                        }
                        if (doIntersect(quokka.getBottomRight(), quokka.getBottomRight2(), moveWall.getUl(), moveWall.getUr())) {
                            hitWall = true;
                            hitTop[1] = true;
                        }
                        if (!(hitBottom[0] && hitBottom[1]) && !(hitTop[0] && hitTop[1])) {
                            if (doIntersect(quokka.getBottomRight(), quokka.getBottomRight2(), moveWall.getBl(), moveWall.getUl())) {
                                hitWall = true;
                                hitLeft[0] = true;
                            }
                            if (doIntersect(quokka.getUpperRight(), quokka.getUpperRight2(), moveWall.getBl(), moveWall.getUl())) {
                                hitWall = true;
                                hitLeft[1] = true;
                            }
                            if (doIntersect(quokka.getBottomLeft(), quokka.getBottomLeft2(), moveWall.getBr(), moveWall.getUr())) {
                                hitWall = true;
                                hitRight[0] = true;
                            }
                            if (doIntersect(quokka.getUpperLeft(), quokka.getUpperLeft2(), moveWall.getBr(), moveWall.getUr())) {
                                hitWall = true;
                                hitRight[1] = true;
                            }
                        }
                        if ((hitLeft[0] && hitLeft[1]) || (hitRight[0] && hitRight[1])) {
                            hitBottom[0] = false;
                            hitBottom[1] = false;
                            hitTop[0] = false;
                            hitTop[1] = false;
                        }
                        for (int i = 0; i < 2; i++) {
                            if (hitBottom[i]) {
                                if (firstSide) {
                                    firstSide = false;
                                    if (i == 0) {
                                        shortestDistance = (float) distance(quokka.getUpperLeft(), intersectionPoint(quokka.getUpperLeft(), quokka.getUpperLeft2(), moveWall.getBl(), moveWall.getBr()));
                                        hitCorner = "topLeft";
                                        hitSide[0] = moveWall.getBl();
                                        hitSide[1] = moveWall.getBr();
                                    } else if (i == 1) {
                                        shortestDistance = (float) distance(quokka.getUpperRight(), intersectionPoint(quokka.getUpperRight(), quokka.getUpperRight2(), moveWall.getBl(), moveWall.getBr()));
                                        hitCorner = "topRight";
                                        hitSide[0] = moveWall.getBl();
                                        hitSide[1] = moveWall.getBr();
                                    }
                                } else {
                                    if (i == 0) {
                                        if (distance(quokka.getUpperLeft(), intersectionPoint(quokka.getUpperLeft(), quokka.getUpperLeft2(), moveWall.getBl(), moveWall.getBr())) < shortestDistance) {
                                            shortestDistance = (float) distance(quokka.getUpperLeft(), intersectionPoint(quokka.getUpperLeft(), quokka.getUpperLeft2(), moveWall.getBl(), moveWall.getBr()));
                                            hitCorner = "topLeft";
                                            hitSide[0] = moveWall.getBl();
                                            hitSide[1] = moveWall.getBr();
                                        }
                                    } else if (i == 1) {
                                        if (distance(quokka.getUpperRight(), intersectionPoint(quokka.getUpperRight(), quokka.getUpperRight2(), moveWall.getBl(), moveWall.getBr())) < shortestDistance) {
                                            shortestDistance = (float) distance(quokka.getUpperRight(), intersectionPoint(quokka.getUpperRight(), quokka.getUpperRight2(), moveWall.getBl(), moveWall.getBr()));
                                            hitCorner = "topRight";
                                            hitSide[0] = moveWall.getBl();
                                            hitSide[1] = moveWall.getBr();
                                        }
                                    }
                                }
                            }
                            if (hitLeft[i]) {
                                if (firstSide) {
                                    firstSide = false;
                                    if (i == 0) {
                                        shortestDistance = (float) distance(quokka.getBottomRight(), intersectionPoint(quokka.getBottomRight(), quokka.getBottomRight2(), moveWall.getBl(), moveWall.getUl()));
                                        hitCorner = "bottomRight";
                                        hitSide[0] = moveWall.getBl();
                                        hitSide[1] = moveWall.getUl();
                                    } else if (i == 1) {
                                        shortestDistance = (float) distance(quokka.getUpperRight(), intersectionPoint(quokka.getUpperRight(), quokka.getUpperRight2(), moveWall.getBl(), moveWall.getUl()));
                                        hitCorner = "topRight";
                                        hitSide[0] = moveWall.getBl();
                                        hitSide[1] = moveWall.getUl();
                                    }
                                } else {
                                    if (i == 0) {
                                        if (distance(quokka.getBottomRight(), intersectionPoint(quokka.getBottomRight(), quokka.getBottomRight2(), moveWall.getBl(), moveWall.getUl())) < shortestDistance) {
                                            shortestDistance = (float) distance(quokka.getBottomRight(), intersectionPoint(quokka.getBottomRight(), quokka.getBottomRight2(), moveWall.getBl(), moveWall.getUl()));
                                            hitCorner = "bottomRight";
                                            hitSide[0] = moveWall.getBl();
                                            hitSide[1] = moveWall.getUl();
                                        }
                                    } else if (i == 1) {
                                        if (distance(quokka.getUpperRight(), intersectionPoint(quokka.getUpperRight(), quokka.getUpperRight2(), moveWall.getBl(), moveWall.getUl())) < shortestDistance) {
                                            shortestDistance = (float) distance(quokka.getUpperRight(), intersectionPoint(quokka.getUpperRight(), quokka.getUpperRight2(), moveWall.getBl(), moveWall.getUl()));
                                            hitCorner = "topRight";
                                            hitSide[0] = moveWall.getBl();
                                            hitSide[1] = moveWall.getUl();
                                        }
                                    }
                                }
                            }
                            if (hitTop[i]) {
                                if (firstSide) {
                                    firstSide = false;
                                    if (i == 0) {
                                        shortestDistance = (float) distance(quokka.getBottomLeft(), intersectionPoint(quokka.getBottomLeft(), quokka.getBottomLeft2(), moveWall.getUl(), moveWall.getUr()));
                                        hitCorner = "bottomLeft";
                                        hitSide[0] = moveWall.getUl();
                                        hitSide[1] = moveWall.getUr();
                                    } else if (i == 1) {
                                        shortestDistance = (float) distance(quokka.getBottomRight(), intersectionPoint(quokka.getBottomRight(), quokka.getBottomRight2(), moveWall.getUl(), moveWall.getUr()));
                                        hitCorner = "bottomRight";
                                        hitSide[0] = moveWall.getUl();
                                        hitSide[1] = moveWall.getUr();
                                    }
                                } else {
                                    if (i == 0) {
                                        if (distance(quokka.getBottomLeft(), intersectionPoint(quokka.getBottomLeft(), quokka.getBottomLeft2(), moveWall.getUl(), moveWall.getUr())) < shortestDistance) {
                                            shortestDistance = (float) distance(quokka.getBottomLeft(), intersectionPoint(quokka.getBottomLeft(), quokka.getBottomLeft2(), moveWall.getUl(), moveWall.getUr()));
                                            hitCorner = "bottomLeft";
                                            hitSide[0] = moveWall.getUl();
                                            hitSide[1] = moveWall.getUr();
                                        }
                                    } else if (i == 1) {
                                        if (distance(quokka.getBottomRight(), intersectionPoint(quokka.getBottomRight(), quokka.getBottomRight2(), moveWall.getUl(), moveWall.getUr())) < shortestDistance) {
                                            shortestDistance = (float) distance(quokka.getBottomRight(), intersectionPoint(quokka.getBottomRight(), quokka.getBottomRight2(), moveWall.getUl(), moveWall.getUr()));
                                            hitCorner = "bottomRight";
                                            hitSide[0] = moveWall.getUl();
                                            hitSide[1] = moveWall.getUr();
                                        }
                                    }
                                }
                            }
                            if (hitRight[i]) {
                                if (firstSide) {
                                    firstSide = false;
                                    if (i == 0) {
                                        shortestDistance = (float) distance(quokka.getBottomLeft(), intersectionPoint(quokka.getBottomLeft(), quokka.getBottomLeft2(), moveWall.getBr(), moveWall.getUr()));
                                        hitCorner = "bottomLeft";
                                        hitSide[0] = moveWall.getBr();
                                        hitSide[1] = moveWall.getUr();
                                    } else if (i == 1) {
                                        shortestDistance = (float) distance(quokka.getUpperLeft(), intersectionPoint(quokka.getUpperLeft(), quokka.getUpperLeft2(), moveWall.getBr(), moveWall.getUr()));
                                        hitCorner = "topLeft";
                                        hitSide[0] = moveWall.getBr();
                                        hitSide[1] = moveWall.getUr();
                                    }
                                } else {
                                    if (i == 0) {
                                        if (distance(quokka.getBottomLeft(), intersectionPoint(quokka.getBottomLeft(), quokka.getBottomLeft2(), moveWall.getBr(), moveWall.getUr())) < shortestDistance) {
                                            shortestDistance = (float) distance(quokka.getBottomLeft(), intersectionPoint(quokka.getBottomLeft(), quokka.getBottomLeft2(), moveWall.getBr(), moveWall.getUr()));
                                            hitCorner = "bottomLeft";
                                            hitSide[0] = moveWall.getBr();
                                            hitSide[1] = moveWall.getUr();
                                        }
                                    } else if (i == 1) {
                                        if (distance(quokka.getUpperLeft(), intersectionPoint(quokka.getUpperLeft(), quokka.getUpperLeft2(), moveWall.getBr(), moveWall.getUr())) < shortestDistance) {
                                            shortestDistance = (float) distance(quokka.getUpperLeft(), intersectionPoint(quokka.getUpperLeft(), quokka.getUpperLeft2(), moveWall.getBr(), moveWall.getUr()));
                                            hitCorner = "topLeft";
                                            hitSide[0] = moveWall.getBr();
                                            hitSide[1] = moveWall.getUr();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                // wall loop
                if (moveWalls.size > 0 && hitWall) {
                    if (hitCorner.equals("topLeft")) {
                        tempWall.set(intersectionPoint(quokka.getUpperLeft(), quokka.getUpperLeft2(), hitSide[0], hitSide[1]));
                        quokka.setPosition(quokka.getPosition().x, tempWall.y - quokka.getQuokkaBounds().getHeight());
                    } else if (hitCorner.equals("topRight")) {
                        tempWall.set(intersectionPoint(quokka.getUpperRight(), quokka.getUpperRight2(), hitSide[0], hitSide[1]));
                        quokka.setPosition(quokka.getPosition().x, tempWall.y - quokka.getQuokkaBounds().getHeight());
                    } else if (hitCorner.equals("bottomRight")) {
                        tempWall.set(intersectionPoint(quokka.getBottomRight(), quokka.getBottomRight2(), hitSide[0], hitSide[1]));
                        quokka.setPosition(quokka.getPosition().x, tempWall.y);
                    } else if (hitCorner.equals("bottomLeft")) {
                        tempWall.set(intersectionPoint(quokka.getBottomLeft(), quokka.getBottomLeft2(), hitSide[0], hitSide[1]));
                        quokka.setPosition(quokka.getPosition().x, tempWall.y);
                    }
                    quokka.setVelocity(resultVector(quokka.getVelocity(), hitSide[0], hitSide[1]));
                }
                justPlanetTemp = false;
                quokka.getGravity().set(0, 0, 0);
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
                                //coachLandmark
                                velocityTemp.set(quokka.getVelocity());
                                gradientVector.set(2 * (intersectionPoint.x - circleCenter.x), 2 * (intersectionPoint.y - circleCenter.y), 0);
                                gradientVector.nor();
                                velocityTemp2.set(velocityTemp.sub((gradientVector).scl(2 * (velocityTemp.dot(gradientVector)))));
                                planetFixer();
                                quokka.setVelocity(velocityTemp2);
                            }
                        }
                    }
                    planetDistance.set(quokka.getPosition().x + quokka.getTexture().getWidth() / 2 - planet.getPosObstacle().x - planet.getTexture().getWidth() / 2, quokka.getPosition().y + quokka.getTexture().getHeight() / 2 - planet.getPosObstacle().y - planet.getTexture().getWidth() / 2, 0);
                    double planetMagnitude = Math.sqrt(Math.pow(planetDistance.x, 2) + Math.pow(planetDistance.y, 2));
                    if (planetMagnitude != 0) {
                        planetDistance.scl((float) (GOODGRAV / Math.pow(planetMagnitude, GRAVPOW)));
                    }
                    quokka.getGravity().add(planetDistance.x, planetDistance.y, 0);
                }
                justPlanet = justPlanetTemp;
                quokka.getGravity().set(quokka.getGravity().x / PLANETSCALER, quokka.getGravity().y / PLANETSCALER, 0);
                if (planets.size == 0) {
                    quokka.getGravity().set(0, -13, 0);
                }
                for (Meteor meteor : meteors) {
                    if (meteor.getPosMeteor().x < (cam.position.x * VIEWPORT_SCALER)) {
                        meteor.setStartFall(true);
                    }
                    if (meteor.isStartFall()) {
                        meteor.move(dt);
                        if (((meteor.getPosMeteor().x > clickPos.x) && (meteor.getPosMeteor().x < clickPos2.x)) || ((meteor.getPosMeteor().x < clickPos.x) && (meteor.getPosMeteor().x > clickPos2.x))) {
                            if (meteor.getMeteorBounds().contains(meteor.getPosMeteor().x, lineY(meteor.getPosMeteor().x))) {
                                meteors.removeValue(meteor, true);
                                meteor.dispose();
                            }
                        } else if ((((meteor.getPosMeteor().x + meteor.getTexture().getWidth()) > clickPos.x) && ((meteor.getPosMeteor().x + meteor.getTexture().getWidth()) < clickPos2.x)) || (((meteor.getPosMeteor().x + meteor.getTexture().getWidth()) < clickPos.x) && ((meteor.getPosMeteor().x + meteor.getTexture().getWidth()) > clickPos2.x))) {
                            if (meteor.getMeteorBounds().contains(meteor.getPosMeteor().x + meteor.getTexture().getWidth(), lineY(meteor.getPosMeteor().x + meteor.getTexture().getWidth()))) {
                                meteors.removeValue(meteor, true);
                                meteor.dispose();
                            }
                        }
                        if (meteor.collides(quokka.getQuokkaBounds())) {
                            if (meteors.contains(meteor, true)) {
                                gsm.set(new PlayState(gsm, world, level));
                                break;
                            }
                        }
                    }
                }
                for (EvilCloud cloud : clouds) {
                    if (cloud.collides(quokka.getQuokkaBounds())) {
                        gsm.set(new PlayState(gsm, world, level));
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
            }
            if (world == 3 && shouldFall) {
                towerVel.scl(dt);
                cam.position.y += towerVel.y;
                iniPot += 13 * towerVel.y;
                if (lineDraw) {
                    clickPosTemp.set(clickPosTemp.x, clickPosTemp.y + towerVel.y, 0);
                }
                towerVel.scl(1 / dt);
            } else {
                cam.position.x = quokka.getPosition().x + 80;
                if (lineDraw) {
                    clickPosTemp.set(clickPosTemp.x + quokka.getBottomLeft2().x - quokka.getBottomLeft().x, clickPosTemp.y, 0);
                }
            }
            backButton.getPosButton().x = cam.position.x - 800;
            pauseButton.getPosButton().x = cam.position.x - 800;
            backButton.getPosButton().y = cam.position.y + 100;
            pauseButton.getPosButton().y = cam.position.y - 200;
            backButton.getButtonBounds().set(backButton.getPosButton().x, backButton.getPosButton().y, backButton.getButtonBounds().getWidth(), backButton.getButtonBounds().getHeight());
            pauseButton.getButtonBounds().set(pauseButton.getPosButton().x, pauseButton.getPosButton().y, pauseButton.getButtonBounds().getWidth(), pauseButton.getButtonBounds().getHeight());
            if (shouldFall && !smallBounce) {
                quokka.update(dt);
            } //gohere
            if (lineCheck && !hitWall) {
                if (justHit) {
                    if (((quokka.getPosition().x > clickPos.x) && (quokka.getPosition().x < clickPos2.x)) || ((quokka.getPosition().x < clickPos.x) && (quokka.getPosition().x > clickPos2.x))) {
                        while (quokka.getQuokkaBounds().contains(quokka.getPosition().x, lineY(quokka.getPosition().x)) || (quokka.getQuokkaBounds().contains(quokka.getPosition().x + quokka.getTexture().getWidth(), lineY(quokka.getPosition().x + quokka.getTexture().getWidth())))) {
                            System.out.println("doing doing");
                            clickPos.set(clickPos.x, clickPos.y- 10, 0);
                            clickPos2.set(clickPos2.x, clickPos2.y- 10, 0);
                        }
                    }
                    else if ((((quokka.getPosition().x + quokka.getTexture().getWidth()) > clickPos.x) && ((quokka.getPosition().x + quokka.getTexture().getWidth()) < clickPos2.x)) || (((quokka.getPosition().x + quokka.getTexture().getWidth()) < clickPos.x) && ((quokka.getPosition().x + quokka.getTexture().getWidth()) > clickPos2.x))) {
                        while (quokka.getQuokkaBounds().contains(quokka.getPosition().x, lineY(quokka.getPosition().x)) ||quokka.getQuokkaBounds().contains(quokka.getPosition().x + quokka.getTexture().getWidth(), lineY(quokka.getPosition().x + quokka.getTexture().getWidth()))) {
                            System.out.println("doing yoing");
                            clickPos.set(clickPos.x, clickPos.y- 10, 0);
                            clickPos2.set(clickPos2.x, clickPos2.y- 10, 0);
                        }
                    }
                    clickPos2d.set(clickPos.x, clickPos.y);
                    clickPos2d2.set(clickPos2.x, clickPos2.y);
                    while(doIntersect(quokka.getUpperLeft2(), quokka.getUpperRight2(), clickPos2d, clickPos2d2) && (doIntersect(quokka.getBottomLeft2(), quokka.getBottomRight2(), clickPos2d, clickPos2d2) || doIntersect(quokka.getBottomLeft2(), quokka.getUpperLeft2(), clickPos2d, clickPos2d2) || doIntersect(quokka.getBottomRight2(), quokka.getUpperRight2(), clickPos2d, clickPos2d2))){
                        System.out.println("zoing ZOING");
                        if(quokka.getVelocity().x < 0) {
                            clickPos.set(clickPos.x + 10, clickPos.y, 0);
                            clickPos2.set(clickPos2.x + 10, clickPos2.y, 0);
                            clickPos2d.set(clickPos.x, clickPos.y);
                            clickPos2d2.set(clickPos2.x, clickPos2.y);
                        }
                        else{
                            clickPos.set(clickPos.x - 10, clickPos.y, 0);
                            clickPos2.set(clickPos2.x - 10, clickPos2.y, 0);
                            clickPos2d.set(clickPos.x, clickPos.y);
                            clickPos2d2.set(clickPos2.x, clickPos2.y);
                        }
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
            if (quokka.getPosition().y + quokka.getTexture().getHeight() <= cam.position.y - cam.viewportHeight / 2) {
                if (moveWalls.size == 0) {
                    gsm.set(new PlayState(gsm, world, level));
                }
            }
            if (layer == finalLayer) {
                if (happyCloud.collides(quokka.getQuokkaBounds())) {
                    gsm.set(new MenuState(gsm, world, level + 1));
                }
            }
            cam.update();
        }
    }
    private void planetFixer(){
        float currentPot = 0f;
        velocityTemp2.set(velocityTemp2.x /velocityTemp2.len(), velocityTemp2.y/velocityTemp2.len(), 0);
        for(int i =0; i<planets.size;i++){
            Obstacle planet = planets.get(i);
            planetDistance.set(quokka.getPosition().x + quokka.getTexture().getWidth() / 2 - planet.getPosObstacle().x - planet.getTexture().getWidth() / 2, quokka.getPosition().y + quokka.getTexture().getHeight() / 2 - planet.getPosObstacle().y - planet.getTexture().getWidth() / 2, 0);
            double planetMagnitude = Math.sqrt(Math.pow(planetDistance.x, 2) + Math.pow(planetDistance.y, 2));
            currentPot+=(1/planetMagnitude);
        }
        currentPot*=(GOODGRAV * PLANETSCALER);
        //coachLandmark
        velocityTemp2.scl((float) Math.sqrt(2*(iniPot - currentPot)));
        velocityTemp2.scl((float) (1/Math.sqrt(currentDT)));
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

    private boolean onSegment(Vector2 p, Vector2 q, Vector2 r)
    {
        return (q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x) && q.y <= Math.max(p.y, r.y) && q.y >= Math.min(p.y, r.y));
    }
    private int orientation(Vector2 p, Vector2 q, Vector2 r)
    {
        float val = (q.y - p.y) * (r.x - q.x) -(q.x - p.x) * (r.y - q.y);

        if (val == 0){
            return 0;  // colinear
        }

        return (val > 0)? 1: 2; // clock or counterclock wise
    }
    private boolean doIntersect(Vector2 p1, Vector2 q1, Vector2 p2, Vector2 q2)
    {
        // Find the four orientations needed for general and
        // special cases
        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);

        // General case
        if (o1 != o2 && o3 != o4)
            return true;

        // Special Cases
        // p1, q1 and p2 are colinear and p2 lies on segment p1q1
        if (o1 == 0 && onSegment(p1, p2, q1)) return true;

        // p1, q1 and q2 are colinear and q2 lies on segment p1q1
        if (o2 == 0 && onSegment(p1, q2, q1)) return true;

        // p2, q2 and p1 are colinear and p1 lies on segment p2q2
        if (o3 == 0 && onSegment(p2, p1, q2)) return true;

        // p2, q2 and q1 are colinear and q1 lies on segment p2q2
        return (o4 == 0 && onSegment(p2, q1, q2));
    }

    private Vector2 intersectionPoint(Vector2 a1, Vector2 a2, Vector2 b1, Vector2 b2){
        xdiff.set(a1.x - a2.x, b1.x - b2.x);
        ydiff.set(a1.y - a2.y, b1.y - b2.y);
        float div = det(xdiff, ydiff);
        tempDet.set(det(a1,a2), det(b1,b2));
        float x = det(tempDet, xdiff) / div;
        float y = det(tempDet, ydiff) / div;
        tempDet.set(x,y);
        return tempDet;
    }
    private double distance(Vector2 point1, Vector2 point2){
        return Math.sqrt(Math.pow(point2.y-point1.y, 2) + Math.pow(point2.x-point1.x, 2));
    }

    private float det(Vector2 a1, Vector2 a2){
        return a1.x * a2.y - a1.y * a2.x;
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
        for(Obstacle wallSwitch: switches){
            sb.draw(wallSwitch.getTexture(), wallSwitch.getPosObstacle().x, wallSwitch.getPosObstacle().y);
        }
        for (Hawk hawk : hawks){
            sb.draw(hawk.getTexture(), hawk.getPosHawk().x, hawk.getPosHawk().y);
        }
        for(Meteor meteor: meteors){
            sb.draw(meteor.getTexture(), meteor.getPosMeteor().x, meteor.getPosMeteor().y);
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
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setProjectionMatrix(cam.combined);
        shapeRenderer.setAutoShapeType(true);
        if(vineDraw) {
            if (clickPos2.y != -100) {
                shapeRenderer.setColor(Color.BROWN);
                shapeRenderer.line(clickPos, clickPos2);
            } else if (clickPosTemp.y != -100) {
                shapeRenderer.setColor(Color.YELLOW);
                shapeRenderer.line(clickPos, clickPosTemp);
            }
        }
        for(Obstacle nullZone : nullZones){
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.rect(nullZone.getPosObstacle().x, nullZone.getPosObstacle().y, nullZone.getObstacleBounds().getWidth(), nullZone.getObstacleBounds().getHeight());
        }
        shapeRenderer.end();
        sb.begin();
        for(BonusQuokka bonusQuokka : bonusQuokkas){
            if(!collectedQuokkas.get(bonusQuokkas.indexOf(bonusQuokka, false))) {
                sb.draw(bonusQuokka.getTexture(), bonusQuokka.getPosQuokka().x, bonusQuokka.getPosQuokka().y);
            }
        }
        sb.draw(quokka.getTexture(), quokka.getPosition().x, quokka.getPosition().y);
        for(Wall wall : walls){
            sb.draw(wall.getTexture(), wall.getPosWall().x, wall.getPosWall().y);
        }
        for(TallDino tallDino : tallDinos){
            sb.draw(tallDino.getTexture(), tallDino.getPosTallDino().x, tallDino.getPosTallDino().y);
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
        for(TallDino tallDino: tallDinos){
            tallDino.dispose();
        }
        for(BonusQuokka bonusQuokka : bonusQuokkas){
            bonusQuokka.dispose();
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
        for(Meteor meteor: meteors){
            meteor.dispose();
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

    private void levelInit(int world, int level){
        backButton = new Button(new Texture("level4Button.png"), 0, 500, 0);
        pauseButton = new Button(new Texture("level4Button.png"), 0, 300, 0);
        walls.add(new Wall(-1000, -220, "wall.png"));
        walls.add(new Wall(-1000, 375, "wall.png"));
        switch(world){
            case 1:
                switch(level){
                    /*case 1:
                        levelBackground = new Texture("level1Background.png");
                        switches.add(new Obstacle(200, 100, "wallSwitch.png"));
                        walls.add(new Wall(400, -220, switches, -320));
                        walls.add(new Wall(400, 375, switches, 500));
                        happyCloud = new HappyCloud(10000,200);
                        break;*/
                    case 1:
                        levelBackground = new Texture("level1Background.png");
                        bonusQuokkas.add(new BonusQuokka(200, 400));
                        happyCloud = new HappyCloud(500, 200);
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
                        clouds.add(new EvilCloud(350, 400));
                        bonusQuokkas.add(new BonusQuokka(350, 50));
                        happyCloud = new HappyCloud(750, 200);
                        break;
                    case 4:
                        levelBackground = new Texture("level3Background.png");
                        walls.add(new Wall(350, -80, "wall.png"));
                        walls.add(new Wall(850, -230, "wall.png"));
                        clouds.add(new EvilCloud(1200, 460));
                        bonusQuokkas.add(new BonusQuokka(1200, 10));
                        happyCloud = new HappyCloud(500, 50);
                        break;
                    case 5:
                        levelBackground = new Texture("level3Background.png");
                        clouds.add(new EvilCloud(200, 300));
                        walls.add(new Wall(900, 450));
                        clouds.add(new EvilCloud(1500, 150));
                        happyCloud = new HappyCloud(1710,400);
                        break;
                    case 6:
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
                    /*case 5:
                        levelBackground = new Texture("level2Background.png");
                        hawks.add(new Hawk(400,150));
                        happyCloud = new HappyCloud(850, 300);
                        break;*/
                    case 7:
                        levelBackground = new Texture("level3Background.png");
                        clouds.add(new EvilCloud(200, 50));
                        walls.add(new Wall (750, 300));
                        walls.add(new Wall(1150, -130));
                        happyCloud = new HappyCloud(1400, 200);
                        break;
                    /*case 8:
                        levelBackground = new Texture("level2Background.png");
                        for(int i = 0; i < 10; i++){
                            walls.add(new Wall(400 + 123 * i, -280));
                            walls.add(new Wall(400 + 123 * i, 500));
                        }
                        happyCloud = new HappyCloud(1400, 5);
                        break;

                    case 8:
                        levelBackground = new Texture("level1Background.png");
                        walls.add(new Wall(300, -80));
                        clouds.add(new EvilCloud(450, 350));
                        clouds.add(new EvilCloud(1200, 350));
                        happyCloud = new HappyCloud(1650, 150);
                        break;
                        */
                    case 8:
                        levelBackground = new Texture("level3Background.png");
                        walls.add(new Wall(350, 300, "wall.png"));
                        bonusQuokkas.add(new BonusQuokka(500, 400));
                        walls.add(new Wall(750,300, "wall.png"));
                        happyCloud = new HappyCloud(1250, 200);
                        break;
                    case 9:
                        levelBackground = new Texture("level1Background.png");
                        clouds.add(new EvilCloud(250, 200));
                        bonusQuokkas.add(new BonusQuokka(650, 0));
                        walls.add(new Wall(900, -180));
                        clouds.add(new EvilCloud(1050, 300));
                        walls.add(new Wall(1500, 400));
                        happyCloud = new HappyCloud(1700, 500);
                        break;
                    case 10:
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
                    /*case 10:
                        levelBackground = new Texture("level3Background.png");
                        arrows.add(new Arrow(100, 1100, true));
                        clouds.add(new EvilCloud(50, 1000));
                        happyCloud = new HappyCloud(50, 2000);
                        break;*/
                }
                break;
            case 2:
                switch(level) {
                    /*case 1:
                        levelBackground = new Texture("level1Background.png");
                        walls.add(new Wall(700, 500, "horizontWall.png"));
                        happyCloud = new HappyCloud(10000,200);
                        break;
                    case 2:
                        levelBackground = new Texture("spaceBackground.png");
                        planets.add(new Obstacle(300, 200, "greenPlanet.png"));
                        planets.add(new Obstacle(900, 400, "greenPlanet.png"));
                        happyCloud = new HappyCloud(1500, 300);
                        planets.add(new Obstacle(1700, 400, "greenPlanet.png"));
                        break;
                    case 3:
                        levelBackground = new Texture("spaceBackground.png");
                        planets.add(new Obstacle(300, 200, "greenPlanet.png"));
                        happyCloud = new HappyCloud(1500, 300);
                        break;
                    case 4:
                        layerTextures.add(new Texture("level1Background.png"));
                        layerTextures.add(new Texture("level2Background.png"));
                        layerTextures.add(new Texture("level3Background.png"));
                        vines.add(new Vine(600, 500, 1, 50));
                        layerVines.add(new Array<Vine>(vines));
                        vines.clear();
                        vines.add(new Vine (900, 600, 0, 50));
                        layerVines.add(new Array<Vine>(vines));
                        happyCloud = new HappyCloud(1300, 6);
                        break;*/
                    case 1:
                        walls.add(new Wall(500, 450));
                        meteors.add(new Meteor(675, 780, 0, 0));
                        clouds.add(new EvilCloud(900, 50));
                        bonusQuokkas.add(new BonusQuokka(1200, 50));
                        happyCloud = new HappyCloud(1550, 50);
                        break;
                    case 2:
                        walls.add(new Wall(450, 380));
                        switches.add(new Obstacle(650, 120, "wallSwitch.png"));
                        walls.add(new Wall(450, -445, switches, -215));
                        meteors.add(new Meteor(800, 780, 0, 0));
                        walls.add(new Wall(900, -80));
                        happyCloud = new HappyCloud(1200, 50);
                        break;
                    case 3:
                        meteors.add(new Meteor(500, 780, 0, 0));
                        clouds.add(new EvilCloud(675, 250));
                        bonusQuokkas.add(new BonusQuokka(700, 400));
                        meteors.add(new Meteor(1000, 780, 0, 0));
                        clouds.add(new EvilCloud(1200, 600));
                        clouds.add(new EvilCloud(1200, 50));
                        meteors.add(new Meteor(1550, 780, 0, 0));
                        happyCloud = new HappyCloud(1750, 300);
                        break;
                    case 4:
                        walls.add(new Wall(350, 500));
                        tallDinos.add(new TallDino(923, -195, -223, 400));
                        bonusQuokkas.add(new BonusQuokka(0, 50));
                        walls.add(new Wall(1000, 500));
                        tallDinos.add(new TallDino(1573, -195, 427, 400));
                        happyCloud = new HappyCloud(1000, 0);
                        break;
                    case 5:
                        meteors.add(new Meteor(500, 780, 0, 0));
                        meteors.add(new Meteor(675, 780, 0, 0));
                        meteors.add(new Meteor(850, 780, 0, 0));
                        meteors.add(new Meteor(1025, 780, 0, 0));
                        meteors.add(new Meteor(1200, 780, 0, 0));
                        happyCloud = new HappyCloud(1375, 50);
                        meteors.add(new Meteor(1550, 780, 0, 0));
                        meteors.add(new Meteor(1725, 780, 0, 0));
                        bonusQuokkas.add(new BonusQuokka(1900, 50));
                        break;
                    case 6:
                        break;
                }
                break;
            case 3:
                switch(level){
                    case 1:
                        clouds.add(new EvilCloud(250,800));
                        walls.add(new Wall(700, 1250, "horizontWall.png"));
                        bonusQuokkas.add(new BonusQuokka(1000, 1550));
                        happyCloud = new HappyCloud(300, 1650);
                        break;
                    case 2:
                        arrows.add(new Arrow(50, 850));
                        arrows.add(new Arrow(50, 1250));
                        bonusQuokkas.add(new BonusQuokka(300, 1250));
                        clouds.add(new EvilCloud(250, 1650));
                        happyCloud = new HappyCloud(700,1800);
                        break;
                    case 3:
                        walls.add(new Wall(-50, 850, "horizontWall.png"));
                        arrows.add(new Arrow (cam.viewportWidth- 100, 1250));
                        arrows.add(new Arrow(50, 1650));
                        arrows.add(new Arrow(cam.viewportWidth- 100, 2050));
                        bonusQuokkas.add(new BonusQuokka(200, 1250));
                        happyCloud = new HappyCloud(50, 2250);
                        break;
                    case 4:
                        break;
                }
                break;
            case 4:
                switch(level){
                    case 1:
                        clouds.add(new EvilCloud(450, 600));
                        clouds.add(new EvilCloud(450, 50));
                        nullZones.add(new Obstacle(700, 0, 580, 800));
                        bonusQuokkas.add(new BonusQuokka(990, 450));
                        happyCloud = new HappyCloud(1600, 150);
                        break;
                    case 2:
                        clouds.add(new EvilCloud(250, 50));
                        walls.add(new Wall(900, 250));
                        bonusQuokkas.add(new BonusQuokka(1068, 500));
                        happyCloud = new HappyCloud(1258, 50);
                        break;
                    case 3:
                        nullZones.add(new Obstacle(400, 100, 900, 800));
                        walls.add(new Wall(1300, 200));
                        bonusQuokkas.add(new BonusQuokka(1100, 200));
                        happyCloud = new HappyCloud(1450, 350);
                        break;
                    case 4:
                        nullZones.add();
                        break;

                }
                break;
        }
        if(world == 3){
            for(int i = -220; i < happyCloud.getPosCloud().y; i+=595){
                walls.add(new Wall(-30, i, "wall.png"));
                walls.add(new Wall(cam.viewportWidth- 10, i, "wall.png"));
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
        if(moveWalls.size!=0 || world == 3){
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
            velocityTemp2.set((float)(velocityTemp2.x * OCEANSLOW), (float)(velocityTemp2.y * OCEANSLOW), 0);
        }
        float currentPot = 0f;
        velocityTemp2.set(velocityTemp2.x /velocityTemp2.len(), velocityTemp2.y/velocityTemp2.len(), 0);
        if(planets.size > 0){
            for(Obstacle planet : planets){
                planetDistance.set(quokka.getPosition().x + quokka.getTexture().getWidth() / 2 - planet.getPosObstacle().x - planet.getTexture().getWidth() / 2, quokka.getPosition().y + quokka.getTexture().getHeight() / 2 - planet.getPosObstacle().y - planet.getTexture().getWidth() / 2, 0);
                double planetMagnitude = Math.sqrt(Math.pow(planetDistance.x, 2) + Math.pow(planetDistance.y, 2));
                currentPot+=(1/planetMagnitude);
            }
            currentPot*=(GOODGRAV * PLANETSCALER);
        }
        else{
            currentPot = 13 * quokka.getPosition().y;
        }
        //coachLandmark
        velocityTemp2.scl((float) Math.sqrt(2.0*(iniPot - currentPot)));
        System.out.println(iniPot - (0.50 * Math.pow(velocityTemp2.len(), 2.0) + 13.0 * quokka.getPosition().y));
        velocityTemp2.scl((float) (1.0/Math.sqrt(currentDT)));
        System.out.println(iniPot - (0.50 * Math.pow(velocityTemp2.len() * Math.sqrt(currentDT), 2.0) + 13.0 * quokka.getPosition().y));
        return velocityTemp2;
    }
    private Vector3 resultVector(Vector3 velocity, Vector2 point1, Vector2 point2) {
        velocityTemp.set(velocity);
        normal.set(point1.y-point2.y,point2.x-point1.x,0);
        normal.nor();
        velocityTemp2.set(velocityTemp.sub((normal).scl(2*(velocityTemp.dot(normal)))));
        if(moveWalls.size!=0){
            velocityTemp2.set((float)(velocityTemp2.x * OCEANSLOW), (float)(velocityTemp2.y * OCEANSLOW), 0);
        }
        float currentPot = 0f;
        velocityTemp2.set(velocityTemp2.x /velocityTemp2.len(), velocityTemp2.y/velocityTemp2.len(), 0);
        if(planets.size > 0){
            for(Obstacle planet : planets){
                planetDistance.set(quokka.getPosition().x + quokka.getTexture().getWidth() / 2 - planet.getPosObstacle().x - planet.getTexture().getWidth() / 2, quokka.getPosition().y + quokka.getTexture().getHeight() / 2 - planet.getPosObstacle().y - planet.getTexture().getWidth() / 2, 0);
                double planetMagnitude = Math.sqrt(Math.pow(planetDistance.x, 2) + Math.pow(planetDistance.y, 2));
                currentPot+=(1/planetMagnitude);
            }
            currentPot*=(GOODGRAV * PLANETSCALER);
        }
        else{
            currentPot = 13 * quokka.getPosition().y;
        }
        //coachLandmark
        velocityTemp2.scl((float) Math.sqrt(2*(iniPot - currentPot)));
        velocityTemp2.scl((float) (1/Math.sqrt(currentDT)));
        System.out.println(1/2 * (Math.pow(velocityTemp2.x, 2) + Math.pow(velocityTemp2.y, 2)) + 13 * quokka.getPosition().y);
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
            gsm.set(new MenuState(gsm, world, level));
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
            shouldMove = true;
            hasCollided = false;
            lineCheck = false;
            paused = false;
            clickPos2.set(screenX, -100, 0);
            clickPos.set(screenX, screenY, 0);
            clickPos.set(cam.unproject(clickPos));
            if(lineDraw) {
                clickPosTemp.set(screenX, screenY, 0);
                clickPosTemp.set(cam.unproject(clickPosTemp));
            }
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
                    if(quokka.getVelocity().y <= 0 ) {
                        while (quokka.getQuokkaBounds().contains(quokka.getPosition().x, lineY(quokka.getPosition().x))) {
                            clickPos.set(clickPos.x, clickPos.y- 10, 0);
                            clickPos2.set(clickPos2.x, clickPos2.y- 10, 0);
                        }
                        quokka.getVelocity().scl(currentDT);
                        quokka.getQuokkaBounds().set(quokka.getQuokkaBounds().x + quokka.getVelocity().x, quokka.getQuokkaBounds().y + quokka.getVelocity().y, quokka.getQuokkaBounds().width, quokka.getQuokkaBounds().height);
                        while (quokka.getQuokkaBounds().contains(quokka.getPosition().x + quokka.getVelocity().x, lineY(quokka.getPosition().x + quokka.getVelocity().y))) {
                            clickPos.set(clickPos.x, clickPos.y- 10, 0);
                            clickPos2.set(clickPos2.x, clickPos2.y- 10, 0);
                        }
                    }
                    else{
                        if(planets.size > 0) {
                            while (quokka.getQuokkaBounds().contains(quokka.getPosition().x, lineY(quokka.getPosition().x))) {
                                clickPos.set(clickPos.x, clickPos.y+ 10, 0);
                                clickPos2.set(clickPos2.x, clickPos2.y+ 10, 0);
                            }
                            quokka.getVelocity().scl(currentDT);
                            quokka.getQuokkaBounds().set(quokka.getQuokkaBounds().x + quokka.getVelocity().x, quokka.getQuokkaBounds().y + quokka.getVelocity().y, quokka.getQuokkaBounds().width, quokka.getQuokkaBounds().height);
                            while (quokka.getQuokkaBounds().contains(quokka.getPosition().x + quokka.getVelocity().x, lineY(quokka.getPosition().x + quokka.getVelocity().y))) {
                                clickPos.set(clickPos.x, clickPos.y+ 10, 0);
                                clickPos2.set(clickPos2.x, clickPos2.y+ 10, 0);
                            }
                        }
                        else{
                            while (quokka.getQuokkaBounds().contains(quokka.getPosition().x, lineY(quokka.getPosition().x))) {
                                clickPos.set(clickPos.x, clickPos.y- 10, 0);
                                clickPos2.set(clickPos2.x, clickPos2.y- 10, 0);
                            }
                            quokka.getVelocity().scl(currentDT);
                            quokka.getQuokkaBounds().set(quokka.getQuokkaBounds().x + quokka.getVelocity().x, quokka.getQuokkaBounds().y + quokka.getVelocity().y, quokka.getQuokkaBounds().width, quokka.getQuokkaBounds().height);
                            while (quokka.getQuokkaBounds().contains(quokka.getPosition().x + quokka.getVelocity().x, lineY(quokka.getPosition().x + quokka.getVelocity().y))) {
                                clickPos.set(clickPos.x, clickPos.y- 10, 0);
                                clickPos2.set(clickPos2.x, clickPos2.y- 10, 0);
                            }
                        }
                    }
                    quokka.getVelocity().scl(1 / currentDT);
                } else if ((((quokka.getPosition().x + quokka.getTexture().getWidth()) > clickPos.x) && ((quokka.getPosition().x + quokka.getTexture().getWidth()) < clickPos2.x)) || (((quokka.getPosition().x + quokka.getTexture().getWidth()) < clickPos.x) && ((quokka.getPosition().x + quokka.getTexture().getWidth()) > clickPos2.x))) {
                    if(quokka.getVelocity().y <= 0 ) {
                        while (quokka.getQuokkaBounds().contains(quokka.getPosition().x + quokka.getTexture().getWidth(), lineY(quokka.getPosition().x + quokka.getTexture().getWidth()))) {
                            clickPos.set(clickPos.x, clickPos.y- 10, 0);
                            clickPos2.set(clickPos2.x, clickPos2.y- 10, 0);
                        }
                        quokka.getVelocity().scl(currentDT);
                        quokka.getQuokkaBounds().set(quokka.getQuokkaBounds().x + quokka.getVelocity().x, quokka.getQuokkaBounds().y + quokka.getVelocity().y, quokka.getQuokkaBounds().width, quokka.getQuokkaBounds().height);
                        while (quokka.getQuokkaBounds().contains(quokka.getPosition().x + quokka.getVelocity().x, lineY(quokka.getPosition().x + quokka.getVelocity().y))) {
                            clickPos.set(clickPos.x, clickPos.y- 10, 0);
                            clickPos2.set(clickPos2.x, clickPos2.y- 10, 0);
                        }
                        quokka.getVelocity().scl(1 / currentDT);
                    }
                    else{
                        if(planets.size > 0) {
                            while (quokka.getQuokkaBounds().contains(quokka.getPosition().x + quokka.getTexture().getWidth(), lineY(quokka.getPosition().x + quokka.getTexture().getWidth()))) {
                                clickPos.set(clickPos.x, clickPos.y+ 10, 0);
                                clickPos2.set(clickPos2.x, clickPos2.y+ 10, 0);
                            }
                            quokka.getVelocity().scl(currentDT);
                            quokka.getQuokkaBounds().set(quokka.getQuokkaBounds().x + quokka.getVelocity().x, quokka.getQuokkaBounds().y + quokka.getVelocity().y, quokka.getQuokkaBounds().width, quokka.getQuokkaBounds().height);
                            while (quokka.getQuokkaBounds().contains(quokka.getPosition().x + quokka.getVelocity().x, lineY(quokka.getPosition().x + quokka.getVelocity().y))) {
                                clickPos.set(clickPos.x, clickPos.y+ 10, 0);
                                clickPos2.set(clickPos2.x, clickPos2.y+ 10, 0);
                            }
                            quokka.getVelocity().scl(1 / currentDT);
                        }
                        else{
                            while (quokka.getQuokkaBounds().contains(quokka.getPosition().x + quokka.getTexture().getWidth(), lineY(quokka.getPosition().x + quokka.getTexture().getWidth()))) {
                                clickPos.set(clickPos.x, clickPos.y- 10, 0);
                                clickPos2.set(clickPos2.x, clickPos2.y- 10, 0);
                            }
                            quokka.getVelocity().scl(currentDT);
                            quokka.getQuokkaBounds().set(quokka.getQuokkaBounds().x + quokka.getVelocity().x, quokka.getQuokkaBounds().y + quokka.getVelocity().y, quokka.getQuokkaBounds().width, quokka.getQuokkaBounds().height);
                            while (quokka.getQuokkaBounds().contains(quokka.getPosition().x + quokka.getVelocity().x, lineY(quokka.getPosition().x + quokka.getVelocity().y))) {
                                clickPos.set(clickPos.x, clickPos.y- 10, 0);
                                clickPos2.set(clickPos2.x, clickPos2.y- 10, 0);
                            }
                            quokka.getVelocity().scl(1 / currentDT);
                        }
                    }
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
