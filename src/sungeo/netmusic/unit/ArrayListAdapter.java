package sungeo.netmusic.unit;

import java.io.UnsupportedEncodingException;

import sungeo.netmusic.R;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ArrayListAdapter extends BaseAdapter{
	private int mSelecteItem;
	private String[] mElementName;
	private Context mContext;
	public ArrayListAdapter(Context context, String[] objects) {
		mContext = context;
		setmElementName(objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) { 
		View view=convertView;  
		if(view==null){  
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
			view=inflater.inflate(R.layout.array_list_item, null);  
		}  

		if (getmElementName() == null) {
			return null;
		}

		TextView elementText = (TextView) view.findViewById(R.id.element_text);
		String str = (position+ 1) + ". " + getmElementName()[position];
		elementText.setText(str);

		ViewGroup.LayoutParams params = elementText.getLayoutParams();
		int len = 0;
		try {
			len = str.getBytes("UTF-8").length;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (len >= 23) {
			params.height = 70;
		} else {
			params.height = 45;
		}
		elementText.setLayoutParams(params);

		RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.array_item_layout);
		if (mSelecteItem == position) {
			layout.setBackgroundColor(0xffffcc00);
		} else {
			layout.setBackgroundColor(Color.TRANSPARENT);
		}

		return view;
	}

	public void setSelected(int selected) {
		this.mSelecteItem = selected;
	}
	public int getSelected() {
		return mSelecteItem;
	}

	public void setmElementName(String[] mElementName) {
		this.mElementName = mElementName;
	}

	public String[] getmElementName() {
		return mElementName;
	}

	@Override
	public int getCount() {
		if (mElementName == null) {
			return 0;
		}
		return mElementName.length;
	}

	@Override
	public Object getItem(int position) {
		if (mElementName == null) {
			return null;
		}
		
		return mElementName[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
