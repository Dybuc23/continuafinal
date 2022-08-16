package com.example.continuafinal.ui.register

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.continuafinal.R
import com.example.continuafinal.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val loginViewModel =
            ViewModelProvider(this)[RegisterViewModel::class.java]

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val editTextName = binding.editTextName
        val editTextLastname = binding.editTextLastname
        val editTextUsername = binding.editTextUsername
        val editTextEmail = binding.editTextEmail
        val editTextPassword = binding.editTextPassword
        val textView: TextView = binding.textLogin

        // firebase auth instance
        binding.register.setOnClickListener(View.OnClickListener {
            val name = editTextName.text.toString()
            val lastname = editTextLastname.text.toString()
            val username = editTextUsername.text.toString()
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            if(email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Please enter email/password", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            } else {
                register(name, lastname, username, email, password)
            }
        })



        loginViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun signIn(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = firebaseAuth.currentUser
                    Toast.makeText(context, "Authentication success.", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("Error", task.exception.toString())
                    // If sign in fails, display a message to the user.
                    Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
        getUser()
    }

    private fun signOut() {
        firebaseAuth.signOut()
    }

    private fun register(name: String,lastname: String,username: String,email: String, password: String) {
        val user = hashMapOf(
            "name" to name,
            "lastname" to lastname,
            "username" to username,
            "email" to email,
            "password" to password
        )
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = firebaseAuth.currentUser
                    Toast.makeText(context, "Register success.", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("Error", task.exception.toString())
                    // If sign in fails, display a message to the user.
                    Toast.makeText(context, "Register failed.", Toast.LENGTH_SHORT).show()
                }
            }
        saveUser(user)
    }

    private fun saveUser(user: HashMap<String, String>) {
        db.collection("users_registration").add(user)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(context, "User saved successfully ${documentReference.id}", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getUser(){
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("User", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("User", "Error getting documents.", exception)
            }
    }

}