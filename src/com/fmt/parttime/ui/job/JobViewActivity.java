package com.fmt.parttime.ui.job;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.map.ObjectMapper;

import com.fmt.parttime.R;
import com.fmt.parttime.common.utils.IntentUtil;
import com.fmt.parttime.common.utils.PreferencesUtils;
import com.fmt.parttime.config.URLs;
import com.fmt.parttime.entity.Joblist;
import com.fmt.parttime.entity.Message;
import com.fmt.parttime.ui.BaseActivity;
import com.fmt.parttime.ui.LoadingDialog;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
/**
 * 工作详细界面
 * @author Administrator
 *
 */
public class JobViewActivity extends BaseActivity {
	
	@ViewInject(R.id.job_theme)
	TextView job_theme;
	@ViewInject(R.id.job_pay)
	TextView job_pay;
	@ViewInject(R.id.job_distance)
	TextView job_distance;
	@ViewInject(R.id.publishTime)
	TextView publishTime;
	@ViewInject(R.id.job_company)
	TextView job_company;
	@ViewInject(R.id.paytime)
	TextView paytime;
	@ViewInject(R.id.job_requestCount)
	TextView job_requestCount;
	@ViewInject(R.id.job_time)
	TextView job_time;
	@ViewInject(R.id.job_address)
	TextView job_address;
	@ViewInject(R.id.inerview_time)
	TextView inerview_time;
	@ViewInject(R.id.inerview_address)
	TextView inerview_address;
	@ViewInject(R.id.job_detailshow)
	TextView job_detailshow;
	@ViewInject(R.id.job_contact)
	TextView job_contact;
	@ViewInject(R.id.job_phone)
	TextView job_phone;
	@ViewInject(R.id.applyCount)
	TextView applycount;
	@ViewInject(R.id.aplayCount)
	TextView aplayCount;
	@ViewInject(R.id.save)
	Button btsave;
	@ViewInject(R.id.callPhone)
	Button btapply;
	
