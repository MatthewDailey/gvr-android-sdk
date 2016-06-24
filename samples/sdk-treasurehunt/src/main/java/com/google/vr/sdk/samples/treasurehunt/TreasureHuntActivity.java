/*
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.vr.sdk.samples.treasurehunt;

import android.content.Context;
import android.opengl.GLES20;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.GvrActivity;
import com.google.vr.sdk.base.GvrView;
import com.google.vr.sdk.base.HeadTransform;
import com.google.vr.sdk.base.Viewport;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * A Google VR sample application.
 * </p><p>
 * The TreasureHunt scene consists of a planar ground grid and a floating
 * "treasure" cube. When the user looks at the cube, the cube will turn gold.
 * While gold, the user can activate the Carboard trigger, which will in turn
 * randomly reposition the cube.
 */
public class TreasureHuntActivity extends GvrActivity implements GvrView.StereoRenderer {

    private static final String TAG = "TreasureHuntActivity";

    public static final int COORDS_PER_VERTEX = 3;

    private List<AudibleGvrObject> audibleGvrObjects = new ArrayList<>();
    private List<VisibleGvrObject> visibleGvrObjects = new ArrayList<>();
    private List<CardboardTriggerListener> cardboardTriggerListeners = new ArrayList<>();

    private GvrHeadData reusedHeadData;
    private GvrEyeData reusedEyeData;

    private Vibrator vibrator;

    /**
     * Sets the view to our GvrView and initializes the transformation matrices we will use
     * to render our scene.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        audibleGvrObjects.clear();
        visibleGvrObjects.clear();
        cardboardTriggerListeners.clear();

        initializeGvrView();

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        reusedHeadData = new GvrHeadData();
        reusedEyeData = new GvrEyeData();

        final TreasureHuntCube cube = new TreasureHuntCube(this);

        audibleGvrObjects.add(cube);
        visibleGvrObjects.add(cube);
        cardboardTriggerListeners.add(new CardboardTriggerListener() {
            @Override
            public void onCardboardTrigger() {
                if (cube.isLookingAtFrom(reusedHeadData.headView)) {
                    cube.hide();
                }
            }
        });

        visibleGvrObjects.add(new TreasureHuntFloor(this));
    }

    public void initializeGvrView() {
        setContentView(R.layout.common_ui);

        GvrView gvrView = (GvrView) findViewById(R.id.gvr_view);
        gvrView.setEGLConfigChooser(8, 8, 8, 8, 16, 8);

        gvrView.setRenderer(this);
        gvrView.setTransitionViewEnabled(true);
        gvrView.setOnCardboardBackButtonListener(
                new Runnable() {
                    @Override
                    public void run() {
                        onBackPressed();
                    }
                });
        setGvrView(gvrView);
    }

    @Override
    public void onPause() {
        super.onPause();
        for(AudibleGvrObject audibleGvrObject : audibleGvrObjects) {
            audibleGvrObject.pauseAudio();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        for(AudibleGvrObject audibleGvrObject : audibleGvrObjects) {
            audibleGvrObject.resumeAudio();
        }
    }

    @Override
    public void onRendererShutdown() {
        Log.i(TAG, "onRendererShutdown");
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        Log.i(TAG, "onSurfaceChanged");
    }

    /**
     * Creates the buffers we use to store information about the 3D world.
     * <p/>
     * <p>OpenGL doesn't use Java arrays, but rather needs data in a format it can understand.
     * Hence we use ByteBuffers.
     *
     * @param config The EGL configuration used when creating the surface.
     */
    @Override
    public void onSurfaceCreated(EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated");
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.5f); // Dark background so text shows up well.

        for(AudibleGvrObject audibleGvrObject : audibleGvrObjects) {
            audibleGvrObject.initializeAndPlayAudio();
        }

        for (VisibleGvrObject visibleGvrObject : visibleGvrObjects) {
            visibleGvrObject.onSurfaceCreated();
        }

        GLErrorUtils.checkGLError("onSurfaceCreated");
    }

    /**
     * Prepares OpenGL ES before we draw a frame.
     *
     * @param headTransform The head transformation in the new frame.
     */
    @Override
    public void onNewFrame(HeadTransform headTransform) {
        reusedHeadData.updateFromHeadTransform(headTransform);

        for (AudibleGvrObject audibleGvrObject : audibleGvrObjects) {
            audibleGvrObject.updateAudioPosition(reusedHeadData);
        }

        for (VisibleGvrObject visibleGvrObject : visibleGvrObjects) {
            visibleGvrObject.onNewFrame();
        }

        GLErrorUtils.checkGLError("onReadyToDraw");
    }

    /**
     * Draws a frame for an eye.
     *
     * @param eye The eye to render. Includes all required transformations.
     */
    @Override
    public void onDrawEye(Eye eye) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLErrorUtils.checkGLError("colorParam");

        reusedEyeData.updateFromEye(eye, reusedHeadData);

        for (VisibleGvrObject visibleGvrObject : visibleGvrObjects) {
            visibleGvrObject.draw(reusedEyeData, reusedHeadData);
        }
    }

    @Override
    public void onFinishFrame(Viewport viewport) {
    }

    /**
     * Called when the Cardboard trigger is pulled.
     */
    @Override
    public void onCardboardTrigger() {
        Log.i(TAG, "onCardboardTrigger");

        for (CardboardTriggerListener cardboardTriggerListener : cardboardTriggerListeners) {
            cardboardTriggerListener.onCardboardTrigger();
        }

        // Always give user feedback.
        vibrator.vibrate(50);
    }

}
