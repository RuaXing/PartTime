package com.fmt.parttime.ui.users;

import java.io.IOException;

import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.map.ObjectMapper;

import com.fmt.parttime.MainActivity;
import com.fmt.parttime.R;
import com.fmt.parttime.common.utils.IntentUtil;
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
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
/**
 * 忘记密码界面
 * @author Administrator
 *
 */
public class ForgetActivity extends BaseActivity {
	
	@ViewInject(R.id.register_telephone)
	private EditText etphone;
	@ViewInject(R.id.register_password)
	private EditText etpwd;
	@ViewInject(R.id.register_confirm_password)
	private EditText etrepwd;
	
	private LoadingDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_password);
		
		dialog = new LoadingDialog(ForgetActivity.this);
		//进行控件与事件的判断
		ViewUtils.inject(this);
	}
	
	@OnClick(R.id.register_submit)
	public void reset(View v){
		//获得忘记密码的信息
		String userphone = etphone.getText().toString();
		String userpwd = etpwd.getText().toString();
		String userrepwd = etrepwd.getText().toString();
		
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
		//封装请求参数
		RequestParams params = new RequestParams();
		params.addBodyParameter("action","forget");
		params.addBodyParameter("usersName",userphone);
		params.addBodyParameter("usersPwd",userpwd);
		params.addBodyParameter("usersIsForgot","1");
		
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
						IntentUtil.start_activity(ForgetActivity.this,LoginActivity.class,nameValuePair);
						ForgetActivity.this.finish();
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
		//调整回主页面
		IntentUtil.start_activity(ForgetActivity.this,MainActivity.class);
		ForgetActivity.this.finish();
	}

}
