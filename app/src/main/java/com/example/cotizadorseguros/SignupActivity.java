package com.example.cotizadorseguros;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText inputEmailRegistro;
    private EditText inputPasswordRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        inputEmailRegistro = findViewById(R.id.inputEmailRegistro);
        inputPasswordRegistro = findViewById(R.id.inputPasswordRegistro);
    }
    public void signupAction(View view) {
        String email = inputEmailRegistro.getText().toString().trim();
        String password = inputPasswordRegistro.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,  "Completá email y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this,  "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}