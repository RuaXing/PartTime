package com.fmt.parttime.ui.job;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import android.R.integer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.fmt.parttime.MainActivity;
import com.fmt.parttime.R;
import com.fmt.parttime.common.utils.IntentUtil;
import com.fmt.parttime.common.utils.LogUtil;
import com.fmt.parttime.common.utils.PreferencesUtils;
import com.fmt.parttime.config.URLs;
import com.fmt.parttime.entity.Joblist;
import com.fmt.parttime.entity.Message;
import com.fmt.parttime.ui.BaseActivity;
import com.fmt.parttime.ui.LoadingDialog;
import com.leelistview.view.LeeListView;
import com.leelistview.view.LeeListView.IXListViewListener;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
/**
 * 兼职邀请列表
 * 1.IXListView开源框架的使用
 *   1.获得数据源(服务器端获取)
 *   2.自定义适配器
 *   3.
 * @author Administrator
 *
 */
public class JobListActivity extends BaseActivity implements IXListViewListener {
	
	@ViewInject(R.id.my_listview)
	private LeeListView leeListView;
	
	//数据源
	List<Joblist> mdatas = new ArrayList<>();
	//适配器
	JobListAdapter adapter;
	
	private LoadingDialog dialog;
	
	Handler handler = new Handler();
	//刷新的时间
	private static String refreshtime = "";
	//刷新的记录
	private static int refreshCount = 0;
	private int pageno;
	
	String cityName = "";
	String jobInfo = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jobinvitation_layout);
		
		ViewUtils.inject(this);
		dialog = new LoadingDialog(this);
		//获得定位城市
		cityName = PreferencesUtils.getString(JobListActivity.this,"city","");
		//获得搜索工作关键字
		if(getIntent().getStringExtra("jobInfor") !=null){
			jobInfo = getIntent().getStringExtra("jobInfor");
		}
		
		adapter = new JobListAdapter(this,mdatas);
		leeListView.setAdapter(adapter);
		loadData();
		
		leeListView.setPullLoadEnable(true,"为您推荐" + refreshCount + "工作");
		leeListView.setPullRefreshEnable(true);
		leeListView.setXListViewListener(this);//设置监听器(核心
		//设置点击事件
		leeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				//获得点击想的jobID
				Joblist joblist = (Joblist) adapter.getItem(position-1);
				//跳转至工作详情页面
				BasicNameValuePair pair = new BasicNameValuePair("jobId",joblist.getJobID() + "");
				//判断是否是搜索工作列表
				if(!jobInfo.equals("")){
					BasicNameValuePair jobInfor = new BasicNameValuePair("jobInfor",jobInfo);
					IntentUtil.start_activity(JobListActivity.this,JobViewActivity.class,pair,jobInfor);
				}else{
					IntentUtil.start_activity(JobListActivity.this,JobViewActivity.class,pair);
				}
				
				
			}
		});
		
	}
	/**
	 * 获得数据源
	 */
	private void loadData(){
		
		HttpUtils httpUtils = new HttpUtils();
		pageno ++;
		//列表地址
		String url = String.format(URLs.JOBLIST,pageno + "",cityName,jobInfo);
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
				
				showLongToast(3,"兼职列表获取失败");
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
						mdatas = joblists;
						//更新数据
						refreshCount = joblists.size();
						//更新的时间
						SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
						refreshtime = format.format(new Date());
						//更新适配器
						adapter.addMore(mdatas);
						adapter.notifyDataSetChanged();
						//停止刷新
						stopRefresh();
						
					}catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					showLongToast(3,"未解析到对象!");
				}
				
				
			}
		});
	}

	@Override
	public void onRefresh() {//刷新
		
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				
				//更新数据
				loadData();
			}
		});
	}
	//停止更新UI
	protected void stopRefresh() {
		
		leeListView.stopRefresh();
		leeListView.stopLoadMore();
		leeListView.setRefreshTime(refreshtime);
	}
	//加载更多
	@Override
	public void onLoadMore() {
		
		handler.post(new Runnable() {//更新ui
			
			@Override
			public void run() {
				//更新数据
				loadData();

			}
		});
	}
	
	@OnClick(R.id.back)
	public void back(View v){
		IntentUtil.start_activity(JobListActivity.this,MainActivity.class);
		JobListActivity.this.finish();
	}

}
