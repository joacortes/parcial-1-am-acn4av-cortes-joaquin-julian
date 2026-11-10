package com.example.cotizadorseguros;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

            txtResumenAsegurado.setText(getString(R.string.resumen_persona_fisica, nombre, apellido, dni));
        }

        if ("juridica".equals(tipoPersona)) {

            String razonSocial = intent.getStringExtra("razonSocial");
            String cuit = intent.getStringExtra("cuit");
            String nombreFantasia = intent.getStringExtra("nombreFantasia");

            txtResumenAsegurado.setText(getString(R.string.resumen_persona_juridica, razonSocial, cuit, nombreFantasia));
        }

        String patente = intent.getStringExtra("patente");
        String marca = intent.getStringExtra("marca");
        String modelo = intent.getStringExtra("modelo");
        String anio = intent.getStringExtra("anio");

        String uso = intent.getStringExtra("uso");
        String cobertura = intent.getStringExtra("cobertura");

        double precio = intent.getDoubleExtra("precio", 0);

        txtResumenVehiculo.setText(getString(R.string.resumen_vehiculo, marca, modelo, anio, patente, uso));

        txtResumenCobertura.setText(cobertura);
        txtResumenPrecio.setText(getString(R.string.resumen_precio, precio));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

                    return insets;
                }
        );
    }

    public void guardarCotizacion(View view) {

        Toast.makeText(
                this,
                "Cotización guardada correctamente",
                Toast.LENGTH_LONG
        ).show();
    }
}