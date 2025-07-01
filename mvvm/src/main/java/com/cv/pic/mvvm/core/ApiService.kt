package com.cv.pic.mvvm.core

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface ApiService {
  @GET
  suspend fun <RES> get(@Url url: String, @QueryMap params: Map<String, Any> = emptyMap()): NetworkResult<RES>

  @POST
  suspend fun<REQ, RES> post(@Url url: String, @Body body: REQ): NetworkResult<RES>

  @PUT
  suspend fun<REQ, RES> put(@Url url: String, @Body body: REQ): NetworkResult<RES>

  @DELETE
  suspend fun<RES> delete(@Url url: String, @QueryMap params: Map<String, Any> = emptyMap()): NetworkResult<RES>
}