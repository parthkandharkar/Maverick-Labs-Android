package com.example.maverick_labs_catalog.Adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.maverick_labs_catalog.R
import com.example.maverick_labs_catalog.fragments.Project
import com.example.maverick_labs_catalog.models.ProjectDelete
import com.example.maverick_labs_catalog.models.Projects
import com.example.maverick_labs_catalog.services.ApiService
import com.example.maverick_labs_catalog.services.ServiceBuilder
import com.example.maverick_labs_catalog.storage.SessionManager
import kotlinx.android.synthetic.main.fragment_project.*
import kotlinx.android.synthetic.main.proj_names.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProjectAdapter(val context: Context, val projects: List<Projects>, private val listener: OnItemClickListener, private val activity: AppCompatActivity): RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>()
{
    private var multiSelect = false
    private var ids = ArrayList<Int>()
    private var actionMode : ActionMode? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.proj_names, parent, false)
        return ProjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = projects[position]
        holder.projnames.text = project.name

        if (ids.contains(project.id)) {
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }

        holder.itemView.projnamescardview.setOnLongClickListener {
            if (!multiSelect) {
                multiSelect = true
                selectItem(holder, project)
                when (actionMode) {
                    null -> {
                        actionMode = activity.startActionMode(actionModeCallback)!!
                        actionMode!!.title = "${ids.size}"
                        true
                    }
                    else -> false
                }
                Log.d("check123", "IDS:${ids}")
            }
            true
        }
        holder.itemView.projnamescardview.setOnClickListener {
            // if the user is in multi-select mode, add it to the multi select list
            if (multiSelect) {
                selectItem(holder, project)
                actionMode!!.title = "${ids.size}"
                if (ids.isEmpty())
                {
                    multiSelect = false
                    actionMode?.let { it1 -> actionModeCallback.onDestroyActionMode(it1) }
                }
                Log.d("check123", "IDS:${ids}")
            }
            else
            {
                val id = project.id
                listener.OnItemClick(id)
            }
        }
    }

    private fun selectItem(holder: ProjectViewHolder, project: Projects) {
        if (ids.contains(project.id)) {
            ids.remove(project.id)
            holder.itemView.setBackgroundColor(Color.WHITE);
        } else {
            ids.add(project.id)
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        }
    }



    override fun getItemCount(): Int {
        return projects.size
    }

    inner class ProjectViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var projnames = itemView.findViewById<TextView>(R.id.projnamescardview)
    }

    interface OnItemClickListener {
        fun OnItemClick(id: Int)
    }

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            val inflater: MenuInflater = mode.menuInflater
            inflater.inflate(R.menu.actionbar_delete, menu)
            return true
        }
        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.action_delete -> {
                    val sessionmanager = SessionManager(context)
                    val apiService = ServiceBuilder.buildService(ApiService::class.java)
                    val requestCall = apiService.projectsmultidelete(token = "Token ${sessionmanager.fetchAuthToken()}", ids)
                    requestCall.enqueue(object : Callback<ProjectDelete> {
                        override fun onResponse(call: Call<ProjectDelete>, response: Response<ProjectDelete>) {
                            if (response.code() == 400) {
                                Toast.makeText(context, "Protected!!Cannot Delete", Toast.LENGTH_SHORT).show()
                            }
                            if (response.code() == 200) {
                                Toast.makeText(context, "Projects Are Deleted Successfully", Toast.LENGTH_SHORT).show()
                            }
                            (activity as AppCompatActivity).supportActionBar?.title = "Projects"
                            val fragmentManager = activity.supportFragmentManager
                            val fragmentTransaction = fragmentManager.beginTransaction()
                            fragmentTransaction.replace(R.id.fragment_container, Project())
                            fragmentTransaction.addToBackStack(null)
                            fragmentTransaction.commit()
                        }

                        override fun onFailure(call: Call<ProjectDelete>, t: Throwable) {
                            Toast.makeText(context,"ERROR!!${t.message}", Toast.LENGTH_LONG).show()
                        }

                    })
                    mode.finish()
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            actionMode = null
            ids.clear()
            notifyDataSetChanged()
            mode.finish()
        }

    }


}