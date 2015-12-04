package com.fmt.parttime.ui.job;


import java.util.Random;

import org.apache.http.message.BasicNameValuePair;

import com.fmt.keywordsflow.view.KeywordsFlow;
import com.fmt.parttime.MainActivity;
import com.fmt.parttime.R;
import com.fmt.parttime.common.utils.IntentUtil;
import com.fmt.parttime.ui.BaseActivity;
import com.fmt.parttime.ui.LoadingDialog;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
/**
 * 工作搜索页面
 * @author Administrator
 *
 */

public class JobSearchActivity extends BaseActivity implements OnClickListener,SensorEventListener {
	
	
	//数据源
	public  String[] keywords= { "客服", "月薪", "服务生", "实习", "兼职",
			"案件","工程师","架构师","项目经理","演出","按件","家教","模特","礼仪","日新","年薪","其他","派单","设计"
			,"翻译","文员"};
		
	@ViewInject(R.id.keywordsFlow)
	KeywordsFlow keywordsFlow;
	@ViewInject(R.id.search_text)
	EditText etsearch;
	
	// 速度阈值，当摇晃速度达到这值后产生作用
	private static final int SPEED_SHRESHOLD = 2000;
	// 两次检测的时间间隔
	private static final int UPTATE_INTERVAL_TIME = 70;
	// 传感器管理器
	private SensorManager sensorManager;
	// 手机上一个位置时重力感应坐标
	private float lastX;
	private float lastY;
	private float lastZ;
	// 上次检测时间
	private long lastUpdateTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fuzzyjob_layout);
		
		ViewUtils.inject(this);
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		keywordsFlow = (KeywordsFlow) findViewById(R.id.keywordsFlow);
		keywordsFlow.setDuration(800l);
		keywordsFlow.setOnItemClickListener(this);
		// 添加
		feedKeywordsFlow(keywordsFlow, keywords);
		//进行搜索关键字的飞入和飞出效果的显示
		keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//进行重力传感器的注册
		sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_GAME);
	}
	//添加关键字
	private static void feedKeywordsFlow(KeywordsFlow keywordsFlow, String[] arr) {
		Random random = new Random();
		for (int i = 0; i < KeywordsFlow.MAX; i++) {
			int ran = random.nextInt(arr.length);
			String tmp = arr[ran];
			keywordsFlow.feedKeyword(tmp);
		}
	}

	@Override
	public void onClick(View v) {
		 if (v instanceof TextView) {
			 //获得搜索的关键字
			String keyword = ((TextView) v).getText().toString();
			etsearch.setText(keyword);
			//跳转至工作列表页面
			BasicNameValuePair nameValuePair = new BasicNameValuePair("jobInfor",keyword);
			IntentUtil.start_activity(JobSearchActivity.this,JobListActivity.class,nameValuePair);
			JobSearchActivity.this.finish();
		}
	}

	@Override
	public void onSensorChanged(SensorEvent event) {//核心
		
		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			// 现在检测时间
			long currentUpdateTime = System.currentTimeMillis();
			// 两次检测的时间间隔
			long timeInterval = currentUpdateTime - lastUpdateTime;
			// 判断是否达到了检测时间间隔
			if (timeInterval < UPTATE_INTERVAL_TIME)
				return;
			// 现在的时间变成last时间
			lastUpdateTime = currentUpdateTime;

			// 获得x,y,z坐标
			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];

			// 获得x,y,z的变化值
			float deltaX = x - lastX;
			float deltaY = y - lastY;
			float deltaZ = z - lastZ;

			// 将现在的坐标变成last坐标
			lastX = x;
			lastY = y;
			lastZ = z;

			double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ
					* deltaZ)
					/ timeInterval * 10000;
			// 达到速度阀值，发出提示
			if (speed >= SPEED_SHRESHOLD) {
				keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
		
	}
	//搜索事件
	@OnClick(R.id.search_btn)
	public void search(View v){
		String jobInfo = etsearch.getText().toString().trim();
		if(jobInfo.equals("")){
			etsearch.setError("搜索的关键字不能为空");
			return ;
		}
		
		BasicNameValuePair nameValuePair = new BasicNameValuePair("jobInfor",jobInfo);
		IntentUtil.start_activity(JobSearchActivity.this,JobListActivity.class,nameValuePair);
		JobSearchActivity.this.finish();
	}
	//返回
	@OnClick(R.id.back)
	public void back(View v){
		IntentUtil.start_activity(JobSearchActivity.this,MainActivity.class);
		JobSearchActivity.this.finish();
	}
}
