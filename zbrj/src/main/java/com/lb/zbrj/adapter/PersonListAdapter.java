package com.lb.zbrj.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
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
import com.lz.oncon.widget.EllipsizeTextView;
import com.lz.oncon.widget.HeadImageView;

public class PersonListAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<FansData> list;
	Context context;
	private HashMap<String, Integer> alphaIndexer;
	private String[] sections;
	private PersonController mPersonController;
	
	public HashMap<String, Integer> getIndexer(){
		return alphaIndexer;
	}
	public String[] getSections(){
		return sections;
	}
	
	private void initIndexer() {
		alphaIndexer=new HashMap<String, Integer>();
		sections = new String[list.size()];
		for (int i = 0;i<list.size();i++) {
			FansData keyF = list.get(i);
			String currentStr = keyF.index;
			int p = i;
			currentStr = currentStr.toUpperCase().substring(0, 1);
			if(alphaIndexer.containsKey(currentStr)){
			}else{
				alphaIndexer.put(currentStr, p);  
            	sections[p] = currentStr;
			}
        }
	}
	private void initListener(){
		
	}
	
	public PersonListAdapter(Context context, List<FansData> list) {
		this.inflater = LayoutInflater.from(context);
		this.list = list;
		this.context = context;
		mPersonController = new PersonController();
		initIndexer();
		initListener();
	}
	
	public void setList(List<FansData> list) {
		this.list = list;
		initIndexer();
		this.notifyDataSetChanged();
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
			convertView = inflater.inflate(R.layout.activity_person_list_item, null);
			holder = new ChooseViewHolder();
			holder.indexLayout = (RelativeLayout) convertView.findViewById(R.id.indexLayout);
			holder.signatureLayout = (RelativeLayout) convertView.findViewById(R.id.signatureLayout);
			holder.firstCharHintTextView = (TextView) convertView.findViewById(R.id.text_first_char_hint);
			holder.nameTextView = (TextView) convertView.findViewById(R.id.name);
			holder.iconView = (HeadImageView) convertView.findViewById(R.id.icon);
			holder.sigTV = (EllipsizeTextView) convertView.findViewById(R.id.signatureTV);
			holder.sigTV.setMaxLines(2);
			holder.isFriendV = (ImageView) convertView.findViewById(R.id.isFriend);
			convertView.setTag(holder);
		} else {
			holder = (ChooseViewHolder) convertView.getTag();
		}
		setPerson(position, fd, holder);
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
		if(TextUtils.isEmpty(person.nickname) || person.account.equals(person.nickname)){
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
		holder.signatureLayout.setVisibility(View.INVISIBLE);
		if(!TextUtils.isEmpty(person.label)){
			holder.sigTV.setText(person.label);
			holder.signatureLayout.setVisibility(View.VISIBLE);
		}else{
			holder.signatureLayout.setVisibility(View.INVISIBLE);
		}
		if(data.isFocused == 1){
			holder.isFriendV.setVisibility(View.VISIBLE);
		}else{
			holder.isFriendV.setVisibility(View.GONE);
		}
	}

	public static class ChooseViewHolder {
		RelativeLayout indexLayout;
		RelativeLayout signatureLayout;
		public TextView firstCharHintTextView;
		
		public TextView nameTextView;
		public HeadImageView iconView;
		EllipsizeTextView sigTV;
		
		ImageView isFriendV;
	}
}