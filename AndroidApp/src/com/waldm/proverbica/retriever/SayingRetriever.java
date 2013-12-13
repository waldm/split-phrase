package com.waldm.proverbica.retriever;

import java.util.Random;

import android.util.Log;

import com.google.common.collect.ImmutableList;

public class SayingRetriever {

    private static final String TAG = SayingRetriever.class.getSimpleName();
    private ImmutableList<String> sayings = new ImmutableList.Builder<String>().add("A dog is a man's best friend")
            .add("A drowning man will clutch at a straw").add("A fool and his money are soon parted")
            .add("Women and children first").add("Worrying never did anyone any good")
            .add("Youth is wasted on the young").build();

    public String loadSaying() {
        Log.d(TAG, "Loaded saying");
        return sayings.get(new Random().nextInt(sayings.size()));
    }
}
