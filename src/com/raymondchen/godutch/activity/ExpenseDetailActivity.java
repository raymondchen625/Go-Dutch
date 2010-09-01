package com.raymondchen.godutch.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.raymondchen.godutch.DataService;
import com.raymondchen.godutch.Expense;
import com.raymondchen.godutch.R;
import com.raymondchen.godutch.Trip;
import com.raymondchen.godutch.User;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ExpenseDetailActivity extends Activity {

	private ListView expenseListView;
	private TextView tripNameTextView;
	private TextView tripMembersTextView;
	private Trip trip;
	List<Expense> expenseList;
	List<Map<String,Object>> listContent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.expense_detail);
		initializeLayoutElements();
		long tripId=getIntent().getExtras().getLong("tripId");
		trip=DataService.getTripById(getApplicationContext(), tripId);
		tripNameTextView.setText(trip.getName());
		tripMembersTextView.setText(getTripMemberNameList());
		refreshExpenseList();
	}
	
	private void initializeLayoutElements() {
		expenseListView=(ListView)findViewById(R.id.expenseListListView);
		tripNameTextView=(TextView)findViewById(R.id.tripNameTextView);
		tripMembersTextView=(TextView)findViewById(R.id.tripMembersTextView);
	}
	
	private String getTripMemberNameList() {
		List<User> memberList=trip.getMembers();
		String nameList="";
		for (int i=0;i<memberList.size();i++) {
			if (nameList.equals("")) {
				nameList=memberList.get(i).getName();
			} else {
				nameList+=","+memberList.get(i).getName();
			}
		}
		return nameList;
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		int groupId = 0;
		int returnItemId = 0;
		MenuItem backItem = menu.add(groupId, returnItemId, Menu.NONE,
				R.string.back);
		backItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem _menuItem) {
				finish();
				return true;
			}
		});
		return true;
	}
	
	private String getSharedUserNameList(String[] userIdList) {
		if (userIdList==null || userIdList.length==trip.getMembers().size()) {
			return getResources().getString(R.string.everyone);
		}
		String result="";
		for (String userIdStr : userIdList) {
			for (User user : trip.getMembers()) {
				if (userIdStr.equals(user.getUserId()+"")) {
					if (result.equals("")) {
						result=user.getName();
					} else {
						result+=","+user.getName();
					}
					break;
				}
			}
		}
		return result;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu contextMenu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(contextMenu, v, menuInfo);
		MenuItem menuItem=contextMenu.add(getResources().getString(R.string.delete));
		final ExpenseDetailActivity parentObj=this;
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
	
	private void refreshExpenseList() {
		expenseList=DataService.getExpenseListByTripId(getApplicationContext(), this.trip.getTripId());
		listContent=new ArrayList<Map<String, Object>>();
		for (Expense expense : expenseList) {
			Map<String,Object> expenseItemMap=new HashMap<String,Object>(); 
			expenseItemMap.put("name", expense.getName());
			expenseItemMap.put("amount", expense.getAmount());
			expenseItemMap.put("paidUserName", DataService.getUserById(getApplicationContext(),expense.getPaidUserId()).getName());
			expenseItemMap.put("sharedUserNames",getSharedUserNameList(expense.getSharedUserIds().split(",")));
			listContent.add(expenseItemMap);
		}
		SimpleAdapter adapter=new SimpleAdapter(getApplicationContext(), listContent, R.layout.expense_list_item,new String[]{"name","amount","paidUserName","sharedUserNames"}, new int[]{R.id.expenseNameTextView,R.id.expenseAmountTextView,R.id.paidUserNameTextView,R.id.sharedUserNamesTextView});
//		adapter.setViewBinder(new ViewBinder() {
//			public boolean setViewValue(View view, Object data,
//					String textRepresentation) {
//				if (view instanceof TextView) {
//					TextView textView=(TextView)view;
//					if (textView.getId()==R.id.expenseNameTextView) {
//						registerForContextMenu(textView);
//					}
//				}
//				return false;
//			}
//		});
		expenseListView.setAdapter(adapter);
		expenseListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				registerForContextMenu(view);
				return false;
			}
		});
	}
}
