package example.luisnava.com.firebaseexample;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adaptor.ListViewAdapter;
import Classes.Stores;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment {

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private List<Stores> dataList;
    private ListView mListView;
    private ListViewAdapter myAdapter;
    private ProgressDialog progressDialog;
    View mView;


    public ListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_list, container, false);


        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataList = new ArrayList<>();
        mListView = (ListView) mView.findViewById(R.id.listView);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait loading list...");
        progressDialog.show();

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        myAdapter = new ListViewAdapter(getActivity(), R.layout.mylist, dataList);
        mListView.setAdapter(myAdapter);

        mDatabase.child("Stores").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Stores stores = singleSnapshot.getValue(Stores.class);
                    myAdapter.addElement(stores);
                }

                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                Log.e("Error", "onCancelled", databaseError.toException());
            }
        });
    }
}
