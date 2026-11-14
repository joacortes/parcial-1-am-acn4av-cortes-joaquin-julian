package com.example.cotizadorseguros;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PolizaActivity extends AppCompatActivity {

    private EditText inputVigencia;
    private Spinner spinnerDuracion;
    private Spinner spinnerFormaPago;
    private Spinner spinnerCondicionImpositiva;

    private long vigenciaMillis = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_poliza);

        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        if (usuario == null) {
            volverAlLogin();
            return;
        }

        inputVigencia = findViewById(R.id.inputVigencia);
        spinnerDuracion = findViewById(R.id.spinnerDuracion);
        spinnerFormaPago = findViewById(R.id.spinnerFormaPago);
        spinnerCondicionImpositiva = findViewById(R.id.spinnerCondicionImpositiva);

        TextView txtProductor = findViewById(R.id.txtProductorPoliza);

        txtProductor.setText(usuario.getEmail());

        if (savedInstanceState != null) {
            vigenciaMillis = savedInstanceState.getLong("vigenciaMillis", -1);

            if (vigenciaMillis != -1) {
                mostrarFecha();
            }
        }

        inputVigencia.setOnClickListener(v -> seleccionarFecha());

        findViewById(R.id.btnContinuarPoliza).setOnClickListener(v -> continuarPoliza());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                    Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());

                    v.setPadding(barras.left, barras.top, barras.right, barras.bottom);

                    return insets;
                }
        );
    }

    private Calendar obtenerHoy() {
        Calendar hoy = Calendar.getInstance();
        hoy.set(Calendar.HOUR_OF_DAY, 0);
        hoy.set(Calendar.MINUTE, 0);
        hoy.set(Calendar.SECOND, 0);
        hoy.set(Calendar.MILLISECOND, 0);
        return hoy;
    }

    private void seleccionarFecha() {
        Calendar fechaInicial = obtenerHoy();

        if (vigenciaMillis >= fechaInicial.getTimeInMillis()) {
            fechaInicial.setTimeInMillis(vigenciaMillis);
        }

        DatePickerDialog dialogo = new DatePickerDialog(
                this,
                (vista, anio, mes, dia) -> {
                    Calendar seleccion = Calendar.getInstance();
                    seleccion.clear();
                    seleccion.set(anio, mes, dia);

                    vigenciaMillis = seleccion.getTimeInMillis();

                    inputVigencia.setError(null);
                    mostrarFecha();
                },
                fechaInicial.get(Calendar.YEAR),
                fechaInicial.get(Calendar.MONTH),
                fechaInicial.get(Calendar.DAY_OF_MONTH)
        );

        dialogo.getDatePicker().setMinDate(obtenerHoy().getTimeInMillis());

        dialogo.show();
    }

    private void mostrarFecha() {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy", new Locale("es", "AR"));

        inputVigencia.setText(formato.format(new Date(vigenciaMillis)));
    }

    private void continuarPoliza() {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        if (usuario == null) {
            volverAlLogin();
            return;
        }

        if (vigenciaMillis < obtenerHoy().getTimeInMillis()) {
            inputVigencia.setError(getString(R.string.poliza_error_fecha));

            Toast.makeText(this, R.string.poliza_error_fecha, Toast.LENGTH_LONG).show();

            return;
        }

        if (spinnerDuracion.getSelectedItemPosition() == 0 || spinnerFormaPago.getSelectedItemPosition() == 0 || spinnerCondicionImpositiva.getSelectedItemPosition() == 0) {

            Toast.makeText(this, R.string.poliza_error_opciones, Toast.LENGTH_LONG).show();

            return;
        }

        int[] mesesPorDuracion = {0, 1, 6, 12};

        int duracionMeses = mesesPorDuracion[spinnerDuracion.getSelectedItemPosition()];

        SimpleDateFormat formatoDatos = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);

        Intent intent = new Intent(PolizaActivity.this, VehiculoActivity.class);

        if (getIntent().getExtras() != null) {
            intent.putExtras(getIntent().getExtras());
        }

        intent.putExtra("vigenciaDesde", formatoDatos.format(new Date(vigenciaMillis)));

        intent.putExtra("duracion", spinnerDuracion.getSelectedItem().toString());

        intent.putExtra("duracionMeses", duracionMeses);

        intent.putExtra("formaPago", spinnerFormaPago.getSelectedItem().toString());

        intent.putExtra("condicionImpositiva", spinnerCondicionImpositiva.getSelectedItem().toString());

        intent.putExtra("productorEmail", usuario.getEmail());

        startActivity(intent);
    }

    private void volverAlLogin() {
        Intent intent = new Intent(this, LoginActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong("vigenciaMillis", vigenciaMillis);
        super.onSaveInstanceState(outState);
    }
}