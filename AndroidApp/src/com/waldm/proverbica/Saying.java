package com.waldm.proverbica;

public class Saying {
    private String text;
    private String imageLocation;

    public Saying(String text, String imageLocation) {
        this.text = text;
        this.imageLocation = imageLocation;
    }

    public String getText() {
        return text;
    }

    public String getImageLocation() {
        return imageLocation;
    }
}
