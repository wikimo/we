package org.we.core;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.we.util.StringUtil;

public class DispatcherFilter implements Filter{
	
	private Map<String,String> appConfig = new HashMap<String,String>();

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)res;
		
		try {
			this.process(request,response);
		    filterChain.doFilter(request, response);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.loadConfig(filterConfig);
	}
	
	/**
	 * filter核心执行流程
	 * @param request
	 * @param response
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unused")
	private void process(HttpServletRequest request,HttpServletResponse response) throws IllegalAccessException{
		//解析uri
		Map<String,String> controllerMap = this.parseUri(request);
		if(null == controllerMap.get("name") || null == controllerMap.get("action")){
			throw new IllegalAccessException("can't found controller or action");
		}
		
		doAction(controllerMap);
	}
	
	
	/**
	 * 执行controller action
	 * @param controllerMap
	 */
	private void doAction(Map<String,String> controllerMap){
		try{
			Class<?> clazz =  Class.forName(controllerMap.get("name"));
			Object controller = clazz.newInstance();
			
			Method method = clazz.getDeclaredMethod(controllerMap.get("action"));
			method.invoke(controller);
			
		}catch(IllegalAccessException e){
			e.printStackTrace();
		}catch(SecurityException e){
			e.printStackTrace();
		}catch(NoSuchMethodException e){
			System.out.println("Method can't found in Current Controller.");
			e.printStackTrace();
		}catch(IllegalArgumentException e){
			e.printStackTrace();
		}catch(InvocationTargetException e){
			e.printStackTrace();
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}catch(InstantiationException e){
			e.printStackTrace();
		}
	}

	/**
	 * 加载fitler配置参数
	 * @param filterConfig
	 */
	private void loadConfig(FilterConfig filterConfig){
		String appPath = filterConfig.getInitParameter("appPath");

		this.appConfig.put("appPath", appPath);
		this.appConfig.put("controllerPath", appPath + ".controller");
		this.appConfig.put("modelPath", appPath + ".model");
	}
	
	/**
	 * 解析uri获取controller与action
	 * @param request
	 * @return
	 */
	private Map<String,String> parseUri(HttpServletRequest request){
		HashMap<String,String>  controllerMap = new HashMap<String,String>();
		
		String appName =  request.getContextPath();
		String uri =  request.getRequestURI();
		
		String controllerUri =  uri.substring(appName.length() + 1);
		
		if(controllerUri.indexOf("/") > 0){
			String[] controllerInfoTmp = controllerUri.split("/");
			
			controllerMap.put("name", appConfig.get("controllerPath") + "." + StringUtil.formatFirstLetter(controllerInfoTmp[0], "upper") + "Controller");
			controllerMap.put("action", controllerInfoTmp[1]);
		}else{
			controllerMap.put("name", appConfig.get("controllerPath") + ".RootController");
			controllerMap.put("action", "index");
		}
		
		return controllerMap;
	}
	
}