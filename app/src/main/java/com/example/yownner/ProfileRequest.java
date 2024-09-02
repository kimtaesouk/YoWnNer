package com.example.yownner;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ProfileRequest extends StringRequest {

    private static String IP_ADDRESS = "49.247.32.169";

    final static private String URL = "http://"+IP_ADDRESS+"/YoWnNer/profile.php";

    private Map<String, String> profileData;


    public ProfileRequest(String pid,  Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        profileData = new HashMap<>();
        System.out.println("1234 " + pid);
        profileData.put("pid" , pid);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return profileData;
    }
}
