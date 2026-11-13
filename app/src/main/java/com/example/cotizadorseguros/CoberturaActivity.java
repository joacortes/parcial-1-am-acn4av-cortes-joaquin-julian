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
import android.widget.ImageView;

import com.bumptech.glide.Glide;

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
    private double sumaAsegurada;
    private double primaCalculada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_cobertura);

        txtDatosVehiculo = findViewById(R.id.txtDatosVehiculo);
        txtCoberturaSeleccionada = findViewById(R.id.txtCoberturaSeleccionada);

        ImageView imgVehiculo = findViewById(R.id.imgVehiculo);

        String urlImagen = "https://cdn-icons-png.flaticon.com/512/2237/2237750.png";

        Glide.with(this).load(urlImagen).into(imgVehiculo);

        Intent intent = getIntent();

        sumaAsegurada = intent.getDoubleExtra("sumaAsegurada", 0);

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

        double coeficiente = 0.002;

        primaCalculada = sumaAsegurada * coeficiente;

        actualizarCoberturaSeleccionada();
    }

    public void seleccionarTercerosCompleto(View view) {

        coberturaSeleccionada = "Terceros Completo";

        double coeficiente = 0.0035;

        primaCalculada = sumaAsegurada * coeficiente;

        actualizarCoberturaSeleccionada();
    }

    public void seleccionarTodoRiesgo(View view) {

        coberturaSeleccionada = "Todo Riesgo";

        double coeficiente = 0.006;
        primaCalculada = sumaAsegurada * coeficiente;

        actualizarCoberturaSeleccionada();
    }

    private void actualizarCoberturaSeleccionada() {

        txtCoberturaSeleccionada.setText("Cobertura seleccionada: " + coberturaSeleccionada + "\nPrima calculada: $" + primaCalculada);

        Toast.makeText(this, "Cobertura seleccionada correctamente", Toast.LENGTH_SHORT).show();
    }

    public void continuarCobertura(View view) {
        if (coberturaSeleccionada.isEmpty()) {
            Toast.makeText(this, "Seleccioná una cobertura", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(CoberturaActivity.this, CondicionesComercialesActivity.class);

        if (getIntent().getExtras() != null) {
            intent.putExtras(getIntent().getExtras());
        }

        intent.putExtra("tipoPersona", tipoPersona);

        if ("fisica".equals(tipoPersona)) {

            intent.putExtra("nombre", nombre);
            intent.putExtra("apellido", apellido);
            intent.putExtra("dni", dni);
        }

        if ("juridica".equals(tipoPersona)) {

            intent.putExtra("razonSocial", razonSocial);
            intent.putExtra("cuit", cuit);
            intent.putExtra("nombreFantasia", nombreFantasia);
        }

        intent.putExtra("patente", patente);
        intent.putExtra("marca", marca);
        intent.putExtra("modelo", modelo);
        intent.putExtra("anio", anio);
        intent.putExtra("uso", uso);

        intent.putExtra("cobertura", coberturaSeleccionada);

        intent.putExtra("sumaAsegurada", sumaAsegurada);
        intent.putExtra("prima", primaCalculada);
        startActivity(intent);
    }
}