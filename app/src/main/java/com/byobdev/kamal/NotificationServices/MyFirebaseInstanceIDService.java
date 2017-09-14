package com.byobdev.kamal.NotificationServices;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService{

    @Override
    public void onTokenRefresh() {
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            DatabaseReference tokenDB= FirebaseDatabase.getInstance().getReference("Users");
            tokenDB.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificationToken").setValue(refreshedToken);
        }

    }
}
