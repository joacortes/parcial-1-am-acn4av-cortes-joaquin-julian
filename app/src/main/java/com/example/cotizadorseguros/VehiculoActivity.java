package com.example.cotizadorseguros;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VehiculoActivity extends AppCompatActivity {
    private EditText inputUso;
    private EditText inputPatente;
    private EditText inputMarca;
    private EditText inputModelo;
    private EditText inputAnio;

    private String tipoPersona;

    private String nombre;
    private String apellido;
    private String dni;

    private String razonSocial;
    private String cuit;
    private String nombreFantasia;
    private TextView txtMarcasApi;
    private OkHttpClient clienteHttp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_vehiculo);

        txtMarcasApi = findViewById(R.id.txtMarcasApi);

        clienteHttp = new OkHttpClient();

        cargarMarcasDesdeApi();

        inputPatente = findViewById(R.id.inputPatente);
        inputMarca = findViewById(R.id.inputMarca);
        inputModelo = findViewById(R.id.inputModelo);
        inputAnio = findViewById(R.id.inputAnio);
        inputUso = findViewById(R.id.inputUso);

        TextView txtAsegurado = findViewById(R.id.txtAsegurado);

        Intent intent = getIntent();

        tipoPersona = intent.getStringExtra("tipoPersona");

        if ("fisica".equals(tipoPersona)) {

            nombre = intent.getStringExtra("nombre");
            apellido = intent.getStringExtra("apellido");
            dni = intent.getStringExtra("dni");

            txtAsegurado.setText(nombre + " " + apellido + " - DNI " + dni);
        }

        if ("juridica".equals(tipoPersona)) {

            razonSocial = intent.getStringExtra("razonSocial");
            cuit = intent.getStringExtra("cuit");
            nombreFantasia = intent.getStringExtra("nombreFantasia");

            txtAsegurado.setText(razonSocial + " - CUIT " + cuit);
        }

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

    private void cargarMarcasDesdeApi() {

        String url = "https://vpic.nhtsa.dot.gov/api/vehicles/GetMakesForVehicleType/car?format=json";

        Request request = new Request.Builder().url(url).build();

        clienteHttp.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(() -> {
                            txtMarcasApi.setText("Error: " + e.getMessage());
                            Log.e("API_MARCAS", "Error al consultar API", e);
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {

                            runOnUiThread(() -> {
                                txtMarcasApi.setText("Error al consultar la API");
                            });

                            return;
                        }

                        String json = response.body().string();

                        Gson gson = new Gson();

                        RespuestaMarcas respuesta = gson.fromJson(json, RespuestaMarcas.class);

                        List<MarcaVehiculo> marcas = respuesta.getResults();

                        StringBuilder texto = new StringBuilder();

                        int cantidad = Math.min(10, marcas.size());

                        for (int i = 0; i < cantidad; i++) {
                            texto.append(marcas.get(i).getMakeName());
                            texto.append("\n");
                        }

                        runOnUiThread(() -> {
                            txtMarcasApi.setText(texto.toString());
                        });
                    }
                });
    }

    public void continuarVehiculo(View view) {
        String patente = inputPatente.getText().toString().trim();
        String marca = inputMarca.getText().toString().trim();
        String modelo = inputModelo.getText().toString().trim();
        String anio = inputAnio.getText().toString().trim();
        String uso = inputUso.getText().toString().trim();

        if (patente.isEmpty()
                || marca.isEmpty()
                || modelo.isEmpty()
                || anio.isEmpty()
                || uso.isEmpty()) {

            Toast.makeText(
                    this,
                    "Completá todos los datos del vehículo",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Intent intent = new Intent(
                VehiculoActivity.this,
                CoberturaActivity.class
        );

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

        startActivity(intent);
    }
}