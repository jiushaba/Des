package com.lz.oncon.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lb.common.util.ImageLoader;
import com.xuanbo.xuan.R;
import com.lb.zbrj.controller.PersonController;
import com.lb.zbrj.data.FansData;
import com.lb.zbrj.data.PersonData;
import com.lz.oncon.app.im.data.IMContactChooserData;
import com.lz.oncon.app.im.util.IMConstants;
import com.lz.oncon.widget.HeadImageView;

public class ChooserAdapter extends BaseAdapter {
	// 继承BaseAdapter来设置ListView每行的内容
	private LayoutInflater inflater;
	private List<FansData> list;
	private HashMap<String, Integer> alphaIndexer;
	private String[] sections;
	private Handler handler ;
	private OnClickListener mOnClickListener;
	private PersonController mPersonController;
	
	public HashMap<String, Integer> getIndexer(){
		return alphaIndexer;
	}
	public String[] getSections(){
		return sections;
	}
	
	public List<FansData> getList() {
		return list;
	}
	public void setList(List<FansData> list) {
		this.list = list;
	}
	private void initIndexer() {
		alphaIndexer=new HashMap<String, Integer>();
		sections = new String[list.size()];
		for (int i = 0;i<list.size();i++) {
			FansData keyF = list.get(i);
			String currentStr = keyF.index;
			int p = i;
			if(alphaIndexer.containsKey(currentStr)){
			}else{
				alphaIndexer.put(currentStr, p);  
            	sections[p] = currentStr;
			}
        }
	}
	private void initListener(){
		mOnClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				ChooseViewHolder holder = (ChooseViewHolder)v.getTag();
				FansData fd = list.get(holder.position);
				Message msg = Message.obtain();
				if(!IMContactChooserData.getInstance().isSelected(fd.account)){
					msg.what = IMConstants.CHOOSER_ADDMEMBER;
					holder.check.setImageResource(R.drawable.btn_check_on_normal);
					msg.obj = fd;
				}else{
					msg.what = IMConstants.CHOOSER_SUBMEMBER;
					holder.check.setImageResource(R.drawable.btn_check_off_normal);
					msg.obj = fd.account;
				}
				handler.sendMessage(msg);
			}
		};
	}
	
	public ChooserAdapter(Context context, List<FansData> list,OnCheckChangedListener mOnCheckChangedListener) {
		this.inflater = LayoutInflater.from(context);
		this.list = list;
		mPersonController = new PersonController();
		initIndexer();
		initListener();
	}
	
	public ChooserAdapter(Context context, List<FansData> list,Handler handler) {
		this.inflater = LayoutInflater.from(context);
		this.list = list;
		this.handler = handler;
		mPersonController = new PersonController();
		initIndexer();
		initListener();
	}

	public int getCount() {
		return list == null ?0:list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ChooseViewHolder holder;
		FansData fd = list.get(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.choose_list_item, null);
			holder = new ChooseViewHolder();
			holder.indexLayout = (RelativeLayout) convertView.findViewById(R.id.indexLayout);
			holder.firstCharHintTextView = (TextView) convertView.findViewById(R.id.text_first_char_hint);
			holder.nameTextView = (TextView) convertView.findViewById(R.id.content);
			holder.check = (ImageView) convertView.findViewById(R.id.addgroup_chooser);
			holder.iconView = (HeadImageView) convertView.findViewById(R.id.addgroup_icon);
			convertView.setTag(holder);
		} else {
			holder = (ChooseViewHolder) convertView.getTag();
		}
		holder.position = position;
		setPerson(position, fd, holder);
		convertView.setOnClickListener(mOnClickListener);
		
		return convertView;
	}
	
	private void setPerson(int position, final FansData data, final ChooseViewHolder holder){
		//这里显示关键字的分类
		int idx = position - 1;
		// 判断前后Item是否匹配，如果不匹配则设置并显示，匹配则取消
		char previewChar = idx >= 0 ? list.get(idx).index.charAt(0) : ' ';
		char currentChar = data.index.charAt(0);
		// 将小写字符转换为大写字符
		char newPreviewChar = Character.toUpperCase(previewChar);
		char newCurrentChar = Character.toUpperCase(currentChar);
		if (newCurrentChar != newPreviewChar) {
			holder.indexLayout.setVisibility(View.VISIBLE);
			holder.firstCharHintTextView.setText(String.valueOf(newCurrentChar));
		} else {
			// 此段代码不可缺：实例化一个CurrentView后，会被多次赋值并且只有最后一次赋值的position是正确
			holder.indexLayout.setVisibility(View.GONE);
		}
		PersonData person = mPersonController.findPerson(data.account);
		if(TextUtils.isEmpty(person.nickname)){
			if(TextUtils.isEmpty(data.nick)){
				holder.nameTextView.setText(data.account);
			}else{
				holder.nameTextView.setText(data.nick);
			}
		}else{
			holder.nameTextView.setText(person.nickname);
		}
		if(TextUtils.isEmpty(person.image)){
			ImageLoader.displayHeadImage(data.imageurl, holder.iconView);
		}else{
			ImageLoader.displayHeadImage(person.image, holder.iconView);
		}
		
		if (IMContactChooserData.getInstance().isSelected(data.account)) {
			holder.check.setImageResource(R.drawable.btn_check_on_normal);
		} else {
			holder.check.setImageResource(R.drawable.btn_check_off_normal);
		}
	}

	public static class ChooseViewHolder {
		RelativeLayout indexLayout;
		public TextView firstCharHintTextView;
		public TextView nameTextView;
		public ImageView check;
		public HeadImageView iconView;
		public int position;
	}
	
	public interface OnCheckChangedListener {
		public void onCheckedChanged(View v,Object object, boolean isChecked);
	}	
}