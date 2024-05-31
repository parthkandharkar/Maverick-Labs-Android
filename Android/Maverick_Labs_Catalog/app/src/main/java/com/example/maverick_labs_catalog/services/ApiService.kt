package com.example.maverick_labs_catalog.services

import android.text.Editable
import com.example.maverick_labs_catalog.models.*
import retrofit2.Call
import retrofit2.http.*
import java.util.ArrayList

interface ApiService {

    @GET("projects/")
    fun getProjectList(@Header("Authorization") token : String): Call<List<Projects>>

    @FormUrlEncoded
    @POST("employees/userlogin/")
    fun userLogin(
            @Field("username") username: Editable,
            @Field("password") password: Editable
    ) : Call<LoginResponse>


    @GET("stints/")
    fun getstintlist(@Header("Authorization") token : String): Call<List<Stints>>

    @GET("projects/{id}/")
    fun getprojectdetails(@Header("Authorization") token: String,@Path("id")id : Int) : Call<Projects>


    @GET("employees/details/")
    fun getuserdetails(@Header("Authorization") token: String) : Call<User>

    @FormUrlEncoded
    @POST("employees/password/")
    fun changepwd(
            @Header("Authorization") token: String,
            @Field("password") password: Editable,
            @Field("confirmpassword") confirmpassword: Editable
    ): Call<ChangePwd>


    @FormUrlEncoded
    @PATCH("employees/{id}/")
    fun updateuser(
            @Header("Authorization") token: String,
            @Path("id") id: Int?,
            @Field("username") username: Editable,
            @Field("first_name") firstname: Editable,
            @Field("last_name") lastname: Editable,
            @Field("email") email: Editable,
            @Field("designation") designation: Editable,
            @Field("gender") gender: Editable,
    ) : Call<UpdateUser>

    @GET("clients/")
    fun getclientlist(@Header("Authorization") token: String) : Call<List<ClientList>>

    @FormUrlEncoded
    @POST("projects/")
    fun addproject(
        @Header("Authorization") token: String,
        @Field("name") name:String,
        @Field("description") description:String,
        @Field("start_date") start_date: String,
        @Field("completion_date") completion_date: String,
        @Field("client_id") client_id: Int,
    ) : Call<Projects>

    @FormUrlEncoded
    @PATCH("projects/{id}/")
    fun updateproject(@Header("Authorization") token: String,
                      @Path("id") id: Int?,
                      @Field("name") name:String,
                      @Field("description") description:String,
                      @Field("start_date") start_date: String,
                      @Field("completion_date") completion_date: String,
                      @Field("client") client: Int,
                        ) : Call<ProjectUpdateresponse>

    @DELETE("projects/{id}/")
    fun deleteproject(
        @Header("Authorization") token: String,
        @Path("id") id: Int?
    ) : Call<ProjectDelete>

    @GET("employees/")
    fun getemployeeslist(@Header("Authorization")token: String): Call<List<User>>

    @GET("stints/{id}/")
    fun getstint(
                @Header("Authorization") token: String,
                 @Path("id")id: Int?) : Call<Stints>

    @FormUrlEncoded
    @POST("stints/")
    fun addstint(
        @Header("Authorization") token: String,
        @Field("role") role : String,
        @Field("contribution") contribution: String,
        @Field("project_id") project_id: Int,
        @Field("employee_id") employee_id: Int) : Call<Stints>

    @FormUrlEncoded
    @PATCH("stints/{id}/")
    fun updatestint(
        @Header("Authorization") token: String,
        @Path("id") id: Int?,
        @Field("role") role : String,
        @Field("contribution") contribution: String,
        @Field("project_id") project_id: Int,
        @Field("employee_id") employee_id: Int) : Call<Stints>


    @DELETE("stints/{id}/")
    fun deletestint(
        @Header("Authorization") token: String,
        @Path("id") id: Int?,) : Call<StintDelete>


    @POST("projects/delete1/")
    fun projectsmultidelete(
            @Header("Authorization") token: String,
            @Body id : ArrayList<Int>
            ) : Call<ProjectDelete>

    @POST("stints/delete1/")
    fun stintsmultidelete(
            @Header("Authorization") token: String,
            @Body id : ArrayList<Int>
    ) : Call<ProjectDelete>

}