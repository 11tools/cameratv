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

import android.app.Activity;
import android.os.Bundle;
import android.Manifest.permission;
import android.content.pm.PackageManager;
import androidx.leanback.app.GuidedStepFragment;

/** The setup activity for partner support sample TV input. */
public class CameraTvInputSetupActivity extends Activity {
    public static final int REQUEST_CODE_START_SETUP_ACTIVITY = 1;
	private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 42;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 45;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestAllPermissions();
        if (savedInstanceState == null) {
            com.android.cameratv.WelcomeFragment wf = new com.android.cameratv.WelcomeFragment();
            wf.setArguments(getIntent().getExtras());
            GuidedStepFragment.addAsRoot(this, wf, android.R.id.content);
        }
    }

    private boolean checkPermissions() {
        if (checkSelfPermission(permission.CAMERA)
                 != PackageManager.PERMISSION_GRANTED)
            return false;
        if (checkSelfPermission(permission.READ_EXTERNAL_STORAGE)
                 != PackageManager.PERMISSION_GRANTED)
            return false;
        return true;
    }

    private void requestAllPermissions() {
        if (checkSelfPermission(permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }
        if (checkSelfPermission(permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
            case MY_PERMISSIONS_REQUEST_CAMERA:
            {
                if (grantResults.length == 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    finish();
                    return;
                }
            }
        }
    }
	
	
}
