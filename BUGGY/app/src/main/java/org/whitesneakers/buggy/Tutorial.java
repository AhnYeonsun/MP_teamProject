package org.whitesneakers.buggy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

public class Tutorial extends AppCompatActivity {

    private ViewPager mPager;
    View.OnClickListener mCloseButtonClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_main);

        PagerAdapterClass pagerAdapterClass = new PagerAdapterClass(getApplicationContext());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(pagerAdapterClass);
        mCloseButtonClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int infoFirst = 1;
                SharedPreferences a = getSharedPreferences("first", MODE_PRIVATE);
                SharedPreferences.Editor editor = a.edit();
                editor.putInt("first", infoFirst);
                editor.commit();
                finish();
            }
        };
    }

    public class PagerAdapterClass extends PagerAdapter {

        private LayoutInflater mInflater;

        public PagerAdapterClass(Context context) {
            super();
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return 12; //튜토리얼 갯수
        }


        @Override
        public Object instantiateItem(View pager, int position) {
            View view = null;

            switch (position) {
                case 0:
                    view = mInflater.inflate(R.layout.tutorial0, null);
                    view.findViewById(R.id.tutorial0);
                    break;
                case 1:
                    view = mInflater.inflate(R.layout.tutorial1, null);
                    view.findViewById(R.id.tutorial1);
                    break;
                case 2:
                    view = mInflater.inflate(R.layout.tutorial2, null);
                    view.findViewById(R.id.tutorial2);
                    break;
                case 3:
                    view = mInflater.inflate(R.layout.tutorial3, null);
                    view.findViewById(R.id.tutorial3);
                    break;
                case 4:
                    view = mInflater.inflate(R.layout.tutorial4, null);
                    view.findViewById(R.id.tutorial4);
                    break;
                case 5:
                    view = mInflater.inflate(R.layout.tutorial5, null);
                    view.findViewById(R.id.tutorial5);
                    break;
                case 6:
                    view = mInflater.inflate(R.layout.tutorial6, null);
                    view.findViewById(R.id.tutorial6);
                    break;
                case 7:
                    view = mInflater.inflate(R.layout.tutorial7, null);
                    view.findViewById(R.id.tutorial7);
                    break;
                case 8:
                    view = mInflater.inflate(R.layout.tutorial8, null);
                    view.findViewById(R.id.tutorial8);
                    break;
                case 9:
                    view = mInflater.inflate(R.layout.tutorial9, null);
                    view.findViewById(R.id.tutorial9);
                    break;
                case 10:
                    view = mInflater.inflate(R.layout.tutorial10, null);
                    view.findViewById(R.id.tutorial10);
                    break;
                case 11:
                    view = mInflater.inflate(R.layout.tutorial11, null);
                    view.findViewById(R.id.tutorial11);
                    view.findViewById(R.id.close1).setOnClickListener(mCloseButtonClick);
                    break;
            }
            ((ViewPager) pager).addView(view, 0);
            return view;
        }

        @Override
        public boolean isViewFromObject(View pager, Object object) {
            return pager == object;
        }

        @Override

        public void destroyItem(View pager, int position, Object view) {

            ((ViewPager) pager).removeView((View) view);

        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }

        @Override
        public void finishUpdate(View arg0) {
        }

    }


}
