package com.byobdev.kamal.helpers;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;

import com.byobdev.kamal.InitiativesActivity;
import com.byobdev.kamal.LoginActivity;

import static android.support.v4.content.ContextCompat.startActivity;

/**
 * Created by nano on 7/23/17.
 * Decide que hacer con lo que sucede al presionar botones del menú.
 */

public class DrawerItemClickListener implements ListView.OnItemClickListener{
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(position);
    }
    //Maneja lo que se hace con cada botón
    private void selectItem(int position) {
        switch(position){
            case 0:
                //code
                break;
            case 1:
                //code
                break;
            case 2:
                //code
                break;
            case 3:
                startActivity(new Intent(, LoginActivity.class));
                break;
        }
     // // Create a new fragment and specify the planet to show based on position
     // Fragment fragment = new PlanetFragment();
     // Bundle args = new Bundle();
     // args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
     // fragment.setArguments(args);
     //
     // // Insert the fragment by replacing any existing fragment
     // FragmentManager fragmentManager = getFragmentManager();
     // fragmentManager.beginTransaction()
     //         .replace(R.id.content_frame, fragment)
     //         .commit();
     //
     // // Highlight the selected item, update the title, and close the drawer
     // mDrawerList.setItemChecked(position, true);
     // setTitle(mPlanetTitles[position]);
     // mDrawerLayout.closeDrawer(mDrawerList);
    }
}
