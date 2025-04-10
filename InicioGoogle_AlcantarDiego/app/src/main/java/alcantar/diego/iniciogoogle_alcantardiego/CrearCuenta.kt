package alcantar.diego.iniciogoogle_alcantardiego

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class CrearCuenta : AppCompatActivity() {

    private lateinit var etCorreo: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnCrearCuenta: Button
    private lateinit var tvVolverLogin: TextView

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_cuenta)

        firebaseAuth = FirebaseAuth.getInstance()

        etCorreo = findViewById(R.id.etCorreo)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta)
        tvVolverLogin = findViewById(R.id.volverLogin)

        btnCrearCuenta.setOnClickListener {
            crearCuenta()
        }

        tvVolverLogin.setOnClickListener {
            volverAlLogin()
        }
    }

    private fun crearCuenta() {
        val correo = etCorreo.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        if (correo.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear cuenta con Firebase
        firebaseAuth.createUserWithEmailAndPassword(correo, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Error al crear la cuenta: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun volverAlLogin() {
        // Regresar a la actividad de login
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
