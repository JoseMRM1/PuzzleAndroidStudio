package com.example.puzzledroid_java.cloud_entities;

public class CloudImage implements Comparable<CloudImage>{
    // variables for storing our image and nivel.
    private String nivel;
    private String imgUrl;

    //constructor vacio, necesario para carga de imagen desde firebase
    public CloudImage() {
    }

    public CloudImage(String nivel, String imgUrl) {
        this.nivel = nivel;
        this.imgUrl = imgUrl;
    }

    //getters y setters
    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public int compareTo(CloudImage ci) {
        if (getNivel() == null || ci.getNivel() == null) {
            return 0;
        }
        return getNivel().compareTo(ci.getNivel());
    }
}
