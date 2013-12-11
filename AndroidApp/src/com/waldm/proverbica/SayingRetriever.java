package com.waldm.proverbica;

public interface SayingRetriever {
	void loadImage(String imageName);

	SayingRetriever loadSayingAndRefresh(String sayingPage);
}
