package com.example.utspemhir;

public class TableRow {
    private String surah;
    private String tanggal;
    private String persyaratan;
    private String parafPA;

    public TableRow(String surah, String tanggal, String persyaratan, String parafPA) {
        this.surah = surah;
        this.tanggal = tanggal;
        this.persyaratan = persyaratan;
        this.parafPA = parafPA;
    }

    public String getSurah() {
        return surah;
    }

    public void setSurah(String surah) {
        this.surah = surah;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getPersyaratan() {
        return persyaratan;
    }

    public void setPersyaratan(String persyaratan) {
        this.persyaratan = persyaratan;
    }

    public String getParafPA() {
        return parafPA;
    }

    public void setParafPA(String parafPA) {
        this.parafPA = parafPA;
    }
}
