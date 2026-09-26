package com.example.cotizadorseguros;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private Button btnPersonaFisica;
    private Button btnPersonaJuridica;
    private Button btnAgregarTelefono;
    private Button btnGuardar;
    private LinearLayout contenedorDatosTipoPersona;
    private LinearLayout contenedorTelefonos;
    private EditText inputEmail;
    private EditText inputTelefono;
    private EditText inputCalle;
    private EditText inputNumero;
    private EditText inputLocalidad;
    private String tipoPersonaSeleccionado = "";
    private ArrayList<String> telefonosAdicionales = new ArrayList<>();
    private EditText inputNombre;
    private EditText inputApellido;
    private EditText inputDni;
    private EditText inputFechaNacimiento;
    private EditText inputRazonSocial;
    private EditText inputCuit;
    private EditText inputNombreFantasia;

    private EditText crearCampo(int hintResId, int inputType) {
        EditText campo = new EditText(this);
        campo.setHint(hintResId);
        campo.setInputType(inputType);
        campo.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );
        return campo;
    }

    private void mostrarCamposPersonaFisica() {
        contenedorDatosTipoPersona.removeAllViews();
        inputNombre = crearCampo(
                R.string.hint_nombre,
                android.text.InputType.TYPE_CLASS_TEXT |
                        android.text.InputType.TYPE_TEXT_VARIATION_PERSON_NAME
        );
        inputApellido = crearCampo(
                R.string.hint_apellido,
                android.text.InputType.TYPE_CLASS_TEXT |
                        android.text.InputType.TYPE_TEXT_VARIATION_PERSON_NAME
        );
        inputDni = crearCampo(
                R.string.hint_dni,
                android.text.InputType.TYPE_CLASS_NUMBER
        );
        inputFechaNacimiento = crearCampo(
                R.string.hint_fecha_nacimiento,
                android.text.InputType.TYPE_CLASS_DATETIME
        );
        contenedorDatosTipoPersona.addView(inputNombre);
        contenedorDatosTipoPersona.addView(inputApellido);
        contenedorDatosTipoPersona.addView(inputDni);
        contenedorDatosTipoPersona.addView(inputFechaNacimiento);
    }

    private void mostrarCamposPersonaJuridica() {
        contenedorDatosTipoPersona.removeAllViews();
        inputRazonSocial = crearCampo(
                R.string.hint_razon_social,
                android.text.InputType.TYPE_CLASS_TEXT
        );
        inputCuit = crearCampo(
                R.string.hint_cuit,
                android.text.InputType.TYPE_CLASS_NUMBER
        );
        inputNombreFantasia = crearCampo(
                R.string.hint_nombre_fantasia,
                android.text.InputType.TYPE_CLASS_TEXT
        );
        contenedorDatosTipoPersona.addView(inputRazonSocial);
        contenedorDatosTipoPersona.addView(inputCuit);
        contenedorDatosTipoPersona.addView(inputNombreFantasia);
    }

    private void mostrarTelefonosAdicionales() {
        contenedorTelefonos.removeAllViews();
        for (int i = 0; i < telefonosAdicionales.size(); i++) {
            final int posicion = i;
            LinearLayout fila = new LinearLayout(this);
            fila.setOrientation(LinearLayout.HORIZONTAL);
            TextView txtTelefono = new TextView(this);
            txtTelefono.setText(telefonosAdicionales.get(i));
            txtTelefono.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1
                    )
            );

            Button btnEliminar = new Button(this);
            btnEliminar.setText(R.string.btn_eliminar);
            btnEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Primero cambia la memoria
                    telefonosAdicionales.remove(posicion);
                    // Despues actualizamos la UI
                    mostrarTelefonosAdicionales();
                }
            });
            fila.addView(txtTelefono);
            fila.addView(btnEliminar);
            contenedorTelefonos.addView(fila);

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btnPersonaFisica = findViewById(R.id.btnPersonaFisica);
        btnPersonaJuridica = findViewById(R.id.btnPersonaJuridica);
        btnAgregarTelefono = findViewById(R.id.btnAgregarTelefono);
        btnGuardar = findViewById(R.id.btnGuardar);
        contenedorDatosTipoPersona = findViewById(R.id.contenedorDatosTipoPersona);
        contenedorTelefonos = findViewById(R.id.contenedorTelefonos);
        inputEmail = findViewById(R.id.inputEmail);
        inputTelefono = findViewById(R.id.inputTelefono);
        inputCalle = findViewById(R.id.inputCalle);
        inputNumero = findViewById(R.id.inputNumero);
        inputLocalidad = findViewById(R.id.inputLocalidad);

        btnPersonaFisica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipoPersonaSeleccionado = "fisica";
                mostrarCamposPersonaFisica();
            }
        });

        btnPersonaJuridica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipoPersonaSeleccionado = "juridica";
                mostrarCamposPersonaJuridica();
            }
        });

        btnAgregarTelefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String telefono = inputTelefono.getText().toString().trim();
                if (telefono.isEmpty()) {
                    Toast.makeText(
                            MainActivity.this,
                            R.string.error_telefono,
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }
                // 1) CAMBIO EN MEMORIA
                telefonosAdicionales.add(telefono);
                // 2) Limpiamos el campo de entrada
                inputTelefono.setText("");
                // 3) Reflejamos el estado de memoria en la pantalla
                mostrarTelefonosAdicionales();
            }
        });




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}