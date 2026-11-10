package com.example.cotizadorseguros;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ResumenActivity extends AppCompatActivity {
    private TextView txtResumenAsegurado;
    private TextView txtResumenVehiculo;
    private TextView txtResumenCobertura;
    private TextView txtResumenPrecio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resumen);

        txtResumenAsegurado = findViewById(R.id.txtResumenAsegurado);
        txtResumenVehiculo = findViewById(R.id.txtResumenVehiculo);
        txtResumenCobertura = findViewById(R.id.txtResumenCobertura);
        txtResumenPrecio = findViewById(R.id.txtResumenPrecio);

        Intent intent = getIntent();

        String tipoPersona = intent.getStringExtra("tipoPersona");

        if ("fisica".equals(tipoPersona)) {

            String nombre = intent.getStringExtra("nombre");
            String apellido = intent.getStringExtra("apellido");
            String dni = intent.getStringExtra("dni");

            txtResumenAsegurado.setText(nombre + " " + apellido + "\nDNI: " + dni);
        }

        if ("juridica".equals(tipoPersona)) {

            String razonSocial = intent.getStringExtra("razonSocial");
            String cuit = intent.getStringExtra("cuit");
            String nombreFantasia = intent.getStringExtra("nombreFantasia");

            txtResumenAsegurado.setText(razonSocial
                    + "\nCUIT: " + cuit
                    + "\nNombre de fantasía: "
                    + nombreFantasia
            );
        }

        String patente = intent.getStringExtra("patente");
        String marca = intent.getStringExtra("marca");
        String modelo = intent.getStringExtra("modelo");
        String anio = intent.getStringExtra("anio");

        String uso = intent.getStringExtra("uso");
        String cobertura = intent.getStringExtra("cobertura");

        double precio = intent.getDoubleExtra("precio", 0);

        txtResumenVehiculo.setText(
                marca + " " + modelo
                        + "\nAño: " + anio
                        + "\nPatente: " + patente
                        + "\nUso: " + uso
        );

        txtResumenCobertura.setText(cobertura);
        txtResumenPrecio.setText("$" + precio);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

                    return insets;
                }
        );
    }
}