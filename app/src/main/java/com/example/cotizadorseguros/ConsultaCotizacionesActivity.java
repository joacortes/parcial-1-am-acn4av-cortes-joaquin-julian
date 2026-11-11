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

        db.collection("cotizaciones").get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documento : queryDocumentSnapshots) {

                        String tipoPersona = documento.getString("tipoPersona");
                        String patente = documento.getString("patente");
                        String marca = documento.getString("marca");
                        String modelo = documento.getString("modelo");
                        String cobertura = documento.getString("cobertura");
                        Double sumaAsegurada = documento.getDouble("sumaAsegurada");
                        Double prima = documento.getDouble("prima");
                        NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));

                        String sumaAseguradaTexto = sumaAsegurada != null
                                ? formatoMoneda.format(sumaAsegurada)
                                : "No disponible";

                        String primaTexto = prima != null
                                ? formatoMoneda.format(prima)
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

                        txtCotizacion.setText(asegurado + "\n" + marca + " " + modelo + "\nPatente: " + patente + "\nCobertura: " + cobertura + "\nSuma asegurada: " + sumaAseguradaTexto + "\nPrima: " + primaTexto);

                        txtCotizacion.setPadding(
                                0,
                                16,
                                0,
                                32
                        );

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