package com.lz.oncon.widget;

import com.lb.common.util.ImageUtil;
import com.lb.common.util.StringUtils;
import com.xuanbo.xuan.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;

public class EditableSpinner extends RelativeLayout {
	
	private Context mContext;
	private EditText editable_spinner_ET;
	private ImageView editable_spinner_IV_del;
	private ImageView editable_spinner_IV;
	private ArrayAdapter<String> mAdapter;
	private PopupWindow popupWindow;
	ListView listView;
	View popView;
	
	private Integer hint = 0;
	private Integer width;
	private Integer height;
//	private Integer minLength = 0;
//	private String digits;
	
	private int xoff = 0;
	private int yoff = 0;
	private int popupWindowWidth = 0;

	public EditableSpinner(Context context) {
		super(context);
		this.mContext = context;
		xoff = ImageUtil.convertDipToPx(EditableSpinner.this.mContext, -6);
		yoff = ImageUtil.convertDipToPx(EditableSpinner.this.mContext, -2);
		init();
	}
	
	public EditableSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		TypedArray attrsArray = context.obtainStyledAttributes(attrs,R.styleable.EditableSpinnerAttrs);
		hint = attrsArray.getResourceId(R.styleable.EditableSpinnerAttrs_hint, 0);
		width = attrsArray.getInt(R.styleable.EditableSpinnerAttrs_width, 0);
		height = attrsArray.getInt(R.styleable.EditableSpinnerAttrs_height, 0);
		xoff = ImageUtil.convertDipToPx(EditableSpinner.this.mContext, -6);
		yoff = ImageUtil.convertDipToPx(EditableSpinner.this.mContext, -2);
		init();
	}
	
	public EditableSpinner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		TypedArray attrsArray = context.obtainStyledAttributes(attrs,R.styleable.EditableSpinnerAttrs);
		hint = attrsArray.getResourceId(R.styleable.EditableSpinnerAttrs_hint, 0);
		width = attrsArray.getInt(R.styleable.EditableSpinnerAttrs_width, 0);
		height = attrsArray.getInt(R.styleable.EditableSpinnerAttrs_height, 0);
		xoff = ImageUtil.convertDipToPx(EditableSpinner.this.mContext, -6);
		yoff = ImageUtil.convertDipToPx(EditableSpinner.this.mContext, -2);
		init();
	}
	
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
	
		super.onLayout(changed, l, t, r, b);
		popupWindowWidth = r-l+xoff-10;
		popupWindow.setWidth(popupWindowWidth);
	}

	private void init(){
		LayoutInflater.from(getContext()).inflate(R.layout.editable_spinner, this);
		editable_spinner_ET =  (EditText)findViewById(R.id.editable_spinner_ET); 
		editable_spinner_IV_del = (ImageView)findViewById(R.id.editable_spinner_IV_del);
		editable_spinner_IV =  (ImageView)findViewById(R.id.editable_spinner_IV);
		initStyles();
		popView = LayoutInflater.from(getContext()).inflate(R.layout.editable_spinner_list, null);
		listView = (ListView)popView.findViewById(R.id.editable_spinner_list_LV);
		popupWindow = new PopupWindow(popView, popupWindowWidth, LayoutParams.WRAP_CONTENT, true);
		//popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		initListeners();
	}
	
	private void initStyles(){
		if(hint != 0){
			editable_spinner_ET.setHint(hint);
		}
		editable_spinner_IV_del.setVisibility(View.GONE);
	}
	
	private void initListeners(){
		editable_spinner_ET.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				if(StringUtils.length(s.toString()) > 0){
					editable_spinner_IV_del.setVisibility(View.VISIBLE);
				}else{
					editable_spinner_IV_del.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start,
					int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start,
					int before, int count) {}
				
		});
		editable_spinner_ET.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(popupWindow.isShowing()){
					editable_spinner_ET.requestFocus();
					popupWindow.dismiss();
				}
			}
			
		});
		editable_spinner_ET.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					if(StringUtils.length(((EditText)v).getText().toString()) > 0){
						editable_spinner_IV_del.setVisibility(View.VISIBLE);
					}else{
						editable_spinner_IV_del.setVisibility(View.GONE);
					}
				}else{
					editable_spinner_IV_del.setVisibility(View.GONE);
				}
			}
			
		});
		listView.setOnItemClickListener(new OnItemClickListener(){

			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				editable_spinner_ET.setText((String)mAdapter.getItem(position));
				editable_spinner_ET.requestFocus();
				popupWindow.dismiss();
			}
			
		});
		
		editable_spinner_IV.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
//				editable_spinner_ET.requestFocus();
				if(mAdapter != null && !mAdapter.isEmpty()){
					if(popupWindow.isShowing()){
						popupWindow.dismiss();
					}else{
						popupWindow.showAsDropDown(editable_spinner_ET, xoff/2, xoff/2);
					}
				}
			}
			
		});
		
		editable_spinner_IV_del.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				editable_spinner_ET.requestFocus();
				editable_spinner_ET.setText("");
				editable_spinner_IV_del.setVisibility(View.GONE);
			}
			
		});
	}
	
	public void setAdapter(ArrayAdapter<String> adapter){
		this.mAdapter = adapter;
		listView.setAdapter(mAdapter);
	}
	
	public String getValue(){
		return editable_spinner_ET.getText().toString();
	}
	
	public void setValue(String value){
		editable_spinner_ET.setText(value);
	}
	
	public void addTextChangedListener(TextWatcher tw){
		editable_spinner_ET.addTextChangedListener(tw);
	}
}
