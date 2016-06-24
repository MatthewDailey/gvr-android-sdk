package com.google.vr.sdk.samples.treasurehunt;

public interface VisibleGvrObject {
    void onSurfaceCreated();
    void draw(float[] lightPosInEyeSpace,
              float[] view,
              float[] perspective);
}
