package com.byobdev.kamal;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class TutorialActivity extends AppIntro2 {
    int firstId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance("Encuentra", "Entérate de lo que ocurre a tu alrededor. Las marcas en el mapa te dirán la categoría", R.drawable.ic_helpimages1, getResources().getColor(R.color.Primary)));
        addSlide(AppIntroFragment.newInstance("Marcas", "Los colores indican el estado de la iniciativa", R.drawable.ic_helpimages2, getResources().getColor(R.color.Primary)));
        addSlide(AppIntroFragment.newInstance("Filtra", "Por categoría o palabra clave usando los íconos en la barra superior", R.drawable.ic_helpimages3, getResources().getColor(R.color.Primary)));
        addSlide(AppIntroFragment.newInstance("¿Tienes tiempo libre?", "Ve que ocurrirá a esa hora usando la línea de tiempo. Elige un rango de tiempo y muevelo a lo largo del día.", R.mipmap.ic_launcher, getResources().getColor(R.color.Primary)));
        setBarColor(getResources().getColor(R.color.Primary));

        firstId = getLayoutId();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        this.startActivity(new Intent(this, InitiativesActivity.class));
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        this.startActivity(new Intent(this, InitiativesActivity.class));
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
    }

    @Override
    public void onBackPressed() {
        if (firstId != getLayoutId()){
            super.onBackPressed();
        } else {
            this.startActivity(new Intent(this, InitiativesActivity.class));
            finish();
        }
    }
}