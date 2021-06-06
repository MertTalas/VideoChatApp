package com.impostors.videochatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class MainEkstra extends AppCompatActivity {
    private TabLayout tablayout;
    private ViewPager2 viewPager2;
    private ArrayList<Fragment> fragments=new ArrayList<>();
    private ArrayList<String> fragmentheader=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ekstra_main);
        tablayout=findViewById(R.id.tabLayout);
        viewPager2=findViewById(R.id.viewPager2);
        fragments.add(new ConferanceFragment());
        fragments.add(new ContactsFragment());

        MyViewPagerAdapter adapter=new MyViewPagerAdapter(this);
        viewPager2.setAdapter(adapter);
        fragmentheader.add("Konferans");
        fragmentheader.add("Özel Kontaklar");
        new TabLayoutMediator(tablayout,viewPager2,(tab, position) -> tab.setText(fragmentheader.get(position))).attach();
    }
    private class MyViewPagerAdapter extends FragmentStateAdapter{
        public MyViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }
    }
}