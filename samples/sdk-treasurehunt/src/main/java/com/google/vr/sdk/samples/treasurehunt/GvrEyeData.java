package com.google.vr.sdk.samples.treasurehunt;

import android.opengl.Matrix;

import com.google.vr.sdk.base.Eye;

import java.util.Arrays;

public class GvrEyeData {
    // We keep the light always position just above the user.
    private static final float[] LIGHT_POS_IN_WORLD_SPACE = new float[]{0.0f, 2.0f, 0.0f, 1.0f};

    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 100.0f;

    public final float[] lightPosInEyeSpace = new float[4];
    public final float[] view = new float[16];
    public float[] perspective = new float[16];

    public void updateFromEye(Eye eye, GvrCameraData cameraData) {
        // Apply the eye transformation to the camera.
        Matrix.multiplyMM(view, 0, eye.getEyeView(), 0, cameraData.camera, 0);

        // Set the position of the light
        Matrix.multiplyMV(lightPosInEyeSpace, 0, view, 0, LIGHT_POS_IN_WORLD_SPACE, 0);

        // Build the ModelView and ModelViewProjection matrices
        // for calculating cube position and light.
        perspective = eye.getPerspective(Z_NEAR, Z_FAR);
    }
}
