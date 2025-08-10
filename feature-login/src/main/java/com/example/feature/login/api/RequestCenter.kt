package com.example.feature.login.api

import com.example.core.common.model.User
import com.example.feature.login.network.BaseModel
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * 登录相关API接口
 */
interface RequestCenter {

    /**
     * 用户登录
     */
    @FormUrlEncoded
    @POST("/user/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): BaseModel<User>

    /**
     * 用户注册
     */
    @FormUrlEncoded
    @POST("/user/register")
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("repassword") repassword: String
    ): BaseModel<User>
}

