package com.waldm.proverbica.retriever;


public interface SayingRetriever {
    SayingRetriever loadSayingAndRefresh();

    String loadSaying();
}
