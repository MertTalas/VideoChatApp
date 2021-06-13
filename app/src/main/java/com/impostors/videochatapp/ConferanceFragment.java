package com.impostors.videochatapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

public class ConferanceFragment extends Fragment {
    private Button btnLogout,btnContacts,btnJoin;
    private FirebaseAuth mAuth;
    private EditText participationCode;
    private Activity context;
    private Animation scaleUp,scaleDown;
    private Switch themeSwitch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main_page, container, false);
        context = getActivity();

        btnLogout=view.findViewById(R.id.btnLogout);

        scaleUp = AnimationUtils.loadAnimation(this.getActivity(),R.anim.scale_up);
        scaleDown = AnimationUtils.loadAnimation(this.getActivity(),R.anim.scale_down);


        mAuth=FirebaseAuth.getInstance();
        btnJoin=view.findViewById(R.id.btnJoin);
        //themeSwitch= getView().findViewById(R.id.themeSwitcher_Main);
        themeSwitch = view.findViewById(R.id.themeSwitcher_Main);

        /*SharedPreferences theme = getActivity().getSharedPreferences("night",0);

        boolean booleanValue = theme.getBoolean("night_mode",true);
        System.out.println(booleanValue);
        if(booleanValue){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            themeSwitch.setChecked(true);
        }*/

        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    /*hemeSwitch.setChecked(true);
                    SharedPreferences.Editor themeEditor = theme.edit();
                    themeEditor.putBoolean("night_mode",true);
                    themeEditor.commit();*/
                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    /*themeSwitch.setChecked(false);
                    SharedPreferences.Editor themeEditor = theme.edit();
                    themeEditor.putBoolean("night_mode",false);
                    themeEditor.commit();*/
                }
            }
        });




        participationCode=view.findViewById(R.id.editTextTextParticipationCode);
        URL serverURL;
        try{
            serverURL=new URL("https://meet.jit.si");
            JitsiMeetConferenceOptions defaultOptions=
                    new JitsiMeetConferenceOptions.Builder()
                            .setServerURL(serverURL)
                            .setWelcomePageEnabled(false)
                            .build();
            JitsiMeet.setDefaultConferenceOptions(defaultOptions);
        }
        catch (MalformedURLException e ){
            e.printStackTrace();
        }

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(participationCode.getText().toString())) {
                    btnJoin.startAnimation(scaleUp);
                    btnJoin.startAnimation(scaleDown);
                    JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                            .setRoom(participationCode.getText().toString())
                            .setWelcomePageEnabled(false)
                            .build();
                    JitsiMeetActivity.launch(context, options);
                }else{
                    Toast.makeText(context, context.getString(R.string.enterParticipationCode), Toast.LENGTH_SHORT).show();
                    btnJoin.startAnimation(scaleUp);
                    btnJoin.startAnimation(scaleDown);
                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnJoin.startAnimation(scaleUp);
                btnJoin.startAnimation(scaleDown);
                mAuth.signOut();
                startActivity(new Intent(context,LoginActivity.class));
                getActivity().finish();
                Toast.makeText(context, context.getString(R.string.logOutSuccessful ), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }


}
