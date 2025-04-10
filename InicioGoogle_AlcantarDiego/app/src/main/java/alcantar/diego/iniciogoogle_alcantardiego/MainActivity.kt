package alcantar.diego.iniciogoogle_alcantardiego

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {

    private lateinit var etCorreo: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnCrearCuenta: Button
    private lateinit var signInButton: SignInButton

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val TAG = "GoogleSignIn"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Configurar Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Inicializar vistas
        etCorreo = findViewById(R.id.etCorreo)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btn_login)
        btnCrearCuenta = findViewById(R.id.btn_crearCuenta)
        signInButton = findViewById(R.id.sign_in_button)

        // Configurar listeners
        btnLogin.setOnClickListener {
            iniciarSesionConCorreo()
        }

        btnCrearCuenta.setOnClickListener {
            val intent = Intent(this, CrearCuenta::class.java)
            startActivity(intent)
        }

        signInButton.setOnClickListener {
            iniciarSesionConGoogle()
        }
    }

    override fun onStart() {
        super.onStart()
        // Verificar si el usuario ya está autenticado
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            irABienvenida()
        }
    }

    private fun iniciarSesionConCorreo() {
        val correo = etCorreo.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (correo.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        firebaseAuth.signInWithEmailAndPassword(correo, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    irABienvenida()
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun iniciarSesionConGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                // Manejo de posibles nulos
                if (task.isSuccessful) {
                    val account = task.getResult(ApiException::class.java)
                    if (account != null && account.idToken != null) {
                        Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                        firebaseAuthWithGoogle(account.idToken!!)
                    } else {
                        Log.w(TAG, "Google sign in failed: account or idToken is null")
                        Toast.makeText(this, "Error: No se pudo obtener la información de la cuenta", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.w(TAG, "Google sign in task unsuccessful")
                    Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this, "Error al iniciar sesión con Google: ${e.statusCode}", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during Google sign in", e)
                Toast.makeText(this, "Error inesperado: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    Toast.makeText(this, "Inicio de sesión con Google exitoso", Toast.LENGTH_SHORT).show()
                    irABienvenida()
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    val errorMessage = task.exception?.message ?: "Error desconocido"
                    Toast.makeText(this, "Error de autenticación con Google: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun irABienvenida() {
        val intent = Intent(this, Bienvenida::class.java)
        startActivity(intent)
        finish()
    }
}