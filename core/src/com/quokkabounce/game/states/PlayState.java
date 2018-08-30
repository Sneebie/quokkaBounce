package com.quokkabounce.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BooleanArray;
import com.quokkabounce.game.QuokkaBounce;
import com.quokkabounce.game.sprites.Airplane;
import com.quokkabounce.game.sprites.Arrow;
import com.quokkabounce.game.sprites.BonusQuokka;
import com.quokkabounce.game.sprites.Button;
import com.quokkabounce.game.sprites.Drone;
import com.quokkabounce.game.sprites.EvilCloud;
import com.quokkabounce.game.sprites.HappyCloud;
import com.quokkabounce.game.sprites.Hawk;
import com.quokkabounce.game.sprites.JumpFish;
import com.quokkabounce.game.sprites.LaserBeam;
import com.quokkabounce.game.sprites.LaserGun;
import com.quokkabounce.game.sprites.Meteor;
import com.quokkabounce.game.sprites.MoveWall;
import com.quokkabounce.game.sprites.Obstacle;
import com.quokkabounce.game.sprites.Quokka;
import com.quokkabounce.game.sprites.Stoplight;
import com.quokkabounce.game.sprites.TallDino;
import com.quokkabounce.game.sprites.Vine;
import com.quokkabounce.game.sprites.Wall;

/**
 * Created by Eric on 8/29/2017.
 */

public class PlayState extends State implements InputProcessor{
    private static final int HAWKSIGHT = 400;
    private static final int ARROWHEIGHT = 150;
    private static final float GOODGRAV = -500000000f;
    private static final float DEPTHSCALER = 0.0001f;
    private static final double OCEANSLOW = 0.98;
    private static final float GRAVPOW = 2f;
    private static final float MAGSCALER = 500;
    private static final float VIEWPORT_SCALER = 1.6f;
    private static final int TOWERFALL = 100;
    private static final int WALLSPEED = 3;

    private Quokka quokka;
    private Button backButton, pauseButton;
    private Texture levelBackground;
    private Vector2 levelBackgroundPos1, levelBackgroundPos2, levelBackgroundPos3, levelBackgroundPos4, intersectionPoint, intersectionPointTemp, circleCenter, quokkaSide, adjustedCenter, planetProj, clickPos2d, clickPos2d2, xdiff, ydiff, tempDet, tempWall, backPoint, tempBack, temp2;
    private Vector3 clickPos, clickPos2, velocityTemp, velocityTemp2, normal, clickPosTemp, planetDistance, gradientVector, touchInput, towerVel;
    private ShapeRenderer shapeRenderer;
    private HappyCloud happyCloud;
    private float currentDT, iniPot, shortestDistance, camVel;
    private int layer, finalLayer, pointCounter;
    private boolean shouldFall, endPoint, touchingWall, lineCheck, lineDraw, justHit, vineDraw, justHitTemp, justHitBrush, justHitBrushTemp, outZone, justPlanet, justPlanetTemp, paused, justPaused, vineCheck, hasCollided, smallMove, smallBounce, hitWall, firstSide, shouldMove, hasEdgeCollided, level1, camUpdate;

    private Array<EvilCloud> clouds;
    private Array<Hawk> hawks;
    private Array<Wall> walls;
    private Array<BonusQuokka> bonusQuokkas;
    private Array<Obstacle> switches;
    private Array<Stoplight> stoplights;
    private Array<Vector2> moveSpots;
    private Array<Obstacle> planets;
    private Array<Obstacle> nebulae;
    private Array<Obstacle> blackHoles;
    private Array<Obstacle> brushes;
    private Array<Obstacle> nullZones;
    private Array<Obstacle> portals;
    private Array<Obstacle> windGusts;
    private Array<Arrow> arrows;
    private Array<Array<Vine>> layerVines;
    private Array<Vine> vines;
    private Array<Texture> layerTextures;
    private Array<MoveWall> moveWalls;
    private Array<Meteor> meteors;
    private Array<Airplane> airplanes;
    private Array<Airplane> tropicBirds;
    private Array<Airplane> tropicFish;
    private Array<JumpFish> jumpFishes;
    private Array<TallDino> tallDinos;
    private Array<Drone> drones;
    private Array<LaserGun> laserGuns;
    private BooleanArray collectedQuokkas;
    private boolean hitLeft[], hitRight[], hitBottom[], hitTop[];
    private Vector2 hitSide[];
    private Vector3 tempGrav;
    private String hitCorner;
    private Texture warningTexture;

