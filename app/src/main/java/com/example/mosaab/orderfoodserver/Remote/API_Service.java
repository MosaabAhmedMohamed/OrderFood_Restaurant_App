package com.example.mosaab.orderfoodserver.Remote;

import com.example.mosaab.orderfoodserver.model.Response;
import com.example.mosaab.orderfoodserver.model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface API_Service {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAJivUrcg:APA91bFYvI4U_ob-6XF63K8bf6_mQm5qCdypK4gyc4KSO7XKzR7zNKxxeYtKHyaoPkCiPDirJe-ctJnSOtX3nmtVxY8l_O7bdGt6fAUpjTJyvwpe7Us88--Ox8vpQciADq6tb9I-HU1h"
            }
    )

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);

}