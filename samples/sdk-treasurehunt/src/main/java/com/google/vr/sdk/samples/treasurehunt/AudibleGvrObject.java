package com.google.vr.sdk.samples.treasurehunt;

public interface AudibleGvrObject {
    void pauseAudio();
    void startAudio();
    void updateAudioPosition(float[] headRotation);
}
