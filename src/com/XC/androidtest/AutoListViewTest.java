package com.XC.androidtest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.XC.androidtest.AutoListView.Loadmore;
import com.XC.androidtest.AutoListView.Refresh;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.SimpleAdapter;

public class AutoListViewTest extends Activity {
	private AutoListView lv;
	private Activity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.autolistview);
		lv = (AutoListView) findViewById(R.id.lv);
		activity = AutoListViewTest.this;
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		for (int i = 0; i < 50; i++) {
			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("t", "123456789");
			data.add(hashMap);
		}

		SimpleAdapter adapter = new SimpleAdapter(activity, data,
				R.layout.item, new String[] { "t" }, new int[] { R.id.tv });
		lv.setAdapter(adapter);
		lv.setRefresh(new Refresh() {

			@Override
			public void onRefresh() {
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						lv.onRefreshComplete();

					}
				}, 1000);
			}
		});
		lv.setLoadmore(new Loadmore() {

			@Override
			public void onLoadmore() {

				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						lv.onLoadmoreCompelte();
					}
				}, 1000);

			}
		});
	}
}
