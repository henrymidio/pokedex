package com.mdio.br.pokedex.camera;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.mdio.br.pokedex.domain.Prediction;
import com.mdio.br.pokedex.network.WatsonService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class CameraHelper extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Context context;

    public CameraHelper(Context context) {
        super(context);
        this.context = context;
        init();

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    /** A safe way to get an instance of the Camera object. */
    private void init(){
        mCamera = null;
        if(mCamera != null) {
            return;
        }
        try {
            mCamera = Camera.open(); // attempt to get a Camera instance
            mCamera.setDisplayOrientation(90);
            Log.e("CAMERA", "" + mCamera.toString());
        }
        catch (Exception e){
            Log.e("CAMERA", e.getMessage());
        }
    }

    public void takePicture() {

        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://nameless-tor-81706.herokuapp.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                WatsonService service = retrofit.create(WatsonService.class);
                RequestBody request = RequestBody.create(MediaType.parse("image/*"), data);
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", "qualquercoisa", request);
                Call<List<Prediction>> call = service.predict(body);
                call.enqueue(new Callback<List<Prediction>>() {
                    @Override
                    public void onResponse(Call<List<Prediction>> call, Response<List<Prediction>> response) {
                        try {
                            Prediction p = response.body().get(0);
                            Log.e("res", p.getClass_());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Prediction>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        });
    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d("CAMERA ERROR", "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
