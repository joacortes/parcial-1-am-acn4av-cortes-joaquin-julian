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

public class CoberturaActivity extends AppCompatActivity {

    private TextView txtDatosVehiculo;
    private TextView txtCoberturaSeleccionada;

    private String tipoPersona;

    private String nombre;
    private String apellido;
    private String dni;

    private String razonSocial;
    private String cuit;
    private String nombreFantasia;

    private String patente;
    private String marca;
    private String modelo;
    private String anio;
    private String uso;

    private String coberturaSeleccionada = "";
    private double precioSeleccionado = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_cobertura);

        txtDatosVehiculo = findViewById(R.id.txtDatosVehiculo);
        txtCoberturaSeleccionada = findViewById(R.id.txtCoberturaSeleccionada);

        Intent intent = getIntent();

        tipoPersona = intent.getStringExtra("tipoPersona");

        if ("fisica".equals(tipoPersona)) {
            nombre = intent.getStringExtra("nombre");
            apellido = intent.getStringExtra("apellido");
            dni = intent.getStringExtra("dni");
        }

        if ("juridica".equals(tipoPersona)) {
            razonSocial = intent.getStringExtra("razonSocial");
            cuit = intent.getStringExtra("cuit");
            nombreFantasia = intent.getStringExtra("nombreFantasia");
        }

        patente = intent.getStringExtra("patente");
        marca = intent.getStringExtra("marca");
        modelo = intent.getStringExtra("modelo");
        anio = intent.getStringExtra("anio");
        uso = intent.getStringExtra("uso");

        txtDatosVehiculo.setText(
                marca + " " + modelo
                        + " - " + anio
                        + " - Patente " + patente
        );

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

    public void seleccionarResponsabilidadCivil(View view) {

        coberturaSeleccionada = "Responsabilidad Civil";
        precioSeleccionado = 45000;

        actualizarCoberturaSeleccionada();
    }

    public void seleccionarTercerosCompleto(View view) {

        coberturaSeleccionada = "Terceros Completo";
        precioSeleccionado = 72500;

        actualizarCoberturaSeleccionada();
    }

    public void seleccionarTodoRiesgo(View view) {

        coberturaSeleccionada = "Todo Riesgo";
        precioSeleccionado = 118900;

        actualizarCoberturaSeleccionada();
    }

    private void actualizarCoberturaSeleccionada() {

        txtCoberturaSeleccionada.setText(
                "Cobertura seleccionada: "
                        + coberturaSeleccionada
                        + " - $"
                        + precioSeleccionado
        );

        Toast.makeText(
                this,
                "Cobertura seleccionada correctamente",
                Toast.LENGTH_SHORT
        ).show();
    }
}