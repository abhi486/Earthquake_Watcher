package com.example.earthquakewatcher;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.earthquakewatcher.Model.Earthquake;
import com.example.earthquakewatcher.UI.CustomInfoWindow;
import com.example.earthquakewatcher.Util.Constants;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerClickListener{

    private LocationManager locationManager;
    private LocationListener locationListener;
    private GoogleMap mMap;
    private RequestQueue requestQueue;
    private Earthquake earthquake;
    private BitmapDescriptor[] randomicon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        requestQueue = Volley.newRequestQueue(this);
        getEarthquakes();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

    private void getEarthquakes() {

        randomicon = new BitmapDescriptor[]{
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
        };

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                Constants.URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray features = response.getJSONArray("features");
                            for (int i=0;i<Constants.LIMIT;i++){
                                JSONObject properties = features.getJSONObject(i).getJSONObject("properties");

                                //get Json geometry Object
                                JSONObject geometry = features.getJSONObject(i).getJSONObject("geometry");

                                //get coordinates from geometry
                                JSONArray coordinates = geometry.getJSONArray("coordinates");

                                earthquake = new Earthquake();
                                earthquake.setLat(coordinates.getDouble(1));
                                earthquake.setLgn(coordinates.getDouble(0));
                                earthquake.setMagnitude(properties.getDouble("mag"));
                                earthquake.setDetailLink(properties.getString("detail"));
                                earthquake.setPlace(properties.getString("place"));
                                earthquake.setDate(properties.getLong("time"));
                                earthquake.setType(properties.getString("magType"));
                                earthquake.setTime(properties.getLong("time"));

                                java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
                                String date = dateFormat.format(new Date(earthquake.getDate()));

                                java.text.DateFormat format = java.text.DateFormat.getTimeInstance();
                                String time = format.format(new Date(earthquake.getTime()));

                                LatLng latLng = new LatLng(earthquake.getLat(),earthquake.getLgn());

                                if (earthquake.getMagnitude() >=2.0){
                                    CircleOptions circleOptions = new CircleOptions();
                                    circleOptions.center(latLng);
                                    circleOptions.fillColor(Color.RED);
                                    circleOptions.radius(30000);
                                    circleOptions.strokeWidth(4.0f);
                                    mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                            .snippet("Magnitude: " + earthquake.getMagnitude() + "\nDate: " + date + "\nTime: " + time).title(earthquake.getPlace())).setTag(earthquake.getDetailLink());
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,5));
                                    mMap.addCircle(circleOptions);
                                }
                                else {
                                    mMap.addMarker(new MarkerOptions().position(latLng).icon(randomicon[Constants.randomint(0, randomicon.length)])
                                            .snippet("Magnitude: " + earthquake.getMagnitude() + "\nDate: " + date + "\nTime: " + time).title(earthquake.getPlace())).setTag(earthquake.getDetailLink());
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
                                }
                                //Log.d("Maps", "onResponse: " + lon + ", " + lat);


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setInfoWindowAdapter(new CustomInfoWindow(getApplicationContext()));
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);
        if (Build.VERSION.SDK_INT < 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        else{
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},2);

            else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
//                mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
//                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        }

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,15));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            else
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},2);
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //Toast.makeText(MapsActivity.this,marker.getTitle(),Toast.LENGTH_SHORT).show();
        getearthquakedetails(marker.getTag().toString(),marker);

    }

    private void getearthquakedetails(String tag, final Marker marker) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET
                , tag, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String detailsURL;
                try {
                    JSONObject properties = response.getJSONObject("properties");
                    JSONObject products = properties.getJSONObject("products");
                    JSONArray geoserve = products.getJSONArray("geoserve");

                    for (int i=0;i<geoserve.length();i++){
                        JSONObject geoserveObj = geoserve.getJSONObject(i);
                        JSONObject contents = geoserveObj.getJSONObject("contents");
                        JSONObject geojsonObj = contents.getJSONObject("geoserve.json");

                        detailsURL = geojsonObj.getString("url");
                        getpopup(detailsURL,marker);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void getpopup(String url, final Marker marker) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                View view = getLayoutInflater().inflate(R.layout.popup,null);

                Button dismissbottom,dismisstop;
                TextView popuptext,titletext;
                WebView webtext;
                final AlertDialog dialog;
                AlertDialog.Builder builder;
                StringBuilder stringBuilder = new StringBuilder();

                dismissbottom = view.findViewById(R.id.dismissbottombutton);
                dismisstop = view.findViewById(R.id.dismisstopbutton);
                popuptext = view.findViewById(R.id.detailstext);
                titletext = view.findViewById(R.id.moreinfotext);
                webtext = view.findViewById(R.id.webtext);

                try {

                    if (response.has("tectonicSummary") && response.getString("tectonicSummary")!=null){
                        JSONObject tectonic = response.getJSONObject("tectonicSummary");
                        if (tectonic.has("text") && tectonic.getString("text")!=null){
                            String text = tectonic.getString("text");
                            webtext.loadDataWithBaseURL(null,text,"text/html","UTF-8",null);
                        }
                    }
                    JSONArray cities = response.getJSONArray("cities");
                    for (int i=0;i<cities.length();i++) {
                        JSONObject city = cities.getJSONObject(i);
                        stringBuilder.append("City : " + city.getString("name")+
                                "\n" + "Population : " + city.getString("population")+
                                "\n" + "Distance : " + city.getString("distance") + " Km");
                        stringBuilder.append("\n\n");
                    }
                    titletext.setText(marker.getTitle());
                    popuptext.setText(stringBuilder);
                    builder = new AlertDialog.Builder(MapsActivity.this);
                    builder.setView(view);
                    dialog = builder.create();
                    dialog.show();
                    dismissbottom.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dismisstop.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
