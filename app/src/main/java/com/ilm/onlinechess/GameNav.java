package com.ilm.onlinechess;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.ilm.onlinechess.databinding.ActivityGameBinding;
import com.ilm.onlinechess.databinding.ActivityGameNavBinding;

public class GameNav extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityGameNavBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGameNavBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarLoginNav.toolbar);


        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Configura AppBarConfiguration con los IDs de los destinos de nivel superior
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                  R.id.nav_profile, R.id.nav_lobby)
                .setOpenableLayout(drawer)
                .build();

        // Obtén NavHostFragment y NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_login_nav);
        NavController navController = navHostFragment.getNavController();

        // Configura ActionBar con NavController y AppBarConfiguration

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Infla el menú; esto añade elementos a la barra de acción si está presente.
        getMenuInflater().inflate(R.menu.login_nav, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_login_nav);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}