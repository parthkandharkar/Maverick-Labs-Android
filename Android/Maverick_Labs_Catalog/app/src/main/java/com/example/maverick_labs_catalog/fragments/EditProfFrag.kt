package com.example.maverick_labs_catalog.fragments

import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.maverick_labs_catalog.R
import com.example.maverick_labs_catalog.models.UpdateUser
import com.example.maverick_labs_catalog.models.User
import com.example.maverick_labs_catalog.services.ApiService
import com.example.maverick_labs_catalog.services.ServiceBuilder
import com.example.maverick_labs_catalog.storage.SessionManager
import kotlinx.android.synthetic.main.fragment_edit_prof.*
import kotlinx.android.synthetic.main.fragment_edit_prof.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class EditProfFrag : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_prof, container, false)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sessionmanager = this.context?.let { SessionManager(it.applicationContext) }
        val apiService = ServiceBuilder.buildService(ApiService::class.java)
        val requestCall = apiService.getuserdetails(token = "Token ${sessionmanager?.fetchAuthToken()}")
        requestCall.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                editusername.setText(response.body()?.username)
                editfirstname.setText(response.body()?.first_name)
                editlastname.setText(response.body()?.last_name)
                editemail.setText(response.body()?.email)
                editdesignation.setText(response.body()?.designation)
                editgender.setText(response.body()?.gender)
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(context,"ERROR!!${t.message}", Toast.LENGTH_LONG).show()
            }

        })

        view.editprofsubbtn.setOnClickListener {
            if (view.editusername.text.isEmpty())
            {
                Toast.makeText(context,"Username Cannot Be Empty",Toast.LENGTH_SHORT).show()
            }
            else
            {
                val sessionmanager = this.context?.let { SessionManager(it.applicationContext) }
                val apiService = ServiceBuilder.buildService(ApiService::class.java)
                val requestCall = apiService.updateuser(token = "Token ${sessionmanager?.fetchAuthToken()}", sessionmanager?.fetchUserId(), view.editusername.text,
                        view.editfirstname.text, view.editlastname.text, view.editemail.text, view.editdesignation.text, view.editgender.text)
                requestCall.enqueue(object : Callback<UpdateUser> {
                    override fun onResponse(call: Call<UpdateUser>, response: Response<UpdateUser>)
                    {
                        if(response.code() == 400)
                        {
                            Toast.makeText(context,"Enter Correct Email",Toast.LENGTH_SHORT).show()
                        }
                        else
                        {
                            Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show()
                            (activity as AppCompatActivity).supportActionBar?.title = "Projects"
                            val fragmentManager = activity?.supportFragmentManager
                            val fragmentTransaction = fragmentManager?.beginTransaction()
                            fragmentTransaction?.replace(R.id.fragment_container, Project())
                            fragmentTransaction?.addToBackStack(null)
                            fragmentTransaction?.commit()
                        }
                    }

                    override fun onFailure(call: Call<UpdateUser>, t: Throwable) {
                        Toast.makeText(context,"ERROR!!${t.message}", Toast.LENGTH_LONG).show()
                    }

                })
            }
        }


    }
}