	private String jobId;
	private int userID ;
	private LoadingDialog dialog;
	String jobInfo = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jobdetail_layout);
		
		ViewUtils.inject(this);
		
		dialog = new LoadingDialog(this);
		
		//获取传过来的jobId
		Intent intent = getIntent();
		jobId = intent.getStringExtra("jobId");
		//获得搜索关键字
		if(intent.getStringExtra("jobInfor") !=null){
			jobInfo = getIntent().getStringExtra("jobInfor");
		}
		//判断当前用户是否登录
		userID  = PreferencesUtils.getInt(JobViewActivity.this,"userId",0);
		if(userID == 0){
			btsave.setEnabled(false);
			btapply.setEnabled(false);
		}
		
		loadapplyCount();
		loadagreeCount();
		//加载数据
		loadData();
	}
	/**
	 * 获得申请该职位的人数
	 */
	private void loadapplyCount() {
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.configDefaultHttpCacheExpiry(0);
		httpUtils.configCurrentHttpCacheExpiry(0);
		//使用占位符拼接地址
		String url = String.format(URLs.JOBLIST_APPLYCOUNT,jobId);
		httpUtils.send(HttpMethod.GET,url,new RequestCallBack<String>() {
			
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
				showLongToast(3,"兼职申请人数获取失败");
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				
				ObjectMapper mapper = new ObjectMapper();
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				Message message = null;
				try {
					message = mapper.readValue(responseInfo.result,
							Message.class);
					Log.e("--test---", responseInfo.result);
				} catch (IOException e) {
					e.printStackTrace();
				}
				applycount.setText("已申请" + message.getData() + "人");

			}
			
		});
		
	}
	/**
	 * 获得该职位以录用的人数
	 */
	private void loadagreeCount() {
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.configDefaultHttpCacheExpiry(0);
		httpUtils.configCurrentHttpCacheExpiry(0);
		//使用占位符拼接地址
		String url = String.format(URLs.APPLY_AGREECOUNT,jobId);
		httpUtils.send(HttpMethod.GET,url,new RequestCallBack<String>() {
			
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
				showLongToast(3,"录用人数获取失败");
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				
				ObjectMapper mapper = new ObjectMapper();
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				Message message = null;
				try {
					message = mapper.readValue(responseInfo.result,
							Message.class);
					Log.e("--test---", responseInfo.result);
				} catch (IOException e) {
					e.printStackTrace();
				}
				aplayCount.setText("已录用" + message.getData() + "人");

			}
			
		});
		
	}
	/**
	 * 加载数据(异步加载信息)
	 */
	private void loadData() {
		
		HttpUtils httpUtils = new HttpUtils();
		//使用占位符拼接地址
		String url = String.format(URLs.JOBLIST_VIEW,jobId);
		httpUtils.send(HttpMethod.GET,url,new RequestCallBack<String>() {
			
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
				showLongToast(3,"兼职详情获取失败");
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				
				ObjectMapper mapper = new ObjectMapper();
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				Message message = null;
				try {
					message = mapper.readValue(responseInfo.result,
							Message.class);
					Log.e("--test---", responseInfo.result);
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					Joblist job = mapper.readValue(message.getData(),
							Joblist.class);
					//设置相应的工作信息
					job_theme.setText(job.jobTitle);
					job_pay.setText(job.jobPayFee);
					
					job_distance.setText(job.jobPostAddress);
					
					publishTime.setText(job.jobPostDate);
					job_company.setText(job.jobPostCompany);
					paytime.setText(job.jobJSRQ);
					job_requestCount.setText(job.jobZPRS+"");
					job_time.setText(job.jobGZRQ);
					job_address.setText(job.jobGZDZ);
					inerview_time.setText(job.jobMSSJ);
					inerview_address.setText(job.jobMSDZ);
					job_detailshow.setText(job.jobZWMS);
					//判断该用户是否登录
					if (userID > 0) {
						job_phone.setText(job.jobPhone);
					} else {
						job_phone.setText("联系信息请登录后查看");
					}

				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			
		});
		
	}

	//进行申请,在申请成功后自动拨打电话
	@OnClick(R.id.callPhone)
	public void callphone(View v){
		applyjob();
	}
	//申请工作
	public void applyjob() {
		if(!job_phone.getText().toString().trim().equals("联系信息请登录后查看")){
			
			//加入申请列表
			//获取收藏时间
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = format.format(new Date());
			
			//进行http异步操作
			HttpUtils httpUtils = new HttpUtils();
			
			RequestParams params = new RequestParams();
			params.addBodyParameter("applyDate",time);
			params.addBodyParameter("usersID",userID + "");
			params.addBodyParameter("jobID",jobId);
			params.addBodyParameter("applyStatus","0");
			
			httpUtils.send(HttpMethod.POST,URLs.APPLY_ADD, params,new RequestCallBack<String>() {
				
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
					showLongToast(3,"收藏失败");
					
				}

				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					//将json对象反序列化
					ObjectMapper mapper = new ObjectMapper();
					if (dialog != null && dialog.isShowing()) {
						dialog.dismiss();
					}
					Message message = null;
					try {
						message = mapper.readValue(responseInfo.result,
								Message.class);
						Log.e("--test---", responseInfo.result);
					} catch (IOException e) {
						e.printStackTrace();
					}
					//判断收藏是否成功
					if(message !=null){
						
						if(message.getStatuId() ==1){
							showShortToast(4,message.getMsg());
							phoneConnect();
						}else if(message.getStatuId() ==2){
							showShortToast(4,message.getMsg());
						}else{
							showShortToast(3,message.getMsg());
						}
					
					}else{
						showLongToast(3,"对象解析错误!");
					}
				}
			});
		}
	}
	//电话联系
	public void phoneConnect() {
		//打电话意图
		Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:" + job_phone.getText().toString().trim()));
		//打电话的地址
		startActivity(intent);
	}
	//收藏该兼职信息
	@OnClick(R.id.save)
	public void save(View v){
		
		//获取收藏时间
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = format.format(new Date());
		
		//进行http异步操作
		HttpUtils httpUtils = new HttpUtils();
		
		RequestParams params = new RequestParams();
		params.addBodyParameter("jobCollectDate",time);
		params.addBodyParameter("usersID",userID + "");
		params.addBodyParameter("jobID",jobId);
		
		httpUtils.send(HttpMethod.POST,URLs.COLLECT_ADD, params,new RequestCallBack<String>() {
			
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
				showLongToast(3,"收藏失败");
				
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				//将json对象反序列化
				ObjectMapper mapper = new ObjectMapper();
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				Message message = null;
				try {
					message = mapper.readValue(responseInfo.result,
							Message.class);
					Log.e("--test---", responseInfo.result);
				} catch (IOException e) {
					e.printStackTrace();
				}
				//判断收藏是否成功,以及是否已经收藏
				if(message !=null){
					
					if(message.getStatuId() ==1){
						showShortToast(4,message.getMsg());
					}else if(message.getStatuId() ==2){
						showShortToast(4,message.getMsg());
						btsave.setClickable(false);
					}else{
						showShortToast(3,message.getMsg());
					}
				
				}else{
					showLongToast(3,"对象解析错误!");
				}
			}
		});
	}
	//返回事件
	@OnClick(R.id.back)
	public void back(View v){
		
		BasicNameValuePair jobInfor = new BasicNameValuePair("jobInfor",jobInfo);
		IntentUtil.start_activity(JobViewActivity.this,JobListActivity.class,jobInfor);
		JobViewActivity.this.finish();

	}
}
