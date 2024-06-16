package com.example.utspemhir;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SetoranResponse {
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;
    @SerializedName("NIM")
    private String nim;
    @SerializedName("setoran")
    private List<Setoran> setoran;

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public List<Setoran> getSetoran() {
        return setoran;
    }

    public void setSetoran(List<Setoran> setoran) {
        this.setoran = setoran;
    }

    public static class Setoran {
        @SerializedName("id_setoran")
        private int idSetoran;
        @SerializedName("Nama_Mahasiswa")
        private String namaMahasiswa;
        @SerializedName("nama_surah")
        private String namaSurah;
        @SerializedName("tanggal")
        private String tanggal;
        @SerializedName("kelancaran")
        private String kelancaran;
        @SerializedName("tajwid")
        private String tajwid;
        @SerializedName("makhrajul_huruf")
        private String makhrajulHuruf;

        // Getters and Setters
        public int getIdSetoran() {
            return idSetoran;
        }

        public void setIdSetoran(int idSetoran) {
            this.idSetoran = idSetoran;
        }

        public String getNamaMahasiswa() {
            return namaMahasiswa;
        }

        public void setNamaMahasiswa(String namaMahasiswa) {
            this.namaMahasiswa = namaMahasiswa;
        }

        public String getNamaSurah() {
            return namaSurah;
        }

        public void setNamaSurah(String namaSurah) {
            this.namaSurah = namaSurah;
        }

        public String getTanggal() {
            return tanggal;
        }

        public void setTanggal(String tanggal) {
            this.tanggal = tanggal;
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
    }
}
