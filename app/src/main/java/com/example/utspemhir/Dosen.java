package com.example.utspemhir;

import java.util.List;

public class Dosen {
    private String Nama;
    private String NIP;
    private List<Student> Mahasiswa;

    // Getter dan setter
    public String getNama() { return Nama; }
    public void setNama(String Nama) { this.Nama = Nama; }
    public String getNIP() { return NIP; }
    public void setNIP(String NIP) { this.NIP = NIP; }
    public List<Student> getMahasiswa() { return Mahasiswa; }
    public void setMahasiswa(List<Student> Mahasiswa) { this.Mahasiswa = Mahasiswa; }
}