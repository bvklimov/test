package com.spb.kbv.sampleskbv.presenters;

public interface NetworkRequestView {
    void showLoading();
    void showConnectionError();
    void showServerError();
    void showAirplaneError();
    void showUnauthorizedError();
    void showBadRequestError();
    void stopLoading();
    void showSnackBar(int stringId);
}
