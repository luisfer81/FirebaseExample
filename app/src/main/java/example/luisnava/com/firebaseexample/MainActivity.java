package example.luisnava.com.firebaseexample;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //display menu1 when the activity is loaded
        displaySelectedScreen(R.id.nav_stores);


        View hView =  navigationView.getHeaderView(0);
        TextView email_header = (TextView) hView.findViewById(R.id.textViewEmail);
        final TextView userName_header = (TextView) hView.findViewById(R.id.textViewUsername);

        email_header.setText(auth.getCurrentUser().getEmail());
        mDatabase.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    String email = singleSnapshot.child("email").getValue().toString();
                    Log.d("User FDB", email);
                    if (email.equals(auth.getCurrentUser().getEmail())) {
                        userName_header.setText(singleSnapshot.child("username").getValue().toString());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error", "onCancelled", databaseError.toException());
            }
        });

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //calling the method displayselectedscreen and passing the id of selected menu
        displaySelectedScreen(id);
        return true;
    }

    public void displaySelectedScreen(int itemId) {

        //creating fragment object
        Fragment fragment = null;

        if (itemId == R.id.nav_stores) {
            fragment = new example.luisnava.com.firebaseexample.ListFragment();
        } else if (itemId == R.id.nav_addStores) {
            fragment = new example.luisnava.com.firebaseexample.AddFragment();
        }  else if (itemId == R.id.nav_maps) {
            fragment = new example.luisnava.com.firebaseexample.MapFragment();
        } else if (itemId == R.id.nav_sign_out) {
            auth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }


        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }

}
