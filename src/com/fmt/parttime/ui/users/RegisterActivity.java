package com.fmt.parttime.ui.users;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.message.BasicNameValuePair;
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

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
/**
 * 注册界面
 * @author Administrator
 *
 */
public class RegisterActivity extends BaseActivity {
	
	@ViewInject(R.id.register_username)
	private EditText etphone;
	@ViewInject(R.id.register_validate_code)
	private EditText etvalitecode;
	@ViewInject(R.id.register_password)
	private EditText etpwd;
	@ViewInject(R.id.register_confirm_password)
	private EditText etrepwd;
	
	private LoadingDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userregist_layout);
		
		dialog = new LoadingDialog(RegisterActivity.this,"加载中,请稍候...");
		ViewUtils.inject(this);
	}
	
	@OnClick(R.id.register_submit)
	public void register(View v){
		//获得注册的信息
		String userphone = etphone.getText().toString();
		String userpwd = etpwd.getText().toString();
		String userrepwd = etrepwd.getText().toString();
		String valitecode = etvalitecode.getText().toString();
		//判断注册信息的合法性
		if(TextUtils.isEmpty(userphone)){
			showShortToast(1,"用户名必填!");
			return ;
		}
		if(TextUtils.isEmpty(userpwd)){
			showShortToast(1,"用户密码必填!");
			return ;
		}
		if(TextUtils.isEmpty(userrepwd)){
			showShortToast(1,"确认密码必填!");
			return ;
		}
		if(!userpwd.equals(userrepwd)){
			showShortToast(1,"两次输入的密码不一致!");
			return ;
		}
		
		HttpUtils httpUtils = new HttpUtils();//进行异步操作
		
		RequestParams params = new RequestParams();
		params.addBodyParameter("action","register");
		params.addBodyParameter("usersName",userphone);
		params.addBodyParameter("usersPwd",userrepwd);
		params.addBodyParameter("usersInvalitCode",valitecode);
		params.addBodyParameter("usersIsForgot","0");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = format.format(new Date());
		params.addBodyParameter("usersRegDate",date);
		
		httpUtils.send(HttpRequest.HttpMethod.POST,URLs.USERS, params,new RequestCallBack<String>() {

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				
				super.onLoading(total, current, isUploading);
				dialog.show();
			}
			
			@Override
			public void onFailure(HttpException arg0, String msg) {
				
				if(dialog !=null && dialog.isShowing()){
					dialog.dismiss();
				}
				//显示错误的说明
				showLongToast(3,msg);
			}

			@Override
			public void onSuccess(ResponseInfo<String> info) {
				
				if(dialog !=null && dialog.isShowing()){
					dialog.dismiss();
				}
				
				LogUtils.e(info.result);
				
				//将json数据反序列化
				ObjectMapper mapper = new ObjectMapper();
				Message message = null;
				Users user = null;
				
				try {
					message = mapper.readValue(info.result,Message.class);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				if(message !=null){
					//注册成功
					if(message.getStatuId()>0){
						
						try {
							//获得注册的用户
							user = mapper.readValue(message.getData(),Users.class);
						} catch (IOException e) {
							e.printStackTrace();
						}
						//将用户信息写入到存储中
						PreferencesUtils.putInt(RegisterActivity.this,"userId",user.getUsersID());
						PreferencesUtils.putString(RegisterActivity.this, "username",user.getUsersName());
						showLongToast(4,"注册成功!");
						//跳转至列表页面
						BasicNameValuePair nameValuePair = new BasicNameValuePair("item","home");
						IntentUtil.start_activity(RegisterActivity.this,LoginActivity.class,nameValuePair);
						RegisterActivity.this.finish();
					}//注册失败
					else{
						showLongToast(3,"注册失败!");
					}
					
				}else{
					showLongToast(3,"注册失败");
				}
				
			}
		});
	}
	@OnClick(R.id.back)
	public void back(View v){
		//调整回主页面
		IntentUtil.start_activity(RegisterActivity.this,MainActivity.class);
		RegisterActivity.this.finish();
	}

}
