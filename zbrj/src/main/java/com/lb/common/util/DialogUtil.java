package com.lb.common.util;

import com.xuanbo.xuan.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class DialogUtil {

	/**
	 * 显示统一风格提示对话框
	 * 
	 * @param context
	 *            上下文
	 * @param tipID
	 *            提示内容文字id
	 * @param positiveId
	 *            确认功能按钮的文字id，无此按钮填-1
	 * @param negativeId
	 *            取消功能按钮的文字id，无此按钮填-1
	 * @param cancelable
	 *            对话框是否可通过物理返回键关闭
	 */
	public static Dialog showTipDialog(Context context,
			int tipID, int positiveId, int negativeId,
			boolean cancelable,final AlertDialogListener alertDialogListener) {

		final Dialog dialog = new Dialog(context, R.style.TipDialog);
		dialog.setContentView(R.layout.tip_dialog);
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams params = dialogWindow.getAttributes();
		
		params.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.6);
		dialogWindow.setGravity(Gravity.CENTER | Gravity.CENTER);
		dialogWindow.setAttributes(params);

		TextView tip = (TextView) dialogWindow
				.findViewById(R.id.dialog_tip);
		
		TextView dialogPositive = (TextView) dialogWindow
				.findViewById(R.id.dialog_positive);
		TextView dialogNegative = (TextView) dialogWindow
				.findViewById(R.id.dialog_negative);


		/** 对话框提示文字 */
		if (tipID != -1) {
			tip.setText(tipID);
		} else {
			tip.setVisibility(View.GONE);
		}
	

		/** 各按钮的状况 */
		if (positiveId != -1) {
			dialogPositive.setText(positiveId);
			dialogPositive.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					alertDialogListener.positive();
					dialog.dismiss();
				}
			});
		} else {
			dialogPositive.setVisibility(View.GONE);
		}
		if (negativeId != -1) {
			dialogNegative.setText(negativeId);
			dialogNegative.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					alertDialogListener.negative();
					dialog.dismiss();
				}
			});
		} else {
			dialogNegative.setVisibility(View.GONE);
		}

		dialog.setCancelable(cancelable);
	
		dialog.show();
		return dialog;
	}
}
