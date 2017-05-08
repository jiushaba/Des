package com.lz.oncon.activity.friendcircle;

import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.text.util.Linkify;

public class Fc_TextToAutoLin {
	public static Spannable getFc_TextToAutoLin(String srcString){
		Spannable outString = null;
//		srcString = srcString.replaceAll("\r\n", "<br/>");
//		srcString = srcString.replaceAll("\n", "<br/>");
//		srcString = srcString.replaceAll("\r", "<br/>");
		outString = linkifyHtml(srcString, Linkify.ALL);
		return outString;
	}
	
	private static Spannable linkifyHtml(String html, int linkifyMask) {
	    String htmltemp = TextUtils.htmlEncode(html);
	    Spanned text = Html.fromHtml(htmltemp);
	    URLSpan[] currentSpans = text.getSpans(0, text.length(), URLSpan.class);
	    SpannableString buffer = new SpannableString(text);
	    Linkify.addLinks(buffer, linkifyMask);

	    for (URLSpan span : currentSpans) {
	        int end = text.getSpanEnd(span);
	        int start = text.getSpanStart(span);
	        buffer.setSpan(span, start, end, 0);
	    }
	    return buffer;
	}

}
