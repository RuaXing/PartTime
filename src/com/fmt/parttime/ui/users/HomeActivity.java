package com.fmt.parttime.ui.users;
import com.fmt.parttime.R;
import com.fmt.parttime.common.utils.IntentUtil;
import com.fmt.parttime.common.utils.PreferencesUtils;
import com.fmt.parttime.ui.BaseActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * 用户主页面
 * @author Administrator
 *
 */
public class HomeActivity extends BaseActivity {
	
	private String username;
	
	@ViewInject(R.id.user_name)
	TextView tvusername;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_home);
		
		ViewUtils.inject(this);
		
		username = PreferencesUtils.getString(HomeActivity.this,"username","");
		tvusername.setText(username + "欢迎你!");
	}
	//我的收藏
	@OnClick(R.id.tvsave)
	public void showSave(View v){
		IntentUtil.start_activity(HomeActivity.this,CollectActivity.class);
		HomeActivity.this.finish();
	}
	//我的申请
	@OnClick(R.id.k1)
	public void showApply(View v){
		IntentUtil.start_activity(HomeActivity.this,ApplyActivity.class);
		HomeActivity.this.finish();
	}
	//设置
	@OnClick(R.id.set)
	public void setting(View v){
		IntentUtil.start_activity(HomeActivity.this,SettingActivity.class);
		HomeActivity.this.finish();
	}

}
