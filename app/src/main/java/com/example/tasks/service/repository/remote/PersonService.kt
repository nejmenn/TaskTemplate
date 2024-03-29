package com.example.tasks.service.repository.remote

import com.example.tasks.service.repository.remote.model.ResponseLogin
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface PersonService {

    @POST("Authentication/Login")
    @FormUrlEncoded
    fun login(@Field("email") email: String, @Field("password") password: String) : Call<ResponseLogin>

    @POST("Authentication/Create")
    @FormUrlEncoded
    fun create(@Field("email") email: String,
               @Field("password") password: String,
               @Field("name") name: String,
               @Field("receivenews") receivenews: Boolean) : Call<ResponseLogin>
}