package com.quokkabounce.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.quokkabounce.game.sprites.Animation;
import com.quokkabounce.game.sprites.Arrow;
import com.quokkabounce.game.sprites.BonusQuokka;
import com.quokkabounce.game.sprites.Button;
import com.quokkabounce.game.sprites.Drone;
import com.quokkabounce.game.sprites.EvilCloud;
import com.quokkabounce.game.sprites.HappyCloud;
import com.quokkabounce.game.sprites.Hawk;
import com.quokkabounce.game.sprites.JumpFish;
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

class PlayState extends State implements InputProcessor{ //This is the largest part of the program, containing the main gameplay loop, the level creation, and input handling. outside of this, I created the classes for obstacles like the Arrow, Stoplight, and Wall classes, and the Quokka, as well as the menus, world selection, etc.
    private static final int ARROWHEIGHT = 160;
    private static final float GOODGRAV = -500000000f;
    private static final float DEPTHSCALER = 0.0001f;
    private static final double OCEANSLOW = 0.98;
    private static final float GRAVPOW = 2f;
    private static final float MAGSCALER = 500;
    private static final float VIEWPORT_SCALER = 1.6f;
    private static final int TOWERFALL = 100;
    private static final int WALLSPEED = 3;
    private static final float TIMELIMIT = 0.025f;
    private static final float TIMEMINIMUM = 0.0011f;
    private static final float LINEWIDTH = 2.5f;

    private Quokka quokka; //initializes variables and arrays
    private Button backButton, pauseButton;
    private Texture levelBackground;
    private Vector2 levelBackgroundPos1, levelBackgroundPos2, levelBackgroundPos3, levelBackgroundPos4, intersectionPoint, intersectionPointTemp, circleCenter, quokkaSide, adjustedCenter, planetProj, clickPos2d, clickPos2d2, xdiff, ydiff, tempDet, tempWall, tempBack, temp2, clickPosNo, clickPosNo2;
    private Vector3 clickPos, clickPos2, velocityTemp, velocityTemp2, normal, clickPosTemp, planetDistance, gradientVector, touchInput, towerVel;
    private ShapeRenderer shapeRenderer;
    private HappyCloud happyCloud;
    private float currentDT, iniPot, shortestDistance, TIMEADJUST;
    private int layer, finalLayer, pointCounter, world;
    private boolean shouldFall, clickedWhileSpawning, touchingWall, lineCheck, lineDraw, justHit, vineDraw, justHitTemp, respawning, justHitBrush, justHitBrushTemp, outZone, justPlanet, justPlanetTemp, paused, justPaused, vineCheck, hasCollided, smallBounce, hitWall, firstSide, shouldMove, hasEdgeCollided, camUpdate, justWall, justTouchUp, justTouchedUp, hasBounced, portalContact, setPortal, resetting;

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
    private TextureRegion ripRegion;
    private Animation portalAnimation;

