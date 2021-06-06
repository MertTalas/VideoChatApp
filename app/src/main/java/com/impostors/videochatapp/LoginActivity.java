package com.impostors.videochatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private FirebaseAuth firebaseAuth;
    private Context mcontext;
    private Button btnVerify,btnContinue;
    private TextView txtPhone,txtCode,txtResend;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth=FirebaseAuth.getInstance();


        init();
        mCallbacks= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);


            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                pd.dismiss();
                Toast.makeText(mcontext,e.getMessage(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(mcontext,"Code Sent",Toast.LENGTH_LONG).show();
                mVerificationId=s;
                forceResendingToken=token;
                pd.dismiss();
            }
        };
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone=txtPhone.getText().toString().trim();
                if(TextUtils.isEmpty(phone)) {
                    Toast.makeText(mcontext, "Enter phone number", Toast.LENGTH_LONG);
                }
                else{
                    startPhoneNumberVerification(phone);
                }


            }
        });
        txtResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone=txtPhone.getText().toString().trim();
                if(TextUtils.isEmpty(phone)) {
                    Toast.makeText(mcontext, "Enter phone number", Toast.LENGTH_LONG);
                }
                else{
                    resendVerificationCode(phone,forceResendingToken);
                }
            }
        });
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code=txtCode.getText().toString().trim();
                if(TextUtils.isEmpty(code)) {
                    Toast.makeText(mcontext, "Enter code", Toast.LENGTH_LONG);
                }
                else{
                    verifyPhoneNumberWithCode(mVerificationId,code);
                }

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!(firebaseAuth.getCurrentUser()==null)){
            startActivity(new Intent(LoginActivity.this,MainEkstra.class));
        }
    }

    private void verifyPhoneNumberWithCode(String mVerificationId, String code) {
        pd.setMessage("Verifying code...");
        pd.show();
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(mVerificationId,code);
        signInWithPhoneAuthCredential(credential);
    }



    private void resendVerificationCode(String phone,PhoneAuthProvider.ForceResendingToken token) {
        pd.setMessage("Resending code...");
        pd.show();
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)
                        .setForceResendingToken(token)// OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    private void startPhoneNumberVerification(String phone) {
        pd.setMessage("Verifying phone number...");
        pd.show();
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        pd.setMessage("Logging In");
        firebaseAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                pd.dismiss();
                String phone=firebaseAuth.getCurrentUser().getPhoneNumber();
                Toast.makeText(mcontext,"Logged in as "+phone,Toast.LENGTH_LONG).show();
                myRef.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("phone_number").setValue(txtPhone.getText().toString());
                startActivity(new Intent(LoginActivity.this,MainEkstra.class));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(mcontext,e.getMessage(),Toast.LENGTH_LONG).show();

            }
        });
    }
    private void init(){
        SharedPreferences sp=getSharedPreferences("phoneNumber",MODE_PRIVATE);
        SharedPreferences.Editor e =sp.edit();
        mcontext=getApplicationContext();
        firebaseAuth=FirebaseAuth.getInstance();
        txtCode=findViewById(R.id.editTextVerificationCode);
        txtPhone=findViewById(R.id.editTextEnterPhoneNumber);
        txtResend=findViewById(R.id.textViewClickableResendCode);
        btnVerify=findViewById(R.id.buttonSentCode);
        btnContinue=findViewById(R.id.buttonLogin);
        pd= new ProgressDialog(this);
        pd.setTitle("Please Wait");
        pd.setCanceledOnTouchOutside(false);

    }
}