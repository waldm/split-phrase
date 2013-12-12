package com.waldm.proverbica.retriever;

public interface SayingRetriever {
    void loadImage(String imageName);

    SayingRetriever loadSayingAndRefresh(String sayingPage);

    String loadSaying(String sayingPage);
}
