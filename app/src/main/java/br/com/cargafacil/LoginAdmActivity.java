package br.com.cargafacil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import static br.com.cargafacil.utils.Utils.CONFIG_FILE;
import static br.com.cargafacil.utils.Utils.sha256;

public class LoginAdmActivity extends AppCompatActivity implements DrawerLayout.DrawerListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    AppCompatEditText inputUser;
    TextInputLayout inputUserLayout;
    AppCompatEditText inputPass;
    TextInputLayout inputPassLayout;
    private String userAdm, passAdm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_adm);

        instantiateItens();

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

        SharedPreferences sharedPreferences = getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE);
        userAdm = sharedPreferences.getString("user_adm", "erro");
        passAdm = sharedPreferences.getString("pass_adm", "erro");
    }

    private void instantiateItens() {
        inputUser = findViewById(R.id.inputUser);
        inputUserLayout = findViewById(R.id.layoutInputUser);
        inputPass = findViewById(R.id.inputPass);
        inputPassLayout = findViewById(R.id.layoutInputPass);
    }

    public void efetuarLogin(View v) {
        String userInputed, passInputed;
        if (inputPass.getText() != null) {
            passInputed = inputPass.getText().toString();
        } else {
            passInputed = "";
        }
        if (passInputed.isEmpty()) {
            inputPassLayout.setErrorEnabled(true);
            inputPassLayout.setError("digite sua senha");
        }
        if (!sha256(passInputed).equals(passAdm)) {
            inputPassLayout.setErrorEnabled(true);
            inputPassLayout.setError("senha incorreta");
        } else {
            inputPassLayout.setErrorEnabled(false);
        }
        if (inputUser.getText() != null) {
            userInputed = inputUser.getText().toString();
        } else {
            userInputed = "";
        }
        if (userInputed.isEmpty()) {
            inputUserLayout.setErrorEnabled(true);
            inputUserLayout.setError("digite o usuario");
        }
        if (!userInputed.equals(userAdm)) {
            inputUserLayout.setErrorEnabled(true);
            inputUserLayout.setError("usuario incorreto");
        } else {
            inputUserLayout.setErrorEnabled(false);
        }
        if (!inputPassLayout.isErrorEnabled() && !inputUserLayout.isErrorEnabled()) {
            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(LoginAdmActivity.this).toBundle();
            Intent intent = new Intent(LoginAdmActivity.this, PainelAdministrativoActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent, bundle);
        } else {
            Toast.makeText(this, "Informações Incorretas!", Toast.LENGTH_SHORT).show();
        }
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
