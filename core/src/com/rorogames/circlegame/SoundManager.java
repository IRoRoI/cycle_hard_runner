package com.rorogames.circlegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {
    public int SOUND_CLICK = 0;
    public int SOUND_COMPLETE = 1;
    public int SOUND_ROCKET = 2;
    private Sound[] sounds = {
            Gdx.audio.newSound(Gdx.files.internal("music/clickSound.wav")),
            Gdx.audio.newSound(Gdx.files.internal("music/completeSound.mp3")),
            Gdx.audio.newSound(Gdx.files.internal("music/rocketSound.mp3"))
    };
    public void play(int sound)
    {
        if(CircleConstructor.main.activeSounds)
        {
            sounds[sound].play();
        }
    }
}
