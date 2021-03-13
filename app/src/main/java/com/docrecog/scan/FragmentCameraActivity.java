package com.docrecog.scan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.accurascan.accuraemirates.AccuraDemoApplication;
import com.accurascan.accuraemirates.R;
import com.accurascan.accuraemirates.motiondetection.SensorsActivity;

public class FragmentCameraActivity extends SensorsActivity {

    private boolean isBackPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_camera);
        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, CameraFragment.newInstance())
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (isBackPressed) {
            try {
                if (AccuraDemoApplication.getFrontimage() != null && !AccuraDemoApplication.getFrontimage().isRecycled())
                    AccuraDemoApplication.getFrontimage().recycle();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (AccuraDemoApplication.getBackimage() != null && !AccuraDemoApplication.getBackimage().isRecycled())
                    AccuraDemoApplication.getBackimage().recycle();
            } catch (Exception e) {
                e.printStackTrace();
            }

            AccuraDemoApplication.setFrontData(null);
            AccuraDemoApplication.setBackData(null);
            super.onBackPressed();
        } else {
            Toast.makeText(FragmentCameraActivity.this, "Press again to exit app", Toast.LENGTH_SHORT).show();
            isBackPressed = true;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isBackPressed = false;
            }
        }, 2000);
    }
}