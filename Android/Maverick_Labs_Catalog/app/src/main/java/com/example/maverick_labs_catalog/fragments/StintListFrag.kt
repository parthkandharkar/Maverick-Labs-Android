package com.example.maverick_labs_catalog.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.maverick_labs_catalog.Adapters.StintAdapter
import com.example.maverick_labs_catalog.Communicator
import com.example.maverick_labs_catalog.R
import com.example.maverick_labs_catalog.models.Stints
import com.example.maverick_labs_catalog.services.ApiService
import com.example.maverick_labs_catalog.services.ServiceBuilder
import com.example.maverick_labs_catalog.storage.SessionManager
import kotlinx.android.synthetic.main.fragment_project.*
import kotlinx.android.synthetic.main.fragment_stint_list.*
import kotlinx.android.synthetic.main.fragment_stint_list.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class StintListFrag : Fragment(), StintAdapter.OnItemClickListener {

    lateinit var adapter : StintAdapter
    lateinit var communicator: Communicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_stint_list, container, false)
        view.addstintbtn.setOnClickListener {
            (activity as AppCompatActivity).supportActionBar?.title = "Add Stint"
            val fragmentManager = activity?.supportFragmentManager
            val fragmentTransaction = fragmentManager?.beginTransaction()
            fragmentTransaction?.replace(R.id.fragment_container,AddEditStintFragment())
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sessionmanager = this.context?.let { SessionManager(it.applicationContext) }
        val apiService = ServiceBuilder.buildService(ApiService::class.java)
        val requestCall = apiService.getstintlist(token = "Token ${sessionmanager?.fetchAuthToken()}")
        requestCall.enqueue(object : Callback<List<Stints>> {
            override fun onResponse(call: Call<List<Stints>>, response: Response<List<Stints>>) {
                val info = response.body()
                val info1 = info?.sortedBy { it.id }
                val divideritem = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
                stintlistrecyclerview.addItemDecoration(divideritem)
                adapter = StintAdapter(context!!.applicationContext,info1!!,this@StintListFrag,activity as AppCompatActivity)
                stintlistrecyclerview.adapter = adapter
                stintlistrecyclerview.layoutManager = LinearLayoutManager(context?.applicationContext)
            }

            override fun onFailure(call: Call<List<Stints>>, t: Throwable) {
                Toast.makeText(context,"ERROR!!${t.message}", Toast.LENGTH_LONG).show()
            }

        })
    }

    override fun OnItemClick(id: Int) {

        communicator = activity as Communicator
        communicator.stintpassdata(id)
    }
}