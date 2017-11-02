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
        addSlide(AppIntroFragment.newInstance("Filtra por categoría y palabra", "Las marcas desaparecerán del mapa quedando sólo las que coinciden con tu búsqueda. \n El color amarillo indica que la categoría fue seleccionada.", R.drawable.ic_helpimages3, getResources().getColor(R.color.Primary)));
        addSlide(AppIntroFragment.newInstance("Explora que ocurrirá en el día con la línea de tiempo", "Elige un rango de tiempo y muévelo a lo largo del día. \n Cambia de día con las flechas de los extremos. \n Vuelve al día actual usando el botón del borde inferior.", R.drawable.ic_helpimages4, getResources().getColor(R.color.Primary)));

        setBarColor(getResources().getColor(R.color.Primary));

        firstId = getLayoutId();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
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
            finish();
        }
    }
}
