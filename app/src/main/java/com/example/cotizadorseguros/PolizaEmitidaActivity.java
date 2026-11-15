package com.example.cotizadorseguros;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.Locale;

public class PolizaEmitidaActivity extends AppCompatActivity {

    private TextView txtDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_poliza_emitida);

        txtDatos = findViewById(R.id.txtDatosPolizaEmitida);

        findViewById(R.id.btnVolverInicio)
                .setOnClickListener(v -> volverAlInicio());

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {
                    Insets barras = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    v.setPadding(
                            barras.left,
                            barras.top,
                            barras.right,
                            barras.bottom
                    );

                    return insets;
                }
        );

        cargarPoliza();
    }

    private void cargarPoliza() {
        FirebaseUser usuario =
                FirebaseAuth.getInstance().getCurrentUser();

        if (usuario == null) {
            txtDatos.setText(R.string.error_sesion_requerida);
            return;
        }

        String cotizacionId =
                getIntent().getStringExtra("cotizacionId");

        if (cotizacionId == null
                || cotizacionId.trim().isEmpty()
                || cotizacionId.contains("/")
                || cotizacionId.equals(".")
                || cotizacionId.equals("..")) {

            txtDatos.setText(R.string.emitida_error_carga);
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("cotizaciones")
                .document(cotizacionId)
                .get()
                .addOnSuccessListener(documento -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    if (!documento.exists()
                            || !usuario.getUid().equals(
                            documento.get("usuarioUid")
                    )
                            || !"emitida".equals(
                            documento.get("estado")
                    )) {

                        txtDatos.setText(
                                R.string.emitida_error_carga
                        );
                        return;
                    }

                    mostrarPoliza(documento);
                })
                .addOnFailureListener(e -> {
                    if (!isFinishing() && !isDestroyed()) {
                        txtDatos.setText(
                                R.string.emitida_error_carga
                        );
                    }
                });
    }

    private void mostrarPoliza(DocumentSnapshot documento) {
        String asegurado;

        if ("fisica".equals(documento.get("tipoPersona"))) {
            asegurado = texto(documento, "nombre")
                    + " "
                    + texto(documento, "apellido");
        } else {
            asegurado = texto(documento, "razonSocial");
        }

        String vehiculo = texto(documento, "marca")
                + " "
                + texto(documento, "modelo")
                + " ("
                + texto(documento, "anio")
                + ")";

        String primaTexto =
                getString(R.string.dato_no_disponible);

        Object prima = documento.get("prima");

        if (prima instanceof Number) {
            NumberFormat moneda =
                    NumberFormat.getCurrencyInstance(
                            new Locale("es", "AR")
                    );

            primaTexto = moneda.format(
                    ((Number) prima).doubleValue()
            );
        }

        txtDatos.setText(
                getString(
                        R.string.emitida_datos,
                        texto(documento, "numeroPoliza"),
                        asegurado,
                        vehiculo,
                        texto(documento, "patente"),
                        texto(documento, "cobertura"),
                        texto(documento, "vigenciaDesde"),
                        texto(documento, "vigenciaHasta"),
                        primaTexto
                )
        );
    }

    private String texto(
            DocumentSnapshot documento,
            String campo
    ) {
        Object valor = documento.get(campo);

        if (valor instanceof String
                && !((String) valor).trim().isEmpty()) {
            return (String) valor;
        }

        return getString(R.string.dato_no_disponible);
    }

    private void volverAlInicio() {
        Intent intent = new Intent(
                PolizaEmitidaActivity.this,
                InicioActivity.class
        );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}