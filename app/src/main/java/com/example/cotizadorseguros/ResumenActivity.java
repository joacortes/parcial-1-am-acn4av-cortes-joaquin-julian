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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ResumenActivity extends AppCompatActivity {
    private TextView txtResumenAsegurado;
    private TextView txtResumenVehiculo;
    private TextView txtResumenCobertura;
    private TextView txtResumenPrecio;
    private FirebaseFirestore db;
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

    private String cobertura;
    private double precio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resumen);

        db = FirebaseFirestore.getInstance();

        txtResumenAsegurado = findViewById(R.id.txtResumenAsegurado);
        txtResumenVehiculo = findViewById(R.id.txtResumenVehiculo);
        txtResumenCobertura = findViewById(R.id.txtResumenCobertura);
        txtResumenPrecio = findViewById(R.id.txtResumenPrecio);

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

        tipoPersona = intent.getStringExtra("tipoPersona");
        patente = intent.getStringExtra("patente");
        marca = intent.getStringExtra("marca");
        modelo = intent.getStringExtra("modelo");
        anio = intent.getStringExtra("anio");
        uso = intent.getStringExtra("uso");

        cobertura = intent.getStringExtra("cobertura");
        precio = intent.getDoubleExtra("precio", 0);

        double precio = intent.getDoubleExtra("precio", 0);

        txtResumenVehiculo.setText(getString(R.string.resumen_vehiculo, marca, modelo, anio, patente, uso));

        txtResumenCobertura.setText(cobertura);
        txtResumenPrecio.setText(getString(R.string.resumen_precio, precio));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

                    return insets;
                }
        );
    }

    public void guardarCotizacion(View view) {

        Map<String, Object> cotizacion = new HashMap<>();
        cotizacion.put("tipoPersona", tipoPersona);

        if ("fisica".equals(tipoPersona)) {
            cotizacion.put("nombre", nombre);
            cotizacion.put("apellido", apellido);
            cotizacion.put("dni", dni);
        }

        if ("juridica".equals(tipoPersona)) {
            cotizacion.put("razonSocial", razonSocial);
            cotizacion.put("cuit", cuit);
            cotizacion.put("nombreFantasia", nombreFantasia);
        }

        cotizacion.put("patente", patente);
        cotizacion.put("marca", marca);
        cotizacion.put("modelo", modelo);
        cotizacion.put("anio", anio);
        cotizacion.put("uso", uso);

        cotizacion.put("cobertura", cobertura);
        cotizacion.put("precio", precio);

        FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();

        if (usuarioActual != null) {
            cotizacion.put("usuario", usuarioActual.getEmail());
        }

        db.collection("cotizaciones").add(cotizacion).addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Cotización guardada correctamente", Toast.LENGTH_LONG).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar la cotización", Toast.LENGTH_LONG).show();
                });
    }
}