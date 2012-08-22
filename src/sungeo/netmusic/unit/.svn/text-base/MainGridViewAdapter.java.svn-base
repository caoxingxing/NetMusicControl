package sungeo.netmusic.unit;

import java.util.List;

import sungeo.netmusic.R;
import sungeo.netmusic.activity.MainActivity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;

public class MainGridViewAdapter extends BaseAdapter{
	private Context 		mContext;  
	private List<String> 	mText;	
	private int 			mDownloadId 	= -1;
	private int 			mPlayingId 		= -1;
	private LayoutInflater listContainer;           //视图容器   
	public final class ListItemView{                //自定义控件集合       
        public Button ctrlBtn;   
        public Button nameBtn;
        public Button listBtn;
	}     
	
	public MainGridViewAdapter(Context context,List<String> text) {
		mContext = context;
		mText = text;
		listContainer = LayoutInflater.from(context);   //创建视图容器并设置上下文   
	}

	@Override
	public int getCount() {
		return mText.size();
	}

	@Override
	public Object getItem(int position) {
		return mText.get(position);  
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//Log.d("getView调用次数", "进入getView了");
		final int selectId = position;   
		ListItemView  listItemView = null;
		if(convertView == null){  
			listItemView = new ListItemView(); 
			convertView = listContainer.inflate(R.layout.gridview_items, null);
			listItemView.ctrlBtn = (Button)convertView.findViewById(R.id.album_ctrl_btn); 
			/*listItemView.ctrlBtn.setId(R.id.album_ctrl_btn);
			listItemView.ctrlBtn.setTag(selectId);*/
			listItemView.listBtn = (Button)convertView.findViewById(R.id.song_list_btn);
			/*listItemView.listBtn.setId(R.id.song_list_btn);
			listItemView.listBtn.setTag(selectId);*/
			listItemView.nameBtn = (Button)convertView.findViewById(R.id.album_name_btn);
			/*listItemView.nameBtn.setId(R.id.album_name_btn);
			listItemView.nameBtn.setTag(selectId);*/
			
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView)convertView.getTag();
		}

		//listItemView.ctrlBtn.setOnClickListener((MainActivity)mContext);
		/*MainApplication.getInstance().getmMsgSender().setDebugLevel(6);
		MainApplication.getInstance().getmMsgSender().OutputLogToSD("加载ITEM的ID为："+String.valueOf(position));
		MainApplication.getInstance().getmMsgSender().setDebugLevel(1);*/

		listItemView.ctrlBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MainActivity mainAct = (MainActivity)mContext;
				mainAct.respondCtrl(selectId);
				/*MainApplication.getInstance().getmMsgSender().setDebugLevel(6);
				MainApplication.getInstance().getmMsgSender().OutputLogToSD("响应点击的ITEM的ID为："+String.valueOf(selectId));
				MainApplication.getInstance().getmMsgSender().setDebugLevel(1);*/
				mainAct = null;
			}});
		
		listItemView.nameBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MainActivity mainAct = (MainActivity)mContext;
				mainAct.showSongList(selectId);
				mainAct = null;
			}});
		
		listItemView.listBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MainActivity mainAct = (MainActivity)mContext;
				mainAct.showSongList(selectId);
				mainAct = null;
			}});
		
		if(position == mPlayingId) {
			listItemView.ctrlBtn.setBackgroundResource(R.drawable.p1_playing);
		} else {
			listItemView.ctrlBtn.setBackgroundResource(R.drawable.p1_pause);				
		}
		
		//listItemView.nameBtn.setOnClickListener((MainActivity)mContext);
		

		//listItemView.listBtn.setOnClickListener((MainActivity)mContext);
		

		if(position == mDownloadId) {
			setAlbumName(listItemView.nameBtn, true, null);
		}else{
			setAlbumName(listItemView.nameBtn, false, mText.get(position));
		}
		
		return convertView;  
	}

	public void setDownId(int id) {
		mDownloadId = id;
	}

	public void setPlayingId(int id ) {
		mPlayingId = id;
	}

	private void setAlbumName(Button btn, boolean downloading, String name) {
		if(downloading){
			btn.setText("正在下载…");
		}else{
			btn.setText(name);	
		}
	}
}
