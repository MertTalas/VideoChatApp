package com.impostors.videochatapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ContactAdapter extends FirebaseRecyclerAdapter<Contact, ContactAdapter.myViewHolder> implements ActivityCompat.OnRequestPermissionsResultCallback {
    private Context context;
    Activity activity;
    ConstraintLayout constraintLayout;
    private static final int REQUEST_CALL = 1;


    public ContactAdapter(@NonNull FirebaseRecyclerOptions<Contact> options, Context mContext, Activity activity) {
        super(options);
        this.activity = activity;
        this.context = mContext;

    }


    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull Contact model) {
        holder.contactName.setText(model.getName());
        holder.contactPhone.setText(model.getPhoneNumber());


    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_contact, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        View a = new View(context);
        myViewHolder myViewHolder = new myViewHolder(a);
        Log.d("MESAJ", "GELİYO");

        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) ;
            String number = myViewHolder.contactPhone.getText().toString();
            if (number.trim().length() > 0) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                    ;
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                String dial = "tel:" + number;
                context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }


        }
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        TextView contactName, contactPhone;
        private static final int REQUEST_CALL = 1;
        ImageButton btnEditContact, btnDeleteContact;
        ;
        ImageView btnCall;

        FirebaseAuth auth;
        FirebaseUser firebaseUser;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            auth = FirebaseAuth.getInstance();
            firebaseUser = auth.getCurrentUser();
            contactName = itemView.findViewById(R.id.contactName);
            contactPhone = itemView.findViewById(R.id.contactPhone);
            btnDeleteContact = itemView.findViewById(R.id.buttonDelete);
            btnCall = itemView.findViewById(R.id.buttonCall);
            btnEditContact = itemView.findViewById(R.id.buttonEdit);

            btnCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("onclick", "tıkladı");
                    call();
                }

            });
            btnEditContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(v.getRootView().getContext());
                    View editTextAlert = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.update_alert, null);
                    final EditText updateName = editTextAlert.findViewById(R.id.editTextUpdateName);
                    final EditText updatePhone = editTextAlert.findViewById(R.id.editTextUpdatePhone);


                    ad.setMessage("Düzenle");
                    ad.setTitle("Kişi düzenle");
                    ad.setView(editTextAlert);

                    ad.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase firebaseDatabase;
                            DatabaseReference databaseReference;
                            firebaseDatabase = FirebaseDatabase.getInstance();
                            databaseReference = firebaseDatabase.getReference();

                            final String updatedPhone = updatePhone.getText().toString();
                            final String updatedName = updateName.getText().toString();
                            Query query = databaseReference.child("users")
                                    .child(firebaseUser.getUid()).child("contacts").orderByChild("name").equalTo(getItem(getAdapterPosition()).getName());
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot appleSnapshot : snapshot.getChildren()) {
                                        Map<String, Object> updateInfo = new HashMap<>();
                                        if (!TextUtils.isEmpty(updatedPhone)) {
                                            updateInfo.put("phoneNumber", updatedPhone);

                                        } if (!TextUtils.isEmpty(updatedName)) {
                                            updateInfo.put("name", updatedName);

                                        }
                                        appleSnapshot.getRef().updateChildren(updateInfo);
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    });
                    ad.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }

                    });
                    ad.create().show();

                }

            });
            btnDeleteContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getRef(getAdapterPosition()).removeValue();
                }

            });

        }

        private void call() {
            String number = contactPhone.getText().toString();
            if (number.trim().length() > 0) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
                } else {
                    String dial = "tel:" + number;
                    context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                }
            }


        }


    }
}
