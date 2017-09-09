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
    private int frameChange;

    public Animation(String folderName, String fileName, int frameCount, float cycleTime){
        frames = new Array<Texture>();
        frameChange = 1;
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
        System.out.println(frame);
        if(currentFrameTime > maxFrameTime){
            frame += frameChange;
            currentFrameTime = 0;
        }
        if(frame >= frameCount - 1){
            frameChange = -1;
        }
        if(frame <= 0){
            frameChange = 1;
        }
    }

    public Texture getFrame(){
        return frames.get(frame);
    }
}
