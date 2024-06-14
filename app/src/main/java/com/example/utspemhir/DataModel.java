package com.example.utspemhir;

public class DataModel {

    private int idSetoran;
    private String tanggal, surah, kelancaran, tajwid, makhrajulHuruf;

    public DataModel(int idSetoran, String tanggal, String surah, String kelancaran, String tajwid, String makhrajulHuruf) {
        this.idSetoran = idSetoran;
        this.tanggal = tanggal;
        this.surah = surah;
        this.kelancaran = kelancaran;
        this.tajwid = tajwid;
        this.makhrajulHuruf = makhrajulHuruf;
    }

    public int getIdSetoran() {
        return idSetoran;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getSurah() {
        return surah;
    }

    public String getKelancaran() {
        return kelancaran;
    }

    public String getTajwid() {
        return tajwid;
    }

    public String getMakhrajulHuruf() {
        return makhrajulHuruf;
    }
}

