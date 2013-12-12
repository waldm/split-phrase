package com.waldm.proverbica;

import java.util.Random;

public class ImageHandler {
    private int imageIndex;
    protected static final String[] images = { "lion.jpg", "monkey.jpg", "gorilla.jpg", "hawk.jpg", "owl.jpg",
            "dog.jpg", "tiger.jpg", "polar_bear.jpg", "elephant.jpg", "leopard.jpg", "cat.jpg" };

    public String getNextImage() {
        int newImageIndex = new Random().nextInt(images.length);
        while (newImageIndex == imageIndex) {
            newImageIndex = new Random().nextInt(images.length);
        }

        imageIndex = newImageIndex;
        return images[imageIndex];
    }
}
