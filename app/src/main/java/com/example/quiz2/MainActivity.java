package com.example.quiz2;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView botton_navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        botton_navigation = findViewById(R.id.bottom_navigation);
        loadFragment(new ajustesFragment());

        botton_navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener()  {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment selectedFragment = null;
                int itemId = item.getItemId();
                if (itemId == R.id.fragmento1) {
                    selectedFragment = new ajustesFragment();
                } else if (itemId == R.id.fragmento2) {
                    selectedFragment = new NoticiasFragment();
                } else if (itemId == R.id.fragmento3) {
                    selectedFragment = new PerfilFragment();
                }

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                    return true;
                }

                return false;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frm_container, fragment);
        transaction.commit();
    }
} 