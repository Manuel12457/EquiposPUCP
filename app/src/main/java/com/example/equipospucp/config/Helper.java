package com.example.equipospucp.config;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Helper {
    private String to;
    private String subject;
    private String body;
    private Context context;

    public Helper(String to, String subject, String body, Context context) {
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void sendEmail(){
        RequestQueue queue = Volley.newRequestQueue(this.context);
        String url = "http://ec2-52-207-211-253.compute-1.amazonaws.com:9000/api/enviarCorreo";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("to",this.to);
            jsonObject.put("subject",this.subject);
            jsonObject.put("body",this.body);
        }catch (JSONException e){
            System.out.println(e);
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getContext(),"Peticion exitosa",Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"Peticion fallida",Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonObjectRequest);
    }

}
