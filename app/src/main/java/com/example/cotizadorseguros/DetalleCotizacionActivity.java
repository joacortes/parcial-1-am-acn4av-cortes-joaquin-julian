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

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import android.view.View;
import com.google.firebase.Timestamp;

public class DetalleCotizacionActivity extends AppCompatActivity {

    private TextView txtDetalle;
    private DocumentSnapshot cotizacionCargada;
    private boolean modoPoliza;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle_cotizacion);

        txtDetalle = findViewById(R.id.txtDetalleCotizacion);


        modoPoliza = getIntent().getBooleanExtra("modoPoliza", false);

        if (modoPoliza) {
            TextView titulo = findViewById(R.id.txtTituloDetalle);
            titulo.setText(R.string.detalle_poliza_titulo);

            findViewById(R.id.btnEmitirPoliza)
                    .setVisibility(View.GONE);

            findViewById(R.id.btnModificarCondiciones)
                    .setVisibility(View.GONE);

            findViewById(R.id.btnModificarPoliza).setVisibility(View.GONE);
            findViewById(R.id.btnModificarCobertura).setVisibility(View.GONE);
            findViewById(R.id.btnModificarVehiculo).setVisibility(View.GONE);
        }

        findViewById(R.id.btnVolverConsulta).setOnClickListener(v -> finish());

        findViewById(R.id.btnModificarCondiciones).setOnClickListener(v -> modificarCondiciones());

        findViewById(R.id.btnEmitirPoliza).setOnClickListener(v -> emitirPoliza());

        findViewById(R.id.btnModificarPoliza).setOnClickListener(v -> modificarPoliza());

        findViewById(R.id.btnModificarCobertura).setOnClickListener(v -> modificarCobertura());
        findViewById(R.id.btnModificarVehiculo).setOnClickListener(v -> modificarVehiculo());

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

                    if (modoPoliza
                            && !"emitida".equals(documento.get("estado"))) {

                        txtDetalle.setText(
                                R.string.detalle_cotizacion_no_disponible
                        );


                        return;
                    }

                    mostrarCotizacion(documento);
                    cotizacionCargada = documento;

                    boolean puedeOperar = documento.get("estado") == null || "cotizada".equals(documento.get("estado"));

                    findViewById(R.id.btnModificarCobertura).setEnabled(puedeOperar);
                    findViewById(R.id.btnModificarCondiciones).setEnabled(puedeOperar);

                    findViewById(R.id.btnEmitirPoliza).setEnabled(puedeOperar);
                    findViewById(R.id.btnModificarPoliza).setEnabled(puedeOperar);
                    findViewById(R.id.btnModificarVehiculo).setEnabled(puedeOperar);
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

        String estadoTexto;

        if ("emitida".equals(documento.get("estado"))) {
            estadoTexto = getString(R.string.estado_emitida);
        } else {
            estadoTexto = getString(R.string.estado_cotizada);
        }

        detalle.append("\n");

        detalle.append(
                getString(
                        R.string.detalle_estado,
                        estadoTexto
                )
        );

        if ("emitida".equals(documento.get("estado"))) {
            detalle.append("\n");

            detalle.append(
                    getString(
                            R.string.detalle_numero_poliza,
                            texto(documento, "numeroPoliza")
                    )
            );

            detalle.append("\n");

            detalle.append(
                    getString(
                            R.string.detalle_fin_vigencia,
                            texto(documento, "vigenciaHasta")
                    )
            );

            Timestamp fechaEmision =
                    documento.getTimestamp("fechaEmision");

            if (fechaEmision != null) {
                SimpleDateFormat formato = new SimpleDateFormat(
                        "dd/MM/yyyy HH:mm",
                        new Locale("es", "AR")
                );

                detalle.append("\n");

                detalle.append(
                        getString(
                                R.string.detalle_fecha_emision,
                                formato.format(fechaEmision.toDate())
                        )
                );
            }
        }

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
        if (!puedeEditarCotizacion()) {
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

        Intent intent = crearIntentEdicion(
                CondicionesComercialesActivity.class
        );

        if (intent == null) {
            return;
        }

        intent.putExtra(
                "prima",
                ((Number) valorBase).doubleValue()
        );

        startActivity(intent);
    }

    private void modificarPoliza() {
        if (!puedeEditarCotizacion()) {
            return;
        }

        Intent intent = crearIntentEdicion(
                PolizaActivity.class
        );

        if (intent != null) {
            startActivity(intent);
        }
    }
    private String calcularFinVigencia(DocumentSnapshot documento) {
        Object inicioGuardado = documento.get("vigenciaDesde");
        Object duracionGuardada = documento.get("duracionMeses");

        if (!(inicioGuardado instanceof String)
                || !(duracionGuardada instanceof Number)) {
            return null;
        }

        String inicioTexto = (String) inicioGuardado;
        Number duracion = (Number) duracionGuardada;
        int meses = duracion.intValue();

        if (duracion.doubleValue() != meses
                || (meses != 1 && meses != 6 && meses != 12)) {
            return null;
        }

        if (!inicioTexto.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
            return null;
        }

        SimpleDateFormat formato =
                new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);

        formato.setLenient(false);

        try {
            Date inicio = formato.parse(inicioTexto);

            if (inicio == null) {
                return null;
            }

            Calendar hoy = Calendar.getInstance();
            hoy.set(Calendar.HOUR_OF_DAY, 0);
            hoy.set(Calendar.MINUTE, 0);
            hoy.set(Calendar.SECOND, 0);
            hoy.set(Calendar.MILLISECOND, 0);

            if (inicio.before(hoy.getTime())) {
                return null;
            }

            Calendar fin = Calendar.getInstance();
            fin.setTime(inicio);
            fin.add(Calendar.MONTH, meses);

            return formato.format(fin.getTime());

        } catch (ParseException e) {
            return null;
        }
    }

    private void emitirPoliza() {
        if (cotizacionCargada == null) {
            return;
        }

        FirebaseUser usuario =
                FirebaseAuth.getInstance().getCurrentUser();

        if (usuario == null) {
            Toast.makeText(
                    this,
                    R.string.error_sesion_requerida,
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference referencia = db.collection("cotizaciones")
                .document(cotizacionCargada.getId());

        String usuarioUid = usuario.getUid();

        findViewById(R.id.btnEmitirPoliza).setEnabled(false);
        findViewById(R.id.btnModificarCondiciones).setEnabled(false);
        findViewById(R.id.btnModificarPoliza).setEnabled(false);
        findViewById(R.id.btnModificarCobertura).setEnabled(false);
        findViewById(R.id.btnModificarVehiculo).setEnabled(false);

        db.<String>runTransaction(transaccion -> {
            DocumentSnapshot documento = transaccion.get(referencia);

            if (!documento.exists()
                    || !usuarioUid.equals(documento.get("usuarioUid"))) {

                throw new FirebaseFirestoreException(
                        "Cotización no disponible",
                        FirebaseFirestoreException.Code.PERMISSION_DENIED
                );
            }

            String estado = documento.getString("estado");

            if ("emitida".equals(estado)) {
                String numeroExistente =
                        documento.getString("numeroPoliza");

                if (numeroExistente == null
                        || numeroExistente.trim().isEmpty()) {

                    throw new FirebaseFirestoreException(
                            "La operación emitida no tiene número",
                            FirebaseFirestoreException.Code.FAILED_PRECONDITION
                    );
                }

                return numeroExistente;
            }

            if (estado != null && !"cotizada".equals(estado)) {
                throw new FirebaseFirestoreException(
                        "Estado no válido",
                        FirebaseFirestoreException.Code.FAILED_PRECONDITION
                );
            }

            String finVigencia = calcularFinVigencia(documento);
            Object primaGuardada = documento.get("prima");

            if (finVigencia == null
                    || !(primaGuardada instanceof Number)
                    || !Double.isFinite(
                    ((Number) primaGuardada).doubleValue()
            )
                    || ((Number) primaGuardada).doubleValue() < 0) {

                throw new FirebaseFirestoreException(
                        getString(R.string.emision_error_datos),
                        FirebaseFirestoreException.Code.FAILED_PRECONDITION
                );
            }

            String numeroPoliza = "POL-" + referencia.getId();

            Map<String, Object> emision = new HashMap<>();
            emision.put("estado", "emitida");
            emision.put("numeroPoliza", numeroPoliza);
            emision.put("vigenciaHasta", finVigencia);
            emision.put("fechaEmision", FieldValue.serverTimestamp());

            transaccion.update(referencia, emision);

            return numeroPoliza;

        }).addOnSuccessListener(numeroPoliza -> {
            if (isFinishing() || isDestroyed()) {
                return;
            }

            Intent intent = new Intent(
                    DetalleCotizacionActivity.this,
                    PolizaEmitidaActivity.class
            );

            intent.putExtra(
                    "cotizacionId",
                    referencia.getId()
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();

        }).addOnFailureListener(e -> {
            if (isFinishing() || isDestroyed()) {
                return;
            }

            Toast.makeText(
                    this,
                    R.string.emision_error,
                    Toast.LENGTH_LONG
            ).show();

            cargarCotizacion();
        });

    }

    private boolean puedeEditarCotizacion() {
        if (cotizacionCargada == null) {
            return false;
        }

        Object estado = cotizacionCargada.get("estado");

        if (estado != null && !"cotizada".equals(estado)) {
            Toast.makeText(
                    this,
                    R.string.error_cotizacion_no_editable,
                    Toast.LENGTH_LONG
            ).show();

            return false;
        }

        return true;
    }

    private Intent crearIntentEdicion(Class<?> destino) {
        Map<String, Object> datos = cotizacionCargada.getData();

        if (datos == null) {
            return null;
        }

        Intent intent = new Intent(this, destino);

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

        return intent;
    }

    private void modificarCobertura() {
        if (!puedeEditarCotizacion()) {
            return;
        }

        Intent intent = crearIntentEdicion(
                CoberturaActivity.class
        );

        if (intent != null) {
            startActivity(intent);
        }
    }

    private void modificarVehiculo() {
        if (!puedeEditarCotizacion()) {
            return;
        }

        Intent intent = crearIntentEdicion(
                VehiculoActivity.class
        );

        if (intent != null) {
            startActivity(intent);
        }
    }
}