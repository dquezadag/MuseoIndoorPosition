package es.alcala.museum;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.graphics.PointF;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;


import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

import com.mapbox.geojson.Feature;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.mapbox.turf.TurfJoins;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ANCHOR_BOTTOM;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAnchor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

import static com.mapbox.mapboxsdk.style.expressions.Expression.exponential;
import static com.mapbox.mapboxsdk.style.expressions.Expression.interpolate;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;
import static com.mapbox.mapboxsdk.style.expressions.Expression.zoom;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


public class MainActivity extends AppCompatActivity implements MapboxMap.OnMapClickListener {
    private GeoJsonSource indoorBuildingSource;
    private GeoJsonSource indoorPOISource;
    private LatLng currentPosition = new LatLng(40.635657, -3.168565);
    private List<List<Point>> boundingBoxList;
    private View levelButtons;
    private MapView mapView;
    private SymbolManager symbolManager;
    private MarkerView markerView;
    private MarkerViewManager markerViewManager;
    private static final String MARKER_INFO_LAYERID = "POIs_LAYER_ID";
    private static final String FEATURE_NAME = "name";
    private static final String FEATURE_DESCRIPTION = "description";
    private Style mapStyle;
    private MapboxMap mapboxMap;

    private Double [][] points = {
            {40.635656, -3.168495, 0.0},
            {40.635656, -3.168541,0.0},
            {40.635678, -3.168557,0.0},
            {40.635714, -3.168569,0.0},
            {40.635736, -3.168522,1.0},
            {40.635753, -3.168472,1.0},
            {40.635821, -3.168481,1.0},
            {40.635888, -3.168521,1.0},
            {40.635959, -3.168571,1.0},
            {40.635971, -3.16865,1.0},

    };

    private int currentPointIdx = 0;

