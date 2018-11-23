package com.example.winnipeginfo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OpenSpaceRecyclerviewFragment extends android.support.v4.app.Fragment {
    private static final boolean GRID_LAYOUT = false;
    private OpenSpaceRecyclerViewAdapter adapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DBHelper openSpaceHelper;
    private static final String TABLENAME="OpenSpace";
    private static final String COLNAME="ParkID";
    private List<OpenSpace> items;
    // private static final int ITEM_COUNT = 100;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    public static OpenSpaceRecyclerviewFragment newInstance() {
        return new OpenSpaceRecyclerviewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        firebaseAuth = FirebaseAuth.getInstance();
        //final FirebaseUser user = firebaseAuth.getCurrentUser();
        user = firebaseAuth.getCurrentUser();
        openSpaceHelper = new DBHelper(getActivity());
        items = openSpaceHelper.loadOpenSpaceData(user.getEmail());

        //setup materialviewpager

        if (GRID_LAYOUT) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
        mRecyclerView.setHasFixedSize(true);

        //Use this now
        mRecyclerView.addItemDecoration(new ItemDecoration(getActivity(), LinearLayoutManager.VERTICAL, 16));
        mRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());
        adapter = new OpenSpaceRecyclerViewAdapter(items);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                OpenSpace openSpace = items.get(position);
                showDialog(openSpace);
                // Toast.makeText(getActivity().getApplicationContext(), community.getName() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    private void showDialog(final OpenSpace openSpace) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(openSpace.toString());
        alertDialogBuilder.setNegativeButton("Delete",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //Delete from DATABASE
                        long Before = openSpaceHelper.getTaskCount(TABLENAME);
                        openSpaceHelper.deleteRow(TABLENAME,COLNAME,openSpace.getPark_id());
                        long after = openSpaceHelper.getTaskCount(TABLENAME);
                        if (Before > after) {
                            customToast("Delete Successfully");
                        } else {
                            customToast("Delete Fail");
                        }
                        updateData();


                    }
                });
        alertDialogBuilder.setPositiveButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    private void updateData() {
        items = openSpaceHelper.loadOpenSpaceData(user.getEmail());
        adapter = new OpenSpaceRecyclerViewAdapter(items);
        mRecyclerView.setAdapter(adapter);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        openSpaceHelper.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateData();

    }

    //snack bar info
    private void customToast(String content) {
        Snackbar.make(getActivity().findViewById(R.id.content_successful), content, Snackbar.LENGTH_LONG)
                .setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
    }
}

