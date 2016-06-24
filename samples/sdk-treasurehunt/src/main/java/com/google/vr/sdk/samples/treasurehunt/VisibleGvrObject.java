package com.google.vr.sdk.samples.treasurehunt;

public interface VisibleGvrObject {
    void onSurfaceCreated();
    void onNewFrame();
    void draw(GvrEyeData eyeData, GvrHeadData headData);
}
