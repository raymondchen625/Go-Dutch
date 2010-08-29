package com.raymondchen.godutch.activity;

import java.util.List;

import com.raymondchen.godutch.DataService;
import com.raymondchen.godutch.Expense;
import com.raymondchen.godutch.R;
import com.raymondchen.godutch.Trip;
import com.raymondchen.godutch.User;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

public class NewExpenseActivity extends Activity {

	private Button expenseSubmitButton;
	private Button listExpenseButton;
	private Button runReportButton;
	private EditText expenseNameEditText;
	private EditText expenseAmountEditText;
	private Trip trip;
	private List<User> userList;
	private String expenseName;
	private double expenseAmount;
	private long paidUserId=-1;
	private String sharedUserIdList;
	private Button paidByUserButton;
	private TextView paidByUserTextView;
	private Button sharedUsersButton;
	private TextView sharedUserTextView;
	List<Expense> expenseList;
	private static final int REQUEST_CODE_SELECT_PAID_USER=0;
	private static final int REQUEST_CODE_SELECT_SHARED_USERS=1;
	private SharedPreferences sharedPreferences;
	private static final String LAST_PAID_USER_ID="lastPaidUserId";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		long tripId=getIntent().getExtras().getLong("tripId");
		trip=DataService.getTripById(getApplicationContext(), tripId);
		sharedPreferences=getSharedPreferences(getPackageName(), MODE_PRIVATE);
		userList=trip.getMembers();
		sharedUserIdList=trip.getMemberIds();
		setContentView(R.layout.new_expense);
		initializeLayoutElements();
		refreshExpenseList();
		// try to get saved last paid UserID
		long savedLastPaidUserId=sharedPreferences.getLong(LAST_PAID_USER_ID, -1L);
		if (savedLastPaidUserId!=-1L) {
			for (User user : userList) {
				if (user.getUserId()==savedLastPaidUserId) {
					paidUserId=savedLastPaidUserId;
					refreshPaidUserDisplay();
				}
			}
		}
		expenseSubmitButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (validateInput()) {
					Expense expense=new Expense();
					expense.setName(expenseName);
					expense.setAmount(expenseAmount);
					expense.setSharedUserIds(sharedUserIdList);
					expense.setTripId(trip.getTripId());
					expense.setPaidUserId(paidUserId);
					DataService.addExpense(getApplicationContext(), expense);
					// save last paid userId
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putLong(LAST_PAID_USER_ID, paidUserId);
					editor.commit();
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
				  Intent intent=new Intent(getApplicationContext(),ExpenseDetailActivity.class);
				  intent.putExtra("tripId", trip.getTripId());
				  startActivity(intent);
				}
			}
		});
		runReportButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				runReport();
			}
		});

		paidByUserButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent=new Intent(getApplicationContext(),SelectPaidUserActivity.class);
				intent.putExtra("tripId", trip.getTripId());
				startActivityForResult(intent, REQUEST_CODE_SELECT_PAID_USER);
			}
		});
		sharedUsersButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent=new Intent(getApplicationContext(),SelectSharedUsersActivity.class);
				intent.putExtra("tripId",trip.getTripId());
				intent.putExtra("sharedUserIdList", sharedUserIdList);
				startActivityForResult(intent, REQUEST_CODE_SELECT_SHARED_USERS);
			}
		});
		
		
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==REQUEST_CODE_SELECT_PAID_USER) {
			if (resultCode==Activity.RESULT_OK) {
				Bundle bundle=data.getBundleExtra(getPackageName());
				paidUserId=bundle.getLong("userId", -1l);
				if (paidUserId!=-1) {
					refreshPaidUserDisplay();
				}
			}
		} else if (requestCode==REQUEST_CODE_SELECT_SHARED_USERS) {
			if (resultCode==Activity.RESULT_OK) {
			Bundle bundle=data.getBundleExtra(getPackageName());
			sharedUserIdList=bundle.getString("userIdList");
				refreshSharedUsersDisplay();
			}
		}
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
		if (paidUserId<=0) {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.specifyPaidUserPlease), Toast.LENGTH_SHORT).show();
			return false;
		}
		// 不是所有人分摊的时候检查应该至少有人参与
		if (sharedUserIdList.equals("")) {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.specifySharedUsersPlease), Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	private void refreshExpenseList() {
		expenseList=DataService.getExpenseListByTripId(getApplicationContext(), this.trip.getTripId());
		registerForContextMenu(listExpenseButton);
	}

	
	private void runReport() {
		int headCount=userList.size();
		// 账单数组，每个数字代表用户应收（正数）或者应付（负数）金额
		double[] alreadyPaidAmount=new double[headCount];
		String[] result=new String[headCount];
		double totalAmount=0;
		for (Expense expense : expenseList) {
			double subTotalAmount=expense.getAmount();
			totalAmount+=subTotalAmount;
			String[] sharedUserIdArray=expense.getSharedUserIds().split(",");
			int sharedHeadCount=sharedUserIdArray.length;
			double average=Math.round((subTotalAmount/sharedHeadCount)*100)/100;
			alreadyPaidAmount[getUserListPositionByUserId(expense.getPaidUserId())]+=expense.getAmount();
			for (int i=0;i<sharedHeadCount;i++) {
				int paidUserPosition=getUserListPositionByUserId(new Long(sharedUserIdArray[i]));
				if (i<sharedHeadCount-1) {
					alreadyPaidAmount[paidUserPosition]-=average;
				} else {
					alreadyPaidAmount[paidUserPosition]-=(subTotalAmount-(sharedHeadCount-1)*average);
				}
			}
		}
		if (totalAmount==0d) {
			Toast.makeText(getApplicationContext(),  getResources().getString(R.string.noExpenseToDisplay), Toast.LENGTH_SHORT).show();
			return ;
		}

		for (int i=0;i<headCount;i++) {
			String paidStr=userList.get(i).getName() + " : " + alreadyPaidAmount[i];
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
	
	private void initializeLayoutElements() {
		expenseSubmitButton=(Button)findViewById(R.id.expenseSubmitButton);
		expenseNameEditText=(EditText)findViewById(R.id.expenseNameEditText);
		listExpenseButton=(Button)findViewById(R.id.listExpenseButton);
		expenseAmountEditText=(EditText)findViewById(R.id.expenseAmountEditText);
		runReportButton=(Button)findViewById(R.id.runReportButton);
		paidByUserButton=(Button)findViewById(R.id.paidUserButton);
		paidByUserTextView=(TextView)findViewById(R.id.paidByUserTextView);
		sharedUsersButton=(Button)findViewById(R.id.sharedUsersButton);
		sharedUserTextView=(TextView)findViewById(R.id.sharedUserTextView);
	}
	


	private void refreshPaidUserDisplay() {
		for (User user : userList) {
			if (paidUserId==user.getUserId()) {
				paidByUserTextView.setText(user.getName());
				return ;
			}
		}
		paidByUserTextView.setText("");
	}
	
	private void refreshSharedUsersDisplay() {
		String[] sharedIdStrList=this.sharedUserIdList.split(",");
		if (sharedIdStrList.length==userList.size()) {
			sharedUserTextView.setText(getResources().getString(R.string.everyone));
		} else {
			String result="";
			for (int i=0;i<sharedIdStrList.length;i++) {
				if (result.equals("")) {
					result=getUserNameById(new Long(sharedIdStrList[i]));
				} else {
					result+=","+getUserNameById(new Long(sharedIdStrList[i]));
				}
			}
			sharedUserTextView.setText(result);
		}
	}
	
	private String getUserNameById(long userId) {
		for (User user : userList) {
			if (user.getUserId()==userId) {
				return user.getName();
			}
		}
		return "";
	}
}
