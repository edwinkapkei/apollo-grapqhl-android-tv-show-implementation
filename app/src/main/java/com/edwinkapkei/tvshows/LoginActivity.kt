package com.edwinkapkei.tvshows

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.exception.ApolloException
import com.edwinkapkei.tvshows.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (SessionManager.isLoggedIn(this)) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

        binding.login.setOnClickListener {
            if (validateInput()) {
                binding.signup.visibility = View.GONE
                binding.login.visibility = View.GONE
                binding.progressbar.visibility = View.VISIBLE

                lifecycleScope.launchWhenCreated {
                    val mutation = LoginMutation(email = binding.email.text.toString(), password = binding.password.text.toString());
                    val response = try {
                        apolloClient.mutate(mutation).toDeferred().await()
                    } catch (e: ApolloException) {
                        null
                    }

                    binding.signup.visibility = View.VISIBLE
                    binding.login.visibility = View.VISIBLE
                    binding.progressbar.visibility = View.GONE
                    if (response != null) {
                        Snackbar.make(binding.rootLayout, response.data?.login?.message.toString(), Snackbar.LENGTH_SHORT).show()
                        if (response.data?.login?.success!!) {
                            val sessionManager = SessionManager(this@LoginActivity)
                            sessionManager.setUser(response.data!!.login.id, "", binding.email.text.toString());
                            Handler().postDelayed({
                                val intent = Intent(this@LoginActivity, MainActivity::class.java);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                                finish()
                            }, 2000)

                        }
                    } else {
                        Snackbar.make(binding.rootLayout, getString(R.string.trouble), Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }

        binding.signup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun validateInput(): Boolean {
        var value = true

        val email = binding.email.text.toString()
        val password = binding.password.text.toString()

        if (!isValidEmail(email)) {
            binding.emailInputLayout.isErrorEnabled = true
            binding.emailInputLayout.error = "Please enter valid email"
            value = false
        } else {
            binding.emailInputLayout.isErrorEnabled = false
        }

        if (password.isEmpty()) {
            binding.passwordInputLayout.isErrorEnabled = true
            binding.passwordInputLayout.error = "Please enter password"
            value = false
        } else {
            binding.passwordInputLayout.isErrorEnabled = false
        }
        return value
    }

    private fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}