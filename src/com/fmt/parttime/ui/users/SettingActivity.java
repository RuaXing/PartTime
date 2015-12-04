package com.fmt.parttime.ui.users;

import org.apache.http.message.BasicNameValuePair;
import com.fmt.parttime.R;
import com.fmt.parttime.common.utils.DataCleanManager;
import com.fmt.parttime.common.utils.IntentUtil;
import com.fmt.parttime.common.utils.PreferencesUtils;
import com.fmt.parttime.common.utils.UpdateManager;
import com.fmt.parttime.ui.BaseActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
/**
 * 用户设置界面
 * @author Administrator
 *
 */

public class SettingActivity extends BaseActivity {
	
	@ViewInject(R.id.settings_cache_size)
	TextView tvcache;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_layout);
		
		ViewUtils.inject(this);
		try {
			tvcache.setText(DataCleanManager.getTotalCacheSize(getApplicationContext()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//退出账号
	@OnClick(R.id.settings_remove_login)
	public void exitnum(View v){
		//清除已有信息
		PreferencesUtils.clear(SettingActivity.this);
		//返回登录页面
		BasicNameValuePair pair = new BasicNameValuePair("item","home");
		IntentUtil.start_activity(SettingActivity.this,LoginActivity.class,pair);
		SettingActivity.this.finish();
	}
	//修改密码
	@OnClick(R.id.settings_change_password)
	public void modfiypwd(View v){
		//返回修改密码页面
	    IntentUtil.start_activity(SettingActivity.this,ModfiyPwdActivity.class);
	    SettingActivity.this.finish();
	}
	//退出应用
	@OnClick(R.id.settings_exit)
	public void exitApp(View v){
		AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
		builder.setTitle("提醒").setIcon(R.drawable.chat_report).setMessage("确定要退出当前应用吗?")
		       .setPositiveButton("确定",new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				
					//返回桌面
					Intent intent = new Intent(Intent.ACTION_MAIN);
					intent.addCategory(Intent.CATEGORY_HOME);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//重点
					startActivity(intent);
					//结束当前应用程序进程
					android.os.Process.killProcess(android.os.Process.myPid());
					
				}
			}).setNegativeButton("取消",null).show();
	}
	//重设电话
	@OnClick(R.id.settings_resetphone)
	public void resetphone(View v){
		//返回修改密码页面
	    IntentUtil.start_activity(SettingActivity.this,ModfiyPhoneActivity.class);
	    SettingActivity.this.finish();
	}

	//用户反馈
	@OnClick(R.id.settings_user_feedback)
	public void feedback(View v){
		//返回修改密码页面
	    IntentUtil.start_activity(SettingActivity.this,FreeBackActivity.class);
	    SettingActivity.this.finish();
	}
	

	//功能介绍
	@OnClick(R.id.settings_function_introduce)
	public void functionIntro(View v){
		//返回修改密码页面
	    IntentUtil.start_activity(SettingActivity.this,AboutActivity.class);
	    SettingActivity.this.finish();
	}
	
	//清空缓存
	@OnClick(R.id.clean_cache)
	public void cleancache(View v){
		DataCleanManager.clearAllCache(getApplicationContext());
		tvcache.setText("0.00M");
		showShortToast(1,"清理缓存完毕!");
	}
	//检查更新
	@OnClick(R.id.settings_check_update)
	public void checkupdate(View v){
		UpdateManager updateManager = new UpdateManager(SettingActivity.this);
		updateManager.checkUpdate();
	}
	
	@OnClick(R.id.back)
	public void back(View v){
		IntentUtil.start_activity(SettingActivity.this, HomeActivity.class);
		SettingActivity.this.finish();
	}
}
