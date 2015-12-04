package com.fmt.parttime.common.utils;
/**
 * apk更新工具类
 * @author Administrator
 *
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.codehaus.jackson.map.ObjectMapper;
import com.fmt.parttime.R;
import com.fmt.parttime.config.URLs;
import com.fmt.parttime.entity.Version;
import com.fmt.parttime.ui.LoadingDialog;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
/**
 * 开发思路:
 * 1.获得当前的版本号并发送至服务端与服务的版本号进行对比
 * 2.若版本一致则无线更新
 * 3.若版本不同则提示更新
 *   1.显示提醒对话框
 *   2.进行下载(线程),使用handle不同更新下载进度条
 *   3.当下载完成后,提醒用户是否进行安装
 * @author Administrator
 *
 */

public class UpdateManager
{
	/* 下载中 */
	private static final int DOWNLOAD = 1;
	/* 下载结束 */
	private static final int DOWNLOAD_FINISH = 2;
	/* 下载保存路径 */
	private String mSavePath;
	/* 记录进度条数量 */
	private int progress;
	/* 是否取消更新 */
	private boolean cancelUpdate = false;
	private Context mContext;
	/* 更新进度条 */
	private ProgressBar mProgress;
	private Dialog mDownloadDialog;
	private LoadingDialog dialog;
	
	private Version version;

	/**
	 * 接受下载过程中的消息
	 */
	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			// 正在下载
			case DOWNLOAD:
				// 设置进度条位置
				mProgress.setProgress(progress);
				break;
			case DOWNLOAD_FINISH:
				// 安装文件
				installApk();
				break;
			default:
				break;
			}
		};
	};

	public UpdateManager(Context context)
	{
		this.mContext = context;
		dialog = new LoadingDialog(context);
	}

	/**
	 * 检测软件更新
	 */
	public void checkUpdate()
	{
		// 获取当前软件版本
		int versionCode = getVersionCode(mContext);
		
		HttpUtils httpUtils = new HttpUtils();//进行异步操作
		//封装请求参数
		RequestParams params = new RequestParams();
		String url = String.format(URLs.VERSION_CHECK,versionCode,"PartTime.apk");
		
		httpUtils.send(HttpRequest.HttpMethod.POST,url, params,new RequestCallBack<String>() {

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
				com.fmt.parttime.entity.Message message = null;
				
				try {
					message = mapper.readValue(info.result,com.fmt.parttime.entity.Message.class);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				if(message !=null){
					//无需更新
					if(message.getStatuId() == 0){
						
						ToastUtils.show(mContext,message.getMsg(),Toast.LENGTH_SHORT);
						
					}//更新
					else{
						//获得version对象
						try {
							version = mapper.readValue(message.getData(),Version.class);
							//信息提醒对话框
							showNoticeDialog(version);
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					}
					
				}else{
					ToastUtils.show(mContext,"为解析到对象",Toast.LENGTH_SHORT);
				}
				
			}
		});
				
	}


	/**
	 * 获取软件版本号(重点)
	 * 
	 * @param context
	 * @return
	 */
	private int getVersionCode(Context context)
	{
		int versionCode = 0;
		try
		{
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			versionCode = context.getPackageManager().getPackageInfo("com.fmt.parttime",0).versionCode;
		} catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return versionCode;
	}

	/**
	 * 显示软件更新对话框
	 */
	private void showNoticeDialog(final Version version)
	{
		// 构造对话框
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("软件更新");
		builder.setMessage("进行更新当前的软件");
		// 更新
		builder.setPositiveButton("更新", new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				// 显示下载对话框
				showDownloadDialog(version);
			}
		});
		// 稍后更新
		builder.setNegativeButton("稍后", new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});
		Dialog noticeDialog = builder.create();
		noticeDialog.show();
	}

	/**
	 * 显示软件下载对话框
	 */
	private void showDownloadDialog(Version version)
	{
		// 构造软件下载对话框
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("软件更新").setMessage("软件下载中,请稍候...");
		// 给下载对话框增加进度条
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.softupdate_progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		builder.setView(v);
		// 取消更新
		builder.setNegativeButton("取消", new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				// 设置取消状态
				cancelUpdate = true;
			}
		});
		mDownloadDialog = builder.create();
		mDownloadDialog.show();
		//下载完成后,提醒安装apk文件
		downloadApk(version);
	}

	/**
	 * 下载apk文件(重点)
	 */
	private void downloadApk(Version version)
	{
		// 启动新线程下载软件
		new downloadApkThread(version).start();
	}

	/**
	 * 下载文件线程
	 */
	private class downloadApkThread extends Thread
	{
		Version version;
		
		public downloadApkThread(Version version){
			this.version = version;
		}
		@Override
		public void run()
		{
			try
			{
				// 判断SD卡是否存在，并且是否具有读写权限
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				{
					// 获得存储卡的路径
					String sdpath = Environment.getExternalStorageDirectory() + "/";
					mSavePath = sdpath + "download";
					URL url = new URL(version.getApkUrl());
					// 创建连接
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.connect();
					// 获取文件大小
					int length = conn.getContentLength();
					// 创建输入流
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					// 判断文件目录是否存在
					if (!file.exists())
					{
						file.mkdir();
					}
					File apkFile = new File(mSavePath, version.getApkName());
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// 缓存
					byte buf[] = new byte[1024];
					// 写入到文件中
					do
					{
						int numread = is.read(buf);
						count += numread;
						// 计算进度条位置
						progress = (int) (((float) count / length) * 100);
						// 更新进度
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0)
						{
							// 下载完成
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						// 写入文件
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);// 点击取消就停止下载.
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			// 取消下载对话框显示
			mDownloadDialog.dismiss();
		}
	};

	/**
	 * 安装APK文件(核心)
	 */
	private void installApk()
	{
		//apk安装的路径
		File apkfile = new File(mSavePath,version.getApkName());
		if (!apkfile.exists())
		{
			return;
		}
		// 通过Intent安装APK文件(核心代码)
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
		mContext.startActivity(i);
	}
}
