package com.google.vr.sdk.samples.treasurehunt;

public interface AudibleGvrObject {
    void initializeAndPlayAudio();
    void pauseAudio();
    void resumeAudio();
    void updateAudioPosition(GvrHeadData headData);
}
