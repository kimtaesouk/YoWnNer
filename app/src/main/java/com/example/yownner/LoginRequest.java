package com.example.yownner;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {

    private static String IP_ADDRESS = "49.247.32.169";

    final static private String URL = "http://"+IP_ADDRESS+"/YoWnNer/login.php";

    private Map<String, String> loginData;


    public LoginRequest(String email, String pw,  Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        loginData = new HashMap<>();
        loginData.put("email" , email);
        loginData.put("pw" , pw);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return loginData;
    }
}
