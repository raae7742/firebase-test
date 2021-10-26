package com.example.firebasetest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.firebasetest.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    var auth : FirebaseAuth? = null

    private var googleSignInClient : GoogleSignInClient?= null
    private var binding: ActivityLoginBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        auth = FirebaseAuth.getInstance()

        binding!!.googleSignInButton.setOnClickListener {
            googleLogin()
        }

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

    }


    fun googleLogin() {
        var signInIntent = googleSignInClient?.signInIntent
        mlauncher.launch(signInIntent)
    }

    val mlauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        var value = Auth.GoogleSignInApi.getSignInResultFromIntent(result.data)!!

        if (value.isSuccess) {
            var account = value.signInAccount
            firebaseAuthWithGoogle(account)
            Toast.makeText(this, "성공", Toast.LENGTH_LONG).show()
        }
        else {
            Toast.makeText(this, "실패", Toast.LENGTH_LONG).show()
        }
    }

    // tokenId로 firebase에 인증하는 함수
    private fun firebaseAuthWithGoogle(idToken: GoogleSignInAccount?) {
        // it가 tokenId, credential은 Firebase 사용자 인증 정보
        var credential = GoogleAuthProvider.getCredential(idToken?.idToken, null)

        // credential로 Firebase 인증
        auth!!.signInWithCredential(credential)
            .addOnCompleteListener(this@LoginActivity) {
                    task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    // Login
                    Toast.makeText(
                        this@LoginActivity,
                        "로그인 성공",
                        Toast.LENGTH_LONG
                    ).show()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                } else {
                    // Show the error message
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    fun moveMainPage (user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent( this, MainActivity::class.java));
            finish()
        }
    }
}