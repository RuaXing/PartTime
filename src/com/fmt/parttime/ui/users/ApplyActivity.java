package com.fmt.parttime.ui.users;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.fmt.parttime.R;
import com.fmt.parttime.common.utils.IntentUtil;
import com.fmt.parttime.common.utils.PreferencesUtils;
import com.fmt.parttime.config.URLs;
import com.fmt.parttime.entity.Joblist;
import com.fmt.parttime.entity.Message;
import com.fmt.parttime.ui.BaseActivity;
import com.fmt.parttime.ui.LoadingDialog;
import com.fmt.parttime.ui.job.JobViewActivity;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * 我的申请
 * 
 * @author Administrator
 * 
 */
public class ApplyActivity extends BaseActivity {

	@ViewInject(R.id.job_list)
	SwipeMenuListView menuListView;

	LoadingDialog dialog;
	ApplyAdapter adapter;
	List<Joblist> mdatas = new ArrayList<Joblist>();
	// 用户Id
	private int userId;
	// 申请项的状态
	int applyStatus = 0;

	@ViewInject(R.id.prepare_pay)
	Button btapply;
	@ViewInject(R.id.already_pay_nocommentary)
	Button btagress;
	@ViewInject(R.id.already_pay_commentary)
	Button btrefures;
	@ViewInject(R.id.dot1)
	TextView dot1;
	@ViewInject(R.id.dot2)
	TextView dot2;
	@ViewInject(R.id.dot3)
	TextView dot3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myparttimejob_layout);

		ViewUtils.inject(this);

		userId = PreferencesUtils.getInt(ApplyActivity.this, "userId", 0);

		dialog = new LoadingDialog(this);

		adapter = new ApplyAdapter(ApplyActivity.this, mdatas);
		menuListView.setAdapter(adapter);
		
		// 创建SwipeMenuCreator
		createMenu();
		// 设置SwipeMenuItem点击事件
		setMenuItemClickListener();

		loadData(applyStatus);

	}

	// 设置SwipeMenuItem点击事件
	public void setMenuItemClickListener() {
		menuListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu,
					int index) {
				switch (index) {
				case 0:
					deleteMenuItem(position);
					break;

				default:
					break;
				}
				return false;
			}
		});
	}

	// 创建SwipeMenuCreator
	public void createMenu() {
		// 1.创建SwipeMenuCreator
		SwipeMenuCreator menuCreator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// 2.创建SwipeMenuItem对象
				SwipeMenuItem deleteItem = new SwipeMenuItem(ApplyActivity.this);
				// 3.设置SwipeMenuItem的相关属性
				deleteItem.setIcon(R.drawable.ic_delete);
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				deleteItem.setWidth(dp2px(90));
				// 4.添加菜单项
				menu.addMenuItem(deleteItem);
			}
		};
		// 5.为SwipeMenuListView设置SwipeMenuCreator
		menuListView.setMenuCreator(menuCreator);
	}

	// 已申请
	@OnClick(R.id.prepare_pay)
	public void preparepay(View v) {
		// 修改按钮的字体颜色
		btapply.setTextColor(getResources()
				.getColor(R.color.app_title_bg_color));
		btagress.setTextColor(getResources().getColor(
				R.color.app_text_second_color));
		btrefures.setTextColor(getResources().getColor(
				R.color.app_text_second_color));
		// 修改下划线
		dot1.setBackgroundColor(getResources().getColor(
				R.color.app_title_bg_color2));
		dot2.setBackgroundColor(getResources().getColor(
				android.R.color.transparent));
		dot3.setBackgroundColor(getResources().getColor(
				android.R.color.transparent));
		applyStatus = 0;
		loadData(applyStatus);
	}

	// 已拒绝
	@OnClick(R.id.already_pay_commentary)
	public void alreadypay(View v) {
		// 修改按钮的字体颜色
		btapply.setTextColor(getResources().getColor(
				R.color.app_text_second_color));
		btagress.setTextColor(getResources().getColor(
				R.color.app_text_second_color));
		btrefures.setTextColor(getResources().getColor(
				R.color.app_title_bg_color));
		// 修改下划线
		dot1.setBackgroundColor(getResources().getColor(
				android.R.color.transparent));
		dot2.setBackgroundColor(getResources().getColor(
				android.R.color.transparent));
		dot3.setBackgroundColor(getResources().getColor(
				R.color.app_title_bg_color2));
		applyStatus = 2;
		loadData(applyStatus);
	}

	// 已同意

	@OnClick(R.id.already_pay_nocommentary)
	public void alreadypayno(View v) {
		// 修改按钮的字体颜色
		btapply.setTextColor(getResources().getColor(
				R.color.app_text_second_color));
		btagress.setTextColor(getResources().getColor(
				R.color.app_title_bg_color));
		btrefures.setTextColor(getResources().getColor(
				R.color.app_text_second_color));
		// 修改下划线
		dot1.setBackgroundColor(getResources().getColor(
				android.R.color.transparent));
		dot2.setBackgroundColor(getResources().getColor(
				R.color.app_title_bg_color2));
		dot3.setBackgroundColor(getResources().getColor(
				android.R.color.transparent));
		//
		applyStatus = 1;
		loadData(applyStatus);
	}

	/**
	 * 获得数据源
	 */
	private void loadData(int applyStatus) {

		HttpUtils httpUtils = new HttpUtils();
		//设置当前请求的缓存时间
		httpUtils.configCurrentHttpCacheExpiry(0*1000);
		//设置默认请求的缓存时间
		httpUtils.configDefaultHttpCacheExpiry(0);
		// 列表地址
		String url = String.format(URLs.APPLY_VIEW, userId, applyStatus + "");
		httpUtils.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onLoading(long total, long current, boolean isUploading) {

				super.onLoading(total, current, isUploading);
				dialog.show();
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {

				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}

				showLongToast(3, "申请列表获取失败");
			}

			@Override
			public void onSuccess(ResponseInfo<String> info) {
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				LogUtils.e("服务端传过来的数据:" + info.result);

				// 将json对象反序列化
				ObjectMapper mapper = new ObjectMapper();
				Message message = null;

				try {

					message = mapper.readValue(info.result, Message.class);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (message != null) {

					try {
						// 解析将json数组解析成List对象
						List<Joblist> joblists = mapper.readValue(
								message.getData(),
								new TypeReference<List<Joblist>>() {
								});
						adapter.addMore(joblists);
						adapter.notifyDataSetChanged();

					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					showLongToast(3, "未解析到对象!");
				}

			}
		});

	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

	// 删除菜单项
	public void deleteMenuItem(int position) {
		// 删除选项
		Joblist joblist2 = (Joblist) adapter.getItem(position);
		HttpUtils httpUtils = new HttpUtils();
		// 列表地址

		String url = String.format(URLs.APPLY_DELETE, joblist2.getJobID(),
				applyStatus, userId);
		httpUtils.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onLoading(long total, long current, boolean isUploading) {

				super.onLoading(total, current, isUploading);
				
				dialog.show();
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {

				if (dialog != null && dialog.isShowing()) {
					
					dialog.dismiss();
				}

				showLongToast(3, "删除申请项失败");
			}

			@Override
			public void onSuccess(ResponseInfo<String> info) {
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				LogUtils.e("服务端传过来的数据:" + info.result);

				// 将json对象反序列化
				ObjectMapper mapper = new ObjectMapper();
				Message message = null;

				try {

					message = mapper.readValue(info.result, Message.class);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (message != null) {

					try {
						// 解析将json数组解析成List对象
						List<Joblist> joblists = mapper.readValue(
								message.getData(),
								new TypeReference<List<Joblist>>() {
								});
						LogUtils.e("joblists:" + joblists.size());
						// 更新适配器(注意应该更新适配器的List集合,才能实现立即刷新)
						adapter.addMore(joblists);
						adapter.notifyDataSetChanged();
						showLongToast(4, "删除申请项成功!");

					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					showLongToast(3, "未解析到对象!");
				}
			}
		});
	}
	
	@OnClick(R.id.back)
	public void back(View v){
		IntentUtil.start_activity(ApplyActivity.this, HomeActivity.class);
		ApplyActivity.this.finish();
	}

}
