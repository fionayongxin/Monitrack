package com.example.monitrackapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.moni.model.Data;
import com.example.monitrackapp.model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;


public class DashboardFragment extends Fragment {

    //Button that floating
    private FloatingActionButton fabMainBtn;
    private FloatingActionButton fabIncomeBtn;
    private FloatingActionButton fabExpenseBtn;

    //textview of button that is floating
    private TextView fabIncomeTxt;
    private TextView fabExpenseTxt;

    //boolean variable
    private boolean isOpen = false;

    //animation variable
    private Animation FadeOpen, FadeClose;

    //Variable for income and expense result in Dashboard
    private TextView totalIncome;
    private TextView totalExpense;

    //Firebase variable
    private FirebaseAuth userAuth;
    private DatabaseReference userIncomeDatabase;
    private DatabaseReference userExpenseDatabase;

    //Recycler view variable
    private RecyclerView userRecyclerIncome;
    private RecyclerView userRecyclerExpense;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // layout inflation for this fragment
        View myview = inflater.inflate(R.layout.fragment_dashboard, container, false);

        userAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = userAuth.getCurrentUser();
        String uid = mUser.getUid();

        //assign value
        userIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        userExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);

        //function to keep data sync
        userIncomeDatabase.keepSynced(true);
        userExpenseDatabase.keepSynced(true);

        //connection between floating button and layout
        fabMainBtn = myview.findViewById(R.id.fb_main_plus_btn);
        fabIncomeBtn = myview.findViewById(R.id.income_ft_btn);
        fabExpenseBtn = myview.findViewById(R.id.expense_ft_btn);

        //floating text connection
        fabIncomeTxt = myview.findViewById(R.id.income_ft_text);
        fabExpenseTxt = myview.findViewById(R.id.expense_ft_text);

        //to set the total income and expense
        totalIncome = myview.findViewById(R.id.income_set_result);
        totalExpense = myview.findViewById(R.id.expense_set_result);

        //Recycler to set value
        userRecyclerIncome = myview.findViewById(R.id.recycler_income);
        userRecyclerExpense = myview.findViewById(R.id.recycler_expense);

        //animation connection
        FadeOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_open);
        FadeClose = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_close);

        //interactive functioning button
        fabMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addData();

                if (isOpen) {
                    fabIncomeBtn.startAnimation(FadeClose);
                    fabExpenseBtn.startAnimation(FadeClose);
                    fabIncomeBtn.setClickable(false);
                    fabExpenseBtn.setClickable(false);

                    fabIncomeTxt.startAnimation(FadeClose);
                    fabExpenseTxt.startAnimation(FadeClose);
                    fabIncomeTxt.setClickable(false);
                    fabExpenseTxt.setClickable(false);
                    isOpen = false;
                } else {
                    fabIncomeBtn.startAnimation(FadeOpen);
                    fabExpenseBtn.startAnimation(FadeOpen);
                    fabIncomeBtn.setClickable(true);
                    fabExpenseBtn.setClickable(true);

                    fabIncomeTxt.startAnimation(FadeOpen);
                    fabExpenseTxt.startAnimation(FadeOpen);
                    fabIncomeTxt.setClickable(true);
                    fabExpenseTxt.setClickable(true);
                    isOpen = true;
                }
            }
        });

        //total income calculation
        userIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int total = 0;
                for (DataSnapshot mysnap : dataSnapshot.getChildren()) {
                    Data data = mysnap.getValue(Data.class);
                    total += data.getAmount();

                    String stResult = String.valueOf(total);
                    totalIncome.setText(stResult + ".00");
                }
            }

            //overriding method
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        //total expense calculation
        userExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int total = 0;

                for (DataSnapshot mysnapshot : dataSnapshot.getChildren()) {

                    Data data = mysnapshot.getValue(Data.class);
                    total += data.getAmount();

                    String strTotalSum = String.valueOf(total);

                    totalExpense.setText(strTotalSum + ".00");
                }
            }
            //overriding method
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //manage layout
        LinearLayoutManager layoutManagerIncome = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        userRecyclerIncome.setHasFixedSize(true);
        userRecyclerIncome.setLayoutManager(layoutManagerIncome);

        LinearLayoutManager layoutManagerExpense = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        layoutManagerExpense.setReverseLayout(true);
        layoutManagerExpense.setStackFromEnd(true);
        userRecyclerExpense.setHasFixedSize(true);
        userRecyclerExpense.setLayoutManager(layoutManagerExpense);

        return myview;
    }

    //animation floating button
    private void ftAnimation() {
        if (isOpen) {                       //to work for different condition
            fabIncomeBtn.startAnimation(FadeClose);
            fabExpenseBtn.startAnimation(FadeClose);
            fabIncomeBtn.setClickable(false);
            fabExpenseBtn.setClickable(false);

            fabIncomeTxt.startAnimation(FadeClose);
            fabExpenseTxt.startAnimation(FadeClose);
            fabIncomeTxt.setClickable(false);
            fabExpenseTxt.setClickable(false);
            isOpen = false;
        } else {
            fabIncomeBtn.startAnimation(FadeOpen);
            fabExpenseBtn.startAnimation(FadeOpen);
            fabIncomeBtn.setClickable(true);
            fabExpenseBtn.setClickable(true);

            fabIncomeTxt.startAnimation(FadeOpen);
            fabExpenseTxt.startAnimation(FadeOpen);
            fabIncomeTxt.setClickable(true);
            fabExpenseTxt.setClickable(true);
            isOpen = true;

        }
    }

    //add data function
    private void addData() {
        //income button
        fabIncomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incomeDataInsert();
            }
        });

        //expense button
        fabExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expenseDataInsert();
            }
        });
    }

    //insert income data
    public void incomeDataInsert() {

        //display dialog about details
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.custom_layout_for_insertdata, null);
        mydialog.setView(myview);
        final AlertDialog dialog = mydialog.create();

        dialog.setCancelable(false);

        final EditText AmountEdit = myview.findViewById(R.id.amount_edt);
        final EditText TypeEdit = myview.findViewById(R.id.type_edt);
        final EditText NoteEdit = myview.findViewById(R.id.note_edt);

        Button btnSave = myview.findViewById(R.id.btnSave);
        Button btnCancel = myview.findViewById(R.id.btnCancel);

        //interactive functioning button
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String type = TypeEdit.getText().toString().trim();
                String amount = AmountEdit.getText().toString().trim();
                String note = NoteEdit.getText().toString().trim();

                if (TextUtils.isEmpty(type)) {
                    TypeEdit.setError("Required type field");
                    return;
                }
                if (TextUtils.isEmpty(amount)) {
                    AmountEdit.setError("Required amount field");
                    return;
                }
                int ouramountint = Integer.parseInt(amount);

                if (TextUtils.isEmpty(note)) {
                    NoteEdit.setError("Required note field");
                    return;
                }

                String id = userIncomeDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(ouramountint, type, note, id, mDate);
                userIncomeDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(), "Data Added", Toast.LENGTH_SHORT).show();

                ftAnimation();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //insert expense data
    public void expenseDataInsert() {

        //display dialog about details
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.custom_layout_for_insertdata, null);
        mydialog.setView(myview);

        final AlertDialog dialog = mydialog.create();

        dialog.setCancelable(false);

        final EditText amount = myview.findViewById(R.id.amount_edt);
        final EditText type = myview.findViewById(R.id.type_edt);
        final EditText note = myview.findViewById(R.id.note_edt);

        Button btnSave = myview.findViewById(R.id.btnSave);
        Button btnCancel = myview.findViewById(R.id.btnCancel);

        //interactive functioning button
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tmAmount = amount.getText().toString().trim();
                String tmType = type.getText().toString().trim();
                String tmNote = note.getText().toString().trim();

                if (TextUtils.isEmpty(tmAmount)) {
                    amount.setError("Required expamount field");
                    return;
                }

                if (TextUtils.isEmpty(tmType)) {
                    type.setError("Required exptype field");
                    return;
                }

                int inamount = Integer.parseInt(tmAmount);

                if (TextUtils.isEmpty(tmNote)) {
                    note.setError("Required expnote field");
                    return;
                }

                String id = userExpenseDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(inamount, tmType, tmNote, id, mDate);
                userExpenseDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(), "Data Added", Toast.LENGTH_SHORT).show();
                ftAnimation();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();

        //binds a Query to a RecyclerView
        FirebaseRecyclerAdapter<Data, IncomeViewHolder> incomeAdapter = new FirebaseRecyclerAdapter<Data, IncomeViewHolder>
                (
                        Data.class,
                        R.layout.dashboard_income,
                        DashboardFragment.IncomeViewHolder.class,
                        userIncomeDatabase
                ) {
            @Override
            protected void populateViewHolder(IncomeViewHolder viewHolder, Data model, int position) {

                viewHolder.setIncomeType(model.getType());
                viewHolder.setIncomeAmmount(model.getAmount());
                viewHolder.setIncomeDate(model.getDate());

            }
        };
        userRecyclerIncome.setAdapter(incomeAdapter);


        FirebaseRecyclerAdapter<Data, ExpenseViewHolder> expenseAdapter = new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>
                (
                        Data.class,
                        R.layout.dashboard_expense,
                        DashboardFragment.ExpenseViewHolder.class,
                        userExpenseDatabase
                ) {
            @Override
            protected void populateViewHolder(ExpenseViewHolder viewHolder, Data model, int position) {

                viewHolder.setExpenseType(model.getType());
                viewHolder.setExpenseAmmount(model.getAmount());
                viewHolder.setExpenseDate(model.getDate());
            }
        };
        userRecyclerExpense.setAdapter(expenseAdapter);
    }

    ////describes income item view and metadata about its place within the RecyclerView
    public static class IncomeViewHolder extends RecyclerView.ViewHolder {

        View userIncomeView;

        public IncomeViewHolder(View itemView) {
            super(itemView);
            userIncomeView = itemView;
        }

        public void setIncomeType(String type) {

            TextView mtype = userIncomeView.findViewById(R.id.type_Income_ds);
            mtype.setText(type);

        }

        public void setIncomeAmmount(int amount) {

            TextView mAmmount = userIncomeView.findViewById(R.id.ammount_income_ds);
            String strAmmount = String.valueOf(amount);
            mAmmount.setText(strAmmount);
        }

        public void setIncomeDate(String date) {

            TextView mDate = userIncomeView.findViewById(R.id.date_income_ds);
            mDate.setText(date);
        }
    }

    //describes expense item view and metadata about its place within the RecyclerView
    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {

        View userExpenseView;

        public ExpenseViewHolder(View itemView) {
            super(itemView);
            userExpenseView = itemView;
        }

        public void setExpenseType(String type) {
            TextView mtype = userExpenseView.findViewById(R.id.type_expense_ds);
            mtype.setText(type);
        }

        public void setExpenseAmmount(int amount) {
            TextView mAmmount = userExpenseView.findViewById(R.id.ammount_expense_ds);
            String strAmmount = String.valueOf(amount);
            mAmmount.setText(strAmmount);
        }

        public void setExpenseDate(String date) {
            TextView mDate = userExpenseView.findViewById(R.id.date_expense_ds);
            mDate.setText(date);
        }
    }
}