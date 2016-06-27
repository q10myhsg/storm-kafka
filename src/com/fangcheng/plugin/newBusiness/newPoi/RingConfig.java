package com.fangcheng.plugin.newBusiness.newPoi;

import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

public class RingConfig {
	public static String default_config;//默认属性文件名称

	private static Properties mConfig;
	
    /*
     * Static block run once at class loading
     *
     * We load the default properties and any custom properties we find
     */	
	
	public static void startConfig(){
		String struts_cl = RingConfig.class.getResource("/").getPath();
		System.out.println(default_config+"------------");
		
		 mConfig = new Properties();
		 try{
			 File file = ResourceUtils.getFile(struts_cl.substring(1, struts_cl.length())+ default_config);
			 InputStream is = new FileInputStream(file);
			 if(is!=null){
				 mConfig.load(is);
			 }else{
			 }
		 }catch(Exception e){
			 e.printStackTrace();
		 }
	}
    /**
     * 在属性文件中根据key获取 属性值
     * @param     key Name of the property
     * @return    String Value of property requested, null if not found
     */
    public static String getProperty(String key) {
        return mConfig.getProperty(key);
    }
    /**
     * 在属性文件中根据key获取 属性值
     * @param     key Name of the property
     * @param     defaultValue Default value of property if not found     
     * @return    String Value of property requested or defaultValue
     */
    public static String getProperty(String key, String defaultValue) {
        String value = mConfig.getProperty(key);
        if(value == null)
          return defaultValue;
        return value;
    }
    /**
     * 在属性文件中根据key获取 属性值boolean型
     * @param key -属性
     * return boolean
     */
    public static boolean getBooleanProperty(String key) {
        return getBooleanProperty(key, false);
    }
    /**
     * 在属性文件中根据key获取 属性值boolean型
     * 
     * @param key  -属性
     * @param defaultValue -如果不存在的 默认值
     * @return boolean
     */
    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        // get the value first, then convert
        String value = RingConfig.getProperty(key);

        if(value == null)
            return defaultValue;

        return (new Boolean(value)).booleanValue();
    }
    /**
     * 在属性文件中根据key获取 属性值int型
     * 
     * @param key  -属性
     * @return int
     */
    public static int getIntProperty(String key) {
        return getIntProperty(key, 0);
    }
    /**
     * 在属性文件中根据key获取 属性值int型
     * 
     * @param key
     * @param defaultValue
     * @return int
     */
    public static int getIntProperty(String key, int defaultValue) {
        // get the value first, then convert
        String value = RingConfig.getProperty(key);

        if (value == null || !value.matches("\\d+"))
            return defaultValue;
        return (new Integer(value)).intValue();
    }
    /**
     * 获取所属性文件中的key集合
     * 
     * @return Enumeration A list of all keys
     **/
    public static Enumeration keys() {
        return mConfig.keys();
    }

}
