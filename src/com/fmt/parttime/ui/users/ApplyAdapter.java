package com.fmt.parttime.ui.users;

import java.util.List;
import com.fmt.parttime.R;
import com.fmt.parttime.entity.Joblist;
import com.lidroid.xutils.util.LogUtils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
/**
 * 申请列表适配器(核心)
 * @author Administrator
 *
 */
public class ApplyAdapter extends BaseAdapter {
	
	List<Joblist> mdatas;//数据源
	Context context;
	
	public ApplyAdapter(Context context,List<Joblist> joblists){
		if(joblists ==null){
			return ;
		}
		this.context = context;
		this.mdatas = joblists;
		
	}
	
	//为适配器添加数据
	public void addMore(List<Joblist> joblists){
		if(joblists ==null){
			return ;
		}
		mdatas.clear();
		mdatas.addAll(joblists);
	}
	
	public void clearData(){
		mdatas.clear();
	}

	@Override
	public int getCount() {
		
		return mdatas.size();
	}

	@Override
	public Object getItem(int position) {
		
		return mdatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {//核心
		//解析自定义布局
		ViewHodler viewHodler = null;
		
		if(convertView == null){
			convertView = View.inflate(context,R.layout.adapter_job_item2,null);
			viewHodler = new ViewHodler();
			viewHodler.theme_content = (TextView) convertView.findViewById(R.id.theme_content);
			viewHodler.time = (TextView) convertView.findViewById(R.id.time);
			viewHodler.distance = (TextView) convertView.findViewById(R.id.distance);
			viewHodler.wage = (TextView) convertView.findViewById(R.id.wage);
			viewHodler.companyName = (TextView) convertView.findViewById(R.id.companyName);
			
			convertView.setTag(viewHodler);
		}else{
			viewHodler = (ViewHodler) convertView.getTag();
		}
		
		//为布局空间绑定数据
		Joblist job = mdatas.get(position);
		viewHodler.theme_content.setText(job.jobTitle);
		viewHodler.time.setText(job.getJobPostDate());
		viewHodler.distance.setText(job.getJobPostAddress());
		viewHodler.wage.setText(job.jobPayFee);
		viewHodler.companyName.setText(job.getJobPostCompany());
		
		return convertView;
	}
	
	class ViewHodler{
		TextView theme_content;
		TextView time;
		TextView distance;
		TextView wage;
		TextView companyName;
	}

}
