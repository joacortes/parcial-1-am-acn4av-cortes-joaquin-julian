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

public class ConsultaCotizacionesActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout contenedorCotizaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_consulta_cotizaciones);

        db = FirebaseFirestore.getInstance();

        contenedorCotizaciones =
                findViewById(R.id.contenedorCotizaciones);

        cargarCotizaciones();
    }

    private void cargarCotizaciones() {

        FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();

        if (usuarioActual == null) {
            Toast.makeText(this, R.string.error_sesion_requerida, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        db.collection("cotizaciones").whereEqualTo("usuarioUid", usuarioActual.getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
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

                    TextView txtCotizacion = new TextView(this);

                    txtCotizacion.setText(asegurado
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
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Error al cargar cotizaciones", Toast.LENGTH_SHORT).show();
            });
    }
}