package com.example.utspemhir;

public class Item {
    String name, nim, semester;
    int image;

    public Item(String name, String nim, String semester, int image) {
        this.name = name;
        this.nim = nim;
        this.semester = semester;
        this.image = image;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

}