    PlayState(GameStateManager gsm, int world, int level) { //more initialization, sets up the camera, creates the level, etc.
        super(gsm, world, level);
        this.world = world;
        respawning = true;
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
        warningTexture = new Texture("warning.png");
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
        paused = false;
        smallBounce = false;
        resetting = false;
        shouldMove = false;
        portalContact = false;
        justPaused = false;
        hasCollided = false;
        hasBounced = false;
        hasEdgeCollided = false;
        setPortal = false;
        camUpdate = false;
        vineCheck = true;
        clickedWhileSpawning = true;
        justHitBrush = false;
        Gdx.input.setInputProcessor(this);
        if(world!= 3) {
            quokka = new Quokka(57, 650);
        }
        else{
            quokka = new Quokka(607, 650);
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
        tempBack = new Vector2();
        gradientVector = new Vector3();
        clickPosNo = new Vector2();
        clickPosNo2 = new Vector2();
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
        cam.position.x = quokka.getPosition().x + 80;
        camUpdate = true;
        backButton = new Button(new Texture("back.png"), cam.position.x - cam.viewportWidth / 2 + 15, cam.position.y + cam.viewportHeight / 2 - 65, 0);
        pauseButton = new Button(new Texture("pause.png"), cam.position.x - cam.viewportWidth / 2 + 80, cam.position.y + cam.viewportHeight / 2 - 65, 0);
    }

    PlayState(GameStateManager gsm, int world, int level, Animation portalAnimation) { //more initialization, sets up the camera, creates the level, etc.
        super(gsm, world, level);
        this.world = world;
        this.portalAnimation = portalAnimation;
        setPortal = true;
        respawning = true;
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
        warningTexture = new Texture("warning.png");
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
        paused = false;
        resetting = false;
        smallBounce = false;
        shouldMove = false;
        portalContact = false;
        justPaused = false;
        hasCollided = false;
        hasBounced = false;
        hasEdgeCollided = false;
        camUpdate = false;
        vineCheck = true;
        clickedWhileSpawning = true;
        justHitBrush = false;
        Gdx.input.setInputProcessor(this);
        if(world!= 3) {
            quokka = new Quokka(57, 650);
        }
        else{
            quokka = new Quokka(607, 650);
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
        tempBack = new Vector2();
        gradientVector = new Vector3();
        clickPosNo = new Vector2();
        clickPosNo2 = new Vector2();
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
        cam.position.x = quokka.getPosition().x + 80;
        camUpdate = true;
        backButton = new Button(new Texture("back.png"), cam.position.x - cam.viewportWidth / 2 + 15, cam.position.y + cam.viewportHeight / 2 - 65, 0);
        pauseButton = new Button(new Texture("pause.png"), cam.position.x - cam.viewportWidth / 2 + 80, cam.position.y + cam.viewportHeight / 2 - 65, 0);
    }

    @Override
    public void pause(){
        paused = true;
        pauseButton.dispose();
        pauseButton.setTexture(new Texture("pauseYellow.png"));
    } //pauses the game, called when pause button is clicked

    private void resetState(){
        resetting = true;
        if(portals.size == 0){
            gsm.set(new PlayState(gsm, world, level));
        }
        else{
            gsm.set(new PlayState(gsm, world, level, portalAnimation));
        }
    }

    @Override
    public void update(float dt) { //this is the main loop that moves everything, updates the physics, and checks collisions
        if((!paused) && dt < TIMELIMIT && dt > TIMEMINIMUM) {
            smallBounce = false;
            if (layerVines.size > 0) { //switches between layers of the jungle when hitting a vine, not part of the video
                levelBackground = (layerTextures.get(layer));
                vines.clear();
                vines.addAll(layerVines.get(layer));
            }
            currentDT = dt;
            updateBackground(); //scrolls the screen to be in the correct position relative to the quokka
            if (moveWalls.size != 0) { //this allows the screen to scroll in both x and y dimensions in underwater levels, not shown in my video
                cam.position.y = quokka.getPosition().y;
                if(lineDraw) {
                    clickPosTemp.set(clickPosTemp.x, clickPosTemp.y + quokka.getBottomLeft2().y - quokka.getBottomLeft().y, 0);
                }
            }
            justHitTemp = false;
            if (lineCheck && !hitWall) { //this checks quokka collision with the line and both bounces it and shifts the line appropriately if it ends up inside the quokka
                outZone = false;
                if(!justHit && !hasEdgeCollided) { //this makes sure the line is outside of the null zones, seen on art world 4 as the black rectangles, before the quokka is actually bounced
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
                                for (Obstacle nullZone : nullZones) {
                                    if (nullZone.getObstacleBounds().contains(intersectionPoint(quokka.getBottomLeft(), quokka.getBottomRight(), clickPos2d, clickPos2d2))) {
                                        outZone = false;
                                    }
                                }
                            } else if (doIntersect(quokka.getUpperLeft(), quokka.getUpperRight(), clickPos2d, clickPos2d2)) {
                                outZone = true;
                                for (Obstacle nullZone : nullZones) {
                                    if (nullZone.getObstacleBounds().contains(intersectionPoint(quokka.getUpperLeft(), quokka.getUpperRight(), clickPos2d, clickPos2d2))) {
                                        outZone = false;
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
                                for (Obstacle nullZone : nullZones) {
                                    if (nullZone.getObstacleBounds().contains(intersectionPoint(quokka.getBottomLeft(), quokka.getBottomRight(), clickPos2d, clickPos2d2))) {
                                        justHitTemp = false;
                                    }
                                }
                            } else if (doIntersect(quokka.getUpperLeft(), quokka.getUpperRight(), clickPos2d, clickPos2d2)) {
                                justHitTemp = true;
                                for (Obstacle nullZone : nullZones) {
                                    if (nullZone.getObstacleBounds().contains(intersectionPoint(quokka.getUpperLeft(), quokka.getUpperRight(), clickPos2d, clickPos2d2))) {
                                        justHitTemp = false;
                                    }
                                }
                            }
                        }
                    }
                }
                final float slope = (clickPos2.x != clickPos.x) ? (clickPos2.y - clickPos.y) / (clickPos2.x - clickPos.x) : 5000;
                quokka.getVelocity().scl(dt);
                System.out.println(quokka.getVelocity().y);
                quokka.getVelocity().scl(1/dt);
                if(Math.abs(slope) > 3.75 && willHit(dt)){
                    //add nullZone code if nullZone ever gets added
                    if(!justHitTemp) {
                        quokka.getVelocity().x *= -1;
                    }
                }
                hasEdgeCollided = false;
                Vector3 tempLastVelocity = new Vector3(quokka.getVelocity());
                if (outZone && !justPlanet) {
                    quokka.setVelocity(resultVector(quokka.getVelocity(), clickPos, clickPos2));
                    hasBounced = true;
                    hasCollided = true;
                    justHitTemp = true;
                }
                else if(!hasCollided){
                    clickPos2d.set(clickPos.x, clickPos.y);
                    clickPos2d2.set(clickPos2.x, clickPos2.y);
                    if(doIntersect(quokka.getBottomLeft(), quokka.getBottomLeft2(), clickPos2d, clickPos2d2)){
                        boolean shouldBounce = true;
                        for (Obstacle nullZone : nullZones) {
                            if(!justTouchUp) {
                                if (nullZone.getObstacleBounds().contains(intersectionPoint(quokka.getBottomLeft(), quokka.getBottomLeft2(), clickPos2d, clickPos2d2))) {
                                    shouldBounce = false;
                                }
                            }
                            else if(nullZone.getObstacleBounds().contains(intersectionPoint(quokka.getBottomLeft(), quokka.getBottomLeft2(), clickPosNo, clickPosNo2))){
                                shouldBounce = false;
                            }
                        }
                        justTouchUp = false;
                        if(shouldBounce && !doIntersect(quokka.getUpperLeft2(), quokka.getUpperRight2(), clickPos2d, clickPos2d2)  && !justPlanet) {
                            hasCollided = true;
                            hasEdgeCollided = true;
                            tempWall.set(intersectionPoint(quokka.getBottomLeft(), quokka.getBottomLeft2(), clickPos2d, clickPos2d2));
                            quokka.setPosition(quokka.getPosition().x, tempWall.y);
                            quokka.setVelocity(resultVector(quokka.getVelocity(), clickPos, clickPos2));
                            hasBounced = true;
                        }
                    }
                    else if(doIntersect(quokka.getBottomRight(), quokka.getBottomRight2(), clickPos2d, clickPos2d2)){
                        boolean shouldBounce = true;
                        for (Obstacle nullZone : nullZones) {
                            if(!justTouchUp) {
                                if (nullZone.getObstacleBounds().contains(intersectionPoint(quokka.getBottomRight(), quokka.getBottomRight2(), clickPos2d, clickPos2d2))) {
                                    shouldBounce = false;
                                }
                            }
                            else if(nullZone.getObstacleBounds().contains(intersectionPoint(quokka.getBottomRight(), quokka.getBottomRight2(), clickPosNo, clickPosNo2))){
                                shouldBounce = false;
                            }
                        }
                        justTouchUp = false;
                        if(shouldBounce && !doIntersect(quokka.getUpperLeft2(), quokka.getUpperRight2(), clickPos2d, clickPos2d2)  && !justPlanet) {
                            hasCollided = true;
                            hasEdgeCollided = true;
                            tempWall.set(intersectionPoint(quokka.getBottomRight(), quokka.getBottomRight2(), clickPos2d, clickPos2d2));
                            quokka.setPosition(quokka.getPosition().x, tempWall.y);
                            quokka.setVelocity(resultVector(quokka.getVelocity(), clickPos, clickPos2));
                            hasBounced = true;
                        }
                    }
                    else if(doIntersect(quokka.getUpperLeft(), quokka.getUpperLeft2(), clickPos2d, clickPos2d2)){
                        boolean shouldBounce = true;
                        for (Obstacle nullZone : nullZones) {
                            if(!justTouchUp) {
                                if (nullZone.getObstacleBounds().contains(intersectionPoint(quokka.getUpperLeft(), quokka.getUpperLeft2(), clickPos2d, clickPos2d2))) {
                                    shouldBounce = false;
                                }
                            }
                            else if(nullZone.getObstacleBounds().contains(intersectionPoint(quokka.getUpperLeft(), quokka.getUpperLeft2(), clickPosNo, clickPosNo2))){
                                shouldBounce = false;
                            }
                        }
                        justTouchUp = false;
                        if(shouldBounce && !justPlanet) {
                            hasCollided = true;
                            hasEdgeCollided = true;
                            tempWall.set(intersectionPoint(quokka.getUpperLeft(), quokka.getUpperLeft2(), clickPos2d, clickPos2d2));
                            quokka.setPosition(quokka.getPosition().x, tempWall.y - quokka.getQuokkaBounds().getHeight());
                            quokka.setVelocity(resultVector(quokka.getVelocity(), clickPos, clickPos2));
                            hasBounced = true;
                        }
                    }
                    else if(doIntersect(quokka.getUpperRight(), quokka.getUpperRight2(), clickPos2d, clickPos2d2)){
                        boolean shouldBounce = true;
                        for (Obstacle nullZone : nullZones) {
                            if(!justTouchUp) {
                                if (nullZone.getObstacleBounds().contains(intersectionPoint(quokka.getUpperRight(), quokka.getUpperRight2(), clickPos2d, clickPos2d2))) {
                                    shouldBounce = false;
                                }
                            }
                            else if(nullZone.getObstacleBounds().contains(intersectionPoint(quokka.getUpperRight(), quokka.getUpperRight2(), clickPosNo, clickPosNo2))){
                                shouldBounce = false;
                            }
                        }
                        justTouchUp = false;
                        if(shouldBounce && !justPlanet) {
                            hasCollided = true;
                            hasEdgeCollided = true;
                            tempWall.set(intersectionPoint(quokka.getUpperRight(), quokka.getUpperRight2(), clickPos2d, clickPos2d2));
                            quokka.setPosition(quokka.getPosition().x, tempWall.y - quokka.getQuokkaBounds().getHeight());
                            quokka.setVelocity(resultVector(quokka.getVelocity(), clickPos, clickPos2));
                            hasBounced = true;
                        }
                    }
                }
                if (justHit && justHitTemp && !justTouchedUp){
                    lineAdjust();
                }
                justHit = justHitTemp;
                if(justHit && !justPlanet){
                    quokka.setLastVelocity(tempLastVelocity);
                    quokka.setVelocity(resultVector(quokka.getVelocity(), clickPos, clickPos2));
                    hasBounced = true;
                }
            }
            if(shouldMove) {
                for (Hawk hawk : hawks) { //checks hawk collision, and if there is none sees if the hawk spots the quokka, causing it to dive, or otherwise moves it in circles more (hawk not shown in demo video)
                    if (isConcaveCollision(hawk.getHawkPolygon(), quokka.getQuokkaBounds())) {
                        respawning = true;
                        resetState();
                        break;
                    }
                    hawk.move(quokka.getQuokkaBounds(), dt, quokka.getPosition());
                }
                for(Drone drone : drones){ //Starts drone movement once close enough, moves the drone, and checks collision
                    if(drone.isStartMove() && isCollision(drone.getPolygon(), quokka.getQuokkaBounds())){
                        respawning = true;
                        resetState();
                        break;
                    }
                    if(drone.getPosDrone().x < (cam.position.x + cam.viewportWidth * VIEWPORT_SCALER / 2)){
                        drone.setStartMove(true);
                    }
                    if(drone.isStartMove()) {
                        drone.move(dt, quokka.getPosition());
                    }
                }
                for(JumpFish jumpFish : jumpFishes){//moves fish, causes them to jump if close enough to quokka, and checks collision (not in demo video)
                    if(isCollision(jumpFish.getPolygon(), quokka.getQuokkaBounds())){
                        respawning = true;
                        resetState();
                    }
                    if(jumpFish.getPosJumpFish().x > (cam.position.x - cam.viewportWidth * VIEWPORT_SCALER / 2)){
                        jumpFish.setStartFall(true);
                    }
                    if(jumpFish.isStartFall()) {
                        jumpFish.move(dt, quokka.getPosition());
                    }
                }
                for(LaserGun laserGun : laserGuns){ //rotates laser gun towards the quokka, checks if the laser beam hits the quokka, and shoots another beam if the previous one has gone off screen
                    if(laserGun.isDrawLaser() && isCollision(laserGun.getMyBeam().getPolygon(), quokka.getQuokkaBounds())){
                        respawning = true;
                        resetState();
                        break;
                    }
                    laserGun.shoot(dt, quokka.getPosition()); //this is called shoot but it actually controls all of the laser gun's movement in addition to firing the beam
                    if(laserGun.getMyBeam().getPosBeam().y > cam.viewportHeight || laserGun.getMyBeam().getPosBeam().y + laserGun.getMyBeam().getBeamBounds().getHeight() < 0 || laserGun.getMyBeam().getPosBeam().x + laserGun.getMyBeam().getBeamBounds().getWidth() < (cam.position.x - cam.viewportWidth / 2) || laserGun.getMyBeam().getPosBeam().x > (cam.position.x + cam.viewportWidth / 2)){
                        laserGun.resetShot();
                    }
                }
                for (TallDino tallDino : tallDinos) { //checks dinosaur collision with quokka, moves and flips dinosaur when necessary
                    if (isConcaveCollision(tallDino.getTallDinoPolygon(), quokka.getQuokkaBounds())) {
                        respawning = true;
                        resetState();
                        break;
                    }
                    tallDino.move(dt);
                }
                for (Arrow arrow : arrows) { //shoots the arrows once the quokka is close enough, checks collision with the quokka
                    if (arrow.collides(quokka.getQuokkaBounds())) {
                        respawning = true;
                        resetState();
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
                boolean tempJustWall = false;
                for (Wall wall : walls) { //checks wall collision, movement, and bouncing
                    if (wall.hasSwitch()) {//flags to move the wall and removes the switch from appearing if the quokka hits the switch linked to the wall
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
                    if(wall.getDir() == 1) { //moves walls in the correct direction after the switch is hit
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
                    if (wall.collides(quokka.getQuokkaBounds())) { //checks wall collision and which side of the quokka collided
                        if (!justWall) {
                            tempJustWall = true;
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
                        else{ //if the quokka just hit the wall, removes it from the wall to prevent glitching into it and being stuck
                            float minDistance = Math.abs(quokka.getPosition().x + quokka.getQuokkaBounds().getWidth() - wall.getPosWall().x);
                            int minArea = 0;
                            if(Math.abs(quokka.getPosition().y - wall.getPosWall().y - wall.getWallBounds().getHeight()) < minDistance){
                                minArea = 1;
                                minDistance = Math.abs(quokka.getPosition().y - wall.getPosWall().y - wall.getWallBounds().getHeight());
                            }
                            if(Math.abs(quokka.getPosition().x - wall.getPosWall().x - wall.getWallBounds().getWidth()) < minDistance){
                                minArea = 2;
                                minDistance = Math.abs(quokka.getPosition().x - wall.getPosWall().x - wall.getWallBounds().getWidth());
                            }
                            if(Math.abs(quokka.getPosition().y + quokka.getQuokkaBounds().getHeight() - wall.getPosWall().y) < minDistance){
                                minArea = 3;
                            }
                            if(minArea == 0){
                                while (wall.collides(quokka.getQuokkaBounds())){
                                    quokka.setPosition(quokka.getPosition().x - 10, quokka.getPosition().y);
                                    //quokka.setVelocity(resultVector(quokka.getVelocity(), wall.getBl(), wall.getUl()));
                                }
                            }
                            else if(minArea == 1){
                                while (wall.collides(quokka.getQuokkaBounds())){
                                    quokka.setPosition(quokka.getPosition().x, quokka.getPosition().y + 10);
                                    //quokka.setVelocity(resultVector(quokka.getVelocity(), wall.getUl(), wall.getUr()));
                                }
                            }
                            else if(minArea == 2){
                                while (wall.collides(quokka.getQuokkaBounds())){
                                    quokka.setPosition(quokka.getPosition().x + 10, quokka.getPosition().y);
                                    //quokka.setVelocity(resultVector(quokka.getVelocity(), wall.getBr(), wall.getUr()));
                                }
                            }
                            else{
                                while (wall.collides(quokka.getQuokkaBounds())){
                                    quokka.setPosition(quokka.getPosition().x, quokka.getPosition().y - 10);
                                    //quokka.setVelocity(resultVector(quokka.getVelocity(), wall.getBl(), wall.getBr()));
                                }
                            }
                        }
                    }
                }
                justWall = tempJustWall;
                if (walls.size > 0 && hitWall) { //repositions the quokka appropriately based on what part of the wall it hit
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
                for (MoveWall moveWall : moveWalls) {//this behaves the same as the wall function, but also moves them appropriately for constantly moving walls unrelated that aren't bound to switches
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
                for (Obstacle planet : planets) { //checks collisions and bounces the quokka off of planets, as well as factoring in their gravity to pulling the quokka
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
                if(blackHoles.size > 0) {
                    for (Obstacle planet : blackHoles) { //checks black hole collision, killing the quokka if they hit, as well as adding gravity to pull the quokka appropriately
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
                                    respawning = true;
                                    resetState();
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
                    //blackHoleAnimation.update(dt);
                }
                for (Obstacle nebula : nebulae) { //adds nebula pull to attract the quokka appropriately, collision doesn't impact anything
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
                for (Meteor meteor : meteors) {//drops meteors once the quokka is close enough, destroys them if they hit a line, and kills the quokka if they collide
                    if(camUpdate) {
                        if (meteor.getPosMeteor().x < cam.position.x + cam.viewportWidth / 7) {
                            meteor.setStartFall(true);
                        }
                    }
                    if (meteor.isStartFall()) {
                        if (Intersector.overlaps(meteor.getMeteorCircle(), quokka.getQuokkaBounds())) {
                            if (meteors.contains(meteor, true)) {
                                respawning = true;
                                resetState();
                                break;
                            }
                        }
                        meteor.move(dt);
                        if(lineCheck) {
                            if (meteor.getMeteorCircle().contains(clickPos.x, clickPos.y) || meteor.getMeteorCircle().contains(clickPos2.x, clickPos2.y)) {
                                meteors.removeValue(meteor, true);
                                meteor.dispose();
                            }
                            clickPos2d.set(clickPos.x, clickPos.y);
                            clickPos2d2.set(clickPos2.x, clickPos2.y);
                            if (Intersector.intersectSegmentCircle(clickPos2d, clickPos2d2, new Vector2(meteor.getMeteorCircle().x, meteor.getMeteorCircle().y), (float) Math.pow(meteor.getMeteorCircle().radius, 2))) {
                                meteors.removeValue(meteor, true);
                                meteor.dispose();
                            }
                        }
                    }
                }
                for (Airplane airplane : airplanes) { //moves airplanes once close enough, kills the quokka if they collide (not part of demo)
                    if (airplane.getPosAirplane().x < (cam.position.x - cam.viewportWidth / 2)) {
                        airplane.setStartFall(true);
                    }
                    if (airplane.isStartFall()) {
                        if (airplane.collides(quokka.getQuokkaBounds())) {
                            if (airplanes.contains(airplane, true)) {
                                respawning = true;
                                resetState();
                                break;
                            }
                        }
                        airplane.move(dt);
                    }
                }
                for (Airplane airplane : tropicBirds) {//same as airplane but used in a different world
                    if (airplane.getPosAirplane().x < (cam.position.x - cam.viewportWidth / 2)) {
                        airplane.setStartFall(true);
                    }
                    if (airplane.isStartFall()) {
                        if (airplane.collides(quokka.getQuokkaBounds())) {
                            if (airplanes.contains(airplane, true)) {
                                respawning = true;
                                resetState();
                                break;
                            }
                        }
                        airplane.move(dt);
                    }
                }
                for (Airplane airplane : tropicFish) {//also the same as airplane but doing this makes it much easier to keep track of things
                    if (airplane.getPosAirplane().x < (cam.position.x - cam.viewportWidth / 2)) {
                        airplane.setStartFall(true);
                    }
                    if (airplane.isStartFall()) {
                        if (airplane.collides(quokka.getQuokkaBounds())) {
                            if (airplanes.contains(airplane, true)) {
                                respawning = true;
                                resetState();
                                break;
                            }
                        }
                        airplane.move(dt);
                    }
                }
                if(portals.size > 0) {//updates the portal animation, checks quokka collisions with portals, and moves the quokka to the paired portal if it's hitting one, as well as scaling velocity based on the new position in order to preserve conservation of energy and prevent the quokka from gaining infinite height/speed
                    boolean touchingPortal = false;
                    boolean touchedPortal = false;
                    boolean portalRect = false;
                    for (Obstacle portal : portals) {
                        if (Intersector.overlaps(portal.getObstacleCircle(), quokka.getQuokkaBounds())) {
                            touchingPortal = true;
                            if (!(quokka.isTouchingPortal() || touchedPortal || portalContact)) {
                                portalContact = true;
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
                                    quokka.setPosition(portals.get(portals.indexOf(portal, true) - 1).getPosObstacle().x + quokka.getPosition().x - portal.getPosObstacle().x, portals.get(portals.indexOf(portal, true) - 1).getPosObstacle().y + quokka.getPosition().y - portal.getPosObstacle().y);
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
                                if (world != 3) {
                                    cam.position.x = quokka.getPosition().x + 80;
                                    if (lineDraw) {
                                        clickPosTemp.set(clickPosTemp.x + quokka.getBottomLeft2().x - quokka.getBottomLeft().x, clickPosTemp.y, 0);
                                    }
                                    camUpdate = true;
                                    updateBackgroundPortal();
                                }
                            }
                        }
                        if(portal.getObstacleBounds().overlaps(quokka.getQuokkaBounds())){
                            portalRect = true;
                        }
                    }
                    quokka.setTouchingPortal(touchingPortal);
                    if(!portalRect){
                        portalContact = false;
                    }
                    portalAnimation.update(dt);
                }
                for(Obstacle brush : brushes){//moves the brushes and draws the lines behind them, checks quokka collision with lines and bounces it appropriately
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
                            if (lineBounce(brush.getMoveSpots().get(0), pointBetween(brush.getMoveSpots().get(0), brush.getMoveSpots().get(brush.getMoveSpots().size - 1), (netDistance - brush.getTotalDistance()) > distance(brush.getMoveSpots().get(0), brush.getMoveSpots().get(brush.getMoveSpots().size - 1)) ? (float) distance(brush.getMoveSpots().get(0), brush.getMoveSpots().get(brush.getMoveSpots().size - 1)) : (netDistance - brush.getTotalDistance())))) {
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
                for (EvilCloud cloud : clouds) {//kills the quokka and resets the level if the quokka hits an evil cloud
                    if (isConcaveCollision(cloud.getCloudPoly(), quokka.getQuokkaBounds())) {
                        respawning = true;
                        resetState();
                        break;
                    }
                }
                for (BonusQuokka bonusQuokka : bonusQuokkas) {//collects the bonus quokka for the level if hit by the quokka. it's currently an array in case of future additional bonus quokkas per level
                    if (bonusQuokka.collides(quokka.getQuokkaBounds())) {
                        if (!collectedQuokkas.get(bonusQuokkas.indexOf(bonusQuokka, false))) {
                            collectedQuokkas.set(bonusQuokkas.indexOf(bonusQuokka, false), true);
                            bonusQuokka.dispose();
                        }
                    }
                }
            }
            if (world == 3 && shouldFall) {//moves camera upwards in tower levels while locking it horizontally
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
            backButton.getPosButton().x = cam.position.x - cam.viewportWidth / 2 + 15; //positions buttons consistently in the correct part of the screen
            pauseButton.getPosButton().x = cam.position.x - cam.viewportWidth / 2 + 80;
            backButton.getPosButton().y = cam.position.y + cam.viewportHeight / 2 - 65;
            pauseButton.getPosButton().y = cam.position.y + cam.viewportHeight / 2 - 65;
            backButton.getButtonBounds().set(backButton.getPosButton().x, backButton.getPosButton().y, backButton.getButtonBounds().getWidth(), backButton.getButtonBounds().getHeight());
            pauseButton.getButtonBounds().set(pauseButton.getPosButton().x, pauseButton.getPosButton().y, pauseButton.getButtonBounds().getWidth(), pauseButton.getButtonBounds().getHeight());
            if (shouldFall && !smallBounce) { //pushes the quokka downwards if it hits a wind gust (not in demo)
                boolean inGust = false;
                for(Obstacle windGust : windGusts){
                    if(windGust.collides(quokka.getQuokkaBounds())){
                        inGust = true;
                    }
                }
                quokka.update(dt, inGust);
            } //gohere
            if(vineCheck) {//moves the quokka to the new layer of the jungle and updates its position if it hits a vine
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
                    respawning = true;
                    resetState();
                }
            }
            if (layer == finalLayer) { //moves the game back to the menu if the quokka hits the rainbow cloud, increases level by 1 until level 10, then increases the world and sets level back to 1
                if (isConcaveCollision(happyCloud.getCloudPoly(), quokka.getQuokkaBounds())) {
                    gsm.set(new CompleteState(gsm, world, level, bonusQuokkas.size > 0 ? collectedQuokkas.get(0) : false));
                }
            }
            cam.update();
        }
        respawning = false;
        justTouchedUp = false;
    }//end of the update method
    private void planetFixer(){ //scales velocity correctly based on the current position of the quokka relative to astronomical objects, preserving total energy
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
        velocityTemp2.scl((float) Math.sqrt(Math.abs(2.0*(iniPot - currentPot))));
        velocityTemp2.scl((float) (1/Math.sqrt(currentDT)));
    }
    private void lineAdjust(){
        final float slope = (clickPos2.y - clickPos.y) / (clickPos2.x - clickPos.x);
        final float perpSlope = slope != 0 ? -1 / slope : -1;
        Vector2 perpPos = new Vector2(1, perpSlope).nor();
        Vector2 perpPos2 = new Vector2(1, -perpSlope).nor();
        Vector2 quokkaVelocity2d = new Vector2(quokka.getVelocity().x, quokka.getVelocity().y);
        Vector2 perpPoint = new Vector2(Math.acos(perpPos.dot(quokkaVelocity2d)) < Math.acos(perpPos2.dot(quokkaVelocity2d)) ? perpPos : perpPos2);
        clickPos2d.set(clickPos.x, clickPos.y);
        clickPos2d2.set(clickPos2.x, clickPos2.y);
        if(slope > 0.7){
            if(doIntersect(quokka.getBottomRight(), quokka.getUpperRight(), clickPos2d, clickPos2d2)){
                //if(quokka.getVelocity().y <= -BOUNCEADJUST) {
                    while (quokkaLineHit()) {
                        clickPos.set(clickPos.x, clickPos.y - perpPoint.y / Math.abs(perpPoint.y), 0);
                        clickPos2.set(clickPos2.x, clickPos2.y - perpPoint.y / Math.abs(perpPoint.y), 0);
                        clickPos2d.set(clickPos.x, clickPos.y);
                        clickPos2d2.set(clickPos2.x, clickPos2.y);
                    }
                /*}
                else{
                    while (quokkaLineHit()){
                        while (quokkaLineHit()) {
                            clickPos.set(clickPos.x, clickPos.y - perpPoint.y / Math.abs(perpPoint.y), 0);
                            clickPos2.set(clickPos2.x, clickPos2.y - perpPoint.y / Math.abs(perpPoint.y), 0);
                            clickPos2d.set(clickPos.x, clickPos.y);
                            clickPos2d2.set(clickPos2.x, clickPos2.y);
                        }
                    }
                }*/
            }
            if(doIntersect(quokka.getBottomLeft(), quokka.getUpperLeft(), clickPos2d, clickPos2d2)){
                if(quokka.getVelocity().x > 0 && quokka.getVelocity().y < 0 && doIntersect(quokka.getUpperLeft(), quokka.getUpperRight(), clickPos2d, clickPos2d2)) {
                    while (quokkaLineHit()) {
                        clickPos.set(clickPos.x, clickPos.y - 1, 0);
                        clickPos2.set(clickPos2.x, clickPos2.y - 1, 0);
                        clickPos2d.set(clickPos.x, clickPos.y);
                        clickPos2d2.set(clickPos2.x, clickPos2.y);
                    }
                }
                while (quokkaLineHit()){
                    clickPos.set(clickPos.x, clickPos.y + perpPoint.y / Math.abs(perpPoint.y), 0);
                    clickPos2.set(clickPos2.x, clickPos2.y + perpPoint.y / Math.abs(perpPoint.y), 0);
                    clickPos2d.set(clickPos.x, clickPos.y);
                    clickPos2d2.set(clickPos2.x, clickPos2.y);
                }
            }
            if(doIntersect(quokka.getBottomLeft(), quokka.getBottomRight(), clickPos2d, clickPos2d2)){
                while (quokkaLineHit()){
                    clickPos.set(clickPos.x + perpPoint.x / Math.abs(perpPoint.x), clickPos.y, 0);
                    clickPos2.set(clickPos2.x + perpPoint.x / Math.abs(perpPoint.x), clickPos2.y, 0);
                    clickPos2d.set(clickPos.x, clickPos.y);
                    clickPos2d2.set(clickPos2.x, clickPos2.y);
                }
                //paused = true;
            }
            else if(doIntersect(quokka.getUpperLeft(), quokka.getUpperRight(), clickPos2d, clickPos2d2)){
                while (quokkaLineHit()){
                    clickPos.set(clickPos.x - perpPoint.x / Math.abs(perpPoint.x), clickPos.y, 0);
                    clickPos2.set(clickPos2.x - perpPoint.x / Math.abs(perpPoint.x), clickPos2.y, 0);
                    clickPos2d.set(clickPos.x, clickPos.y);
                    clickPos2d2.set(clickPos2.x, clickPos2.y);
                }
            }
        }
        else if(slope < -0.7){
            if(doIntersect(quokka.getBottomRight(), quokka.getUpperRight(), clickPos2d, clickPos2d2)){
                if(quokka.getVelocity().y <= 0 && (doIntersect(quokka.getUpperLeft(), quokka.getUpperRight(), clickPos2d, clickPos2d2) || doIntersect(quokka.getBottomLeft(), quokka.getUpperLeft(), clickPos2d, clickPos2d2))) {
                    while (quokkaLineHit()) {
                        clickPos.set(clickPos.x, clickPos.y - 1, 0);
                        clickPos2.set(clickPos2.x, clickPos2.y - 1, 0);
                        clickPos2d.set(clickPos.x, clickPos.y);
                        clickPos2d2.set(clickPos2.x, clickPos2.y);
                    }
                }
                else {
                    //if(quokka.getVelocity().y > -BOUNCEADJUST) {
                    while (quokkaLineHit()) {
                        clickPos.set(clickPos.x, clickPos.y - perpPoint.y / Math.abs(perpPoint.y), 0);
                        clickPos2.set(clickPos2.x, clickPos2.y - perpPoint.y / Math.abs(perpPoint.y), 0);
                        clickPos2d.set(clickPos.x, clickPos.y);
                        clickPos2d2.set(clickPos2.x, clickPos2.y);
                    }
                }
                /*}
                else{
                    while (quokkaLineHit()) {
                        clickPos.set(clickPos.x - perpPoint.x / Math.abs(perpPoint.x), clickPos.y + perpPoint.y / Math.abs(perpPoint.y), 0);
                        clickPos2.set(clickPos2.x - perpPoint.x / Math.abs(perpPoint.x), clickPos2.y + perpPoint.y / Math.abs(perpPoint.y), 0);
                        clickPos2d.set(clickPos.x, clickPos.y);
                        clickPos2d2.set(clickPos2.x, clickPos2.y);
                    }
                }*/
            }
            if(doIntersect(quokka.getBottomLeft(), quokka.getUpperLeft(), clickPos2d, clickPos2d2)){
                while (quokkaLineHit()) {
                    clickPos.set(clickPos.x, clickPos.y + perpPoint.y / Math.abs(perpPoint.y), 0);
                    clickPos2.set(clickPos2.x, clickPos2.y + perpPoint.y / Math.abs(perpPoint.y), 0);
                    clickPos2d.set(clickPos.x, clickPos.y);
                    clickPos2d2.set(clickPos2.x, clickPos2.y);
                }
            }
            if(doIntersect(quokka.getBottomLeft(), quokka.getBottomRight(), clickPos2d, clickPos2d2)){
                while (quokkaLineHit()){
                    clickPos.set(clickPos.x - perpPoint.x / Math.abs(perpPoint.x), clickPos.y, 0);
                    clickPos2.set(clickPos2.x - perpPoint.x / Math.abs(perpPoint.x), clickPos2.y, 0);
                    clickPos2d.set(clickPos.x, clickPos.y);
                    clickPos2d2.set(clickPos2.x, clickPos2.y);
                }
                //paused = true;
            }
            if(doIntersect(quokka.getUpperLeft(), quokka.getUpperRight(), clickPos2d, clickPos2d2)){
                while (quokkaLineHit()){
                    clickPos.set(clickPos.x + perpPoint.x / Math.abs(perpPoint.x), clickPos.y, 0);
                    clickPos2.set(clickPos2.x + perpPoint.x / Math.abs(perpPoint.x), clickPos2.y, 0);
                    clickPos2d.set(clickPos.x, clickPos.y);
                    clickPos2d2.set(clickPos2.x, clickPos2.y);
                }
            }
        }
        else if(quokka.getVelocity().x < 0) {
            if(slope >= 0) {
                quokka.getVelocity().scl(currentDT);
                if(quokka.getVelocity().y <= TIMEADJUST) {
                    while (quokkaLineHit()) {
                        clickPos.set(clickPos.x + perpPoint.x / Math.abs(perpPoint.x), clickPos.y - perpPoint.y / Math.abs(perpPoint.y), 0);
                        clickPos2.set(clickPos2.x + perpPoint.x / Math.abs(perpPoint.x), clickPos2.y - perpPoint.y / Math.abs(perpPoint.y), 0);
                        clickPos2d.set(clickPos.x, clickPos.y);
                        clickPos2d2.set(clickPos2.x, clickPos2.y);
                    }
                }
                else{
                    while (quokkaLineHit()){
                        clickPos.set(clickPos.x, clickPos.y + perpPoint.y / Math.abs(perpPoint.y), 0);
                        clickPos2.set(clickPos2.x, clickPos2.y + perpPoint.y / Math.abs(perpPoint.y), 0);
                        clickPos2d.set(clickPos.x, clickPos.y);
                        clickPos2d2.set(clickPos2.x, clickPos2.y);
                    }
                }
                quokka.getVelocity().scl(1/currentDT);
            }
            else{
                quokka.getVelocity().scl(currentDT);
                if(quokka.getVelocity().y <= TIMEADJUST) {
                    while (quokkaLineHit()) {
                        clickPos.set(clickPos.x, clickPos.y - 1, 0);
                        clickPos2.set(clickPos2.x, clickPos2.y - 1, 0);
                        clickPos2d.set(clickPos.x, clickPos.y);
                        clickPos2d2.set(clickPos2.x, clickPos2.y);
                    }
                }
                else{
                    /*if(slope > -0.2 && world != 4) {
                        while (quokkaLineHit()) {
                            clickPos.set(clickPos.x + perpPoint.x / Math.abs(perpPoint.x), clickPos.y + 1, 0);
                            clickPos2.set(clickPos2.x + perpPoint.x / Math.abs(perpPoint.x), clickPos2.y + 1, 0);
                            clickPos2d.set(clickPos.x, clickPos.y);
                            clickPos2d2.set(clickPos2.x, clickPos2.y);
                        }
                    }
                    else{*/
                        while (quokkaLineHit()) {
                            clickPos.set(clickPos.x, clickPos.y + 1, 0);
                            clickPos2.set(clickPos2.x, clickPos2.y + 1, 0);
                            clickPos2d.set(clickPos.x, clickPos.y);
                            clickPos2d2.set(clickPos2.x, clickPos2.y);
                        }
                    //}
                }
                quokka.getVelocity().scl(1/currentDT);
            }
        }
        else{
            if(slope >= 0) {
                quokka.getVelocity().scl(currentDT);
                if(quokka.getVelocity().y <= TIMEADJUST) {
                    while (quokkaLineHit()) {
                        clickPos.set(clickPos.x, clickPos.y - perpPoint.y / Math.abs(perpPoint.y), 0);
                        clickPos2.set(clickPos2.x, clickPos2.y - perpPoint.y / Math.abs(perpPoint.y), 0);
                        clickPos2d.set(clickPos.x, clickPos.y);
                        clickPos2d2.set(clickPos2.x, clickPos2.y);
                    }
                }
                /*else if(slope < 0.2 && world != 4){
                    while (quokkaLineHit()) {
                        clickPos.set(clickPos.x - perpPoint.x / Math.abs(perpPoint.x), clickPos.y + 1, 0);
                        clickPos2.set(clickPos2.x - perpPoint.x / Math.abs(perpPoint.x), clickPos2.y + 1, 0);
                        clickPos2d.set(clickPos.x, clickPos.y);
                        clickPos2d2.set(clickPos2.x, clickPos2.y);
                    }
                }*/
                else{
                    while (quokkaLineHit()) {
                        clickPos.set(clickPos.x, clickPos.y + 1, 0);
                        clickPos2.set(clickPos2.x, clickPos2.y + 1, 0);
                        clickPos2d.set(clickPos.x, clickPos.y);
                        clickPos2d2.set(clickPos2.x, clickPos2.y);
                    }
                }
                quokka.getVelocity().scl(1/currentDT);
            }
            else{
                quokka.getVelocity().scl(currentDT);
                if(quokka.getVelocity().y <= TIMEADJUST) {
                    while (quokkaLineHit()) {
                        clickPos.set(clickPos.x - perpPoint.x / Math.abs(perpPoint.x), clickPos.y + perpPoint.y / Math.abs(perpPoint.y), 0);
                        clickPos2.set(clickPos2.x - perpPoint.x / Math.abs(perpPoint.x), clickPos2.y + perpPoint.y / Math.abs(perpPoint.y), 0);
                        clickPos2d.set(clickPos.x, clickPos.y);
                        clickPos2d2.set(clickPos2.x, clickPos2.y);
                    }
                }
                else{
                    while (quokkaLineHit()) {
                        clickPos.set(clickPos.x, clickPos.y - perpPoint.y / Math.abs(perpPoint.y), 0);
                        clickPos2.set(clickPos2.x, clickPos2.y - perpPoint.y / Math.abs(perpPoint.y), 0);
                        clickPos2d.set(clickPos.x, clickPos.y);
                        clickPos2d2.set(clickPos2.x, clickPos2.y);
                    }
                }
                quokka.getVelocity().scl(1/currentDT);
            }
        }
        hasCollided = false;
    }
    private void lineAdjustTouchUp(){
        final float slope = (clickPos2.x != clickPos.x) ? (clickPos2.y - clickPos.y) / (clickPos2.x - clickPos.x) : 5000;
        clickPos2d.set(clickPos.x, clickPos.y);
        clickPos2d2.set(clickPos2.x, clickPos2.y);
        if(Math.abs(slope) > 3.5 && quokka.getVelocity().x != 0){
            if(quokka.getVelocity().x > 0){
                while (quokkaLineHit()) {
                    clickPos.set(clickPos.x + 1, clickPos.y, 0);
                    clickPos2.set(clickPos2.x + 1, clickPos2.y, 0);
                    clickPos2d.set(clickPos.x, clickPos.y);
                    clickPos2d2.set(clickPos2.x, clickPos2.y);
                }
            }
            if(quokka.getVelocity().x < 0){
                while (quokkaLineHit()) {
                    clickPos.set(clickPos.x - 1, clickPos.y, 0);
                    clickPos2.set(clickPos2.x - 1, clickPos2.y, 0);
                    clickPos2d.set(clickPos.x, clickPos.y);
                    clickPos2d2.set(clickPos2.x, clickPos2.y);
                }
            }
        }
        else {
            lineAdjust();
        }
        hasCollided = false;
    }
    private float lineY(float x){ //finds the y value of the drawn line at a given x coordinate
        if(clickPos2.x > clickPos.x) {
            final float slope = (clickPos2.y - clickPos.y) / (clickPos2.x - clickPos.x);
            return (slope * (x - clickPos.x) + clickPos.y);
        }
        else{
            final float slope = (clickPos.y - clickPos2.y) / (clickPos.x - clickPos2.x);
            return (slope * (x - clickPos2.x) + clickPos2.y);
        }
    }

    private float lineY(float x, Vector2 p1, Vector2 p2){ //finds the y value of any line at a given x coordinate (I'll combine these into 1 function after the application I didn't know about default values back when I made this)
        if(p2.x > p1.x) {
            final float slope = (p2.y - p1.y) / (p2.x - p1.x);
            return (slope * (x - p1.x) + p1.y);
        }
        else{
            final float slope = (p1.y - p2.y) / (p1.x - p2.x);
            return (slope * (x - p2.x) + p2.y);
        }
    }

    private boolean lineBounce(Vector2 p1, Vector2 p2){ //bounces the quokka in the correct direction and repositions it appropriately if it hits a line
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
                        //
                    }
                } else if ((((quokka.getPosition().x + quokka.getTexture().getWidth()) > p1.x) && ((quokka.getPosition().x + quokka.getTexture().getWidth()) < p2.x)) || (((quokka.getPosition().x + quokka.getTexture().getWidth()) < p1.x) && ((quokka.getPosition().x + quokka.getTexture().getWidth()) > p2.x))) {
                    while (quokka.getQuokkaBounds().contains(quokka.getPosition().x, lineY(quokka.getPosition().x, p1, p2)) || quokka.getQuokkaBounds().contains(quokka.getPosition().x + quokka.getTexture().getWidth(), lineY(quokka.getPosition().x + quokka.getTexture().getWidth(), p1, p2))) {
                        quokka.setPosition(quokka.getPosition().x, quokka.getPosition().y + 10);
                        bounce6 = true;
                        //
                    }
                }
                while (doIntersect(quokka.getUpperLeft2(), quokka.getUpperRight2(), p1, p2) && (doIntersect(quokka.getBottomLeft2(), quokka.getBottomRight2(), p1, p2) || doIntersect(quokka.getBottomLeft2(), quokka.getUpperLeft2(), p1, p2) || doIntersect(quokka.getBottomRight2(), quokka.getUpperRight2(), p1, p2))) {
                    if (quokka.getVelocity().x < 0) {
                        quokka.setPosition(quokka.getPosition().x - 10, quokka.getPosition().y);
                        bounce7 = true;
                        //
                    } else {
                        quokka.setPosition(quokka.getPosition().x + 10, quokka.getPosition().y);
                        bounce8 = true;
                        //
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
                        //
                    }
                } else if ((((quokka.getPosition().x + quokka.getTexture().getWidth()) > p1.x) && ((quokka.getPosition().x + quokka.getTexture().getWidth()) < p2.x)) || (((quokka.getPosition().x + quokka.getTexture().getWidth()) < p1.x) && ((quokka.getPosition().x + quokka.getTexture().getWidth()) > p2.x))) {
                    while (quokka.getQuokkaBounds().contains(quokka.getPosition().x, lineY(quokka.getPosition().x, p1, p2)) || quokka.getQuokkaBounds().contains(quokka.getPosition().x + quokka.getTexture().getWidth(), lineY(quokka.getPosition().x + quokka.getTexture().getWidth(), p1, p2))) {
                        quokka.setPosition(quokka.getPosition().x, quokka.getPosition().y + 10);
                        bounce6 = true;
                        //
                    }
                }
                while (doIntersect(quokka.getUpperLeft2(), quokka.getUpperRight2(), p1, p2) && (doIntersect(quokka.getBottomLeft2(), quokka.getBottomRight2(), p1, p2) || doIntersect(quokka.getBottomLeft2(), quokka.getUpperLeft2(), p1, p2) || doIntersect(quokka.getBottomRight2(), quokka.getUpperRight2(), p1, p2))) {
                    if (quokka.getVelocity().x < 0) {
                        quokka.setPosition(quokka.getPosition().x - 10, quokka.getPosition().y);
                        bounce7 = true;
                        //
                    } else {
                        quokka.setPosition(quokka.getPosition().x + 10, quokka.getPosition().y);
                        bounce8 = true;
                        //
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

    private Vector2 perpPoint(Vector2 p1, Vector2 p2){ //finds a point that, when used in conjunction with point p2, will make a perpendicular line to the line created by using p1 and p2 as the endpoints
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
    private boolean onSegment(Vector2 p, Vector2 q, Vector2 r) //functions onSegment, orientation, and doIntersect were things I found online which I use to check some collisions by seeing if the sides of the quokka overlap various obstacles' sides or lines
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
    private boolean willHit(float dt){
        quokka.getVelocity().scl(dt);
        if(doIntersect(clickPos2d, clickPos2d2, new Vector2(quokka.getPosition().x, quokka.getPosition().y), new Vector2(quokka.getPosition().x + quokka.getVelocity().x, quokka.getPosition().y + quokka.getVelocity().y))){
            quokka.getVelocity().scl(1/dt);
            return true;
        }
        if(doIntersect(clickPos2d, clickPos2d2, new Vector2(quokka.getPosition().x + quokka.getQuokkaBounds().getWidth(), quokka.getPosition().y + quokka.getQuokkaBounds().getHeight()), new Vector2(quokka.getPosition().x + quokka.getQuokkaBounds().getWidth() + quokka.getVelocity().x, quokka.getPosition().y + quokka.getQuokkaBounds().getHeight() + quokka.getVelocity().y))){
            quokka.getVelocity().scl(1/dt);
            return true;
        }
        if(doIntersect(clickPos2d, clickPos2d2, new Vector2(quokka.getPosition().x + quokka.getQuokkaBounds().getWidth(), quokka.getPosition().y), new Vector2(quokka.getPosition().x + quokka.getQuokkaBounds().getWidth() + quokka.getVelocity().x, quokka.getPosition().y + quokka.getVelocity().y))){
            quokka.getVelocity().scl(1/dt);
            return true;
        }
        if(doIntersect(clickPos2d, clickPos2d2, new Vector2(quokka.getPosition().x, quokka.getPosition().y + quokka.getQuokkaBounds().getHeight()), new Vector2(quokka.getPosition().x + quokka.getVelocity().x, quokka.getPosition().y + quokka.getQuokkaBounds().getHeight() + quokka.getVelocity().y))){
            quokka.getVelocity().scl(1/dt);
            return true;
        }
        Rectangle testBounds = new Rectangle(quokka.getQuokkaBounds());
        testBounds.setX(testBounds.getX() + quokka.getVelocity().x);
        testBounds.setY(testBounds.getY() + quokka.getVelocity().y);
        if(testBounds.contains(clickPos2d) || testBounds.contains(clickPos2d2)){
            quokka.getVelocity().scl(1/dt);
            return true;
        }
        quokka.getVelocity().scl(1/dt);
        return false;
    }

    private Vector2 pointBetween(Vector2 p1, Vector2 p2, float distanceBetween){ //finds a point located on the line between p1 and p2 at a given distance from p2
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

    private boolean quokkaLineHit(){//checks if the quokka collides with the drawn line
        Polygon rPoly = new Polygon(new float[] { 0, 0, quokka.getQuokkaBounds().width, 0, quokka.getQuokkaBounds().width, quokka.getQuokkaBounds().height, 0, quokka.getQuokkaBounds().height });
        rPoly.setPosition(quokka.getPosition().x, quokka.getPosition().y);
        clickPos2d.set(clickPos.x, clickPos.y);
        clickPos2d2.set(clickPos2.x, clickPos2.y);
        if(Intersector.intersectSegmentPolygon(clickPos2d, clickPos2d2, rPoly)){
            return true;
        }
        if(Intersector.intersectSegments(clickPos2d, clickPos2d2, quokka.getBottomLeft(), quokka.getBottomRight(), null)){
            return true;
        }
        if(Intersector.intersectSegments(clickPos2d, clickPos2d2, quokka.getBottomLeft(), quokka.getUpperLeft(), null)){
            return true;
        }
        if(Intersector.intersectSegments(clickPos2d, clickPos2d2, quokka.getBottomRight(), quokka.getUpperRight(), null)){
            return true;
        }
        if(Intersector.intersectSegments(clickPos2d, clickPos2d2, quokka.getUpperLeft(), quokka.getUpperRight(), null)){
            return true;
        }
        if(quokka.getQuokkaBounds().contains(clickPos2d)){
            return true;
        }
        return quokka.getQuokkaBounds().contains(clickPos2d2);
    }

    private Vector2 pointBetween(Vector2 p1, Vector2 p2, float distanceBetween, boolean quokBack){//finds a point between p1 and p2 located at a given distance from p1. the quokBack boolean is just used as a way to have different functions based on whether the distance should be from p1 or p2 while not having to type an additional parameter in the majority of use cases due to the other pointBetween function
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

    private Vector2 intersectionPoint(Vector2 a1, Vector2 a2, Vector2 b1, Vector2 b2){ //fines the point where the line between the points a1 and a2 and the line between the points b1 and b2 intersect
        xdiff.set(a1.x - a2.x, b1.x - b2.x);
        ydiff.set(a1.y - a2.y, b1.y - b2.y);
        float div = det(xdiff, ydiff);
        tempDet.set(det(a1,a2), det(b1,b2));
        float x = det(tempDet, xdiff) / div;
        float y = det(tempDet, ydiff) / div;
        tempDet.set(x,y);
        return tempDet;
    }
    private double distance(Vector2 point1, Vector2 point2){ //returns the distance between two points
        return Math.sqrt(Math.pow(point2.y-point1.y, 2) + Math.pow(point2.x-point1.x, 2));
    }

    private float det(Vector2 a1, Vector2 a2){//finds the determinant of a 2x2 matrix made of vectors a1 and a2
        return a1.x * a2.y - a1.y * a2.x;
    }

    @Override
    public void render(SpriteBatch sb) {//renders all of the art assets including the backgrounds, the obstacles, the quokka, the lines, etc.
            sb.setProjectionMatrix(cam.combined);
            sb.begin();
            if (moveWalls.size != 0) {
                sb.setColor(DEPTHSCALER * (quokka.getPosition().y - 650) + 1, DEPTHSCALER * (quokka.getPosition().y - 650) + 1, DEPTHSCALER * (quokka.getPosition().y - 650) + 1, 1f);
            }
            sb.draw(levelBackground, levelBackgroundPos1.x, levelBackgroundPos1.y);
            sb.draw(levelBackground, levelBackgroundPos2.x, levelBackgroundPos2.y);
            sb.draw(levelBackground, levelBackgroundPos3.x, levelBackgroundPos3.y);
            sb.draw(levelBackground, levelBackgroundPos4.x, levelBackgroundPos4.y);
        for (Obstacle nebula : nebulae) {
            sb.draw(nebula.getTexture(), nebula.getPosObstacle().x, nebula.getPosObstacle().y);
        }
            sb.end();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setProjectionMatrix(cam.combined);
            shapeRenderer.setAutoShapeType(true);
            for (Obstacle windGust : windGusts) {
                shapeRenderer.setColor(Color.GREEN);
                shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.rect(windGust.getPosObstacle().x, windGust.getPosObstacle().y, windGust.getObstacleBounds().getWidth(), windGust.getObstacleBounds().getHeight());
            }
            shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
            if(world == 1 && level == 1){
                shapeRenderer.setColor(Color.YELLOW);
                boolean yesLine = true;
                for(int i = -142; i <= 258; i+=50) {
                    if(yesLine) {
                        shapeRenderer.rectLine(i, (float)(-0.286*i + 293), i + 50, (float)(-0.286*(i+50) + 293), LINEWIDTH);
                    }
                    yesLine = !yesLine;
                }
            }
        if(world == 1 && level == 2){
            shapeRenderer.setColor(Color.YELLOW);
            boolean yesLine = true;
            if(!hasBounced) {
                for (int i = -142; i <= 258; i += 50) {
                    if (yesLine) {
                        shapeRenderer.rectLine(i, (float) (-0.161 * i + 184), i + 50, (float) (-0.161 * (i + 50) + 184), LINEWIDTH);
                    }
                    yesLine = !yesLine;
                }
            }
            yesLine = true;
            for(int i = 750; i <= 1250; i+=50) {
                if(yesLine) {
                    shapeRenderer.rectLine(i, (float)(-0.104*i + 380), i + 50, (float)(-0.104*(i+50) + 380), LINEWIDTH);
                }
                yesLine = !yesLine;
            }
        }
        if(world == 2 && level == 1 && !hasBounced){
            shapeRenderer.setColor(Color.YELLOW);
            boolean yesLine = true;
            for(int i = -92; i <= 408; i+=50) {
                if(yesLine) {
                    shapeRenderer.rectLine(i, (float)(0.244*i + 184), i + 50, (float)(0.244*(i+50) + 184), LINEWIDTH);
                }
                yesLine = !yesLine;
            }
        }
            else if(world == 2 && level == 2){
                shapeRenderer.setColor(Color.YELLOW);
                boolean yesLine = true;
                for(int i = 610; i <= 860; i+=50) {
                    if(yesLine) {
                        shapeRenderer.rectLine(i, 500, i + 50, 500, LINEWIDTH);
                    }
                    yesLine = !yesLine;
                }
            }
            if (vineDraw) {
                if (clickPos2.y != -100) {
                    shapeRenderer.setColor(Color.BROWN);
                    shapeRenderer.rectLine(clickPos.x, clickPos.y, clickPos2.x, clickPos2.y, LINEWIDTH);
                } else if (clickPosTemp.y != -100) {
                    shapeRenderer.setColor(Color.YELLOW);
                    shapeRenderer.rectLine(clickPos.x, clickPos.y, clickPosTemp.x, clickPosTemp.y, LINEWIDTH);
                }
            }
            shapeRenderer.end();
            sb.begin();
            for (Obstacle nullZone : nullZones) {
                sb.draw(ripRegion, nullZone.getPosObstacle().x, nullZone.getPosObstacle().y, nullZone.getObstacleBounds().getWidth(), nullZone.getObstacleBounds().getHeight());
            }
            for (Wall wall : walls) {
                sb.draw(wall.getTexture(), wall.getPosWall().x, wall.getPosWall().y);
            }
            for (Obstacle wallSwitch : switches) {
                sb.draw(wallSwitch.getTexture(), wallSwitch.getPosObstacle().x, wallSwitch.getPosObstacle().y);
            }
            for (Obstacle brush : brushes) {
                sb.draw(brush.getTexture(), brush.getPosObstacle().x, brush.getPosObstacle().y);
            }
            for (Stoplight stoplight : stoplights) {
                sb.draw(stoplight.getTexture(), stoplight.getPosStoplight().x, stoplight.getPosStoplight().y);
            }
            if (layer == finalLayer) {
                sb.draw(happyCloud.getTexture(), happyCloud.getPosCloud().x, happyCloud.getPosCloud().y);
            }
            sb.end();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setProjectionMatrix(cam.combined);
            shapeRenderer.setAutoShapeType(true);
            for (Obstacle brush : brushes) {
                shapeRenderer.setColor(Color.BROWN);
                pointCounter = 0;
                float netDistance = 0;
                int i = brush.getMoveTracker() - 1;
                shapeRenderer.rectLine(brush.getPosObstacle(), distance(brush.getPosObstacle(), brush.getMoveSpots().get(i = (i >= 0 ? i : brush.getMoveSpots().size + i))) > brush.getLineback() ? pointBetween(brush.getPosObstacle(), brush.getMoveSpots().get(i = (i >= 0 ? i : brush.getMoveSpots().size + i)), brush.getLineback(), true) : brush.getMoveSpots().get(i = (i >= 0 ? i : brush.getMoveSpots().size + i)), LINEWIDTH);
                netDistance += distance(brush.getPosObstacle(), brush.getMoveSpots().get(i = i >= 0 ? i : brush.getMoveSpots().size + i));
                while (true) {
                    if (i != 0) {
                        i = i > 0 ? i : brush.getMoveSpots().size + i;
                        netDistance += distance(brush.getMoveSpots().get(i), brush.getMoveSpots().get(i - 1));
                        if (netDistance > brush.getTotalDistance()) {
                            break;
                        }
                        pointCounter++;
                        shapeRenderer.rectLine(brush.getMoveSpots().get(i), brush.getMoveSpots().get(i - 1), LINEWIDTH);
                    } else {
                        netDistance += distance(brush.getMoveSpots().get(0), brush.getMoveSpots().get(brush.getMoveSpots().size - 1));
                        if (netDistance > brush.getTotalDistance()) {
                            break;
                        }
                        pointCounter++;
                        shapeRenderer.rectLine(brush.getMoveSpots().get(0), brush.getMoveSpots().get(brush.getMoveSpots().size - 1), LINEWIDTH);
                    }
                    i--;
                }
                if (brush.getMoveTracker() - pointCounter > 1) {
                    shapeRenderer.rectLine(brush.getMoveSpots().get(brush.getMoveTracker() - pointCounter - 1), pointBetween(brush.getMoveSpots().get(brush.getMoveTracker() - pointCounter - 1), brush.getMoveSpots().get(brush.getMoveTracker() - pointCounter - 2), (netDistance - brush.getTotalDistance()) > distance(brush.getMoveSpots().get(brush.getMoveTracker() - pointCounter - 1), brush.getMoveSpots().get(brush.getMoveTracker() - pointCounter - 2)) ? (float) distance(brush.getMoveSpots().get(brush.getMoveTracker() - pointCounter - 1), brush.getMoveSpots().get(brush.getMoveTracker() - pointCounter - 2)) : (netDistance - brush.getTotalDistance())), LINEWIDTH);
                } else if (brush.getMoveTracker() - pointCounter == 1) {
                    shapeRenderer.rectLine(brush.getMoveSpots().get(0), pointBetween(brush.getMoveSpots().get(0), brush.getMoveSpots().get(brush.getMoveSpots().size - 1), (netDistance - brush.getTotalDistance()) > distance(brush.getMoveSpots().get(0), brush.getMoveSpots().get(brush.getMoveSpots().size - 1)) ? (float) distance(brush.getMoveSpots().get(0), brush.getMoveSpots().get(brush.getMoveSpots().size - 1)) : (netDistance - brush.getTotalDistance())), LINEWIDTH);
                } else {
                    shapeRenderer.rectLine(brush.getMoveSpots().get(brush.getMoveSpots().size + brush.getMoveTracker() - pointCounter - 1), pointBetween(brush.getMoveSpots().get(brush.getMoveSpots().size + brush.getMoveTracker() - pointCounter - 1), brush.getMoveSpots().get(brush.getMoveSpots().size + brush.getMoveTracker() - pointCounter - 2), (netDistance - brush.getTotalDistance()) > distance(brush.getMoveSpots().get(brush.getMoveSpots().size + brush.getMoveTracker() - pointCounter - 1), brush.getMoveSpots().get(brush.getMoveSpots().size + brush.getMoveTracker() - pointCounter - 2)) ? (float) distance(brush.getMoveSpots().get(brush.getMoveSpots().size + brush.getMoveTracker() - pointCounter - 1), brush.getMoveSpots().get(brush.getMoveSpots().size + brush.getMoveTracker() - pointCounter - 2)) : (netDistance - brush.getTotalDistance())), LINEWIDTH);
                }
            }
            shapeRenderer.end();
            sb.begin();
            for (BonusQuokka bonusQuokka : bonusQuokkas) {
                if (!collectedQuokkas.get(bonusQuokkas.indexOf(bonusQuokka, false))) {
                    sb.draw(bonusQuokka.getTexture(), bonusQuokka.getPosQuokka().x, bonusQuokka.getPosQuokka().y);
                }
            }
            for (Obstacle portal : portals) {
                sb.draw(portalAnimation.getFrame(), portal.getPosObstacle().x, portal.getPosObstacle().y);
            }
            for (MoveWall moveWall : moveWalls) {
                sb.draw(moveWall.getTexture(), moveWall.getPosWall().x, moveWall.getPosWall().y);
            }
            for (EvilCloud cloud : clouds) {
                sb.draw(cloud.getTexture(), cloud.getPosCloud().x, cloud.getPosCloud().y);
            }
            for (Obstacle planet : planets) {
                sb.draw(planet.getTexture(), planet.getPosObstacle().x, planet.getPosObstacle().y);
            }
            for (Obstacle blackHole : blackHoles) {
                sb.draw(blackHole.getTexture(), blackHole.getPosObstacle().x, blackHole.getPosObstacle().y);
            }
        /*for(Vine vine : vines){
            sb.draw(vine.getTexture(), vine.getPosVine().x, vine.getPosVine().y);
        }*/
        for (TallDino tallDino : tallDinos) {
            sb.draw(tallDino.getTexture(), tallDino.getPosTallDino().x, tallDino.getPosTallDino().y);
        }
            for (Hawk hawk : hawks) {
                sb.draw(hawk.getTexture(), hawk.getPosHawk().x + (hawk.getFirstWidth() - hawk.getTexture().getWidth()) / 2, hawk.getPosHawk().y);
            }
            for (Meteor meteor : meteors) {
                sb.draw(meteor.getTexture(), meteor.getPosMeteor().x, meteor.getPosMeteor().y);
                if (meteor.getPosMeteor().y > 768) {
                    sb.draw(warningTexture, meteor.getPosMeteor().x + meteor.getMeteorBounds().getWidth() / 2 - 60, 660);
                }
            }
            for (Arrow arrow : arrows) {
                sb.draw(arrow.getTexture(), arrow.getPosArrow().x, arrow.getPosArrow().y);
            }
            /*for (Airplane airplane : airplanes) {
                sb.draw(airplane.getTexture(), airplane.getPosAirplane().x, airplane.getPosAirplane().y);
                if (airplane.getPosAirplane().y > 768) {
                    sb.draw(warningTexture, airplane.getPosAirplane().x, 700);
                } else if (airplane.getPosAirplane().y + airplane.getTexture().getHeight() < 0) {
                    sb.draw(warningTexture, airplane.getPosAirplane().x, 10);
                }
            }
            for (Airplane airplane : tropicBirds) {
                sb.draw(airplane.getTexture(), airplane.getPosAirplane().x, airplane.getPosAirplane().y);
                if (airplane.getPosAirplane().y > 768) {
                    sb.draw(warningTexture, airplane.getPosAirplane().x, 700);
                } else if (airplane.getPosAirplane().y + airplane.getTexture().getHeight() < 0) {
                    sb.draw(warningTexture, airplane.getPosAirplane().x, 10);
                }
            }
            for (Airplane airplane : tropicFish) {
                sb.draw(airplane.getTexture(), airplane.getPosAirplane().x, airplane.getPosAirplane().y);
                if (airplane.getPosAirplane().y > 768) {
                    sb.draw(warningTexture, airplane.getPosAirplane().x, 700);
                } else if (airplane.getPosAirplane().y + airplane.getTexture().getHeight() < 0) {
                    sb.draw(warningTexture, airplane.getPosAirplane().x, 10);
                }
            }*/
            for (JumpFish jumpFish : jumpFishes) {
                sb.draw(jumpFish.getJumpFishRegion(), jumpFish.getPosJumpFish().x, jumpFish.getPosJumpFish().y, jumpFish.getJumpFishRegion().getRegionWidth() / 2, jumpFish.getJumpFishRegion().getRegionHeight() / 2, jumpFish.getJumpFishRegion().getRegionWidth(), jumpFish.getJumpFishRegion().getRegionHeight(), 1, 1, jumpFish.getJumpFishAngle());
            }
            for (LaserGun laserGun : laserGuns) {
                sb.draw(laserGun.getGunRegion(), laserGun.getPosGun().x, laserGun.getPosGun().y, laserGun.getGunRegion().getRegionWidth() / 2, laserGun.getGunRegion().getRegionHeight() / 2, laserGun.getGunRegion().getRegionWidth(), laserGun.getGunRegion().getRegionHeight(), 1, 1, laserGun.getGunAngle());
                if (laserGun.isDrawLaser()) {
                    sb.draw(laserGun.getMyBeam().getBeamRegion(), laserGun.getMyBeam().getPosBeam().x, laserGun.getMyBeam().getPosBeam().y, laserGun.getMyBeam().getBeamRegion().getRegionWidth() / 2, laserGun.getMyBeam().getBeamRegion().getRegionHeight() / 2, laserGun.getMyBeam().getBeamRegion().getRegionWidth(), laserGun.getMyBeam().getBeamRegion().getRegionHeight(), 1, 1, laserGun.getBeamAngle());
                }
            }
            for (Drone drone : drones) {
                sb.draw(drone.getDroneRegion(), drone.getPosDrone().x, drone.getPosDrone().y, drone.getDroneRegion().getRegionWidth() / 2, drone.getDroneRegion().getRegionHeight() / 2, drone.getDroneRegion().getRegionWidth(), drone.getDroneRegion().getRegionHeight(), 1, 1, drone.getDroneAngle());
            }
            sb.draw(quokka.getTexture(), quokka.getPosition().x - 7, quokka.getPosition().y);
            sb.draw(backButton.getTexture(), backButton.getPosButton().x, backButton.getPosButton().y);
            sb.draw(pauseButton.getTexture(), pauseButton.getPosButton().x, pauseButton.getPosButton().y);
            sb.end();
            sb.setColor(1f, 1f, 1f, 1f);
        /*shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.YELLOW);
            for(Hawk hawk : hawks) {
                shapeRenderer.polygon(hawk.getHawkPolygon().getTransformedVertices());
            }
        for(Meteor meteor : meteors){
            shapeRenderer.circle(meteor.getMeteorCircle().x, meteor.getMeteorCircle().y, meteor.getMeteorCircle().radius);
        }
        for(Obstacle portal : portals){
            shapeRenderer.circle(portal.getObstacleCircle().x, portal.getObstacleCircle().y, portal.getObstacleCircle().radius);
        }
        for(LaserGun laserGun : laserGuns){
            shapeRenderer.polygon(laserGun.getMyBeam().getPolygon().getTransformedVertices());
        }
        for(EvilCloud evilCloud : clouds){
            shapeRenderer.polygon(evilCloud.getCloudPoly().getTransformedVertices());
        }
        shapeRenderer.polygon(happyCloud.getCloudPoly().getTransformedVertices());
        shapeRenderer.end();*/
    }

    @Override
    public void dispose() {//disposes of art assets after completing each level to reduce memory usage
        levelBackground.dispose();
        quokka.dispose();
        warningTexture.dispose();
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
        if(portals.size > 0 && !resetting) {
            for (Texture frame : portalAnimation.getFrames()) {
                frame.dispose();
            }
        }
        backButton.dispose();
        pauseButton.dispose();
        shapeRenderer.dispose();
    }

    private void levelInit(int world, int level){//initializes the level in the given world, sets the background and adds all of the correct obstacles to their obstacle array at the proper coordinates
        TIMEADJUST = ((world == 4) ? 0 : 3.5f);
        if(world == 1) {
            if(level < 11) {
                walls.add(new Wall(-1000, -220));
                walls.add(new Wall(-1000, 375));
            }
            else{
                walls.add(new Wall(-945, -220));
                walls.add(new Wall(-945, 375));
            }
        }
        else if(world == 2){
            walls.add(new Wall(-1000, -220, "stump.png"));
            walls.add(new Wall(-1000, 375, "stump.png"));
        }
        else if(world == 4){
            walls.add(new Wall(-1000, -220, "asteroidBelt.png"));
            walls.add(new Wall(-1000, 375, "asteroidBelt.png"));
        }
        else if(world == 5 && level != 7){
            walls.add(new Wall(-1000, -220, "futureWall.png"));
            walls.add(new Wall(-1000, 375, "futureWall.png"));
        }
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
                        bonusQuokkas.add(new BonusQuokka(650, 20));
                        happyCloud = new HappyCloud(600, 300);
                        break;
                    /*case 1:
                        levelBackground = new Texture("level2Background.png");
                        hawks.add(new Hawk(400,150));
                        happyCloud = new HappyCloud(850, 300);
                        break;*/
                    /*case 1:
                        levelBackground = new Texture("level2Background.png");
                        happyCloud = new HappyCloud(30000, 500);
                        walls.add(new Wall(-50, 300));
                        walls.add(new Wall(73, 600, "horizontWall.png"));
                        break;*/
                    case 2:
                        levelBackground = new Texture("level2Background.png");
                        walls.add(new Wall(500,-180, "wall.png"));
                        happyCloud = new HappyCloud(1510, 200);
                        bonusQuokkas.add(new BonusQuokka(20,200));
                        break;
                    case 3:
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
                    case 4:
                        levelBackground = new Texture("level1Background.png");
                        walls.add(new Wall(350, 340, "wall.png"));
                        bonusQuokkas.add(new BonusQuokka(550, 500));
                        walls.add(new Wall(750,340, "wall.png"));
                        happyCloud = new HappyCloud(950, 375);
                        break;
                    case 5:
                        levelBackground = new Texture("level2Background.png");
                        clouds.add(new EvilCloud(350, 400));
                        bonusQuokkas.add(new BonusQuokka(420, 570));
                        happyCloud = new HappyCloud(950, 425);
                        break;
                    case 6:
                        levelBackground = new Texture("level2Background.png");
                        walls.add(new Wall(350, -80, "wall.png"));
                        walls.add(new Wall(850, -230, "wall.png"));
                        clouds.add(new EvilCloud(1200, 460));
                        bonusQuokkas.add(new BonusQuokka(650, 50));
                        happyCloud = new HappyCloud(1200, 10);
                        break;
                    case 7:
                        levelBackground = new Texture("level1Background.png");
                        clouds.add(new EvilCloud(200, 300));
                        walls.add(new Wall(900, 450));
                        clouds.add(new EvilCloud(1420, 270));
                        bonusQuokkas.add(new BonusQuokka(1825, 120));
                        happyCloud = new HappyCloud(1730,350);
                        break;
                    case 8:
                        levelBackground = new Texture("level1Background.png");
                        walls.add(new Wall(300,500, "wall.png"));
                        walls.add(new Wall(300, -380, "wall.png"));
                        walls.add(new Wall (600, -380, "wall.png"));
                        bonusQuokkas.add(new BonusQuokka(600, 550));
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
                    case 9:
                        levelBackground = new Texture("level1Background.png");
                        clouds.add(new EvilCloud(250, 200));
                        bonusQuokkas.add(new BonusQuokka(650, 10));
                        walls.add(new Wall(900, -180));
                        clouds.add(new EvilCloud(1050, 300));
                        walls.add(new Wall(1500, 400));
                        happyCloud = new HappyCloud(1700, 500);
                        break;
                    case 10:
                        levelBackground = new Texture("level2Background.png");
                        clouds.add(new EvilCloud(200, 50));
                        walls.add(new Wall (750, -130));
                        switches.add(new Obstacle(530, 20, "wallSwitch.png"));
                        walls.add(new Wall(750, 465, switches, 800));
                        walls.add(new Wall(1150, 350));
                        bonusQuokkas.add(new BonusQuokka(1320, 500));
                        happyCloud = new HappyCloud(1550, 500);
                        break;
                    case 11:
                        levelBackground = new Texture("level2Background.png");
                        walls.add(new Wall(-822,377, "horizontWall.png"));
                        hawks.add(new Hawk(500, 200, 600, 300, 500, 2, 200, 500, 17, 0, 1, 0, 600, 25));
                        walls.add(new Wall(1300, -200));
                        walls.add(new Wall(1300, 395));
                        switches.add(new Obstacle(1150, 460, "wallSwitch.png"));
                        walls.add(new Wall(-350, 500, switches.get(0), 800));
                        walls.add(new Wall(2350, -220));
                        walls.add(new Wall(2350, 375));
                        happyCloud = new HappyCloud(-660, 550);
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
                        walls.add(new Wall(-350, 350, "stump.png"));
                        meteors.add(new Meteor(150, 780, 0, 0));
                        walls.add(new Wall(500, 450, "stump.png"));
                        meteors.add(new Meteor(740, 780, 0, 0));
                        clouds.add(new EvilCloud(955, 50));
                        bonusQuokkas.add(new BonusQuokka(1255, 50));
                        happyCloud = new HappyCloud(1605, 50);
                        break;
                    case 2:
                        levelBackground = new Texture("dino2Back.png");
                        walls.add(new Wall(450, -100, "horizontStump.png"));
                        walls.add(new Wall(450, 380, "stump.png"));
                        switches.add(new Obstacle(590, 120, "stumpSwitch.png"));
                        bonusQuokkas.add(new BonusQuokka(750, -30));
                        walls.add(new Wall(450, -445, switches, -215, "stump.png"));
                        meteors.add(new Meteor(690, 780, 0, 0));
                        walls.add(new Wall(922, -80, "stump.png"));
                        happyCloud = new HappyCloud(1222, 50);
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
                        levelBackground = new Texture("dino1Back.png");
                        meteors.add(new Meteor(500, 780, 0, 0));
                        meteors.add(new Meteor(675, 780, 0, 0));
                        meteors.add(new Meteor(850, 780, 0, 0));
                        meteors.add(new Meteor(1025, 780, 0, 0));
                        meteors.add(new Meteor(1200, 780, 0, 0));
                        walls.add(new Wall (1375, -150, "stump.png"));
                        meteors.add(new Meteor(1550, 780, 0, 0));
                        meteors.add(new Meteor(1725, 780, 0, 0));
                        meteors.add(new Meteor(1900, 780, 0, 0));
                        meteors.add(new Meteor(2075, 780, 0, 0));
                        meteors.add(new Meteor(2250, 780, 0, 0));
                        walls.add(new Wall(2425, -150, "stump.png"));
                        happyCloud = new HappyCloud(2600, 200);
                        meteors.add(new Meteor(2775, 780, 0, 0));
                        meteors.add(new Meteor(2950, 780, 0, 0));
                        meteors.add(new Meteor(3125, 780, 0, 0));
                        bonusQuokkas.add(new BonusQuokka(3300, 50));
                        break;
                    case 5:
                        levelBackground = new Texture("dino1Back.png");
                        walls.add(new Wall(330, -200, "stump.png"));
                        meteors.add(new Meteor(600, 880, 0, 0));
                        tallDinos.add(new TallDino(600, -150, 1000, 200));
                        bonusQuokkas.add(new BonusQuokka(630, 50));
                        happyCloud = new HappyCloud(1350, 50);
                        break;
                    case 6:
                        levelBackground = new Texture("dino1Back.png");
                        clouds.add(new EvilCloud(150, 50));
                        meteors.add(new Meteor(450, 780, 0, 0));
                        bonusQuokkas.add(new BonusQuokka(500, 450));
                        walls.add(new Wall(650, 350, "stump.png"));
                        tallDinos.add(new TallDino(450, -250, 1200, 250));
                        happyCloud = new HappyCloud(1450, 50);
                        tallDinos.add(new TallDino(1750, -250, 2000, 250));
                        break;
                    case 7:
                        levelBackground = new Texture("dino1Back.png");
                        walls.add(new Wall(200, -100, "stump.png"));
                        tallDinos.add(new TallDino(350, -100, 1350, 300));
                        walls.add(new Wall(575, 601, "stump.png"));
                        meteors.add(new Meteor(820, 780, 0, 0));
                        clouds.add(new EvilCloud(1070, 550));
                        bonusQuokkas.add(new BonusQuokka(1420, 550));
                        happyCloud = new HappyCloud(1600, 100);
                        break;
                    case 8:
                        levelBackground = new Texture("dino2Back.png");
                        switches.add(new Obstacle(400, 50,"stumpSwitch.png"));
                        bonusQuokkas.add(new BonusQuokka(525, 120));
                        walls.add(new Wall(200, 350, "horizontStump.png"));
                        walls.add(new Wall(672, 472, switches, 1000, "stump.png"));
                        tallDinos.add(new TallDino(250, -255, 822, 200));
                        meteors.add(new Meteor(915, 780, 0, 0));
                        happyCloud = new HappyCloud(1200, 150);
                        break;
                    case 9:
                        levelBackground = new Texture("dino2Back.png");
                        walls.add(new Wall(350, 500, "stump.png"));
                        tallDinos.add(new TallDino(923, -195, -223, 400));
                        bonusQuokkas.add(new BonusQuokka(0, 50));
                        walls.add(new Wall(1000, 500, "stump.png"));
                        tallDinos.add(new TallDino(1573, -195, 427, 400));
                        happyCloud = new HappyCloud(1000, 0);
                        break;
                    case 10:
                        levelBackground = new Texture("dino2Back.png");
                        meteors.add(new Meteor(400, 880, 0, 0));
                        meteors.add(new Meteor(600, 880, 0, 0));
                        meteors.add(new Meteor(800, 880, 0, 0));
                        meteors.add(new Meteor(1000, 880, 0, 0));
                        tallDinos.add(new TallDino(200, -200, 400, 150));
                        tallDinos.add(new TallDino(432, -200, 632, 150));
                        tallDinos.add(new TallDino(1064, -200, 864, 150));
                        tallDinos.add(new TallDino(1296, -200, 1096, 150));
                        meteors.add(new Meteor(1644, 880, 0, 0));
                        tallDinos.add(new TallDino(1528, -100, 1960, 150));
                        bonusQuokkas.add(new BonusQuokka(1878, 80));
                        happyCloud = new HappyCloud(2192, 150);
                        break;
                    case 11:
                        levelBackground = new Texture("dino2Back.png");
                        walls.add(new Wall(350, 400, "stump.png"));
                        tallDinos.add(new TallDino(-450, -200, 1100, 200));
                        hawks.add(new Hawk(1150, 150, 5000, 100, 500, 2, 200, 475, 15, 375, 1,0, 550, 15));
                        walls.add(new Wall(2085, 400, "stump.png"));
                        tallDinos.add(new TallDino(2900, -200, 1350, 200));
                        happyCloud = new HappyCloud(2400, 500);
                        break;
                }
                break;
            case 3:
                switch(level){
                    case 1:
                        clouds.add(new EvilCloud(175,800));
                        walls.add(new Wall(700, 1250, "horizontWall.png"));
                        bonusQuokkas.add(new BonusQuokka(1000, 1550));
                        happyCloud = new HappyCloud(300, 1650);
                        break;
                    case 2:
                        walls.add(new Wall(675, 450, "horizontWall.png"));
                        walls.add(new Wall(93, 1000, "horizontWall.png"));
                        walls.add(new Wall(565, 1123));
                        switches.add(new Obstacle(798, 1868, "wallSwitch.png"));
                        walls.add(new Wall(93, 1718, switches, -718, 0,"horizontWall.png"));
                        walls.add(new Wall(675, 2200, "horizontWall.png"));
                        walls.add(new Wall(552, 2200, switches, -400, 1));
                        bonusQuokkas.add(new BonusQuokka(376, 1500));
                        happyCloud = new HappyCloud(820, 2373);
                        break;
                    case 3:
                        arrows.add(new Arrow(50, 850));
                        arrows.add(new Arrow(50, 1250));
                        bonusQuokkas.add(new BonusQuokka(300, 1250));
                        clouds.add(new EvilCloud(250, 1500));
                        happyCloud = new HappyCloud(700,1800);
                        break;
                    case 4:
                        walls.add(new Wall(-50, 850, "horizontWall.png"));
                        arrows.add(new Arrow (cam.viewportWidth- 100, 1250));
                        arrows.add(new Arrow(50, 1650));
                        arrows.add(new Arrow(cam.viewportWidth- 100, 2050));
                        bonusQuokkas.add(new BonusQuokka(200, 1250));
                        happyCloud = new HappyCloud(700, 2250);
                        break;
                    case 5:
                        arrows.add(new Arrow(0, 1325));
                        arrows.add(new Arrow(0, 300));
                        walls.add(new Wall(-100, 1300, "horizontWall.png"));
                        walls.add(new Wall(800, 1100));
                        bonusQuokkas.add(new BonusQuokka(1000, 1110));
                        walls.add(new Wall(923, 1300, "horizontWall.png"));
                        clouds.add(new EvilCloud(250, 1617));
                        happyCloud = new HappyCloud(940, 1485);
                        break;
                    case 6:
                        for(int i = 0; i < 3; i++) {
                            walls.add(new Wall(327, 595 * i));
                            walls.add(new Wall(796, 595 * i));
                        }
                        arrows.add(new Arrow(70, 750));
                        bonusQuokkas.add(new BonusQuokka(560, 800));
                        arrows.add(new Arrow(70, 950));
                        arrows.add(new Arrow(1050, 950));
                        arrows.add(new Arrow(1050, 1150));
                        happyCloud = new HappyCloud(490, 1300);
                        break;
                    case 7:
                        walls.add(new Wall(-342, 875, "horizontWall.png"));
                        walls.add(new Wall(253, 875, "horizontWall.png"));
                        arrows.add(new Arrow(-50, 900));
                        walls.add(new Wall(848, 875));
                        arrows.add(new Arrow(1180, 1270));
                        walls.add(new Wall(490, 1665));
                        bonusQuokkas.add(new BonusQuokka(210, 1240));
                        happyCloud = new HappyCloud(120, 1845);
                        break;
                    case 8:
                        clouds.add(new EvilCloud(500, 1000));
                        walls.add(new Wall(595, 1200));
                        walls.add(new Wall(595, 1795));
                        walls.add(new Wall(718, 2267, "horizontWall.png"));
                        clouds.add(new EvilCloud(750, 1323));
                        portals.add(new Obstacle(900, 2067, "portal.png"));
                        bonusQuokkas.add(new BonusQuokka(920, 1750));
                        walls.add(new Wall(595, 2267));
                        walls.add(new Wall(595, 2862));
                        walls.add(new Wall(0, 2850, "horizontWall.png"));
                        walls.add(new Wall(718, 3334, "horizontWall.png"));
                        portals.add(new Obstacle(400, 2067, "portal.png"));
                        portals.add(new Obstacle(400, 2610, "portal.png"));
                        portals.add(new Obstacle(720, 2610, "portal.png"));
                        happyCloud = new HappyCloud(850, 2860);
                        break;
                    case 9:
                        arrows.add(new Arrow(50, 1300));
                        clouds.add(new EvilCloud(950, 1000));
                        clouds.add(new EvilCloud(93, 1450));
                        walls.add(new Wall(946, 1450,"horizontWall.png"));
                        walls.add(new Wall(404, 1850, "horizontWall.png"));
                        walls.add(new Wall(946, 1450));
                        portals.add(new Obstacle(108, 1600, "portal.png"));
                        walls.add(new Wall(93, 1850, "horizontWall.png"));
                        portals.add(new Obstacle(1085, 1600, "portal.png"));
                        arrows.add(new Arrow(50, 2330));
                        arrows.add(new Arrow(1230, 2420));
                        bonusQuokkas.add(new BonusQuokka(150,2420));
                        walls.add(new Wall(93, 2670, "horizontWall.png"));
                        happyCloud = new HappyCloud(223, 2843);
                        break;
                    case 10:
                        walls.add(new Wall(678, 0));
                        walls.add(new Wall(678, 595));
                        walls.add(new Wall(93, 1067, "horizontWall.png"));
                        portals.add(new Obstacle(120, 870, "portal.png"));
                        portals.add(new Obstacle(817, 870, "portal.png"));
                        clouds.add(new EvilCloud(828, 600));
                        arrows.add(new Arrow(1150, 700));
                        arrows.add(new Arrow(1150, 1350));
                        arrows.add(new Arrow(cam.viewportWidth-100, 1650));
                        walls.add(new Wall(678, 1190));
                        walls.add(new Wall(93, 1662, "horizontWall.png"));
                        portals.add(new Obstacle(130, 2000, "portal.png"));
                        bonusQuokkas.add(new BonusQuokka(130, 2324));
                        portals.add(new Obstacle(900, 2180, "portal.png"));
                        clouds.add(new EvilCloud(130, 2150));
                        happyCloud = new HappyCloud(780, 2420);
                        break;
                    case 11:
                        switches.add(new Obstacle(250, 1200, "wallSwitch.png"));
                        //walls.add(new Wall(-200, 1150, "horizontWall.png"));
                        hawks.add(new Hawk(125, 150, 5000, 300, 650, 2, 200, 400, 17, 550, 1, 0));
                        walls.add(new Wall(-200, 1700, "horizontWall.png"));
                        walls.add(new Wall(395, 1700, switches, 1300, 0, "horizontWall.png"));
                        walls.add(new Wall(688, 1700, switches, 1300, 0, "horizontWall.png"));
                        happyCloud = new HappyCloud(120, 1843);
                        break;
                }
                break;
            case 4:
                levelBackground = new Texture("spaceBackground.png");
                switch(level) {
                    case 1:
                        planets.add(new Obstacle(300, 200, "greenPlanet.png"));
                        planets.add(new Obstacle(900, 400, "greenPlanet.png"));
                        happyCloud = new HappyCloud(1500, 100);
                        planets.add(new Obstacle(1700, 300, "greenPlanet.png"));
                        bonusQuokkas.add(new BonusQuokka(2000, 350));
                        break;
                    /*case 1:
                        planets.add(new Obstacle(400, 300, "greenPlanet.png"));
                        laserGuns.add(new LaserGun(600, 300));
                        happyCloud = new HappyCloud(30000, 300);
                        break;*/
                    case 2:
                        switches.add(new Obstacle(300, 0, "asteroidSwitch.png"));
                        planets.add(new Obstacle(200, 300, "greenPlanet.png"));
                        walls.add(new Wall(500, 380, switches, 1000, "asteroidBelt.png"));
                        walls.add(new Wall(500, -215, "asteroidBelt.png"));
                        bonusQuokkas.add(new BonusQuokka(700, 70));
                        planets.add(new Obstacle(800, 50, "greenPlanet.png"));
                        happyCloud = new HappyCloud(1500, 300);
                        planets.add(new Obstacle(1800, 500, "greenPlanet.png"));
                        break;
                    /*case 3:
                        levelBackground = new Texture("spaceBackground.png");
                        planets.add(new Obstacle(200, 300, "greenPlanet.png"));
                        planets.add(new Obstacle(600, 500, "blackHole.png"));
                        happyCloud = new HappyCloud(50, 50);
                        break;*/
                    case 3:
                        nebulae.add(new Obstacle(250, 300, "nebula.png"));
                        bonusQuokkas.add(new BonusQuokka(750, 550));
                        blackHoles.add(new Obstacle(800, 300, "blackHole.png"));
                        planets.add(new Obstacle(1650, 30, "greenPlanet.png"));
                        planets.add(new Obstacle(1650, 520, "greenPlanet.png"));
                        happyCloud = new HappyCloud(2050, 250);
                        break;
                    case 4:
                        nebulae.add(new Obstacle(300, 350, "nebula.png"));
                        clouds.add(new EvilCloud(500, 50));
                        planets.add(new Obstacle(1200, 20, "greenPlanet.png"));
                        blackHoles.add(new Obstacle(1200, 500, "blackHole.png"));
                        nebulae.add(new Obstacle(1750, 300, "nebula.png"));
                        happyCloud = new HappyCloud(2300, 350);
                        bonusQuokkas.add(new BonusQuokka(950, 70));
                        planets.add(new Obstacle(2700, 500, "greenPlanet.png"));
                        planets.add(new Obstacle(2700, 30, "greenPlanet.png"));
                        nebulae.add(new Obstacle(2950, 400, "nebula.png"));
                        nebulae.add(new Obstacle(2950, 100, "nebula.png"));
                        break;
                    case 5:
                        blackHoles.add(new Obstacle(300, 500, "blackHole.png"));
                        blackHoles.add(new Obstacle(725, 30, "blackHole.png"));
                        nebulae.add(new Obstacle(1150, 320, "nebula.png"));
                        planets.add(new Obstacle(2000, 300, "greenPlanet.png"));
                        planets.add(new Obstacle(2575, 300, "greenPlanet.png"));
                        bonusQuokkas.add(new BonusQuokka(2300, 500));
                        nebulae.add(new Obstacle(2875, 50, "nebula.png"));
                        happyCloud = new HappyCloud(3275, 50);
                        nebulae.add(new Obstacle(3625, 50, "nebula.png"));
                        planets.add(new Obstacle(3775, 300, "greenPlanet.png"));
                        nebulae.add(new Obstacle(3775, 50, "nebula.png"));
                        nebulae.add(new Obstacle(3975, 350, "nebula.png"));
                        break;
                    case 6:
                        clouds.add(new EvilCloud(300, 20));
                        clouds.add(new EvilCloud(600, 550));
                        clouds.add(new EvilCloud(900, 30));
                        clouds.add(new EvilCloud(1200, 343));
                        bonusQuokkas.add(new BonusQuokka(1275, 60));
                        clouds.add(new EvilCloud(1500, 30));
                        clouds.add(new EvilCloud(1800, 560));
                        nebulae.add(new Obstacle(2100, 256, "nebula.png"));
                        happyCloud = new HappyCloud(2450, 308);
                        break;
                    case 7:
                        planets.add(new Obstacle(350, 400, "greenPlanet.png"));
                        drones.add(new Drone(750, 550));
                        planets.add(new Obstacle(1000, 30, "greenPlanet.png"));
                        bonusQuokkas.add(new BonusQuokka(1300, 55));
                        planets.add(new Obstacle(1550, 350, "greenPlanet.png"));
                        planets.add(new Obstacle(2150, 500, "greenPlanet.png"));
                        nebulae.add(new Obstacle(2750, 150, "nebula.png"));
                        happyCloud = new HappyCloud(3100, 350);
                        planets.add(new Obstacle(3400, 500, "greenPlanet.png"));
                        planets.add(new Obstacle(3400, 30, "greenPlanet.png"));
                        break;
                    case 8:
                        nebulae.add(new Obstacle(350, 500, "nebula.png"));
                        planets.add(new Obstacle(800, 300, "greenPlanet.png"));
                        laserGuns.add(new LaserGun(820, 540));
                        blackHoles.add(new Obstacle(1320, 500, "blackHole.png"));
                        nebulae.add(new Obstacle(1920, 250, "nebula.png"));
                        nebulae.add(new Obstacle(1320, 30, "nebula.png"));
                        bonusQuokkas.add(new BonusQuokka(1690, 560));
                        happyCloud = new HappyCloud(2220, 300);
                        planets.add(new Obstacle(2490, 500, "greenPlanet.png"));
                        planets.add(new Obstacle(2490, 30, "greenPlanet.png"));
                        break;
                    case 9:
                        nebulae.add(new Obstacle(250, 300, "nebula.png"));
                        switches.add(new Obstacle(900, 500, "asteroidSwitch.png"));
                        nebulae.add(new Obstacle(850, 125, "nebula.png"));
                        walls.add(new Wall(1200, -195, switches.get(0), 400, "asteroidBelt.png"));
                        walls.add(new Wall(1200, 400, "asteroidBelt.png"));
                        nebulae.add(new Obstacle(1600, 280, "nebula.png"));
                        bonusQuokkas.add(new BonusQuokka(2050, 500));
                        blackHoles.add(new Obstacle(2200, 450, "blackHole.png"));
                        nebulae.add(new Obstacle(2200, 50, "nebula.png"));
                        planets.add(new Obstacle(2800, 50, "greenPlanet.png"));
                        walls.add(new Wall(3100, -300, "asteroidBelt.png"));
                        nebulae.add(new Obstacle(3000, 300, "nebula.png"));
                        happyCloud = new HappyCloud(3250, 50);
                        blackHoles.add(new Obstacle(3600, 500, "blackHole.png"));
                        blackHoles.add(new Obstacle(3600, 50, "blackHole.png"));
                        break;
                    case 10:
                        planets.add(new Obstacle(350, 250, "greenPlanet.png"));
                        portals.add(new Obstacle(375, 500, "portal.png"));
                        planets.add(new Obstacle(900, 50, "greenPlanet.png"));
                        switches.add(new Obstacle(3480, 10, "asteroidSwitch.png"));
                        walls.add(new Wall(1350, -361, "asteroidBelt.png"));
                        walls.add(new Wall(1350, 234, switches.get(0), 834, "asteroidBelt.png"));
                        planets.add(new Obstacle(1750, 350, "greenPlanet.png"));
                        switches.add(new Obstacle(2150, 20, "asteroidSwitch.png"));
                        walls.add(new Wall(2400, 384, "asteroidBelt.png"));
                        walls.add(new Wall(2400, -211, "asteroidBelt.png"));
                        planets.add(new Obstacle(2650, 400, "greenPlanet.png"));
                        happyCloud = new HappyCloud(2630, 25);
                        nebulae.add(new Obstacle(2980, 5, "nebula.png"));
                        bonusQuokkas.add(new BonusQuokka(3030, 400));
                        walls.add(new Wall(3280, -211, switches.get(1), 384, "asteroidBelt.png"));
                        walls.add(new Wall(3280, 384, "asteroidBelt.png"));
                        portals.add(new Obstacle(3955, 500, "portal.png"));
                        planets.add(new Obstacle(3930, 250, "greenPlanet.png"));
                        planets.add(new Obstacle(4580, 250, "greenPlanet.png"));
                        walls.add(new Wall(5073, -220, "asteroidBelt.png"));
                        walls.add(new Wall(5073, 375, "asteroidBelt.png"));
                        break;
                    case 11:
                        planets.add(new Obstacle(200, 500, "greenPlanet.png"));
                        planets.add(new Obstacle(200, 30, "greenPlanet.png"));
                        hawks.add(new Hawk(950, 550, 600, 300, 400, 2, 270, 500, 10, 0, -1, 0, 600, 20));
                        blackHoles.add(new Obstacle(900, 250, "blackHole.png"));
                        planets.add(new Obstacle(1700, 500, "greenPlanet.png"));
                        planets.add(new Obstacle(1700, 30, "greenPlanet.png"));
                        planets.add(new Obstacle(2400, 500, "greenPlanet.png"));
                        planets.add(new Obstacle(2400, 30, "greenPlanet.png"));
                        //planets.add(new Obstacle(2570, 280, "greenPlanet.png"));
                        happyCloud = new HappyCloud(2200, 350);
                        break;
                }
                break;
            case 5:
                levelBackground = new Texture("futureBackground.png");
                switch(level){
                    case 1:
                        walls.add(new Wall(-100,380, "futureWall.png"));
                        walls.add(new Wall(-100, -215, "futureWall.png"));
                        portals.add(new Obstacle(25, 50, "portal.png"));
                        walls.add(new Wall(200,380, "futureWall.png"));
                        walls.add(new Wall(200, -215, "futureWall.png"));
                        portals.add(new Obstacle(450, 600, "portal.png"));
                        clouds.add(new EvilCloud(600, 100));
                        bonusQuokkas.add(new BonusQuokka(450, 20));
                        clouds.add(new EvilCloud(850, 600));
                        happyCloud = new HappyCloud(1200, 550);
                        break;
                    case 2:
                        walls.add(new Wall(200, -50, "futureWall.png"));
                        laserGuns.add(new LaserGun(350, 50));
                        clouds.add(new EvilCloud(670, 350));
                        bonusQuokkas.add(new BonusQuokka(810, 110));
                        walls.add(new Wall(1195, 350, "futureWall.png"));
                        walls.add(new Wall(1595, -220, "futureWall.png"));
                        happyCloud = new HappyCloud(1745, 50);
                        break;
                    case 3:
                        walls.add(new Wall(200, -130, "futureWall.png"));
                        bonusQuokkas.add(new BonusQuokka(600, 200));
                        laserGuns.add(new LaserGun(950, 550));
                        laserGuns.add(new LaserGun(1250, 0));
                        walls.add(new Wall(1750,300, "futureWall.png"));
                        happyCloud = new HappyCloud(2200, 500);
                        break;
                    case 4:
                        drones.add(new Drone(400, 600));
                        walls.add(new Wall(652, 350, "futureWall.png"));
                        walls.add(new Wall(950, -150, "futureWall.png"));
                        drones.add(new Drone(1300, 50));
                        clouds.add(new EvilCloud(1500, 250));
                        bonusQuokkas.add(new BonusQuokka(1550, 20));
                        clouds.add(new EvilCloud(1750, 250));
                        happyCloud = new HappyCloud(1950, 20);
                        break;
                    case 5:
                        drones.add(new Drone(-250, 450));
                        clouds.add(new EvilCloud(250, 500));
                        portals.add(new Obstacle(500, 250, "portal.png"));
                        clouds.add(new EvilCloud(450, 0));
                        bonusQuokkas.add(new BonusQuokka(770, 250));
                        portals.add(new Obstacle(600, 550, "portal.png"));
                        walls.add(new Wall(1020, -95, "futureWall.png"));
                        drones.add(new Drone(1170, 50));
                        clouds.add(new EvilCloud(1470, 550));
                        clouds.add(new EvilCloud(1720, 150));
                        happyCloud = new HappyCloud(2190, 0);
                        break;
                    case 6:
                        /*walls.add(new Wall(150, 350, "futureWall.png"));
                        portals.add(new Obstacle(300, 600, "portal.png"));
                        walls.add(new Wall(475, -175, "futureWall.png"));
                        switches.add(new Obstacle(625, 0, "wallSwitch.png"));
                        walls.add(new Wall(475, 420, switches, 900, "futureWall.png"));
                        walls.add(new Wall(598, 420, "horizontFutureWall.png"));
                        portals.add(new Obstacle(1015, 239, "portal.png"));
                        walls.add(new Wall(1193, -52, "futureWall.png"));
                        walls.add(new Wall(1518, 375, "futureWall.png"));
                        bonusQuokkas.add(new BonusQuokka(2100, 580));
                        clouds.add(new EvilCloud(2050, 380));
                        walls.add(new Wall(2300, 250, "futureWall.png"));
                        //hawks.add(new Hawk(300, 250));*/
                        walls.add(new Wall(236, 437, "horizontFutureWall.png"));
                        walls.add(new Wall(831, 437, "horizontFutureWall.png"));
                        portals.add(new Obstacle(600, 550, "portal.png"));
                        switches.add(new Obstacle(4000, 100, "futureSwitch.png"));
                        walls.add(new Wall(770,560, switches, 900, "futureWall.png"));
                        walls.add(new Wall(770, -158, "futureWall.png"));
                        portals.add(new Obstacle(4725, 550, "portal.png"));
                        walls.add(new Wall(3500, -220, "futureWall.png"));
                        walls.add(new Wall(3500,375, "futureWall.png"));
                        walls.add(new Wall(4200, 437, "horizontFutureWall.png"));
                        walls.add(new Wall(4795, 437, "horizontFutureWall.png"));
                        walls.add(new Wall(6090, -220, "futureWall.png"));
                        walls.add(new Wall(6090,375, "futureWall.png"));
                        clouds.add(new EvilCloud(4930, 291));
                        bonusQuokkas.add(new BonusQuokka(5270, 275));
                        clouds.add(new EvilCloud(4200, 0));
                        walls.add(new Wall(4596, 560, switches, 900, "futureWall.png"));
                        happyCloud = new HappyCloud(1000, 150);
                        break;
                    case 7:
                        walls.add(new Wall(0, 427, "horizontFutureWall.png"));
                        walls.add(new Wall(-595, 427, "horizontFutureWall.png"));
                        portals.add(new Obstacle(781, 600, "portal.png"));
                        portals.add(new Obstacle(-500, 266, "portal.png"));
                        portals.add(new Obstacle(331, 266, "portal.png"));
                        walls.add(new Wall(-718, -220, "futureWall.png"));
                        walls.add(new Wall(-718, 375, "futureWall.png"));
                        walls.add(new Wall(595, -45, "futureWall.png"));
                        clouds.add(new EvilCloud(751, 350));
                        clouds.add(new EvilCloud(1250, 400));
                        bonusQuokkas.add(new BonusQuokka(1100, 400));
                        portals.add(new Obstacle(1050, 175, "portal.png"));
                        portals.add(new Obstacle(1200, 600, "portal.png"));
                        clouds.add(new EvilCloud(1850, 595));
                        walls.add(new Wall(2100, 0, "futureWall.png"));
                        portals.add(new Obstacle(2243, 350, "portal.png"));
                        happyCloud = new HappyCloud(2483, 100);
                        break;
                    case 8:
                        walls.add(new Wall(660, 280, "futureWall.png"));
                        laserGuns.add(new LaserGun(450, 500));
                        portals.add(new Obstacle(800, 600, "portal.png"));
                        walls.add(new Wall(990, -211, "futureWall.png"));
                        walls.add(new Wall(990, 384, "futureWall.png"));
                        portals.add(new Obstacle(1140, 50, "portal.png"));
                        drones.add(new Drone(2100, 650));
                        walls.add(new Wall(2050, -100, "futureWall.png"));
                        bonusQuokkas.add(new BonusQuokka(2250, 50));
                        clouds.add(new EvilCloud(2700, 550));
                        walls.add(new Wall(3200, -50, "futureWall.png"));
                        happyCloud = new HappyCloud(3400, 50);
                        break;
                    case 9:
                        drones.add(new Drone(150, 10));
                        walls.add(new Wall(350, -175, "futureWall.png"));
                        portals.add(new Obstacle(473, 50, "portal.png"));
                        walls.add(new Wall(637, 270, "futureWall.png"));
                        walls.add(new Wall(760, 270, "horizontFutureWall.png"));
                        walls.add(new Wall(1355, 270, "horizontFutureWall.png"));
                        walls.add(new Wall(637, -325, "futureWall.png"));
                        laserGuns.add(new LaserGun(1362, 20));
                        portals.add(new Obstacle(760, 400, "portal.png"));
                        portals.add(new Obstacle(1705, 400, "portal.png"));
                        portals.add(new Obstacle(2078, 400, "portal.png"));
                        walls.add(new Wall(1950, 270, "futureWall.png"));
                        bonusQuokkas.add(new BonusQuokka(2341, 145));
                        laserGuns.add(new LaserGun(2741, 150));
                        happyCloud = new HappyCloud(2991, 400);
                        break;
                    case 10:
                        laserGuns.add(new LaserGun(-450, 600));
                        laserGuns.add(new LaserGun(-450, 100));
                        walls.add(new Wall(300, 575, "futureWall.png"));
                        clouds.add(new EvilCloud(550, 30));
                        walls.add(new Wall(950, 375, "futureWall.png"));
                        laserGuns.add(new LaserGun(1100, 600));
                        clouds.add(new EvilCloud(1250, 50));
                        bonusQuokkas.add(new BonusQuokka(1550, 50));
                        walls.add(new Wall(1700, -150, "futureWall.png"));
                        walls.add(new Wall(2100, 300, "futureWall.png"));
                        happyCloud = new HappyCloud(2300, 500);
                        break;
                    case 11:
                        walls.add(new Wall(400, -200, "futureWall.png"));
                        laserGuns.add(new LaserGun(1350, 500));
                        laserGuns.add(new LaserGun(1350, 30));
                        portals.add(new Obstacle(2150, 350, "portal.png"));
                        walls.add(new Wall(2320, -220, "futureWall.png"));
                        walls.add(new Wall(2320,375, "futureWall.png"));
                        walls.add(new Wall(3871, -220, "futureWall.png"));
                        walls.add(new Wall(3871,375, "futureWall.png"));
                        portals.add(new Obstacle(4000, 350, "portal.png"));
                        switches.add(new Obstacle(4330, 275, "futureSwitch.png"));
                        walls.add(new Wall(4150, 610, switches.get(0), 225, "futureWall.png"));
                        walls.add(new Wall(4150, -370, "futureWall.png"));
                        hawks.add(new Hawk(5300, 500, 5000, 200, 475, 3, 200, 750, 15, 415, -1,0, 450, 20));
                        walls.add(new Wall(6050, -125, "futureWall.png"));
                        switches.add(new Obstacle(7380, 275, "futureSwitch.png"));
                        walls.add(new Wall(7200, 610, switches.get(1), 225, "futureWall.png"));
                        walls.add(new Wall(7200, -370, "futureWall.png"));
                        walls.add(new Wall(8950, -125, "futureWall.png"));
                        walls.add(new Wall(9650, 300, "futureWall.png"));
                        happyCloud = new HappyCloud(10200, 450);
                        break;
                }
                break;
            /*case 7:
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
                        break;
                    case 1:
                        levelBackground = new Texture("level2Background.png");
                        hawks.add(new Hawk(400,150));
                        happyCloud = new HappyCloud(850, 300);
                        break;

                }
                break;*/
        }
        if(world == 3){
            int i;
            if(level == 11){
                for (i = -220; i < happyCloud.getPosCloud().y + happyCloud.getTexture().getHeight() + 595; i += 595) {
                    walls.add(new Wall(-30, i));
                    walls.add(new Wall(cam.viewportWidth - 10, i));
                }
                walls.add(new Wall(93, i - 500, "horizontWall.png"));
                walls.add(new Wall(688, i - 500, "horizontWall.png"));
            }
            else {
                for (i = -220; i < happyCloud.getPosCloud().y + happyCloud.getTexture().getHeight(); i += 595) {
                    walls.add(new Wall(-30, i));
                    walls.add(new Wall(cam.viewportWidth - 10, i));
                }
                walls.add(new Wall(93, i - 123, "horizontWall.png"));
                walls.add(new Wall(688, i - 123, "horizontWall.png"));
            }
        }
        else {
            if(world == 1) {
                if(level < 11) {
                    walls.add(new Wall(happyCloud.getPosCloud().x + 1000, -220));
                    walls.add(new Wall(happyCloud.getPosCloud().x + 1000, 375));
                }
                else{
                    //checkPoint
                }
            }
            else if(world == 2){
                walls.add(new Wall(happyCloud.getPosCloud().x + 1000, -220, "stump.png"));
                walls.add(new Wall(happyCloud.getPosCloud().x + 1000, 375, "stump.png"));
            }
            else if(world == 4){
                if(level != 10) {
                    walls.add(new Wall(happyCloud.getPosCloud().x + 1000, -220, "asteroidBelt.png"));
                    walls.add(new Wall(happyCloud.getPosCloud().x + 1000, 375, "asteroidBelt.png"));
                }
                for (int i = -877; i < happyCloud.getPosCloud().x + 2000; i += 595) {
                    walls.add(new Wall(i, -100, "horizontAsteroidBelt.png"));
                    walls.add(new Wall(i, cam.viewportHeight - 10, "horizontAsteroidBelt.png"));
                }
            }
            else{
                walls.add(new Wall(happyCloud.getPosCloud().x + 1000, -220, "futureWall.png"));
                walls.add(new Wall(happyCloud.getPosCloud().x + 1000, 375, "futureWall.png"));
            }
        }
        if(portals.size > 0 && !setPortal){
            portalAnimation = new Animation("portalFrames", "Portal_Final00", 100, 1.5f);
        }
        /*if(blackHoles.size > 0){
            //blackHoleAnimation = new Animation("blackHoleFrames", "Blackhole_Final00", 100, 0.5f);
        }*/
        collectedQuokkas.setSize(bonusQuokkas.size);
    }

    private void updateBackground(){//moves the backgrounds appropriately so that the quokka never goes off of the background in either the x or y directions
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

    private void updateBackgroundPortal(){ //moves the backgrounds to the new position when the quokka touches a portal to prevent it from briefly flashing red
        while((cam.position.x +(cam.viewportWidth / 2) < levelBackgroundPos1.x + levelBackground.getWidth())&&(cam.position.x + (cam.viewportWidth / 2) < levelBackgroundPos2.x + levelBackground.getWidth())) {
            levelBackgroundPos2.sub(levelBackground.getWidth() * 2, 0);
            levelBackgroundPos4.sub(levelBackground.getWidth() * 2, 0);
            levelBackgroundPos1.sub(levelBackground.getWidth() * 2, 0);
            levelBackgroundPos3.sub(levelBackground.getWidth() * 2, 0);
        }
        while(cam.position.x - (cam.viewportWidth / 2) > levelBackgroundPos1.x + levelBackground.getWidth()) {
            levelBackgroundPos1.add(levelBackground.getWidth() * 2, 0);
            levelBackgroundPos3.add(levelBackground.getWidth() * 2, 0);
        }
        while(cam.position.x - (cam.viewportWidth / 2) > levelBackgroundPos2.x + levelBackground.getWidth()){
            levelBackgroundPos2.add(levelBackground.getWidth() * 2, 0);
            levelBackgroundPos4.add(levelBackground.getWidth() * 2, 0);
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

    private Vector3 resultVector(Vector3 velocity, Vector3 point1, Vector3 point2) { //finds the quokka's new velocity when it collides with the line between point1 and point2 at the given velocity
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
                planetDistance.set(Math.abs(quokka.getQuokkaBounds().x + quokka.getTexture().getWidth() / 2 - planet.getPosObstacle().x - planet.getTexture().getWidth() / 2), Math.abs(quokka.getQuokkaBounds().y + quokka.getTexture().getHeight() / 2 - planet.getPosObstacle().y - planet.getTexture().getHeight() / 2), 0);
                double planetMagnitude = MAGSCALER * Math.sqrt(Math.pow(planetDistance.x, 2) + Math.pow(planetDistance.y, 2));
                currentPot += GOODGRAV / Math.pow(planetMagnitude, GRAVPOW - 1);
            }
        }
        else{
            currentPot = -1 * quokka.getGravity().y * quokka.getQuokkaBounds().y;
        }
        //coachLandmark
        velocityTemp2.scl((float) Math.sqrt(Math.abs(2.0*(iniPot - currentPot))));
        velocityTemp2.scl((float) (1.0/Math.sqrt(currentDT)));
        return velocityTemp2;
    }
    private Vector3 resultVector(Vector3 velocity, Vector2 point1, Vector2 point2) {//same as above but using 2d vectors instead of 3d vectors when useful
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
                planetDistance.set(Math.abs(quokka.getQuokkaBounds().x + quokka.getTexture().getWidth() / 2 - planet.getPosObstacle().x - planet.getTexture().getWidth() / 2), Math.abs(quokka.getQuokkaBounds().y + quokka.getTexture().getHeight() / 2 - planet.getPosObstacle().y - planet.getTexture().getHeight() / 2), 0);
                double planetMagnitude = MAGSCALER * Math.sqrt(Math.pow(planetDistance.x, 2) + Math.pow(planetDistance.y, 2));
                currentPot += GOODGRAV / Math.pow(planetMagnitude, GRAVPOW - 1);
            }
        }
        else{
            currentPot = -1 * quokka.getGravity().y * quokka.getQuokkaBounds().y;
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
    private boolean isCollision(Polygon p, Rectangle r) { //checks if a given convex polygon and rectangle overlap
        Polygon rPoly = new Polygon(new float[] { 0, 0, r.width, 0, r.width, r.height, 0, r.height });
        rPoly.setPosition(r.x, r.y);
        return Intersector.overlapConvexPolygons(rPoly, p);
    }


    private boolean isConcaveCollision(Polygon p, Rectangle r) { //checks if a given concave polygon and rectangle overlap //needs fixing
        Polygon rPoly = new Polygon(new float[] { 0, 0, r.width, 0, r.width, r.height, 0, r.height });
        rPoly.setPosition(r.x, r.y);
        for(int i = 0; i < rPoly.getTransformedVertices().length; i+=2){
            if(p.contains(rPoly.getTransformedVertices()[i],rPoly.getTransformedVertices()[i+1])){
                return true;
            }
        }
        for(int i = 0; i < p.getTransformedVertices().length; i += 2){
            if(r.contains(p.getTransformedVertices()[i],p.getTransformedVertices()[i+1])){
                return true;
            }
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
    public boolean touchDown(int screenX, int screenY, int pointer, int button) { //places the first point at the correct coordinates on the screen when the mouse/finger is pressed down
        if(!respawning) {
            touchInput.set(screenX, screenY, 0);
            cam.unproject(touchInput);
            justPaused = false;
            if (backButton.getButtonBounds().contains(touchInput.x, touchInput.y)) {
                gsm.set(new MenuState(gsm, world, level, false));
            } else if (pauseButton.getButtonBounds().contains(touchInput.x, touchInput.y)) {
                paused = !paused;
                if(paused) {
                    pauseButton.dispose();
                    pauseButton.setTexture(new Texture("pauseYellow.png"));
                }
                else{
                    pauseButton.dispose();
                    pauseButton.setTexture(new Texture("pause.png"));
                }
                justPaused = true;
            } else {
                vineDraw = true;
                vineCheck = true;
                lineDraw = true;
                //shouldFall = true;
                hasCollided = false;
                lineCheck = false;
                paused = false;
                pauseButton.dispose();
                pauseButton.setTexture(new Texture("pause.png"));
                clickPos2.set(screenX, -100, 0);
                clickPos.set(screenX, screenY, 0);
                clickPos.set(cam.unproject(clickPos));
                if (lineDraw) {
                    clickPosTemp.set(screenX, screenY, 0);
                    clickPosTemp.set(cam.unproject(clickPosTemp));
                }
            }
            clickedWhileSpawning = false;
        }
        else{
            clickedWhileSpawning = true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {//places the second point at the correct coordinates on the screen and creates the line when the mouse/finger is released
        if(!paused && !justPaused && !clickedWhileSpawning && !respawning) {
            if (lineDraw) {
                clickPos2.set(screenX, screenY, 0);
                clickPos2.set(cam.unproject(clickPos2));
                clickPosNo.set(clickPos.x, clickPos.y);
                clickPosNo2.set(clickPos2.x, clickPos2.y);
                //justTouchUp = true;
                //justTouchedUp = true;
                if(quokkaLineHit()) {
                    lineAdjustTouchUp();
                }
                clickPosTemp.set(0, -100, 0);
                vineDraw = true;
                shouldFall = true;
                shouldMove = true;
                lineCheck = true;
            }
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) { //draws a demo line showing where the line will end up when the mouse/finger is moved while down
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
