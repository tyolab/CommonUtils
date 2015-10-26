package au.com.tyo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Observable;
import java.util.Properties;

public class CommonSettings extends Observable {
	public static String[] BROWSERS = {"chrome", "epiphany", "firefox", "mozilla", "konqueror",
        "netscape","opera", "seamonkey", "links","lynx"};
	
	public static final String CACHE_DIR_STRING = "Cache";
	
	public static final int DEVICE_PC = 0;
	public static final int DEVICE_MOBILE = 1;
	
	public static final int OS_UNKNOWN = -1;
	public static final int OS_LINUX = 0;
	public static final int OS_UNIX = 1;
	public static final int OS_WIN = 2;
	public static final int OS_MAC = 3;
	
	public static final int OS_LINUX_ANDROID = 0;
	public static final int OS_LINUX_OTHERS = 1;
	
	protected static String version = "0.9.9"; // that is the number to show something wrong
	protected static String os = null;
	protected static String device = "desktop";
//	protected static String osArch = null;
//	protected static String osVersion = null;
//	protected static String javaVendor = null;
//	protected static String javaVendorUrl = null;
	
	private static int osSubId = OS_UNKNOWN;
	private static int platform;
	
	private static boolean isLandscapeMode;  // either the landscape mode of mobile phone, or the tablet
	
	private static boolean isTablet;
	
	protected int deviceIndicator =  DEVICE_MOBILE;
	
	protected boolean debug = true;
	
	protected boolean firstTimeRun;
	
	protected Locale locale;
	
	
	static {
		isLandscapeMode = false;
		getOSProperties();
	}
	
	public CommonSettings() {
		setDefaultLocale();
		
	    if (osSubId == OS_LINUX_ANDROID)
	    	setDeviceMobile();
	}
	
	public void setDefaultLocale() {
		locale = Locale.getDefault();
	}

	public static void getOSProperties() {
		os = System.getProperty("os.name").toLowerCase();
//		String osVersion = System.getProperty("os.version");
	    String osArch = System.getProperty("os.arch").toLowerCase();
	    
	    String javaVendor = System.getProperty("java.vendor").toLowerCase();
	    
//	    http://jmonkeyengine.org/groups/android/forum/topic/determining-if-the-game-is-on-android-or-pc/
//	    boolean is64 = is64Bit(arch);
	    if (os.contains("windows")) {
	    	platform = OS_WIN;
	    	//	        return is64 ? Platform.Windows64 : Platform.Windows32;
	    } else if (os.contains("linux") || os.contains("freebsd") || os.contains("sunos")) {
	    	platform = OS_LINUX;
	    	
		    if (javaVendor.contains("android")) {
		    	osSubId = OS_LINUX_ANDROID;
		    	os = "android";
		    }
		    
//	        return is64 ? Platform.Linux64 : Platform.Linux32;
	    } else if (os.contains("mac os x") || os.contains("darwin")) {
	    	platform = OS_MAC;
//	        if (arch.startsWith("ppc")) {
//	            return is64 ? Platform.MacOSX_PPC64 : Platform.MacOSX_PPC32;
//	        } else {
//	            return is64 ? Platform.MacOSX64 : Platform.MacOSX32;
	        }
//	    } else {
//	        throw new UnsupportedOperationException("The specified platform: " + os + " is not supported.");
//	    }
	}
	
	public static Properties loadInfo(String file) {
//		HashMap<String, String> infoMap = new HashMap<String, String>();
		Properties infoMap = null;
//        if (version == null/* && !isMobileDevice()*/) {
            try {
                InputStream in =
                    CommonSettings.class.getResourceAsStream(file);
                if (in != null) {
                	infoMap = new Properties();
                	infoMap.load(in);
	                in.close();
	
	//                StringBuffer msg = new StringBuffer();
	//                msg.append("Wikipedia Anywhere version ");
	//                msg.append();
	//                msg.append(" compiled on ");
	//                msg.append(props.getProperty("DATE"));
                }
            } catch (IOException ioe) {
            	ioe.printStackTrace();
            } 
//        }
        return infoMap;
	}
	
	public void loadInfo() {
		Properties props = loadInfo("/au/com/tyo/wiki/api_version.txt");
		if (props != null) {
	        version = props.getProperty("VERSION"); //msg.toString();
	        debug = props.getProperty("BUILD").equals("debug");
		}
	}
	
	public void setDevice(int device) {
		deviceIndicator =  device;
	}
	
	public void setDeviceMobile() {
		deviceIndicator =  DEVICE_MOBILE;
	}
	
	public void setDevicePC() {
		deviceIndicator =  DEVICE_PC;
	}

	public boolean isMobileDevice() {
		return deviceIndicator ==  DEVICE_MOBILE;
	}
	
	public String getVersion() {
		return version;
	}
	
	public void openBrowser(String url) {
	    Runtime runtime = Runtime.getRuntime();
	      
	  	try{
	  	    switch (platform) {
	  	    case OS_WIN:
	  	        runtime.exec( "rundll32 url.dll,FileProtocolHandler " + url);
	  	    case OS_MAC: 
	  	        runtime.exec( "open " + url);
	        case OS_UNIX: 
	        case OS_LINUX:
		        {
		  	        StringBuffer cmd = new StringBuffer();
		  	        for (int i = 0; i< BROWSERS.length; ++i)
		  	            cmd.append( (i==0  ? "" : " || " ) + BROWSERS[i] +" \"" + url + "\" ");
		  	        runtime.exec(new String[] { "sh", "-c", cmd.toString() });
		        } 
		        break;
	        default:
	        	break;
	  	    }
	    }catch (Exception e){
	  	   
	    }
	}
	
	public File getCacheDir() {
		return new File("." + File.separator + CACHE_DIR_STRING);
	}

	public void informObserver() {
        setChanged();
        notifyObservers();
	}
	
	public Locale getLocale() {
		return locale;
	}

	public boolean isFirstTimeRun() {
		return firstTimeRun;
	}

	public void setFirstTimeRun(boolean firstTimeRun) {
		this.firstTimeRun = firstTimeRun;
	}
	
	public static boolean isAndroid() {
		return osSubId == OS_LINUX_ANDROID;
	}

	public static boolean isLandscapeMode() {
		return isLandscapeMode;
	}

	public static void setIsLandscapeMode(boolean isLandscapeMode) {
		CommonSettings.isLandscapeMode = isLandscapeMode;
	}

	public static boolean isTablet() {
		return isTablet;
	}

	public static void setIsTablet(boolean isTablet) {
		CommonSettings.isTablet = isTablet;
		if (isTablet)
			device = "tablet";
		else
			device = "phone";
	}

	public static String getOs() {
		return os;
	}
	
	public static String getDevice() {
		return device;
	}
}
