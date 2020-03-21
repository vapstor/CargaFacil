package br.com.cargafacil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.transition.Fade;
import androidx.transition.TransitionManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import br.com.cargafacil.utils.Mask;

import static br.com.cargafacil.utils.Utils.CONFIG_FILE;
import static br.com.cargafacil.utils.Utils.elapsedTime;
import static br.com.cargafacil.utils.Utils.sha256;

public class MainActivity extends AppCompatActivity implements DrawerLayout.DrawerListener {

    private AppCompatTextView cnpjView, nomeView, enderecoView, numView, bairroView, cepView, cidadeView, estadoView;
    private TextInputLayout placaInputLayout, dataInputLayout, horaInputLayout, numNotaInputLayout, taraInputLayout, pbtInputLayout;
    private AppCompatEditText placaInput, dataInput, horaInput, numNotaInput, taraInput, pbtInput;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private AppCompatImageButton imageButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instanceViews();
        sharedPreferences = getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE);

        //define como falso e mostra diálogo toda vez que abrir o app, até que se cadastre uma senha.
        boolean showDialog = sharedPreferences.getBoolean("showDialogCadastroSenha", true);
        if (showDialog) {
            /*/Dialog para cadastrar user senha**/
            LayoutInflater layoutInflater = getLayoutInflater();
            final View view = layoutInflater.inflate(R.layout.dialog_cadastro_adm, null);
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Cadastrar Administrador");
            alertDialog.setCancelable(false);


            AppCompatEditText inputUser = view.findViewById(R.id.inputUser);
            TextInputLayout inputUserLayout = view.findViewById(R.id.layoutInputUser);
            AppCompatEditText inputPass = view.findViewById(R.id.inputPass);
            TextInputLayout inputPassLayout = view.findViewById(R.id.layoutInputPass);
            AppCompatButton buttonCadastrar = view.findViewById(R.id.btnCadastrar);

            buttonCadastrar.setOnClickListener(v -> {
                String user = "", pass = "";
                if (inputUser.getText() != null) {
                    user = inputUser.getText().toString();
                    if (user.isEmpty()) {
                        inputUserLayout.setErrorEnabled(true);
                        inputUserLayout.setError("preencha este campo");
                    } else {
                        inputUserLayout.setErrorEnabled(false);
                    }
                } else {
                    inputUserLayout.setErrorEnabled(true);
                    inputUserLayout.setError("preencha este campo");
                }
                if (inputPass.getText() != null) {
                    pass = inputPass.getText().toString();
                    if (pass.isEmpty()) {
                        inputPassLayout.setErrorEnabled(true);
                        inputPassLayout.setError("preencha este campo");
                    } else {
                        pass = sha256(pass);
                        inputPassLayout.setErrorEnabled(false);
                    }
                } else {
                    inputPassLayout.setErrorEnabled(true);
                    inputPassLayout.setError("preencha este campo");
                }

                if (inputUserLayout.isErrorEnabled() || inputPassLayout.isErrorEnabled()) {
                    Toast.makeText(this, "Verifique as informações!", Toast.LENGTH_SHORT).show();
                } else {
                    if (efetuarCadastroAdm(user, pass)) {
                        Toast.makeText(this, "Administrador cadastrado", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    } else {
                        Toast.makeText(this, "Ocorreu um erro ao cadastrar", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            alertDialog.setView(view);
            /*/fim dialog cadastrar senha**/

            new MaterialAlertDialogBuilder(this, R.style.Theme_MaterialComponents_Light_Dialog)
                    .setTitle("Cadastrar Administrador")
                    .setMessage("Atualmente o aplicativo não possui um administrador cadastrado. Deseja cadastrar um usuário e uma senha para efetuar alterações no aplicativo posteriormente?\n")
                    .setPositiveButton("Cadastrar", (dialog, which) -> {
                        alertDialog.show();
                    })
                    .setNegativeButton("Não", null)
                    .setNeutralButton("Não mostrar novamente", (dialog, which) -> {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("showDialogCadastroSenha", false);
                        editor.apply();
                    })
                    .show();
        }

        imageButton.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.fecharDrawer);
        drawerLayout.addDrawerListener(toggle);
        drawerLayout.addDrawerListener(this);

        toggle.syncState();
        navigationView.getMenu().getItem(0).setEnabled(false);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            switch (menuItem.getItemId()) {
                case R.id.iniciar_pesagem:
                    break;
                case R.id.calculadora:
                    break;
                case R.id.historico:
                    break;
                case R.id.administrativo:
                    Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this).toBundle();
                    Intent intent = new Intent(MainActivity.this, LoginAdmActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent, bundle);
                    break;
                default:
                    Toast.makeText(this, "Menu Desconhecido!", Toast.LENGTH_SHORT).show();
            }
            return false;
        });

        //populate
        getAndSetDataFromPreferences();
    }

    private void instanceViews() {
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        imageButton = toolbar.findViewById(R.id.infoBtn);

        navigationView = findViewById(R.id.navView);

        nomeView = findViewById(R.id.txtViewValueNome);
        cnpjView = findViewById(R.id.txtViewValueCNPJ);
        enderecoView = findViewById(R.id.txtViewValueEndereco);
        numView = findViewById(R.id.txtViewValueNum);
        bairroView = findViewById(R.id.txtViewValueBairro);
        cepView = findViewById(R.id.txtViewValueCEP);
        cidadeView = findViewById(R.id.txtViewValueCidade);
        estadoView = findViewById(R.id.txtViewValueUF);

        placaInput = findViewById(R.id.placaInput);
        numNotaInput = findViewById(R.id.notaInput);
        dataInput = findViewById(R.id.dataInput);
        horaInput = findViewById(R.id.horaInput);
        taraInput = findViewById(R.id.taraInput);
        pbtInput = findViewById(R.id.pbtInput);

        placaInputLayout = findViewById(R.id.texttInputLayoutPlaca);
        numNotaInputLayout = findViewById(R.id.textInputLayoutNumNota);
        dataInputLayout = findViewById(R.id.textInputLayoutData);
        dataInputLayout.setEndIconOnClickListener(v -> {
            dataInput.setText("");
            String mYear, mMonth, mDay;
            final Calendar c = Calendar.getInstance();
            mYear = String.valueOf(c.get(Calendar.YEAR));
            mMonth = String.valueOf(c.get(Calendar.MONTH) + 1); //calendario começa do 0
            if (mMonth.length() == 1) {
                mMonth = "0" + mMonth;
            }
            mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
            if (mDay.length() == 1) {
                mDay = "0" + mDay;
            }
            dataInput.setText(mDay + mMonth + mYear);

        });
        horaInputLayout = findViewById(R.id.textInputLayoutHora);
        horaInputLayout.setEndIconOnClickListener(v -> {
            horaInput.setText("");
            String mHora, mMinute;
            final Calendar c = Calendar.getInstance();
            mHora = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
            if (mHora.length() == 1) {
                mHora = "0" + mHora;
            }
            mMinute = String.valueOf(c.get(Calendar.MINUTE));
            if (mMinute.length() == 1) {
                mMinute = "0" + mMinute;
            }
//            mSecond = String.valueOf(c.get(Calendar.SECOND));
//            if (mSecond.length() == 1) {
//                mSecond = "0" + mSecond;
//            }
            horaInput.setText(mHora + mMinute);

        });
        taraInputLayout = findViewById(R.id.textInputLayoutTara);
        pbtInputLayout = findViewById(R.id.textInputLayoutPesoBruto);

        placaInput.addTextChangedListener(Mask.insert(placaInput, "###-####"));
        dataInput.addTextChangedListener(Mask.insert(dataInput, "##/##/####"));
        horaInput.addTextChangedListener(Mask.insert(horaInput, "##:##"));
        taraInput.addTextChangedListener(Mask.insert(taraInput, "##,###"));
        pbtInput.addTextChangedListener(Mask.insert(pbtInput, "##,###"));
    }

    private void getAndSetDataFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE);
        //Procura por arquivos gravados no app se não usa os fakes
        String nome = sharedPreferences.getString("nome", "Pedreira Campo Redondo LTDA.");
        String cnpj = sharedPreferences.getString("cnpj", "14.766.790/0001-25");
        String endereco = sharedPreferences.getString("endereco", "Logradouro Campo Redondo");
        String numero = sharedPreferences.getString("numero", "1320");
        String bairro = sharedPreferences.getString("bairro", "Campo Redondo");
        String cep = sharedPreferences.getString("cep", "28940-000");
        String cidade = sharedPreferences.getString("cidade", "São Pedro da Aldeia");
        String estado = sharedPreferences.getString("estado", "RJ");

        nomeView.setText(nome);
        cnpjView.setText(cnpj);
        enderecoView.setText(endereco);
        numView.setText(numero);
        bairroView.setText(bairro);
        cepView.setText(cep);
        cidadeView.setText(cidade);
        estadoView.setText(estado);
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
    protected void onResume() {
        super.onResume();
        getAndSetDataFromPreferences();
    }

    public void validateInfos(View v) {
        String placa, data, hora, numeroNota, tara, pbt;
        //Placa
        if (placaInput.getText() != null) {
            placa = placaInput.getText().toString();
            if (placa.isEmpty()) {
                placaInputLayout.setErrorEnabled(true);
                placaInputLayout.setError("preencha a placa");
            } else if (placa.length() < 8) {
                horaInputLayout.setErrorEnabled(true);
                horaInputLayout.setError("hora inválida");
            } else {
                placaInputLayout.setErrorEnabled(false);
            }
        } else {
            placa = "";
            Toast.makeText(this, "Placa Nulo!", Toast.LENGTH_SHORT).show();
        }

        //data
        if (dataInput.getText() != null) {
            data = dataInput.getText().toString();
            if (data.isEmpty()) {
                dataInputLayout.setErrorEnabled(true);
                dataInputLayout.setError("preencha a data");
            } else if (data.length() < 4) {
                dataInputLayout.setErrorEnabled(true);
                dataInputLayout.setError("data inválida");
            } else {
                dataInputLayout.setErrorEnabled(false);
            }
        } else {
            data = "";
            Toast.makeText(this, "Endereço Nulo!", Toast.LENGTH_SHORT).show();
        }

        //Hora
        if (horaInput.getText() != null) {
            hora = horaInput.getText().toString();
            if (hora.isEmpty()) {
                horaInputLayout.setErrorEnabled(true);
                horaInputLayout.setError("preencha a hora");
            } else if (hora.length() < 4) {
                horaInputLayout.setErrorEnabled(true);
                horaInputLayout.setError("hora inválida");
            } else {
                horaInputLayout.setErrorEnabled(false);
            }
        } else {
            hora = "";
            Toast.makeText(this, "Hora Nula!", Toast.LENGTH_SHORT).show();
        }

        //Numero
        if (numNotaInput.getText() != null) {
            numeroNota = numNotaInput.getText().toString();
            if (numeroNota.isEmpty()) {
                numNotaInputLayout.setErrorEnabled(true);
                numNotaInputLayout.setError("preencha o número da nota");
            } else {
                numNotaInputLayout.setErrorEnabled(false);
            }
        } else {
            numeroNota = "";
            Toast.makeText(this, "Numero Nulo!", Toast.LENGTH_SHORT).show();
        }

        //Tara
        if (taraInput.getText() != null) {
            tara = taraInput.getText().toString();
            if (tara.isEmpty()) {
                taraInputLayout.setErrorEnabled(true);
                taraInputLayout.setError("preencha a tara");
            } else if (tara.length() < 6) {
                taraInputLayout.setErrorEnabled(true);
                taraInputLayout.setError("tara inválida");
            } else {
                taraInputLayout.setErrorEnabled(false);
            }
        } else {
            tara = "";
            Toast.makeText(this, "Tara Nula!", Toast.LENGTH_SHORT).show();
        }

        //PBT
        if (pbtInput.getText() != null) {
            pbt = pbtInput.getText().toString();
            if (pbt.isEmpty()) {
                pbtInputLayout.setErrorEnabled(true);
                pbtInputLayout.setError("preencha o pbt");
            } else if (pbt.length() < 6) {
                pbtInputLayout.setErrorEnabled(true);
                pbtInputLayout.setError("pbt inválido");
            } else {
                pbtInputLayout.setErrorEnabled(false);
            }
        } else {
            pbt = "";
            Toast.makeText(this, "PBT Nulo!", Toast.LENGTH_SHORT).show();
        }

        if (placaInputLayout.isErrorEnabled() ||
                dataInputLayout.isErrorEnabled() ||
                horaInputLayout.isErrorEnabled() ||
                numNotaInputLayout.isErrorEnabled() ||
                taraInputLayout.isErrorEnabled() ||
                pbtInputLayout.isErrorEnabled()) {
            Toast.makeText(this, "Confira as Informações!", Toast.LENGTH_SHORT).show();
        } else {
            int myTara = Integer.parseInt(tara.replace(",", ""));
            int myPbt = Integer.parseInt(pbt.replace(",", ""));
            if (myTara > myPbt) {
                taraInputLayout.setErrorEnabled(true);
                taraInputLayout.setError("tara inválido");
                pbtInputLayout.setErrorEnabled(true);
                pbtInputLayout.setError("pbt inválido");
                Toast.makeText(this, "Tara maior que peso bruto!", Toast.LENGTH_SHORT).show();
            } else {
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this).toBundle();
                if (bundle != null) {
                    ArrayList<String> dados = new ArrayList<>(Arrays.asList(placa, data, hora, numeroNota, tara, pbt));
                    Intent intent = new Intent(MainActivity.this, PesagemActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("dados", dados);
                    startActivity(intent, bundle);
                }
            }
        }

    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (SystemClock.elapsedRealtime() - elapsedTime < 1500) {
                finishAffinity();
            } else {
                Toast.makeText(this, "Toque novamente para sair", Toast.LENGTH_SHORT).show();
            }
            elapsedTime = SystemClock.elapsedRealtime();
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

    public boolean efetuarCadastroAdm(String user, String encryptedPass) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            editor.putBoolean("showDialogCadastroSenha", false);
            editor.putString("user_adm", user);
            editor.putString("pass_adm", encryptedPass);
            editor.apply();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            editor.putBoolean("showDialogCadastroSenha", true);
            editor.putString("user_adm", "");
            editor.putString("pass_adm", "");
            return false;
        }
    }
}
