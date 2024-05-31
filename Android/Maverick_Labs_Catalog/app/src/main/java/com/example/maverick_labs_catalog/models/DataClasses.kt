package com.example.maverick_labs_catalog.models

import java.util.*

data class Projects(val id : Int, val name :String, val description : String, val start_date : Date, val completion_date : Date ,val client : ClientList)

data class LoginResponse(val msg: String,val token : String ,val id : Int,val is_superuser : Boolean)

data class Stints(val id :Int,val role :String, val contribution: String,val project: Projects,val employee: User)

data class User(val id: Int,val username: String,val first_name : String,val last_name : String,val designation : String,val gender : String
                     ,val email : String ,val password : String)

data class ChangePwd(val msg : String,val password: String)

data class UpdateUser(val id: Int,val username: String,val first_name : String,val last_name : String,val designation : String,val gender : String
                      ,val email : String )

data class ClientList(val id : Int,val name: String, val number_of_projects: Int, val location: String)

data class ProjectDelete(val msg: String)

data class ProjectUpdateresponse(val id : Int,val client : Int, val name: String, val description : String,val start_date: Date,val completion_date: Date)

data class StintDelete(val msg: String)
