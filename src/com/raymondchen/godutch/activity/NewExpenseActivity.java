package com.raymondchen.godutch.activity;

import java.util.ArrayList;
import java.util.List;

import com.raymondchen.godutch.DataService;
import com.raymondchen.godutch.Expense;
import com.raymondchen.godutch.R;
import com.raymondchen.godutch.Trip;
import com.raymondchen.godutch.User;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.test.PerformanceTestCase;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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
				if (validateInput()) {
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
				} 
			}
		});
		listExpenseButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if (expenseList.size()==0) {
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.noExpenseToDisplay), Toast.LENGTH_SHORT).show();
				} else {
				listExpenseButton.performLongClick();
				}
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
		int i=0;
		for (Expense expense : expenseList) {
			User paidUser=DataService.getUserById(getApplicationContext(), expense.getPaidUserId());
			MenuItem menuItem=contextMenu.add(0,i++,0,expense.getName() + " ("+expense.getAmount()+") - "+ paidUser.getName());
			final NewExpenseActivity parentObj=this;
			menuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {
					final long expenseId=expenseList.get(item.getItemId()).getExpenseId();
					AlertDialog.Builder builder=new AlertDialog.Builder(parentObj);
					builder.setPositiveButton(R.string.delete,new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							DataService.deleteExpenseById(getApplicationContext(), expenseId);
							refreshExpenseList();
						}
					});
					builder.setCancelable(true);
					builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int which) {
						}
						
					});
					AlertDialog dialog=builder.create();
					dialog.setTitle(getResources().getString(R.string.confirmDeleteExpense));
					dialog.setMessage(getResources().getString(R.string.deleteExpenseWarning));
					dialog.show();
					return true;
				}
			});
		}

	}
	
	


	private boolean validateInput() {
		if (expenseNameEditText.getText().toString().trim().equals("")) {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.specifyExpenseNamePlease), Toast.LENGTH_SHORT).show();
			return false;
		} else {
			this.expenseName=expenseNameEditText.getText().toString().trim();
		}
		try {
			this.expenseAmount=Double.parseDouble(expenseAmountEditText.getText().toString().trim());
			if (this.expenseAmount==0d) {
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.zeroAmountIllegal), Toast.LENGTH_SHORT).show();
				return false;
			}
		} catch (NumberFormatException e) {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.specifyExpenseAmount), Toast.LENGTH_SHORT).show();
			return false;
		}
		if (paidUserRadioGroup.getCheckedRadioButtonId()<0) {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.specifyPaidUserPlease), Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
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
			Toast.makeText(getApplicationContext(),  getResources().getString(R.string.noExpenseToDisplay), Toast.LENGTH_SHORT).show();
			return ;
		}
		double average=Math.round((totalAmount/headCount)*100)/100;
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
//		Toast.makeText(getApplicationContext(), finalReportString, Toast.LENGTH_LONG).show();
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		AlertDialog dialog=builder.create();
		TextView tv=new TextView(getApplicationContext());
		tv.setText(finalReportString);
		dialog.setView(tv);
		dialog.setTitle("Expense Report");
//		dialog.setMessage(finalReportString);
		dialog.show();
	}
	
	private int getUserListPositionByUserId(long userId) {
		for (int i=0;i<userList.size();i++) {
			if (userList.get(i).getUserId()==userId) {
				return i;
			}
		}
		throw new IllegalArgumentException("Invalid value of userId="+userId);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		int groupId = 0;
		int returnItemId = 0;
		int deleteTripItemId = 1;
		MenuItem backItem = menu.add(groupId, returnItemId, Menu.NONE,
				R.string.back);
		backItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem _menuItem) {
				finish();
				return true;
			}
		});
		final NewExpenseActivity parentObj=this;
		MenuItem aboutItem = menu.add(groupId, deleteTripItemId, Menu.NONE,
				R.string.menuNameDeleteTrip);
		aboutItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem _menuItem) {
				AlertDialog.Builder builder=new AlertDialog.Builder(parentObj);
				builder.setPositiveButton(R.string.delete,new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						DataService.deleteTripById(getApplicationContext(), trip.getTripId());
						finish();
					}
				});
				builder.setCancelable(true);
				builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				AlertDialog dialog=builder.create();
				dialog.setTitle(getResources().getString(R.string.confirmDeleteTrip));
				dialog.setMessage(getResources().getString(R.string.deleteTripWarning));
				dialog.show();
				
				return true;
			}
		});
		return true;
	}


	@Override
	protected void onResume() {
		super.onResume();
		refreshExpenseList();
	}

}
