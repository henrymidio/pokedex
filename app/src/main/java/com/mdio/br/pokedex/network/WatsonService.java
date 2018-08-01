package com.mdio.br.pokedex.network;

import com.mdio.br.pokedex.model.Prediction;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface WatsonService {

    @Multipart
    @POST("upload")
    Call<List<Prediction>> predict(@Part MultipartBody.Part photo);

}
