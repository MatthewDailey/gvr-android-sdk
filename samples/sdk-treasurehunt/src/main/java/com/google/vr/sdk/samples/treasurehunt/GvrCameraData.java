package com.google.vr.sdk.samples.treasurehunt;

import android.opengl.Matrix;

import com.google.vr.sdk.base.HeadTransform;

public class GvrCameraData {

    private static final float CAMERA_Z = 0.01f;

    public final float[] camera = new float[16];
    public final float[] headView = new float[16];
    public final float[] headRotation = new float[4];

    public void updateFromHeadTransform(HeadTransform headTransform) {

        // Build the camera matrix and apply it to the ModelView.
        Matrix.setLookAtM(camera, 0, 0.0f, 0.0f, CAMERA_Z, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);

        headTransform.getHeadView(headView, 0);

        // Update the 3d audio engine with the most recent head rotation.
        headTransform.getQuaternion(headRotation, 0);
    }
}
