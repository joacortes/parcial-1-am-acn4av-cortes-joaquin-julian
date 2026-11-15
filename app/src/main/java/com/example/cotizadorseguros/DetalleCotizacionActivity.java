package com.example.cotizadorseguros;

import android.os.Bundle;
import android.text.TextUtils;
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
import java.util.List;
import java.util.Locale;
import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

public class DetalleCotizacionActivity extends AppCompatActivity {

    private TextView txtDetalle;
    private DocumentSnapshot cotizacionCargada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle_cotizacion);

        txtDetalle = findViewById(R.id.txtDetalleCotizacion);

        findViewById(R.id.btnVolverConsulta)
                .setOnClickListener(v -> finish());

        findViewById(R.id.btnModificarCondiciones)
                .setOnClickListener(v -> modificarCondiciones());

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

        cargarCotizacion();
    }

    private void cargarCotizacion() {
        FirebaseUser usuario =
                FirebaseAuth.getInstance().getCurrentUser();

        if (usuario == null) {
            txtDetalle.setText(R.string.error_sesion_requerida);
            return;
        }

        String cotizacionId =
                getIntent().getStringExtra("cotizacionId");

        if (cotizacionId == null
                || cotizacionId.trim().isEmpty()
                || cotizacionId.contains("/")
                || cotizacionId.equals(".")
                || cotizacionId.equals("..")) {

            txtDetalle.setText(
                    R.string.detalle_cotizacion_no_disponible
            );
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
                    )) {

                        txtDetalle.setText(
                                R.string.detalle_cotizacion_no_disponible
                        );
                        return;
                    }

                    mostrarCotizacion(documento);
                    cotizacionCargada = documento;

                    findViewById(R.id.btnModificarCondiciones).setEnabled(true);
                })
                .addOnFailureListener(e -> {
                    if (!isFinishing() && !isDestroyed()) {
                        txtDetalle.setText(
                                R.string.detalle_cotizacion_error
                        );
                    }
                });
    }

    private void mostrarCotizacion(DocumentSnapshot documento) {
        StringBuilder detalle = new StringBuilder();

        detalle.append(
                getString(
                        R.string.detalle_cotizacion_numero,
                        documento.getId()
                )
        );

        detalle.append("\n\n");

        if ("fisica".equals(documento.get("tipoPersona"))) {
            detalle.append(
                    getString(
                            R.string.detalle_persona_fisica,
                            texto(documento, "nombre"),
                            texto(documento, "apellido"),
                            texto(documento, "dni")
                    )
            );
        } else {
            detalle.append(
                    getString(
                            R.string.detalle_persona_juridica,
                            texto(documento, "razonSocial"),
                            texto(documento, "cuit"),
                            texto(documento, "nombreFantasia")
                    )
            );
        }

        detalle.append("\n\n");

        detalle.append(
                getString(
                        R.string.detalle_contacto,
                        texto(documento, "email"),
                        texto(documento, "telefono"),
                        telefonos(documento),
                        texto(documento, "calle"),
                        texto(documento, "numero"),
                        texto(documento, "localidad")
                )
        );

        detalle.append("\n\n");

        detalle.append(
                getString(
                        R.string.detalle_vehiculo,
                        texto(documento, "marca"),
                        texto(documento, "modelo"),
                        texto(documento, "anio"),
                        texto(documento, "patente"),
                        texto(documento, "uso")
                )
        );

        detalle.append("\n\n");

        detalle.append(
                getString(
                        R.string.detalle_poliza,
                        texto(documento, "vigenciaDesde"),
                        texto(documento, "duracion"),
                        texto(documento, "formaPago"),
                        texto(documento, "condicionImpositiva"),
                        texto(documento, "productorEmail")
                )
        );

        detalle.append("\n\n");

        detalle.append(
                getString(
                        R.string.detalle_importes,
                        texto(documento, "cobertura"),
                        moneda(documento, "sumaAsegurada"),
                        moneda(documento, "primaBase"),
                        porcentaje(documento, "descuento"),
                        porcentaje(documento, "recargo"),
                        moneda(documento, "prima"),
                        porcentaje(documento, "porcentajeComision"),
                        moneda(documento, "comision")
                )
        );

        txtDetalle.setText(detalle.toString());
    }

    private String texto(DocumentSnapshot documento, String campo) {
        Object valor = documento.get(campo);

        if (valor instanceof String
                && !((String) valor).trim().isEmpty()) {
            return (String) valor;
        }

        return getString(R.string.dato_no_disponible);
    }

    private String moneda(DocumentSnapshot documento, String campo) {
        Object valor = documento.get(campo);

        if (valor instanceof Number) {
            NumberFormat formato = NumberFormat.getCurrencyInstance(
                    new Locale("es", "AR")
            );

            return formato.format(
                    ((Number) valor).doubleValue()
            );
        }

        return getString(R.string.dato_no_disponible);
    }

    private String porcentaje(
            DocumentSnapshot documento,
            String campo
    ) {
        Object valor = documento.get(campo);

        if (valor instanceof Number) {
            NumberFormat formato = NumberFormat.getNumberInstance(
                    new Locale("es", "AR")
            );

            formato.setMaximumFractionDigits(2);

            return formato.format(
                    ((Number) valor).doubleValue()
            ) + "%";
        }

        return getString(R.string.dato_no_disponible);
    }

    private String telefonos(DocumentSnapshot documento) {
        Object valor = documento.get("telefonosAdicionales");

        if (valor instanceof List<?> && !((List<?>) valor).isEmpty()) {
            return TextUtils.join(", ", (List<?>) valor);
        }

        return getString(R.string.dato_no_disponible);
    }

    private void modificarCondiciones() {
        if (cotizacionCargada == null) {
            return;
        }

        Object valorBase = cotizacionCargada.get("primaBase");

        if (!(valorBase instanceof Number)
                || !Double.isFinite(((Number) valorBase).doubleValue())
                || ((Number) valorBase).doubleValue() <= 0) {

            Toast.makeText(
                    this,
                    R.string.error_modificar_condiciones,
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        Map<String, Object> datos = cotizacionCargada.getData();

        if (datos == null) {
            return;
        }

        Intent intent = new Intent(
                DetalleCotizacionActivity.this,
                CondicionesComercialesActivity.class
        );

        for (Map.Entry<String, Object> campo : datos.entrySet()) {
            String clave = campo.getKey();
            Object valor = campo.getValue();

            if (valor instanceof String) {
                intent.putExtra(clave, (String) valor);

            } else if (valor instanceof Number) {
                if ("duracionMeses".equals(clave)) {
                    intent.putExtra(
                            clave,
                            ((Number) valor).intValue()
                    );
                } else {
                    intent.putExtra(
                            clave,
                            ((Number) valor).doubleValue()
                    );
                }
            }
        }

        Object telefonos = datos.get("telefonosAdicionales");

        if (telefonos instanceof List<?>) {
            ArrayList<String> listaTelefonos = new ArrayList<>();

            for (Object telefono : (List<?>) telefonos) {
                if (telefono instanceof String) {
                    listaTelefonos.add((String) telefono);
                }
            }

            intent.putStringArrayListExtra(
                    "telefonosAdicionales",
                    listaTelefonos
            );
        }

        intent.putExtra(
                "cotizacionId",
                cotizacionCargada.getId()
        );

        intent.putExtra(
                "prima",
                ((Number) valorBase).doubleValue()
        );

        startActivity(intent);
    }
}