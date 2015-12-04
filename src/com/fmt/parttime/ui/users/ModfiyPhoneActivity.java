package com.fmt.parttime.ui.users;

import java.io.IOException;

import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.map.ObjectMapper;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.fmt.parttime.R;
import com.fmt.parttime.common.utils.IntentUtil;
import com.fmt.parttime.common.utils.PreferencesUtils;
import com.fmt.parttime.config.URLs;
import com.fmt.parttime.entity.Message;
import com.fmt.parttime.ui.BaseActivity;
import com.fmt.parttime.ui.LoadingDialog;
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


/**
 * 更换手机号
 * @author Administrator
 *
 */

public class ModfiyPhoneActivity extends BaseActivity {
	@ViewInject(R.id.register_telephone)
	EditText etphone;
	@ViewInject(R.id.register_password)
	EditText etpwd;
	
	private String username;
	
	private LoadingDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_phone);
		
		ViewUtils.inject(this);
		
		dialog = new LoadingDialog(ModfiyPhoneActivity.this);
		
		username = PreferencesUtils.getString(this,"username","");
	}
	
	@OnClick(R.id.register_submit)
	public void modify(View v){
		//获得忘记密码的信息
		String phone = etphone.getText().toString();
		String pwd = etpwd.getText().toString();
		
		
		//判断注册信息的合法性
		if(TextUtils.isEmpty(phone)){
			showShortToast(1,"电话号码必填!");
			return ;
		}
		if(TextUtils.isEmpty(pwd)){
			showShortToast(1,"新密码必填!");
			return ;
		}
		
		HttpUtils httpUtils = new HttpUtils();//进行异步操作
		//封装请求参数
		RequestParams params = new RequestParams();
		params.addBodyParameter("action","modfiyphone");
		params.addBodyParameter("usersName",phone);
		params.addBodyParameter("usersPwd",pwd);
		params.addBodyParameter("oldusername",username);
		
		httpUtils.send(HttpRequest.HttpMethod.POST,URLs.USERS, params,new RequestCallBack<String>() {

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				
				super.onLoading(total, current, isUploading);
				dialog.show();
			}
			
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				
				if(dialog !=null && dialog.isShowing()){
					dialog.dismiss();
				}
				
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
				
				try {
					message = mapper.readValue(info.result,Message.class);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				if(message !=null){
					//修改成功
					if(message.getStatuId()==1){
						showLongToast(4,message.getMsg());
						
						//跳转到登录界面,重新登录
						BasicNameValuePair nameValuePair = new BasicNameValuePair("item","home");
						IntentUtil.start_activity(ModfiyPhoneActivity.this,LoginActivity.class,nameValuePair);
						ModfiyPhoneActivity.this.finish();
					}//修改失败
					else{
						showLongToast(3,message.getMsg());
					}
					
				}else{
					showLongToast(3,"对不起该用户不存在");
				}
				
			}
		});
	}
	
	@OnClick(R.id.back)
	public void back(View v){
		IntentUtil.start_activity(ModfiyPhoneActivity.this,SettingActivity.class);
		ModfiyPhoneActivity.this.finish();
	}

}
