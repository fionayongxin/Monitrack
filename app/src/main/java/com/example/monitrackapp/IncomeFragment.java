package com.example.monitrackapp;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.monitrackapp.model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.DateFormat;
import java.util.Date;

public class IncomeFragment extends Fragment {

    ////to declare variable firebase

    private FirebaseAuth userAuth;
    private DatabaseReference incomeDatabase;

    //to declare variable recyclerView
    private RecyclerView recView;

    //to declare textView variable
    private TextView sumIncome;

    //to declare editText variable
    private EditText AmountEdit;
    private EditText TypeEdit;
    private EditText NoteEdit;

    //to declare button for update and delete

    private Button UpdateBttn;
    private Button DeleteBttn;

    //to declare item inside the textView

    private String type;
    private String note;
    private int amount;

    private String keyPosition;

    public IncomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myview = inflater.inflate(R.layout.fragment_income, container, false);

        userAuth= FirebaseAuth.getInstance();

        FirebaseUser mUser=userAuth.getCurrentUser();
        String uid =mUser.getUid();

//Create subdirectory at Firebase
        incomeDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        sumIncome=myview.findViewById(R.id.income_txt_result);

        recView=myview.findViewById(R.id.recycler_id_income);

        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recView.setHasFixedSize(true);
        recView.setLayoutManager(layoutManager);
//add value to the Firebase
        incomeDatabase.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                int tot =0;

                for (DataSnapshot mysanapshot: dataSnapshot.getChildren()){
                    Data data=mysanapshot.getValue(Data.class);
                    tot+=data.getAmount();
                    String stTotalvale=String.valueOf(tot);
                    sumIncome.setText(stTotalvale+".00");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError){

            }

        });
        return myview;
    }

    @Override
    public void onStart(){
        super.onStart();
//The FirebaseRecyclerAdapter binds a Query to a RecyclerView .
        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter=new FirebaseRecyclerAdapter<Data, MyViewHolder>
                (
                        Data.class,
                        R.layout.income_recycler_data,
                        MyViewHolder.class,
                        incomeDatabase
                ){
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, Data model, int position){
                viewHolder.setType(model.getType());
                viewHolder.setNote(model.getNote());
                viewHolder.setDate(model.getDate());
                viewHolder.setAmmount(model.getAmount());

                viewHolder.userView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        keyPosition=getRef(position).getKey();
                        type=model.getType();
                        note=model.getNote();
                        amount=model.getAmount();
                        updateDataItem();
                    }
                });
            }


        };

        recView.setAdapter(adapter);
    }
    //A ViewHolder describes an item view and metadata about its place within the RecyclerView.
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View userView;

        public MyViewHolder(View itemView) {
            super(itemView);
            userView=itemView;
        }
        private void setType(String type){
            TextView mType=userView.findViewById(R.id.type_txt_income);
            mType.setText(type);
        }
        private void setNote(String note){
            TextView mNote=userView.findViewById(R.id.note_txt_income);
            mNote.setText(note);
        }
        private void setDate(String date){
            TextView mDate=userView.findViewById(R.id.date_txt_income);
            mDate.setText(date);
        }
        private void setAmmount(int ammount){
            TextView mAmmount=userView.findViewById(R.id.ammount_txt_income);
            String stammount=String.valueOf(ammount);
            mAmmount.setText(stammount);
        }
    }
    //to update the data item
    private void updateDataItem(){
        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View myview=inflater.inflate(R.layout.update_data_item,null);
        mydialog.setView(myview);

        AmountEdit=myview.findViewById(R.id.amount_edt);
        TypeEdit=myview.findViewById(R.id.type_edt);
        NoteEdit=myview.findViewById(R.id.note_edt);

        //Set data to edit text

        TypeEdit.setText(type);
        TypeEdit.setSelection(type.length());

        NoteEdit.setText(note);
        NoteEdit.setSelection(note.length());

        AmountEdit.setText(String.valueOf(amount));
        AmountEdit.setSelection(String.valueOf(amount).length());



        UpdateBttn=myview.findViewById(R.id.btn_upd_Update);
        DeleteBttn=myview.findViewById(R.id.btnuPD_Delete);

        AlertDialog dialog= mydialog.create();

        UpdateBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type=TypeEdit.getText().toString().trim();
                note=NoteEdit.getText().toString().trim();

                String mdammount=String.valueOf(amount);

                mdammount=AmountEdit.getText().toString().trim();

                int myAmmount=Integer.parseInt(mdammount);

                String mDate= DateFormat.getDateInstance().format(new Date());
                Data data=new Data(myAmmount,type,note,keyPosition,mDate);

                incomeDatabase.child(keyPosition).setValue(data);

                dialog.dismiss();

            }
        });
        DeleteBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                incomeDatabase.child(keyPosition).removeValue();
                dialog.dismiss();

            }
        });
        dialog.show();


    }
}