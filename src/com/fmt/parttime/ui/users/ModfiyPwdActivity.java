package com.fmt.parttime.ui.users;

import java.io.IOException;

import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.map.ObjectMapper;

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
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
/**
 * 修改密码
 * @author Administrator
 *
 */

public class ModfiyPwdActivity extends BaseActivity {
	
	@ViewInject(R.id.old_password)
	EditText etoldpwd;
	@ViewInject(R.id.register_password)
	EditText etnewpwd;
	@ViewInject(R.id.register_confirm_password)
	EditText etnewrepwd;
	
	private String username;
	
	private LoadingDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modpassword_layout);
		
		ViewUtils.inject(this);
		
		dialog = new LoadingDialog(ModfiyPwdActivity.this);
		
		username = PreferencesUtils.getString(this,"username","");
	}
	@OnClick(R.id.submit)
	public void modify(View v){
		//获得忘记密码的信息
		String oldpwd = etoldpwd.getText().toString();
		String newpwd = etnewpwd.getText().toString();
		String newrepwd = etnewrepwd.getText().toString();
		
		//判断注册信息的合法性
		if(TextUtils.isEmpty(oldpwd)){
			showShortToast(1,"旧密码必填!");
			return ;
		}
		if(TextUtils.isEmpty(newpwd)){
			showShortToast(1,"新密码必填!");
			return ;
		}
		if(TextUtils.isEmpty(newrepwd)){
			showShortToast(1,"确认密码必填!");
			return ;
		}
		if(!newpwd.equals(newrepwd)){
			showShortToast(1,"两次输入的密码不一致!");
			return ;
		}
		
		HttpUtils httpUtils = new HttpUtils();//进行异步操作
		//封装请求参数
		RequestParams params = new RequestParams();
		params.addBodyParameter("action","modfiypwd");
		params.addBodyParameter("usersName",username);
		params.addBodyParameter("usersPwd",newpwd);
		params.addBodyParameter("oldPwd",oldpwd);
		
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
						IntentUtil.start_activity(ModfiyPwdActivity.this,LoginActivity.class,nameValuePair);
						ModfiyPwdActivity.this.finish();
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
		IntentUtil.start_activity(ModfiyPwdActivity.this, SettingActivity.class);
		ModfiyPwdActivity.this.finish();
	}

}
