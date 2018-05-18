package com.example.ahn.ttubucke;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

import net.daum.mf.map.api.MapView;

@SuppressWarnings("ALL")
public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //MapView mapView = new MapView(this);
        //mapView.setDaumMapApiKey("3dde7e865dc040ff89b50813840dbc02");
        //ViewGroup mapViewContainer = (ViewGroup)findViewById(R.id.map_view);
        //mapViewContainer.addView(mapView);

        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("daummaps://route?sp=37.537229,127.005515&ep=37.4979502,127.0276368&by=PUBLICTRANSIT")));
    }
}
