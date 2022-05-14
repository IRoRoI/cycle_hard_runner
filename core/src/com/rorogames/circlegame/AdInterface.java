package com.rorogames.circlegame;

public interface AdInterface {
    void loadInterstitialAd();
    void showInterstitialAd();
    void redirect(String url);
    void setBanner(boolean active);

    void loadAfterDeadRAd();
    void showAfterDeadAd();
}