    private static final String ICON_ID = "ICON_ID";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        // Mapbox.getInstance(this, getString(R.string.access_token));
        Mapbox.getInstance(this, "pk.eyJ1IjoiZGFyd2lucXVlemFkYSIsImEiOiJja2libzZvMXIwYTA5MnJvNTVieWZ6MDh0In0.BvOUJY8W0v_ShUnvm8m22Q");

        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(@NonNull final MapboxMap mapBoxMap) {
                mapboxMap = mapBoxMap;
                mapBoxMap.addOnMapClickListener(MainActivity.this);

                mapBoxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    //mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/darwinquezada/cjfobz6b92ulv2rl8t9troc8v"), new Style.OnStyleLoaded() {

                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        mapStyle = style;
                        levelButtons = findViewById(R.id.floor_level_buttons);

                        final List<Point> boundingBox = new ArrayList<>();

                        boundingBox.add(Point.fromLngLat(-3.17273, 40.637508));
                        boundingBox.add(Point.fromLngLat(-3.17273, 40.630986));
                        boundingBox.add(Point.fromLngLat(-3.157806, 40.630986));
                        boundingBox.add(Point.fromLngLat(-3.157806, 40.637508));

                        boundingBoxList = new ArrayList<>();
                        boundingBoxList.add(boundingBox);

                        mapboxMap.addOnCameraMoveListener(new MapboxMap.OnCameraMoveListener() {
                            @Override
                            public void onCameraMove() {
                                if (mapboxMap.getCameraPosition().zoom > 16) {

                                    if (TurfJoins.inside(Point.fromLngLat(mapboxMap.getCameraPosition().target.getLongitude(),
                                            mapboxMap.getCameraPosition().target.getLatitude()), Polygon.fromLngLats(boundingBoxList))) {
                                        if (levelButtons.getVisibility() != View.VISIBLE) {
                                            showLevelButton();
                                        }
                                    } else {
                                        if (levelButtons.getVisibility() == View.VISIBLE) {
                                            hideLevelButton();
                                        }
                                    }
                                } else if (levelButtons.getVisibility() == View.VISIBLE) {
                                    hideLevelButton();
                                }
                            }
                        });

                        GeoJsonOptions geoJsonOptions = new GeoJsonOptions().withTolerance(0.4f);
                        symbolManager = new SymbolManager(mapView, mapboxMap, style, null, geoJsonOptions);


                        // set non data driven properties
                        symbolManager.setIconAllowOverlap(true);
                        symbolManager.setTextAllowOverlap(true);

                        indoorBuildingSource = new GeoJsonSource(
                                "indoor-building", loadJsonFromAsset("PlantaBaja.geojson"));
                        style.addSource(indoorBuildingSource);

                        indoorPOISource = new GeoJsonSource("indoor-pois", loadJsonFromAsset("POI_PlantaBaja.geojson"));
                        style.addSource(indoorPOISource);

                        style.addImage(ICON_ID,BitmapFactory.decodeResource(
                                MainActivity.this.getResources(), R.drawable.mapbox_marker_icon_default));
                        // Add the building layers since we know zoom levels in range
                        loadBuildingLayer(style);
                        loadPOILayer(style);

                        //Add POIs to the Mapview
                        /*Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.mapbox_info_icon_default, null);
                        Bitmap icon = BitmapUtils.getBitmapFromDrawable(drawable);
                        GeoJSONToMap(style, "indoor_pois", MARKER_INFO_LAYERID, "POI_PlantaBaja.geojson", icon);*/

                    }
                });

                Button buttonSecondLevel = findViewById(R.id.second_level_button);
                buttonSecondLevel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removeMarkView();
                        indoorBuildingSource.setGeoJson(loadJsonFromAsset("PrimeraPlanta.geojson"));
                        indoorPOISource.setGeoJson(loadJsonFromAsset("POI_PrimeraPlanta.geojson"));
                    }
                });

                Button buttonGroundLevel = findViewById(R.id.ground_level_button);
                buttonGroundLevel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removeMarkView();
                        indoorBuildingSource.setGeoJson(loadJsonFromAsset("PlantaBaja.geojson"));
                        indoorPOISource.setGeoJson(loadJsonFromAsset("POI_PlantaBaja.geojson"));
                    }
                });

                FloatingActionButton fab = findViewById(R.id.fab);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Double[] currentPoint = points[(currentPointIdx++)%10];
                        // create nearby symbols
                        symbolManager.deleteAll();
                        SymbolOptions nearbyOptions = new SymbolOptions()
                                .withLatLng(position(currentPoint[0], currentPoint[1]))
                                .withIconImage(ICON_ID)
                                .withIconSize(1f)
                                .withSymbolSortKey(5.0f)
                                .withDraggable(false);
                        symbolManager.create(nearbyOptions);
                    }
                });
            }
        });
    }

    private void loadPOILayer(@NonNull Style style) {

        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.mapbox_info_icon_default, null);
        Bitmap icon = BitmapUtils.getBitmapFromDrawable(drawable);

        style.addImage(MARKER_INFO_LAYERID + " marker", icon);

        SymbolLayer symbolLayer = new SymbolLayer(MARKER_INFO_LAYERID, "indoor-pois");

        symbolLayer.setProperties(
                iconImage(MARKER_INFO_LAYERID + " marker"),
                iconAllowOverlap(true),
                iconAnchor(ICON_ANCHOR_BOTTOM),
                iconIgnorePlacement(true)
        );
        style.addLayer(symbolLayer);
    }

    private void removeMarkView(){
        if (markerViewManager != null) {
            markerViewManager.removeMarker(markerView);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapboxMap != null) {
            mapboxMap.removeOnMapClickListener(this);
        }
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void hideLevelButton() {
        // When the user moves away from our bounding box region or zooms out far enough the floor level
        // buttons are faded out and hidden.
        AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setDuration(500);
        levelButtons.startAnimation(animation);
        levelButtons.setVisibility(View.GONE);
    }

    private void showLevelButton() {
        // When the user moves inside our bounding box region or zooms in to a high enough zoom level,
        // the floor level buttons are faded out and hidden.
        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(500);
        levelButtons.startAnimation(animation);
        levelButtons.setVisibility(View.VISIBLE);
    }

    private void loadBuildingLayer(@NonNull Style style) {
        // Method used to load the indoor layer on the map. First the fill layer is drawn and then the
        // line layer is added.

        FillLayer indoorBuildingLayer = new FillLayer("indoor-building-fill", "indoor-building").withProperties(
                fillColor(Color.parseColor("#00FFFFFF")),
                // Function.zoom is used here to fade out the indoor layer if zoom level is beyond 16. Only
                // necessary to show the indoor map at high zoom levels.
                fillOpacity(interpolate(exponential(1f), zoom(),
                        stop(16f, 0f),
                        stop(16.5f, 0.5f),
                        stop(17f, 1f))));

        style.addLayer(indoorBuildingLayer);

        LineLayer indoorBuildingLineLayer = new LineLayer("indoor-building-line", "indoor-building").withProperties(
                lineColor(Color.parseColor("#212121")),
                lineWidth(1f),
                lineOpacity(interpolate(exponential(1f), zoom(),
                        stop(16f, 0f),
                        stop(16.5f, 0.5f),
                        stop(17f, 1f))));
        //style.addLayer(indoorBuildingLineLayer);
        style.addLayerBelow(indoorBuildingLineLayer,"tunnel-street-minor-low");
    }

    private LatLng position(double latitude, double longitude){
        return new LatLng(latitude,longitude);
    }

    private String loadJsonFromAsset(String filename) {
        // Using this method to load in GeoJSON files from the assets folder.
        try {
            InputStream is = getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, Charset.forName("UTF-8"));

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        //Toast.makeText(MainActivity.this, String.format("Click en: %s", point.toString()), Toast.LENGTH_LONG).show();
        final PointF pixel = mapboxMap.getProjection().toScreenLocation(point);

        removeMarkView();

        List<Feature> features = mapboxMap.queryRenderedFeatures(mapboxMap.getProjection().toScreenLocation(point), MARKER_INFO_LAYERID);
        if (!features.isEmpty()) {
            String name = features.get(0).getStringProperty(FEATURE_NAME);
            String description = features.get(0).getStringProperty(FEATURE_DESCRIPTION);

            // Initialize the MarkerViewManager
            markerViewManager = new MarkerViewManager(mapView, mapboxMap);

            // Use an XML layout to create a View object
            View customView = LayoutInflater.from(MainActivity.this).inflate(
                    R.layout.content_info_popup, null);
            customView.setLayoutParams(new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));

            // Set the View's TextViews with content
            TextView titleTextView = customView.findViewById(R.id.marker_window_title);
            titleTextView.setText(name);

            TextView snippetTextView = customView.findViewById(R.id.marker_window_description);
            snippetTextView.setText(description);

            // Use the View to create a MarkerView which will eventually be given to
            // the plugin's MarkerViewManager class
            markerView = new MarkerView(new LatLng(point.getLatitude(), point.getLongitude()), customView);
            markerViewManager.addMarker(markerView);

            return true;
        } else {
            return false;
        }
    }

}
