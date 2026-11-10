package com.example.cotizadorseguros;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class InicioActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView txtEmailUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        mAuth = FirebaseAuth.getInstance();

        txtEmailUsuario = findViewById(R.id.txtEmailUsuario);

        FirebaseUser usuarioActual = mAuth.getCurrentUser();

        if (usuarioActual != null) {
            txtEmailUsuario.setText(usuarioActual.getEmail());
        }
    }

    public void nuevaCotizacion(View view) {

        Intent intent = new Intent(
                InicioActivity.this,
                MainActivity.class
        );

        startActivity(intent);
    }

    public void cerrarSesion(View view) {

        mAuth.signOut();

        Intent intent = new Intent(
                InicioActivity.this,
                LoginActivity.class
        );

        startActivity(intent);

        finish();
    }

    public void consultarCotizaciones(View view) {

        Intent intent = new Intent(
                InicioActivity.this,
                ConsultaCotizacionesActivity.class
        );

        startActivity(intent);
    }
}