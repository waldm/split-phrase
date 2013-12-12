package com.waldm.proverbica.retriever;

import com.squareup.picasso.Target;

public interface SayingRetriever {
    SayingRetriever loadSayingAndRefresh(String sayingPage);

    String loadSaying(String sayingPage);

    void loadImage(String imageName, Target target);
}
