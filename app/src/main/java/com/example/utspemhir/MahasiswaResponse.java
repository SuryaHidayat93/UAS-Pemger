package com.example.utspemhir;

public class MahasiswaResponse {
    private String Nama;
    private String NIM;
    private String Semester;

    // Getters and setters
    public String getNama() {
        return Nama;
    }

    public void setNama(String nama) {
        Nama = nama;
    }

    public String getNim() {
        return NIM;
    }

    public void setNim(String nim) {
        NIM = nim;
    }

    public String getSemester() {
        return Semester;
    }

    public void setSemester(String semester) {
        Semester = semester;
    }
}
