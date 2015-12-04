package com.fmt.parttime.ui.users;

import java.io.IOException;
import org.codehaus.jackson.map.ObjectMapper;

import com.fmt.parttime.MainActivity;
import com.fmt.parttime.R;
import com.fmt.parttime.common.utils.IntentUtil;
import com.fmt.parttime.common.utils.PreferencesUtils;
import com.fmt.parttime.config.URLs;
import com.fmt.parttime.entity.Message;
import com.fmt.parttime.entity.Users;
import com.fmt.parttime.ui.BaseActivity;
import com.fmt.parttime.ui.LoadingDialog;
import com.fmt.parttime.ui.job.JobListActivity;
import com.fmt.parttime.ui.job.JobSearchActivity;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
/**
 * 登录界面
 * @author Administrator
 *
 */
public class LoginActivity extends BaseActivity {
	
	@ViewInject(R.id.login_username)
	private EditText etphone;
	@ViewInject(R.id.login_password)
	private EditText etpwd;
	
	private LoadingDialog loadingDialog;//在异步加载时进行提醒
	
	private String item;//判断跳转的页面选项
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		loadingDialog = new LoadingDialog(LoginActivity.this,"加载中,请稍等...");
		item = getIntent().getStringExtra("item");
		
		//进行空间与事件的判断
		ViewUtils.inject(this);
	}
	//用户登录
	@OnClick(R.id.login_submit)
	public void login(View v){
		//获得登录信息
		String userphone = etphone.getText().toString();
		String userpwd = etpwd.getText().toString();
		//判断登录信息的合法性
		if(TextUtils.isEmpty(userphone)){
			showShortToast(1,"用户名必填!");
			return ;
		}
		if(TextUtils.isEmpty(userpwd)){
			showShortToast(1,"密码必填!");
			return ;
		}
		//进行登录验证(HttpUtils异步处理)
		HttpUtils httpUtils = new HttpUtils();
		//封装请求参数
		RequestParams params = new RequestParams();
		params.addBodyParameter("action","login");
		params.addBodyParameter("usersName",userphone);
		params.addBodyParameter("usersPwd",userpwd);
		//进行http请求
		httpUtils.send(HttpRequest.HttpMethod.POST,URLs.USERS, params,new RequestCallBack<String>() {

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
		
				super.onLoading(total, current, isUploading);
				loadingDialog.show();
			}
			
			@Override
			public void onFailure(HttpException exception, String msg) {
				
				if(loadingDialog !=null && loadingDialog.isShowing()){
					loadingDialog.dismiss();
				}
				//显示错误的说明
				showLongToast(3,msg);
			}

			@Override
			public void onSuccess(ResponseInfo<String> info) {
				
				if(loadingDialog !=null && loadingDialog.isShowing()){
					loadingDialog.dismiss();
				}
				//将json对象反序列化
				
				ObjectMapper mapper = new ObjectMapper();
				Message message = null;
				Users user = null;
				try {
					//获得Message和Users对象
					message =  mapper.readValue(info.result,Message.class);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				//判断是否解析成功
				if(message !=null){
					
					int statuId = message.getStatuId();
					
					try {
						user = mapper.readValue(message.getData(),Users.class);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					if(statuId > 0){
						showLongToast(4,"登录成功!");
						//保持用户登录成功后的信息SharedPreferences(轻量级,最佳选择)
						PreferencesUtils.putInt(LoginActivity.this,"userId",user.getUsersID());
						PreferencesUtils.putString(LoginActivity.this, "username",user.getUsersName());
						//若登录成功,则跳转至用户主页面
						if(item.equals("home")){
							IntentUtil.start_activity(LoginActivity.this,HomeActivity.class);
							LoginActivity.this.finish();
						}else if(item.equals("part") || item.equals("job")){
							IntentUtil.start_activity(LoginActivity.this,JobListActivity.class);
							LoginActivity.this.finish();
						}else if(item.equals("search")){
							IntentUtil.start_activity(LoginActivity.this,JobSearchActivity.class);
							LoginActivity.this.finish();
						}
						
					}else{
						showLongToast(3,"登录失败!");
					}
				}else{
					showLongToast(3,"未解析到对象!");
				}
			}
		});
		
	}
	
	@OnClick(R.id.login_register)
	public void register(View v){
	
		IntentUtil.start_activity(LoginActivity.this,RegisterActivity.class);
		LoginActivity.this.finish();
	}
	
	@OnClick(R.id.login_forget_password)
	public void forget(View v){
		
		IntentUtil.start_activity(LoginActivity.this,ForgetActivity.class);
		LoginActivity.this.finish();
	}
	
	@OnClick(R.id.back)
	public void back(View v){
		//调整回主页面
		IntentUtil.start_activity(LoginActivity.this,MainActivity.class);
		LoginActivity.this.finish();
	}

}