    public PlayState(GameStateManager gsm, int world, int level) {
        super(gsm, world, level);
        levelBackground = new Texture("level2Background.png");
        clouds = new Array<EvilCloud>();
        walls = new Array<Wall>();
        hawks = new Array<Hawk>();
        laserGuns = new Array<LaserGun>();
        drones = new Array<Drone>();
        bonusQuokkas = new Array<BonusQuokka>();
        collectedQuokkas = new BooleanArray();
        switches = new Array<Obstacle>();
        planets = new Array<Obstacle>();
        nebulae = new Array<Obstacle>();
        blackHoles = new Array<Obstacle>();
        nullZones = new Array<Obstacle>();
        arrows = new Array<Arrow>();
        brushes = new Array<Obstacle>();
        stoplights = new Array<Stoplight>();
        portals = new Array<Obstacle>();
        windGusts = new Array<Obstacle>();
        moveWalls = new Array<MoveWall>();
        moveSpots = new Array<Vector2>();
        vines = new Array<Vine>();
        meteors = new Array<Meteor>();
        airplanes = new Array<Airplane>();
        tropicBirds = new Array<Airplane>();
        tropicFish = new Array<Airplane>();
        jumpFishes = new Array<JumpFish>();
        tallDinos = new Array<TallDino>();
        layerTextures = new Array<Texture>();
        layerVines = new Array<Array<Vine>>();
        hitLeft = new boolean[4];
        hitRight = new boolean[4];
        hitBottom = new boolean[4];
        hitTop = new boolean[4];
        hitSide = new Vector2[2];
        layer = 0;
        level1 = false;
        //warningTexture = new Texture("warning.png");
        finalLayer = 0;
        if(planets.size == 0 && nebulae.size == 0 && blackHoles.size == 0) {
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
        endPoint = false;
        paused = false;
        smallBounce = false;
        shouldMove = false;
        justPaused = false;
        hasCollided = false;
        hasEdgeCollided = false;
        smallMove = false;
        camUpdate = false;
        vineCheck = true;
        justHitBrush = false;
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
        backPoint = new Vector2();
        tempBack = new Vector2();
        gradientVector = new Vector3();
        touchInput = new Vector3();
        tempGrav = new Vector3();
        xdiff = new Vector2();
        ydiff = new Vector2();
        tempDet = new Vector2();
        tempWall = new Vector2();
        temp2 = new Vector2();
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
        if(planets.size>0 || nebulae.size > 0 || blackHoles.size > 0){
            tempGrav.set(0, 0, 0);
            for(Obstacle planet : planets){
                planetDistance.set(Math.abs(quokka.getPosition().x + quokka.getTexture().getWidth() / 2 - planet.getPosObstacle().x - planet.getTexture().getWidth() / 2), Math.abs(quokka.getPosition().y + quokka.getTexture().getHeight() / 2 - planet.getPosObstacle().y - planet.getTexture().getHeight() / 2), 0);
                double planetMagnitude = MAGSCALER * Math.sqrt(Math.pow(planetDistance.x, 2) + Math.pow(planetDistance.y, 2));
                iniPot += GOODGRAV / Math.pow(planetMagnitude, GRAVPOW - 1);
            }
            for(Obstacle planet : blackHoles){
                planetDistance.set(Math.abs(quokka.getPosition().x + quokka.getTexture().getWidth() / 2 - planet.getPosObstacle().x - planet.getTexture().getWidth() / 2), Math.abs(quokka.getPosition().y + quokka.getTexture().getHeight() / 2 - planet.getPosObstacle().y - planet.getTexture().getHeight() / 2), 0);
                double planetMagnitude = MAGSCALER * Math.sqrt(Math.pow(planetDistance.x, 2) + Math.pow(planetDistance.y, 2));
                iniPot += GOODGRAV / Math.pow(planetMagnitude, GRAVPOW - 1);
            }
            for(Obstacle nebula : nebulae){
                planetDistance.set(Math.abs(quokka.getPosition().x + quokka.getTexture().getWidth() / 2 - nebula.getPosObstacle().x - nebula.getTexture().getWidth() / 2), Math.abs(quokka.getPosition().y + quokka.getTexture().getHeight() / 2 - nebula.getPosObstacle().y - nebula.getTexture().getHeight() / 2), 0);
                double planetMagnitude = MAGSCALER * Math.sqrt(Math.pow(planetDistance.x, 2) + Math.pow(planetDistance.y, 2));
                iniPot += GOODGRAV / Math.pow(planetMagnitude, GRAVPOW - 1);
            }
        }
        else{
            iniPot = -1 * quokka.getGravity().y * quokka.getPosition().y;
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
                for(Drone drone : drones){
                    if(isCollision(drone.getPolygon(), quokka.getQuokkaBounds())){
                        gsm.set(new PlayState(gsm, world, level));
                        break;
                    }
                    if(drone.getPosDrone().x < (cam.position.x + cam.viewportWidth * VIEWPORT_SCALER / 2)){
                        drone.setStartMove(true);
                    }
                    if(drone.isStartMove()) {
                        drone.move(dt, quokka.getPosition());
                    }
                }
                for(JumpFish jumpFish : jumpFishes){
                    if(isCollision(jumpFish.getPolygon(), quokka.getQuokkaBounds())){
                        gsm.set(new PlayState(gsm, world, level));
                    }
                    if(jumpFish.getPosJumpFish().x > (cam.position.x - cam.viewportWidth * VIEWPORT_SCALER / 2)){
                        jumpFish.setStartFall(true);
                    }
                    if(jumpFish.isStartFall()) {
                        jumpFish.move(dt, quokka.getPosition());
                    }
                }
                for(LaserGun laserGun : laserGuns){
                    if(isCollision(laserGun.getMyBeam().getPolygon(), quokka.getQuokkaBounds())){
                        gsm.set(new PlayState(gsm, world, level));
                        break;
                    }
                    laserGun.shoot(dt, quokka.getPosition());
                    if(laserGun.getMyBeam().getPosBeam().y > cam.viewportHeight || laserGun.getMyBeam().getPosBeam().y + laserGun.getMyBeam().getBeamBounds().getHeight() < 0 || laserGun.getMyBeam().getPosBeam().x + laserGun.getMyBeam().getBeamBounds().getWidth() < (cam.position.x - cam.viewportWidth / 2) || laserGun.getMyBeam().getPosBeam().x > (cam.position.x + cam.viewportWidth / 2)){
                        laserGun.resetShot();
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
                                switches.removeValue(wallSwitch, false);
                                wallSwitch.dispose();
                            }
                        }
                    }
                    if(wall.getDir() == 1) {
                        if (wall.isMoveWall() && (Math.abs(wall.getWallMove() - wall.getPosWall().y) > 0)) {
                            wall.setPosWall(wall.getPosWall().x, wall.getPosWall().y + (wall.getWallMove() - wall.getPosWall().y) / WALLSPEED);
                            wall.setWallBounds(wall.getPosWall().x, wall.getPosWall().y, wall.getTexture().getWidth(), wall.getTexture().getHeight());
                        }
                    }
                    else{
                        if (wall.isMoveWall() && (Math.abs(wall.getWallMove() - wall.getPosWall().y) > 0)) {
                            wall.setPosWall(wall.getPosWall().x + (wall.getWallMove() - wall.getPosWall().x) / WALLSPEED, wall.getPosWall().y);
                            wall.setWallBounds(wall.getPosWall().x, wall.getPosWall().y, wall.getTexture().getWidth(), wall.getTexture().getHeight());
                        }
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
                    if (Intersector.overlaps(planet.getObstacleCircle(), quokka.getQuokkaBounds())) {
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
                    planetDistance.set(quokka.getPosition().x + quokka.getTexture().getWidth() / 2 - planet.getPosObstacle().x - planet.getTexture().getWidth() / 2, quokka.getPosition().y + quokka.getTexture().getHeight() / 2 - planet.getPosObstacle().y - planet.getTexture().getHeight() / 2, 0);
                    double planetMagnitude = MAGSCALER * Math.sqrt(Math.pow(planetDistance.x, 2) + Math.pow(planetDistance.y, 2));
                    if (planetMagnitude != 0) {
                        planetDistance.scl((float) (GOODGRAV / Math.pow(planetMagnitude, GRAVPOW)));
                    }
                    quokka.getGravity().add(planetDistance.x, planetDistance.y, 0);
                }
                for (Obstacle planet : blackHoles) {
                    if (Intersector.overlaps(planet.getObstacleCircle(), quokka.getQuokkaBounds())) {
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
                                gsm.set(new PlayState(gsm, world, level));
                                break;
                            }
                        }
                    }
                    planetDistance.set(quokka.getPosition().x + quokka.getTexture().getWidth() / 2 - planet.getPosObstacle().x - planet.getTexture().getWidth() / 2, quokka.getPosition().y + quokka.getTexture().getHeight() / 2 - planet.getPosObstacle().y - planet.getTexture().getHeight() / 2, 0);
                    double planetMagnitude = MAGSCALER * Math.sqrt(Math.pow(planetDistance.x, 2) + Math.pow(planetDistance.y, 2));
                    if (planetMagnitude != 0) {
                        planetDistance.scl((float) (GOODGRAV / Math.pow(planetMagnitude, GRAVPOW)));
                    }
                    quokka.getGravity().add(planetDistance.x, planetDistance.y, 0);
                }
                for (Obstacle nebula : nebulae) {
                    planetDistance.set(quokka.getPosition().x + quokka.getTexture().getWidth() / 2 - nebula.getPosObstacle().x - nebula.getTexture().getWidth() / 2, quokka.getPosition().y + quokka.getTexture().getHeight() / 2 - nebula.getPosObstacle().y - nebula.getTexture().getHeight() / 2, 0);
                    double planetMagnitude = MAGSCALER * Math.sqrt(Math.pow(planetDistance.x, 2) + Math.pow(planetDistance.y, 2));
                    if (planetMagnitude != 0) {
                        planetDistance.scl((float) (GOODGRAV / Math.pow(planetMagnitude, GRAVPOW)));
                    }
                    quokka.getGravity().add(planetDistance.x, planetDistance.y, 0);
                }
                justPlanet = justPlanetTemp;
                if (planets.size == 0 && nebulae.size == 0 && blackHoles.size == 0) {
                    quokka.getGravity().set(0, -13, 0);
                }
                for (Meteor meteor : meteors) {
                    if(camUpdate) {
                        if (meteor.getPosMeteor().x < cam.position.x + cam.viewportWidth / 7) {
                            meteor.setStartFall(true);
                        }
                    }
                    if (meteor.isStartFall()) {
                        if (Intersector.overlaps(meteor.getMeteorCircle(), quokka.getQuokkaBounds())) {
                            if (meteors.contains(meteor, true)) {
                                gsm.set(new PlayState(gsm, world, level));
                                break;
                            }
                        }
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
                    }
                }
                for (Airplane airplane : airplanes) {
                    if (airplane.getPosAirplane().x < (cam.position.x - cam.viewportWidth / 2)) {
                        airplane.setStartFall(true);
                    }
                    if (airplane.isStartFall()) {
                        if (airplane.collides(quokka.getQuokkaBounds())) {
                            if (airplanes.contains(airplane, true)) {
                                gsm.set(new PlayState(gsm, world, level));
                                break;
                            }
                        }
                        airplane.move(dt);
                    }
                }
                for (Airplane airplane : tropicBirds) {
                    if (airplane.getPosAirplane().x < (cam.position.x - cam.viewportWidth / 2)) {
                        airplane.setStartFall(true);
                    }
                    if (airplane.isStartFall()) {
                        if (airplane.collides(quokka.getQuokkaBounds())) {
                            if (airplanes.contains(airplane, true)) {
                                gsm.set(new PlayState(gsm, world, level));
                                break;
                            }
                        }
                        airplane.move(dt);
                    }
                }
                for (Airplane airplane : tropicFish) {
                    if (airplane.getPosAirplane().x < (cam.position.x - cam.viewportWidth / 2)) {
                        airplane.setStartFall(true);
                    }
                    if (airplane.isStartFall()) {
                        if (airplane.collides(quokka.getQuokkaBounds())) {
                            if (airplanes.contains(airplane, true)) {
                                gsm.set(new PlayState(gsm, world, level));
                                break;
                            }
                        }
                        airplane.move(dt);
                    }
                }
                boolean touchingPortal = false;
                boolean touchedPortal = false;
                for(Obstacle portal : portals){
                    if (Intersector.overlaps(portal.getObstacleCircle(), quokka.getQuokkaBounds())) {
                        touchingPortal = true;
                        if(!(quokka.isTouchingPortal() || touchedPortal)) {
                            touchedPortal = true;
                            if (portals.indexOf(portal, true) % 2 == 0) {
                                quokka.setPosition(portals.get(portals.indexOf(portal, true) + 1).getPosObstacle().x + quokka.getPosition().x - portal.getPosObstacle().x, portals.get(portals.indexOf(portal, true) + 1).getPosObstacle().y + quokka.getPosition().y - portal.getPosObstacle().y);
                                velocityTemp2.set(quokka.getVelocity().x / quokka.getVelocity().len(), quokka.getVelocity().y / quokka.getVelocity().len(), 0);
                                float currentPot = 0;
                                if (planets.size > 0 || nebulae.size > 0 || blackHoles.size > 0) {
                                    tempGrav.set(0, 0, 0);
                                    for (Obstacle planet : planets) {
                                        planetDistance.set(Math.abs(quokka.getPosition().x + quokka.getTexture().getWidth() / 2 - planet.getPosObstacle().x - planet.getTexture().getWidth() / 2), Math.abs(quokka.getPosition().y + quokka.getTexture().getHeight() / 2 - planet.getPosObstacle().y - planet.getTexture().getHeight() / 2), 0);
                                        double planetMagnitude = MAGSCALER * Math.sqrt(Math.pow(planetDistance.x, 2) + Math.pow(planetDistance.y, 2));
                                        currentPot += GOODGRAV / Math.pow(planetMagnitude, GRAVPOW - 1);
                                    }
                                    for (Obstacle nebula : nebulae) {
                                        planetDistance.set(Math.abs(quokka.getPosition().x + quokka.getTexture().getWidth() / 2 - nebula.getPosObstacle().x - nebula.getTexture().getWidth() / 2), Math.abs(quokka.getPosition().y + quokka.getTexture().getHeight() / 2 - nebula.getPosObstacle().y - nebula.getTexture().getHeight() / 2), 0);
                                        double planetMagnitude = MAGSCALER * Math.sqrt(Math.pow(planetDistance.x, 2) + Math.pow(planetDistance.y, 2));
                                        currentPot += GOODGRAV / Math.pow(planetMagnitude, GRAVPOW - 1);
                                    }
                                    for (Obstacle planet : blackHoles) {
                                        planetDistance.set(Math.abs(quokka.getPosition().x + quokka.getTexture().getWidth() / 2 - planet.getPosObstacle().x - planet.getTexture().getWidth() / 2), Math.abs(quokka.getPosition().y + quokka.getTexture().getHeight() / 2 - planet.getPosObstacle().y - planet.getTexture().getHeight() / 2), 0);
                                        double planetMagnitude = MAGSCALER * Math.sqrt(Math.pow(planetDistance.x, 2) + Math.pow(planetDistance.y, 2));
                                        currentPot += GOODGRAV / Math.pow(planetMagnitude, GRAVPOW - 1);
                                    }
                                } else {
                                    currentPot = -1 * quokka.getGravity().y * quokka.getPosition().y;
                                }
                                //coachLandmark
                                velocityTemp2.scl((float) Math.sqrt(Math.abs(2.0 * (iniPot - currentPot))));
                                velocityTemp2.scl((float) (1.0 / Math.sqrt(currentDT)));
                                quokka.getVelocity().set(velocityTemp2);
                            } else {
                                quokka.setPosition(portals.get(portals.indexOf(portal, true) - 1).getPosObstacle());
                                velocityTemp2.set(quokka.getVelocity().x / quokka.getVelocity().len(), quokka.getVelocity().y / quokka.getVelocity().len(), 0);
                                float currentPot = 0;
                                if (planets.size > 0 || nebulae.size > 0 || blackHoles.size > 0) {
                                    tempGrav.set(0, 0, 0);
                                    for (Obstacle planet : planets) {
                                        planetDistance.set(Math.abs(quokka.getPosition().x + quokka.getTexture().getWidth() / 2 - planet.getPosObstacle().x - planet.getTexture().getWidth() / 2), Math.abs(quokka.getPosition().y + quokka.getTexture().getHeight() / 2 - planet.getPosObstacle().y - planet.getTexture().getHeight() / 2), 0);
                                        double planetMagnitude = MAGSCALER * Math.sqrt(Math.pow(planetDistance.x, 2) + Math.pow(planetDistance.y, 2));
                                        currentPot += GOODGRAV / Math.pow(planetMagnitude, GRAVPOW - 1);
                                    }
                                    for (Obstacle nebula : nebulae) {
                                        planetDistance.set(Math.abs(quokka.getPosition().x + quokka.getTexture().getWidth() / 2 - nebula.getPosObstacle().x - nebula.getTexture().getWidth() / 2), Math.abs(quokka.getPosition().y + quokka.getTexture().getHeight() / 2 - nebula.getPosObstacle().y - nebula.getTexture().getHeight() / 2), 0);
                                        double planetMagnitude = MAGSCALER * Math.sqrt(Math.pow(planetDistance.x, 2) + Math.pow(planetDistance.y, 2));
                                        currentPot += GOODGRAV / Math.pow(planetMagnitude, GRAVPOW - 1);
                                    }
                                    for (Obstacle planet : blackHoles) {
                                        planetDistance.set(Math.abs(quokka.getPosition().x + quokka.getTexture().getWidth() / 2 - planet.getPosObstacle().x - planet.getTexture().getWidth() / 2), Math.abs(quokka.getPosition().y + quokka.getTexture().getHeight() / 2 - planet.getPosObstacle().y - planet.getTexture().getHeight() / 2), 0);
                                        double planetMagnitude = MAGSCALER * Math.sqrt(Math.pow(planetDistance.x, 2) + Math.pow(planetDistance.y, 2));
                                        currentPot += GOODGRAV / Math.pow(planetMagnitude, GRAVPOW - 1);
                                    }
                                } else {
                                    currentPot = -1 * quokka.getGravity().y * quokka.getPosition().y;
                                }
                                //coachLandmark
                                velocityTemp2.scl((float) Math.sqrt(Math.abs(2.0 * (iniPot - currentPot))));
                                velocityTemp2.scl((float) (1.0 / Math.sqrt(currentDT)));
                                quokka.getVelocity().set(velocityTemp2);
                            }
                        }
                    }
                }
                quokka.setTouchingPortal(touchingPortal);
                for(Obstacle brush : brushes){
                    brush.move(dt);
                    pointCounter = 0;
                    float netDistance = 0;
                    int i = brush.getMoveTracker() - 1;
                    justHitBrushTemp = false;
                        if (lineBounce(brush.getPosObstacle(), distance(brush.getPosObstacle(), brush.getMoveSpots().get(i = (i >= 0 ? i : brush.getMoveSpots().size + i))) > brush.getLineback() ? pointBetween(brush.getPosObstacle(), brush.getMoveSpots().get(i = (i >= 0 ? i : brush.getMoveSpots().size + i)), brush.getLineback(), true) : brush.getMoveSpots().get(i = (i >= 0 ? i : brush.getMoveSpots().size + i)))) {
                            justHitBrushTemp = true;
                            break;
                        }
                        netDistance += distance(brush.getPosObstacle(), brush.getMoveSpots().get(i = i >= 0 ? i : brush.getMoveSpots().size + i));
                        while (true) {
                            if (i != 0) {
                                i = i > 0 ? i : brush.getMoveSpots().size + i;
                                netDistance += distance(brush.getMoveSpots().get(i), brush.getMoveSpots().get(i - 1));
                                if (netDistance > brush.getTotalDistance()) {
                                    break;
                                }
                                pointCounter++;
                                if (lineBounce(brush.getMoveSpots().get(i), brush.getMoveSpots().get(i - 1))) {
                                    justHitBrushTemp = true;
                                    break;
                                }
                            } else {
                                netDistance += distance(brush.getMoveSpots().get(0), brush.getMoveSpots().get(brush.getMoveSpots().size - 1));
                                if (netDistance > brush.getTotalDistance()) {
                                    break;
                                }
                                pointCounter++;
                                if (lineBounce(brush.getMoveSpots().get(0), brush.getMoveSpots().get(brush.getMoveSpots().size - 1))) {
                                    justHitBrushTemp = true;
                                    break;
                                }
                            }
                            i--;
                        }
                        if (brush.getMoveTracker() - pointCounter > 1) {
                            if (lineBounce(brush.getMoveSpots().get(brush.getMoveTracker() - pointCounter - 1), pointBetween(brush.getMoveSpots().get(brush.getMoveTracker() - pointCounter - 1), brush.getMoveSpots().get(brush.getMoveTracker() - pointCounter - 2), (netDistance - brush.getTotalDistance()) > distance(brush.getMoveSpots().get(brush.getMoveTracker() - pointCounter - 1), brush.getMoveSpots().get(brush.getMoveTracker() - pointCounter - 2)) ? (float) distance(brush.getMoveSpots().get(brush.getMoveTracker() - pointCounter - 1), brush.getMoveSpots().get(brush.getMoveTracker() - pointCounter - 2)) : (netDistance - brush.getTotalDistance())))) {
                                justHitBrushTemp = true;
                                break;
                            }
                        } else if (brush.getMoveTracker() - pointCounter == 1) {
                            if (lineBounce(brush.getMoveSpots().get(0), pointBetween(brush.getMoveSpots().get(0), brush.getMoveSpots().get(brush.getMoveSpots().size - 1), (netDistance - brush.getTotalDistance()) > distance(brush.getMoveSpots().get(0), brush.getMoveSpots().get(brush.getMoveSpots().size - 1)) ? (float) distance(brush.getMoveSpots().get(0), brush.getMoveSpots().get(brush.getMoveSpots().size - 1)) : (float) (netDistance - brush.getTotalDistance())))) {
                                justHitBrushTemp = true;
                                break;
                            }
                        } else {
                            if (lineBounce(brush.getMoveSpots().get(brush.getMoveSpots().size + brush.getMoveTracker() - pointCounter - 1), pointBetween(brush.getMoveSpots().get(brush.getMoveSpots().size + brush.getMoveTracker() - pointCounter - 1), brush.getMoveSpots().get(brush.getMoveSpots().size + brush.getMoveTracker() - pointCounter - 2), (netDistance - brush.getTotalDistance()) > distance(brush.getMoveSpots().get(brush.getMoveSpots().size + brush.getMoveTracker() - pointCounter - 1), brush.getMoveSpots().get(brush.getMoveSpots().size + brush.getMoveTracker() - pointCounter - 2)) ? (float) distance(brush.getMoveSpots().get(brush.getMoveSpots().size + brush.getMoveTracker() - pointCounter - 1), brush.getMoveSpots().get(brush.getMoveSpots().size + brush.getMoveTracker() - pointCounter - 2)) : (netDistance - brush.getTotalDistance())))) {
                                justHitBrushTemp = true;
                                break;
                            }
                        }
                        justHitBrush = justHitBrushTemp;
                        pointCounter = 0;
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
                iniPot += -1 * quokka.getGravity().y * towerVel.y;
                if (lineDraw) {
                    clickPosTemp.set(clickPosTemp.x, clickPosTemp.y + towerVel.y, 0);
                }
                towerVel.scl(1 / dt);
            } else {
                cam.position.x = quokka.getPosition().x + 80;
                if (lineDraw) {
                    clickPosTemp.set(clickPosTemp.x + quokka.getBottomLeft2().x - quokka.getBottomLeft().x, clickPosTemp.y, 0);
                }
                camUpdate = true;
            }
            backButton.getPosButton().x = cam.position.x - cam.viewportWidth / 2 + 15;
            pauseButton.getPosButton().x = cam.position.x - cam.viewportWidth / 2 + 80;
            backButton.getPosButton().y = cam.position.y + cam.viewportHeight / 2 - 65;
            pauseButton.getPosButton().y = cam.position.y + cam.viewportHeight / 2 - 65;
            backButton.getButtonBounds().set(backButton.getPosButton().x, backButton.getPosButton().y, backButton.getButtonBounds().getWidth(), backButton.getButtonBounds().getHeight());
            pauseButton.getButtonBounds().set(pauseButton.getPosButton().x, pauseButton.getPosButton().y, pauseButton.getButtonBounds().getWidth(), pauseButton.getButtonBounds().getHeight());
            if (shouldFall && !smallBounce) {
                boolean inGust = false;
                for(Obstacle windGust : windGusts){
                    if(windGust.collides(quokka.getQuokkaBounds())){
                        inGust = true;
                    }
                }
                quokka.update(dt, inGust);
            } //gohere
            if (lineCheck && !hitWall) {
                if (justHit) {
                    if (((quokka.getPosition().x > clickPos.x) && (quokka.getPosition().x < clickPos2.x)) || ((quokka.getPosition().x < clickPos.x) && (quokka.getPosition().x > clickPos2.x))) {
                        while (quokka.getQuokkaBounds().contains(quokka.getPosition().x, lineY(quokka.getPosition().x)) || (quokka.getQuokkaBounds().contains(quokka.getPosition().x + quokka.getTexture().getWidth(), lineY(quokka.getPosition().x + quokka.getTexture().getWidth())))) {
                            clickPos.set(clickPos.x, clickPos.y- 10, 0);
                            clickPos2.set(clickPos2.x, clickPos2.y- 10, 0);
                        }
                    }
                    else if ((((quokka.getPosition().x + quokka.getTexture().getWidth()) > clickPos.x) && ((quokka.getPosition().x + quokka.getTexture().getWidth()) < clickPos2.x)) || (((quokka.getPosition().x + quokka.getTexture().getWidth()) < clickPos.x) && ((quokka.getPosition().x + quokka.getTexture().getWidth()) > clickPos2.x))) {
                        while (quokka.getQuokkaBounds().contains(quokka.getPosition().x, lineY(quokka.getPosition().x)) ||quokka.getQuokkaBounds().contains(quokka.getPosition().x + quokka.getTexture().getWidth(), lineY(quokka.getPosition().x + quokka.getTexture().getWidth()))) {
                            clickPos.set(clickPos.x, clickPos.y- 10, 0);
                            clickPos2.set(clickPos2.x, clickPos2.y- 10, 0);
                        }
                    }
                    clickPos2d.set(clickPos.x, clickPos.y);
                    clickPos2d2.set(clickPos2.x, clickPos2.y);
                    while(doIntersect(quokka.getUpperLeft2(), quokka.getUpperRight2(), clickPos2d, clickPos2d2) && (doIntersect(quokka.getBottomLeft2(), quokka.getBottomRight2(), clickPos2d, clickPos2d2) || doIntersect(quokka.getBottomLeft2(), quokka.getUpperLeft2(), clickPos2d, clickPos2d2) || doIntersect(quokka.getBottomRight2(), quokka.getUpperRight2(), clickPos2d, clickPos2d2))){
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
                    if(bonusQuokkas.size > 0) {
                        if (level == 10) {
                            gsm.set(new MenuState(gsm, world + 1, 1, collectedQuokkas.get(0)));
                        }
                        else {
                            gsm.set(new MenuState(gsm, world, level + 1, collectedQuokkas.get(0)));
                        }
                    }
                    else{
                        if (level == 10) {
                            gsm.set(new MenuState(gsm, world + 1, 1, false));
                        }
                        else {
                            gsm.set(new MenuState(gsm, world, level + 1, false));
                        }
                    }
                }
            }
            cam.update();
        }
    }
    private void planetFixer(){
        float currentPot = 0f;
        velocityTemp2.set(velocityTemp2.x /velocityTemp2.len(), velocityTemp2.y/velocityTemp2.len(), 0);
        tempGrav.set(0, 0, 0);
        for(int i = 0; i < planets.size; i++){
            planetDistance.set(Math.abs(quokka.getPosition().x + quokka.getTexture().getWidth() / 2 - planets.get(i).getPosObstacle().x - planets.get(i).getTexture().getWidth() / 2), Math.abs(quokka.getPosition().y + quokka.getTexture().getHeight() / 2 - planets.get(i).getPosObstacle().y - planets.get(i).getTexture().getHeight() / 2), 0);
            double planetMagnitude = MAGSCALER * Math.sqrt(Math.pow(planetDistance.x, 2) + Math.pow(planetDistance.y, 2));
            currentPot += GOODGRAV / Math.pow(planetMagnitude, GRAVPOW - 1);
        }
        if(nebulae.size > 0){
            for(int i = 0; i < nebulae.size; i++){
                planetDistance.set(Math.abs(quokka.getPosition().x + quokka.getTexture().getWidth() / 2 - nebulae.get(i).getPosObstacle().x - nebulae.get(i).getTexture().getWidth() / 2), Math.abs(quokka.getPosition().y + quokka.getTexture().getHeight() / 2 - nebulae.get(i).getPosObstacle().y - nebulae.get(i).getTexture().getHeight() / 2), 0);
                double planetMagnitude = MAGSCALER * Math.sqrt(Math.pow(planetDistance.x, 2) + Math.pow(planetDistance.y, 2));
                currentPot += GOODGRAV / Math.pow(planetMagnitude, GRAVPOW - 1);
            }
        }
        if(blackHoles.size > 0){
            for(int i = 0; i < blackHoles.size; i++){
                planetDistance.set(Math.abs(quokka.getPosition().x + quokka.getTexture().getWidth() / 2 - blackHoles.get(i).getPosObstacle().x - blackHoles.get(i).getTexture().getWidth() / 2), Math.abs(quokka.getPosition().y + quokka.getTexture().getHeight() / 2 - blackHoles.get(i).getPosObstacle().y - blackHoles.get(i).getTexture().getHeight() / 2), 0);
                double planetMagnitude = MAGSCALER * Math.sqrt(Math.pow(planetDistance.x, 2) + Math.pow(planetDistance.y, 2));
                currentPot += GOODGRAV / Math.pow(planetMagnitude, GRAVPOW - 1);
            }
        }
        //coachLandmark
        velocityTemp2.scl((float) Math.sqrt(Math.abs(2.0*(iniPot - currentPot))));
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

    private float lineY(float x, Vector2 p1, Vector2 p2){
        if(p2.x > p1.x) {
            final float slope = (p2.y - p1.y) / (p2.x - p1.x);
            return (slope * (x - p1.x) + p1.y);
        }
        else{
            final float slope = (p1.y - p2.y) / (p1.x - p2.x);
            return (slope * (x - p2.x) + p2.y);
        }
    }

    private boolean lineBounce(Vector2 p1, Vector2 p2){
        if(!justHitBrush) {
            boolean hitCorner = false;
            boolean bounce1 = false;
            boolean bounce2 = false;
            boolean bounce3 = false;
            boolean bounce4 = false;
            boolean bounce5 = false;
            boolean bounce6 = false;
            boolean bounce7 = false;
            boolean bounce8 = false;
            if (doIntersect(quokka.getBottomLeft(), quokka.getBottomLeft2(), p1, p2)) {
                hitCorner = true;
                quokka.setPosition(intersectionPoint(quokka.getBottomLeft(), quokka.getBottomLeft2(), p1, p2).x + 5, intersectionPoint(quokka.getBottomLeft(), quokka.getBottomLeft2(), p1, p2).y + 5);
                bounce1 = true;
            } else if (doIntersect(quokka.getBottomRight(), quokka.getBottomRight2(), p1, p2)) {
                hitCorner = true;
                quokka.setPosition(intersectionPoint(quokka.getBottomRight(), quokka.getBottomRight2(), p1, p2).x - quokka.getQuokkaBounds().getWidth() - 5, intersectionPoint(quokka.getBottomRight(), quokka.getBottomRight2(), p1, p2).y + 5);
                bounce2 = true;
            } else if (doIntersect(quokka.getUpperRight(), quokka.getUpperRight2(), p1, p2)) {
                hitCorner = true;
                quokka.setPosition(intersectionPoint(quokka.getUpperRight(), quokka.getUpperRight2(), p1, p2).x - quokka.getQuokkaBounds().getWidth() - 5, intersectionPoint(quokka.getUpperRight(), quokka.getUpperRight2(), p1, p2).y - quokka.getQuokkaBounds().getHeight() - 5);
                bounce3 = true;
            } else if (doIntersect(quokka.getUpperLeft(), quokka.getUpperLeft2(), p1, p2)) {
                hitCorner = true;
                quokka.setPosition(intersectionPoint(quokka.getUpperLeft(), quokka.getUpperLeft2(), p1, p2).x + 5, intersectionPoint(quokka.getUpperLeft(), quokka.getUpperLeft2(), p1, p2).y - quokka.getQuokkaBounds().getHeight() - 5);
                bounce4 = true;
            }
            if (!hitCorner) {
                if (doIntersect(quokka.getBottomLeft2(), quokka.getBottomRight2(), p1, p2)) {
                    //quokka.setPosition(quokka.getPosition().x, );
                    hitCorner = true;
                }
                if (doIntersect(quokka.getUpperLeft2(), quokka.getUpperRight2(), p1, p2)) {
                    hitCorner = true;
                }
                if (doIntersect(quokka.getBottomLeft2(), quokka.getUpperLeft2(), p1, p2)) {
                    hitCorner = true;
                }
                if (doIntersect(quokka.getBottomRight2(), quokka.getUpperRight2(), p1, p2)) {
                    hitCorner = true;
                }
            }
            if(hitCorner) {
                if (((quokka.getPosition().x > p1.x) && (quokka.getPosition().x < p2.x)) || ((quokka.getPosition().x < p1.x) && (quokka.getPosition().x > p2.x))) {
                    while (quokka.getQuokkaBounds().contains(quokka.getPosition().x, lineY(quokka.getPosition().x, p1, p2)) || (quokka.getQuokkaBounds().contains(quokka.getPosition().x + quokka.getTexture().getWidth(), lineY(quokka.getPosition().x + quokka.getTexture().getWidth(), p1, p2)))) {
                        quokka.setPosition(quokka.getPosition().x, quokka.getPosition().y + 10);
                        bounce5 = true;
                        //System.out.println("shift1");
                    }
                } else if ((((quokka.getPosition().x + quokka.getTexture().getWidth()) > p1.x) && ((quokka.getPosition().x + quokka.getTexture().getWidth()) < p2.x)) || (((quokka.getPosition().x + quokka.getTexture().getWidth()) < p1.x) && ((quokka.getPosition().x + quokka.getTexture().getWidth()) > p2.x))) {
                    while (quokka.getQuokkaBounds().contains(quokka.getPosition().x, lineY(quokka.getPosition().x, p1, p2)) || quokka.getQuokkaBounds().contains(quokka.getPosition().x + quokka.getTexture().getWidth(), lineY(quokka.getPosition().x + quokka.getTexture().getWidth(), p1, p2))) {
                        quokka.setPosition(quokka.getPosition().x, quokka.getPosition().y + 10);
                        bounce6 = true;
                        //System.out.println("shift2");
                    }
                }
                while (doIntersect(quokka.getUpperLeft2(), quokka.getUpperRight2(), p1, p2) && (doIntersect(quokka.getBottomLeft2(), quokka.getBottomRight2(), p1, p2) || doIntersect(quokka.getBottomLeft2(), quokka.getUpperLeft2(), p1, p2) || doIntersect(quokka.getBottomRight2(), quokka.getUpperRight2(), p1, p2))) {
                    if (quokka.getVelocity().x < 0) {
                        quokka.setPosition(quokka.getPosition().x - 10, quokka.getPosition().y);
                        bounce7 = true;
                        //System.out.println("shift3");
                    } else {
                        quokka.setPosition(quokka.getPosition().x + 10, quokka.getPosition().y);
                        bounce8 = true;
                        //System.out.println("shift4");
                    }
                }
            }
            if(!(bounce5 || bounce6 || bounce7 || bounce8)) {
                if (bounce1) {
                    quokka.setVelocity(resultVector(quokka.getVelocity(), p1, p2));
                } else if (bounce2) {
                    quokka.setVelocity(resultVector(quokka.getVelocity(), p1, p2));
                } else if (bounce3) {
                    quokka.setVelocity(resultVector(quokka.getVelocity(), p1, p2));
                } else if (bounce4) {
                    quokka.setVelocity(resultVector(quokka.getVelocity(), p1, p2));
                }
            }
            else{
                quokka.setVelocity(resultVector(quokka.getVelocity(), perpPoint(p1, p2), p2));
            }
            return hitCorner;
        }
        else{
            boolean hitCorner = false;
            boolean bounce1 = false;
            boolean bounce2 = false;
            boolean bounce3 = false;
            boolean bounce4 = false;
            boolean bounce5 = false;
            boolean bounce6 = false;
            boolean bounce7 = false;
            boolean bounce8 = false;
            if (doIntersect(quokka.getBottomLeft(), quokka.getBottomLeft2(), p1, p2)) {
                hitCorner = true;
                quokka.setPosition(intersectionPoint(quokka.getBottomLeft(), quokka.getBottomLeft2(), p1, p2).x + 5, intersectionPoint(quokka.getBottomLeft(), quokka.getBottomLeft2(), p1, p2).y + 5);
                bounce1 = true;
            } else if (doIntersect(quokka.getBottomRight(), quokka.getBottomRight2(), p1, p2)) {
                hitCorner = true;
                quokka.setPosition(intersectionPoint(quokka.getBottomRight(), quokka.getBottomRight2(), p1, p2).x - quokka.getQuokkaBounds().getWidth() - 5, intersectionPoint(quokka.getBottomRight(), quokka.getBottomRight2(), p1, p2).y + 5);
                bounce2 = true;
            } else if (doIntersect(quokka.getUpperRight(), quokka.getUpperRight2(), p1, p2)) {
                hitCorner = true;
                quokka.setPosition(intersectionPoint(quokka.getUpperRight(), quokka.getUpperRight2(), p1, p2).x - quokka.getQuokkaBounds().getWidth() - 5, intersectionPoint(quokka.getUpperRight(), quokka.getUpperRight2(), p1, p2).y - quokka.getQuokkaBounds().getHeight() - 5);
                bounce3 = true;
            } else if (doIntersect(quokka.getUpperLeft(), quokka.getUpperLeft2(), p1, p2)) {
                hitCorner = true;
                quokka.setPosition(intersectionPoint(quokka.getUpperLeft(), quokka.getUpperLeft2(), p1, p2).x + 5, intersectionPoint(quokka.getUpperLeft(), quokka.getUpperLeft2(), p1, p2).y - quokka.getQuokkaBounds().getHeight() - 5);
                bounce4 = true;
            }
            if (!hitCorner) {
                if (doIntersect(quokka.getBottomLeft2(), quokka.getBottomRight2(), p1, p2)) {
                    //quokka.setPosition(quokka.getPosition().x, );
                    hitCorner = true;
                }
                if (doIntersect(quokka.getUpperLeft2(), quokka.getUpperRight2(), p1, p2)) {
                    hitCorner = true;
                }
                if (doIntersect(quokka.getBottomLeft2(), quokka.getUpperLeft2(), p1, p2)) {
                    hitCorner = true;
                }
                if (doIntersect(quokka.getBottomRight2(), quokka.getUpperRight2(), p1, p2)) {
                    hitCorner = true;
                }
            }
            if(hitCorner) {
                if (((quokka.getPosition().x > p1.x) && (quokka.getPosition().x < p2.x)) || ((quokka.getPosition().x < p1.x) && (quokka.getPosition().x > p2.x))) {
                    while (quokka.getQuokkaBounds().contains(quokka.getPosition().x, lineY(quokka.getPosition().x, p1, p2)) || (quokka.getQuokkaBounds().contains(quokka.getPosition().x + quokka.getTexture().getWidth(), lineY(quokka.getPosition().x + quokka.getTexture().getWidth(), p1, p2)))) {
                        quokka.setPosition(quokka.getPosition().x, quokka.getPosition().y + 10);
                        bounce5 = true;
                        //System.out.println("shift1");
                    }
                } else if ((((quokka.getPosition().x + quokka.getTexture().getWidth()) > p1.x) && ((quokka.getPosition().x + quokka.getTexture().getWidth()) < p2.x)) || (((quokka.getPosition().x + quokka.getTexture().getWidth()) < p1.x) && ((quokka.getPosition().x + quokka.getTexture().getWidth()) > p2.x))) {
                    while (quokka.getQuokkaBounds().contains(quokka.getPosition().x, lineY(quokka.getPosition().x, p1, p2)) || quokka.getQuokkaBounds().contains(quokka.getPosition().x + quokka.getTexture().getWidth(), lineY(quokka.getPosition().x + quokka.getTexture().getWidth(), p1, p2))) {
                        quokka.setPosition(quokka.getPosition().x, quokka.getPosition().y + 10);
                        bounce6 = true;
                        //System.out.println("shift2");
                    }
                }
                while (doIntersect(quokka.getUpperLeft2(), quokka.getUpperRight2(), p1, p2) && (doIntersect(quokka.getBottomLeft2(), quokka.getBottomRight2(), p1, p2) || doIntersect(quokka.getBottomLeft2(), quokka.getUpperLeft2(), p1, p2) || doIntersect(quokka.getBottomRight2(), quokka.getUpperRight2(), p1, p2))) {
                    if (quokka.getVelocity().x < 0) {
                        quokka.setPosition(quokka.getPosition().x - 10, quokka.getPosition().y);
                        bounce7 = true;
                        //System.out.println("shift3");
                    } else {
                        quokka.setPosition(quokka.getPosition().x + 10, quokka.getPosition().y);
                        bounce8 = true;
                        //System.out.println("shift4");
                    }
                }
            }
            if(!(bounce5 || bounce6 || bounce7 || bounce8)) {
                if (bounce1) {
                    quokka.setVelocity(resultVector(quokka.getVelocity(), p1, p2));
                } else if (bounce2) {
                    quokka.setVelocity(resultVector(quokka.getVelocity(), p1, p2));
                } else if (bounce3) {
                    quokka.setVelocity(resultVector(quokka.getVelocity(), p1, p2));
                } else if (bounce4) {
                    quokka.setVelocity(resultVector(quokka.getVelocity(), p1, p2));
                }
            }
            else{
                quokka.setVelocity(resultVector(quokka.getVelocity(), perpPoint(p1, p2), p2));
            }
            return hitCorner;
        }
    }

    private Vector2 perpPoint(Vector2 p1, Vector2 p2){
        if(p1.y == p2.y) {
            temp2.set(p1.x, p1.y + 100);
        }
        else if (p1.x == p2.x){
            temp2.set(p1.x + 100, p1.y);
        }
        else{
            temp2.set(-1 * (p2.x - p1.x) + p1.x, p2.y);
        }
        return temp2;
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

    private Vector2 pointBetween(Vector2 p1, Vector2 p2, float distanceBetween){
        final double x;
        final double y;
        if(p1.x != p2.x) {
            final float slope = (p1.y - p2.y) / (p1.x - p2.x);
            final float yInt = p2.y - slope * p2.x;
            x = p2.x - (distanceBetween * (p2.x - p1.x)) / distance(p2, p1);
            y = slope * x + yInt;
        }
        else if (p2.y > p1.y){
            x = p1.x;
            y = p2.y - distanceBetween;
        }
        else{
            x = p1.x;
            y = p2.y + distanceBetween;
        }
        tempBack.set((float) x, (float) y);
        return tempBack;
    }

    private Vector2 pointBetween(Vector2 p1, Vector2 p2, float distanceBetween, boolean quokBack){
        final double x;
        final double y;
        if(p1.x != p2.x) {
            final float slope = (p2.y - p1.y) / (p2.x - p1.x);
            final float yInt = p1.y - slope * p1.x;
            x = p1.x - (distanceBetween * (p1.x - p2.x)) / distance(p1, p2);
            y = slope * x + yInt;
        }
        else if (p2.y > p1.y){
            x = p1.x;
            y = p1.y + distanceBetween;
        }
        else{
            x = p1.x;
            y = p1.y - distanceBetween;
        }
        tempBack.set((float) x, (float) y);
        return tempBack;
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
        sb.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setProjectionMatrix(cam.combined);
        shapeRenderer.setAutoShapeType(true);
        for(Obstacle windGust :windGusts){
            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.rect(windGust.getPosObstacle().x, windGust.getPosObstacle().y, windGust.getObstacleBounds().getWidth(), windGust.getObstacleBounds().getHeight());
        }
        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
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
        for(Wall wall : walls){
            sb.draw(wall.getTexture(), wall.getPosWall().x, wall.getPosWall().y);
        }
        for(Obstacle wallSwitch: switches){
            sb.draw(wallSwitch.getTexture(), wallSwitch.getPosObstacle().x, wallSwitch.getPosObstacle().y);
        }
        for(Obstacle brush: brushes){
            sb.draw(brush.getTexture(), brush.getPosObstacle().x, brush.getPosObstacle().y);
        }
        for (Hawk hawk : hawks){
            sb.draw(hawk.getTexture(), hawk.getPosHawk().x, hawk.getPosHawk().y);
        }
        for(LaserGun laserGun : laserGuns){
            sb.draw(laserGun.getGunRegion(), laserGun.getPosGun().x, laserGun.getPosGun().y, laserGun.getGunRegion().getRegionWidth() / 2, laserGun.getGunRegion().getRegionHeight() / 2, laserGun.getGunRegion().getRegionWidth(), laserGun.getGunRegion().getRegionHeight(), 1, 1, laserGun.getGunAngle());
            if(laserGun.isDrawLaser()) {
                sb.draw(laserGun.getMyBeam().getBeamRegion(), laserGun.getMyBeam().getPosBeam().x, laserGun.getMyBeam().getPosBeam().y, laserGun.getMyBeam().getBeamRegion().getRegionWidth() / 2, laserGun.getMyBeam().getBeamRegion().getRegionHeight() / 2, laserGun.getMyBeam().getBeamRegion().getRegionWidth(), laserGun.getMyBeam().getBeamRegion().getRegionHeight(), 1, 1, laserGun.getBeamAngle());
            }
        }
        for(Drone drone: drones){
            sb.draw(drone.getDroneRegion(), drone.getPosDrone().x, drone.getPosDrone().y, drone.getDroneRegion().getRegionWidth() / 2, drone.getDroneRegion().getRegionHeight() / 2, drone.getDroneRegion().getRegionWidth(), drone.getDroneRegion().getRegionHeight(), 1, 1, drone.getDroneAngle());
        }
        for(JumpFish jumpFish: jumpFishes){
            sb.draw(jumpFish.getJumpFishRegion(), jumpFish.getPosJumpFish().x, jumpFish.getPosJumpFish().y, jumpFish.getJumpFishRegion().getRegionWidth() / 2, jumpFish.getJumpFishRegion().getRegionHeight() / 2, jumpFish.getJumpFishRegion().getRegionWidth(), jumpFish.getJumpFishRegion().getRegionHeight(), 1, 1, jumpFish.getJumpFishAngle());
        }
        for(Stoplight stoplight : stoplights){
            sb.draw(stoplight.getTexture(), stoplight.getPosStoplight().x, stoplight.getPosStoplight().y);
        }
        for(Meteor meteor: meteors){
            sb.draw(meteor.getTexture(), meteor.getPosMeteor().x, meteor.getPosMeteor().y);
            if(meteor.getPosMeteor().y > 768){
                //sb.draw(warningTexture, meteor.getPosMeteor().x, 700);
            }
            else if(meteor.getPosMeteor().y + meteor.getTexture().getHeight() < 0){
                //sb.draw(warningTexture, meteor.getPosMeteor().x, 10);
            }
        }
        for(Airplane airplane : airplanes){
            sb.draw(airplane.getTexture(), airplane.getPosAirplane().x, airplane.getPosAirplane().y);
            if(airplane.getPosAirplane().y > 768){
                sb.draw(warningTexture, airplane.getPosAirplane().x, 700);
            }
            else if(airplane.getPosAirplane().y + airplane.getTexture().getHeight() < 0){
                sb.draw(warningTexture, airplane.getPosAirplane().x, 10);
            }
        }
        for(Airplane airplane : tropicBirds){
            sb.draw(airplane.getTexture(), airplane.getPosAirplane().x, airplane.getPosAirplane().y);
            if(airplane.getPosAirplane().y > 768){
                sb.draw(warningTexture, airplane.getPosAirplane().x, 700);
            }
            else if(airplane.getPosAirplane().y + airplane.getTexture().getHeight() < 0){
                sb.draw(warningTexture, airplane.getPosAirplane().x, 10);
            }
        }
        for(Airplane airplane : tropicFish){
            sb.draw(airplane.getTexture(), airplane.getPosAirplane().x, airplane.getPosAirplane().y);
            if(airplane.getPosAirplane().y > 768){
                sb.draw(warningTexture, airplane.getPosAirplane().x, 700);
            }
            else if(airplane.getPosAirplane().y + airplane.getTexture().getHeight() < 0){
                sb.draw(warningTexture, airplane.getPosAirplane().x, 10);
            }
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
        for(Obstacle brush : brushes){
            shapeRenderer.setColor(Color.BROWN);
            pointCounter = 0;
                float netDistance = 0;
                int i = brush.getMoveTracker() - 1;
                shapeRenderer.line(brush.getPosObstacle(), distance(brush.getPosObstacle(),brush.getMoveSpots().get(i = (i >= 0 ? i : brush.getMoveSpots().size + i))) > brush.getLineback() ? pointBetween(brush.getPosObstacle(),brush.getMoveSpots().get(i = (i >= 0 ? i : brush.getMoveSpots().size + i)), brush.getLineback(), true) : brush.getMoveSpots().get(i = (i >= 0 ? i : brush.getMoveSpots().size + i)));
                netDistance += distance(brush.getPosObstacle(), brush.getMoveSpots().get(i = i >= 0 ? i : brush.getMoveSpots().size + i));
                while(true){
                    if(i != 0) {
                        i = i > 0 ? i : brush.getMoveSpots().size + i;
                        netDistance += distance(brush.getMoveSpots().get(i), brush.getMoveSpots().get(i - 1));
                        if (netDistance > brush.getTotalDistance()) {
                            break;
                        }
                        pointCounter++;
                        shapeRenderer.line(brush.getMoveSpots().get(i), brush.getMoveSpots().get(i - 1));
                    }
                    else{
                        netDistance += distance(brush.getMoveSpots().get(0), brush.getMoveSpots().get(brush.getMoveSpots().size - 1));
                        if (netDistance > brush.getTotalDistance()) {
                            break;
                        }
                        pointCounter++;
                        shapeRenderer.line(brush.getMoveSpots().get(0), brush.getMoveSpots().get(brush.getMoveSpots().size - 1));
                    }
                    i--;
                }
                if(brush.getMoveTracker() - pointCounter > 1){
                    shapeRenderer.line(brush.getMoveSpots().get(brush.getMoveTracker() - pointCounter - 1), pointBetween(brush.getMoveSpots().get(brush.getMoveTracker() - pointCounter - 1), brush.getMoveSpots().get(brush.getMoveTracker() - pointCounter - 2), (netDistance - brush.getTotalDistance()) > distance(brush.getMoveSpots().get(brush.getMoveTracker() - pointCounter - 1), brush.getMoveSpots().get(brush.getMoveTracker() - pointCounter - 2)) ? (float) distance(brush.getMoveSpots().get(brush.getMoveTracker() - pointCounter - 1), brush.getMoveSpots().get(brush.getMoveTracker() - pointCounter - 2)) : (netDistance - brush.getTotalDistance())));
                }
                else if (brush.getMoveTracker() - pointCounter == 1) {
                    shapeRenderer.line(brush.getMoveSpots().get(0), pointBetween(brush.getMoveSpots().get(0), brush.getMoveSpots().get(brush.getMoveSpots().size - 1), (netDistance - brush.getTotalDistance()) > distance(brush.getMoveSpots().get(0), brush.getMoveSpots().get(brush.getMoveSpots().size - 1)) ? (float) distance(brush.getMoveSpots().get(0), brush.getMoveSpots().get(brush.getMoveSpots().size - 1)) : (netDistance - brush.getTotalDistance())));
                }
                else{
                    shapeRenderer.line(brush.getMoveSpots().get(brush.getMoveSpots().size + brush.getMoveTracker() - pointCounter - 1), pointBetween(brush.getMoveSpots().get(brush.getMoveSpots().size + brush.getMoveTracker() - pointCounter - 1), brush.getMoveSpots().get(brush.getMoveSpots().size + brush.getMoveTracker() - pointCounter - 2), (netDistance - brush.getTotalDistance()) > distance(brush.getMoveSpots().get(brush.getMoveSpots().size + brush.getMoveTracker() - pointCounter - 1), brush.getMoveSpots().get(brush.getMoveSpots().size + brush.getMoveTracker() - pointCounter - 2)) ? (float) distance(brush.getMoveSpots().get(brush.getMoveSpots().size + brush.getMoveTracker() - pointCounter - 1), brush.getMoveSpots().get(brush.getMoveSpots().size + brush.getMoveTracker() - pointCounter - 2)) : (netDistance - brush.getTotalDistance())));
                }
        }
        shapeRenderer.end();
        sb.begin();
        for(BonusQuokka bonusQuokka : bonusQuokkas){
            if(!collectedQuokkas.get(bonusQuokkas.indexOf(bonusQuokka, false))) {
                sb.draw(bonusQuokka.getTexture(), bonusQuokka.getPosQuokka().x, bonusQuokka.getPosQuokka().y);
            }
        }
        for(Obstacle portal : portals){
            sb.draw(portal.getTexture(), portal.getPosObstacle().x, portal.getPosObstacle().y);
        }
        sb.draw(quokka.getTexture(), quokka.getPosition().x, quokka.getPosition().y);
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
        for(Obstacle blackHole : blackHoles){
            sb.draw(blackHole.getTexture(), blackHole.getPosObstacle().x, blackHole.getPosObstacle().y);
        }
        for(Obstacle nebula : nebulae){
            sb.draw(nebula.getTexture(), nebula.getPosObstacle().x, nebula.getPosObstacle().y);
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
        //warningTexture.dispose();
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
        for(Obstacle blackHole : blackHoles){
            blackHole.dispose();
        }
        for(Obstacle nebula : nebulae){
            nebula.dispose();
        }
        for(Obstacle wallSwitch: switches){
            wallSwitch.dispose();
        }
        for(Stoplight stoplight : stoplights){
            stoplight.dispose();
        }
        for(Obstacle brush: brushes){
            brush.dispose();
        }
        for(MoveWall moveWall : moveWalls){
            moveWall.dispose();
        }
        for(Hawk hawk : hawks){
            hawk.dispose();
        }
        for(LaserGun laserGun : laserGuns){
            laserGun.dispose();
        }
        for(Drone drone : drones){
            drone.dispose();
        }
        for(JumpFish jumpFish : jumpFishes){
            jumpFish.dispose();
        }
        for(Meteor meteor: meteors) {
            meteor.dispose();
        }
        for(Airplane airplane : airplanes){
            airplane.dispose();
        }
        for(Airplane airplane : tropicBirds){
            airplane.dispose();
        }
        for(Airplane airplane : tropicFish){
            airplane.dispose();
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
        for(Obstacle portal : portals){
            portal.dispose();
        }
        backButton.dispose();
        pauseButton.dispose();
        shapeRenderer.dispose();
    }

    private void levelInit(int world, int level){
        backButton = new Button(new Texture("back.png"), 0, 500, 0);
        pauseButton = new Button(new Texture("pause.png"), 0, 300, 0);
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
                        levelBackground = new Texture("dino1Back.png");
                        walls.add(new Wall(500, 450, "stump.png"));
                        meteors.add(new Meteor(675, 780, 0, 0));
                        clouds.add(new EvilCloud(900, 50));
                        bonusQuokkas.add(new BonusQuokka(1200, 50));
                        happyCloud = new HappyCloud(1550, 50);
                        break;
                    case 2:
                        levelBackground = new Texture("dino2Back.png");
                        walls.add(new Wall(450, 380, "stump.png"));
                        switches.add(new Obstacle(650, 120, "wallSwitch.png"));
                        walls.add(new Wall(450, -445, switches, -215, "stump.png"));
                        meteors.add(new Meteor(800, 780, 0, 0));
                        walls.add(new Wall(900, -80, "stump.png"));
                        happyCloud = new HappyCloud(1200, 50);
                        break;
                    case 3:
                        levelBackground = new Texture("dino1Back.png");
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
                        levelBackground = new Texture("dino2Back.png");
                        walls.add(new Wall(350, 500, "stump.png"));
                        tallDinos.add(new TallDino(923, -195, -223, 400));
                        bonusQuokkas.add(new BonusQuokka(0, 50));
                        walls.add(new Wall(1000, 500, "stump.png"));
                        tallDinos.add(new TallDino(1573, -195, 427, 400));
                        happyCloud = new HappyCloud(1000, 0);
                        break;
                    case 5:
                        levelBackground = new Texture("dino1Back.png");
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
                        levelBackground = new Texture("dino2Back.png");
                        switches.add(new Obstacle(400, 0,"wallSwitch.png"));
                        walls.add(new Wall(200, 300, "horizontStump.png"));
                        walls.add(new Wall(672, 422, switches, 1000, "stump.png"));
                        tallDinos.add(new TallDino(200, -305, 672, 200));
                        meteors.add(new Meteor(750, 780, 0, 0));
                        happyCloud = new HappyCloud(900, 150);
                        break;
                    case 7:
                        levelBackground = new Texture("dino1Back.png");
                        walls.add(new Wall(200, -100, "stump.png"));
                        tallDinos.add(new TallDino(350, -100, 1350, 300));
                        walls.add(new Wall(575, 601, "stump.png"));
                        meteors.add(new Meteor(720, 780, 0, 0));
                        clouds.add(new EvilCloud(1070, 550));
                        happyCloud = new HappyCloud(1600, 100);
                        break;
                    case 8:
                        levelBackground = new Texture("dino2Back.png");
                        meteors.add(new Meteor(400, 880, 0, 0));
                        meteors.add(new Meteor(600, 880, 0, 0));
                        meteors.add(new Meteor(800, 880, 0, 0));
                        meteors.add(new Meteor(1000, 880, 0, 0));
                        tallDinos.add(new TallDino(200, -350, 400, 150));
                        tallDinos.add(new TallDino(432, -350, 632, 150));
                        tallDinos.add(new TallDino(1064, -350, 864, 150));
                        tallDinos.add(new TallDino(1296, -350, 1096, 150));
                        meteors.add(new Meteor(1644, 880, 0, 0));
                        tallDinos.add(new TallDino(1528, -250, 1960, 150));
                        happyCloud = new HappyCloud(2192, 150);
                        break;
                    case 9:
                        levelBackground = new Texture("dino1Back.png");
                        windGusts.add(new Obstacle(150, 200, 250, 600));
                        clouds.add(new EvilCloud(150, 0));
                        meteors.add(new Meteor(450, 780, 0, 0));
                        walls.add(new Wall(650, 250, "stump.png"));
                        windGusts.add(new Obstacle(800, 250, 650, 530));
                        tallDinos.add(new TallDino(800, -350, 1300, 200));
                        happyCloud = new HappyCloud(1450, 0);
                        break;
                    case 10:
                        levelBackground = new Texture("dino1Back.png");
                        walls.add(new Wall(330, -200, "stump.png"));
                        meteors.add(new Meteor(600, 880, 0, 0));
                        tallDinos.add(new TallDino(600, -150, 1000, 200));
                        happyCloud = new HappyCloud(1350, 50);
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
                        for(int i = 0; i < 3; i++) {
                            walls.add(new Wall(327, 595 * i));
                            walls.add(new Wall(796, 595 * i));
                        }
                        arrows.add(new Arrow(320, 750));
                        arrows.add(new Arrow(320, 950));
                        arrows.add(new Arrow(800, 950));
                        happyCloud = new HappyCloud(490, 1550);
                        break;
                    case 5:
                        arrows.add(new Arrow(0, 1000));
                        arrows.add(new Arrow(0, 300));
                        walls.add(new Wall(0, 1300, "horizontWall.png"));
                        walls.add(new Wall(850, 1000));
                        bonusQuokkas.add(new BonusQuokka(1000, 1100));
                        walls.add(new Wall(850, 1595, "horizontWall.png"));
                        clouds.add(new EvilCloud(600, 2017));
                        happyCloud = new HappyCloud(850, 1768);
                        break;
                    case 6:
                        walls.add(new Wall(675, 450, "horizontWall.png"));
                        walls.add(new Wall(93, 1000, "horizontWall.png"));
                        walls.add(new Wall(565, 1123));
                        switches.add(new Obstacle(798, 1868, "wallSwitch.png"));
                        walls.add(new Wall(93, 1718, switches, -718, 0,"horizontWall.png"));
                        walls.add(new Wall(675, 2200, "horizontWall.png"));
                        walls.add(new Wall(552, 2200, switches, -400, 1));
                        bonusQuokkas.add(new BonusQuokka(376, 1500));
                        happyCloud = new HappyCloud(720, 2323);
                        break;
                    case 7:
                        walls.add(new Wall(678, 0));
                        walls.add(new Wall(678, 595));
                        walls.add(new Wall(93, 1067, "horizontWall.png"));
                        portals.add(new Obstacle(150, 870, "portal.png"));
                        portals.add(new Obstacle(800, 500, "portal.png"));
                        arrows.add(new Arrow(1150, 700));
                        arrows.add(new Arrow(1150, 1350));
                        walls.add(new Wall(678, 1190));
                        walls.add(new Wall(93, 1662, "horizontWall.png"));
                        portals.add(new Obstacle(130, 2000, "portal.png"));
                        bonusQuokkas.add(new BonusQuokka(130, 2324));
                        portals.add(new Obstacle(900, 2086, "portal.png"));
                        clouds.add(new EvilCloud(130, 2162));
                        happyCloud = new HappyCloud(460, 2656);
                        break;
                    case 8:
                        clouds.add(new EvilCloud(500, 1000));
                        walls.add(new Wall(595, 1200));
                        walls.add(new Wall(595, 1795));
                        walls.add(new Wall(718, 2267, "horizontWall.png"));
                        clouds.add(new EvilCloud(800, 1323));
                        portals.add(new Obstacle(900, 2067, "portal.png"));
                        walls.add(new Wall(718, 2667, "horizontWall.png"));
                        bonusQuokkas.add(new BonusQuokka(800, 2400));
                        walls.add(new Wall(595, 2667));
                        walls.add(new Wall(718, 3139));
                        portals.add(new Obstacle(550, 2167, "portal.png"));
                        walls.add(new Wall(-100, 2167));
                        portals.add(new Obstacle(250, 2367, "portal.png"));
                        portals.add(new Obstacle(720, 2810, "portal.png"));
                        happyCloud = new HappyCloud(1000, 2810);
                        break;
                    case 9:
                        arrows.add(new Arrow(50, 1300));
                        clouds.add(new EvilCloud(160, 1300));
                        clouds.add(new EvilCloud(950, 1000));
                        clouds.add(new EvilCloud(600, 1600));
                        portals.add(new Obstacle(160, 1600, "portal.png"));
                        walls.add(new Wall(123, 1850, "horizontWall.png"));
                        walls.add(new Wall(718, 1850, "horizontWall.png"));
                        portals.add(new Obstacle(1050, 2020, "portal.png"));
                        arrows.add(new Arrow(50, 2170));
                        arrows.add(new Arrow(1230, 2420));
                        bonusQuokkas.add(new BonusQuokka(950,3000));
                        portals.add(new Obstacle(150, 2420, "portal.png"));
                        walls.add(new Wall(123, 2670, "horizontWall.png"));
                        portals.add(new Obstacle(200, 3000, "portal.png"));
                        clouds.add(new EvilCloud(500, 3000));
                        portals.add(new Obstacle(200, 3450, "portal.png"));
                        portals.add(new Obstacle(600, 3450, "portal.png"));
                        happyCloud = new HappyCloud(850, 3450);
                        walls.add(new Wall(805, 3250, "horizontWall.png"));
                        walls.add(new Wall(210, 3250, "horizontWall.png"));
                        walls.add(new Wall(210, 3373));
                        walls.add(new Wall(210, 3968, "horizontWall.png"));
                        walls.add(new Wall(805, 3968, "horizontWall.png"));
                        break;
                    case 10:
                        walls.add(new Wall(93, 850, "horizontWall.png"));
                        arrows.add(new Arrow(538, 875));
                        walls.add(new Wall(688, 850));
                        arrows.add(new Arrow(1180, 1245));
                        walls.add(new Wall(330, 1100));
                        walls.add(new Wall(330, 1695));
                        happyCloud = new HappyCloud(-50, 1595);
                        break;
                }
                break;
            case 4:
                levelBackground = new Texture("canvasBackground.png");
                switch(level){
                    case 1:
                        nullZones.add(new Obstacle(-100, -300, 350, 1068));
                        moveSpots.add(new Vector2(-50, 330));
                        moveSpots.add(new Vector2(250, 70));
                        moveSpots.add(new Vector2(250.01f, 70));
                        moveSpots.add(new Vector2(-50.01f, 330));
                        brushes.add(new Obstacle("brush.png", moveSpots, 200, 200));
                        walls.add(new Wall(280, 100, "horizontWall.png"));
                        clouds.add(new EvilCloud(900, 250));
                        walls.add(new Wall(1350, 400));
                        happyCloud = new HappyCloud(1550, 550);
                        break;
                    case 2:
                        clouds.add(new EvilCloud(450, 600));
                        clouds.add(new EvilCloud(450, 50));
                        nullZones.add(new Obstacle(700, -300, 580, 1100));
                        bonusQuokkas.add(new BonusQuokka(990, 450));
                        happyCloud = new HappyCloud(1600, 150);
                        break;
                    /*case 2:
                        clouds.add(new EvilCloud(250, 50));
                        walls.add(new Wall(900, 250));
                        bonusQuokkas.add(new BonusQuokka(1068, 500));
                        happyCloud = new HappyCloud(1258, 50);
                        break;*/
                    /*case 3:
                        nullZones.add(new Obstacle(400, 100, 900, 800));
                        walls.add(new Wall(1300, 200));
                        bonusQuokkas.add(new BonusQuokka(1100, 200));
                        happyCloud = new HappyCloud(1450, 350);
                        break;*/
                    case 3:
                        nullZones.add(new Obstacle(400, -300, 300, 1100));
                        nullZones.add(new Obstacle(900, -300, 300, 1100));
                        nullZones.add(new Obstacle(1400, -300, 300, 1100));
                        bonusQuokkas.add(new BonusQuokka(1550, 500));
                        happyCloud = new HappyCloud(1800, 50);
                        break;
                    /*case 4:
                        moveSpots.add(new Vector2(200, 550));
                        moveSpots.add(new Vector2(200, 100));
                        moveSpots.add(new Vector2(200.01f, 100));
                        moveSpots.add(new Vector2(200.01f, 550));
                        brushes.add(new Obstacle("brush.png", moveSpots, 200, 400));
                        clouds.add(new EvilCloud(300, 50));
                        moveSpots.clear();
                        moveSpots.add(new Vector2(600, 600));
                        moveSpots.add(new Vector2(900, 150));
                        moveSpots.add(new Vector2(1050, 250));
                        moveSpots.add(new Vector2(750, 700));
                        brushes.add(new Obstacle("brush.png", moveSpots, 250, 600));
                        walls.add(new Wall(1250, 0));
                        happyCloud = new HappyCloud(1500, 50);
                        break;*/
                    case 4:
                        nullZones.add(new Obstacle(300, 459, 1000, 359));
                        nullZones.add(new Obstacle(300, -200, 1000, 509));
                        walls.add(new Wall(1450, 380));
                        clouds.add(new EvilCloud(1650, 50));
                        moveSpots.add(new Vector2(1875, 768));
                        moveSpots.add(new Vector2(2125, 440));
                        moveSpots.add(new Vector2(2125.01f, 440));
                        moveSpots.add(new Vector2(1875.01f, 768));
                        brushes.add(new Obstacle("brush.png", moveSpots, 200, 200));
                        happyCloud = new HappyCloud(2450, 400);
                        break;
                    case 5:
                        nullZones.add(new Obstacle(200, -300, 600, 1068));
                        moveSpots.add(new Vector2(250, 300));
                        moveSpots.add(new Vector2(750, 300));
                        moveSpots.add(new Vector2(750, 300.01f));
                        moveSpots.add(new Vector2(250, 300.01f));
                        brushes.add(new Obstacle("brush.png", moveSpots, 250, 250));
                        nullZones.add(new Obstacle(1200, -300, 1200, 1068));
                        moveSpots.clear();
                        moveSpots.add(new Vector2(1440, 350));
                        moveSpots.add(new Vector2(1780, 350));
                        moveSpots.add(new Vector2(1780, 350.01f));
                        moveSpots.add(new Vector2(1440, 350.01f));
                        brushes.add(new Obstacle("brush.png", moveSpots, 200, 200));
                        moveSpots.clear();
                        moveSpots.add(new Vector2(1920, 150));
                        moveSpots.add(new Vector2(2160, 150));
                        moveSpots.add(new Vector2(2160, 150.01f));
                        moveSpots.add(new Vector2(1920, 150.01f));
                        brushes.add(new Obstacle("brush.png", moveSpots, 150, 150));
                        happyCloud = new HappyCloud(2250, 500);
                        break;
                    case 6:
                        clouds.add(new EvilCloud(-50, 100));
                        moveSpots.add(new Vector2(350, 50));
                        moveSpots.add(new Vector2(550, 350));
                        moveSpots.add(new Vector2(550.01f, 350));
                        moveSpots.add(new Vector2(350.01f, 50));
                        brushes.add(new Obstacle("brush.png", moveSpots, 150, 150));
                        windGusts.add(new Obstacle(300, 400, 300, 400));
                        walls.add(new Wall(700, 300));
                        moveSpots.clear();
                        moveSpots.add(new Vector2(823, 550));
                        moveSpots.add(new Vector2(1123, 550));
                        moveSpots.add(new Vector2(1123.01f, 550));
                        moveSpots.add(new Vector2(823.01f, 550));
                        brushes.add(new Obstacle("brush.png", moveSpots, 150, 150));
                        bonusQuokkas.add(new BonusQuokka(970, 570));
                        nullZones.add(new Obstacle(1123, -300, 250, 1068));
                        happyCloud = new HappyCloud(1450, 50);
                        break;
                    case 7:
                        nullZones.add(new Obstacle(250, -300, 350, 1068));
                        windGusts.add(new Obstacle(250, 400, 350, 368));
                        clouds.add(new EvilCloud(600, 550));
                        moveSpots.add(new Vector2(850, 0));
                        moveSpots.add(new Vector2(850, 768));
                        moveSpots.add(new Vector2(850.01f, 768));
                        moveSpots.add(new Vector2(850.01f, 0));
                        brushes.add(new Obstacle("brush.png", moveSpots, 200, 200));
                        nullZones.add(new Obstacle(1100, -300, 350, 1068));
                        windGusts.add(new Obstacle(1100, 400, 350, 368));
                        walls.add(new Wall(1650, -280));
                        happyCloud= new HappyCloud(1850, 50);
                        break;
                    case 8:
                        nullZones.add(new Obstacle(200, -300, 350, 1068));
                        walls.add(new Wall(300, -250));
                        walls.add(new Wall(750, -250));
                        nullZones.add(new Obstacle(873, -300, 250, 1068));
                        walls.add(new Wall(1123, 400));
                        nullZones.add(new Obstacle(1346, -300, 350, 1068));
                        walls.add(new Wall(1446, 450));
                        walls.add(new Wall(1446, -350));
                        happyCloud = new HappyCloud(1619, 50);
                        break;
                    /*case 9:
                        moveSpots.add(new Vector2(50,100));
                        moveSpots.add(new Vector2(250,400));
                        moveSpots.add(new Vector2(100,600));
                        moveSpots.add(new Vector2(50.01f,100));
                        brushes.add(new Obstacle("brush.png", moveSpots,150, 700));
                        moveSpots.clear();
                        moveSpots.add(new Vector2(-400,100));
                        moveSpots.add(new Vector2(-400,400));
                        moveSpots.add(new Vector2(-400,600));
                        moveSpots.add(new Vector2(-400,800));
                        brushes.add(new Obstacle("brush.png", moveSpots,50, 700));
                        happyCloud=new HappyCloud(-300, 50);
                        break;*/
                    /*case 9:
                        moveSpots.add(new Vector2(60, 400));
                        moveSpots.add(new Vector2(60, 100));
                        moveSpots.add(new Vector2(60.01f, 400));
                        moveSpots.add(new Vector2(60.01f, 100));
                        brushes.add(new Obstacle("brush.png", moveSpots, 200, 300));
                        happyCloud = new HappyCloud(2000, 30);
                        break;*/
                    case 9:
                        walls.add(new Wall(300, 384));
                        nullZones.add(new Obstacle(523, -200, 342,968));
                        switches.add(new Obstacle(669, 75, "wallSwitch.png"));
                        walls.add(new Wall(1665, -325, switches.get(0), 420));
                        switches.add(new Obstacle(669, 590, "wallSwitch.png"));
                        walls.add(new Wall(1788, -325, switches.get(1), 420));
                        clouds.add(new EvilCloud(1165, 50));
                        walls.add(new Wall(1665, 270));
                        walls.add(new Wall(1788, 270));
                        happyCloud = new HappyCloud(2011, 280);
                        break;
                    case 10:
                        moveSpots.add(new Vector2(-150, 0));
                        moveSpots.add(new Vector2(-150, 758));
                        moveSpots.add(new Vector2(-150.01f, 758));
                        moveSpots.add(new Vector2(-150.01f, 0));
                        brushes.add(new Obstacle("brush.png", moveSpots, 220, 220));
                        moveSpots.clear();
                        moveSpots.add(new Vector2(-75, 100));
                        moveSpots.add(new Vector2(150, 325));
                        moveSpots.add(new Vector2(150.01f, 325));
                        moveSpots.add(new Vector2(-75.01f, 100));
                        brushes.add(new Obstacle("brush.png", moveSpots, 250, 250));
                        moveSpots.clear();
                        moveSpots.add(new Vector2(350, 130));
                        moveSpots.add(new Vector2(650, 130));
                        moveSpots.add(new Vector2(650.01f, 130));
                        moveSpots.add(new Vector2(350.01f, 130));
                        brushes.add(new Obstacle("brush.png", moveSpots, 200, 200));
                        nullZones.add(new Obstacle(-300, -200, 1000, 968));
                        happyCloud = new HappyCloud(720, 20);
                        break;
                }
                break;
            case 5:
                switch(level) {
                    case 1:
                        levelBackground = new Texture("spaceBackground.png");
                        planets.add(new Obstacle(300, 200, "greenPlanet.png"));
                        planets.add(new Obstacle(900, 400, "greenPlanet.png"));
                        happyCloud = new HappyCloud(1500, 300);
                        planets.add(new Obstacle(1700, 400, "greenPlanet.png"));
                        break;
                    case 2:
                        levelBackground = new Texture("spaceBackground.png");
                        switches.add(new Obstacle(300, 0, "wallSwitch.png"));
                        planets.add(new Obstacle(200, 300, "greenPlanet.png"));
                        walls.add(new Wall(500, 380, switches, 1000));
                        walls.add(new Wall(500, -215));
                        planets.add(new Obstacle(800, 50, "greenPlanet.png"));
                        happyCloud = new HappyCloud(1500, 300);
                        planets.add(new Obstacle(1800, 600, "greenPlanet.png"));
                        break;
                    /*case 3:
                        levelBackground = new Texture("spaceBackground.png");
                        planets.add(new Obstacle(200, 300, "greenPlanet.png"));
                        planets.add(new Obstacle(600, 500, "blackHole.png"));
                        happyCloud = new HappyCloud(50, 50);
                        break;*/
                    case 3:
                        nebulae.add(new Obstacle(250, 300, "blackHole.png"));
                        bonusQuokkas.add(new BonusQuokka(750, 600));
                        blackHoles.add(new Obstacle(800, 300, "blackHole.png"));
                        planets.add(new Obstacle(1100, 600, "greenPlanet.png"));
                        planets.add(new Obstacle(1650, 50, "greenPlanet.png"));
                        planets.add(new Obstacle(1650, 460, "greenPlanet.png"));
                        happyCloud = new HappyCloud(2050, 250);
                        break;
                    case 4:
                        blackHoles.add(new Obstacle(300, 550, "blackHole.png"));
                        blackHoles.add(new Obstacle(725, 30, "blackHole.png"));
                        nebulae.add(new Obstacle(1150, 320, "greenPlanet.png"));
                        blackHoles.add(new Obstacle(1525, 50, "blackHole.png"));
                        blackHoles.add(new Obstacle(1525, 460, "blackHole.png"));
                        planets.add(new Obstacle(2000, 300, "greenPlanet.png"));
                        planets.add(new Obstacle(2575, 300, "greenPlanet.png"));
                        bonusQuokkas.add(new BonusQuokka(2300, 500));
                        nebulae.add(new Obstacle(2875, 50, "blackHole.png"));
                        happyCloud = new HappyCloud(3275, 50);
                        break;
                    case 5:
                        nebulae.add(new Obstacle(250, 300, "blackHole.png"));
                        switches.add(new Obstacle(300, 50, "wallSwitch.png"));
                        walls.add(new Wall(550, -13));
                        walls.add(new Wall(550, 588, switches.get(0), 200));
                        switches.add(new Obstacle(900, 550, "wallSwitch.png"));
                        nebulae.add(new Obstacle(900, 225, "blackHole.png"));
                        walls.add(new Wall(1200, -345, switches.get(1), -250));
                        walls.add(new Wall(1200, 250));
                        planets.add(new Obstacle(1600, 280, "greenPlanet.png"));
                        blackHoles.add(new Obstacle(2200, 550, "blackHole.png"));
                        planets.add(new Obstacle(2800, 50, "greenPlanet.png"));
                        walls.add(new Wall(3100, -300));
                        happyCloud = new HappyCloud(3250, 50);
                        blackHoles.add(new Obstacle(3600, 300, "blackHole.png"));
                        break;
                    case 6:
                        planets.add(new Obstacle(350, 250, "greenPlanet.png"));
                        portals.add(new Obstacle(375, 500, "portal.png"));
                        planets.add(new Obstacle(900, 50, "greenPlanet.png"));
                        switches.add(new Obstacle(3480, 10, "wallSwitch.png"));
                        walls.add(new Wall(1350, -311));
                        walls.add(new Wall(1350, 284, switches.get(0), 484));
                        planets.add(new Obstacle(1750, 350, "greenPlanet.png"));
                        switches.add(new Obstacle(2150, 20, "wallSwitch.png"));
                        walls.add(new Wall(2400, 384));
                        walls.add(new Wall(2400, -211));
                        nebulae.add(new Obstacle(2700, 350, "blackHole.png"));
                        happyCloud = new HappyCloud(2730, 25);
                        walls.add(new Wall(3280, -211));
                        walls.add(new Wall(3280, 384, switches.get(1), 384));
                        portals.add(new Obstacle(3480, 500, "portal.png"));
                        planets.add(new Obstacle(3930, 260, "greenPlanet.png"));
                        break;
                    case 7:
                        planets.add(new Obstacle(350, 400, "greenPlanet.png"));
                        drones.add(new Drone(750, 550));
                        planets.add(new Obstacle(1000, 10, "greenPlanet.png"));
                        bonusQuokkas.add(new BonusQuokka(1300, 5));
                        planets.add(new Obstacle(1550, 350, "greenPlanet.png"));
                        planets.add(new Obstacle(2150, 520, "greenPlanet.png"));
                        blackHoles.add(new Obstacle(2750, 150, "blackHole.png"));
                        happyCloud = new HappyCloud(3100, 350);
                        planets.add(new Obstacle(3400, 550, "greenPlanet.png"));
                        break;
                    case 8:
                        nebulae.add(new Obstacle(350, 550, "blackHole.png"));
                        planets.add(new Obstacle(800, 350, "greenPlanet.png"));
                        laserGuns.add(new LaserGun(820, 370));
                        blackHoles.add(new Obstacle(1320, 550, "blackHole.png"));
                        planets.add(new Obstacle(1920, 250, "greenPlanet.png"));
                        bonusQuokkas.add(new BonusQuokka(1570, 560));
                        happyCloud = new HappyCloud(2020, 300);
                        break;
                    case 9:
                        clouds.add(new EvilCloud(300, 20));
                        clouds.add(new EvilCloud(600, 550));
                        clouds.add(new EvilCloud(900, 20));
                        clouds.add(new EvilCloud(1200, 308));
                        clouds.add(new EvilCloud(1500, 308));
                        planets.add(new Obstacle(2100, 256, "greenPlanet.png"));
                        happyCloud = new HappyCloud(2450, 308);
                        break;
                    case 10:
                        nebulae.add(new Obstacle(300, 350, "blackHole.png"));
                        clouds.add(new EvilCloud(550, 50));
                        blackHoles.add(new Obstacle(800, 500, "blackHole.png"));
                        planets.add(new Obstacle(1100, 20, "greenPlanet.png"));
                        blackHoles.add(new Obstacle(1400, 500, "blackHole.png"));
                        planets.add(new Obstacle(1600, 300, "greenPlanet.png"));
                        happyCloud = new HappyCloud(1900, 250);
                        break;
                }
                break;
            case 6:
                switch(level){
                    case 1:
                        walls.add(new Wall(-100,380));
                        walls.add(new Wall(-100, -265));
                        portals.add(new Obstacle(25, 50, "portal.png"));
                        walls.add(new Wall(200,380));
                        walls.add(new Wall(200, -265));
                        portals.add(new Obstacle(450, 600, "portal.png"));
                        clouds.add(new EvilCloud(550, 100));
                        bonusQuokkas.add(new BonusQuokka(450, 0));
                        clouds.add(new EvilCloud(850, 600));
                        happyCloud = new HappyCloud(1100, 600);
                        break;
                    case 2:
                        walls.add(new Wall(200, -50));
                        laserGuns.add(new LaserGun(350, 50));
                        clouds.add(new EvilCloud(600, 350));
                        bonusQuokkas.add(new BonusQuokka(600, 0));
                        walls.add(new Wall(1000, 350));
                        walls.add(new Wall(1400, -220));
                        happyCloud = new HappyCloud(1450, 50);
                        break;
                    case 3:
                        drones.add(new Drone(400, 600));
                        walls.add(new Wall(650, 350));
                        walls.add(new Wall(950, -150));
                        drones.add(new Drone(1300, 50));
                        clouds.add(new EvilCloud(1500, 250));
                        bonusQuokkas.add(new BonusQuokka(1550, 0));
                        clouds.add(new EvilCloud(1750, 250));
                        happyCloud = new HappyCloud(1950, 0);
                        break;
                    case 4:
                        walls.add(new Wall(200, -130));
                        bonusQuokkas.add(new BonusQuokka(600, 400));
                        laserGuns.add(new LaserGun(850, 550));
                        laserGuns.add(new LaserGun(1100, 0));
                        happyCloud = new HappyCloud(1050, 250);
                        break;
                    case 5:
                        walls.add(new Wall(0, 427, "horizontWall.png"));
                        walls.add(new Wall(-595, 427, "horizontWall.png"));
                        portals.add(new Obstacle(431, 266, "portal.png"));
                        portals.add(new Obstacle(781, 550, "portal.png"));
                        portals.add(new Obstacle(-300, 266, "portal.png"));
                        walls.add(new Wall(-718, -45));
                        walls.add(new Wall(595, -45));
                        clouds.add(new EvilCloud(751, 350));
                        clouds.add(new EvilCloud(1250, 350));
                        bonusQuokkas.add(new BonusQuokka(1100, 400));
                        portals.add(new Obstacle(1050, 175, "portal.png"));
                        portals.add(new Obstacle(1300, 550, "portal.png"));
                        clouds.add(new EvilCloud(1850, 595));
                        walls.add(new Wall(2100, 0));
                        portals.add(new Obstacle(2223, 350, "portal.png"));
                        happyCloud = new HappyCloud(2463, 100);
                        break;
                    case 6:
                        walls.add(new Wall(650, 250));
                        laserGuns.add(new LaserGun(450, 500));
                        portals.add(new Obstacle(800, 600, "portal.png"));
                        walls.add(new Wall(1050, -211));
                        walls.add(new Wall(1050, 384));
                        portals.add(new Obstacle(1200, 50, "portal.png"));
                        drones.add(new Drone(1900, 650));
                        walls.add(new Wall(2050, -100));
                        bonusQuokkas.add(new BonusQuokka(2250, 50));
                        clouds.add(new EvilCloud(2700, 550));
                        walls.add(new Wall(3200, -50));
                        happyCloud = new HappyCloud(3400, 50);
                        break;
                    case 7:
                        drones.add(new Drone(450, -250));
                        clouds.add(new EvilCloud(250, 500));
                        portals.add(new Obstacle(500, 250, "portal.png"));
                        clouds.add(new EvilCloud(450, 0));
                        bonusQuokkas.add(new BonusQuokka(770, 250));
                        portals.add(new Obstacle(500, 550, "portal.png"));
                        walls.add(new Wall(1020, -95));
                        drones.add(new Drone(1170, 50));
                        clouds.add(new EvilCloud(1470, 550));
                        clouds.add(new EvilCloud(1720, 200));
                        happyCloud = new HappyCloud(1970, 0);
                        break;
                    case 8:
                        walls.add(new Wall(150, 200));
                        portals.add(new Obstacle(300, 600, "portal.png"));
                        walls.add(new Wall(550, -211));
                        switches.add(new Obstacle(700, 0, "wallSwitch.png"));
                        walls.add(new Wall(550, 384, switches, 384));
                        walls.add(new Wall(673, 320, "horizontWall.png"));
                        portals.add(new Obstacle(1018, 100, "portal.png"));
                        walls.add(new Wall(1268, -211));
                        clouds.add(new EvilCloud(1018, 550));
                        walls.add(new Wall(1518, 373));
                        walls.add(new Wall(1518, 250));
                        portals.add(new Obstacle(1650, 380, "portal.png"));
                        bonusQuokkas.add(new BonusQuokka(1670, 580));
                        clouds.add(new EvilCloud(2050, 380));
                        walls.add(new Wall(2300, 250));
                        happyCloud = new HappyCloud(2500, 450);
                        break;
                    case 9:
                        drones.add(new Drone(150, 10));
                        walls.add(new Wall(350, 450));
                        portals.add(new Obstacle(500, 600, "portal.png"));
                        walls.add(new Wall(750, 450));
                        walls.add(new Wall(873, 450));
                        portals.add(new Obstacle(873, 600, "portal.png"));
                        laserGuns.add(new LaserGun(935, 450));
                        portals.add(new Obstacle(1223, 600, "portal.png"));
                        portals.add(new Obstacle(1596, 600, "portal.png"));
                        bonusQuokkas.add(new BonusQuokka(1596, 400));
                        clouds.add(new EvilCloud(1796, 400));
                        laserGuns.add(new LaserGun(2046, 400));
                        happyCloud = new HappyCloud(2296, 400);
                        break;
                    case 10:
                        laserGuns.add(new LaserGun(-100, 700));
                        laserGuns.add(new LaserGun(-100, 250));
                        walls.add(new Wall(300, 575));
                        clouds.add(new EvilCloud(550, 30));
                        walls.add(new Wall(950, 575));
                        laserGuns.add(new LaserGun(1100, 600));
                        clouds.add(new EvilCloud(1250, 50));
                        happyCloud = new HappyCloud(1550, 400);
                        break;
                }
                break;
            case 7:
                switch(level){
                    case 1:
                        stoplights.add(new Stoplight(200, 568));
                        moveWalls.add(new MoveWall(300, 384, 192, 384, 3, stoplights.get(0)));
                        walls.add(new Wall(300, -211));
                        switches.add(new Obstacle(500, 20, "wallSwitch.png"));
                        walls.add(new Wall(750, -211));
                        walls.add(new Wall(750, 384, switches, 384));
                        airplanes.add(new Airplane(1150, 600, -100, 0));
                        happyCloud = new HappyCloud(950, 50);
                        break;
                    case 2:
                        walls.add(new Wall(200, 200));
                        stoplights.add(new Stoplight(450, 568));
                        moveWalls.add(new MoveWall(550, 384, 130, 384, 3, stoplights.get(0)));
                        moveWalls.add(new MoveWall(673, 384, 130, 384, 3, stoplights.get(0)));
                        moveWalls.add(new MoveWall(796, 384, 130, 384, 3, stoplights.get(0)));
                        walls.add(new Wall(550, -211));
                        walls.add(new Wall(673, -211));
                        walls.add(new Wall(796, -211));
                        clouds.add(new EvilCloud(1119, 575));
                        happyCloud = new HappyCloud(1369,50);
                        break;
                    case 3:
                        walls.add(new Wall(250, 534));
                        walls.add(new Wall(250, -361));
                        airplanes.add(new Airplane(550, 234, -150, 0));
                        walls.add(new Wall(750, -95));
                        airplanes.add(new Airplane(1050, 500, -150, 0));
                        walls.add(new Wall(1250, 200));
                        happyCloud = new HappyCloud(1400, 500);
                        break;
                    case 4:
                        airplanes.add(new Airplane(300, 600, -150, 0));
                        airplanes.add(new Airplane(300, 50, -150, 0));
                        airplanes.add(new Airplane(800, 350, -150, 0));
                        airplanes.add(new Airplane(1300, 350, -150, 0));
                        airplanes.add(new Airplane(1300, 600, -150, 0));
                        airplanes.add(new Airplane(1800, 350, -150, 0));
                        airplanes.add(new Airplane(1800, 50, -150, 0));
                        happyCloud = new HappyCloud(1350, 50);
                        break;
                    case 5:
                        stoplights.add(new Stoplight(200, 568));
                        moveWalls.add(new MoveWall(300, 645, 150, 100, 2, stoplights.get(0), "horizontWall.png"));
                        moveWalls.add(new MoveWall(300, 522, 150, 100, 2, stoplights.get(0), "horizontWall.png"));
                        moveWalls.add(new MoveWall(895, 645, 150, 100, 0, stoplights.get(0), "horizontWall.png"));
                        moveWalls.add(new MoveWall(895, 522, 150, 100, 0, stoplights.get(0), "horizontWall.png"));
                        stoplights.add(new Stoplight(200, 0));
                        moveWalls.add(new MoveWall(300, 0, 150, 100, 2, stoplights.get(1), "horizontWall.png"));
                        moveWalls.add(new MoveWall(300, 123, 150, 100, 2, stoplights.get(1), "horizontWall.png"));
                        moveWalls.add(new MoveWall(895, 0, 150, 100, 0, stoplights.get(1), "horizontWall.png"));
                        moveWalls.add(new MoveWall(895, 123, 150, 100, 0, stoplights.get(1), "horizontWall.png"));
                        airplanes.add(new Airplane(910, 334, -150, 0));
                        airplanes.add(new Airplane(1520, 334, -150, 0));
                        happyCloud = new HappyCloud(1520, 550);
                        break;
                    case 6:
                        windGusts.add(new Obstacle(300, 369, 1190, 399));
                        stoplights.add(new Stoplight(200, 0));
                        moveWalls.add(new MoveWall(300, 0, 150, 100, 2, stoplights.get(0), "horizontWall.png"));
                        moveWalls.add(new MoveWall(300, 123, 150, 100, 2, stoplights.get(0), "horizontWall.png"));
                        moveWalls.add(new MoveWall(895, 0, 150, 100, 0, stoplights.get(0), "horizontWall.png"));
                        moveWalls.add(new MoveWall(895, 123, 150, 100, 0, stoplights.get(0), "horizontWall.png"));
                        clouds.add(new EvilCloud(1690, 550));
                        clouds.add(new EvilCloud(2140, 0));
                        airplanes.add(new Airplane(2390, 550, -150, 0));
                        happyCloud = new HappyCloud(2390, 550);
                        break;
                    case 7:
                        airplanes.add(new Airplane(300, 880, 0, -150));
                        stoplights.add(new Stoplight(550, 568));
                        walls.add(new Wall(650, -211));
                        moveWalls.add(new MoveWall(650, 384, 130, 384, 3, stoplights.get(0)));
                        airplanes.add(new Airplane(850, 0, 0, 150));
                        stoplights.add(new Stoplight(1150, 568));
                        walls.add(new Wall(1250, -211));
                        moveWalls.add(new MoveWall(1250, 384, 130, 384, 3, stoplights.get(0)));
                        airplanes.add(new Airplane(1450, 880, 0, -150));
                        stoplights.add(new Stoplight(1750, 568));
                        walls.add(new Wall(1850, -211));
                        moveWalls.add(new MoveWall(1850, 384, 130, 384, 3, stoplights.get(0)));
                        airplanes.add(new Airplane(20, 880, 0, -150));
                        break;
                    case 8:
                        walls.add(new Wall(-200, 350, "horizontWall.png"));
                        airplanes.add(new Airplane(-400, 500, 150, 0));
                        airplanes.add(new Airplane(-400, 100, 150, 0));
                        walls.add(new Wall(700, 350, "horizontWall.png"));
                        walls.add(new Wall(1172, 473));
                        walls.add(new Wall(1595, -122));
                        walls.add(new Wall(1718, 261, "horizontWall.png"));
                        walls.add(new Wall(2563, 384));
                        windGusts.add(new Obstacle(2313, 100, 250, 434));
                        bonusQuokkas.add(new BonusQuokka(1800, 50));
                        happyCloud = new HappyCloud(2736, 410);
                        break;
                    case 9:
                        walls.add(new Wall(200, -200));
                        stoplights.add(new Stoplight(450, 169));
                        bonusQuokkas.add(new BonusQuokka(400, 50));
                        moveWalls.add(new MoveWall(650, 369, 150, 100, 2, stoplights.get(0)));
                        moveWalls.add(new MoveWall(650, 246, 150, 100, 2, stoplights.get(0)));
                        moveWalls.add(new MoveWall(650, 123, 150, 100, 2, stoplights.get(0)));
                        moveWalls.add(new MoveWall(650, 0, 150, 100, 2, stoplights.get(0)));
                        walls.add(new Wall(1245, 369));
                        walls.add(new Wall(1245, 246));
                        walls.add(new Wall(1245, 123));
                        walls.add(new Wall(1717, 492));
                        airplanes.add(new Airplane(1840, 292, -150, 0));
                        airplanes.add(new Airplane(1840, 130, -150, 0));
                        airplanes.add(new Airplane(2090, 550, 0, -150));
                        happyCloud = new HappyCloud(2290, 550);
                        break;
                    case 10:
                        walls.add(new Wall(250, -250));
                        walls.add(new Wall(373, 222, "horizontWall.png"));
                        stoplights.add(new Stoplight(2063, 145));
                        moveWalls.add(new MoveWall(968, 222, 250, 500, 0, stoplights.get(0), "horizontWall.png"));
                        walls.add(new Wall(1563, 345));
                        bonusQuokkas.add(new BonusQuokka(1743, 550));
                        walls.add(new Wall(1940, 345));
                        clouds.add(new EvilCloud(1890, -50));
                        clouds.add(new EvilCloud(2440, 250));
                        airplanes.add(new Airplane(2740, 500, -100, 0));
                        happyCloud = new HappyCloud(2740, 50);
                        break;
                }
                break;
            case 8:
                world = 8;
                switch(level){
                    /*case 1:
                        levelBackground = new Texture("level1Background.png");
                        bonusQuokkas.add(new BonusQuokka(200, 400));
                        happyCloud = new HappyCloud(500, 200);
                        break;
                    case 2:
                        levelBackground = new Texture("level3Background.png");
                        walls.add(new Wall(350, -80, "wall.png"));
                        walls.add(new Wall(850, -230, "wall.png"));
                        clouds.add(new EvilCloud(1200, 460));
                        bonusQuokkas.add(new BonusQuokka(1200, 10));
                        happyCloud = new HappyCloud(500, 50);
                        break;
                    case 3:
                        levelBackground = new Texture("dino1Back.png");
                        walls.add(new Wall(330, -200, "stump.png"));
                        meteors.add(new Meteor(600, 880, 0, 0));
                        tallDinos.add(new TallDino(700, -150, 1100, 200));
                        meteors.add(new Meteor( 1800, 880, 5, 0));
                        happyCloud = new HappyCloud(1450, 50);
                        break;
                    case 4:
                        levelBackground = new Texture( "dino2Back.png");
                        tallDinos.add(new TallDino( -50, -150, 100, 200));
                        tallDinos.add(new TallDino( 0, -150, 250, 200));
                        tallDinos.add(new TallDino( 200, -150, 500, 200));
                        tallDinos.add(new TallDino( 300, -150, 700, 200));
                        tallDinos.add(new TallDino( 500, -150, 900, 200));
                        tallDinos.add(new TallDino( 800, -150, 1200, 200));
                        happyCloud = new HappyCloud(1300, 50);
                        break;*/
                    case 1:
                        levelBackground = new Texture("level2Background.png");
                        hawks.add(new Hawk(400,150));
                        happyCloud = new HappyCloud(850, 300);
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
        if(world == 5){
            for(int i = -220; i < happyCloud.getPosCloud().x; i+=595){
                walls.add(new Wall(i, -100, "horizontWall.png"));
                walls.add(new Wall(i, cam.viewportHeight - 10, "horizontWall.png"));
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
            tempGrav.set(0, 0, 0);
            for(Obstacle planet : planets){
                planetDistance.set(Math.abs(quokka.getPosition().x + quokka.getTexture().getWidth() / 2 - planet.getPosObstacle().x - planet.getTexture().getWidth() / 2), Math.abs(quokka.getPosition().y + quokka.getTexture().getHeight() / 2 - planet.getPosObstacle().y - planet.getTexture().getHeight() / 2), 0);
                double planetMagnitude = MAGSCALER * Math.sqrt(Math.pow(planetDistance.x, 2) + Math.pow(planetDistance.y, 2));
                currentPot += GOODGRAV / Math.pow(planetMagnitude, GRAVPOW - 1);
            }
        }
        else{
            currentPot = -1 * quokka.getGravity().y * quokka.getPosition().y;
        }
        //coachLandmark
        velocityTemp2.scl((float) Math.sqrt(Math.abs(2.0*(iniPot - currentPot))));
        velocityTemp2.scl((float) (1.0/Math.sqrt(currentDT)));
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
            tempGrav.set(0, 0, 0);
            for(Obstacle planet : planets){
                planetDistance.set(Math.abs(quokka.getPosition().x + quokka.getTexture().getWidth() / 2 - planet.getPosObstacle().x - planet.getTexture().getWidth() / 2), Math.abs(quokka.getPosition().y + quokka.getTexture().getHeight() / 2 - planet.getPosObstacle().y - planet.getTexture().getHeight() / 2), 0);
                double planetMagnitude = MAGSCALER * Math.sqrt(Math.pow(planetDistance.x, 2) + Math.pow(planetDistance.y, 2));
                currentPot += GOODGRAV / Math.pow(planetMagnitude, GRAVPOW - 1);
            }
        }
        else{
            currentPot = -1 * quokka.getGravity().y * quokka.getPosition().y;
        }
        //coachLandmark
        velocityTemp2.scl((float) Math.sqrt(Math.abs(2.0*(iniPot - currentPot))));
        velocityTemp2.scl((float) (1.0/Math.sqrt(currentDT)));
        return velocityTemp2;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }
    private boolean isCollision(Polygon p, Rectangle r) {
        Polygon rPoly = new Polygon(new float[] { 0, 0, r.width, 0, r.width,
                r.height, 0, r.height });
        rPoly.setPosition(r.x, r.y);
        if (Intersector.overlapConvexPolygons(rPoly, p)) {
            return true;
        }
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
            gsm.set(new MenuState(gsm, world, level, false));
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
                            if(lineY(quokka.getPosition().x) < quokka.getPosition().y + quokka.getQuokkaBounds().getHeight()* 0.5) {
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
                            }
                            else{
                                while (quokka.getQuokkaBounds().contains(quokka.getPosition().x, lineY(quokka.getPosition().x))) {
                                    clickPos.set(clickPos.x, clickPos.y + 10, 0);
                                    clickPos2.set(clickPos2.x, clickPos2.y + 10, 0);
                                }
                                quokka.getVelocity().scl(currentDT);
                                quokka.getQuokkaBounds().set(quokka.getQuokkaBounds().x + quokka.getVelocity().x, quokka.getQuokkaBounds().y + quokka.getVelocity().y, quokka.getQuokkaBounds().width, quokka.getQuokkaBounds().height);
                                while (quokka.getQuokkaBounds().contains(quokka.getPosition().x + quokka.getVelocity().x, lineY(quokka.getPosition().x + quokka.getVelocity().y))) {
                                    clickPos.set(clickPos.x, clickPos.y + 10, 0);
                                    clickPos2.set(clickPos2.x, clickPos2.y + 10, 0);
                                }
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
                vineDraw = true;
                lineCheck = true;
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
