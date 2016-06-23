package com.google.vr.sdk.samples.treasurehunt;

import android.opengl.GLES20;
import android.util.Log;

public class GLErrorUtils {

    /**
     * Checks if we've had an error inside of OpenGL ES, and if so what that error is.
     *
     * @param label Label to report in case of error.
     */
    public static void checkGLError(String label) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("GvrTestApp - GLError", label + ": glError " + error);
            throw new RuntimeException(label + ": glError " + error);
        }
    }
}
