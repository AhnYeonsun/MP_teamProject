package org.whitesneakers.buggy;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
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

public class fragment_recom_shopping extends android.support.v4.app.Fragment {
    private FirebaseAuth mAuth;
    private DatabaseReference addPlan;

    View view, main;
    GetArea getArea = new GetArea(); //object of GetArea() function
    RecomListViewAdapter listViewAdapter;
    ListView listview1;
    //Button searchBtn;
    private String region = "", sigungu = "", contentTypeID = "";
    private String key="", title="", addr="", mapX="", mapY="", imgURL="";
    private int imgId = 0;
    AlertDialog.Builder builder;
    Recommend FR;
    Handler handler;
    HashMap<String, String[]> tourspot = new HashMap<>();
    ArrayList<RecomListViewItem> recomlistViewItemList =new ArrayList<RecomListViewItem>();
    SharedPreferences dataBasket;
    SharedPreferences.Editor toEdit;
    Bundle bundle;

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_fragment_recom_shopping, container, false);
        listview1 = view.findViewById(R.id.recom_shopping);
        recomlistViewItemList.clear();
        tourspot.clear();

        listViewAdapter = new RecomListViewAdapter(recomlistViewItemList);// Adapter 생성

        FR = (Recommend) getActivity();
        getArea = FR.getObject();
        recomlistViewItemList.clear();
        listview1.setAdapter(listViewAdapter);

        region = FR.getRegionItem();
        sigungu = FR.getSigunguItem();
        bundle = getArguments();
        contentTypeID = bundle.getString("contentTypeID");
        Searching_basedRegion tour = new Searching_basedRegion(region, sigungu, contentTypeID, getArea, tourspot);
        tour.main();

        Iterator<String> iterator3 = tourspot.keySet().iterator();

        while (iterator3.hasNext()) {
            String title = "";
            String addr = "", mapX = "", mapY = "", imgURL = "";
            int imgId = 0;

            String key = (String) iterator3.next();
            addr = tourspot.get(key)[0];
            contentTypeID = tourspot.get(key)[1];
            title = tourspot.get(key)[6];
            mapX = tourspot.get(key)[4];
            mapY = tourspot.get(key)[3];
            imgURL = tourspot.get(key)[2];

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
                builder.setTitle("Do you prefer to add "+item.getName()+"?")
                        .setMessage(item.getAddress() + "\n" + message)
                        .setCancelable(false)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Plan plan = new Plan(item.getName(),item.getAddress(), item.getMapX(), item.getMapY(),finalMessage);
                                mAuth = FirebaseAuth.getInstance();
                                addPlan = FirebaseDatabase.getInstance().getReference();
                                FirebaseUser mUser = mAuth.getCurrentUser();
                                addPlan.child("Users").child(mUser.getUid().toString()).child("TravelTemp").child(item.getContentID()).setValue(plan);
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();    // 알림창 객체 생성
                dialog.show();
            }
        });
        return view;
    }
}
