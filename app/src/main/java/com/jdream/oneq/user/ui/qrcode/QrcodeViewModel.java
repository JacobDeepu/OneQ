package com.jdream.oneq.user.ui.qrcode;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class QrcodeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public QrcodeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }
}