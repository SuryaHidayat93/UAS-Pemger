package com.example.utspemhir;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @GET("dosenpa/by-nip.php")  // Contoh endpoint untuk mendapatkan dosen berdasarkan NIP
    Call<DosenResponse> getDosenByNip(@Query("nip") String nip);

    @GET("surah/get-all.php")  // Contoh endpoint untuk mendapatkan semua surah
    Call<List<Surah>> getAllSurah();

    @FormUrlEncoded
    @POST("setoran/insert.php")
    Call<SetoranResponse> insertSetoran(
            @Field("nim") String nim,
            @Field("nip") String nip,
            @Field("id_surah") int idSurah,
            @Field("tanggal") String tanggal,
            @Field("kelancaran") String kelancaran,
            @Field("tajwid") String tajwid,
            @Field("makhrajul_huruf") String makhrajulHuruf
    );
}
