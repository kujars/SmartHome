package net.pcswv.smarthome;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    public TextView TV1;
    public TextView TV2;
    public Button openses;
    public String key;
    FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        TV1 = (TextView) findViewById(R.id.Status);
        TV2 = (TextView) findViewById(R.id.textView4);
        openses = (Button) findViewById(R.id.open);
        final TextView TV2 = new TextView(this);
        TV1.findViewById(R.id.Status);
        TV2.findViewById(R.id.textView4);
        final Button openses = new Button(this);
        openses.findViewById(R.id.open);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null) {
                    chk_usr();
                }
                else {
                    Login();
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
        String hmky = prefs.getString("Homekey", "");
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference nmRef = rootRef.child("homes").child(hmky);
        String hmname = nmRef.child("Name").getKey();
        editor.putString("HouseName", hmname);

    }
    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mAuth.addAuthStateListener(mAuthListener);
        String hsname = prefs.getString("HouseName", "None");
        TV2.setText(hsname);
    }
    public void Login() {
        startActivity(new Intent(this, LoginActivity.class));
    }
    public void chk_usr() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        String mUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usrRef = rootRef.child("users").child(mUid);
        DatabaseReference hskyRef = usrRef.child("Homekey");
        String ky = Objects.requireNonNull(hskyRef.getKey());
        editor.putString("Homekey", ky);
        editor.putString("mUid", mUid);
        editor.apply();
        final DatabaseReference hmRef = rootRef.child("homes").child(ky);
        usrRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    setup_usr();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void setup_usr() {
        String mUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usrRef = rootRef.child("users").child(mUid);
        DatabaseReference houseRef = rootRef.child("homes");
        showDialog(0);

    }
    public void setup_cont(String hsnm) {
        String mUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usrRef = rootRef.child("users").child(mUid);
        DatabaseReference houseRef = rootRef.child("homes");
        final String key = Objects.requireNonNull(houseRef.push().getKey());
        final String name = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
        final String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        Map<String, String> userData = new HashMap<String, String>();

        userData.put("Name", name);
        userData.put("Email", email);
        userData.put("uid", mUid);
        userData.put("Homekey", key);
        Map<String, String> homeData = new HashMap<String, String>();

        homeData.put("Name", hsnm);
        homeData.put("Owner", mUid);
        usrRef.setValue(userData);
        houseRef.child(key).setValue(homeData);
    }
    protected Dialog onCreateDialog(int id) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        switch (id) {
            case 0:
                final EditText input = new EditText(this);
                return new AlertDialog.Builder(this).setMessage("Name your home").setView(input).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nm = input.getText().toString();
                        prefs.edit().putString("HomeName", nm).apply();
                        TV2.setText(nm);
                        setup_cont(nm);
                        dialog.dismiss();
                    }
                }).create();
        }
        return null;
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        String mUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference keyRef = rootRef.child("users").child(mUid).child("Homekey");
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

                case R.id.users:
                    Intent intent = new Intent(getApplicationContext(), addremoveuser.class);
                    String ky = Objects.requireNonNull(keyRef.getKey());
                    intent.putExtra("key", ky);
                    startActivity(intent);
                    return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
