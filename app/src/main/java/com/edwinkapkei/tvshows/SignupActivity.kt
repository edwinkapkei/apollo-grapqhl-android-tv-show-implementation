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
import com.edwinkapkei.tvshows.databinding.ActivitySignupBinding
import com.google.android.material.snackbar.Snackbar

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signup.setOnClickListener {
            if (validateInput()) {
                binding.signup.visibility = View.GONE
                binding.progressbar.visibility = View.VISIBLE

                lifecycleScope.launchWhenCreated {
                    val mutation = SignUpMutation(name = binding.name.text.toString(), email = binding.email.text.toString(), password = binding.password.text.toString());
                    val response = try {
                        apolloClient.mutate(mutation).toDeferred().await()
                    } catch (e: ApolloException) {
                        null
                    }

                    binding.signup.visibility = View.VISIBLE
                    binding.progressbar.visibility = View.GONE
                    if (response != null) {
                        Snackbar.make(binding.rootLayout, response.data?.signup?.message.toString(), Snackbar.LENGTH_LONG).show()
                        if (response.data?.signup?.success!!) {
                            val sessionManager = SessionManager(this@SignupActivity)
                            sessionManager.setUser(response.data!!.signup.id, binding.name.text.toString(), binding.email.text.toString());

                            Handler().postDelayed({
                                val intent = Intent(this@SignupActivity, MainActivity::class.java);
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
    }

    private fun validateInput(): Boolean {
        var value = true

        val name = binding.name.text.toString()
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()

        if (name.isEmpty()) {
            binding.nameInputLayout.isErrorEnabled = true
            binding.nameInputLayout.error = "Please enter name"
            value = false
        } else {
            binding.nameInputLayout.isErrorEnabled = false
        }

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