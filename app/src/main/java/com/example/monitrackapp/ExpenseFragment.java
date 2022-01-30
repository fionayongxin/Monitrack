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

public class ExpenseFragment extends Fragment {

    //to declare variable firebase

    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;

    //to declare variable recyclerView
    private RecyclerView recyclerView;

    //to declare textView variable
    private TextView expenseSumResult;

    //to declare editText variable
    private EditText edtAmmount;
    private EditText edtType;
    private EditText edtNote;

    //to declare button for update and delete

    private Button btnUpdate;
    private Button btnDelete;

    //to declare item inside the textView

    private String type;
    private String note;
    private int ammount;

    private String post_key;

    public ExpenseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myview = inflater.inflate(R.layout.fragment_expense, container, false);

        mAuth= FirebaseAuth.getInstance();

        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid =mUser.getUid();
//Create subdirectory at Firebase
        mExpenseDatabase= FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);
        expenseSumResult=myview.findViewById(R.id.expense_txt_result);

        recyclerView=myview.findViewById(R.id.recycler_id_expense);

        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
//add value to the Firebase
        mExpenseDatabase.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                int expensnSum =0;

                for (DataSnapshot mysanapshot: dataSnapshot.getChildren()){
                    Data data=mysanapshot.getValue(Data.class);
                    expensnSum+=data.getAmount();
                    String strExpensesum=String.valueOf(expensnSum);
                    expenseSumResult.setText(strExpensesum+".00");
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
                        R.layout.expense_recycler_data,
                        MyViewHolder.class,
                        mExpenseDatabase
                ){
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, Data model, int position){
                viewHolder.setType(model.getType());
                viewHolder.setNote(model.getNote());
                viewHolder.setDate(model.getDate());
                viewHolder.setAmmount(model.getAmount());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        post_key=getRef(position).getKey();
                        type=model.getType();
                        note=model.getNote();
                        ammount=model.getAmount();
                        updateDataItem();
                    }
                });
            }


        };

        recyclerView.setAdapter(adapter);
    }
    //A ViewHolder describes an item view and metadata about its place within the RecyclerView.
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }
        private void setType(String type){
            TextView mType=mView.findViewById(R.id.type_txt_expense);
            mType.setText(type);
        }
        private void setNote(String note){
            TextView mNote=mView.findViewById(R.id.note_txt_expense);
            mNote.setText(note);
        }
        private void setDate(String date){
            TextView mDate=mView.findViewById(R.id.date_txt_expense);
            mDate.setText(date);
        }
        private void setAmmount(int ammount){
            TextView mAmmount=mView.findViewById(R.id.ammount_txt_expense);
            String strammount=String.valueOf(ammount);
            mAmmount.setText(strammount);
        }
    }
    //to update the data item
    private void updateDataItem(){
        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View myview=inflater.inflate(R.layout.update_data_item,null);
        mydialog.setView(myview);

        edtAmmount=myview.findViewById(R.id.amount_edt);
        edtType=myview.findViewById(R.id.type_edt);
        edtNote=myview.findViewById(R.id.note_edt);

        //Set data to edit text

        edtType.setText(type);
        edtType.setSelection(type.length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        edtAmmount.setText(String.valueOf(ammount));
        edtAmmount.setSelection(String.valueOf(ammount).length());


//calling button unique id
        btnUpdate=myview.findViewById(R.id.btn_upd_Update);
        btnDelete=myview.findViewById(R.id.btnuPD_Delete);

        AlertDialog dialog= mydialog.create();
//function for update button
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type=edtType.getText().toString().trim();
                note=edtNote.getText().toString().trim();

                String stammount=String.valueOf(ammount);

                stammount=edtAmmount.getText().toString().trim();

                int intamount=Integer.parseInt(stammount);

                String mDate= DateFormat.getDateInstance().format(new Date());
                Data data=new Data(intamount,type,note,post_key,mDate);

                mExpenseDatabase.child(post_key).setValue(data);

                dialog.dismiss();

            }
        });
        //function for delete button
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mExpenseDatabase.child(post_key).removeValue();
                dialog.dismiss();

            }
        });
        dialog.show();


    }
}