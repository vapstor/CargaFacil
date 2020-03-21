package br.com.cargafacil;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.transition.Fade;
import androidx.transition.TransitionManager;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class PesagemActivity extends AppCompatActivity {
    ProgressBar circularBar;
    private ArrayList<String> dados;
    private AppCompatTextView volView, pesoLiqView, taraView, horaView, pbtView, dataView, numNotaView, placaView;
    private String pesoLiquido;

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
        } else {
            Toast.makeText(this, "Erro ao transmitir dados!", Toast.LENGTH_SHORT).show();
            finish();
        }
        calculaPesLiqVol(dados != null ? dados.get(5) : null);
        populateViews();
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
    }

    //placa, data, hora, numeroNota, tara, pbt
    private void populateViews() {
        placaView.setText(dados.get(0));
        dataView.setText("Data: " + dados.get(1));
        horaView.setText("Hora: " + dados.get(2));
        numNotaView.setText("Nota Nº: " + dados.get(3));
        taraView.setText("Tara: " + dados.get(4));
        pbtView.setText("PBT: " + dados.get(5));
        pesoLiqView.setText(pesoLiquido);
    }

    private void calculaPesLiqVol(String pbtStr) {
        if (pbtStr == null) {
            Toast.makeText(this, "Dados Inválidos!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            pbtStr = pbtStr.replace(",", "");
            String taraStr = dados.get(4).replace(",", "");
            int pbt = Integer.parseInt(pbtStr);
            int tara = Integer.parseInt(taraStr);
            int pesoLiq = pbt - tara;
            pesoLiquido = String.valueOf(pesoLiq);
        }
    }

    public void dismissDialog(View v) {
        this.finish();
    }

    private void toggle() {
        final ViewGroup root = this.findViewById(R.id.dialog_resultado_calculo);
        Button btn = findViewById(R.id.btnImprimir);
        TransitionManager.beginDelayedTransition(root, new Fade());
        if (btn.getVisibility() == View.VISIBLE) {
            btn.setVisibility(View.GONE);
        } else {
            btn.setVisibility(View.VISIBLE);
        }
    }

    public void imprimir(View v) {
        AsyncCircular asyncCircular = new AsyncCircular();
        asyncCircular.execute();
    }

    public class AsyncCircular extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("VAPSTOR", "PRE EXECUTE");
            toggle();
            circularBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("VAPSTOR", "START BACKGROUND");
            for (int i = 0; i < 15; i++) {
                try {
                    sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.d("VAPSTOR", "BACKGROUND FEITO");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            circularBar.setVisibility(View.GONE);
            toggle();
            Log.d("VAPSTOR", "POST EXECUTE");
        }
    }
}
