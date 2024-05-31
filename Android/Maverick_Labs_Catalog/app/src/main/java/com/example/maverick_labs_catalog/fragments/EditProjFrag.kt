package com.example.maverick_labs_catalog.fragments


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.maverick_labs_catalog.R
import com.example.maverick_labs_catalog.models.*
import com.example.maverick_labs_catalog.services.ApiService
import com.example.maverick_labs_catalog.services.ServiceBuilder
import com.example.maverick_labs_catalog.storage.SessionManager
import kotlinx.android.synthetic.main.fragment_edit_proj.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

var clientid : Int = 0

@Suppress("DEPRECATION")
class EditProjFrag : Fragment() {


    @SuppressLint("CutPasteId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_edit_proj, container, false)

        val id = this.arguments?.getInt("projectid")

        val startdate = view.findViewById<EditText>(R.id.editprojstart)
        val compdate = view.findViewById<EditText>(R.id.editprojcomp)
        val projdelete =view.findViewById<Button>(R.id.projdelete)
        val projupdate = view.findViewById<Button>(R.id.projupdate)
        val projadd = view.findViewById<Button>(R.id.projadd)
        val calendar = Calendar.getInstance()

        if(id == null)
        {
            projdelete.visibility = View.GONE
            projupdate.visibility = View.GONE

        }
        else
        {
            projadd.visibility = View.GONE
        }

        startdate.setOnClickListener {
            val datepicker = DatePickerDialog(this.requireContext(), OnDateSetListener { view, year, month, dayOfMonth ->
                startdate!!.text = Editable.Factory.getInstance().newEditable("${year}-${month + 1}-${dayOfMonth}")
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            datepicker.show()
        }

        compdate.setOnClickListener {
            val datepicker = DatePickerDialog(this.requireContext(), OnDateSetListener { view, year, month, dayOfMonth ->
                compdate!!.text = Editable.Factory.getInstance().newEditable("${year}-${month + 1}-${dayOfMonth}")
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            datepicker.show()
        }
        return view
    }


    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = this.arguments?.getInt("projectid")

        val clientinfolist : ArrayList<ClientInformation> = ArrayList()

        val sessionmanager = this.context?.let { SessionManager(it.applicationContext) }
        val apiService = ServiceBuilder.buildService(ApiService::class.java)
        val requestCall = apiService.getclientlist(token = "Token ${sessionmanager?.fetchAuthToken()}")
        requestCall.enqueue(object : Callback<List<ClientList>> {
            override fun onResponse(call: Call<List<ClientList>>, response: Response<List<ClientList>>) {
                val info = response.body()
                info?.forEach {
                    val clientinformation = ClientInformation(it.id,it.name)
                    clientinfolist.add(clientinformation)
                }
                val sortedclientlist = clientinfolist.sortedBy { it.id }
                val dataAdapter: ArrayAdapter<ClientInformation> = ArrayAdapter<ClientInformation>(context!!, android.R.layout.simple_spinner_item, sortedclientlist)
                dataAdapter.notifyDataSetChanged()
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                clientlist.adapter = dataAdapter
            }

            override fun onFailure(call: Call<List<ClientList>>, t: Throwable) {
                Toast.makeText(context,"ERROR!!${t.message}", Toast.LENGTH_LONG).show()
            }

        })
        clientlist.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
            {
                   val sortedclientlist = clientinfolist.sortedBy { it.id }
                    val info: ClientInformation = sortedclientlist[position]
                    clientid = info.id
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        if (id != null)
        {
            val sdf = SimpleDateFormat("yyyy-MM-dd");
            val sessionmanager = this.context?.let { SessionManager(it.applicationContext) }
            val apiService = ServiceBuilder.buildService(ApiService::class.java)
            val requestCall = apiService.getprojectdetails(token = "Token ${sessionmanager?.fetchAuthToken()}", id)
            requestCall.enqueue(object : Callback<Projects> {
                override fun onResponse(call: Call<Projects>, response: Response<Projects>) {
                    editprojname!!.text = Editable.Factory.getInstance().newEditable(response.body()?.name)
                    editprojdesc!!.text = Editable.Factory.getInstance().newEditable(response.body()?.description)
                    editprojstart!!.text = Editable.Factory.getInstance().newEditable(sdf.format(response.body()?.start_date))
                    editprojcomp!!.text = Editable.Factory.getInstance().newEditable(sdf.format(response.body()?.completion_date))
                    val sortedclientlist = clientinfolist.sortedBy { it.id }
                    for (i in sortedclientlist)
                    {
                        if (response.body()!!.client.id == i.id)
                        {
                            val clientname = sortedclientlist.indexOf(i)
                            clientlist.setSelection(clientname)
                        }
                    }
                }

                override fun onFailure(call: Call<Projects>, t: Throwable) {
                    Toast.makeText(context,"ERROR!!${t.message}", Toast.LENGTH_LONG).show()
                }

            })
        }


        projadd.setOnClickListener {
            if (editprojname.text.isEmpty() or editprojdesc.text.isEmpty() or editprojstart.text.isEmpty() or editprojcomp.text.isEmpty())
            {
                Toast.makeText(context,"Fields Cannot be Empty",Toast.LENGTH_SHORT).show()
            }
            else
            {
                val startdate = view.findViewById<EditText>(R.id.editprojstart)
                val compdate = view.findViewById<EditText>(R.id.editprojcomp)
                val projname =  view.findViewById<EditText>(R.id.editprojname)
                val projdesc =  view.findViewById<EditText>(R.id.editprojdesc)
                val sdf = SimpleDateFormat("yyyy-MM-dd");
                val name = projname.text.toString()
                val desc = projdesc.text.toString()
                val start= startdate.text
                val comp = compdate.text

                val startformat  = sdf.parse(start.toString())
                val startformat1= sdf.format(startformat)
                val compformat = sdf.parse(comp.toString())
                val compformat1 = sdf.format(compformat)

                val sessionmanager = this.context?.let { SessionManager(it.applicationContext) }
                val apiService = ServiceBuilder.buildService(ApiService::class.java)
                val requestCall = apiService.addproject(
                    token = "Token ${sessionmanager?.fetchAuthToken()}",
                    name,
                    desc,
                    startformat1,
                    compformat1,
                    clientid
                )
                requestCall.enqueue(object : Callback<Projects> {
                    override fun onResponse(call: Call<Projects>, response: Response<Projects>) {
                        Toast.makeText(context, "Project Added Successfully", Toast.LENGTH_SHORT).show()
                        (activity as AppCompatActivity).supportActionBar?.title = "Projects"
                        val fragmentManager = activity?.supportFragmentManager
                        val fragmentTransaction = fragmentManager?.beginTransaction()
                        fragmentTransaction?.replace(R.id.fragment_container, Project())
                        fragmentTransaction?.addToBackStack(null)
                        fragmentTransaction?.commit()
                    }

                    override fun onFailure(call: Call<Projects>, t: Throwable) {
                        Toast.makeText(context,"ERROR!!${t.message}", Toast.LENGTH_LONG).show()
                    }

                })
            }

        }

        projupdate.setOnClickListener {
            if (editprojname.text.isEmpty() or editprojdesc.text.isEmpty() or editprojstart.text.isEmpty() or editprojcomp.text.isEmpty())
            {
                Toast.makeText(context,"Fields Cannot be Empty",Toast.LENGTH_SHORT).show()
            }
            else
            {
                val startdate = view.findViewById<EditText>(R.id.editprojstart)
                val compdate = view.findViewById<EditText>(R.id.editprojcomp)
                val projname =  view.findViewById<EditText>(R.id.editprojname)
                val projdesc =  view.findViewById<EditText>(R.id.editprojdesc)
                val sdf = SimpleDateFormat("yyyy-MM-dd");
                val name = projname.text.toString()
                val desc = projdesc.text.toString()
                val start= startdate.text
                val comp = compdate.text

                val startformat  = sdf.parse(start.toString())
                val startformat1= sdf.format(startformat)
                val compformat = sdf.parse(comp.toString())
                val compformat1 = sdf.format(compformat)

                val sessionmanager = this.context?.let { SessionManager(it.applicationContext) }
                val apiService = ServiceBuilder.buildService(ApiService::class.java)
                val requestCall = apiService.updateproject(
                    token = "Token ${sessionmanager?.fetchAuthToken()}",
                    id,
                    name,
                    desc,
                    startformat1,
                    compformat1,
                    clientid
                )
                requestCall.enqueue(object : Callback<ProjectUpdateresponse> {
                    override fun onResponse(call: Call<ProjectUpdateresponse>, response: Response<ProjectUpdateresponse>) {
                        Toast.makeText(context, "Project Updated Successfully", Toast.LENGTH_SHORT).show()
                        (activity as AppCompatActivity).supportActionBar?.title = "Projects"
                        val fragmentManager = activity?.supportFragmentManager
                        val fragmentTransaction = fragmentManager?.beginTransaction()
                        fragmentTransaction?.replace(R.id.fragment_container, Project())
                        fragmentTransaction?.addToBackStack(null)
                        fragmentTransaction?.commit()
                    }

                    override fun onFailure(call: Call<ProjectUpdateresponse>, t: Throwable) {
                        Toast.makeText(context,"ERROR!!${t.message}", Toast.LENGTH_LONG).show()
                    }

                })
            }
        }

        projdelete.setOnClickListener {
            val sessionmanager = this.context?.let { SessionManager(it.applicationContext) }
            val apiService = ServiceBuilder.buildService(ApiService::class.java)
            val requestCall = apiService.deleteproject(token = "Token ${sessionmanager?.fetchAuthToken()}",id)
            requestCall.enqueue(object : Callback<ProjectDelete> {
                override fun onResponse(call: Call<ProjectDelete>, response: Response<ProjectDelete>) {
                    if(response.code() == 500)
                    {
                        Toast.makeText(context,"Protected!!Cannot Delete",Toast.LENGTH_SHORT).show()
                    }
                    else {
                    Toast.makeText(context,"Project Deleted Successfully",Toast.LENGTH_SHORT).show()
                    (activity as AppCompatActivity).supportActionBar?.title = "Projects"
                    val fragmentManager = activity?.supportFragmentManager
                    val fragmentTransaction = fragmentManager?.beginTransaction()
                    fragmentTransaction?.replace(R.id.fragment_container, Project())
                    fragmentTransaction?.addToBackStack(null)
                    fragmentTransaction?.commit()
                    }
                }

                override fun onFailure(call: Call<ProjectDelete>, t: Throwable) {
                    Toast.makeText(context,"ERROR!!${t.message}", Toast.LENGTH_LONG).show()
                }

            })
        }
    }
}