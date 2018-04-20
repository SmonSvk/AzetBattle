package eu.smon.azetbattle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;
import java.util.List;

import eu.smon.azetbattle.Classes.Pouzivatel;

public class MainAppActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int RC_SIGN_IN = 123;

    private GoogleSignInOptions gso;
    private Button addtoorder;
    private DatabaseReference dbref;
    private TextView docText, orderText, waitTime;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Init();
    }

    protected void Init(){
        addtoorder = (Button) findViewById(R.id.addToOrderBtn);
        docText = (TextView) findViewById(R.id.docNameTxt);
        orderText = (TextView) findViewById(R.id.orderTxt);
        waitTime = (TextView) findViewById(R.id.waitingTimeTxt);

        addtoorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(), AddToQuery.class);
                startActivity(i);
            }
        });

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
        );

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);

        user = FirebaseAuth.getInstance().getCurrentUser();

        dbref = FirebaseDatabase.getInstance().getReference();

        dbref.addValueEventListener(new ValueEventListener() {
                @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("users").child(user.getUid()).child("DoktorID").getValue() != null) {
                    String docID = dataSnapshot.child("users").child(user.getUid()).child("DoktorID").getValue().toString();
                    docText.setText(dataSnapshot.child("doktory").child(docID).child("name").getValue().toString());
                    int poradie = Integer.parseInt(dataSnapshot.child("users").child(user.getUid()).child("poradie").getValue().toString());
                    int narade;
                    if(dataSnapshot.child("rad").child(docID).child("aktualne").getValue() == null)
                        narade = 0;
                    else
                        narade = Integer.parseInt(dataSnapshot.child("rad").child(docID).child("aktualne").getValue().toString());
                    if(poradie - narade > 0)
                        orderText.setText(String.valueOf(poradie - narade));
                    else
                        orderText.setText("Ste na rade");

                    int wait = Integer.parseInt(dataSnapshot.child("doktory").child(docID).child("cakanie").getValue().toString());

                    waitTime.setText(String.valueOf(((poradie - narade) * wait)));
                    orderText.setVisibility(View.VISIBLE);
                    waitTime.setVisibility(View.VISIBLE);
                }
                else{
                    docText.setText("Nie ste prihlásený do žiadnej rady");
                    orderText.setVisibility(View.GONE);
                    waitTime.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in

                dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.child("users").child(user.getUid()).exists()) {
                            Pouzivatel poz = new Pouzivatel(user.getDisplayName(), user.getEmail(), FirebaseInstanceId.getInstance().getToken());
                            dbref.child("users").child(user.getUid()).setValue(poz);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                Toast.makeText(MainAppActivity.this, "Nepodarilo sa prihlásiť", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("token", "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        dbref.child("users").child(user.getUid()).child("token").setValue(refreshedToken);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_main) {
            // Handle the camera action
        }
        else if (id == R.id.nav_edit_user) {
            startActivity(new Intent(getApplicationContext(), EditUserActivity.class));
        }
        else if (id == R.id.nav_logout) {
            // log out
        }
        else if (id == R.id.nav_about) {
            startActivity(new Intent(getApplicationContext(), AboutAppActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}