package com.example.abcplay

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    // instancia do firebase auth service
    private val firebaseAuth = FirebaseAuth.getInstance()

    // listener para redirecionar o usuario no login sucesso
    private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
        val user = firebaseAuth.currentUser?.uid
        user?.let {
            startActivity(HomeActivity.newIntent(this))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // listener para qd o usuario digitar o erro sumir
        setTextChangeListener(email_input_text, email_layout)
        setTextChangeListener(password_input_text, password_layout)

        // listener para evitar varias requisicoes ao clicar no botao de login
        loginProgressLayout.setOnTouchListener { view, motionEvent -> true }

    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(firebaseAuthListener)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(firebaseAuthListener)
    }

    fun onLogin(view: View) {
        var proceed = true

        if (email_input_text.text.isNullOrEmpty()) {
            email_layout.error = "Email obrigatorio"
            email_layout.isErrorEnabled = true
            proceed = false
        }

        if (password_input_text.text.isNullOrEmpty()) {
            password_layout.error = "Senha obrigatorio"
            password_layout.isErrorEnabled = true
            proceed = false
        }

        if (proceed) {
            loginProgressLayout.visibility = View.VISIBLE
            firebaseAuth.signInWithEmailAndPassword(
                email_input_text.text.toString(),
                password_input_text.text.toString()
            )
                .addOnCompleteListener {
                    if (!it.isSuccessful) {
                        loginProgressLayout.visibility = View.GONE
                        Toast.makeText(
                            this@MainActivity,
                            "Erro login: ${it.exception?.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.addOnFailureListener {
                    loginProgressLayout.visibility = View.GONE
                    it.printStackTrace()
                }
        }
    }

    private fun setTextChangeListener(text: EditText, layout: TextInputLayout) {
        text.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                layout.isErrorEnabled = false
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }
}