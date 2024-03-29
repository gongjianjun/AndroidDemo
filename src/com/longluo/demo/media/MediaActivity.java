package com.longluo.demo.media;

import java.util.Arrays;
import java.util.LinkedList;

import com.longluo.demo.MainActivity;
import com.longluo.demo.R;
import com.longluo.demo.activitycard.MonthActivityCardActivity;
import com.longluo.demo.animation.AnimationActivity;
import com.longluo.demo.badgeview.BadgeViewDemoActivity;
import com.longluo.demo.calendarcard.CalendarCardDemoActivity;
import com.longluo.demo.fragment.FragmentDemoActivity;
import com.longluo.demo.friends.FriendsActivity;
import com.longluo.demo.jpush.JPushDemoActivity;
import com.longluo.demo.login.LoginActivity;
import com.longluo.demo.media.audio.AudioCaptureActivity;
import com.longluo.demo.media.audio.AudioRecordActivity;
import com.longluo.demo.media.camera.CameraActivity;
import com.longluo.demo.media.camera.CaptureImageActivity;
import com.longluo.demo.media.video.CamcorderActivity;
import com.longluo.demo.media.video.VideoCaptureActivity;
import com.longluo.demo.media.video.VideoRecordActivity;
import com.longluo.demo.notifications.NotificationActivity;
import com.longluo.demo.numberprogressbar.NumberProgressBarActivity;
import com.longluo.demo.roundedimageview.RoundedImageViewActivity;
import com.longluo.demo.searchview.SearchViewActivity;
import com.longluo.demo.slideview.SlideViewDemoActivity;
import com.longluo.demo.swipelistview.SwipeListViewDemoActivity;
import com.longluo.demo.util.LinkUtils;
import com.longluo.demo.util.UIUtils;
import com.longluo.demo.viewpager.ViewPagerActivity;
import com.longluo.demo.viewpager.fragments.ViewPagerMultiFragmentActivity;
import com.longluo.demo.viewpager.tabpageindicator.miui.MIUITabPageIndicatorActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class MediaActivity extends Activity {

	private ListView mListView;

	private static final String[] mMediaStrings = { "Audio Capture",
			"Audio Record", "Camera", "Capture Image", "Camcorder",
			"Video Capture", "Video Record" };

	private static final int mTotal = mMediaStrings.length - 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_media);

		initViews();
		init();
	}

	private void initViews() {
		mListView = (ListView) findViewById(R.id.id_lv_media);

	}

	private void init() {
		LinkedList<String> mListItems = new LinkedList<String>();
		mListItems.addAll(Arrays.asList(mMediaStrings));
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mListItems);

		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					startActivity(AudioCaptureActivity.class);
					break;

				case 1:
					startActivity(AudioRecordActivity.class);
					break;

				case 2:
					startActivity(CameraActivity.class);
					break;

				case 3:
					startActivity(CaptureImageActivity.class);
					break;

				case 4:
					startActivity(CamcorderActivity.class);
					break;

				case 5:
					startActivity(VideoCaptureActivity.class);
					break;
					
				case 6:
					startActivity(VideoRecordActivity.class);
					break;

				default:
					break;
				}
			}
		});
	}

	private void startActivity(Class<?> cls) {
		Intent intent = new Intent(MediaActivity.this, cls);
		startActivity(intent);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
