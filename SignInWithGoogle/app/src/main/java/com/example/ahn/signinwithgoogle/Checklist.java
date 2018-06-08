package com.example.ahn.signinwithgoogle;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Set;

public class Checklist extends android.support.v4.app.Fragment {
    public static Checklist newInstance() {
        Checklist fragment = new Checklist();
        return fragment;
    }

    //Log.d(TAG, variable);
    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;

    public static String bucketList_FILE = "bucketList";

    ImageButton addbtn; // 항목 추가버튼
    ListView userItemList; //완성된 체크리스트
    EditText addBar; // 추가할 친구들
    AlertDialog.Builder builder; // 꾹 누르면 이벤트 생성(삭제할때 꾹 누르면 알림창떠서 삭제 가능하게, 나중에 추가할 것: 소분류 꾹 누르면 체크리스트에 추가가능하도록)
    ChecklistListViewAdapter adapter;
    Set<String> values;

    private FirebaseAuth mAuth;
    private DatabaseReference getBucketList;
    private DatabaseReference addUserBucketList;
    private DatabaseReference getUserBucketList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //View view= super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_checklist, container, false);
        builder=new AlertDialog.Builder(getActivity());

        final ListView listview ;

        addBar=view.findViewById(R.id.addBar);
        addbtn=view.findViewById(R.id.addbtn);

        // Adapter 생성
        adapter = new ChecklistListViewAdapter(getActivity());

        //get list from DB to adapter
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        getBucketList = FirebaseDatabase.getInstance().getReference().child("DefaultBucketList");
        addUserBucketList = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid()).child("BucketList");

        //**************************************************************************************** default bucketlist를 user bucketlist에 넣음
        ValueEventListener addUserBucketListListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    Checklist_item c = child.getValue(Checklist_item.class);
                    addUserBucketList.child(child.getKey()).setValue(c);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }; getBucketList.addListenerForSingleValueEvent(addUserBucketListListener);
        //**************************************************************************************** 한 번만 실행되어야 함!

        getUserBucketList = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid()).child("BucketList");
        ValueEventListener getBucketListListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("CHECKLIST", "1");
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    Checklist_item c = child.getValue(Checklist_item.class);
                    Log.d("CHECKLIST", "2 " + c.list);
                    Log.d("CHECKLIST", "3" + c.check);
                    adapter.addItem(c.list, c.check);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }; getUserBucketList.addListenerForSingleValueEvent(getBucketListListener);

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) view.findViewById(R.id.userItemList);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBar.setText("");

                //입력 후 키보드 감추기
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(addBar.getWindowToken(), 0);
            }
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("CHECKLIST", "4");
                listview.setAdapter(adapter);
            }
        }, 3000);

        return view;
    }
}