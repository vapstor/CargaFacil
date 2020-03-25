package br.com.cargafacil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.Locale;

import static br.com.cargafacil.utils.Utils.CONFIG_FILE;
import static br.com.cargafacil.utils.Utils.elapsedTime;
import static br.com.cargafacil.utils.Utils.formatValuesToInteger;
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
    private TextWatcher dataInputListener, horaInputListener;
    private String nome, cnpj, endereco, numero, bairro, cep, cidade, estado;
//    placaInputListener, dataInputListener, horaInputListener, taraInputListener, pbtInputListener;

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
//            alertDialog.setTitle("Cadastrar Administrador");
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
                        Toast.makeText(this, "Administrador cadastrado", Toast.LENGTH_LONG).show();
                        alertDialog.dismiss();
                    } else {
                        Toast.makeText(this, "Ocorreu um erro ao cadastrar", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            alertDialog.setView(view);
            /*/fim dialog cadastrar senha**/

            new MaterialAlertDialogBuilder(this, R.style.customMaterialDialogLight)
                    .setTitle("Cadastrar Administrador")
                    .setMessage("Atualmente o aplicativo não possui um administrador cadastrado. Deseja cadastrar um usuário e uma senha para efetuar alterações no aplicativo posteriormente?\n")
                    .setPositiveButton("Cadastrar", (dialog, which) -> alertDialog.show())
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

//        dataInputListener = Mask.insert(dataInput, "##/##/####");
//        placaInputListener = Mask.insert(placaInput, "###-####");
//        horaInputListener = Mask.insert(horaInput, "##:##");
        setDataListener();
        setHorarioListener();
//        taraInputListener = Mask.insert(taraInput);
//        pbtInputListener = Mask.insert(pbtInput);
    }

    private void getAndSetDataFromPreferences() {
        new Thread(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE);
            //Procura por arquivos gravados no app se não usa os fakes
            nome = sharedPreferences.getString("nome", "Pedreira Campo Redondo LTDA.");
            cnpj = sharedPreferences.getString("cnpj", "14.766.790/0001-25");
            endereco = sharedPreferences.getString("endereco", "Logradouro Campo Redondo");
            numero = sharedPreferences.getString("numero", "1320");
            bairro = sharedPreferences.getString("bairro", "Campo Redondo");
            cep = sharedPreferences.getString("cep", "28940-000");
            cidade = sharedPreferences.getString("cidade", "São Pedro da Aldeia");
            estado = sharedPreferences.getString("estado", "RJ");

            runOnUiThread(() -> {
                nomeView.setText(nome);
                cnpjView.setText(cnpj);
                enderecoView.setText(endereco);
                numView.setText(numero);
                bairroView.setText(bairro);
                cepView.setText(cep);
                cidadeView.setText(cidade);
                estadoView.setText(estado);
            });
        }).start();
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
    protected void onStart() {
        super.onStart();
//        placaInput.addTextChangedListener(Mask.insert(placaInput, "###-####"));
        dataInput.addTextChangedListener(dataInputListener);
        horaInput.addTextChangedListener(horaInputListener);
//        taraInput.addTextChangedListener(taraInputListener);
//        pbtInput.addTextChangedListener(pbtInputListener);
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
            } else if (placa.length() < 7) {
                placaInputLayout.setErrorEnabled(true);
                placaInputLayout.setError("placa inválida");
            } else {
                placaInputLayout.setErrorEnabled(false);
            }
        } else {
            placa = "";
            placaInputLayout.setErrorEnabled(true);
            Toast.makeText(this, "Placa Nulo!", Toast.LENGTH_SHORT).show();
        }

        //data
        if (dataInput.getText() != null) {
            data = dataInput.getText().toString();
            if (data.isEmpty()) {
                dataInputLayout.setErrorEnabled(true);
                dataInputLayout.setError("preencha a data");
            } else if (data.contains("d") || data.contains("m") || data.contains("a")) {
                dataInputLayout.setErrorEnabled(true);
                dataInputLayout.setError("data inválida");
            } else {
                dataInputLayout.setErrorEnabled(false);
            }
        } else {
            data = "";
            dataInputLayout.setErrorEnabled(true);
            Toast.makeText(this, "Data Nula!", Toast.LENGTH_SHORT).show();
        }

        //Hora
        if (horaInput.getText() != null) {
            hora = horaInput.getText().toString();
            if (hora.isEmpty()) {
                horaInputLayout.setErrorEnabled(true);
                horaInputLayout.setError("preencha a hora");
            } else if (hora.contains("h") || hora.contains("m")) {
                horaInputLayout.setErrorEnabled(true);
                horaInputLayout.setError("hora inválida");
            } else {
                horaInputLayout.setErrorEnabled(false);
            }
        } else {
            hora = "";
            horaInputLayout.setErrorEnabled(true);
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
            numNotaInputLayout.setErrorEnabled(true);
            Toast.makeText(this, "Numero Nulo!", Toast.LENGTH_SHORT).show();
        }

        //Tara
        if (taraInput.getText() != null) {
            tara = taraInput.getText().toString();
            if (tara.isEmpty()) {
                taraInputLayout.setErrorEnabled(true);
                taraInputLayout.setError("preencha a tara");
//            } else if (tara.length() < 6) {
//                taraInputLayout.setErrorEnabled(true);
//                taraInputLayout.setError("tara inválida");
            } else {
                taraInputLayout.setErrorEnabled(false);
            }
        } else {
            tara = "";
            taraInputLayout.setErrorEnabled(true);
            Toast.makeText(this, "Tara Nula!", Toast.LENGTH_SHORT).show();
        }

        //PBT
        if (pbtInput.getText() != null) {
            pbt = pbtInput.getText().toString();
            if (pbt.isEmpty()) {
                pbtInputLayout.setErrorEnabled(true);
                pbtInputLayout.setError("preencha o pbt");
//            } else if (pbt.length() < 6) {
//                pbtInputLayout.setErrorEnabled(true);
//                pbtInputLayout.setError("pbt inválido");
            } else {
                pbtInputLayout.setErrorEnabled(false);
            }
        } else {
            pbt = "";
            pbtInputLayout.setErrorEnabled(true);
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
            int myTara = formatValuesToInteger(tara);
            int myPbt = formatValuesToInteger(pbt);
            if (myTara > myPbt) {
                taraInputLayout.setErrorEnabled(true);
                taraInputLayout.setError("tara inválido");
                pbtInputLayout.setErrorEnabled(true);
                pbtInputLayout.setError("pbt inválido");
                Toast.makeText(this, "Tara maior que peso bruto!", Toast.LENGTH_SHORT).show();
            } else {
                int fatorVolProd = getSharedPreferences(CONFIG_FILE, MODE_PRIVATE).getInt("pesoProduto", -1);
                if (fatorVolProd == -1) {
                    Toast.makeText(this, "Peso do produto não cadastrado!", Toast.LENGTH_LONG).show();
                } else {
                    Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this).toBundle();
                    if (bundle != null) {
                        ArrayList<String> dados = new ArrayList<>(Arrays.asList(placa, data, hora, numeroNota, tara, pbt));
                        ArrayList<String> dadosEmpresa = new ArrayList<>(Arrays.asList(nome, cnpj, endereco, numero, bairro, cep, cidade, estado));
                        Intent intent = new Intent(MainActivity.this, PesagemActivity.class);
                        intent.putExtra("dados", dados);
                        intent.putExtra("dadosEmpresa", dadosEmpresa);
                        startActivity(intent, bundle);
                    }
                }
            }
        }

    }

    private void setDataListener() {
        dataInputListener = new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "ddmmaaaa";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day = Integer.parseInt(clean.substring(0, 2));
                        int mon = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon - 1);
                        year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                        clean = String.format("%02d%02d%02d", day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    dataInput.setText(current);
                    dataInput.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    private void setHorarioListener() {
        horaInputListener = new TextWatcher() {
            private String current = "";
            private String hhmm = "hhmm";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 4; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 4) {
                        clean = clean + hhmm.substring(clean.length());
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int hours = Integer.parseInt(clean.substring(0, 2));
                        int minutes = Integer.parseInt(clean.substring(2, 4));

                        minutes = (minutes < 1) ? 0 : minutes > 59 ? 00 : minutes;
                        cal.set(Calendar.MINUTE, minutes);
                        hours = (hours > cal.getActualMaximum(Calendar.HOUR_OF_DAY)) ? cal.getActualMaximum(Calendar.HOUR_OF_DAY) : hours;
                        clean = String.format(Locale.getDefault(), "%02d%02d", hours, minutes);
                    }

                    clean = String.format(Locale.getDefault(), "%s:%s", clean.substring(0, 2), clean.substring(2, 4));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    horaInput.setText(current);
                    horaInput.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    public void resetInfos(View v) {
        new MaterialAlertDialogBuilder(this, R.style.customMaterialDialogLight)
                .setTitle("Atenção")
                .setMessage("Deseja realmente limpar todas os dados?")
                .setPositiveButton("limpar", (dialog, which) -> {
                    placaInput.setText("");
                    dataInput.setText("");
                    horaInput.setText("");
                    numNotaInput.setText("");
                    taraInput.setText("");
                    pbtInput.setText("");
                    placaInput.requestFocus();
                    Toast.makeText(this, "Dados limpos", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                })
                .create().show();
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
