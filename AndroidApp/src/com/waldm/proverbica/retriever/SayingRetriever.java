package com.waldm.proverbica.retriever;

import com.waldm.proverbica.Saying;
import com.waldm.proverbica.infrastructure.SayingSource;

public interface SayingRetriever {
    SayingRetriever loadSayingAndRefresh(SayingSource source);

    Saying loadSaying(SayingSource source);

}
