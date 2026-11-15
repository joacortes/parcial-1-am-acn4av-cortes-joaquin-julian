package com.example.cotizadorseguros;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.NumberFormat;
import java.util.Locale;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.Query;
import android.content.Intent;
import android.widget.Spinner;
import java.text.Normalizer;

public class ConsultaCotizacionesActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout contenedorCotizaciones;
    private EditText inputBuscarCotizacion;
    private Button btnBuscarCotizacion;
    private Button btnVerTodasCotizaciones;
    private TextView txtEstadoConsulta;
    private Spinner spinnerCriterioBusqueda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_consulta_cotizaciones);

        db = FirebaseFirestore.getInstance();

        contenedorCotizaciones = findViewById(R.id.contenedorCotizaciones);
        inputBuscarCotizacion = findViewById(R.id.inputBuscarCotizacion);

        btnBuscarCotizacion = findViewById(R.id.btnBuscarCotizacion);
        btnVerTodasCotizaciones = findViewById(R.id.btnVerTodasCotizaciones);
        txtEstadoConsulta = findViewById(R.id.txtEstadoConsulta);

        btnBuscarCotizacion.setOnClickListener(v -> cargarCotizaciones());

        btnVerTodasCotizaciones.setOnClickListener(v -> {
            inputBuscarCotizacion.setText("");
            cargarCotizaciones();
        });

        spinnerCriterioBusqueda = findViewById(R.id.spinnerCriterioBusqueda);
        cargarCotizaciones();
    }

    private void cargarCotizaciones() {

        FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();

        if (usuarioActual == null) {
            Toast.makeText(this, R.string.error_sesion_requerida, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        String valorBusqueda = inputBuscarCotizacion.getText().toString().trim();

        boolean buscarPorAsegurado = spinnerCriterioBusqueda.getSelectedItemPosition() == 1;

        String busquedaNormalizada =
                normalizarTexto(valorBusqueda);

        if (!buscarPorAsegurado && (valorBusqueda.contains("/") || valorBusqueda.equals(".") || valorBusqueda.equals(".."))) {

            inputBuscarCotizacion.setError(getString(R.string.consulta_numero_invalido));

            return;
        }

        inputBuscarCotizacion.setError(null);

        Query consulta = db.collection("cotizaciones")
                .whereEqualTo(
                        "usuarioUid",
                        usuarioActual.getUid()
                );

        if (!valorBusqueda.isEmpty() && !buscarPorAsegurado) {
            consulta = consulta.whereEqualTo(FieldPath.documentId(), valorBusqueda);
        }

        cambiarEstadoCarga(true);
        contenedorCotizaciones.removeAllViews();
        txtEstadoConsulta.setText(R.string.consulta_cargando);



        consulta.get().addOnSuccessListener(queryDocumentSnapshots -> {

            cambiarEstadoCarga(false);
            contenedorCotizaciones.removeAllViews();
            txtEstadoConsulta.setText("");

            int coincidencias = 0;

            int coincidencias = 0;

            for (QueryDocumentSnapshot documento : queryDocumentSnapshots) {

                String tipoPersona = documento.getString("tipoPersona");
                String patente = documento.getString("patente");
                String marca = documento.getString("marca");
                String modelo = documento.getString("modelo");
                String cobertura = documento.getString("cobertura");
                Double sumaAsegurada = documento.getDouble("sumaAsegurada");
                Double prima = documento.getDouble("prima");
                NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));
                Double primaBase = documento.getDouble("primaBase");
                Double descuento = documento.getDouble("descuento");
                Double recargo = documento.getDouble("recargo");
                Double porcentajeComision = documento.getDouble("porcentajeComision");
                Double comision = documento.getDouble("comision");

                String sumaAseguradaTexto = sumaAsegurada != null
                        ? formatoMoneda.format(sumaAsegurada)
                        : "No disponible";

                String primaBaseTexto = primaBase != null
                                ? formatoMoneda.format(primaBase)
                                : "No disponible";

                String primaTexto = prima != null
                        ? formatoMoneda.format(prima)
                        : "No disponible";

                String comisionTexto = comision != null
                                ? formatoMoneda.format(comision)
                                : "No disponible";

                String descuentoTexto = descuento != null
                                ? descuento + "%"
                                : "No disponible";

                String recargoTexto = recargo != null
                                ? recargo + "%"
                                : "No disponible";

                String porcentajeComisionTexto = porcentajeComision != null
                                ? porcentajeComision + "%"
                                : "No disponible";

                String asegurado = "";

                if ("fisica".equals(tipoPersona)) {

                    String nombre = documento.getString("nombre");
                    String apellido = documento.getString("apellido");
                    asegurado = nombre + " " + apellido;
                }

                if ("juridica".equals(tipoPersona)) {
                    asegurado = documento.getString("razonSocial");
                }

                if (buscarPorAsegurado && !busquedaNormalizada.isEmpty() && !normalizarTexto(asegurado).contains(busquedaNormalizada)) {
                    continue;
                }

                coincidencias++;

                TextView txtCotizacion = new TextView(this);
                Button btnDetalle = new Button(this);
                btnDetalle.setText(R.string.detalle_cotizacion_ver);

                btnDetalle.setOnClickListener(v -> {
                    Intent intent = new Intent(
                            ConsultaCotizacionesActivity.this,
                            DetalleCotizacionActivity.class
                    );

                    intent.putExtra("cotizacionId", documento.getId());

                    startActivity(intent);
                });

                contenedorCotizaciones.addView(
                        btnDetalle,
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                );

                txtCotizacion.setText(getString(R.string.consulta_numero_tarjeta, documento.getId())
                        + "\n\n"
                        + asegurado
                        + "\n"
                        + marca + " " + modelo
                        + "\nPatente: " + patente
                        + "\n"
                        + "\nSuma asegurada: " + sumaAseguradaTexto
                        + "\nCobertura: " + cobertura
                        + "\n"
                        + "\nPrima base: " + primaBaseTexto
                        + "\nDescuento: " + descuentoTexto
                        + "\nRecargo: " + recargoTexto
                        + "\nPrima final: " + primaTexto
                        + "\n"
                        + "\nComisión: " + porcentajeComisionTexto
                        + "\nImporte comisión: " + comisionTexto
                );

                txtCotizacion.setTextIsSelectable(true);
                txtCotizacion.setTextSize(16);

                txtCotizacion.setPadding(
                        16,
                        16,
                        16,
                        16
                );

                txtCotizacion.setBackgroundResource(
                        R.drawable.fondo_cotizacion
                );

                LinearLayout.LayoutParams parametros =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );

                parametros.setMargins(
                        0,
                        0,
                        0,
                        24
                );

                txtCotizacion.setLayoutParams(parametros);
                contenedorCotizaciones.addView(
                        txtCotizacion
                );
            }


            txtEstadoConsulta.setText(
                    coincidencias == 0
                            ? getString(R.string.consulta_sin_resultados)
                            : ""
            );
        })
        .addOnFailureListener(e -> {
            cambiarEstadoCarga(false);
            contenedorCotizaciones.removeAllViews();
            txtEstadoConsulta.setText(R.string.consulta_error);
        });
    }

    private void cambiarEstadoCarga(boolean cargando) {
        inputBuscarCotizacion.setEnabled(!cargando);
        btnBuscarCotizacion.setEnabled(!cargando);
        btnVerTodasCotizaciones.setEnabled(!cargando);
        spinnerCriterioBusqueda.setEnabled(!cargando);
    }

    private String normalizarTexto(String texto) {
        if (texto == null) {
            return "";
        }

        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }
}