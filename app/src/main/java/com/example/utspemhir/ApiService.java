package com.example.utspemhir;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("dosenpa/by-nip.php")
    Call<DosenResponse> getDosenByNip(@Query("nip") String nip);
}
