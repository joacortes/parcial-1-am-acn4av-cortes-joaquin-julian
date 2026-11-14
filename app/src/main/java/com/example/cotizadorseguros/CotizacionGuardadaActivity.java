package com.example.cotizadorseguros;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.NumberFormat;
import java.util.Locale;

public class CotizacionGuardadaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cotizacion_guardada);

        String numero = getIntent().getStringExtra("cotizacionId");
        String asegurado = getIntent().getStringExtra("asegurado");
        double prima = getIntent().getDoubleExtra("prima", 0);

        TextView txtNumero =
                findViewById(R.id.txtNumeroCotizacion);

        TextView txtAsegurado =
                findViewById(R.id.txtAseguradoGuardado);

        TextView txtPrima =
                findViewById(R.id.txtPrimaGuardada);

        NumberFormat moneda = NumberFormat.getCurrencyInstance(
                new Locale("es", "AR")
        );

        txtNumero.setText(
                getString(R.string.guardada_numero, numero)
        );

        txtAsegurado.setText(
                getString(R.string.guardada_asegurado, asegurado)
        );

        txtPrima.setText(
                getString(
                        R.string.guardada_prima,
                        moneda.format(prima)
                )
        );

        findViewById(R.id.btnVolverInicio)
                .setOnClickListener(v -> volverAlInicio());

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
    }

    private void volverAlInicio() {
        Intent intent = new Intent(
                CotizacionGuardadaActivity.this,
                InicioActivity.class
        );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}