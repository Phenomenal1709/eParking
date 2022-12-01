package com.example.paymenttesting;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    TextInputLayout email,password;
    Button loginBtn,gotoRegister;

    boolean valid = true;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);

        loginBtn = findViewById(R.id.loginBtn);
        gotoRegister = findViewById(R.id.gotoRegister);

        gotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Register.class));
            }
        });

       loginBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               checkField(email);
               checkField(password);

               if(valid){

                   fAuth.signInWithEmailAndPassword(email.getEditText().getText().toString(),password.getEditText().getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                       @Override
                       public void onSuccess(AuthResult authResult) {

                           Toast.makeText(Login.this, "Login Successful:)", Toast.LENGTH_SHORT).show();
                           checkUserAccessLevel(authResult.getUser().getUid());
                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                       }
                   });

               }

           }
       });

    }

    private void checkUserAccessLevel(String uid) {
        DocumentReference df = fStore.collection("Users").document(uid);
        //extract data from document
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                //documentSnapshot contains the data of the user stored in fireStore
                Log.d("TAG","onSuccess: "+documentSnapshot.getData());

                //identify the user access level

                if(documentSnapshot.getString("isAdmin")!=null){
                    //it means user is admin
                    startActivity(new Intent(getApplicationContext(),adminDashboard.class));
                    finish();
                }

                if(documentSnapshot.getString("isUser")!=null){
                    //it means user is 'user'
                    startActivity(new Intent(getApplicationContext(),Dashboard.class));
                    finish();
                }

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

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            startActivity(new Intent(getApplicationContext(), Dashboard.class));
            finish();
        }
    }
}