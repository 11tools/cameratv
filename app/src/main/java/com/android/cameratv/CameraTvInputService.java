/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.cameratv;

import android.content.Context;
import android.media.tv.TvInputService;
import android.net.Uri;
import android.view.Surface;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.util.Log;
import android.os.Looper;



import java.util.ArrayList;
import java.util.List;

/** SampleTvInputService */
public class CameraTvInputService extends TvInputService {
    public static final String INPUT_ID =
            "com.android.cameratv/.CameraTvInputService";

    private BaseTvInputSessionImpl mBaseTvInputSessionImpl;
    private static final String TAG = "CameraTvInputService";
    public BaseTvInputSessionImpl onCreateSession(String s) {
        Log.d(TAG, " onCreateSession s = " + s);
		mBaseTvInputSessionImpl = new BaseTvInputSessionImpl(this);
        return mBaseTvInputSessionImpl;
    }
	
	
	

    class BaseTvInputSessionImpl extends Session implements CameraOps.ErrorDisplayer, CameraOps.CameraReadyListener {
		
		
		private Surface mSurface = null;
		//private MediaPlayer mMediaPlayer = null;
		private Handler mUiHandler;
		private Context mContext;
		private CameraCharacteristics mCameraInfo;
		private CameraManager mCameraManager;
		private CameraOps mCameraOps;
		private Object mAutoExposureTag = new Object();
		CaptureRequest mPreviewRequest;
		

        public BaseTvInputSessionImpl(Context context) {
            super(context);
			mContext = context;
			mUiHandler = new Handler(Looper.getMainLooper());
        }

        public void onRelease() {}

        public boolean onSetSurface(Surface surface) {
            Log.d(TAG,"on set onSetSurface ");
			
			mSurface = surface;
			//mMediaPlayer = new MediaPlayer();
            return true;
        }

        public void onSetStreamVolume(float v) {}

        /*private class OnPreparedListener implements MediaPlayer.OnPreparedListener {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d(TAG, "start now ");
                mp.start();
				mBaseTvInputSessionImpl.notifyVideoAvailable();
            }
        }*/

        public boolean onTune(Uri uri) {
			Log.d(TAG,"on Tune");
			/*try{
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource("/sdcard/sophie.mp4");
            mMediaPlayer.setSurface(mSurface);
            mMediaPlayer.setOnErrorListener(null);
            mMediaPlayer.setOnBufferingUpdateListener(null);
            mMediaPlayer.setOnInfoListener(null);
            mMediaPlayer.setOnPreparedListener(new OnPreparedListener());
            mMediaPlayer.prepareAsync();
			}catch(Exception e){
				e.printStackTrace();
			}*/
			
			tuneCamera(mContext);
            return true;
        }
		
		private void tuneCamera(Context context) {
			mCameraManager = (CameraManager) context.getSystemService(CAMERA_SERVICE);
			if (mCameraManager != null) {
				mCameraOps = new CameraOps(mCameraManager,
                /*errorDisplayer*/ this,
                /*readyListener*/ this,
                /*readyHandler*/ mUiHandler);
			} else {
				Log.e(TAG, "Couldn't initialize the camera");
			}
			try {
			List<Surface> cameraOutputSurfaces = new ArrayList<Surface>();
			cameraOutputSurfaces.add(mSurface);
			String[] cameraIds = mCameraManager.getCameraIdList();
            for (String id : cameraIds) {
				CameraCharacteristics info = mCameraManager.getCameraCharacteristics(id);
                int facing = info.get(CameraCharacteristics.LENS_FACING);
				Log.d(TAG,"find camera id = " + id);
				Log.d(TAG," now open back camera ");
				mCameraOps.openCamera(id);
				mCameraOps.setSurfaces(cameraOutputSurfaces);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			mBaseTvInputSessionImpl.notifyVideoAvailable();
		}
		
		@Override
        public void onCameraReady() {
			try {
            CaptureRequest.Builder previewBuilder =
                    mCameraOps.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewBuilder.addTarget(mSurface);
            previewBuilder.setTag(mAutoExposureTag);
            mPreviewRequest = previewBuilder.build();
		    mCameraOps.setRepeatingRequest(mPreviewRequest,
                        null, mUiHandler);
			}catch(Exception e){
				e.printStackTrace();
			}
			Log.d(TAG,"Set onCameraReady");
        }

        @Override
        public void showErrorDialog(String errorMessage) {

        }

        @Override
        public String getErrorString(CameraAccessException e) {
            return null;
        }

        public void onSetCaptionEnabled(boolean b) {}
    }
}
