package br.com.cargafacil;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.transition.Fade;
import androidx.transition.TransitionManager;

public class MainActivity extends AppCompatActivity implements DrawerLayout.DrawerListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private AppCompatImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        imageButton = toolbar.findViewById(R.id.infoBtn);
        imageButton.setPressed(false);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.fecharDrawer);
        drawerLayout.addDrawerListener(toggle);
        drawerLayout.addDrawerListener(this);

        toggle.syncState();
    }

    public void toggleInfosEmpresa(View view) {
        final ViewGroup layoutDadosEmpresa = this.findViewById(R.id.dados_empresa_include);
        if (layoutDadosEmpresa != null) {
            TransitionManager.beginDelayedTransition(layoutDadosEmpresa, new Fade());
            imageButton.setPressed(!imageButton.isPressed());
            if (layoutDadosEmpresa.getVisibility() == View.VISIBLE) {
                layoutDadosEmpresa.setVisibility(View.GONE);
            } else {
                layoutDadosEmpresa.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {
    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {
        if (newState == DrawerLayout.STATE_IDLE) {
            new Thread(() -> {
                if (getCurrentFocus() != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            }).start();
        }
    }
}
