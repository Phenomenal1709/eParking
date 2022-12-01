package com.example.paymenttesting;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements PaymentResultListener {

    Button btnPay;
    Button btnCall;
    TextView detailsText;
    LinearLayout layout;
    //EditText amountEdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPay = findViewById(R.id.btn_pay);
        btnCall = findViewById(R.id.btn_call);
        //amountEdt = findViewById(R.id.idEdtAmount);

        detailsText = findViewById(R.id.details);
        layout = findViewById(R.id.layout);
        layout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:6391494863"));
                startActivity(intent);
            }
        });

        String sAmount = "100";
        int amount = Math.round(Float.parseFloat(sAmount)*100);

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Initializing razorpay checkout
                Checkout checkout = new Checkout();

                checkout.setKeyID("rzp_test_JEFYir3ApkZjZu");
                checkout.setImage(R.drawable.rzpicon);

                //Initializing json object
                JSONObject object = new JSONObject();

                try {
                    object.put("name","e-Parking");
                    object.put("description","Test Payment");
                    object.put("theme.color","#528FF0");
                    object.put("currency","INR");
                    object.put("amount",amount);
                    object.put("prefill.contact","");
                    object.put( "prefill.email","");

                    checkout.open(MainActivity.this,object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    @Override
    public void onPaymentSuccess(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Payment ID");
        builder.setMessage(s);
        builder.show();
    }

    @Override
    public void onPaymentError(int i, String s) {

        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();

    }


    public void expand(View view) {

        int v = (detailsText.getVisibility() == View.GONE)?View.VISIBLE:View.GONE;

        TransitionManager.beginDelayedTransition(layout,new AutoTransition());
        detailsText.setVisibility(v);
    }
}