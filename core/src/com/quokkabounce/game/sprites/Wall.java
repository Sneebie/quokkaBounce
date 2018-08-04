package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Eric on 9/2/2017.
 */

public class Wall{
    private Texture wallTexture;
    private Vector2 posWall, bl, br, ul, ur;
    private Rectangle wallBounds;
    private Array<Obstacle> wallSwitches;
    private float wallMove;
    private boolean hasSwitch, moveWall;
    private int dir;

    public Wall(float x, float y, String textureString){
        setTexture(textureString);

        posWall = new Vector2(x, y);
        hasSwitch = false;
        wallBounds = new Rectangle(posWall.x, posWall.y, wallTexture.getWidth(), wallTexture.getHeight());
        moveWall = false;
        bl = new Vector2(x,y);
        br = new Vector2(x + getWallBounds().getWidth(), y);
        ul = new Vector2(x, y + getWallBounds().getHeight());
        ur = new Vector2(x + getWallBounds().getWidth(), y + getWallBounds().getHeight());
    }

    public Wall(float x, float y){
        setTexture("wall.png");

        posWall = new Vector2(x, y);
        hasSwitch = false;
        wallBounds = new Rectangle(posWall.x, posWall.y, wallTexture.getWidth(), wallTexture.getHeight());
        moveWall = false;
        bl = new Vector2(x,y);
        br = new Vector2(x + getWallBounds().getWidth(), y);
        ul = new Vector2(x, y + getWallBounds().getHeight());
        ur = new Vector2(x + getWallBounds().getWidth(), y + getWallBounds().getHeight());
    }

    public Wall(float x, float y, Array<Obstacle> wallSwitches, float wallMove, int dir){
        setTexture("wall.png");
        this.dir = dir;
        posWall = new Vector2(x, y);
        hasSwitch = true;
        this.wallMove = wallMove;
        this.wallSwitches = wallSwitches;
        wallBounds = new Rectangle(posWall.x, posWall.y, wallTexture.getWidth(), wallTexture.getHeight());
        moveWall = false;
        bl = new Vector2(x,y);
        br = new Vector2(x + getWallBounds().getWidth(), y);
        ul = new Vector2(x, y + getWallBounds().getHeight());
        ur = new Vector2(x + getWallBounds().getWidth(), y + getWallBounds().getHeight());
    }

    public Wall(float x, float y, Array<Obstacle> wallSwitches, float wallMove, int dir, String textureString){
        setTexture(textureString);
        this.dir = dir;
        posWall = new Vector2(x, y);
        hasSwitch = true;
        this.wallMove = wallMove;
        this.wallSwitches = wallSwitches;
        wallBounds = new Rectangle(posWall.x, posWall.y, wallTexture.getWidth(), wallTexture.getHeight());
        moveWall = false;
        bl = new Vector2(x,y);
        br = new Vector2(x + getWallBounds().getWidth(), y);
        ul = new Vector2(x, y + getWallBounds().getHeight());
        ur = new Vector2(x + getWallBounds().getWidth(), y + getWallBounds().getHeight());
    }

    public Wall(float x, float y, Array<Obstacle> wallSwitches, float wallMove){
        setTexture("wall.png");
        this.dir = 1;
        posWall = new Vector2(x, y);
        hasSwitch = true;
        this.wallMove = wallMove;
        this.wallSwitches = wallSwitches;
        wallBounds = new Rectangle(posWall.x, posWall.y, wallTexture.getWidth(), wallTexture.getHeight());
        moveWall = false;
        bl = new Vector2(x,y);
        br = new Vector2(x + getWallBounds().getWidth(), y);
        ul = new Vector2(x, y + getWallBounds().getHeight());
        ur = new Vector2(x + getWallBounds().getWidth(), y + getWallBounds().getHeight());
    }

    public Wall(float x, float y, Array<Obstacle> wallSwitches, float wallMove, String textureString){
        setTexture(textureString);
        this.dir = 1;
        posWall = new Vector2(x, y);
        hasSwitch = true;
        this.wallMove = wallMove;
        this.wallSwitches = wallSwitches;
        wallBounds = new Rectangle(posWall.x, posWall.y, wallTexture.getWidth(), wallTexture.getHeight());
        moveWall = false;
        bl = new Vector2(x,y);
        br = new Vector2(x + getWallBounds().getWidth(), y);
        ul = new Vector2(x, y + getWallBounds().getHeight());
        ur = new Vector2(x + getWallBounds().getWidth(), y + getWallBounds().getHeight());
    }
    public Wall(float x, float y, Obstacle wallSwitch, float wallMove){
        setTexture("wall.png");
        this.dir = 1;
        posWall = new Vector2(x, y);
        hasSwitch = true;
        this.wallMove = wallMove;
        wallSwitches = new Array<Obstacle>();
        wallSwitches.add(wallSwitch);
        wallBounds = new Rectangle(posWall.x, posWall.y, wallTexture.getWidth(), wallTexture.getHeight());
        moveWall = false;
        bl = new Vector2(x,y);
        br = new Vector2(x + getWallBounds().getWidth(), y);
        ul = new Vector2(x, y + getWallBounds().getHeight());
        ur = new Vector2(x + getWallBounds().getWidth(), y + getWallBounds().getHeight());
    }
    public Wall(float x, float y, Obstacle wallSwitch, float wallMove, String textureString){
        setTexture(textureString);
        this.dir = 1;
        posWall = new Vector2(x, y);
        hasSwitch = true;
        this.wallMove = wallMove;
        wallSwitches = new Array<Obstacle>();
        wallSwitches.add(wallSwitch);
        wallBounds = new Rectangle(posWall.x, posWall.y, wallTexture.getWidth(), wallTexture.getHeight());
        moveWall = false;
        bl = new Vector2(x,y);
        br = new Vector2(x + getWallBounds().getWidth(), y);
        ul = new Vector2(x, y + getWallBounds().getHeight());
        ur = new Vector2(x + getWallBounds().getWidth(), y + getWallBounds().getHeight());
    }

    public int getDir() {
        return dir;
    }

    public float getWallMove() {
        return wallMove;
    }

    public boolean hasSwitch() {
        return hasSwitch;
    }

    public boolean isMoveWall() {
        return moveWall;
    }

    public void setMoveWall(boolean moveWall) {
        this.moveWall = moveWall;
    }

    public void setWallBounds(float x, float y, float width, float height) {
        wallBounds.set(x, y, width, height);
    }

    public void setPosWall(float x, float y) {
        posWall.set(x, y);
        bl.set(x,y);
        br.set(x + getWallBounds().getWidth(), y);
        ul.set(x, y + getWallBounds().getHeight());
        ur.set(x + getWallBounds().getWidth(), y + getWallBounds().getHeight());
    }

    public Vector2 getBl() {
        return bl;
    }

    public Vector2 getBr() {
        return br;
    }

    public Vector2 getUl() {
        return ul;
    }

    public Vector2 getUr() {
        return ur;
    }

    public Vector2 getPosWall() {
        return posWall;
    }

    public Texture getTexture() {
        return wallTexture;
    }

    public Array<Obstacle> getWallSwitches() {
        return wallSwitches;
    }

    public boolean collides(Rectangle player) {
        return player.overlaps(wallBounds);
    }

    public void dispose() {
        wallTexture.dispose();
    }

    public void setTexture(String textureString) {
        wallTexture = new Texture(textureString);
    }

    public Rectangle getWallBounds() {
        return wallBounds;
    }
}
