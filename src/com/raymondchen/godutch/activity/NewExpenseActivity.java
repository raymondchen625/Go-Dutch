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
	private Button runReportButton;
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
		trip=DataService.getTripById(getApplicationContext(), tripId);
		userList=trip.getMembers();
		setContentView(R.layout.new_expense);
		expenseSubmitButton=(Button)findViewById(R.id.expenseSubmitButton);
		expenseNameEditText=(EditText)findViewById(R.id.expenseNameEditText);
		listExpenseButton=(Button)findViewById(R.id.listExpenseButton);
		expenseAmountEditText=(EditText)findViewById(R.id.expenseAmountEditText);
		paidUserRadioGroup=(RadioGroup)findViewById(R.id.paidUserRadioGroup);
		runReportButton=(Button)findViewById(R.id.runReportButton);
		
		refreshExpenseList();
		for (User user : userList) {
			RadioButton radioButton=new RadioButton(getApplicationContext());
			radioButton.setText(user.getName());
			paidUserRadioGroup.addView(radioButton);
		}
		expenseSubmitButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String validateResult=validateInput();
				if (validateResult.equals("")) {
					System.out.println("checkedRadioButtonId="+paidUserRadioGroup.getCheckedRadioButtonId());
					Expense expense=new Expense();
					expense.setName(expenseName);
					expense.setAmount(expenseAmount);
					expense.setSharedUserIds(trip.getMemberIds());
					expense.setTripId(trip.getTripId());
					expense.setPaidUserId(getCheckedPaidUser().getUserId());
					DataService.addExpense(getApplicationContext(), expense);
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.addExpenseSucceeded), Toast.LENGTH_SHORT).show();
					expenseNameEditText.setText("");
					expenseAmountEditText.setText("");
					refreshExpenseList();
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
		runReportButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				runReport();
			}
			
		});
	}
	
	
	@Override
	public void onCreateContextMenu(ContextMenu contextMenu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(contextMenu, v, menuInfo);
		System.out.println();
		int i=0;
		for (Expense expense : expenseList) {
			User paidUser=DataService.getUserById(getApplicationContext(), expense.getPaidUserId());
			contextMenu.add(0,i++,0,expense.getName() + " ("+expense.getAmount()+") - paid by "+ paidUser.getName());
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
		if (paidUserRadioGroup.getCheckedRadioButtonId()<0) {
			result+=" " +getResources().getString(R.string.specifyPaidUserPlease);
		}
		return result;
	}
	
	private void refreshExpenseList() {
		expenseList=DataService.getExpenseListByTripId(getApplicationContext(), this.trip.getTripId());
		registerForContextMenu(listExpenseButton);
	}
	
	private User getCheckedPaidUser() {
		for (int i=0;i<paidUserRadioGroup.getChildCount();i++) {
			RadioButton rb=(RadioButton)paidUserRadioGroup.getChildAt(i);
			if (rb.isChecked()) {
				return userList.get(i);
			}
		}
		return null;
	}
	
	private void runReport() {
		int headCount=userList.size();
		double[] alreadyPaidAmount=new double[headCount];
		String[] result=new String[headCount];
		double totalAmount=0;
		for (Expense expense : expenseList) {
			totalAmount+=expense.getAmount();
			alreadyPaidAmount[getUserListPositionByUserId(expense.getPaidUserId())]+=expense.getAmount();
		}
		if (totalAmount==0d) {
			Toast.makeText(getApplicationContext(), "Nothing paid", Toast.LENGTH_SHORT).show();
			return ;
		}
		double average=Math.round((totalAmount/headCount)*100)/100;
		System.out.println("average="+average);
		for (int i=0;i<headCount;i++) {
			String paidStr=userList.get(i).getName() + " : ";
			if (i<headCount-1) {
				paidStr+=alreadyPaidAmount[i]-average;
			} else {
				paidStr+=alreadyPaidAmount[i]-(totalAmount-average*(headCount-1));
			}
			result[i]=paidStr;
		}
		String finalReportString="";
		for (String s : result) {
			finalReportString+=s;
			finalReportString+="\n";
		}
		Toast.makeText(getApplicationContext(), finalReportString, Toast.LENGTH_LONG).show();
	}
	
	private int getUserListPositionByUserId(long userId) {
		for (int i=0;i<userList.size();i++) {
			if (userList.get(i).getUserId()==userId) {
				return i;
			}
		}
		throw new IllegalArgumentException("Invalid value of userId="+userId);
	}

}
