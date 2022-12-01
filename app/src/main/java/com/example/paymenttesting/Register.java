package com.example.paymenttesting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    TextInputLayout fullName,email,password,phone,vehicleNo;
    Button registerBtn,goToLogin;
    RadioButton isAdminbtn,isUserbtn;
    boolean valid = true;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_register);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        fullName = findViewById(R.id.registerName);
        email = findViewById(R.id.registerEmail);
        password = findViewById(R.id.registerPassword);
        phone = findViewById(R.id.registerPhone);
        vehicleNo = findViewById(R.id.registerVehicleNo);

        registerBtn = findViewById(R.id.registerBtn);
        goToLogin = findViewById(R.id.gotoLogin);

        isAdminbtn = findViewById(R.id.isAdmin);
        isUserbtn = findViewById(R.id.isUser);

        //check radio button logic
        isUserbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(compoundButton.isChecked()){
                    isAdminbtn.setChecked(false);
                }
            }
        });

        isAdminbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(compoundButton.isChecked()){
                    isUserbtn.setChecked(false);
                }
            }
        });


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkField(fullName);
                checkField(email);
                checkField(password);
                checkField(phone);
                checkField(vehicleNo);

                //radio button validation
                if(!(isAdminbtn.isChecked() || isUserbtn.isChecked())){

                    Toast.makeText(Register.this, "Select the account type", Toast.LENGTH_SHORT).show();
                    return;

                }

                if(valid){
                    //start the user registration process
                    fAuth.createUserWithEmailAndPassword(email.getEditText().getText().toString(),password.getEditText().getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = fAuth.getCurrentUser();
                            Toast.makeText(Register.this, "Account Created Successfully:)", Toast.LENGTH_SHORT).show();

                            DocumentReference df = fStore.collection("Users").document(user.getUid());
                            Map<String,Object> userInfo = new HashMap<>();

                            //Data to be stored in database for a user(this map object is to be passed to admin later)
                            userInfo.put("FullName",fullName.getEditText().getText().toString());
                            userInfo.put("UserEmail",email.getEditText().getText().toString());
                            userInfo.put("UserPassword",password.getEditText().getText().toString());
                            userInfo.put("PhoneNumber",phone.getEditText().getText().toString());
                            userInfo.put("VehicleNumber",vehicleNo.getEditText().getText().toString());

                            //specify if the user is admin or user
                            if(isAdminbtn.isChecked()){
                                userInfo.put("isAdmin","1");
                            }
                            if(isUserbtn.isChecked()){
                                userInfo.put("isUser","1");
                            }

                            df.set(userInfo);

                            if(isAdminbtn.isChecked()){
                                startActivity(new Intent(getApplicationContext(),adminDashboard.class));
                                finish();
                            }
                            if(isUserbtn.isChecked()){
                                startActivity(new Intent(getApplicationContext(),Dashboard.class));
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
            }
        });
    }

    public boolean checkField(TextInputLayout textField){
        if(textField.getEditText().getText().toString().isEmpty()){
            textField.setError("Error");
            valid = false;
        }else {
            valid = true;
        }

        return valid;
    }
}