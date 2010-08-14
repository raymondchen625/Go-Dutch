package com.raymondchen.godutch.activity;

import java.util.ArrayList;
import java.util.List;

import com.raymondchen.godutch.DataService;
import com.raymondchen.godutch.Expense;
import com.raymondchen.godutch.R;
import com.raymondchen.godutch.Trip;
import com.raymondchen.godutch.User;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class NewExpenseActivity extends Activity {

	private Button expenseSubmitButton;
	private Button listExpenseButton;
	private EditText expenseNameEditText;
	private EditText expenseAmountEditText;
	private RadioGroup paidUserRadioGroup;
	private Trip trip;
	private List<User> userList;
	private String expenseName;
	private double expenseAmount;
	List<Expense> expenseList;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		long tripId=getIntent().getExtras().getLong("tripId");
		System.out.println("tripId="+tripId);
		trip=DataService.getTripById(getApplicationContext(), tripId);
		System.out.println("trip="+trip);
		expenseList=DataService.getExpenseListByTripId(getApplicationContext(), tripId);
		userList=trip.getMembers();
		setContentView(R.layout.new_expense);
		expenseSubmitButton=(Button)findViewById(R.id.expenseSubmitButton);
		expenseNameEditText=(EditText)findViewById(R.id.expenseNameEditText);
		listExpenseButton=(Button)findViewById(R.id.listExpenseButton);
		expenseAmountEditText=(EditText)findViewById(R.id.expenseAmountEditText);
		paidUserRadioGroup=(RadioGroup)findViewById(R.id.paidUserRadioGroup);
		for (User user : userList) {
			RadioButton radioButton=new RadioButton(getApplicationContext());
			radioButton.setText(user.getName());
			paidUserRadioGroup.addView(radioButton);
		}
		expenseSubmitButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String validateResult=validateInput();
				if (validateResult.equals("")) {
					Expense expense=new Expense();
					expense.setName(expenseName);
					expense.setAmount(expenseAmount);
					expense.setSharedUserIds(trip.getMemberIds());
					expense.setTripId(trip.getTripId());
					DataService.addExpense(getApplicationContext(), expense);
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.addExpenseSucceeded), Toast.LENGTH_SHORT).show();
					expenseNameEditText.setText("");
					expenseAmountEditText.setText("");
				} else {
					Toast.makeText(getApplicationContext(), validateResult, Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		

		listExpenseButton.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				listExpenseButton.performLongClick();
				
			}
			
		});
		
		registerForContextMenu(listExpenseButton);
		
	}
	
	
	@Override
	public void onCreateContextMenu(ContextMenu contextMenu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(contextMenu, v, menuInfo);
		System.out.println();
		int i=0;
		for (Expense expense : expenseList) {
			contextMenu.add(0,i++,0,expense.getName() + " ("+expense.getAmount()+")");
		}
	}


	private String validateInput() {
		String result="";
		if (expenseNameEditText.getText().toString().trim().equals("")) {
			result +=getResources().getString(R.string.specifyExpenseNamePlease);
		} else {
			this.expenseName=expenseNameEditText.getText().toString().trim();
		}
		try {
			this.expenseAmount=Double.parseDouble(expenseAmountEditText.getText().toString().trim());
			if (this.expenseAmount==0d) {
				result += getResources().getString(R.string.zeroAmountIllegal);
			}
		} catch (NumberFormatException e) {
			result +=" "+getResources().getString(R.string.specifyExpenseAmount);
		}
		return result;
	}

}
