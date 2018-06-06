package com.example.ahn.signinwithgoogle;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Recommend extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    Intent informIntent;
    String title = "", startDate = "", endDate = "";
    int days = 0;
    GetArea getArea = new GetArea(); //object of GetArea() function
    Spinner regionSpinnerList, sigunguSpinnerList;
    Button searchBtn;
    String region = "", sigungu = "", contentTypeID = "";
    AlertDialog.Builder builder;
    ArrayList<String> regionList, sigunguList;
    ArrayAdapter<String> regionSpinnerAdapter, sigunguSpinnerAdapter;
    String regionCode = new String();
    boolean check = false;

    private FirebaseAuth mAuth;
    private DatabaseReference addRecomByday;
    private DatabaseReference getTempRecom;

    public int dayIndex;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_recommend);
        informIntent = getIntent();

        //Firebase Database setting
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser mUser = mAuth.getCurrentUser();
        addRecomByday = FirebaseDatabase.getInstance().getReference();
        getTempRecom = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid()).child("TravelTemp");
        //Iterable<DataSnapshot> iterable = getTempRecom.


        // ******* 지역 스피너 리스트 세팅********* //
        regionSpinnerList = findViewById(R.id.regionList);
        regionList = new ArrayList<String>(Arrays.asList(""));
        regionSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, regionList);

        getArea.getRegionCode();
        Iterator<String> iterator = getArea.regionHashMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            regionSpinnerAdapter.add(key);
        }
        regionSpinnerAdapter.notifyDataSetChanged();
        regionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regionSpinnerList.setAdapter(regionSpinnerAdapter);
        // *************************************** //

        // ************** 지역스피너 입력 후, 시군구 스피너 리스트 세팅 ****************  //
        sigunguSpinnerList = (Spinner) findViewById(R.id.sigunguList); //spinner of sigungu list
        sigunguList = new ArrayList<String>(Arrays.asList("")); //array list of spinner
        sigunguSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sigunguList);

        regionSpinnerList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (check) {
                    sigunguList.clear();
                    sigunguSpinnerAdapter.clear();
                    sigunguSpinnerList.setAdapter(sigunguSpinnerAdapter);
                    getArea.sigunguHashMap.clear();
                    regionCode = new SearchHashMap(getArea.regionHashMap, parent.getItemAtPosition(position).toString()).searching();
                    getArea.getSigunguCode(regionCode);

                    Iterator<String> iterator = getArea.sigunguHashMap.keySet().iterator();
                    while (iterator.hasNext()) {
                        String key = (String) iterator.next();
                        sigunguSpinnerAdapter.add(key);
                    }

                    sigunguSpinnerAdapter.notifyDataSetChanged();
                    sigunguSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // ****************************************** //
                } else {
                    check = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        searchBtn = findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set up the ViewPager with the sections adapter.
                mViewPager = (ViewPager) findViewById(R.id.container);
                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                mViewPager.setAdapter(mSectionsPagerAdapter);
                TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                tabLayout.setupWithViewPager(mViewPager);
            }
        });

        FloatingActionButton addRecom = (FloatingActionButton) findViewById(R.id.addRecom);
        addRecom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("recomfloating", "coming?");
                days = informIntent.getIntExtra("day", 0);
                final String[] items = new String[days + 1];

                if (days != 0) {
                    items[0] = "Entire Journey";
                    for (int i = 1; i <= days; i++) {
                        items[i] = "Date "+ i + "!";
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(Recommend.this);
                    builder.setTitle("What date do you want to go?");
                    builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dayIndex = which;
                            Toast.makeText(getApplicationContext(), items[which], Toast.LENGTH_LONG).show(); //items[which]가 유저가 선택한 날짜
                        }
                    });

                    //******* 온돈온돈 *********8//
                    ObjectForBlank objectForBlank = new ObjectForBlank(1,"1",1.1);
                    builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() { //이때 알고리즘 돌려서 알맞은 날짜에 디비랑 넣어주면 되염
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
//                            informIntent.getStringExtra("title");         //이거는 여행의 제목
//                            informIntent.getStringExtra("startDay");      //이거는 여행 출발 날짜
//                            informIntent.getStringExtra("endDay");        //여행 마지막 날짜
//                            days                                          //총 여행 일수

                            //DB에 Object Plan 형식으로 넣음. 똑같이 가져온 후, 맞은 일차에 넣고 temp 비우면 됨.
                            //전체 Days, title 불러오기
                            //GetDaysForTravel getDaysForTravel = new GetDaysForTravel();
                            //String[] AllDays = getDaysForTravel.getPD();
                            String[] AllDays = CalculateDays.days;
                            final String planTitle = CalculateDays.title;
                            if (dayIndex == 0) { //전체 + 알고리즘

                            } else {
                                //일차에 맞게 넣어주기
                                // AllDays의 0,1,2,... 는 whichButton의 1,2,3...과 같음. 주의***************
                                // Temp DB에 있는 객체들에게 날짜 부여 후 메인 플랜 DB에 넣기.
                                final String setday = AllDays[dayIndex - 1]; //넣어야할 날
                                ValueEventListener travelTempListener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                                            Plan plan = child.getValue(Plan.class);
                                            plan.Day = setday;
                                            addRecomByday.child("Users").child(mUser.getUid()).child(planTitle).push().setValue(plan);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                };
                                getTempRecom.addListenerForSingleValueEvent(travelTempListener);

                            }

                        }
                    });

                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });
                    builder.create().show();
                }
            }

        });

    }
    @Override
    protected void onPause(){
        super.onPause();
        getTempRecom.removeValue();

    }

    public String getRegionItem() {
        return (String) regionSpinnerList.getSelectedItem();
    }

    public String getSigunguItem() {
        return (String) sigunguSpinnerList.getSelectedItem();
    }

    public GetArea getObject() {
        return getArea;
    }

    public String getContentTypeID() {
        return contentTypeID;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fragment__search_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Bundle bundle = new Bundle(1);

            switch (position) {
                case 0:
                    fragment_Recom_all FA = new fragment_Recom_all();
                    bundle.putString("contentTypeID", "");
                    FA.setArguments(bundle);
                    return FA;
                case 1:
                    fragment_Recom_tourspot FT = new fragment_Recom_tourspot();
                    bundle.putString("contentTypeID", "12");
                    FT.setArguments(bundle);
                    return FT;
                case 2:
                    fragment_recom_cultural Fcul = new fragment_recom_cultural();
                    bundle.putString("contentTypeID", "14");
                    Fcul.setArguments(bundle);
                    return Fcul;
                case 3:
                    fragment_recom_event Fevent = new fragment_recom_event();
                    bundle.putString("contentTypeID", "15");
                    Fevent.setArguments(bundle);
                    return Fevent;
                case 4:
                    fragment_recom_course Fcourse = new fragment_recom_course();
                    bundle.putString("contentTypeID", "25");
                    Fcourse.setArguments(bundle);
                    return Fcourse;
                case 5:
                    fragment_recom_leports Fleport = new fragment_recom_leports();
                    bundle.putString("contentTypeID", "28");
                    Fleport.setArguments(bundle);
                    return Fleport;
                case 6:
                    fragment_recom_accom Faccom = new fragment_recom_accom();
                    bundle.putString("contentTypeID", "32");
                    Faccom.setArguments(bundle);
                    return Faccom;
                case 7:
                    fragment_recom_shopping Fshopping = new fragment_recom_shopping();
                    bundle.putString("contentTypeID", "38");
                    Fshopping.setArguments(bundle);
                    return Fshopping;
                case 8:
                    fragment_recom_food Ffood = new fragment_recom_food();
                    bundle.putString("contentTypeID", "39");
                    Ffood.setArguments(bundle);
                    return Ffood;
            }
            return null;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 9;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "All";
                case 1:
                    return "Tour spot";
                case 2:
                    return "Cultural facility";
                case 3:
                    return "Event";
                case 4:
                    return "Course";
                case 5:
                    return "Leports";
                case 6:
                    return "Accommodation";
                case 7:
                    return "Shopping";
                case 8:
                    return "Food";
                default:
                    return null;
            }
        }
    }

}