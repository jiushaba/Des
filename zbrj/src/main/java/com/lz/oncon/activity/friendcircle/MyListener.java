package com.lz.oncon.activity.friendcircle;

import com.xuanbo.xuan.R;
import com.lz.oncon.activity.friendcircle.FriendCircleActivity.ShowCommentLayoutInterface;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;

public class MyListener implements OnClickListener {
	public Context mc;
	public ShowCommentLayoutInterface ms;
	public int mp;
	public String mf;
	public boolean mu;

	public MyListener(Context c, ShowCommentLayoutInterface s, int p, String f, boolean u) {
		this.mc = c;
		this.ms = s;
		this.mp = p;
		this.mf = f;
		this.mu = u;
	}

	@Override
	public void onClick(View v) {
		FC_CommentOrUp_PopUtil pop = new FC_CommentOrUp_PopUtil(mc, ms, mp, mf, mu);
		PopupWindow p = pop.getPopupwindown();

		int[] xy = new int[2];
		v.getLocationOnScreen(xy);
		if (mu) {
			p.showAtLocation(v, Gravity.LEFT | Gravity.TOP, xy[0] - mc.getResources().getDimensionPixelSize(R.dimen.fc_uppop_x_1)
					, xy[1] - mc.getResources().getDimensionPixelSize(R.dimen.fc_uppop_y));
		} else {
			p.showAtLocation(v, Gravity.LEFT | Gravity.TOP, xy[0] - mc.getResources().getDimensionPixelSize(R.dimen.fc_uppop_x_2)
					, xy[1] - mc.getResources().getDimensionPixelSize(R.dimen.fc_uppop_y));
		}
	}
}
