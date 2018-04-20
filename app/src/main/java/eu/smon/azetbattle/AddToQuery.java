package eu.smon.azetbattle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import eu.smon.azetbattle.Classes.ZapisanieDoRady;

public class AddToQuery extends AppCompatActivity {

    private Button signButton;
    private EditText ID;

    private DatabaseReference dbref;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_query);

        signButton = (Button) findViewById(R.id.SignButton);
        ID = (EditText) findViewById(R.id.casenkaID);

        dbref = FirebaseDatabase.getInstance().getReference();

        user = FirebaseAuth.getInstance().getCurrentUser();

        signButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ID.length() > 0) {
                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("casenky").child(ID.getText().toString()).child("docID").getValue() != null) {
                                ZapisanieDoRady rada = new ZapisanieDoRady(dataSnapshot.child("casenky").child(ID.getText().toString()).child("docID").getValue().toString(), dataSnapshot.child("casenky").child(ID.getText().toString()).child("poradie").getValue().toString());
                                dbref.child("users").child(user.getUid()).setValue(rada);

                                dbref.child("rad").child(dataSnapshot.child("casenky").child(ID.getText().toString()).child("poradie").getValue().toString()).child("userid").setValue(user.getUid());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }
}
