package com.example.ahn.signinwithgoogle;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class fragment_Recom_all extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference addRecomAllPlan;

    View view, main;
    GetArea getArea = new GetArea(); //object of GetArea() function
    RecomListViewAdapter listViewAdapter;
    ListView listview1;
    //Button searchBtn;
    HashMap<String,String[]> hash = new HashMap<>();
    Bundle bundle;

    private String region = "", sigungu = "", contentTypeID = "";
    AlertDialog.Builder builder;
    Recommend FR;
    Handler handler;
    int count = 0;
    ArrayList<RecomListViewItem> recomlistViewItemList =new ArrayList<RecomListViewItem>();

    public fragment_Recom_all() {
    }

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_fragment__recom_all, container, false);
        listview1 = view.findViewById(R.id.recom_all);

        listViewAdapter = new RecomListViewAdapter(recomlistViewItemList);
        // listViewAdapter = new RecomListViewAdapter();// Adapter 생성
        bundle=getArguments();

        FR = (Recommend) getActivity();
        getArea = FR.getObject();
//        searchBtn = FR.findViewById(R.id.searchBtn);
//        searchBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
        RecomListViewItem item = new RecomListViewItem();

        recomlistViewItemList.clear();


        region = FR.getRegionItem();
        sigungu = FR.getSigunguItem();
        contentTypeID = bundle.getString("contentTypeID");
        Searching_basedRegion tour = new Searching_basedRegion(region, sigungu, contentTypeID, getArea, hash);
        tour.main();

        Iterator<String> iterator3 = hash.keySet().iterator();

        while (iterator3.hasNext()) {
            String title = "";
            String addr = "", mapX = "", mapY = "", imgURL = "";
            int imgId = 0;

            String key = (String) iterator3.next();
            addr = hash.get(key)[0];
            contentTypeID = hash.get(key)[1];
            title = hash.get(key)[6];
            mapX = hash.get(key)[3];
            mapY = hash.get(key)[4];
            imgURL = hash.get(key)[2];
            imgId = classification(hash.get(key)[1]);

            listViewAdapter.addItem(imgId, key, contentTypeID, title, addr, mapX, mapY, imgURL);//컨텐츠 타입, 이름, 주소 보내기, // 정보도 보내야할 것 같음
            listViewAdapter.notifyDataSetChanged();
            }
        listview1.setAdapter(listViewAdapter);


        listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final RecomListViewItem item = (RecomListViewItem)listViewAdapter.getItem(position);

                GetDetailInfo getDetailInfo = new GetDetailInfo(item.getContentID(), item.getContentTypeID());
                getDetailInfo.main();

                String message = "";

                Iterator<String> iterator = getDetailInfo.detailInfoHashMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    message += key + getDetailInfo.detailInfoHashMap.get(key) + "\n";
                }

                builder = new AlertDialog.Builder(getActivity());
                final String finalMessage = message;
                builder.setTitle("Do you prefer to add "+ item.getName()+"?")
                        //.setIcon(item.getMainImg())  //이게 사진 받는 함수고
                        .setMessage(item.getAddress() + "\n" + message) //이게 정보 받아주는 함수
                        .setCancelable(false)

                        //******************************여기갈랭! 버튼을 누르면 데이터 베이스에 넣어주기*******************//
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Plan plan = new Plan(item.getName(),item.getAddress(), item.getMapX(), item.getMapY(),item.getAddress());
                                mAuth = FirebaseAuth.getInstance();
                                addRecomAllPlan = FirebaseDatabase.getInstance().getReference();
                                FirebaseUser mUser = mAuth.getCurrentUser();
                                addRecomAllPlan.child("Users").child(mUser.getUid()).child("TravelTemp").child(item.getContentID()).setValue(plan);
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
                //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("daummaps://look?p="+item.getMapX()+","+item.getMapY())));
                AlertDialog dialog = builder.create();    // 알림창 객체 생성
                dialog.show();
            }
        });
       return view;
    }

    class temp{
        String title;
        double mapX;
        double mapY;
    }
    public void view_listData() {
        // 리스트뷰 참조 및 Adapter달기


    }

    public int classification(String content) {
        int temp = 0;

        switch (content) {
            case "12":
                temp = R.drawable.attraction;//관광지
                break;
            case "14":
                temp = R.drawable.culture; //문화시설
                break;
            case "15":
                temp = R.drawable.festival; //축제,공연,행사
                break;
            case "25":
                temp = R.drawable.travel; //여행코스
                break;
            case "28":
                temp = R.drawable.leisure; //레포츠
                break;
            case "32":
                temp = R.drawable.hotel; //숙박
                break;
            case "38":
                temp = R.drawable.shopping; //쇼핑
                break;
            case "39":
                temp = R.drawable.restaurant; //음식
                break;
        }
        return temp;
    }
}

