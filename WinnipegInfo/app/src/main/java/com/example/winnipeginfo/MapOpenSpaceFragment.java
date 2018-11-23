package com.example.winnipeginfo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MapOpenSpaceFragment extends Fragment implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback
        , GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener,GoogleMap.OnPolylineClickListener,
        GoogleMap.OnPolygonClickListener{

    SupportMapFragment mapFragment;
    private ProgressDialog progressDialog;
    private GoogleMap mMap;

    private static String TAG = "My Location";
    private LatLng myPosition;
    private ArrayList<Marker> markerArrayList =new ArrayList<Marker>();
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DBHelper openSpaceHelper;
    private static final String TABLENAME="OpenSpace";

    //search
    private FloatingSearchView mSearchView;

    private String url = "https://data.winnipeg.ca/resource/nh34-5ztm.json";
    private String tag = "QueueTag";

    int MY_LOCATION_REQUEST_CODE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_map_open_space,container,false);
        mapFragment=(SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map_open_space);
        if (mapFragment==null)
        {
            FragmentManager fragmentManager =getFragmentManager();
            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
            mapFragment=SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map_open_space,mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        View mapView = mapFragment.getView();
        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 100, 200);
        }

        //search button linster
        mSearchView =v.findViewById(R.id.floating_search_view_open_space);
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                //get suggestions based on newQuery
                // List<SearchSuggestion> suggestions=new ArrayList<SearchSuggestion>() ;
                //pass them on to the search view
                //mSearchView.swapSuggestions(suggestions);
                //display in map
                onMapSearch(newQuery);
            }

        });
        firebaseAuth = FirebaseAuth.getInstance();
        //final FirebaseUser user = firebaseAuth.getCurrentUser();
        user = firebaseAuth.getCurrentUser();
        openSpaceHelper= new DBHelper(getActivity());

        return v;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        progressDialog = new ProgressDialog(getActivity(), ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("loading...");
        progressDialog.show();
        mMap = googleMap;
        //turning on the my location layer
        //(runtime location permission is required)
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "enabling my location");
            mMap.setMyLocationEnabled(true);

            mMap.setOnMyLocationButtonClickListener((GoogleMap.OnMyLocationButtonClickListener) this);
            mMap.setOnMyLocationClickListener((GoogleMap.OnMyLocationClickListener) this);
        } else {
            // Show rationale and request permission.
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            // Getting latitude of the current location
            double latitude = location.getLatitude();

            // Getting longitude of the current location
            double longitude = location.getLongitude();

            // Creating a LatLng object for the current location
            LatLng latLng = new LatLng(latitude, longitude);

            myPosition = new LatLng(latitude, longitude);

            mMap.addMarker(new MarkerOptions().position(myPosition).title("My Location"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom( myPosition, 15));
        }

        //add marker
        getJsonData();
        mMap.setOnPolygonClickListener(this);
        mMap.setOnPolylineClickListener(this);
        //marker listener
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {if (!marker.getTitle().equals("My Location")) {
                OpenSpace openSpace = (OpenSpace) marker.getTag();
                showDialog(openSpace);
            }
                // Show your alert dialog for the information related to the position
                return false;
            }
        });


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0].equals(android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //either check to see if permission is available, or handle a potential SecurityException before calling mMap.setMyLocationEnabled
                try {
                    mMap.setMyLocationEnabled(true);
                } catch(SecurityException e) {
                    Log.d(TAG, "SecurityException in MapsActivity.onRequestPermissionsResult: " + e.getMessage());
                }
            } else {
                // Permission was denied. Display an error message.
                Toast.makeText(getActivity(), "Permission to access your location was denied so your location cannot be displayed on the map.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getActivity(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(getActivity(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }
    //request from the url parse to json
    private void getJsonData() {

        // JsonArrayRequest class instance to get json array
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d(TAG, "onResponse: "+response);
                        for (int i = 0; i <response.length(); i++){
                            try {
                                // Gets jsonObject at index i
                                JSONObject jsonObject = response.getJSONObject(i);
                                String park_id =jsonObject.getString("park_id");
                                String park_name=jsonObject.getString("park_name");
                                String location=jsonObject.getString("location");
                                String category=jsonObject.getString("category");
                                String district=jsonObject.getString("district");
                                String nbhd=jsonObject.getString("nbhd");
                                String ward=jsonObject.getString("ward");
                                String area_ha=jsonObject.getString("area_ha");
                                String water_area=jsonObject.getString("water_area");
                                String land_area=jsonObject.getString("land_area");
                                OpenSpace openSpace =new OpenSpace(park_id,park_name,location,category,district,nbhd,ward,area_ha,water_area,land_area);
                                JSONObject the_geom=jsonObject.getJSONObject("the_geom");
                                JSONArray coordinates= the_geom.getJSONArray("coordinates").getJSONArray(0).getJSONArray(0);
                                PolygonOptions polygonOptions=new PolygonOptions();
                               for (int j=0;j<coordinates.length();j++)
                                {
                                    JSONArray coordinate=coordinates.getJSONArray(j);
                                    Double latitude =coordinate.getDouble(1);
                                    Double longitude = coordinate.getDouble(0);
                                    LatLng latLng= new LatLng(latitude,longitude);
                                    polygonOptions.add(latLng);
                                    if (j==0)
                                    {
                                        Marker marker= mMap.addMarker(new MarkerOptions().position(latLng).title(park_name));
                                        marker.setSnippet(location);
                                        marker.setTag(openSpace);
                                        markerArrayList.add(marker);
                                    }
                                }
                                 polygonOptions.strokeColor(R.color.colorPrimaryDark);
                                 polygonOptions.fillColor(R.color.colorPrimary);
                                 Polygon polygon= mMap.addPolygon(polygonOptions);
                                 polygon.setTag(openSpace);
                                 progressDialog.dismiss();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // On error in parsing logs the error
                Log.e("Volley", error.toString());
            }
        });

        // Sets tag for the request
        jsonArrayRequest.setTag(tag);
        // Adds jsonArrayRequest to the request queue
        //  mReqQueue.add(jsonArrayRequest);
        VolleyRequest.getInstance().addToRequestQueue(jsonArrayRequest);
    }

        private  void showDialog(final OpenSpace openSpace) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(openSpace.toString());
        alertDialogBuilder.setNeutralButton("Dissmiss",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
        alertDialogBuilder.setPositiveButton("Add To My List",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //ADD TO DATABASE
                        long Before=openSpaceHelper.getTaskCount(TABLENAME);
                        openSpaceHelper.insertValuesToOpenSpace(openSpace.getPark_id()
                               ,openSpace.getPark_name(),openSpace.getLocation(),openSpace.getCategory()
                                ,openSpace.getDistrict(),openSpace.getNbhd(),openSpace.getWard()
                                ,openSpace.getArea_ha(),openSpace.getWater_area(),openSpace.getLand_area(),user.getEmail());
                        long after=openSpaceHelper.getTaskCount(TABLENAME);
                        if (Before<after) {
                            customToast("Add To List Successfully");
                        }
                        else{
                            customToast("Add To List Fail");
                        }

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void onMapSearch(String location) {

        boolean found=false;
        for (Marker marker :markerArrayList)
        {
            OpenSpace openSpace =(OpenSpace)marker.getTag();

            if (openSpace.getPark_name().trim().toUpperCase().equals(location.trim().toUpperCase()))
            {
                found=true;
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 23));
            }
        }

        if (found=false)
        {
            customToast("NO Found! check the text you enter");
        }

    }


    @Override
    public void onPolygonClick(Polygon polygon) {
        OpenSpace openSpace =(OpenSpace) polygon.getTag();
        showDialog(openSpace);
    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        OpenSpace openSpace =(OpenSpace) polyline.getTag();
        showDialog(openSpace);
    }

    //snack bar info
    private void customToast(String content) {
        Snackbar.make(getActivity().findViewById(R.id.container_maps), content, Snackbar.LENGTH_LONG)
                .setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
    }


}