package com.fmt.parttime.ui.users;

import android.os.Bundle;
import android.view.View;

import com.fmt.parttime.R;
import com.fmt.parttime.common.utils.IntentUtil;
import com.fmt.parttime.ui.BaseActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class AboutActivity extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		ViewUtils.inject(this);
		
	}
	
	@OnClick(R.id.back)
	public void back(View v){
		IntentUtil.start_activity(AboutActivity.this,SettingActivity.class);
		AboutActivity.this.finish();
	}

}
