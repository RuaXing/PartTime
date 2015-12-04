package com.fmt.parttime;

import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.fmt.parttime.common.utils.IntentUtil;
import com.fmt.parttime.common.utils.PreferencesUtils;
import com.fmt.parttime.ui.job.JobListActivity;
import com.fmt.parttime.ui.job.JobSearchActivity;
import com.fmt.parttime.ui.users.HomeActivity;
import com.fmt.parttime.ui.users.LoginActivity;
import com.gitonway.androidimagesliderdemo.widget.imageslider.SliderLayout;
import com.gitonway.androidimagesliderdemo.widget.imageslider.Animations.DescriptionAnimation;
import com.gitonway.androidimagesliderdemo.widget.imageslider.SliderTypes.BaseSliderView;
import com.gitonway.androidimagesliderdemo.widget.imageslider.SliderTypes.BaseSliderView.OnSliderClickListener;
import com.gitonway.androidimagesliderdemo.widget.imageslider.SliderTypes.TextSliderView;
import com.gitonway.androidimagesliderdemo.widget.imageslider.Indicators.PagerIndicator;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


/**
 * 用户主界面
 * 功能:
 *    (1).在获取定位失败后,重新进行定位
 *    (2).初始化幻灯片布局对象
 *        1.实现OnSliderClickListener接口
 *        2.初始化SliderLayout对象,并设置监听器与其相应的属性
 *        
 * @author Administrator
 *
 */

public class MainActivity extends Activity implements AMapLocationListener,OnSliderClickListener {
	
	String cityName = "";
	private LocationManagerProxy proxy;
	
	private String username;
	
	@ViewInject(R.id.city)
	TextView city;
	@ViewInject(R.id.slider)
	SliderLayout mSlider;//幻灯片的布局控件
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mymainfunction_layout);
		
		ViewUtils.inject(this);
		
		username = PreferencesUtils.getString(MainActivity.this,"username","");
		
		//获取定位信息
		cityName = PreferencesUtils.getString(MainActivity.this,"city","");
		//重新定位
		if(cityName.equals("")){
			initLocation();
		}else{
			//设置定位信息
			city.setText(cityName);
		}
		
		//初始化广告控件
		initAD();
	}

	private void initAD() {
		//组装广告数据
		HashMap<String, String> files = new HashMap<>();
		files.put("转发抵用券 分享很轻松", "http://61.155.81.202/zz/banner1.jpg");
		files.put("每天几分钟 赚钱分分钟", "http://pic.58pic.com/58pic/14/94/17/96f58PICHhZ_1024.jpg");
		files.put("收个好徒弟 逍遥快乐中", "http://61.155.81.202/zz/banner3.jpg");
		
		for(String name : files.keySet()){
			//加载幻灯片对象TextSliderView
			TextSliderView textSliderView = new TextSliderView(this);
			//初始化每张幻灯片的描述与图片并设置点击事件
			textSliderView.description(name).image(files.get(name))
			.setOnSliderClickListener(this);
			
			textSliderView.getBundle().putString("extra",name);
			//添加幻灯片
			mSlider.addSlider(textSliderView);
			
		}
		//设置广告更换样式
		mSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
		//设置广告指示符的位置
		mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
		//设置示符是否显示
		mSlider.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Visible);
		//设置动画描述
		mSlider.setCustomAnimation(new DescriptionAnimation());
		//设置显示周期
		mSlider.setDuration(3000);
		
		
	}
	/**
	 * 实现地理定位
	 */
	private void initLocation() {
		
		proxy = LocationManagerProxy.getInstance(this);
		proxy.requestLocationUpdates(LocationProviderProxy.AMapNetwork,5000,10,this);
	}

	@Override
	public void onLocationChanged(Location location) {
		
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		
		
	}

	@Override
	public void onLocationChanged(AMapLocation location) {//核心
		//获取定位的信息
		if(location !=null){
			//获得所在的城市
			String cityName = location.getCity();
			//将定位信息写入到xml文件
			PreferencesUtils.putString(MainActivity.this,"city",cityName);
			city.setText(cityName);//更新UI
			
		}
		
	}
	
	@Override
	protected void onPause() {
		if(proxy !=null){
			proxy.removeUpdates(this);
		}
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		if(proxy !=null){
			proxy.removeUpdates(this);
			proxy.destory();
		}
		proxy = null;
		super.onDestroy();
	}

	//每张广告的点击事件
	@Override
	public void onSliderClick(BaseSliderView slider) {
		
		
	}
	
	//返回首页
	@OnClick(R.id.tv_home)
	public void tvhome(View v){
		IntentUtil.start_activity(this,MainActivity.class);
		this.finish();
	}
	
	//兼职事件
	@OnClick(R.id.tv_job)
	public void partjob(View v){
		if(username.trim().equals("")){
			BasicNameValuePair nameValuePair = new BasicNameValuePair("item","job");
			IntentUtil.start_activity(MainActivity.this,LoginActivity.class,nameValuePair);
		}else{
			IntentUtil.start_activity(MainActivity.this,JobListActivity.class);
		}
		
		this.finish();
	}
	
	//实现事件
	@OnClick(R.id.tv_parttime)
	public void partTime(View v){
		if(username.trim().equals("")){
			BasicNameValuePair nameValuePair = new BasicNameValuePair("item","part");
			BasicNameValuePair jobcity = new BasicNameValuePair("jobcity","海南");
			IntentUtil.start_activity(MainActivity.this,LoginActivity.class,nameValuePair,jobcity);
		}else{
			IntentUtil.start_activity(MainActivity.this,JobListActivity.class);
		}
		
		this.finish();
	}
	
	//兼职事件
	@OnClick(R.id.tv_profile)
	public void profile(View v){
		if(username.trim().equals("")){
			BasicNameValuePair nameValuePair = new BasicNameValuePair("item","home");
			IntentUtil.start_activity(MainActivity.this,LoginActivity.class,nameValuePair);
		}else{
			IntentUtil.start_activity(MainActivity.this,HomeActivity.class);
		}
		
		this.finish();
	}
	//搜索兼职事件
	@OnClick(R.id.title)
	public void search(View v){
		if(username.trim().equals("")){
			BasicNameValuePair nameValuePair = new BasicNameValuePair("item","search");
			IntentUtil.start_activity(MainActivity.this,LoginActivity.class,nameValuePair);
		}else{
			IntentUtil.start_activity(MainActivity.this,JobSearchActivity.class);
		}
		
		MainActivity.this.finish();
	}
	
	

}
