package com.example.winnipeginfo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MapCommunityFragment extends Fragment implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback
        , GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    SupportMapFragment mapFragment;

    private GoogleMap mMap;

    private static String TAG = "My Location";
    private LatLng myPosition;
    private ArrayList<Marker> markerArrayList =new ArrayList<Marker>();
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DBHelper communityHelper;
    private static final String TABLENAME="Community";
    //search
    private FloatingSearchView mSearchView;

    private String url = "https://data.winnipeg.ca/resource/m6ra-xm9i.json";
    private String tag = "QueueTag";
  //  private RequestQueue mReqQueue;
    //pregress
    private ProgressDialog progressDialog;

    int MY_LOCATION_REQUEST_CODE = 1;

    public MapCommunityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_map_community,container,false);
        mapFragment=(SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map_community);
        if (mapFragment==null)
        {
            FragmentManager fragmentManager =getFragmentManager();
            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
            mapFragment=SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map_community,mapFragment).commit();
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
        mSearchView =v.findViewById(R.id.floating_search_view_community);
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
        communityHelper= new DBHelper(getActivity());

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

        //marker listener
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if (!marker.getTitle().equals("My Location")) {
                            Community community = (Community) marker.getTag();
                            showDialog(community);
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

                        for (int i = 0; i < response.length(); i++){
                            try {
                                // Gets jsonObject at index i
                                JSONObject jsonObject = response.getJSONObject(i);

                                // gets individual values of data according to key names
                                String name = jsonObject.getString("name");
                                final String address = jsonObject.getString("address");
                                JSONObject location_1= jsonObject.getJSONObject("location_1");
                                Double latitude = Double.parseDouble(location_1.getString("latitude"));
                                Double longitude = Double.parseDouble(location_1.getString("longitude"));
                                LatLng latLng= new LatLng(latitude,longitude);
                                String complex_id=jsonObject.getString("complex_id");
                                //add to list
                                //Community community = new Community(name,address,website,email,complex_id);
                                Community community = new Community(name,address,complex_id);
                                // add marker
                                Marker marker=mMap.addMarker(new MarkerOptions().position(latLng).title(name));
                                marker.setSnippet(address);
                                marker.setTag(community);
                                markerArrayList.add(marker);
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
        VolleyRequest.getInstance().addToRequestQueue(jsonArrayRequest);
    }

    private  void showDialog(final Community community) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(community.toString());
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
                long Before=communityHelper.getTaskCount(TABLENAME);
                communityHelper.insertValuesToCommunity(community.getName(),
                        community.getAddress(),community.getComplexId(),user.getEmail());
                long after=communityHelper.getTaskCount(TABLENAME);
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

        boolean found =false;
        for (Marker marker:markerArrayList)
        {
            Community community=(Community) marker.getTag();
            if (community.getName().toUpperCase().trim().equals(location.toUpperCase().trim()))
            {
                found=true;
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 14));
            }
        }
        if (found=false)
        {
            customToast("NO Found! check the text you enter");
        }

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
