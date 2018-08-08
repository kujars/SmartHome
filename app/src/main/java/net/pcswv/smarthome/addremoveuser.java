package net.pcswv.smarthome;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class addremoveuser extends AppCompatActivity {
    DatabaseReference dref;
    DatabaseReference cref;
    DatabaseReference uref;
    FirebaseAuth auth;
    ListView listView;
    EditText nusr;
    Button addbtn;
    Button rem;
    String home;
    String hmkey;
    String key;
    public String k;
    ArrayList<HashMap> list=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addremoveuser);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = prefs.edit();
        auth=FirebaseAuth.getInstance();
        nusr = (EditText) findViewById(R.id.newusr);
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        addbtn = (Button) findViewById(R.id.addbtn);
        final String mUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        listView = (ListView)findViewById(R.id.listview);
        final ArrayAdapter<HashMap> adapter=new ArrayAdapter<HashMap>(this,android.R.layout.simple_dropdown_item_1line,list);
        listView.setAdapter(adapter);
        dref=FirebaseDatabase.getInstance().getReference();
        uref=dref.child("users").child(mUid);
        DatabaseReference href = uref.child("Homekey");
        href.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String k = Objects.requireNonNull(Objects.requireNonNull(dataSnapshot.getValue()).toString());
                editor.putString("Key", k);
                editor.apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        String key = prefs.getString("Key", "");
        cref=dref.child("homes").child(key);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nusrs = Objects.requireNonNull(nusr.getText().toString());
                cref.child("Email").setValue(nusrs);
            }
        });
        cref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                list.add(dataSnapshot.getValue(HashMap.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                list.remove(dataSnapshot.getValue(HashMap.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            final String hmkey = extras.getString("key");
        }
    }
}
