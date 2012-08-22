package sungeo.netmusic.unit;

import sungeo.netmusic.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MultipleAdapter extends BaseAdapter{
	private String[] mName = null;
	private String[] mSinger = null;
	private boolean[] mSel = null;
	private Context mContext;

	public MultipleAdapter(Context context, String[] name, String[] singer) {
		mContext = context;
		setmName(name);
		setmSinger(singer);
		setmSel(new boolean[getmName().length]);
	}

	public MultipleAdapter(Context context, String[] name, String[] singer, boolean[] sel) {
		mContext = context;
		setmName(name);
		setmSinger(singer);
		setmSel(sel);
	}

	@Override
	public int getCount() {
		if (mName == null) {
			return 0;
		}
		return getmName().length;
	}

	@Override
	public Object getItem(int position) {
		if (mName == null) {
			return null;
		}
		return getmName()[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (mName == null) {
			return null;
		}

		View view=convertView;  
		if(view==null){  
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
			view=inflater.inflate(R.layout.multiple_items, null);  
		}  

		ImageView checkBox = (ImageView)view.findViewById(R.id.check_box);
		if (getmSel() == null) {
			setmSel();
		}
		
		checkBox.setVisibility(View.VISIBLE);
		if (getmSel()[position]) {
			checkBox.setBackgroundResource(android.R.drawable.checkbox_on_background);
		} else {
			checkBox.setBackgroundResource(android.R.drawable.checkbox_off_background);
		}

		TextView name = (TextView) view.findViewById(R.id.element_name);
		String str = getmName()[position];
		name.setText(str);

/*		int len = 0;
		ViewGroup.LayoutParams params = name.getLayoutParams();
		try {
			len = str.getBytes("UTF-8").length;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (len >= 24) {
			params.height = 70;
			name.setLayoutParams(params);
		} else {
			params.height = 45;
			name.setLayoutParams(params);
		}*/
		
		TextView singer = (TextView) view.findViewById(R.id.singer_name);
		if (getmSinger() == null) {
			singer.setVisibility(View.GONE);
		} else {
			singer.setVisibility(View.VISIBLE);
			singer.setText(getmSinger()[position]);
		}

		return view;
	}

	public boolean isCheckByIndex(int position) {
		boolean flag = false;
		if (getmSel() == null) {
			return flag;
		}

		int len = getmSel().length;
		if (position >= len) {
			return flag;
		}

		return getmSel()[position];
	}

	public void allCheck() {
		if (getmSel() == null) {
			return;
		}

		int len = getmSel().length;
		for (int i = 0; i < len; i ++) {
			getmSel()[i] = true;
		}
	}

	public void reverseCheck() {
		if (getmSel() == null) {
			return;
		}

		int len = getmSel().length;
		for(int i = 0; i < len; i ++) {
			getmSel()[i] = !getmSel()[i];
		}
	}

	public void reverseCheck(int position) {
		if (getmSel() == null) {
			return;
		}

		int len = getmSel().length;
		if (position >= len) {
			return;
		}

		getmSel()[position] = !getmSel()[position];
	}

	public void setmName(String[] mName) {
		this.mName = mName;
	}

	public String[] getmName() {
		return mName;
	}

	public void setmSinger(String[] mSinger) {
		this.mSinger = mSinger;
	}

	public String[] getmSinger() {
		return mSinger;
	}

	public void setmSel() {
		if (mName == null) {
			return;
		}

		int len = mName.length;
		if (mSel == null) {
			mSel = new boolean[len];
		}
	}

	public void setmSel(boolean[] mSel) {
		this.mSel = mSel;
	}

	public int getSelCount() {
		int count = 0;
		if (mSel == null) {
			return 0;
		}

		int len = mSel.length;
		for (int i = 0; i < len; i ++) {
			if (mSel[i]) {
				count ++;
			}
		}
		return count;
	}

	public boolean hasSelected() {
		boolean flag = false;
		if (mSel == null) {
			return flag;
		}
		
		int len = mSel.length;
		for (int i = 0; i < len; i ++) {
			if (mSel[i]) {
				flag = true;
				break;
			}
		}
		
		return flag;
	}
	
	public boolean[] getmSel() {
		return mSel;
	}
}
