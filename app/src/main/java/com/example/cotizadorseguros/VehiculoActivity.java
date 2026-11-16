package com.example.cotizadorseguros;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import java.math.BigDecimal;

public class VehiculoActivity extends AppCompatActivity {
    private Spinner spinnerMarca;
    private Spinner spinnerModelo;

    private ArrayList<MarcaVehiculo> listaMarcas = new ArrayList<>();
    private ArrayList<String> nombresMarcas = new ArrayList<>();
    private ArrayList<String> nombresModelos = new ArrayList<>();
    private EditText inputUso;
    private EditText inputPatente;
    private EditText inputAnio;
    private EditText inputSumaAsegurada;

    private String tipoPersona;

    private String nombre;
    private String apellido;
    private String dni;

    private String razonSocial;
    private String cuit;
    private String nombreFantasia;

    private OkHttpClient clienteHttp;
    private String marcaPendiente;
    private String modeloPendiente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_vehiculo);

        spinnerMarca = findViewById(R.id.spinnerMarca);
        spinnerModelo = findViewById(R.id.spinnerModelo);
        inputSumaAsegurada = findViewById(R.id.inputSumaAsegurada);
        if (savedInstanceState != null) {
            marcaPendiente =
                    savedInstanceState.getString("marcaSeleccionada");

            modeloPendiente =
                    savedInstanceState.getString("modeloSeleccionado");
        } else {
            marcaPendiente = getIntent().getStringExtra("marca");
            modeloPendiente = getIntent().getStringExtra("modelo");
        }

        spinnerMarca.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {
                        if (position <= 0
                                || position >= nombresMarcas.size()) {
                            spinnerModelo.setAdapter(null);
                            return;
                        }

                        String nombreMarca = nombresMarcas.get(position);

                        if (modeloPendiente != null
                                && !nombreMarca.equals(marcaPendiente)) {
                            modeloPendiente = null;
                        }

                        spinnerModelo.setAdapter(null);
                        cargarModelosDesdeApi(nombreMarca);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );



        clienteHttp = new OkHttpClient();

        cargarMarcasDesdeApi();

        inputPatente = findViewById(R.id.inputPatente);

        inputAnio = findViewById(R.id.inputAnio);
        inputUso = findViewById(R.id.inputUso);
        if (savedInstanceState == null
                && getIntent().hasExtra("cotizacionId")) {
            precargarVehiculo();
        }

        TextView txtAsegurado = findViewById(R.id.txtAsegurado);

        Intent intent = getIntent();

        tipoPersona = intent.getStringExtra("tipoPersona");

        if ("fisica".equals(tipoPersona)) {

            nombre = intent.getStringExtra("nombre");
            apellido = intent.getStringExtra("apellido");
            dni = intent.getStringExtra("dni");

            txtAsegurado.setText(
                    getString(
                            R.string.vehiculo_asegurado_fisica,
                            nombre,
                            apellido,
                            dni
                    )
            );
        }

        if ("juridica".equals(tipoPersona)) {

            razonSocial = intent.getStringExtra("razonSocial");
            cuit = intent.getStringExtra("cuit");
            nombreFantasia = intent.getStringExtra("nombreFantasia");

            txtAsegurado.setText(
                    getString(
                            R.string.vehiculo_asegurado_juridica,
                            razonSocial,
                            cuit
                    )
            );
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

    private void cargarModelosDesdeApi(String marca) {
        String url = "https://vpic.nhtsa.dot.gov/api/vehicles/GetModelsForMake/" + marca + "?format=json";
        Request request = new Request.Builder().url(url).build();
        clienteHttp.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(VehiculoActivity.this, R.string.error_cargar_modelos, Toast.LENGTH_SHORT).show());
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        return;
                    }
                    String json = response.body().string();
                    Gson gson = new Gson();

                    RespuestaModelos respuesta = gson.fromJson(json, RespuestaModelos.class);

                    nombresModelos.clear();
                    nombresModelos.add(
                            getString(R.string.vehiculo_seleccionar_modelo)
                    );
                    for(ModeloVehiculo modelo : respuesta.getResults()) {
                        nombresModelos.add(modelo.getModelName());
                    }

                    runOnUiThread(() -> {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(VehiculoActivity.this, android.R.layout.simple_spinner_item, nombresModelos);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerModelo.setAdapter(adapter);
                        if (modeloPendiente != null
                                && marca.equals(marcaPendiente)
                                && spinnerMarca.getSelectedItem() != null
                                && marca.equals(
                                spinnerMarca.getSelectedItem().toString()
                        )) {

                            int posicion = adapter.getPosition(modeloPendiente);

                            if (posicion > 0) {
                                spinnerModelo.setSelection(posicion);
                            }

                            modeloPendiente = null;
                        }
                    });
                }
        });
    }

    private void cargarMarcasDesdeApi() {

        String url = "https://vpic.nhtsa.dot.gov/api/vehicles/GetMakesForVehicleType/car?format=json";

        Request request = new Request.Builder().url(url).build();

        clienteHttp.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(() -> {
                            Log.e("API_MARCAS", "Error al consultar API", e);
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }

                        String json = response.body().string();
                        Gson gson = new Gson();

                        RespuestaMarcas respuesta = gson.fromJson(json, RespuestaMarcas.class);

                        listaMarcas.clear();
                        listaMarcas.addAll(respuesta.getResults());

                        nombresMarcas.clear();
                        nombresMarcas.add(getString(R.string.vehiculo_seleccionar_marca));

                        for (MarcaVehiculo marca : listaMarcas) {
                            nombresMarcas.add(marca.getMakeName());
                        }

                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(VehiculoActivity.this, android.R.layout.simple_spinner_item, nombresMarcas);

                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerMarca.setAdapter(adapter);

                            if (marcaPendiente != null) {
                                int posicion = adapter.getPosition(marcaPendiente);

                                if (posicion > 0) {
                                    spinnerMarca.setSelection(posicion);
                                }
                            }
                        });
                    }
                });
    }

    public void continuarVehiculo(View view) {

        if (spinnerMarca.getSelectedItem() == null
                || spinnerModelo.getSelectedItem() == null
                || spinnerMarca.getSelectedItemPosition() <= 0
                || spinnerModelo.getSelectedItemPosition() <= 0) {

            Toast.makeText(
                    this,
                    R.string.error_marca_modelo,
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String patente = inputPatente.getText().toString().trim();
        String marca = spinnerMarca.getSelectedItem().toString();
        String modelo = spinnerModelo.getSelectedItem().toString();
        String anio = inputAnio.getText().toString().trim();
        String uso = inputUso.getText().toString().trim();
        String sumaAseguradaTexto = inputSumaAsegurada.getText().toString().trim();

        if (patente.isEmpty() || marca.isEmpty() || modelo.isEmpty() || anio.isEmpty() || uso.isEmpty() || sumaAseguradaTexto.isEmpty()) {
            Toast.makeText(this, R.string.error_datos_vehiculo  , Toast.LENGTH_SHORT).show();
            return;
        }

        double sumaAsegurada;

        try {
            // Acepta números como 15000000, 15000000.50 o 15000000,50.
            if (!sumaAseguradaTexto.matches("[0-9]+([.,][0-9]+)?")) {
                throw new IllegalArgumentException();
            }

            sumaAsegurada = Double.parseDouble(sumaAseguradaTexto.replace(',', '.'));

            if (!Double.isFinite(sumaAsegurada) || sumaAsegurada <= 0) {
                throw new IllegalArgumentException();
            }

        } catch (IllegalArgumentException e) {
            inputSumaAsegurada.setError(getString(R.string.error_suma_asegurada));
            inputSumaAsegurada.requestFocus();
            return;
        }

        Intent intent = new Intent(VehiculoActivity.this, CoberturaActivity.class);

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
        intent.putExtra("sumaAsegurada", sumaAsegurada);

        startActivity(intent);
    }

    private void precargarVehiculo() {
        inputPatente.setText(
                getIntent().getStringExtra("patente")
        );

        inputAnio.setText(
                getIntent().getStringExtra("anio")
        );

        inputUso.setText(
                getIntent().getStringExtra("uso")
        );

        double suma = getIntent().getDoubleExtra(
                "sumaAsegurada",
                0
        );

        if (Double.isFinite(suma) && suma > 0) {
            inputSumaAsegurada.setText(
                    BigDecimal.valueOf(suma).toPlainString()
            );
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        String marcaActual = marcaPendiente;
        String modeloActual = modeloPendiente;

        if (spinnerMarca.getSelectedItemPosition() > 0) {
            marcaActual =
                    spinnerMarca.getSelectedItem().toString();
        }

        if (spinnerModelo.getSelectedItemPosition() > 0) {
            modeloActual =
                    spinnerModelo.getSelectedItem().toString();
        }

        outState.putString("marcaSeleccionada", marcaActual);
        outState.putString("modeloSeleccionado", modeloActual);

        super.onSaveInstanceState(outState);
    }
}