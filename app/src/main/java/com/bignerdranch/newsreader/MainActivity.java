package com.bignerdranch.newsreader;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_headlines, R.id.navigation_customsearch, R.id.navigation_savedarticles)
                .build();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        StickyFragmentNavigator navigator = new StickyFragmentNavigator(this,navHostFragment.getChildFragmentManager(),R.id.nav_host_fragment);
        navController.getNavigatorProvider().addNavigator(navigator);
        navController.setGraph(R.navigation.mobile_navigation);


        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (menuItem.isChecked()) return false;

                switch (id)
                {
                    case R.id.navigation_headlines :
                        navController.navigate(R.id.action_global_navigation_headlines);
                        break;
                    case R.id.navigation_customsearch :
                        navController.navigate(R.id.action_global_navigation_customsearch);
                        break;
                    case R.id.navigation_savedarticles :
                        navController.navigate(R.id.action_global_navigation_savedarticles);
                        break;
                }
                return true;

            }
        });

        NewsRepository.getInstance(getApplicationContext());
    }

}
