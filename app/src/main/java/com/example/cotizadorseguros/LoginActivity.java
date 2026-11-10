package com.example.cotizadorseguros;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText inputEmail;
    private EditText inputPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(
                    LoginActivity.this,
                    InicioActivity.class
            );

            startActivity(intent);

            finish();

            return;
        }

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void loginAction(View view) {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completá email y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Inicio de sesión correcto", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, InicioActivity.class);
                startActivity(intent);

                finish();
            } else {
                Toast.makeText(this, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void goToSignup(View view) {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }
}