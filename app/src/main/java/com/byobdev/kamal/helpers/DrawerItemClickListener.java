package com.byobdev.kamal.helpers;


import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.byobdev.kamal.InitiativesActivity;
import com.byobdev.kamal.Login;

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
                Intent intentMain = new Intent(mActivity, InitiativesActivity.class);
                mActivity.startActivity(intentMain);
                break;
        }
    }
}
