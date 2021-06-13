package com.impostors.videochatapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ContactsFragment extends Fragment {
    private Button addContactbtn;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private Activity context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_contacts, container, false);
        context = getActivity();

        addContactbtn=view.findViewById(R.id.btnAddContact);
        auth = FirebaseAuth.getInstance();
        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        firebaseUser = auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");
        addContactbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { View editTextAlert = getLayoutInflater().inflate(R.layout.alert_addcontact, null);
                AlertDialog.Builder ad = new AlertDialog.Builder(context);
                //ad.setMessage("Kişi Bilgileri");
                ad.setMessage(getActivity().getApplicationContext().getString(R.string.contactInformation));

                //ad.setTitle("Yeni Kişi ekle");
                ad.setMessage(getActivity().getApplicationContext().getString(R.string.addNewContact));

                final EditText InsertedContactName = editTextAlert.findViewById(R.id.editTextContactName);
                final EditText InsertedContactPhoneNumber= editTextAlert.findViewById(R.id.editTextContactPhoneNumber);
                ad.setView(editTextAlert);
                ad.setPositiveButton(getActivity().getApplicationContext().getString(R.string.add), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String ContactName = InsertedContactName.getText().toString();
                        final String ContactPhone = InsertedContactPhoneNumber.getText().toString();

                        if (TextUtils.isEmpty(ContactPhone) || TextUtils.isEmpty(ContactName)
                        ) {
                            //Toast.makeText(context, "Gerekli Alanları Doldurunuz", Toast.LENGTH_LONG).show();
                            Toast.makeText(context, getActivity().getApplicationContext().getString(R.string.fillTheNecesseryBlankets), Toast.LENGTH_SHORT).show();
                        } else {
                            final Query query = databaseReference.orderByKey().equalTo(firebaseUser.getUid());
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    final Contact contact = new Contact(ContactPhone, ContactName);
                                    contact.setContact_id(firebaseUser.getUid());
                                    query.getRef().child(firebaseUser.getUid()).child("contacts").push().setValue(contact);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }).setNegativeButton(getActivity().getApplicationContext().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                ad.create().show();

            }
        });
        FirebaseRecyclerOptions<Contact> options =
                new FirebaseRecyclerOptions.Builder<Contact>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid()).child("contacts"), Contact.class)
                        .build();
        adapter= new ContactAdapter(options,context,context);
        recyclerView.setAdapter(adapter);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}

