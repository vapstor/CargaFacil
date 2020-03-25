package br.com.cargafacil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.transition.Fade;
import androidx.transition.TransitionManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;

import br.com.cargafacil.bluetooth.BluetoothService;
import br.com.cargafacil.bluetooth.DevicesActivity;
import br.com.cargafacil.bluetooth.commands.Command;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static br.com.cargafacil.utils.Mask.unmask;
import static br.com.cargafacil.utils.Utils.CONFIG_FILE;
import static br.com.cargafacil.utils.Utils.MY_LOG_TAG;
import static br.com.cargafacil.utils.Utils.MY_PERMISSION_REQUEST_USE_BLUETOOTH;
import static br.com.cargafacil.utils.Utils.formatValuesToString;

public class PesagemActivity extends AppCompatActivity {

    private ProgressBar circularBar;
    private ArrayList<String> dados, dadosEmpresa;
    private AppCompatTextView volView, pesoLiqView, taraView, horaView, pbtView, dataView, numNotaView, placaView;
    private String pesoLiquido, volumeTotal;
    private int fatorVolProd;

    /******************************************************************************************************/
    // Message types sent from the BluetoothService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_STATUS_UI = 5;
    public static final int MESSAGE_CONNECTION_LOST = 6;
    public static final int MESSAGE_UNABLE_CONNECT = 7;
    public static final int MESSAGE_PROGRESS_BAR = 8;
    /*******************************************************************************************************/
    // Key names received from the BluetoothService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String STATUS_UI = "status_ui";
    public static final String PROGRESS_BAR = "progress_bar";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
//    private static final int REQUEST_CHOSE_BMP = 3;
//    private static final int REQUEST_CAMER = 4;
    /******************************************************************************************************/
    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the services
    private BluetoothService mService = null;
    private AppCompatTextView printerStatus;
    private AppCompatButton btnImprimir;

    /***************************   指                 令****************************************************************/

