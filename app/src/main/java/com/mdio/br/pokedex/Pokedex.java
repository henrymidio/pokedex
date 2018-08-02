package com.mdio.br.pokedex;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.IOException;

public class Pokedex {

    public static MediaPlayer playMedia(String fileName, MediaPlayer.OnCompletionListener callback) {
        String audioUrl = "https://firebasestorage.googleapis.com/v0/b/pokedex-2cb9e.appspot.com/o/audios%2F" + fileName + ".mp3?alt=media";
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            Log.e("audio erro: ", e.getMessage());
            e.printStackTrace();
        }

        mediaPlayer.setOnCompletionListener(callback);

        return mediaPlayer;

    }

    public static void displayPokemonImage(String fileName, final FrameLayout preview, Context context) {
        Glide.with(context).load("https://firebasestorage.googleapis.com/v0/b/pokedex-2cb9e.appspot.com/o/images%2F" + fileName + ".png?alt=media")
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        preview.removeAllViews();
                        preview.setBackground(resource);
                    }
                });
    }

}
