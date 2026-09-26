package com.example.cotizadorseguros;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}