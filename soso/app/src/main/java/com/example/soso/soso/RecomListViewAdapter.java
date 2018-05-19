package com.example.soso.soso;

/**
 * Created by SOSO on 2018-05-14.
 */
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;

import javax.net.ssl.HttpsURLConnection;

import static java.util.Arrays.*;


/**
 * Created by SOSO on 2018-05-14.
 */

public class RecomListViewAdapter extends BaseAdapter{
    private static ArrayList<RecomListViewItem> recomlistViewItemList =new ArrayList<RecomListViewItem>();
    ImageView iconImageView;
    TextView titleView;
    TextView addressView;
    Drawable icon;
    String url = "";
    String info[];
    final String[] contentType = {"관광지", "문화시설", "축제/공연/행사", "여행코스", "레포츠", "숙박", "쇼핑", "음식"}; //initial content type

    public RecomListViewAdapter(){}
    public RecomListViewAdapter(Context c) { }
    @Override
    public int getCount() {
        return recomlistViewItemList.size();
    }
    
    @Override
    public Object getItem(int position) {
        System.out.println("************* : " + position);
        return recomlistViewItemList.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();
        
        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        //System.out.println("AAAAAAAAAAAAAAAAA");
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.recommend_item, parent, false);
        }
        
        iconImageView = convertView.findViewById(R.id.recommendImg);
        titleView=convertView.findViewById(R.id.recommendText);
        addressView=convertView.findViewById(R.id.recommendAdd);
        RecomListViewItem recomListViewItem =recomlistViewItemList.get(position);
        
        //iconImageView.setImageBitmap(recomListViewItem.getIcon());
        titleView.setText(recomListViewItem.getName()); //이름 가져오기
        addressView.setText(recomListViewItem.getAddress()); //주소가져오기
        iconImageView.setImageResource(recomlistViewItemList.get(position).getImgId());
        
        return convertView;
    }
    
    /*public void addItem(String url, String text){
     RecomListViewItem item = new RecomListViewItem();
     item.setIcon(url);
     item.setText(text);
     recomlistViewItemList.add(item);
     }*/
    public void addItem(int img, String name,String address, String info){
        RecomListViewItem item = new RecomListViewItem();
        item.setName(name);
        item.setAddress(address);
        item.setImgId(img);
        item.setInfo(info);
        recomlistViewItemList.add(item);
    }
}