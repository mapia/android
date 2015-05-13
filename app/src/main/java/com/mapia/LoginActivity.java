package com.mapia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.mapia.map.MapActivity;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class LoginActivity extends Activity implements OnClickListener{
    EditText edtID, edtPW;
    Button btnLogin, btnSignup;
    TextView txtHelp;


    // RSA 공개키
    private String publicKey;


    protected void loginCheck(String id, String pw){

        if(id.compareTo("admin")==0 && pw.compareTo("admin")==0){
            Intent i = new Intent(LoginActivity.this, MapActivity.class);
            startActivity(i);
            finish();
        }
        else{

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.mapia.R.layout.activity_login);


        edtID = (EditText)findViewById(com.mapia.R.id.edtID);
        edtPW = (EditText)findViewById(com.mapia.R.id.edtPW);

        btnLogin = (Button)findViewById(com.mapia.R.id.btnLogin);
        btnSignup = (Button) findViewById(R.id.btnSignup);

        btnLogin.setOnClickListener(this);
        btnSignup.setOnClickListener(this);

        txtHelp = (TextView)findViewById(com.mapia.R.id.txtHelpRequest);
        txtHelp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call Website ./Help
            }
        });


        RestRequestHelper requestHelper = RestRequestHelper.newInstance();
        // RSA 공개키를 받아오기 위한 요청
        requestHelper.login(new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                publicKey = jsonObject.get("public_key").toString();
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });

        requestHelper.profile(
                new Callback<JsonObject>() {
                    @Override
                    public void success(JsonObject jsonObject,
                                        Response response) {
                        String resultMessage =
                                jsonObject.get("message").toString();
                        Toast.makeText(LoginActivity.this,
                                resultMessage, Toast.LENGTH_LONG).show();

                        Intent i = new Intent(LoginActivity.this, MapActivity.class);
                        startActivity(i);
                        finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(LoginActivity.this, "토큰 불일치"
                                .toString(), Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                });
    }

    public RSAPublicKey getPublicKeyFromString(String key)
            throws IOException, GeneralSecurityException {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(
                Base64.decode(key, Base64.DEFAULT));
        RSAPublicKey pubKey = null;

        try {
            pubKey = (RSAPublicKey) kf.generatePublic(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pubKey;
    }

    public String encrypt(String rawText, RSAPublicKey publicKey)
            throws IOException, GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(
                "RSA/NONE/OAEPWithSHA1AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return new String(Base64.encode(
                cipher.doFinal(rawText.getBytes()), Base64.DEFAULT));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                String username = edtID.getText().toString();
                String password = edtPW.getText().toString();

                try {
                    String encryptedUsername = encrypt(username,
                            getPublicKeyFromString(publicKey));
                    String encryptedPassword = encrypt(password,
                            getPublicKeyFromString(publicKey));

                    RestRequestHelper requestHelper = RestRequestHelper.newInstance();

                    requestHelper.login(encryptedUsername, encryptedPassword,
                            new Callback<JsonObject>() {
                                @Override
                                public void success(JsonObject jsonObject, Response response) {
                                    Toast.makeText(LoginActivity.this, jsonObject.get("message")
                                            .toString(), Toast.LENGTH_LONG).show();


                                    Intent i = new Intent(LoginActivity.this, MapActivity.class);
                                    startActivity(i);
                                    finish();
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    Toast.makeText(LoginActivity.this, "로그인 실패"
                                            .toString(), Toast.LENGTH_LONG).show();
                                    error.printStackTrace();
                                }
                            });

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.btnSignup:
                startActivity(new Intent(this, SignupActivity.class));
                break;

        }
    }
}