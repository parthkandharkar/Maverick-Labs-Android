package com.example.maverick_labs_catalog.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.maverick_labs_catalog.Adapters.ProjectAdapter
import com.example.maverick_labs_catalog.ProjectList
import com.example.maverick_labs_catalog.R
import com.example.maverick_labs_catalog.models.ChangePwd
import com.example.maverick_labs_catalog.models.Projects
import com.example.maverick_labs_catalog.services.ApiService
import com.example.maverick_labs_catalog.services.ServiceBuilder
import com.example.maverick_labs_catalog.storage.SessionManager
import kotlinx.android.synthetic.main.fragment_change_pwd.view.*
import kotlinx.android.synthetic.main.fragment_project.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ChangePwdFrag : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_change_pwd, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pwd = view.findViewById<EditText>(R.id.enterpwd)
        val confirmpwd = view.findViewById<EditText>(R.id.reenterpwd)

        val password = pwd.text
        val confirmpassword =confirmpwd.text

        view.changepwdbtn.setOnClickListener {
            if (password.isEmpty() or confirmpassword.isEmpty())
            {
                Toast.makeText(context, "Password fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
            else
            {
                if (password.toString() == confirmpassword.toString())
                {
                    val sessionmanager = this.context?.let { SessionManager(it.applicationContext) }
                    val apiService = ServiceBuilder.buildService(ApiService::class.java)
                    val requestCall = apiService.changepwd(token = "Token ${sessionmanager?.fetchAuthToken()}", password, confirmpassword)
                    requestCall.enqueue(object : Callback<ChangePwd> {
                        override fun onResponse(call: Call<ChangePwd>, response: Response<ChangePwd>) {
                            Toast.makeText(context, response.body()?.msg.toString(), Toast.LENGTH_SHORT).show()
                            (activity as AppCompatActivity).supportActionBar?.title = "Projects"
                            val fragmentManager = activity?.supportFragmentManager
                            val fragmentTransaction = fragmentManager?.beginTransaction()
                            fragmentTransaction?.replace(R.id.fragment_container, Project())
                            fragmentTransaction?.addToBackStack(null)
                            fragmentTransaction?.commit()
                        }

                        override fun onFailure(call: Call<ChangePwd>, t: Throwable) {
                            Toast.makeText(context,"ERROR!!${t.message}", Toast.LENGTH_LONG).show()
                        }

                    })
                }
                else
                {
                    Toast.makeText(context, "Passwords are not matching", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}