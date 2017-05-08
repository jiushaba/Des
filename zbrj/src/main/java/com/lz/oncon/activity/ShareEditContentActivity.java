package com.lz.oncon.activity;

import java.io.File;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.xuanbo.xuan.R;
import com.lz.oncon.widget.TitleView;

public class ShareEditContentActivity extends BaseActivity {

	private TitleView title;
	private ImageView speak_pic;
	private EditText content;
	private TextView wordsum;

	private Intent intent;
	private String imagePath;
	private int sum = 0;
	private int sum_finally = 0;
	private String in_content = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.share_edit_content);

		title = (TitleView) findViewById(R.id.title);
		speak_pic = (ImageView) findViewById(R.id.edit_content_picture);
		content = (EditText) findViewById(R.id.edit_content_input);
		wordsum = (TextView) findViewById(R.id.edit_content_wordsum);

		initController();
		setValue();
	}

	public void initController() {
		intent = getIntent();
		Bundle b = intent.getExtras();
		if (b != null) {
			imagePath = b.getString("imagepath");
			sum = b.getInt("sum");
			in_content = b.getString("content");
		}

	}

	public void setValue() {
		File imageFile = new File(imagePath);
		if (imageFile.exists() && imageFile.length() > 0) {
			speak_pic.setImageURI(Uri.fromFile(imageFile));
		}

		int suma = 140 - sum - in_content.length();

		content.setText(in_content);
		wordsum.setText(suma + "");

		content.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				sum_finally = 140 - s.length() + count;
				wordsum.setText(sum_finally + "");
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

	}

	@Override
	public void onClick(View arg0) {
		super.onClick(arg0);
		switch (arg0.getId()) {
		case R.id.common_title_TV_left:
			ShareEditContentActivity.this.finish();
			break;
		case R.id.common_title_TV_right:
			if (sum_finally < 0) {
				Toast.makeText(ShareEditContentActivity.this, getString(R.string.share_word_over_limit),
						Toast.LENGTH_SHORT).show();
				return;
			}
			ShareEditContentActivity.this.finish();
			break;
		default:
			break;
		}
	}

}
