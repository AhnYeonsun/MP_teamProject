package com.example.ahn.signinwithgoogle;
/**
 * Created by SOSO on 2018-05-14.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

//TODO : if CheckBox is checked, DB update.
//TODO : if CheckBox is deleted, DB update.
//TODO : show bucket list every update or delete.

public class ChecklistListViewAdapter extends BaseAdapter{
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<Checklist_item> listViewItemList = new ArrayList<Checklist_item>() ;
    ImageView deleteBtn;
    TextView itemListView;
    CheckBox checkbox;

    private FirebaseAuth mAuth;
    private DatabaseReference DBrefer;

    AlertDialog.Builder builder;
    // ListViewAdapter의 생성자
    public ChecklistListViewAdapter(Context c) {
        builder= new AlertDialog.Builder(c);
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //final int pos = position;
        final Context context = parent.getContext();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        DBrefer = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid()).child("BucketList");

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_checklist_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        itemListView = (TextView) convertView.findViewById(R.id.itemList) ;
        deleteBtn=convertView.findViewById(R.id.deletebtn);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        final Checklist_item listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        itemListView.setText(listViewItem.list);
        if(listViewItem.check==0){

        }

        //ischeckedListener
        checkbox =  convertView.findViewById(R.id.checkbox);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {
                final String changePosition = listViewItemList.get(position).list;
                final int bool;
                if(b) bool = 1;
                else bool = 0;
                ValueEventListener changeListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()){
                            if((child.getKey()).equals(changePosition)){
                                Checklist_item c = child.getValue(Checklist_item.class);
                                c.setIsChecked(bool);
                                DBrefer.child(changePosition).setValue(c);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }; DBrefer.addListenerForSingleValueEvent(changeListener);
            }
        });

        // 아이템 삭제
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setTitle("Delete Bucket List")
                        .setMessage("Are you sure to delete "+listViewItem.list+"?")
                        .setCancelable(false) //뒤로 버튼 클릭시 취소 가능 설정
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {

                                final String deletePosition = listViewItemList.get(position).list;
                                Log.d("DELETE", position+"");
                                Log.d("DELETE", deletePosition);

                                ValueEventListener deleteListener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot child : dataSnapshot.getChildren()){
                                            if((child.getKey()).equals(deletePosition)){
                                                Log.d("DELETE",child.getKey());
                                                DBrefer.child(deletePosition).removeValue();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                }; DBrefer.addListenerForSingleValueEvent(deleteListener);

                                listViewItemList.remove(position); //position 0,1,2,...
                                notifyDataSetChanged();

                            }
                        })
                        .setNegativeButton("NO!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String list, int check) {
        Checklist_item item = new Checklist_item();
        item.setText(list);
        item.setIsChecked(check);
        listViewItemList.add(item);
    }
}