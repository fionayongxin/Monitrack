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

    private FirebaseAuth userAuth;
    private DatabaseReference userExpenseDatabase;

    //to declare variable recyclerView
    private RecyclerView recView;

    //to declare textView variable
    private TextView expenseSum;

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

    public ExpenseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myview = inflater.inflate(R.layout.fragment_expense, container, false);

        userAuth= FirebaseAuth.getInstance();

        FirebaseUser mUser=userAuth.getCurrentUser();
        String uid =mUser.getUid();
//Create subdirectory at Firebase
        userExpenseDatabase= FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);
        expenseSum=myview.findViewById(R.id.expense_txt_result);

        recView=myview.findViewById(R.id.recycler_id_expense);

        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recView.setHasFixedSize(true);
        recView.setLayoutManager(layoutManager);
//add value to the Firebase
        userExpenseDatabase.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                int expense2Sum =0;

                for (DataSnapshot mysanapshot: dataSnapshot.getChildren()){
                    Data data=mysanapshot.getValue(Data.class);
                    expense2Sum+=data.getAmount();
                    String stringExpensesum=String.valueOf(expense2Sum);
                    expenseSum.setText(stringExpensesum+".00");
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
                        userExpenseDatabase
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
            TextView mType=userView.findViewById(R.id.type_txt_expense);
            mType.setText(type);
        }
        private void setNote(String note){
            TextView mNote=userView.findViewById(R.id.note_txt_expense);
            mNote.setText(note);
        }
        private void setDate(String date){
            TextView mDate=userView.findViewById(R.id.date_txt_expense);
            mDate.setText(date);
        }
        private void setAmmount(int amount){
            TextView mAmmount=userView.findViewById(R.id.ammount_txt_expense);
            String strammount=String.valueOf(amount);
            mAmmount.setText(strammount);
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


//calling button unique id
        UpdateBttn=myview.findViewById(R.id.btn_upd_Update);
        DeleteBttn=myview.findViewById(R.id.btnuPD_Delete);

        AlertDialog dialog= mydialog.create();
//function for update button
        UpdateBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type=TypeEdit.getText().toString().trim();
                note=NoteEdit.getText().toString().trim();

                String stammount=String.valueOf(amount);

                stammount=AmountEdit.getText().toString().trim();

                int intamount=Integer.parseInt(stammount);

                String mDate= DateFormat.getDateInstance().format(new Date());
                Data data=new Data(intamount,type,note,keyPosition,mDate);

                userExpenseDatabase.child(keyPosition).setValue(data);

                dialog.dismiss();

            }
        });
        //function for delete button
        DeleteBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userExpenseDatabase.child(keyPosition).removeValue();
                dialog.dismiss();

            }
        });
        dialog.show();


    }
}