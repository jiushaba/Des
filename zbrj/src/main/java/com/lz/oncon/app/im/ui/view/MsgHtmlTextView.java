package com.lz.oncon.app.im.ui.view;

import android.content.Context;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xuanbo.xuan.R;
import com.lz.oncon.activity.BaseActivity;
import com.lz.oncon.api.SIXmppMessage;
import com.lz.oncon.app.im.ui.news.AsyncImageLoader;
import com.lz.oncon.app.im.util.IMConstants;

public class MsgHtmlTextView extends LinearLayout {
	public Context mContext;
	public LinearLayout contentLL, rootLL;
	public ImageView image;
	public TextView title, brief;
	
	public MsgHtmlTextView(Context context) {
		super(context);
		this.mContext = context;
		LayoutInflater.from(getContext()).inflate(R.layout.message_htmltext, this);
		title = (TextView)findViewById(R.id.title);
		image = (ImageView) findViewById(R.id.image);
		brief = (TextView)findViewById(R.id.brief);	
		contentLL = (LinearLayout)findViewById(R.id.content);	
		LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(BaseActivity.screenWidth * 2 / 3, LayoutParams.WRAP_CONTENT);
		contentLL.setLayoutParams(ll);
	}

	public void setMessage(final SIXmppMessage msg){
		String htmlMsgTemp = "";
		if(msg.getSourceType() == SIXmppMessage.SourceType.SEND_MESSAGE){
			contentLL.setBackgroundResource(R.drawable.bg_msg_outgoing);
		}else{
			contentLL.setBackgroundResource(R.drawable.bg_msg_incoming_white);
		}
		if(msg.getContentType() == SIXmppMessage.ContentType.TYPE_HTML_TEXT_2){
			htmlMsgTemp = msg.getTextContent().replaceAll("m1_extend_msg@@@lz-oncon@@@v1.0\\|\\|\\|type=16\\|\\|\\|", "");
			String[] htmlMsgtemp2 = htmlMsgTemp.split("\\|\\|\\|");
			if(htmlMsgtemp2.length > 1){
				String[] htmls = htmlMsgtemp2[1].replaceAll("mobilehtml=", "").split("\\|\\|\\|");
				htmlMsgTemp = new String(Base64.decode(htmls[0].getBytes(), Base64.DEFAULT));
			}
		}else if(msg.getContentType() == SIXmppMessage.ContentType.TYPE_HTML_TEXT_GENERAL){
			title.setVisibility(View.GONE);
			htmlMsgTemp = msg.getTextContent().replaceAll("m1_extend_msg@@@lz-oncon@@@v1.0\\|\\|\\|type=26\\|\\|\\|", "");
			String[] htmlMsgtemp2 = htmlMsgTemp.split("\\|\\|\\|");
			if(htmlMsgtemp2.length > 1){
				String[] htmls = htmlMsgtemp2[0].replaceAll("mhtml=", "").split("\\|\\|\\|");
				htmlMsgTemp = new String(Base64.decode(htmls[0].getBytes(), Base64.DEFAULT));
			}
			if(htmlMsgtemp2.length > 3){
				String[] htmls = htmlMsgtemp2[3].replaceAll("img=", "").split("\\|\\|\\|");
				String remoteUrl = htmls[0];
				if (remoteUrl!=null&&remoteUrl.lastIndexOf("/") != -1) {
					String fileName = remoteUrl.substring(remoteUrl.lastIndexOf("/") + "/".length());
					String picLocalPath = IMConstants.PATH_APPAD_PICTURE.concat(fileName);
					AsyncImageLoader.getInstance().loadDrawableToSave(fileName, picLocalPath, remoteUrl,image,false);
				}
			}
		}
		brief.setText(Html.fromHtml(htmlMsgTemp).toString());		
	}	
}