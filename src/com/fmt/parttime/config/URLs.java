package com.fmt.parttime.config;
/**
 * 访问资源文件配置类
 * @author Administrator
 *
 */
public class URLs {
	
	public static final  String BASE_URL = "http://192.168.1.105:8080/PartTimeJob";
	public static final  String USERS = BASE_URL + "/UsersServlet";
	public static final  String JOBLIST = BASE_URL + "/JoblistServlet?action=list&page=%s&jobCity=%s&jobInfo=%s";
	public static final  String JOBLIST_VIEW = BASE_URL + "/JoblistServlet?action=view&jobID=%s";
	public static final  String JOBLIST_APPLYCOUNT = BASE_URL + "/JoblistServlet?action=applycount&jobID=%s";
	public static final  String COLLECT_ADD = BASE_URL + "/JobcollectServlet?action=add";
	public static final  String COLLECT_DELETE = BASE_URL + "/JobcollectServlet?action=delete&jobID=%s&usersID=%s";
	public static final  String COLLECT_VIEW = BASE_URL + "/JobcollectServlet?action=list&usersID=%s";
	public static final  String APPLY_ADD = BASE_URL + "/JobapplyServlet?action=add";
	public static final  String APPLY_DELETE = BASE_URL + "/JobapplyServlet?action=delete&jobID=%s&applyStatus=%s&usersID=%s";
	public static final  String APPLY_VIEW = BASE_URL + "/JobapplyServlet?action=list&usersID=%s&applyStatus=%s";
	public static final  String APPLY_AGREECOUNT = BASE_URL + "/JobapplyServlet?action=agreecount&jobID=%s";
	public static final  String COMMIT_ADD = BASE_URL + "/CommitServlet?action=add";
	public static final  String VERSION_CHECK = BASE_URL + "/VersionServlet?action=check&versionID=%s&apkName=%s";
	public static final  String SEARCH_VIEW = BASE_URL + "/SearchServlet?action=view";

}
