package com.example.halil_bali_final_project;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Random;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    //    load elements
    void Load() {
//        toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        drawer layout
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

//        navigation view
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
    }

    // for selecting items on the toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        //random number generator
        Random random = new Random();
        //list of messages
        int[] stringID = {R.string.earth1, R.string.earth2, R.string.earth3, R.string.earth4, R.string.earth5,
                R.string.earth6, R.string.earth7, R.string.earth8};
        //generates random number up to 8
        int randomNum = random.nextInt(8);

        String item1 = getString(R.string.item1);
        TextView earth = findViewById(R.id.earthFact);
        //sets the text as the random string pulled from above generator
        earth.setText(getResources().getString(stringID[randomNum]));

        switch (item.getItemId()) {
            case R.id.item1:
                //makes a toast message when you click on the spaceship
                Toast.makeText(getApplicationContext(), item1, Toast.LENGTH_SHORT).show();
                //click takes you to the DatePicker activity

                Intent intent = new Intent(this, DatePicker.class);
                startActivity(intent);
                earth.setText(null);
                break;
            case R.id.item2:
                //makes a snackbar asking if you knew the fact
                Snackbar.make(earth, "Did you know?", Snackbar.LENGTH_LONG)
                        //sets up a button on the snackbar
                        .setAction("Go away!", click -> {
                            //clicking on the button clears the earth fact
                            earth.setText(null);
                            //creates a sad faced toast
                            Toast.makeText(getApplicationContext(), ":(", Toast.LENGTH_SHORT).show();
                            //sets the duration of the snackbar to 10 seconds
                        }).setDuration(10000)
                        .show();
                break;
            case R.id.item3:
                //takes you to help popup
                popUp();
                earth.setText(null);
                break;
                //takes you to PlanetActivity
            case R.id.item4:
                earth.setText(null);
                Intent planetIntent = new Intent(this, PlanetActivity.class);
                startActivity(planetIntent);
                break;
        }
        return false;
    }

    //creates help popup window
    public void popUp(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(R.string.helpTitle)
                .setMessage(R.string.helpText)
                .setPositiveButton(R.string.doneHelp, (click,arg) ->{

                });
        dialogBuilder.create().show();

    }

    //    on select for navigation drawer
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            // to home
            case R.id.actionHome:
                Intent intentHome = new Intent(this, MainActivity.class);
                startActivity(intentHome);
                break;
            // to date picker
            case R.id.actionDatePicker:
                Intent intentDatePicker = new Intent(this, DatePicker.class);
                startActivity(intentDatePicker);
                break;
            // exit app
            case R.id.actionExit:
                finishAffinity();
                break;
            // opens help window
            case  R.id.actionHelp:
                popUp();
                break;
            //opens up planets page
            case R.id.actionPlanetPage:
                Intent planetIntent = new Intent(this, PlanetActivity.class);
                startActivity(planetIntent);
                break;
        }
        return false;
    }

    //    displays toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return true;
    }

}
