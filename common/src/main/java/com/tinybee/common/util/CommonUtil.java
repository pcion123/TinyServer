package com.tinybee.common.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonUtil
{
	private static Logger mLogger = LoggerFactory.getLogger(CommonUtil.class);
	
	public static String blank = " ";
	public static String commod = ",";
	public static String at = "@";
	public static String colon = ":";
	public static String semicolon = ";";
	public static String verticalLine = "|";

	/**
	 * 判断当前操作是否Windows.
	 * 
	 * @return true---是Windows操作系统
	 */
	public static boolean isWindowsOS()
	{
		boolean isWindowsOS = false;
		String osName = System.getProperty("os.name");
		if (osName.toLowerCase().indexOf("windows") > -1)
		{
			isWindowsOS = true;
		}
		return isWindowsOS;
	}

	/**
	 * 获取本机IP地址，并自动区分Windows还是Linux操作系统
	 * 
	 * @return String
	 */
	public static String getLocalIP()
	{
		String sIP = "";
		InetAddress ip = null;
		try
		{
			// 如果是Windows操作系统
			if (isWindowsOS())
			{
				ip = InetAddress.getLocalHost();
			}
			// 如果是Linux操作系统
			else
			{
				boolean bFindIP = false;
				Enumeration<NetworkInterface> netInterfaces = (Enumeration<NetworkInterface>) NetworkInterface
						.getNetworkInterfaces();
				while (netInterfaces.hasMoreElements())
				{
					if (bFindIP)
					{
						break;
					}
					NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
					// ----------特定情况，可以考虑用ni.getName判断
					// 遍历所有ip
					Enumeration<InetAddress> ips = ni.getInetAddresses();
					while (ips.hasMoreElements())
					{
						ip = (InetAddress) ips.nextElement();
						if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() // 127.开头的都是lookback地址
								&& ip.getHostAddress().indexOf(":") == -1)
						{
							bFindIP = true;
							break;
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		if (null != ip)
		{
			sIP = ip.getHostAddress();
		}
		return sIP;
	}

	public static String getRandomInt(int len)
	{
		Random random = new Random();
		StringBuffer str = new StringBuffer();
		for (int i = 0; i < len; i++)
		{
			str.append(random.nextInt(10));
		}
		return str.toString();
	}

	public static boolean isNumeric(String str)
	{
		if (str == null)
		{
			return false;
		}
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches())
		{
			return false;
		}
		return true;
	}

	public static int getIntFromDoubleTwoLittleNum(double val)
	{
		String s = String.valueOf(val);
		int index = s.indexOf(".");
		int len = s.length();
		if (index == -1)
		{
			s = s + "00";
		}
		else if (index == len - 2)
		{
			s = s.replace(".", "") + "0";
		}
		else if (index == len - 3)
		{
			s = s.replace(".", "");
		}
		else
		{
			s = "-1";
		}
		return Integer.parseInt(s);
	}

	private static long getIpNum(String ipAddress)
	{
		String[] ip = ipAddress.split("\\.");
		long a = Integer.parseInt(ip[0]);
		long b = Integer.parseInt(ip[1]);
		long c = Integer.parseInt(ip[2]);
		long d = Integer.parseInt(ip[3]);

		long ipNum = a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;
		return ipNum;
	}

	private static boolean isInner(long userIp, long begin, long end)
	{
		return (userIp >= begin) && (userIp <= end);
	}

	public static boolean isInnerIP(String ipAddress)
	{
		boolean isInnerIp = false;
		long ipNum = getIpNum(ipAddress);
		/**
		 * 私有IP：A类 10.0.0.0-10.255.255.255 B类 172.16.0.0-172.31.255.255 C类
		 * 192.168.0.0-192.168.255.255 当然，还有127这个网段是环回地址
		 **/
		long aBegin = getIpNum("10.0.0.0");
		long aEnd = getIpNum("10.255.255.255");
		long bBegin = getIpNum("172.16.0.0");
		long bEnd = getIpNum("172.31.255.255");
		long cBegin = getIpNum("192.168.0.0");
		long cEnd = getIpNum("192.168.255.255");
		isInnerIp = isInner(ipNum, aBegin, aEnd) || isInner(ipNum, bBegin, bEnd) || isInner(ipNum, cBegin, cEnd) || ipAddress.equals("127.0.0.1");
		return isInnerIp;
	}

	/**
	 * 获取客户端ip
	 *
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request)
	{
		String ip = request.getHeader("HTTP_CLIENT_IP");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
		{
			ip = request.getHeader("x-forwarded-for");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
		{
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
		{
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
		{
			ip = request.getRemoteAddr();
			if (ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1"))
			{
				// 根据网卡取本机配置的IP
				InetAddress inet = null;
				try
				{
					inet = InetAddress.getLocalHost();
				}
				catch (UnknownHostException e)
				{
					e.printStackTrace();
				}
				ip = inet.getHostAddress();
			}
		}

		if (ip != null && ip.length() > 15)
		{ // "***.***.***.***".length() = 15
			if (ip.indexOf(",") > 0)
			{
				ip = ip.substring(0, ip.indexOf(","));
			}
		}
		return ip;
	}

	public static boolean regMatchNumStr(String str)
	{
		return regMatch("[0-9]+", str);
	}

	public static boolean regMatch(String reg, String str)
	{
		if (StringUtil.isNullOrEmpty(reg))
		{
			return false;
		}
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	public static boolean getCompareTwoNumResultWithOperator(int num1, int num2, String operator)
	{
		if (">".equals(operator))
		{
			return num1 > num2;
		}
		if ("<".equals(operator))
		{
			return num1 < num2;
		}
		if ("!=".equals(operator))
		{
			return num1 != num2;
		}
		if (">=".equals(operator))
		{
			return num1 >= num2;
		}
		if ("<=".equals(operator))
		{
			return num1 <= num2;
		}
		return false;
	}

	public static String stringAppend(Object... objects)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < objects.length; i++)
		{
			if (objects[i] != null)
				sb.append(objects[i].toString());
			else
				sb.append("null");
		}
		return sb.toString();
	}
}
