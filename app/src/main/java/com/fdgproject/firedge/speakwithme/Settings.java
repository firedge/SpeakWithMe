package com.fdgproject.firedge.speakwithme;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by Firedge on 10/01/2015.
 */
public class Settings implements Serializable{
    private boolean sound;
    private Locale language;
    private float speed;
    private float voice;

    public Settings(boolean sound, Locale language, float voice, float speed) {
        this.sound = sound;
        this.language = language;
        this.speed = speed;
        this.voice = voice;
    }

    public boolean isSound() {
        return sound;
    }

    public void setSound(boolean sound) {
        this.sound = sound;
    }

    public Locale getLanguage() {
        return language;
    }

    public void setLanguage(Locale language) {
        this.language = language;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getVoice() {
        return voice;
    }

    public void setVoice(float voice) {
        this.voice = voice;
    }
}
