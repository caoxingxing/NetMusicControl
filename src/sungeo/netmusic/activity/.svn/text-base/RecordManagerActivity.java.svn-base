package sungeo.netmusic.activity;

import java.util.List;

import sungeo.netmusic.R;
import sungeo.netmusic.manager.RecordMgr;
import sungeo.netmusic.unit.DeletableAdapter;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class RecordManagerActivity extends BaseActivity{
	private ListView 		mRecordList;
	private RecordMgr 		mRecMgr;
	private List<String> 	mRecordInfo;
	private Button 			mStartRec;
	private Button 			mStopRec;
	private Chronometer 	mChronometer;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recordmanager);

		mRecMgr = new RecordMgr();

		mRecordInfo = mRecMgr.getRecList();

		initBackBtn();

		initCtrlBtn();

		initChronometer();

		initRecordList();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initChronometer() {
		mChronometer = (Chronometer) findViewById(R.id.rec_chronometer);
	}

	private void initCtrlBtn() {
		mStartRec = (Button) findViewById(R.id.start_record_btn);
		mStopRec = (Button) findViewById(R.id.save_record_btn);

		if (mStartRec != null) {
			mStartRec.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startRec();
				}});
		}

		if (mStopRec != null) {
			mStopRec.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					saveRec();
				}});
		}
	}

	private void initBackBtn() {
		Button back = (Button) findViewById(R.id.backmain_btn);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}});
	}

	private void initRecordList() {
		if (mRecordList == null) {
			mRecordList = (ListView) findViewById(R.id.record_list);
		} else {
			mRecordList.removeAllViewsInLayout();
		}

		//生成动态数组，加入数据
		if (mRecordInfo == null) {
			mRecordList.setAdapter(null);
			return;
		}
		int musicCount = mRecordInfo.size();

		if (musicCount != 0) {
			//生成适配器的Item和动态数组对应的元素

			DeletableAdapter adapter = new DeletableAdapter(this, mRecordInfo);

			//添加并且显示
			mRecordList.setAdapter(adapter);
			addListEvent();
		} else {
			mRecordList.setAdapter(null);
		}
	}

	private void addListEvent() {
		if (mRecordList == null || mRecordList.getAdapter() == null) {
			return;
		}

		mRecordList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long row) {
				if (position < 0) {
					return;
				}
				DeletableAdapter adapter = (DeletableAdapter) parent.getAdapter();
				if (adapter != null) {
					adapter.setSelectItem(position);
					adapter.notifyDataSetInvalidated();
				}
				playRecord(position);
			}});
	}

	private void playRecord(int pos) {
		if (mRecMgr == null) {
			return;
		}

		mRecMgr.playRecord(pos);
	}

	private void startRec() {
		if (mRecMgr == null) {
			return;
		}

		mRecMgr.startRecord();

		if (mChronometer == null) {
			return;
		}
		mChronometer.setBase(SystemClock.elapsedRealtime());
		mChronometer.start();
	}

	private void saveRec() {
		if (mRecMgr == null) {
			return;
		}

		mRecMgr.stopRecord();

		if (mChronometer == null) {
			return;
		}

		mChronometer.stop();

		mRecordInfo = mRecMgr.getRecList();

		initRecordList();
	}

	@Override
	public void refreshUi(Message msg) {
		// TODO Auto-generated method stub
	}
}
