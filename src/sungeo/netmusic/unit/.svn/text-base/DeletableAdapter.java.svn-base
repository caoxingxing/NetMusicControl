package sungeo.netmusic.unit;

import java.io.File;
import java.util.List;

import sungeo.netmusic.R;
import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DeletableAdapter extends BaseAdapter{  
	private Context 		mContext;  
	private List<String> 	mText;  
	private int 			mSelectItem = -1;
	
	public DeletableAdapter(Context context,List<String> text){  
		mContext = context;  
		mText=text;  
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
		final int index=position;  
		View view=convertView;  
		if(view==null){  
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
			view=inflater.inflate(R.layout.recorditem, null);  
		}  

		if(position==mSelectItem){
			view.setBackgroundColor(0xffffc700);
		}else{
			view.setBackgroundColor(Color.TRANSPARENT);
		}

		final TextView textView=(TextView)view.findViewById(R.id.simple_item_1);  
		textView.setText(mText.get(position)); 
		textView.setTextSize(30);
		textView.setGravity(Gravity.CENTER_VERTICAL);
		final ImageView imageView=(ImageView)view.findViewById(R.id.simple_item_2);  
		imageView.setBackgroundResource(R.drawable.deleterecordbtn);  
		imageView.setTag(position);  
		imageView.setOnClickListener(new OnClickListener() {  

			@Override  
			public void onClick(View v) {  
				boolean ret = deleteFile(index);
				if (ret) {
					mText.remove(index);  
					notifyDataSetChanged(); 
				}

				StringBuffer str = new StringBuffer();
				str.append("É¾³ýÂ¼Òô£º");
				str.append(textView.getText().toString());
				if (ret) {
					str.append("³É¹¦!");
				} else {
					str.append("Ê§°Ü!");
				}
				Toast.makeText(mContext, str.toString(), Toast.LENGTH_SHORT).show();  
			}  
		});  
		return view;  
	}  

	public void setSelectItem(int selectItem) {
		mSelectItem = selectItem;
	}

	private boolean deleteFile(int index) {
		boolean ret = false;
		String fileName = Environment.getExternalStorageDirectory() 
		+ "/" + "My_Record" 
		+ "/" + mText.get(index)
		+ ".amr";
		File file = new File(fileName);
		ret = file.delete();
		return ret;
	}
}  
