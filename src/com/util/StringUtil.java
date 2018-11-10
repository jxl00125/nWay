package com.util;

import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class StringUtil {

	public static String truncateStr(String _string, int _maxLength) {
		return truncateStr(_string, _maxLength, "..");
	}

	public static String truncateStr(String _string, int _maxLength,
			String _sExt) {
		if (_string == null) {
			return null;
		}

		if (_sExt == null) {
			_sExt = "..";
		}

		int nSrcLen = getStringViewWidth(_string);
		if (nSrcLen <= _maxLength) {
			return _string;
		}

		int nExtLen = getStringViewWidth(_sExt);
		if (nExtLen >= _maxLength) {
			return _string;
		}

		int iLength = _string.length();
		int iRemain = _maxLength - nExtLen;
		StringBuffer sb = new StringBuffer(_maxLength + 2);

		for (int i = 0; i < iLength; i++) {
			char aChar = _string.charAt(i);
			int iNeed = getCharViewWidth(aChar);
			if (iNeed > iRemain) {
				sb.append(_sExt);
				break;
			}
			sb.append(aChar);
			iRemain -= iNeed;
		}

		return sb.toString();
	}

	public static final int getStringViewWidth(String s) {
		if ((s == null) || (s.length() == 0)) {
			return 0;
		}

		int iWidth = 0;
		int iLength = s.length();

		for (int i = 0; i < iLength; i++) {
			iWidth += getCharViewWidth(s.charAt(i));
		}

		return iWidth;
	}

	public static final int getCharViewWidth(int c) {
		return isChineseChar(c) ? 2 : 1;
	}

	public static final boolean isChineseChar(int c) {
		return c > 127;
	}

	public static String numberToStr(long round, int _length, char c) {
		String numberstr = String.valueOf(round);
		return expandStr(numberstr, _length, c);
	}

	public static String expandStr(String _string, int _length, char _chrFill) {
		int nLen = _string.length();
		if (_length <= nLen) {
			return _string;
		}

		String sRet = _string;
		for (int i = 0; i < _length - nLen; i++) {
			sRet = sRet + _chrFill;
		}
		return sRet;
	}

	public static String setStrEndWith(String _string, char _chrEnd) {
		return setStrEndWith0(_string, _chrEnd);
	}

	private static String setStrEndWith0(String _str, char _charEnd) {
		if ((isEmpty(_str)) || (_str.endsWith(String.valueOf(_charEnd)))) {
			return _str;
		}

		return _str + _charEnd;
	}

	public static String format(String _sFormat, Object[] _args) {
		if (isEmpty(_sFormat))
			return "";
		_sFormat = _sFormat.replaceAll("(\\$)", "\\\\$1");
		Pattern regExp = null;
		for (int i = 0; i < _args.length; i++) {
			if (_args[i] == null)
				continue;
			String param = _args[i].toString().replaceAll("(\\$)", "\\\\$1");
			regExp = Pattern.compile("\\{" + i + "}");
			_sFormat = regExp.matcher(_sFormat).replaceAll(param);
		}
		return _sFormat.replaceAll("\\\\\\$", "\\$");
	}

	public static String format(String _sFormat, int[] _args) {
		if (isEmpty(_sFormat))
			return "";
		Pattern regExp = null;
		for (int i = 0; i < _args.length; i++) {
			regExp = Pattern.compile("\\{" + i + "}");
			_sFormat = regExp.matcher(_sFormat).replaceAll(
					String.valueOf(_args[i]));
		}
		return _sFormat;
	}

	public static boolean isEmpty(String _string) {
		return (_string == null) || (_string.trim().length() == 0);
	}

	public static String filterForJs(String _sContent) {
		if (_sContent == null) {
			return "";
		}
		char[] srcBuff = _sContent.toCharArray();
		int nLen = srcBuff.length;
		if (nLen == 0) {
			return "";
		}
		StringBuffer retBuff = new StringBuffer((int) (nLen * 1.8D));

		for (int i = 0; i < nLen; i++) {
			char cTemp = srcBuff[i];
			switch (cTemp) {
			case '"':
				retBuff.append("\\\"");
				break;
			case '\'':
				retBuff.append("\\'");
				break;
			case '\\':
				retBuff.append("\\\\");
				break;
			case '\n':
				retBuff.append("\\n");
				break;
			case '\r':
				retBuff.append("\\r");
				break;
			case '\f':
				retBuff.append("\\f");
				break;
			case '\t':
				retBuff.append("\\t");
				break;
			case '/':
				retBuff.append("\\/");
				break;
			default:
				retBuff.append(cTemp);
			}
		}

		return retBuff.toString();
	}

	public static String showNull(String p_sValue) {
		return showNull(p_sValue, "");
	}

	public static String showNull(String _sValue, String _sReplaceIfNull) {
		return _sValue == null ? _sReplaceIfNull : _sValue;
	}

	public static int[] splitToInt(String _str, String _sDelim)
	  {
	    if (isEmpty(_str)) {
	      return new int[0];
	    }

	    if (isEmpty(_sDelim)) {
	      _sDelim = ",";
	    }

	    StringTokenizer stTemp = new StringTokenizer(_str, 
	      _sDelim);
	    int[] arInt = new int[stTemp.countTokens()];
	    int nIndex = 0;

	    while (stTemp.hasMoreElements()) {
	      String sValue = (String)stTemp.nextElement();
	      arInt[nIndex] = Integer.parseInt(sValue.trim());
	      nIndex++;
	    }
	    return arInt;
	  }
	
	public static String replaceStr(String _strSrc, String _strOld, String _strNew)
	  {
	    if ((_strSrc == null) || (_strNew == null) || (_strOld == null)) {
	      return _strSrc;
	    }

	    char[] srcBuff = _strSrc.toCharArray();
	    int nSrcLen = srcBuff.length;
	    if (nSrcLen == 0) {
	      return "";
	    }

	    char[] oldStrBuff = _strOld.toCharArray();
	    int nOldStrLen = oldStrBuff.length;
	    if ((nOldStrLen == 0) || (nOldStrLen > nSrcLen)) {
	      return _strSrc;
	    }
	    StringBuffer retBuff = new StringBuffer(nSrcLen * (1 + 
	      _strNew.length() / 
	      nOldStrLen));

	    boolean bIsFound = false;

	    int i = 0;
	    while (i < nSrcLen) {
	      bIsFound = false;
	      if (srcBuff[i] == oldStrBuff[0]) {
	        for (int j = 1; j < nOldStrLen; j++) {
	          if (i + j >= nSrcLen)
	            break;
	          if (srcBuff[(i + j)] != oldStrBuff[j])
	            break;
	        }
	        //bIsFound =(nOldStrLen==j);
	      }

	      if (bIsFound) {
	        retBuff.append(_strNew);
	        i += nOldStrLen;
	      }
	      else
	      {
	        int nSkipTo;
	        if (i + nOldStrLen >= nSrcLen)
	          nSkipTo = nSrcLen - 1;
	        else {
	          nSkipTo = i;
	        }
	        for (; i <= nSkipTo; i++) {
	          retBuff.append(srcBuff[i]);
	        }
	      }
	    }
	    srcBuff = (char[])null;
	    oldStrBuff = (char[])null;
	    return retBuff.toString();
	  }

}