    /*
     *SendDataByte
     */
    private void SendDataByte(byte[] data) {

        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            printerStatus.setText("não conectado");
            return;
        }
        mService.write(data);
    }


    /*
     * SendDataString
     */
    private void sendDataString(String data) {
        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, "Não conectado", Toast.LENGTH_SHORT).show();
            mService.stop();
            finish();
        }
        if (data.length() > 0) {
            try {
                mService.write(data.getBytes("GBK"));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(this, "Erro: UnsupportedEncodingException", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /****************************************************************************************************/
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    Log.i(MY_LOG_TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            btnImprimir.setEnabled(true);
                            btnImprimir.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            printerStatus.setText("conectando");
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            printerStatus.setText("não conectado");
                            toggleCircularBarBtn(false);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    printerStatus.setText("imprimindo...");
                    break;
                case MESSAGE_READ:
//                    Log.d(MY_LOG_TAG, "MESSAGE_READ: "+msg.getData().getInt(MESSAGE_READ));
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Conectado a " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_STATUS_UI:
                    printerStatus.setText(msg.getData().getString(STATUS_UI));
                    break;
                case MESSAGE_CONNECTION_LOST:    //蓝牙已断开连接
                    Toast.makeText(getApplicationContext(), "Conexão perdida", Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_UNABLE_CONNECT:     //无法连接设备
                    Toast.makeText(getApplicationContext(), "Impossível conectar", Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_PROGRESS_BAR:
                    toggleCircularBarBtn(msg.getData().getBoolean(PesagemActivity.PROGRESS_BAR));
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_resultado_pesagem);
        instantiateViews();

        circularBar = findViewById(R.id.progressBar);
        circularBar.setVisibility(View.GONE);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            dados = extras.getStringArrayList("dados");
            dadosEmpresa = extras.getStringArrayList("dadosEmpresa");
        } else {
            Toast.makeText(this, "Erro ao transmitir dados!", Toast.LENGTH_SHORT).show();
            finish();
        }
        calculaPesLiqVol(dados != null ? dados.get(5) : null);
        populateViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mBluetoothAdapter == null) {
            btnImprimir.setEnabled(false);
            printerStatus.setText("bluetooth não suportado");
        } else {
            checkAppPermission();
            if (mBluetoothAdapter.isEnabled()) {
                init();
                if (mService.getState() == BluetoothService.STATE_CONNECTED) {
                    printerStatus.setText("conectado");
                } else {
                    printerStatus.setText("não conectado");
                }
            }
        }
    }

    private void instantiateViews() {
        placaView = findViewById(R.id.placa);
        dataView = findViewById(R.id.data);
        horaView = findViewById(R.id.hora);
        numNotaView = findViewById(R.id.numNota);
        taraView = findViewById(R.id.tara);
        pbtView = findViewById(R.id.pbt);
        pesoLiqView = findViewById(R.id.valorPesoLiquido);
        volView = findViewById(R.id.valorVolume);
        fatorVolProd = getSharedPreferences(CONFIG_FILE, MODE_PRIVATE).getInt("pesoProduto", -1);
        if (fatorVolProd == -1) {
            Toast.makeText(this, "Peso do produto não cadastrado!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Log.d(MY_LOG_TAG, "fatorVolProd = " + fatorVolProd);
        }
        printerStatus = findViewById(R.id.printerStatus);
        btnImprimir = findViewById(R.id.btnImprimir);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void populateViews() {
        placaView.setText(dados.get(0));
        dataView.setText("Data: " + dados.get(1));
        horaView.setText("Hora: " + dados.get(2));
        numNotaView.setText("Nota Nº: " + dados.get(3));
        taraView.setText("Tara: " + dados.get(4));
        pbtView.setText("PBT: " + dados.get(5));
        pesoLiqView.setText(pesoLiquido);
        volView.setText(volumeTotal);
    }

    private void calculaPesLiqVol(String pbtStr) {
        if (pbtStr == null) {
            Toast.makeText(this, "Dados Inválidos!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            pbtStr = unmask(pbtStr);
            String taraStr = unmask(dados.get(4));
            int pbt = Integer.parseInt(pbtStr);
            int tara = Integer.parseInt(taraStr);
            int pesoLiq = pbt - tara;
            Log.d(MY_LOG_TAG, "pesoLiq = " + pesoLiq);
            Log.d(MY_LOG_TAG, "pesoLiq / fatorVolProd [int] = " + (double) pesoLiq / fatorVolProd);

            //calcula volume com 3 casas e troca . por ,
            volumeTotal = String.valueOf(new BigDecimal((double) pesoLiq / fatorVolProd).setScale(3, BigDecimal.ROUND_HALF_EVEN)).replace(".", ",");
            pesoLiquido = formatValuesToString(pesoLiq);
        }
    }

    public void dismissDialog(View v) {
        this.finish();
    }

    private void toggleCircularBarBtn(boolean circularBarVisibility) {
        final ViewGroup root = this.findViewById(R.id.dialog_resultado_calculo);
        Button btn = findViewById(R.id.btnImprimir);
        TransitionManager.beginDelayedTransition(root, new Fade());
        if (circularBarVisibility) {
            circularBar.setVisibility(View.VISIBLE);
            btn.setVisibility(View.GONE);
        } else {
            btn.setVisibility(View.VISIBLE);
            circularBar.setVisibility(View.GONE);
        }
    }

    public void imprimir(View v) {
        toggleCircularBarBtn(true);
        sendDataString(dadosEmpresa.get(0));
        SendDataByte(Command.LF);
        sendDataString(dadosEmpresa.get(1));
        SendDataByte(Command.LF);
        sendDataString(dadosEmpresa.get(2));
        SendDataByte(Command.LF);
        if (dadosEmpresa.get(3) != null && !dadosEmpresa.get(3).equals("")) {
            sendDataString(dadosEmpresa.get(3));
            SendDataByte(Command.LF);
        }
        sendDataString(dadosEmpresa.get(4));
        SendDataByte(Command.LF);
        sendDataString(dadosEmpresa.get(5));
        SendDataByte(Command.LF);
        sendDataString(dadosEmpresa.get(6) + " - " + dadosEmpresa.get(7));

        SendDataByte(Command.LF);
        SendDataByte(Command.LF);
        sendDataString("================================");
        SendDataByte(Command.LF);
        sendDataString("Comprovante de Pesagem");
        SendDataByte(Command.LF);
        sendDataString("================================");
        SendDataByte(Command.LF);
        SendDataByte(Command.LF);

        sendDataString("PLACA:" + dados.get(0));
        SendDataByte(Command.LF);
        sendDataString("NOTA: " + dados.get(3));
        SendDataByte(Command.LF);
        sendDataString("Data: " + dados.get(1));
        SendDataByte(Command.LF);
        sendDataString("Hora: " + dados.get(2));
        SendDataByte(Command.LF);
        sendDataString("Peso Bruto: " + dados.get(5) + "    Ton");
        SendDataByte(Command.LF);
        sendDataString("Tara: " + dados.get(4) + "    Ton");
        SendDataByte(Command.LF);
        sendDataString("Peso Liquido: " + pesoLiquido.replace(".", ",") + "    Ton");
        SendDataByte(Command.LF);
        sendDataString("M3: " + volumeTotal);
        SendDataByte(Command.LF);
        SendDataByte(Command.LF);
        SendDataByte(Command.LF);
        SendDataByte(Command.LF);
        toggleCircularBarBtn(false);
        printerStatus.setText("conectado");
    }

    private void checkAppPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                new MaterialAlertDialogBuilder(this, R.style.customMaterialDialogLight)
                        .setTitle("Nota sobre permissões")
                        .setMessage("Não utilizamos sua localização em momento algum do aplicativo, a permissão é necessária apenas para o correto funcionamento do Bluetooth.\n\nMais informações podem ser facilmente encontradas na documentação oficial do Android.")
                        .setPositiveButton("Ok", (dialog, which) -> {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_COARSE_LOCATION)) {
                                new Thread(() -> runOnUiThread(() -> new MaterialAlertDialogBuilder(this, R.style.customMaterialDialogLight)
                                        .setTitle("Permissão negada")
                                        .setMessage("Você negou as permissões e optou por não as mostrar novamente. Não é possível imprimir, deseja tentar novamente?")
                                        .setPositiveButton("Ok", (dialog2, which2) -> ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION}, MY_PERMISSION_REQUEST_USE_BLUETOOTH))
                                        .setNegativeButton("Cancelar", (dialog2, which2) -> Toast.makeText(this, "Permissões não concedidas", Toast.LENGTH_SHORT).show())
                                        .setCancelable(false)
                                        .show())).start();
                            } else {
                                ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION}, MY_PERMISSION_REQUEST_USE_BLUETOOTH);
                            }
                        }).create().show();
            } else {
                connectToPrinter();
                // Permission has already been granted
            }
        } else {
            //API < 23
            if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)) {
                    new Thread(() -> runOnUiThread(() -> new MaterialAlertDialogBuilder(this, R.style.customMaterialDialogLight)
                            .setTitle("Permissão negada")
                            .setMessage("Você negou as permissões e optou por não as mostrar novamente. Não é possível imprimir, deseja tentar novamente?")
                            .setPositiveButton("Ok", (dialog3, which3) -> ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_USE_BLUETOOTH))
                            .setNegativeButton("Cancelar", (dialog3, which3) -> Toast.makeText(this, "Permissões não concedidas", Toast.LENGTH_SHORT).show())
                            .setCancelable(false)
                            .show())).start();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_USE_BLUETOOTH);
                }
            } else {
                connectToPrinter();
                // Permission has already been granted
            }
        }
    }

    private void connectToPrinter() {
        if (mService != null && mService.getState() == BluetoothService.STATE_CONNECTED) {
            printerStatus.setText("conectado");
            btnImprimir.setEnabled(true);
        } else {
            printerStatus.setText("verificando dispositivos pareados...");
            toggleCircularBarBtn(true);

            // If Bluetooth is not on, request that it be enabled.
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                // Otherwise, setup the session
            } else {
                init();
                if (mService != null && mService.getState() == BluetoothService.STATE_CONNECTED) {
                    printerStatus.setText("conectado");
                    btnImprimir.setEnabled(true);
                } else {
                    Intent serverIntent = new Intent(PesagemActivity.this, DevicesActivity.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSION_REQUEST_USE_BLUETOOTH) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay!
                checkAppPermission();
            } else {
                Toast.makeText(this, "Permissões não concedidas!", Toast.LENGTH_SHORT).show();
            }
        } else {
            throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }

    //private static class AsyncExplanation extends AsyncTask<Void, Integer, Void> {
//    private final WeakReference<AppCompatActivity> weakActivity;
//
//    public AsyncExplanation(AppCompatActivity activity) {
//        weakActivity = new WeakReference<>(activity);
//    }
//
//    @Override
//    protected Void doInBackground(Void... voids) {
//
//    }
//
//    @Override
//    public void onPostExecute(Void result) {
//        // Re-acquire a strong reference to the activity, and verify
//        // that it still exists and is active.
//        AppCompatActivity activity = weakActivity.get();
//        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
//            // activity is no longer valid, don't do anything!
//        } else {
//            // The activity is still valid, do main-thread stuff here
//            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
//        }
//    }
//}

    //Get the results from 1. Enable BT 2. Devices Search
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(MY_LOG_TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE: {
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = Objects.requireNonNull(data.getExtras()).getString(DevicesActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    if (BluetoothAdapter.checkBluetoothAddress(address)) {
                        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                        // Attempt to connect to the device
                        mService.connect(device);
                    }
                } else {
                    printerStatus.setText("não conectado");
                    btnImprimir.setEnabled(false);
                }
                break;
            }
            case REQUEST_ENABLE_BT: {
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a session
                    init();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Log.d(MY_LOG_TAG, "BT not enabled");
                    Toast.makeText(this, "Bluetooth não foi permitido, finalizando", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            }
        }

    }

    //init service
    private void init() {
        mService = new BluetoothService(this, mHandler);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mService != null) {
            if (mService.getState() == BluetoothService.STATE_NONE) {
                // Start the Bluetooth services
                mService.start();
            }
        }
        Log.e(MY_LOG_TAG, "--- ON RESUME ---");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth services
        if (mService != null)
            mService.stop();
        Log.e(MY_LOG_TAG, "--- ON DESTROY ---");
    }


}
