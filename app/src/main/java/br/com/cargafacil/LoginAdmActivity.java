package br.com.cargafacil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class LoginAdmActivity extends AppCompatActivity implements DrawerLayout.DrawerListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_adm);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Entrar");
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.fecharDrawer);
        drawerLayout.addDrawerListener(toggle);
        drawerLayout.addDrawerListener(this);

        toggle.syncState();

        navigationView = findViewById(R.id.navView);
        navigationView.getMenu().getItem(2).getSubMenu().getItem(1).setEnabled(false);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            switch (menuItem.getItemId()) {
                case R.id.iniciar_pesagem:
                    Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(LoginAdmActivity.this).toBundle();
                    Intent intent = new Intent(LoginAdmActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent, bundle);
                    break;
                case R.id.calculadora:
                    Toast.makeText(LoginAdmActivity.this, "Calculadora!", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.historico:
                    visualizarHistorico();
                    Toast.makeText(LoginAdmActivity.this, "Histórico!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(this, "Menu Desconhecido!", Toast.LENGTH_SHORT).show();
            }
            return false;
        });
    }

    public void efetuarLogin(View v) {
        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(LoginAdmActivity.this).toBundle();
        Intent intent = new Intent(LoginAdmActivity.this, PainelAdministrativoActivity.class).setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent, bundle);
    }

    private void visualizarHistorico() {
        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(LoginAdmActivity.this).toBundle();
        Intent intent = new Intent(LoginAdmActivity.this, LoginAdmActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent, bundle);
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

    }
}
