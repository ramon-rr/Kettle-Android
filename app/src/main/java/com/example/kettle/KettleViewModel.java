package com.example.kettle;

import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class KettleViewModel extends ViewModel {
    MutableLiveData<Boolean> postCreated;
    MutableLiveData<String>  postTitle;
    MutableLiveData<String>  postBody;
    //Possible variable for profile.

    public MutableLiveData<Boolean> getPostCreated() {
        if(postCreated == null) {
            postCreated = new MutableLiveData<>();
        }
        return postCreated;
    }

    public MutableLiveData<String> getPostTitle() {
        if(postTitle == null) {
            postTitle = new MutableLiveData<>();
        }
        return postTitle;
    }

    public MutableLiveData<String> getPostBody() {
        if(postBody == null) {
            postBody = new MutableLiveData<>();
        }
        return postBody;
    }
}
