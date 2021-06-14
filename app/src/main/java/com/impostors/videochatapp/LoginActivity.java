package com.impostors.videochatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
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

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private FirebaseAuth firebaseAuth;
    private Context mcontext;
    private Button btnVerify,btnContinue;
    private TextView txtPhone,txtCode,txtResend;
    private Animation scaleUp,scaleDown;


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private ProgressDialog pd;

    private Switch themeSwitch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth=FirebaseAuth.getInstance();

        if(!(firebaseAuth.getCurrentUser()==null)){
            startActivity(new Intent(LoginActivity.this,MainEkstra.class));
            finish();
        }

        mcontext=getApplicationContext();
        txtCode=findViewById(R.id.editTextVerificationCode);
        txtPhone=findViewById(R.id.editTextEnterPhoneNumber);
        txtResend=findViewById(R.id.textViewClickableResendCode);
        btnVerify=findViewById(R.id.buttonSentCode);
        btnContinue=findViewById(R.id.buttonLogin);
        pd= new ProgressDialog(this);
        pd.setTitle(getApplicationContext().getString(R.string.pleaseWait));
        pd.setCanceledOnTouchOutside(false);


        scaleUp = AnimationUtils.loadAnimation(this,R.anim.scale_up);
        scaleDown = AnimationUtils.loadAnimation(this,R.anim.scale_down);







        themeSwitch= findViewById(R.id.themeSwitcher_login);

        /*SharedPreferences theme = getSharedPreferences("night",0);
        boolean booleanValue = theme.getBoolean("night_mode",false);
        System.out.println(booleanValue);*/

        /*if(booleanValue){
            //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            themeSwitch.setChecked(true);
        }*/

        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //themeSwitch.setChecked(false);

        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    themeSwitch.setChecked(true);
                    /*SharedPreferences.Editor themeEditor = theme.edit();
                    themeEditor.putBoolean("night_mode",true);
                    themeEditor.commit();*/
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    return;
                }else{
                    themeSwitch.setChecked(false);
                  /*  SharedPreferences.Editor themeEditor = theme.edit();
                    themeEditor.putBoolean("night_mode",false);
                    themeEditor.commit();*/
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    return;
                }
            }
        });




        //init();
        mCallbacks= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);


            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                pd.dismiss();
                Toast.makeText(mcontext,e.getMessage(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(s, forceResendingToken);
                String codeSent = getApplicationContext().getString(R.string.codeSent);
                //Toast.makeText(mcontext,"@String/codeSent",Toast.LENGTH_LONG).show();
                Toast.makeText(mcontext,codeSent,Toast.LENGTH_SHORT).show();
                mVerificationId=s;
                forceResendingToken=token;
                pd.dismiss();
            }
        };
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone=txtPhone.getText().toString().trim();
                System.out.println("Gönder  tuşuna basıldı");
                if(TextUtils.isEmpty(phone)) {
                    //Toast.makeText(mcontext, "@string/enterPhoneForContinue", Toast.LENGTH_LONG);
                    Toast.makeText(mcontext, getApplicationContext().getString(R.string.enterPhoneForContinue), Toast.LENGTH_SHORT).show();
                    btnVerify.startAnimation(scaleUp);
                    btnVerify.startAnimation(scaleDown);
                }
                else{
                    btnVerify.startAnimation(scaleUp);
                    btnVerify.startAnimation(scaleDown);
                    startPhoneNumberVerification(phone);
                }


            }
        });
        txtResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone=txtPhone.getText().toString().trim();
                if(TextUtils.isEmpty(phone)) {
                    //Toast.makeText(mcontext, "Enter phone number", Toast.LENGTH_LONG);
                    Toast.makeText(mcontext, getApplicationContext().getString(R.string.enterPhoneForContinue), Toast.LENGTH_SHORT).show();
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
                /*btnContinue.setOnTouchListener(new View.OnTouchListener() { @Override public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction()==MotionEvent.ACTION_DOWN){
                         }
                    else if(event.getAction()==MotionEvent.ACTION_UP){
                        ;}
                    return true;
                }
                });*/
                btnContinue.startAnimation(scaleUp);
                btnContinue.startAnimation(scaleDown);
                System.out.println("Devam et tuşuna basıldı");
                if(TextUtils.isEmpty(code)) {
                    //Toast.makeText(mcontext, "Enter code", Toast.LENGTH_LONG);
                    Toast.makeText(mcontext, getApplicationContext().getString(R.string.enterTheCode), Toast.LENGTH_SHORT).show();
                    btnContinue.startAnimation(scaleUp);
                    btnContinue.startAnimation(scaleDown);
                }
                else{
                    verifyPhoneNumberWithCode(mVerificationId,code);
                    btnContinue.startAnimation(scaleUp);
                    btnContinue.startAnimation(scaleDown);
                }

            }
        });


    }


    private void verifyPhoneNumberWithCode(String mVerificationId, String code) {
        //pd.setMessage("Verifying code...");
        pd.setMessage(getApplicationContext().getString(R.string.codeVerify));
        pd.show();
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(mVerificationId,code);
        signInWithPhoneAuthCredential(credential);
    }



    private void resendVerificationCode(String phone,PhoneAuthProvider.ForceResendingToken token) {
        //pd.setMessage("Resending code...");
        pd.setMessage(getApplicationContext().getString(R.string.resendingCode));
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
        //pd.setMessage("Verifying phone number...");
        pd.setMessage(getApplicationContext().getString(R.string.phoneVerify));
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
        pd.setMessage(getString(R.string.LoggingIn));
        firebaseAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                pd.dismiss();
                String phone=firebaseAuth.getCurrentUser().getPhoneNumber();
                Toast.makeText(mcontext,getApplicationContext().getString(R.string.loggedInAs) + " " + phone,Toast.LENGTH_LONG).show();
                myRef.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("phone_number").setValue(txtPhone.getText().toString());
                startActivity(new Intent(LoginActivity.this,MainEkstra.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(mcontext,e.getMessage(),Toast.LENGTH_LONG).show();

            }
        });
    }
    /*private void init(){
        SharedPreferences sp=getSharedPreferences("phoneNumber",MODE_PRIVATE);
        SharedPreferences.Editor e =sp.edit();
        mcontext=getApplicationContext();
        txtCode=findViewById(R.id.editTextVerificationCode);
        txtPhone=findViewById(R.id.editTextEnterPhoneNumber);
        txtResend=findViewById(R.id.textViewClickableResendCode);
        btnVerify=findViewById(R.id.buttonSentCode);
        btnContinue=findViewById(R.id.buttonLogin);
        pd= new ProgressDialog(this);
        pd.setTitle(getApplicationContext().getString(R.string.pleaseWait));
        pd.setCanceledOnTouchOutside(false);
    }*/

    /*@Override
    protected void onResume() {
        super.onResume();

        /*SharedPreferences theme = getSharedPreferences("night",0);
        boolean booleanValue = theme.getBoolean("night_mode",true);

        if(booleanValue){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            themeSwitch.setChecked(true);
        }
    }*/

    /*@Override
    protected void onDestroy() {
        super.onDestroy();

        /*SharedPreferences theme = getSharedPreferences("night",0);
        boolean booleanValue = theme.getBoolean("night_mode",true);

        if(booleanValue){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            themeSwitch.setChecked(true);
        }
    }*/
}