package com.example.utspemhir;


import com.google.gson.annotations.SerializedName;

public class NIMResponse {
    @SerializedName("nim")
    private String nim;

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }
}

