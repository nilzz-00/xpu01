package com.xpu01;

/**
 *  ListAdapter
 */
import android.content.*;
import android.view.*;
import android.widget.*;
import com.xpu01.*;
import java.util.*;

public class DialogItemAdapter extends BaseAdapter
 {
	 
    //这里可以传递个对象，用来控制不同的item的效果
    //比如每个item的背景资源，选中样式等
    public List<String> list;
    LayoutInflater inflater;

    public DialogItemAdapter(Context context, List<String> list) { //接数据
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int i) {
        if (i == getCount() || list == null) {
            return null;
        }
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_app, null);
            holder.typeTextview = (TextView) convertView.findViewById(R.id.tv_app_name);
			holder.typeImageView = (ImageView) convertView.findViewById(R.id.iv_app);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
		holder.typeTextview.setText(Japp.appname(getItem(position))); //添加文字
		holder.typeImageView.setImageBitmap(Japp.applcon(getItem(position))); //添加图标
		
        return convertView;
    }

	
    public static class ViewHolder
	{
		public TextView typeTextview;

		public ImageView typeImageView;
		
    }
	
	
	
	
	
	
}

