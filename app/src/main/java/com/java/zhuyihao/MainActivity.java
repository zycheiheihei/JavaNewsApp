package com.java.zhuyihao;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity implements Config{
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private ArrayList<String> titleList;
    private ArrayList<Fragment> fragmentList;

    private void init(){
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager2 = (ViewPager2) findViewById(R.id.view_pager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        try {
            titleList = new ArrayList<String>(Arrays.asList(NEWS_TAGS));
            fragmentList = new ArrayList<>();
            for(String title:titleList){
                fragmentList.add(new NewsListFragment(TAGS2NUM.get(title)));
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NewsDataBaseServer.init();
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        final Context context =this;
        Log.d("address", Environment.getExternalStorageDirectory().getAbsolutePath());
        setSupportActionBar(toolbar);
        Statistics.getInstance().refresh();
        ExpertsProfile.getInstance().getExperts();
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        BottomNavigationViewHelper.removeNavigationShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.news_button:
                        Log.d("Bottom Clicked","news Clicked");
                        break;
                    case R.id.data_button:
                        Log.d("Bottom Clicked","data Clicked");
                        Intent intentData = new Intent(context,StatisticActivity.class);
                        context.startActivity(intentData);
                        break;
                    case R.id.graph_button:
                        Log.d("Bottom Clicked","graph Clicked");
                        Intent intentKG = new Intent(context,KnowledgeGraphActivity.class);
                        context.startActivity(intentKG);
                        break;
                    case R.id.class_button:
                        Log.d("Bottom Clicked","class Clicked");
                        break;
                    case R.id.scholar_button:
                        Log.d("Bottom Clicked","scholar Clicked");
                        Intent intentExpert = new Intent(context,ExpertsListActivity.class);
                        context.startActivity(intentExpert);
                        break;
                    default:
                }
                return true;
            }
        });
        init();
        viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        FragmentAdapter fragmentAdapter = new FragmentAdapter(this,fragmentList);
        viewPager2.setAdapter(fragmentAdapter);

        //关联TabLayout 添加attach()
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(titleList.get(position));
            }
        }).attach();

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Log.d("page Selected",position+"");
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.history:
                Toast.makeText(this,"You clicked History",Toast.LENGTH_SHORT).show();
                Intent intentHistory = new Intent(this,HistoryActivity.class);
                this.startActivity(intentHistory);
                break;
            case R.id.edit:
                Toast.makeText(this,"You clicked Edit",Toast.LENGTH_SHORT).show();
                Intent intentEdit = new Intent(this,EditActivity.class);
                intentEdit.putExtra("currentTagCondition",formEditString());
                this.startActivityForResult(intentEdit,1);
                break;
            case R.id.search:
                Toast.makeText(this,"You clicked Search",Toast.LENGTH_SHORT).show();
                Intent intentSearch = new Intent(this,SearchActivity.class);
                this.startActivity(intentSearch);
                break;
            default:
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("MAIN",requestCode+" & "+resultCode);
        if (requestCode == 1 && resultCode == 2) {
            String returnedTags = data.getStringExtra("changedTag");
            Log.e("MAIN",returnedTags);
        }
    }

    private String formEditString(){
        StringBuilder sb =  new StringBuilder();
        for(String tag:titleList){
            sb.append(TAGS2NUM.get(tag));
        }
        return sb.toString();
    }
}