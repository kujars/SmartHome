package net.pcswv.smarthome;

import net.pcswv.smarthome.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseUtil {
    public FirebaseDatabase database;
    private DatabaseReference myRef;


    public void onCreate() {
        database = FirebaseDatabase.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
    }
    public void writenewuser(String userId, String email, String name) {
        User user = new User(name, email);
        myRef.child("users").child(userId).setValue(user);
    }
}
