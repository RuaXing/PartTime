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
import android.widget.Toast;

/**
 * 收藏页面
 * SwipeMenuListView的使用步骤:
 * 1.
 * 2.
 * 3.
 * @author Administrator
 *
 */

public class CollectActivity extends BaseActivity{
	
	@ViewInject(R.id.collect_listview)
	SwipeMenuListView menuListView;
	
	CollectAdapter adapter;
	List<Joblist> mdatas = new ArrayList<Joblist>();
	LoadingDialog dialog;
	//用户Id
	private int userId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_likeandcollection_layout);
		
		ViewUtils.inject(this);
		
		userId = PreferencesUtils.getInt(CollectActivity.this,"userId",0);
		
		dialog = new LoadingDialog(this);
		
		 //初始化数据源
		adapter = new CollectAdapter(CollectActivity.this,mdatas);
		menuListView.setAdapter(adapter);
		loadData();
		
		//创建SwipeMenuCreator
		createMenu();
		//设置SwipeMenuItem点击事件
		setMenuItemClickListener();
	}
	
	//设置SwipeMenuItem点击事件
	public void setMenuItemClickListener() {
		menuListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
				switch (index) {
			
				case 0:
					Joblist joblist2 = (Joblist) adapter.getItem(position);
					HttpUtils httpUtils = new HttpUtils();
					//列表地址
					String url = String.format(URLs.COLLECT_DELETE,joblist2.getJobID(),userId);
					httpUtils.send(HttpMethod.GET, url,new RequestCallBack<String>() {

						@Override
						public void onLoading(long total, long current, boolean isUploading) {
					
							super.onLoading(total, current, isUploading);
							dialog.show();
						}
						
						
						@Override
						public void onFailure(HttpException arg0, String arg1) {
							
							if(dialog!=null && dialog.isShowing()){
								dialog.dismiss();
							}
							
							showLongToast(3,"删除收藏项失败");
						}

						@Override
						public void onSuccess(ResponseInfo<String> info) {
							if(dialog!=null && dialog.isShowing()){
								dialog.dismiss();
							}
							LogUtils.e("服务端传过来的数据:" + info.result);
							
							//将json对象反序列化
							ObjectMapper mapper = new ObjectMapper();
							Message message = null;
							
							try {
								
								message = mapper.readValue(info.result,Message.class);
							}catch (IOException e) {
								e.printStackTrace();
							}
							if(message !=null){
								
								try {
									//解析将json数组解析成List对象
									List<Joblist> joblists = mapper.readValue(message.getData(),new TypeReference<List<Joblist>>(){});
									//更新适配器(注意应该更新适配器的List集合,才能实现立即刷新)
									adapter.addMore(joblists);
									
									adapter.notifyDataSetChanged();
									
									showLongToast(4,"删除收藏项成功!");
									
								}catch (IOException e) {
									e.printStackTrace();
								}
							}else{
								showLongToast(3,"未解析到对象!");
							}
							
							
						}
					});
					break;

				default:
					break;
				}
				return false;
			}
		});
	}
	//创建SwipeMenuCreator
	public void createMenu() {
		//1.创建SwipeMenuCreator
		SwipeMenuCreator menuCreator = new SwipeMenuCreator() {
			
			@Override
			public void create(SwipeMenu menu) {
			
				//2.创建SwipeMenuItem对象
				SwipeMenuItem deleteItem = new SwipeMenuItem(CollectActivity.this);
				//3.设置SwipeMenuItem的相关属性
				deleteItem.setIcon(R.drawable.ic_delete);
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,0x3F, 0x25)));
				deleteItem.setWidth(dp2px(90));
				//4.添加菜单项
				menu.addMenuItem(deleteItem);
			}
		};
		//5.为SwipeMenuListView设置SwipeMenuCreator
		menuListView.setMenuCreator(menuCreator);
	}
	
	/**
	 * 获得数据源
	 */
	private void loadData(){
		
		HttpUtils httpUtils = new HttpUtils();
		//设置缓存
		httpUtils.configCurrentHttpCacheExpiry(0);
		httpUtils.configDefaultHttpCacheExpiry(0);
		//列表地址
		String url = String.format(URLs.COLLECT_VIEW,userId);
		httpUtils.send(HttpMethod.GET, url,new RequestCallBack<String>() {

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
		
				super.onLoading(total, current, isUploading);
				dialog.show();
			}
			
			
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				
				if(dialog!=null && dialog.isShowing()){
					dialog.dismiss();
				}
				
				showLongToast(3,"收藏列表获取失败");
			}

			@Override
			public void onSuccess(ResponseInfo<String> info) {
				if(dialog!=null && dialog.isShowing()){
					dialog.dismiss();
				}
				LogUtils.e("服务端传过来的数据:" + info.result);
				
				//将json对象反序列化
				ObjectMapper mapper = new ObjectMapper();
				Message message = null;
				
				try {
					
					message = mapper.readValue(info.result,Message.class);
				}catch (IOException e) {
					e.printStackTrace();
				}
				if(message !=null){
					
					try {
						//解析将json数组解析成List对象
						List<Joblist> joblists = mapper.readValue(message.getData(),new TypeReference<List<Joblist>>(){});
						adapter.addMore(joblists);
						
						adapter.notifyDataSetChanged();
						
					}catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					showLongToast(3,"未解析到对象!");
				}
				
				
			}
		});
		
	}

	private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
	
	@OnClick(R.id.back)
	public void back(View v){
		IntentUtil.start_activity(CollectActivity.this, HomeActivity.class);
		CollectActivity.this.finish();
	}

}
