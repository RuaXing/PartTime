package com.fmt.parttime;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.fmt.parttime.common.utils.IntentUtil;
import com.fmt.parttime.common.utils.PreferencesUtils;
import com.lidroid.xutils.ViewUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

/**
 * 功能:
 * (1)广告页面三秒钟实现调整,Handler实现
 * (2)实现高德地图定位
 *	  1.实现AMapLocationListener接口
 *	  2.创建LocationManagerProxy定位管理对象
 *	  3.获得定位信息
 * 
 * @author Administrator
 *
 */
public class SplashActivity extends Activity implements AMapLocationListener {
	
	private LocationManagerProxy proxy = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//设置全屏
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash);
		
		ViewUtils.inject(this);
		
		//实现3秒跳转
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				
				if(!isNetValiable()){
					setNetWork();
					return ;
				}else{
					IntentUtil.start_activity(SplashActivity.this, MainActivity.class);
					SplashActivity.this.finish();
				}
			}
		},3000);
		
		//实现定位
		initLocation();
	}
	
	private void setNetWork() {
		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
		builder.setTitle("网络状态提醒").setIcon(R.drawable.not)
		       .setMessage("当前网络不可用,请进行设置!")
		       .setPositiveButton("设置",new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//跳转设置页面
					Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
					startActivity(intent);
					SplashActivity.this.finish();
				}
			}).setNegativeButton("取消",null).show();
		
	}
	//判断当前的网络状态
	private boolean isNetValiable() {
		//获得网络管理对象
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		
		if(manager == null){
			return false;
		}else{
			//判断当前的设配是否有网络
			NetworkInfo []networkInfos =  manager.getAllNetworkInfo();
			if(networkInfos !=null){
				for (NetworkInfo networkInfo : networkInfos) {
					//判断当前网络设配是否处于连接状态
					if(networkInfo.getState() == NetworkInfo.State.CONNECTED){
						return true;
					}
				}
			}
		}
		
		return false;
	}

	//设置网络
	

	//初始化定位
	private void initLocation() {
		//获得LocationManagerProxy对象
		proxy = LocationManagerProxy.getInstance(this);
		//产生定位请求,参数一:定位的方式(Provider),参数二:定位的最短周期,参数三:定位最短距离,超过则重新定位,参数四:监听者(重点)
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
			String city = location.getCity();
			//将定位信息写入到xml文件
			PreferencesUtils.putString(SplashActivity.this,"city",city);
		
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
	
	

}
