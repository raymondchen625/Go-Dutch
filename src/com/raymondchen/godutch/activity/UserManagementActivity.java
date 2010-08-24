package com.raymondchen.godutch.activity;

import java.util.ArrayList;
import java.util.List;

import com.raymondchen.godutch.DataService;
import com.raymondchen.godutch.R;
import com.raymondchen.godutch.User;
import com.raymondchen.godutch.R.layout;
import com.raymondchen.godutch.R.string;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class UserManagementActivity extends Activity {
	private String TAG = "UserManagementActivity";
	private List<User> userList;
	private List<String> screenElementList;
	private ListView listView;
	private Button addUserButton;
	private Button importUsersButton;

	private static final int PICK_CONTACT = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_admin);
		listView = (ListView) findViewById(R.id.currentUsersListView);
		addUserButton = (Button) findViewById(R.id.addUserButton01);
		importUsersButton = (Button) findViewById(R.id.importFromContactsButton);
		addUserButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(),
						NewUserActivity.class);
				startActivity(intent);
			}
		});
		importUsersButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_PICK,
						ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(intent, PICK_CONTACT);
			}
		});
		initializeScreenElements();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println("resultCode=" + resultCode);
		if (resultCode == Activity.RESULT_OK) {
			System.out.println("data=" + data);
			Uri uri = data.getData();
			ContentResolver resolver = getContentResolver();
			Cursor cursor = resolver.query(uri, null, null, null, null);
			System.out.println("cursor=" + cursor);
			if (cursor.moveToFirst()) {
				int idIdx = cursor
						.getColumnIndexOrThrow(ContactsContract.Contacts._ID);
				long id = cursor.getLong(idIdx);
				String name = cursor.getString(cursor
						.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
				int mailCount=cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.HAS_PHONE_NUMBER));
				 // String email=cursor.getString(cursor.getColumnIndexOrThrow(Email.CONTENT_ITEM_TYPE));
				Cursor mailCursor=resolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID+"=?", new String[]{Long.toString(id)}, null);
				System.out.println("mailCursor="+mailCursor);
				System.out.println("mailCount="+mailCursor.getCount());
				if (mailCursor.moveToFirst()) {
					do {
//						String[] columnNames = mailCursor.getColumnNames();
//						for (String column : columnNames) {
//							Log.v(TAG, "column=" + column + ", value="+mailCursor.getString(mailCursor.getColumnIndex(column)));
//						}
						String mail=mailCursor.getString(mailCursor.getColumnIndexOrThrow(Email.DATA));
						System.out.println("email="+mail);
					} while (mailCursor.moveToNext());
				}
				System.out.println("id=" + id + ", name=" + name );
				String[] columnNames = cursor.getColumnNames();
//				for (String column : columnNames) {
//					Log.v(TAG, "column=" + column + ", value="+cursor.getString(cursor.getColumnIndex(column)));
//				}
				Bitmap bitmap=People.loadContactPhoto(getApplicationContext(), ContentUris.withAppendedId(People.CONTENT_URI, idIdx), R.drawable.icon, null);
				Log.v(TAG,"bigmap is null? "+(bitmap==null));
				User user=new User();
				user.setName(name);
				user.setEmail("");
				DataService.addUser(getApplicationContext(), user);
				Toast.makeText(getApplicationContext(), getResources().getText(R.string.addusersucceeded), Toast.LENGTH_SHORT).show();
			}
			cursor.close();
			initializeScreenElements();
		} else {
			System.out.println("canceled");
		}
	}

	private void initializeScreenElements() {
		userList = DataService.loadUserList(getApplicationContext());
		screenElementList = new ArrayList<String>();
		for (int i = 0; i < userList.size(); i++) {
			String email = "";
			if (userList.get(i).getEmail() != null
					&& !userList.get(i).getEmail().trim().equals("")) {
				email = " (" + userList.get(i).getEmail() + ")";
			}
			screenElementList.add(userList.get(i).getName() + email);
		}
		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
				R.layout.main, screenElementList);
		listView.setAdapter(listAdapter);
		final UserManagementActivity parentObj = this;
		listView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			public void onCreateContextMenu(ContextMenu contextMenu, View v,
					ContextMenuInfo info) {
				MenuItem menuItem = contextMenu.add(getResources().getString(
						R.string.delete));
				menuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item
								.getMenuInfo();
						final int position = menuInfo.position;
						AlertDialog.Builder builder = new AlertDialog.Builder(
								parentObj);
						builder.setPositiveButton(R.string.delete,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										DataService.deleteUserById(
												getApplicationContext(),
												userList.get(position)
														.getUserId());
										initializeScreenElements();
									}
								});
						builder.setCancelable(true);
						builder.setNegativeButton(R.string.cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}

								});
						AlertDialog dialog = builder.create();
						dialog.setTitle(getResources().getString(
								R.string.confirmDeleteUser));
						dialog.setMessage(getResources().getString(
								R.string.deleteUserWarning));
						dialog.show();
						return true;
					}
				});

			}

		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		initializeScreenElements();
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

}
