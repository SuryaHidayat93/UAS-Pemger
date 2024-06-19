package com.example.utspemhir;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SurahResponse {
    @SerializedName("status")
    private String status;
    @SerializedName("Nama")
    private String nama;
    @SerializedName("NIM")
    private String nim;
    @SerializedName("percentages")
    private List<Percentage> percentages;

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public List<Percentage> getPercentages() {
        return percentages;
    }

    public void setPercentages(List<Percentage> percentages) {
        this.percentages = percentages;
    }

    public static class Percentage {
        @SerializedName("lang")
        private String lang;
        @SerializedName("percent")
        private int percent;
        @SerializedName("surah_names")
        private List<String> surahNames;

        // Getters and Setters
        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }

        public int getPercent() {
            return percent;
        }

        public void setPercent(int percent) {
            this.percent = percent;
        }

        public List<String> getSurahNames() {
            return surahNames;
        }

        public void setSurahNames(List<String> surahNames) {
            this.surahNames = surahNames;
        }
    }
}
