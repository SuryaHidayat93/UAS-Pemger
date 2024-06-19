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

    public String getTanggal() {
        return tanggal;
    }

    public String getPersyaratan() {
        return persyaratan;
    }

    public String getParafPA() {
        return parafPA;
    }
}
