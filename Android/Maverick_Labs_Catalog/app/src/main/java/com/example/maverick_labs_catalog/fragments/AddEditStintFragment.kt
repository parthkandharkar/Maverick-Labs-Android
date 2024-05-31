package com.example.maverick_labs_catalog.fragments

import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.maverick_labs_catalog.Adapters.ProjectAdapter
import com.example.maverick_labs_catalog.R
import com.example.maverick_labs_catalog.models.*
import com.example.maverick_labs_catalog.services.ApiService
import com.example.maverick_labs_catalog.services.ServiceBuilder
import com.example.maverick_labs_catalog.storage.SessionManager
import kotlinx.android.synthetic.main.fragment_add_edit_stint.*
import kotlinx.android.synthetic.main.fragment_edit_proj.*
import kotlinx.android.synthetic.main.fragment_project.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

var projid : Int = 0
var empid : Int = 0
class AddEditStintFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_edit_stint, container, false)

        val id = this.arguments?.getInt("stintid")

        val deletestint = view.findViewById<Button>(R.id.deletestint)
        val updatestint = view.findViewById<Button>(R.id.updatestint)
        val addstint = view.findViewById<Button>(R.id.addstint)

        if (id == null)
        {
            deletestint.visibility = View.GONE
            updatestint.visibility = View.GONE
        }
        else
        {
            addstint.visibility = View.GONE
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = this.arguments?.getInt("stintid")

        val projectsinfo : ArrayList<ProjectInformation> = ArrayList()
        val employeesinfo : ArrayList<EmpInformation> = ArrayList()

        val sessionmanager = this.context?.let { SessionManager(it.applicationContext) }
        val apiService = ServiceBuilder.buildService(ApiService::class.java)

        val requestCall = apiService.getProjectList(token = "Token ${sessionmanager?.fetchAuthToken()}")
        requestCall.enqueue(object : Callback<List<Projects>> {
            override fun onResponse(call: Call<List<Projects>>, response: Response<List<Projects>>) {
                val info = response.body()
                info!!.forEach {
                    val projectinfo = ProjectInformation(it.id,it.name)
                    projectsinfo.add(projectinfo)
                }
                val sortedprojects = projectsinfo.sortedBy { it.id }
                val dataAdapter1: ArrayAdapter<ProjectInformation> = ArrayAdapter<ProjectInformation>(context!!, android.R.layout.simple_spinner_item, sortedprojects)
                dataAdapter1.notifyDataSetChanged()
                dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                selectproject.adapter = dataAdapter1
            }
            override fun onFailure(call: Call<List<Projects>>, t: Throwable) {
                Toast.makeText(context,"ERROR!!${t.message}", Toast.LENGTH_LONG).show()
            }

        })
        selectproject.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val sortedprojects = projectsinfo.sortedBy { it.id }
                val info: ProjectInformation = sortedprojects[position]
                projid = info.id
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        val requestCall1 = apiService.getemployeeslist(token = "Token ${sessionmanager?.fetchAuthToken()}")
        requestCall1.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                val info = response.body()
                info!!.forEach {
                    val employeeinfo = EmpInformation(it.id,it.username)
                    employeesinfo.add(employeeinfo)
                }
                val sortedemployees = employeesinfo.sortedBy { it.id }
                val dataAdapter: ArrayAdapter<EmpInformation> = ArrayAdapter<EmpInformation>(context!!, android.R.layout.simple_spinner_item, sortedemployees)
                dataAdapter.notifyDataSetChanged()
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                selectemployee.adapter = dataAdapter
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
        selectemployee.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val sortedemployees = employeesinfo.sortedBy { it.id }
                val info: EmpInformation = sortedemployees[position]
                empid = info.id
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }


        if (id != null)
        {
            val requestCall = apiService.getstint(token = "Token ${sessionmanager?.fetchAuthToken()}", id)
            requestCall.enqueue(object : Callback<Stints> {
                override fun onResponse(call: Call<Stints>, response: Response<Stints>) {
                    editrole!!.text = Editable.Factory.getInstance().newEditable(response.body()?.role)
                    editcontribution!!.text = Editable.Factory.getInstance().newEditable(response.body()?.contribution)
                    val sortedprojects = projectsinfo.sortedBy { it.id }
                    val sortedemployees = employeesinfo.sortedBy { it.id }
                    for (i in sortedprojects)
                    {
                        if (response.body()!!.project.id == i.id)
                        {
                            val projectname = sortedprojects.indexOf(i)
                            selectproject.setSelection(projectname)
//                            Log.d("spinnerid","Inside I:$i,${i.id},ProjectID:${response.body()!!.project.id},Index:${projectsinfo.indexOf(i)}")
                        }
                    }

                    for (i in sortedemployees)
                    {
                        if (response.body()!!.employee.id == i.id)
                        {
                            val employeename = sortedemployees.indexOf(i)
                            selectemployee.setSelection(employeename)
                        }
                    }
                }

                override fun onFailure(call: Call<Stints>, t: Throwable) {
                    Toast.makeText(context,"ERROR!!${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }

        addstint.setOnClickListener {
            if(editrole.text.isEmpty() or editcontribution.text.isEmpty())
            {
                Toast.makeText(context,"Fields cannot be Empty",Toast.LENGTH_SHORT).show()
            }
            else
            {
                val sessionmanager = this.context?.let { SessionManager(it.applicationContext) }
                val apiService = ServiceBuilder.buildService(ApiService::class.java)
                val requestCall = apiService.addstint(token = "Token ${sessionmanager?.fetchAuthToken()}",
                        editrole.text.toString(),
                        editcontribution.text.toString(),
                        projid,
                        empid)
                requestCall.enqueue(object : Callback<Stints> {
                    override fun onResponse(call: Call<Stints>, response: Response<Stints>) {
                        Toast.makeText(context, "Stint Added Successfully", Toast.LENGTH_SHORT).show()
                        (activity as AppCompatActivity).supportActionBar?.title = "Stints"
                        val fragmentManager = activity?.supportFragmentManager
                        val fragmentTransaction = fragmentManager?.beginTransaction()
                        fragmentTransaction?.replace(R.id.fragment_container, StintListFrag())
                        fragmentTransaction?.addToBackStack(null)
                        fragmentTransaction?.commit()
                    }

                    override fun onFailure(call: Call<Stints>, t: Throwable) {
                        Toast.makeText(context,"ERROR!!${t.message}", Toast.LENGTH_LONG).show()
                    }

                })

            }
        }

        updatestint.setOnClickListener {
            if(editrole.text.isEmpty() or editcontribution.text.isEmpty())
            {
                Toast.makeText(context,"Fields cannot be Empty",Toast.LENGTH_SHORT).show()
            }
            else
            {
                val sessionmanager = this.context?.let { SessionManager(it.applicationContext) }
                val apiService = ServiceBuilder.buildService(ApiService::class.java)
                val requestCall = apiService.updatestint(token = "Token ${sessionmanager?.fetchAuthToken()}",
                    id,
                    editrole.text.toString(),
                    editcontribution.text.toString(),
                    projid,
                    empid)
                requestCall.enqueue(object : Callback<Stints> {
                    override fun onResponse(call: Call<Stints>, response: Response<Stints>) {
                        Toast.makeText(context, "Stint Updated Successfully", Toast.LENGTH_SHORT).show()
                        (activity as AppCompatActivity).supportActionBar?.title = "Stints"
                        val fragmentManager = activity?.supportFragmentManager
                        val fragmentTransaction = fragmentManager?.beginTransaction()
                        fragmentTransaction?.replace(R.id.fragment_container, StintListFrag())
                        fragmentTransaction?.addToBackStack(null)
                        fragmentTransaction?.commit()
                    }

                    override fun onFailure(call: Call<Stints>, t: Throwable) {
                        Toast.makeText(context,"ERROR!!${t.message}", Toast.LENGTH_LONG).show()
                    }

                })

            }
        }

        deletestint.setOnClickListener {
                val sessionmanager = this.context?.let { SessionManager(it.applicationContext) }
                val apiService = ServiceBuilder.buildService(ApiService::class.java)
                val requestCall = apiService.deletestint(token = "Token ${sessionmanager?.fetchAuthToken()}",id)
                requestCall.enqueue(object : Callback<StintDelete> {
                    override fun onResponse(call: Call<StintDelete>, response: Response<StintDelete>) {
                        if(response.code() == 500)
                        {
                            Toast.makeText(context,"Protected!!Cannot Delete",Toast.LENGTH_SHORT).show()
                        }
                        else
                        {
                            Toast.makeText(context, "Stint Deleted Successfully", Toast.LENGTH_SHORT).show()
                            (activity as AppCompatActivity).supportActionBar?.title = "Stints"
                            val fragmentManager = activity?.supportFragmentManager
                            val fragmentTransaction = fragmentManager?.beginTransaction()
                            fragmentTransaction?.replace(R.id.fragment_container, StintListFrag())
                            fragmentTransaction?.addToBackStack(null)
                            fragmentTransaction?.commit()
                        }
                    }
                    override fun onFailure(call: Call<StintDelete>, t: Throwable) {
                        Toast.makeText(context,"ERROR!!${t.message}", Toast.LENGTH_LONG).show()
                    }

                })
        }
    }
}