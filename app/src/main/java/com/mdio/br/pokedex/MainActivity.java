package com.mdio.br.pokedex;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.mdio.br.pokedex.camera.CameraHelper;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity {

    private ImageView coverTop;
    private ImageView coverBottom;
    private boolean COVER_OPEN = false;
    private CameraHelper mCamera;
    FrameLayout preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
            Esconde actionbar e status bar
         */
        getSupportActionBar().hide();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        /*
            inicializa view
         */
        preview = (FrameLayout) findViewById(R.id.camera_preview);

        initCameraPreview();

    }

    public void initCameraPreview() {
        mCamera = new CameraHelper(this);
        preview.addView(mCamera);
    }

    public void takePicture(View view) {
        mCamera.takePicture();
    }
}
