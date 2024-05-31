package com.example.maverick_labs_catalog

import android.content.Intent
import android.media.session.MediaSession
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.navigation.Navigation
import com.example.maverick_labs_catalog.fragments.*
import com.example.maverick_labs_catalog.models.Projects
import com.example.maverick_labs_catalog.services.ApiService
import com.example.maverick_labs_catalog.services.ServiceBuilder
import com.example.maverick_labs_catalog.storage.SessionManager
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_project_list.*
import kotlinx.android.synthetic.main.content_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProjectList : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener,Communicator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_list)
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(this,drawerlayout,toolbar,R.string.open,R.string.close)
        toggle.isDrawerIndicatorEnabled = true
        drawerlayout.addDrawerListener(toggle)
        toggle.syncState()
        navlayout.setNavigationItemSelectedListener(this)
        setToolbarTitle("Home")
        val fragment = supportFragmentManager.beginTransaction()
        fragment.replace(R.id.fragment_container,Project()).commit()
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerlayout.closeDrawer(GravityCompat.START)
        when(item.itemId){
            R.id.home ->{
                setToolbarTitle("Home")
                val fragment = supportFragmentManager.beginTransaction()
                fragment.replace(R.id.fragment_container,Project()).commit()
            }
            R.id.project ->{
                setToolbarTitle("Projects")
                val fragment = supportFragmentManager.beginTransaction()
                fragment.replace(R.id.fragment_container,Project()).commit()
            }
            R.id.stint ->{
                setToolbarTitle("Stints")
                val fragment = supportFragmentManager.beginTransaction()
                fragment.replace(R.id.fragment_container,StintListFrag()).commit()
            }
            R.id.editprofile ->{
                setToolbarTitle("Edit Profile")
                val fragment = supportFragmentManager.beginTransaction()
                fragment.replace(R.id.fragment_container,EditProfFrag()).commit()
            }
            R.id.changepwd ->{
                setToolbarTitle("Change Password")
                val fragment = supportFragmentManager.beginTransaction()
                fragment.replace(R.id.fragment_container,ChangePwdFrag()).commit()
            }
            R.id.logout ->{
                logout()
                Toast.makeText(this,"You have been logged out",Toast.LENGTH_SHORT).show()
            }
            R.id.chart ->{
                setToolbarTitle("Chart")
                val fragment = supportFragmentManager.beginTransaction()
                fragment.replace(R.id.fragment_container,ChartFragment()).commit()
            }
        }
        return true
    }

    fun setToolbarTitle(title:String){
        supportActionBar?.title = title
    }

    fun logout()
    {
        val sessionmanager = SessionManager(applicationContext)
        sessionmanager.logout()
        val intent = Intent(this,MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun passdata(id: Int) {
        val editproj = EditProjFrag()
        val bundle = Bundle()
        bundle.putInt("projectid",id)
        editproj.arguments = bundle
        setToolbarTitle("Edit Project")
        val fragment = supportFragmentManager.beginTransaction()
        fragment.replace(R.id.fragment_container,editproj,"editproj").addToBackStack(null).commit()
    }

    override fun stintpassdata(id: Int) {
        val editstint = AddEditStintFragment()
        val bundle = Bundle()
        bundle.putInt("stintid",id)
        editstint.arguments = bundle
        setToolbarTitle("Edit Stint")
        val fragment = supportFragmentManager.beginTransaction()
        fragment.replace(R.id.fragment_container,editstint,"editstint").addToBackStack(null).commit()
    }
}