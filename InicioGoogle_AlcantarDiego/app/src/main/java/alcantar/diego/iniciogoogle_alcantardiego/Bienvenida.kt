package alcantar.diego.iniciogoogle_alcantardiego

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class Bienvenida : AppCompatActivity() {

    private lateinit var tvCorreo: TextView
    private lateinit var btnSalir: Button
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bienvenida)

        firebaseAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        tvCorreo = findViewById(R.id.evCorreo)
        btnSalir = findViewById(R.id.btnSalir)

        mostrarInformacionUsuario()

        btnSalir.setOnClickListener {
            cerrarSesion()
        }
    }

    private fun mostrarInformacionUsuario() {
        val user = firebaseAuth.currentUser
        if (user != null) {
            val correo = user.email ?: "No disponible"

            val proveedor = when {
                user.providerData.any { it.providerId == "google.com" } -> "Google"
                else -> "Email/Password"
            }

            tvCorreo.text = "Correo: $correo\nProveedor: $proveedor"
        } else {
            volverAlLogin()
        }
    }

    private fun cerrarSesion() {
        firebaseAuth.signOut()

        googleSignInClient.signOut().addOnCompleteListener(this) {
            volverAlLogin()
        }
    }

    private fun volverAlLogin() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}