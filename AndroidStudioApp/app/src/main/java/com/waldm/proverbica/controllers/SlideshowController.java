package com.waldm.proverbica.controllers;

import android.os.Handler;
import android.os.Looper;

import com.google.common.base.Stopwatch;
import com.waldm.proverbica.SlideshowDisplayer;
import com.waldm.proverbica.infrastructure.ImageSize;
import com.waldm.proverbica.infrastructure.SayingSource;

import java.util.concurrent.TimeUnit;

public class SlideshowController {
    private final SayingController sayingController;
    private final SlideshowDisplayer slideshowDisplayer;
    private Runnable moveToNextImage;
    private static final long SLIDESHOW_TRANSITION = 3000;
    private final Stopwatch stopwatch = Stopwatch.createUnstarted();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isSlideshowRunning;

    public SlideshowController(final SayingController sayingController, SlideshowDisplayer slideshowDisplayer){
        this.sayingController = sayingController;
        this.slideshowDisplayer = slideshowDisplayer;
        moveToNextImage = new Runnable() {
            @Override
            public void run() {
                if (stopwatch.elapsed(TimeUnit.MILLISECONDS) > SLIDESHOW_TRANSITION) {
                    sayingController.loadSaying(SayingSource.EITHER, ImageSize.NORMAL);
                    stopwatch.reset();
                    stopwatch.start();
                }

                handler.postDelayed(moveToNextImage, SLIDESHOW_TRANSITION);
            }
        };
    }

    public void setIsSlideshowRunning(boolean isSlideshowRunning) {
        this.isSlideshowRunning = isSlideshowRunning;
        if (isSlideshowRunning) {
            startSlideshow();
        } else {
            stopSlideshow();
        }
    }

    public boolean isSlideshowRunning() {
        return isSlideshowRunning;
    }

    public void stopStopwatch() {
        if (stopwatch.isRunning()) {
            stopwatch.stop();
        }
    }

    public void startSlideshow() {
        isSlideshowRunning = true;
        slideshowDisplayer.startSlideshow();

        stopwatch.reset();
        stopwatch.start();
        sayingController.loadSaying(SayingSource.EITHER, ImageSize.NORMAL);
        handler.postDelayed(moveToNextImage, 0);
    }

    public void stopSlideshow() {
        isSlideshowRunning = false;
        slideshowDisplayer.stopSlideshow();

        handler.removeCallbacks(moveToNextImage);
        stopwatch.reset();
    }
}
