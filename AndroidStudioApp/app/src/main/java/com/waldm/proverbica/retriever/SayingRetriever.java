package com.waldm.proverbica.retriever;

import com.waldm.proverbica.SayingListener;
import com.waldm.proverbica.controllers.SayingController;
import com.waldm.proverbica.infrastructure.ImageSize;
import com.waldm.proverbica.infrastructure.SayingSource;

public interface SayingRetriever {

    void loadSaying(SayingSource source, ImageSize imageSize);

    void setSayingListener(SayingListener sayingListener);
}
