package com.example.cotizadorseguros;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.widget.Toast;

public class VehiculoActivity extends AppCompatActivity {
    private EditText inputUso;
    private EditText inputPatente;
    private EditText inputMarca;
    private EditText inputModelo;
    private EditText inputAnio;

    private String tipoPersona;

    private String nombre;
    private String apellido;
    private String dni;

    private String razonSocial;
    private String cuit;
    private String nombreFantasia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_vehiculo);

        inputPatente = findViewById(R.id.inputPatente);
        inputMarca = findViewById(R.id.inputMarca);
        inputModelo = findViewById(R.id.inputModelo);
        inputAnio = findViewById(R.id.inputAnio);
        inputUso = findViewById(R.id.inputUso);

        TextView txtAsegurado = findViewById(R.id.txtAsegurado);

        Intent intent = getIntent();

        tipoPersona = intent.getStringExtra("tipoPersona");

        if ("fisica".equals(tipoPersona)) {

            nombre = intent.getStringExtra("nombre");
            apellido = intent.getStringExtra("apellido");
            dni = intent.getStringExtra("dni");

            txtAsegurado.setText(
                    nombre + " " + apellido + " - DNI " + dni
            );
        }

        if ("juridica".equals(tipoPersona)) {

            razonSocial = intent.getStringExtra("razonSocial");
            cuit = intent.getStringExtra("cuit");
            nombreFantasia = intent.getStringExtra("nombreFantasia");

            txtAsegurado.setText(
                    razonSocial + " - CUIT " + cuit
            );
        }

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

    }

    public void continuarVehiculo(View view) {
        String patente = inputPatente.getText().toString().trim();
        String marca = inputMarca.getText().toString().trim();
        String modelo = inputModelo.getText().toString().trim();
        String anio = inputAnio.getText().toString().trim();
        String uso = inputUso.getText().toString().trim();

        if (patente.isEmpty()
                || marca.isEmpty()
                || modelo.isEmpty()
                || anio.isEmpty()
                || uso.isEmpty()) {

            Toast.makeText(
                    this,
                    "Completá todos los datos del vehículo",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Intent intent = new Intent(
                VehiculoActivity.this,
                CoberturaActivity.class
        );

        intent.putExtra("tipoPersona", tipoPersona);

        if ("fisica".equals(tipoPersona)) {

            intent.putExtra("nombre", nombre);
            intent.putExtra("apellido", apellido);
            intent.putExtra("dni", dni);
        }

        if ("juridica".equals(tipoPersona)) {

            intent.putExtra("razonSocial", razonSocial);
            intent.putExtra("cuit", cuit);
            intent.putExtra("nombreFantasia", nombreFantasia);
        }

        intent.putExtra("patente", patente);
        intent.putExtra("marca", marca);
        intent.putExtra("modelo", modelo);
        intent.putExtra("anio", anio);
        intent.putExtra("uso", uso);

        startActivity(intent);
    }
}