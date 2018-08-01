package com.mdio.br.pokedex;


import android.content.ContentResolver;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mdio.br.pokedex.camera.CameraWraper;
import com.mdio.br.pokedex.model.Prediction;
import com.mdio.br.pokedex.network.WatsonService;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity {

    private Camera mCamera;
    FrameLayout preview;
    TextView tvPokemon;
    Retrofit retrofit;
    WatsonService service;
    Animation anim;
    MediaPlayer mediaPlayer;
    private boolean loading = false;
    private CameraWraper cameraWraper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
            Fullscreen
         */
        getSupportActionBar().hide();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // Init views
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        tvPokemon = (TextView) findViewById(R.id.tvPokemon);

        initCamera();

        // Set retrofit request
        retrofit = new Retrofit.Builder()
                .baseUrl("https://nameless-tor-81706.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(WatsonService.class);

        // Set animation text view
        anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCamera != null){
            mCamera.release();
            mCamera = null;
            if(mediaPlayer != null) {
                mediaPlayer.release();
            }
            finish();
        }
    }

    private void initCamera(){
        try {
            mCamera = Camera.open(); // attempt to get a Camera instance
            mCamera.setDisplayOrientation(90);
            cameraWraper = new CameraWraper(this, mCamera);
            preview.addView(cameraWraper);
        }
        catch (Exception e){
            Log.e("CAMERA", e.getMessage());
        }
    }

    public void takePicture(View view) {
        if(loading) return;

        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                    sendPredictionRequest(data);

            }
        });
    }

    private void sendPredictionRequest(byte[] photo) {

        tvPokemon.setText("Identificando...");
        tvPokemon.startAnimation(anim);
        loading = true;

        RequestBody request = RequestBody.create(MediaType.parse("image/*"), photo);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", "pokemon", request);
        Call<List<Prediction>> call = service.predict(body);

        call.enqueue(new Callback<List<Prediction>>() {
            @Override
            public void onResponse(Call<List<Prediction>> call, Response<List<Prediction>> response) {
                try {

                    Prediction p = response.body().get(0);
                    playMedia(p.getClass_());
                    tvPokemon.clearAnimation();
                    tvPokemon.setText(p.getClass_());
                    Log.e("pokemon", response.body().size() + "");

                } catch (Exception e) {
                    tvPokemon.clearAnimation();
                    tvPokemon.setText("Pokemon não identificado :(");
                    mCamera.startPreview();
                    loading = false;
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<Prediction>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void playMedia(String fileName) {

        // Vídeo
        int drawableResourceId = this.getResources().getIdentifier(fileName, "drawable", this.getPackageName());
        preview.removeAllViews();
        preview.setBackgroundResource(drawableResourceId);

        // Áudio
        mediaPlayer = MediaPlayer.create(this, getRawUri(fileName));
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                tvPokemon.setText("");
                mp.release();
                loading = false;
                preview.addView(cameraWraper);
                initCamera();
            }
        });

    }

    private Uri getRawUri(String filename) {
        int res_sound_id = getResources().getIdentifier(filename, "raw", getPackageName());
        return Uri.parse("android.resource://" + getPackageName() + "/" +res_sound_id );
    }


}
