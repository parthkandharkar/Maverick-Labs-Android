package com.example.maverick_labs_catalog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.maverick_labs_catalog.models.LoginResponse
import com.example.maverick_labs_catalog.services.ApiService
import com.example.maverick_labs_catalog.services.ServiceBuilder
import com.example.maverick_labs_catalog.storage.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = "Login"
        val usrnme = findViewById<EditText>(R.id.usrnme)
        val pwd = findViewById<EditText>(R.id.pswd)
        val loginbtn = findViewById<Button>(R.id.lgnbtn)

        val username = usrnme.text
        val password = pwd.text

        val sessionmanager= SessionManager(this)

        loginbtn.setOnClickListener {
            if (username.isEmpty() or password.isEmpty())
            {
                Toast.makeText(this@MainActivity, "Username and Password cannot be empty", Toast.LENGTH_LONG).show()
            }
            else
            {
                val apiService = ServiceBuilder.buildService(ApiService::class.java)
                val requestCall = apiService.userLogin(username, password)
                requestCall.enqueue(object : Callback<LoginResponse>
                {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>)
                    {
                        if(response.code()==200)
                        {
                            val info = response.body()
                            Log.d("login","Response:${info}")
                            if (info != null) {
                                sessionmanager.saveAuthToken(info.token)
                                sessionmanager.saveUserId(info.id)
                                sessionmanager.savesuperuser(info.is_superuser)
                            }
                            val intent = Intent(this@MainActivity,ProjectList::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        else if (response.code()==400)
                        {
                            Toast.makeText(this@MainActivity,"Invalid User",Toast.LENGTH_LONG).show()
                        }
                        else
                        {
                            Toast.makeText(this@MainActivity,"Error!!${response}",Toast.LENGTH_LONG).show()
                        }
                    }
                    override fun onFailure(call: Call<LoginResponse>, t: Throwable)
                    {
                        Toast.makeText(this@MainActivity,"ERROR!!${t.message}",Toast.LENGTH_LONG).show()
                    }
                })
            }
        }
    }
}