package com.example.whatsappclone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.viewpager.widget.ViewPager;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapters.ViewPagerAdapter;
import com.example.whatsappclone.fragments.ChatFragment;
import com.example.whatsappclone.fragments.ContacsFragment;
import com.example.whatsappclone.fragments.StatusFragment;
import com.example.whatsappclone.providers.AuthProvider;
import com.google.android.material.tabs.TabLayout;
import com.mancj.materialsearchbar.MaterialSearchBar;

public class HomeActivity extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener{

    AuthProvider mAuthProvider;
    MaterialSearchBar mSearchBar;

    TabLayout mTabLayout;
    ViewPager mViewPager;
    ChatFragment mChatFragment;
    ContacsFragment mContactsFragment;
    StatusFragment mStatusFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mSearchBar = findViewById(R.id.searchBar);
        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(3);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        mChatFragment = new ChatFragment();
        mContactsFragment = new ContacsFragment();
        mStatusFragment = new StatusFragment();
        adapter.addFragment(mChatFragment,"CHAT");
        adapter.addFragment(mContactsFragment,"KONTAKT");
        adapter.addFragment(mStatusFragment,"STATUS");
        mViewPager.setAdapter(adapter);

        mTabLayout.setupWithViewPager(mViewPager);

        mSearchBar.setOnSearchActionListener(this);
        mSearchBar.inflateMenu(R.menu.main_menu);
        mSearchBar.getMenu().setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.itemSignOut){
                    signOut();;
                }
                return true;
            }
        });

        mAuthProvider = new AuthProvider();

    }
    private void signOut() {
        mAuthProvider.signOut();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {

    }

    @Override
    public void onSearchConfirmed(CharSequence text) {

    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }
}





