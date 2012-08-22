package sungeo.netmusic.unit;

import java.util.ArrayList;
import java.util.List;

import sungeo.netmusic.R;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class SongAdapter extends BaseAdapter{

	private Context 		mContext;  
	private List<String> 	mSongName;
	private List<String> 	mSinger;
	private List<View> 		mItem 		= new ArrayList<View>();
	private int 			mSelectItem	= -1;
	private boolean 		mDelSong 	= false;

	public SongAdapter(Context context,List<String> text) {
		mContext = context;  
		mSongName=text;  
	}

	@Override
	public int getCount() {
		return mSongName.size();  
	}

	@Override
	public Object getItem(int position) {
		return mSongName.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;  
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) { 
		View view=convertView;  
		if(view==null){  
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
			view=inflater.inflate(R.layout.list_items, null);  
		}  

		if (mSinger != null && mSinger.size() != 0) {
			TextView singerText = (TextView)view.findViewById(R.id.singer_text);
			singerText.setText(mSinger.get(position));
		}
		TextView textView=(TextView)view.findViewById(R.id.ItemTitle);  
		textView.setText(mSongName.get(position));  
		final ImageView imageView=(ImageView)view.findViewById(R.id.ItemImage);  
		final ImageView imageView2=(ImageView)view.findViewById(R.id.delImage);  

		imageView.setTag(position);  
		if(position==mSelectItem){
			view.setBackgroundColor(0xffffc700);

			textView.setTextColor(0xffffffff);
			imageView.setBackgroundResource(android.R.drawable.btn_star_big_on); 
		}else{
			view.setBackgroundColor(Color.TRANSPARENT);
			textView.setTextColor(0xffffffff);
			imageView.setBackgroundResource(android.R.drawable.btn_star_big_off); 
		}
		if(mDelSong) {
			imageView2.setVisibility(View.VISIBLE);
		} else {
			imageView2.setVisibility(View.GONE);
		}
		imageView2.setBackgroundResource(R.drawable.deleterecordbtn); 
		mItem.add(view);
		return view;  
	}

	public int getSelectItem() {
		return mSelectItem;
	}

	public void setSelectItem(int selectItem) {
		mSelectItem = selectItem;
	}

	public void setSinger(List<String> singer) {
		mSinger = singer;
	}

	public List<String> getSinger() {
		return mSinger;
	}

	public void setmDelSong(boolean mDelSong) {
		this.mDelSong = mDelSong;
	}

	public boolean ismDelSong() {
		return mDelSong;
	}
}
