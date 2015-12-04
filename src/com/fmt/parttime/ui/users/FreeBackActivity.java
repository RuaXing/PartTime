package com.fmt.parttime.ui.users;

import java.io.IOException;

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
 * 用户反馈
 * @author Administrator
 *
 */

public class FreeBackActivity extends BaseActivity {
	
	@ViewInject(R.id.remark)
	EditText etcontent;
	
	private LoadingDialog dialog;
	private int userId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userfeedback_layout);
		
		ViewUtils.inject(this);
		
		dialog = new LoadingDialog(this);
		userId = PreferencesUtils.getInt(FreeBackActivity.this,"userId",0);
	}
	
	@OnClick(R.id.commit)
	public void commit(View v){
		//获得信息
		String content = etcontent.getText().toString();
		
		//判断注册信息的合法性
		if(TextUtils.isEmpty(content)){
			showShortToast(1,"用户名反馈必填!");
			return ;
		}
		
		HttpUtils httpUtils = new HttpUtils();//进行异步操作
		//封装请求参数
		RequestParams params = new RequestParams();
		params.addBodyParameter("usersID",userId + "");
		params.addBodyParameter("commitDesc",content);
	
		
		httpUtils.send(HttpRequest.HttpMethod.POST,URLs.COMMIT_ADD, params,new RequestCallBack<String>() {

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
						IntentUtil.start_activity(FreeBackActivity.this,SettingActivity.class);
						FreeBackActivity.this.finish();
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
		IntentUtil.start_activity(FreeBackActivity.this, SettingActivity.class);
		FreeBackActivity.this.finish();
	}

}
