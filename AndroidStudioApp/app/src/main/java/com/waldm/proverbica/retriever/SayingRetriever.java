package com.waldm.proverbica.retriever;

import com.waldm.proverbica.infrastructure.ImageSize;
import com.waldm.proverbica.infrastructure.SayingSource;

public interface SayingRetriever {

    void loadSaying(SayingSource source, ImageSize imageSize);

}