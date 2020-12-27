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
    private boolean hawkAnimation;

    public Animation(String folderName, String fileName, int frameCount, float cycleTime){
        frames = new Array<Texture>();
        if(folderName == "hawkIdle"){
            hawkAnimation = true;
        }else{
            hawkAnimation = false;
        }
        frameChange = 1;
        for(int i = 0; i < frameCount; i++){
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
            frame += frameChange;
            currentFrameTime = 0;
        }
        if(frame >= frameCount - 1){
            if(hawkAnimation) {
                if (frameChange != -1) {
                    maxFrameTime = maxFrameTime / 3;
                }
                frameChange = -1;
            }
            else{
                frame = 0;
            }
        }
        if(frame <= 0){
            if(frameChange!= 1) {
                maxFrameTime = maxFrameTime * 3;
            }
            frameChange = 1;
            frame = 0;
        }
    }

    public Texture getFrame(){
        return frames.get(frame);
    }

    public int getFrameNumber() {
        return frame;
    }
}
