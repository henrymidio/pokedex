package com.mdio.br.pokedex.network;

import com.mdio.br.pokedex.domain.Prediction;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface WatsonService {

    @Multipart
    @POST("upload")
    Call<List<Prediction>> predict(@Part MultipartBody.Part photo);

}
