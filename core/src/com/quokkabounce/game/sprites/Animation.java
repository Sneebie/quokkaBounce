package com.quokkabounce.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Eric on 9/9/2017.
 */

public class Animation {
    private Array<Texture> frames;
    private float maxFrameTime;
    private float currentFrameTime;
    private int frameCount;
    private int frame;

    public Animation(String folderName, String fileName, int frameCount, float cycleTime){
        frames = new Array<Texture>();
        for(int i = 1; i <= frameCount; i++){
            frames.add(new Texture(folderName + "/" + fileName + Integer.toString(i) + ".png"));
        }
        this.frameCount = frameCount;
        maxFrameTime = cycleTime / frameCount;
        frame = 0;
    }

    public Array<Texture> getFrames() {
        return frames;
    }

    public void update(float dt){
        currentFrameTime += dt;
        if(currentFrameTime > maxFrameTime){
            frame++;
            currentFrameTime = 0;
        }
        if(frame >= frameCount){
            frame = 0;
        }
    }

    public Texture getFrame(){
        return frames.get(frame);
    }
}
