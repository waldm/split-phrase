package com.waldm.proverbica.retriever;

import com.squareup.picasso.Target;

public interface SayingRetriever {
    SayingRetriever loadSayingAndRefresh();

    String loadSaying();

    void loadImage(String imageName, Target target);
}
