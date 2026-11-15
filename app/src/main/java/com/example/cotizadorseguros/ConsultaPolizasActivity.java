package com.example.cotizadorseguros;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.util.Locale;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.firestore.Query;

import java.text.Normalizer;

public class ConsultaPolizasActivity extends AppCompatActivity {

    private LinearLayout contenedorPolizas;
    private TextView txtEstadoPolizas;
    private Spinner spinnerCriterioPolizas;
    private EditText inputBuscarPoliza;
    private Button btnBuscarPoliza;
    private Button btnVerTodasPolizas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_consulta_polizas);

        contenedorPolizas = findViewById(R.id.contenedorPolizas);
        txtEstadoPolizas = findViewById(R.id.txtEstadoPolizas);

        findViewById(R.id.btnVolverInicio)
                .setOnClickListener(v -> finish());

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

        spinnerCriterioPolizas = findViewById(R.id.spinnerCriterioPolizas);
        inputBuscarPoliza = findViewById(R.id.inputBuscarPoliza);
        btnBuscarPoliza = findViewById(R.id.btnBuscarPoliza);
        btnVerTodasPolizas = findViewById(R.id.btnVerTodasPolizas);

        btnBuscarPoliza.setOnClickListener(
                v -> cargarPolizas()
        );

        btnVerTodasPolizas.setOnClickListener(v -> {
            inputBuscarPoliza.setText("");
            cargarPolizas();
        });

        cargarPolizas();
    }

    private void cargarPolizas() {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        if (usuario == null) {
            Toast.makeText(
                    this,
                    R.string.error_sesion_requerida,
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        String valorBusqueda = inputBuscarPoliza
                .getText()
                .toString()
                .trim();

        boolean buscarPorAsegurado =
                spinnerCriterioPolizas.getSelectedItemPosition() == 1;

        String busquedaNormalizada =
                normalizarTexto(valorBusqueda);

        Query consulta = FirebaseFirestore.getInstance()
                .collection("cotizaciones")
                .whereEqualTo("usuarioUid", usuario.getUid())
                .whereEqualTo("estado", "emitida");

        if (!valorBusqueda.isEmpty() && !buscarPorAsegurado) {
            consulta = consulta.whereEqualTo(
                    "numeroPoliza",
                    valorBusqueda
            );
        }

        cambiarEstadoCarga(true);
        contenedorPolizas.removeAllViews();
        txtEstadoPolizas.setText(R.string.polizas_cargando);

        consulta.get()
                .addOnSuccessListener(documentos -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    cambiarEstadoCarga(false);
                    contenedorPolizas.removeAllViews();

                    int coincidencias = 0;

                    for (QueryDocumentSnapshot documento : documentos) {
                        String asegurado;

                        if ("fisica".equals(documento.get("tipoPersona"))) {
                            asegurado = texto(documento, "nombre")
                                    + " "
                                    + texto(documento, "apellido");
                        } else {
                            asegurado = texto(documento, "razonSocial");
                        }

                        if (buscarPorAsegurado
                                && !busquedaNormalizada.isEmpty()
                                && !normalizarTexto(asegurado)
                                .contains(busquedaNormalizada)) {
                            continue;
                        }

                        agregarTarjeta(documento);
                        coincidencias++;
                    }

                    txtEstadoPolizas.setText(
                            coincidencias == 0
                                    ? getString(R.string.polizas_sin_resultados)
                                    : ""
                    );
                })
                .addOnFailureListener(e -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    cambiarEstadoCarga(false);
                    contenedorPolizas.removeAllViews();
                    txtEstadoPolizas.setText(R.string.polizas_error);
                });
    }

    private void agregarTarjeta(DocumentSnapshot documento) {
        String asegurado;

        if ("fisica".equals(documento.get("tipoPersona"))) {
            asegurado = texto(documento, "nombre")
                    + " "
                    + texto(documento, "apellido");
        } else {
            asegurado = texto(documento, "razonSocial");
        }

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

        LinearLayout tarjeta = new LinearLayout(this);
        tarjeta.setOrientation(LinearLayout.VERTICAL);
        tarjeta.setBackgroundResource(R.drawable.fondo_cotizacion);

        int padding = getResources().getDimensionPixelSize(
                R.dimen.margin_normal
        );

        tarjeta.setPadding(padding, padding, padding, padding);

        TextView resumen = new TextView(this);

        resumen.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(R.dimen.texto_normal_size)
        );

        resumen.setText(
                getString(
                        R.string.polizas_tarjeta,
                        texto(documento, "numeroPoliza"),
                        asegurado,
                        texto(documento, "marca"),
                        texto(documento, "modelo"),
                        texto(documento, "patente"),
                        texto(documento, "cobertura"),
                        texto(documento, "vigenciaDesde"),
                        texto(documento, "vigenciaHasta"),
                        primaTexto
                )
        );

        Button btnDetalle = new Button(this);
        btnDetalle.setText(R.string.polizas_ver_detalle);

        btnDetalle.setOnClickListener(v -> {
            Intent intent = new Intent(
                    ConsultaPolizasActivity.this,
                    DetalleCotizacionActivity.class
            );

            intent.putExtra("cotizacionId", documento.getId());
            intent.putExtra("modoPoliza", true);

            startActivity(intent);
        });

        tarjeta.addView(resumen);

        tarjeta.addView(
                btnDetalle,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        LinearLayout.LayoutParams parametros =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        parametros.bottomMargin = padding;

        contenedorPolizas.addView(tarjeta, parametros);
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

    private void cambiarEstadoCarga(boolean cargando) {
        spinnerCriterioPolizas.setEnabled(!cargando);
        inputBuscarPoliza.setEnabled(!cargando);
        btnBuscarPoliza.setEnabled(!cargando);
        btnVerTodasPolizas.setEnabled(!cargando);
    }

    private String normalizarTexto(String texto) {
        if (texto == null) {
            return "";
        }

        return Normalizer.normalize(
                        texto,
                        Normalizer.Form.NFD
                )
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }
}