package com.byobdev.kamal.helpers;


import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.byobdev.kamal.CreateInitiativeActivity;
import com.byobdev.kamal.LoginActivity;
import com.byobdev.kamal.SetInterestsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Set;

/**
 * Created by nano on 7/23/17.
 * Decide que hacer con lo que sucede al presionar botones del menú.
 */

public class DrawerItemClickListener implements ListView.OnItemClickListener{

    private Activity mActivity;
    public DrawerItemClickListener(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                Intent intentMain = new Intent(mActivity, LoginActivity.class);
                mActivity.startActivity(intentMain);
                break;
            case 1:
                if(FirebaseAuth.getInstance().getCurrentUser()==null){
                    Intent intentMain3 = new Intent(mActivity, LoginActivity.class);
                    mActivity.startActivity(intentMain3);
                    break;
                }
                else{
                    Intent intentMain2 = new Intent(mActivity, CreateInitiativeActivity.class);
                    mActivity.startActivity(intentMain2);
                    break;
                }
            case 2:
                if(FirebaseAuth.getInstance().getCurrentUser()==null){
                    Intent intentMain3 = new Intent(mActivity, LoginActivity.class);
                    mActivity.startActivity(intentMain3);
                    break;
                }
                else{
                    Intent intentMain2 = new Intent(mActivity, SetInterestsActivity.class);
                    mActivity.startActivity(intentMain2);
                    break;
                }
        }
    }
}
