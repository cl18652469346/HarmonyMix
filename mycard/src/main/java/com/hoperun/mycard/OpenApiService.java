package com.hoperun.mycard;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface OpenApiService {
    String BASE_URL = "https://api.tianapi.com";

    // http://api.tianapi.com/txapi/naowan/index
//    @HTTP(method = "GET", path = "/txapi/naowan/index")
    @GET("/txapi/naowan/index")
    Observable<Kid> getKid(@Query("key") String key, @Query("num") int num);
}
