package com.raymondchen.godutch.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
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
	List<Map<String,Object>> listContent;

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
				Cursor avatarCursor=resolver.query(ContactsContract.Data.CONTENT_URI, null, ContactsContract.CommonDataKinds.Photo.CONTACT_ID+"=?", new String[]{Long.toString(id)}, null);
				System.out.println("avatarCursor=null?"+(avatarCursor==null));
				byte[] avatar=null;
				if (avatarCursor.moveToFirst()) {
					do {
					avatar=avatarCursor.getBlob(avatarCursor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO));
					System.out.println("record found: "+avatarCursor.getCount());
					if (avatar!=null) {
						break ;
					}
					} while (avatarCursor.moveToNext());
				}
				avatarCursor.close();
				System.out.println("avatar="+avatar);
				if (avatar!=null) {
				Bitmap avatarBitmap=BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
				System.out.println("avatarBitmap created! height="+avatarBitmap.getHeight()+", width="+avatarBitmap.getWidth());
				}
				String email="";
				Cursor mailCursor=resolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID+"=?", new String[]{Long.toString(id)}, null);
				if (mailCursor.moveToFirst()) {
					// only get the first email address
						email=mailCursor.getString(mailCursor.getColumnIndexOrThrow(Email.DATA));
				}
				mailCursor.close();
				User user=new User();
				user.setName(name);
				user.setEmail(email);
				user.setAvatar(avatar);
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
		listContent=new ArrayList<Map<String,Object>>();
		for (int i = 0; i < userList.size(); i++) {
			String email = "";
			Bitmap avatarBitmap=null;
			User user=userList.get(i);
			if (user.getEmail() != null
					&& !user.getEmail().trim().equals("")) {
				email = " (" + user.getEmail() + ")";
			}
			if (user.getAvatar()!=null) {
				avatarBitmap=BitmapFactory.decodeByteArray(user.getAvatar(), 0, user.getAvatar().length);
			}
			screenElementList.add(userList.get(i).getName() + email);
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("name", user.getName());
			map.put("email",email);
			map.put("avatar", avatarBitmap);
			listContent.add(map);
		}
//		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,R.layout.main, screenElementList);
//		listView.setAdapter(listAdapter);
		SimpleAdapter adapter=new SimpleAdapter(getApplicationContext(), listContent, R.layout.list_user_item,new String[]{"name","email","avatar"}, new int[]{R.id.nameListTextView,R.id.emailListTextView,R.id.avatarListImageView});
		adapter.setViewBinder(new ViewBinder(){

			public boolean setViewValue(View view, Object obj, String textRepresentation) {
				if (view instanceof ImageView) {
					ImageView image=(ImageView)view;
					if (obj!=null) {
					
					image.setImageBitmap((Bitmap)obj);
					} else {
						image.setImageResource(R.drawable.default_avatar);
					}
					return true;
				}
				return false;
			}
			
		});
		listView.setAdapter(adapter);
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
