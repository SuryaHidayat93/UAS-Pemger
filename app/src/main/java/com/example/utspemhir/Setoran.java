package com.example.utspemhir;

public class Setoran {
    private String tanggal;
    private String surah;
    private String kelancaran;
    private String tajwid;
    private String makhrajulHuruf;
    private String nim;
    private String nip;
    private int idSurah;

    public Setoran(String nim, String nip, String selectedSurah, String tanggal, String selectedKelancaran, String selectedTajwid, String selectedMakhrajulKhuruf) {
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getSurah() {
        return surah;
    }

    public void setSurah(String surah) {
        this.surah = surah;
    }

    public String getKelancaran() {
        return kelancaran;
    }

    public void setKelancaran(String kelancaran) {
        this.kelancaran = kelancaran;
    }

    public String getTajwid() {
        return tajwid;
    }

    public void setTajwid(String tajwid) {
        this.tajwid = tajwid;
    }

    public String getMakhrajulHuruf() {
        return makhrajulHuruf;
    }

    public void setMakhrajulHuruf(String makhrajulHuruf) {
        this.makhrajulHuruf = makhrajulHuruf;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public int getIdSurah() {
        return idSurah;
    }

    public void setIdSurah(int idSurah) {
        this.idSurah = idSurah;
    }
}

