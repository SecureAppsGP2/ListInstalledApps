package com.listinstalledapps;

import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class AllAppsActivity extends AppCompatActivity {

	private PackageManager packageManager = null;
	private List<ApplicationInfo> applist = null;
	private ApplicationAdapter listadaptor = null;
	private ListView lvPackageList = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		packageManager = getPackageManager();

		new LoadApplications().execute();
		lvPackageList = (ListView) findViewById(android.R.id.list);

		lvPackageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ApplicationInfo app = applist.get(position);
				try {
					Intent intent = packageManager
							.getLaunchIntentForPackage(app.packageName);

					if (null != intent) {
						startActivity(intent);
					}
				} catch (ActivityNotFoundException e) {
					Toast.makeText(AllAppsActivity.this, e.getMessage(),
							Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					Toast.makeText(AllAppsActivity.this, e.getMessage(),
							Toast.LENGTH_LONG).show();
				}

			}
		});

	}


	private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
		ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
		for (ApplicationInfo info : list) {
			try {
				if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
					if(getPermissionsContact(info.packageName ))
						applist.add(info);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return applist;
	}
	public boolean getPermissionsContact( String packageName )
	{
		boolean isPermissionsContact =false;
		try
		{
			PackageInfo info = packageManager.getPackageInfo( packageName, PackageManager.GET_PERMISSIONS );
			for( String permission : info.requestedPermissions )
			{
				String p=permission;
				//   Toast.makeText(context,p,Toast.LENGTH_LONG).show();
				if(p.equals("android.permission.WRITE_CONTACTS")||p.equals("android.permission.READ_CONTACTS"))
				{
					isPermissionsContact=true;
					break;
				}
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		return isPermissionsContact;
	}
	private class LoadApplications extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress = null;

		@Override
		protected Void doInBackground(Void... params) {
			applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
			listadaptor = new ApplicationAdapter(AllAppsActivity.this,
					R.layout.snippet_list_row, applist);

			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Void result) {
			lvPackageList.setAdapter (listadaptor);
			progress.dismiss();
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			progress = ProgressDialog.show(AllAppsActivity.this, null,
					"Loading application info...");
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
	}
}