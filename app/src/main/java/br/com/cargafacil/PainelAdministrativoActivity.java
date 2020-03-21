package br.com.cargafacil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

import br.com.cargafacil.utils.Mask;

import static br.com.cargafacil.utils.Utils.CONFIG_FILE;

public class PainelAdministrativoActivity extends AppCompatActivity implements DrawerLayout.DrawerListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private String[] arrayEstadosBR;
    private String nome, cnpj, endereco, numero, bairro, cep, cidade, estado;
    private AppCompatEditText inputNome, inputCNPJ, inputEndereco, inputNumero, inputBairro, inputCEP, inputCidade;
    private TextInputLayout layoutNome, layoutCNPJ, layoutEndereco, layoutNumero, layoutBairro, layoutCEP, layoutCidade, layoutEstado;
    private AutoCompleteTextView inputEstado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        boolean logado = getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE).getBoolean("logado", );

        setContentView(R.layout.painel_administrativo);

        //recupera views
        instantiateViews();

        toolbar.setTitle("Administrativo");
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.fecharDrawer);
        drawerLayout.addDrawerListener(toggle);
        drawerLayout.addDrawerListener(this);

        toggle.syncState();

        navigationView.getMenu().getItem(2).getSubMenu().getItem(1).setEnabled(false);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            switch (menuItem.getItemId()) {
                case R.id.iniciar_pesagem:
                    Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(PainelAdministrativoActivity.this).toBundle();
                    Intent intent = new Intent(PainelAdministrativoActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent, bundle);
                    break;
                case R.id.calculadora:
                case R.id.historico:
                default:
                    Toast.makeText(this, "Menu Desconhecido!", Toast.LENGTH_SHORT).show();
            }
            return false;
        });


        ArrayAdapter<String> adapter = new ArrayAdapter<>(PainelAdministrativoActivity.this, R.layout.dropdown_menu_popup_item, arrayEstadosBR);
        inputEstado.setAdapter(adapter);
    }

    public void validateFields(View v) {
        //Nome
        if (inputNome.getText() != null) {
            nome = inputNome.getText().toString();
            if (nome.isEmpty()) {
                layoutNome.setErrorEnabled(true);
                layoutNome.setError("preencha o nome da empresa");
            } else {
                layoutNome.setErrorEnabled(false);
            }
        } else {
            Toast.makeText(this, "Nome Nulo!", Toast.LENGTH_SHORT).show();
        }

        //CNPJ
        if (inputCNPJ.getText() != null) {
            cnpj = inputCNPJ.getText().toString();
            if (cnpj.isEmpty()) {
                layoutCNPJ.setErrorEnabled(true);
                layoutCNPJ.setError("preencha o cnpj");
            } else if (cnpj.length() < 14) {
                layoutCNPJ.setErrorEnabled(true);
                layoutCNPJ.setError("cnpj inválido");
            } else {
                layoutCNPJ.setErrorEnabled(false);
            }
        } else {
            Toast.makeText(this, "CNPJ Nulo!", Toast.LENGTH_SHORT).show();
        }

        //Endereco
        if (inputEndereco.getText() != null) {
            endereco = inputEndereco.getText().toString();
            if (endereco.isEmpty()) {
                layoutEndereco.setErrorEnabled(true);
                layoutEndereco.setError("preencha o endereço");
            } else {
                layoutEndereco.setErrorEnabled(false);
            }
        } else {
            Toast.makeText(this, "Endereço Nulo!", Toast.LENGTH_SHORT).show();
        }

        //Numero
        if (inputNumero.getText() != null) {
            numero = inputNumero.getText().toString();
            if (numero.isEmpty()) {
                layoutNumero.setErrorEnabled(true);
                layoutNumero.setError("preencha o número");
            } else {
                layoutNumero.setErrorEnabled(false);
            }
        } else {
            Toast.makeText(this, "Numero Nulo!", Toast.LENGTH_SHORT).show();
        }

        //Bairro
        if (inputBairro.getText() != null) {
            bairro = inputBairro.getText().toString();
            if (bairro.isEmpty()) {
                layoutBairro.setErrorEnabled(true);
                layoutBairro.setError("preencha o bairro");
            } else {
                layoutBairro.setErrorEnabled(false);
            }
        } else {
            Toast.makeText(this, "Bairro Nulo!", Toast.LENGTH_SHORT).show();
        }

        //CEP
        if (inputCEP.getText() != null) {
            cep = inputCEP.getText().toString();
            if (cep.isEmpty()) {
                layoutCEP.setErrorEnabled(true);
                layoutCEP.setError("preencha o cep");
            } else if (cep.length() < 8) {
                layoutCEP.setErrorEnabled(true);
                layoutCEP.setError("cep inválido");
            } else {
                layoutCEP.setErrorEnabled(false);
            }
        } else {
            Toast.makeText(this, "CEP Nulo!", Toast.LENGTH_SHORT).show();
        }

        //Cidade
        if (inputCidade.getText() != null) {
            cidade = inputCidade.getText().toString();
            if (cidade.isEmpty()) {
                layoutCidade.setErrorEnabled(true);
                layoutCidade.setError("preencha a cidade");
            } else {
                layoutCidade.setErrorEnabled(false);
            }
        } else {
            Toast.makeText(this, "Cidade Nula!", Toast.LENGTH_SHORT).show();
        }

        //Estado
        if (inputEstado.getText() != null) {
            estado = inputEstado.getText().toString();
            if (estado.isEmpty()) {
                layoutEstado.setErrorEnabled(true);
                layoutEstado.setError("preencha o estado");
            } else {
                layoutEstado.setErrorEnabled(false);
            }
        } else {
            Toast.makeText(this, "Estado Nulo!", Toast.LENGTH_SHORT).show();
        }

        if (layoutNome.isErrorEnabled() ||
                layoutCNPJ.isErrorEnabled() ||
                layoutEndereco.isErrorEnabled() ||
                layoutNumero.isErrorEnabled() ||
                layoutCEP.isErrorEnabled() ||
                layoutBairro.isErrorEnabled() ||
                layoutCidade.isErrorEnabled() ||
                layoutEstado.isErrorEnabled()) {
            Toast.makeText(this, "Confira as Informações!", Toast.LENGTH_SHORT).show();
        } else {
            saveInfos();
        }
    }

    private void instantiateViews() {
        inputNome = findViewById(R.id.inputNomeEmpresa);
        inputCNPJ = findViewById(R.id.inputCNPJ);
        inputEndereco = findViewById(R.id.inputEndereco);
        inputNumero = findViewById(R.id.inputNumero);
        inputBairro = findViewById(R.id.inputBairro);
        inputCEP = findViewById(R.id.inputCEP);
        inputCidade = findViewById(R.id.inputCidade);
        inputEstado = findViewById(R.id.inputUF);

        layoutNome = findViewById(R.id.layoutInputNomeEmpresa);
        layoutCNPJ = findViewById(R.id.layoutInputCNPJ);
        layoutEndereco = findViewById(R.id.layoutInputEndereco);
        layoutNumero = findViewById(R.id.layoutInputNumero);
        layoutBairro = findViewById(R.id.layoutInputBairro);
        layoutCEP = findViewById(R.id.layoutInputCEP);
        layoutCidade = findViewById(R.id.layoutInputCidade);
        layoutEstado = findViewById(R.id.inputLayoutUF);

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);

        navigationView = findViewById(R.id.navView);
        arrayEstadosBR = getResources().getStringArray(R.array.ufs);

        inputCNPJ.addTextChangedListener(Mask.insert(inputCNPJ, "##.###.###/####-##"));
        inputCEP.addTextChangedListener(Mask.insert(inputCEP, "######-##"));
    }

    public void saveInfos() {
        SharedPreferences sharedPreferences = getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("nome", nome);
        editor.putString("cnpj", cnpj);
        editor.putString("endereco", endereco);
        editor.putString("numero", numero);
        editor.putString("bairro", bairro);
        editor.putString("cep", cep);
        editor.putString("cidade", cidade);
        editor.putString("estado", estado);
        editor.apply();
        Toast.makeText(this, "Salvo com Sucesso!", Toast.LENGTH_SHORT).show();
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
