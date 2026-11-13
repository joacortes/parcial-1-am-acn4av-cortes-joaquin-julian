package com.example.cotizadorseguros;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.util.Locale;

public class CondicionesComercialesActivity
        extends AppCompatActivity {

    private EditText inputDescuento;
    private EditText inputRecargo;
    private EditText inputComision;

    private TextView txtPrimaBase;
    private TextView txtPrimaFinal;
    private TextView txtComisionCalculada;

    private double primaBase;
    private double primaFinal;
    private double comisionCalculada;

    private double descuento;
    private double recargo;
    private double porcentajeComision;

    private Intent datosAnteriores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_condiciones_comerciales);

        inputDescuento = findViewById(R.id.inputDescuento);
        inputRecargo = findViewById(R.id.inputRecargo);
        inputComision = findViewById(R.id.inputComision);
        txtPrimaBase = findViewById(R.id.txtPrimaBase);
        txtPrimaFinal = findViewById(R.id.txtPrimaFinal);
        txtComisionCalculada = findViewById(R.id.txtComisionCalculada);

        datosAnteriores = getIntent();
        primaBase = datosAnteriores.getDoubleExtra("prima", 0);
        NumberFormat moneda = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));

        txtPrimaBase.setText("Prima base: " + moneda.format(primaBase));
    }

    public void calcularCondiciones(View view) {
        actualizarCalculo();
    }

    private boolean actualizarCalculo() {
        try {
            double nuevoDescuento = leerPorcentaje(inputDescuento, 100);
            double nuevoRecargo = leerPorcentaje(inputRecargo, Double.MAX_VALUE);
            double nuevaComision = leerPorcentaje(inputComision, 100);

            if (!Double.isFinite(primaBase) || primaBase <= 0) {
                throw new IllegalArgumentException();
            }

            double montoDescuento = primaBase * (nuevoDescuento / 100);
            double montoRecargo = primaBase * (nuevoRecargo / 100);
            double nuevaPrimaFinal = primaBase - montoDescuento + montoRecargo;
            double nuevaComisionCalculada = nuevaPrimaFinal * (nuevaComision / 100);

            if (!Double.isFinite(nuevaPrimaFinal) || !Double.isFinite(nuevaComisionCalculada)) {
                throw new IllegalArgumentException();
            }

            descuento = nuevoDescuento;
            recargo = nuevoRecargo;
            porcentajeComision = nuevaComision;
            primaFinal = nuevaPrimaFinal;
            comisionCalculada = nuevaComisionCalculada;

            NumberFormat moneda = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));

            txtPrimaFinal.setText("Prima final: " + moneda.format(primaFinal));

            txtComisionCalculada.setText("Comisión productor: " + moneda.format(comisionCalculada));

            return true;

        } catch (IllegalArgumentException e) {

            txtPrimaFinal.setText(R.string.prima_pendiente);
            txtComisionCalculada.setText(R.string.comision_pendiente);

            Toast.makeText(this, R.string.error_condiciones, Toast.LENGTH_LONG).show();

            return false;
        }
    }
    private void copiarExtras(Intent origen, Intent destino) {
        if (origen.getExtras() != null) {
            destino.putExtras(origen.getExtras());
        }
    }
    public void continuarCondiciones(View view) {

        if (!actualizarCalculo()) {
            return;
        }

        Intent intent = new Intent(CondicionesComercialesActivity.this, ResumenActivity.class);
        copiarExtras(datosAnteriores, intent);

        intent.putExtra("primaBase", primaBase);
        intent.putExtra("descuento", descuento);
        intent.putExtra("recargo", recargo);
        intent.putExtra("porcentajeComision", porcentajeComision);
        intent.putExtra("prima", primaFinal);
        intent.putExtra("comision", comisionCalculada);

        startActivity(intent);
    }

    private double leerPorcentaje(EditText campo, double maximo) {
        String texto = campo.getText().toString().trim();

        // Un porcentaje opcional vacío equivale a cero.
        if (texto.isEmpty()) {
            return 0;
        }

        if (!texto.matches("[0-9]+([.,][0-9]+)?")) {
            throw new IllegalArgumentException();
        }

        double porcentaje = Double.parseDouble(texto.replace(',', '.'));

        if (!Double.isFinite(porcentaje) || porcentaje < 0 || porcentaje > maximo) {
            throw new IllegalArgumentException();
        }

        return porcentaje;
    }
}