package pe.gob.sunat.utils;

import java.util.HashMap;
import java.util.Hashtable;

public class Utilidades {
	
	public Utilidades() {
	}

	public final String CRLF = "\r\n";
	public final int UNDEFINT = Integer.MIN_VALUE;
	public final int adText = 1;
	public final int adDate = 2;
	public final int adNumber = 3;
	public final int adSearch_ = 4;
	public final int ad_Search_ = 5;
	public final String appPath = "/";
	static final String DBDriver = "com.informix.jdbc.IfxDriver";
	static final String strConn = "jdbc:informix-sqli://150.50.1.12:50001/sp:informixserver=s0r_bancos";
	static final String DBusername = "mansp";
	static final String DBpassword = "sp5537";

	public static String loadDriver() {
		String sErr = "";
		try {
			java.sql.DriverManager.registerDriver((java.sql.Driver) (Class
					.forName(DBDriver).newInstance()));
		} catch (Exception e) {
			sErr = e.toString();
		}
		return (sErr);
	}

	public static void absolute(java.sql.ResultSet rs, int row)
			throws java.sql.SQLException {
		for (int x = 1; x < row; x++)
			rs.next();
	}

	public java.sql.ResultSet openrs(java.sql.Statement stat, String sql)
			throws java.sql.SQLException {
		java.sql.ResultSet rs = stat.executeQuery(sql);
		return (rs);
	}

	public String dLookUp(java.sql.Statement stat, String table, String fName,
			String where) {
		java.sql.Connection conn1 = null;
		java.sql.Statement stat1 = null;

		try {
			conn1 = cn();
			stat1 = conn1.createStatement();
			java.sql.ResultSet rsLookUp = openrs(stat1, "SELECT " + fName
					+ " FROM " + table + " WHERE " + where);
			if (!rsLookUp.next()) {
				rsLookUp.close();
				stat1.close();
				conn1.close();
				return "";
			}
			String res = rsLookUp.getString(1);
			rsLookUp.close();
			stat1.close();
			conn1.close();
			return (res == null ? "" : res);
		} catch (Exception e) {
			return "";
		}
	}

	public String dLookUpNombres(java.sql.Statement stat, String table,
			String fName, String fName1, String fName2, String where) {
		java.sql.Connection conn1 = null;
		java.sql.Statement stat1 = null;
		try {
			conn1 = cn();
			stat1 = conn1.createStatement();
			java.sql.ResultSet rsLookUp = openrs(stat1, "SELECT " + fName
					+ fName1 + fName2 + " FROM " + table + " WHERE " + where);
			if (!rsLookUp.next()) {
				rsLookUp.close();
				stat1.close();
				conn1.close();
				return "";
			}
			String res = rsLookUp.getString(1) + " " + rsLookUp.getString(2)
					+ ", " + rsLookUp.getString(3);
			rsLookUp.close();
			stat1.close();
			conn1.close();
			return (res == null ? "" : res);
		} catch (Exception e) {
			return "";
		}
	}

	public long dCountRec(java.sql.Statement stat, String table, String sWhere) {
		long lNumRecs = 0;
		try {
			java.sql.ResultSet rs = stat.executeQuery("select count(*) from "
					+ table + " where " + sWhere);
			if (rs != null && rs.next()) {
				lNumRecs = rs.getLong(1);
			}
			rs.close();
		} catch (Exception e) {
		}
		return lNumRecs;
	}

	public String proceedError(javax.servlet.http.HttpServletResponse response,
			Exception e) {
		return e.toString();
	}

	public String[] getFieldsName(java.sql.ResultSet rs)
			throws java.sql.SQLException {
		java.sql.ResultSetMetaData metaData = rs.getMetaData();
		int count = metaData.getColumnCount();
		String[] aFields = new String[count];
		for (int j = 0; j < count; j++) {
			aFields[j] = metaData.getColumnLabel(j + 1);
		}
		return aFields;
	}

	public Hashtable getRecordToHash(java.sql.ResultSet rs,
			java.util.Hashtable rsHash, String[] aFields)
			throws java.sql.SQLException {
		for (int iF = 0; iF < aFields.length; iF++) {
			rsHash.put(aFields[iF], getValue(rs, aFields[iF]));
		}
		return rsHash;
	}
	
	public HashMap getRecordToMap(java.sql.ResultSet rs,
			HashMap mapa, String[] aFields)
			throws java.sql.SQLException {
		for (int iF = 0; iF < aFields.length; iF++) {
			mapa.put(aFields[iF], getValue(rs, aFields[iF]));
		}
		return mapa;
	}	

	public java.sql.Connection cn() throws java.sql.SQLException {
		return java.sql.DriverManager.getConnection(strConn, DBusername,
				DBpassword);
	}

	public String toURL(String strValue) {
		if (strValue == null)
			return "";
		if (strValue.compareTo("") == 0)
			return "";
		return java.net.URLEncoder.encode(strValue);
	}

	public String toHTML(String value) {
		if (value == null)
			return "";
		value = replace(value, "&", "&amp;");
		value = replace(value, "<", "&lt;");
		value = replace(value, ">", "&gt;");
		value = replace(value, "\"", "&" + "quot;");
		return value;
	}

	public String getValueHTML(java.sql.ResultSet rs, String fieldName) {
		try {
			String value = rs.getString(fieldName);
			if (value != null) {
				return toHTML(value);
			}
		} catch (java.sql.SQLException sqle) {
		}
		return "";
	}

	public String getValue(java.sql.ResultSet rs, String strFieldName) {
		if ((rs == null) || (isEmpty(strFieldName))
				|| ("".equals(strFieldName)))
			return "";
		try {
			String sValue = (rs.getString(strFieldName)).trim();
			if (sValue == null)
				sValue = "";
			return sValue;
		} catch (Exception e) {
			return "";
		}
	}

	public String getParam(javax.servlet.http.HttpServletRequest req,
			String paramName) {
		String param = req.getParameter(paramName);
		if (param == null || param.equals(""))
			return "";
		param = replace(param, "&amp;", "&");
		param = replace(param, "&lt;", "<");
		param = replace(param, "&gt;", ">");
		param = replace(param, "&amp;lt;", "<");
		param = replace(param, "&amp;gt;", ">");
		return param;
	}

	public boolean isNumber(String param) {
		boolean result;
		if (param == null || param.equals(""))
			return true;
		param = param.replace('d', '_').replace('f', '_');
		try {
			/*Double dbl =*/ new Double(param);
			result = true;
		} catch (NumberFormatException nfe) {
			result = false;
		}
		return result;
	}

	public boolean isEmpty(int val) {
		return val == UNDEFINT;
	}

	public boolean isEmpty(String val) 
	{
		return (val == null || val.equals("") || val
				.equals(Integer.toString(UNDEFINT)));
	}

	public String getCheckBoxValue(String val, String checkVal,
			String uncheckVal, int ctype) {
		if (val == null || val.equals(""))
			return toSQL(uncheckVal, ctype);
		else
			return toSQL(checkVal, ctype);
	}

	public String toWhereSQL(String fieldName, String fieldVal, int type) {
		String res = "";
		switch (type) {
		case adText:
			if (!"".equals(fieldVal)) {
				res = " " + fieldName + " like '%" + fieldVal + "%'";
			}
		case adNumber:
			res = " " + fieldName + " = " + fieldVal + " ";
		case adDate:
			res = " " + fieldName + " = '" + fieldVal + "' ";
		default:
			res = " " + fieldName + " = '" + fieldVal + "' ";
		}
		return res;
	}

	public String toSQL(String value, int type) {
		if (value == null)
			return "Null";
		String param = value;
		if ("".equals(param) && (type == adText || type == adDate)) {
			return "Null";
		}
		switch (type) {
		case adText: {
			param = replace(param, "'", "''");
			param = replace(param, "&amp;", "&");
			param = "'" + param + "'";
			break;
		}
		case adSearch_:
		case ad_Search_: {
			param = replace(param, "'", "''");
			break;
		}
		case adNumber: {
			try {
				if (!isNumber(value) || "".equals(param))
					param = "null";
				else
					param = value;
			} catch (NumberFormatException nfe) {
				param = "null";
			}
			break;
		}
		case adDate: {
			param = "'" + param + "'";
			break;
		}
		}
		return param;
	}

	public String replace(String str, String pattern, String replace) {
		if (replace == null) {
			replace = "";
		}
		int s = 0, e = 0;
		StringBuffer result = new StringBuffer((int) str.length() * 2);
		while ((e = str.indexOf(pattern, s)) >= 0) {
			result.append(str.substring(s, e));
			result.append(replace);
			s = e + pattern.length();
		}
		result.append(str.substring(s));
		return result.toString();
	}

	public String getOptions(java.sql.Connection conn, String sql,
			boolean isSearch, boolean isRequired, String selectedValue) {

		String sOptions = "";
		String sSel = "";

		if (isSearch) {
			sOptions += "<option value=\"\">Todos</option>";
		} else {
			if (!isRequired) {
				sOptions += "<option value=\"\"></option>";
			}
		}
		try {
			java.sql.Statement stat = conn.createStatement();
			java.sql.ResultSet rs = null;
			rs = openrs(stat, sql);
			while (rs.next()) {
				String id = toHTML(rs.getString(1));
				String val = toHTML(rs.getString(2));

				if (id.compareTo(selectedValue) == 0) {
					sSel = "SELECTED";
				} else {
					sSel = "";
				}
				sOptions += "<option value=\"" + id + "\" " + sSel + ">" + id
						+ ":" + val + "</option>";
			}
			rs.close();
			stat.close();
		} catch (Exception e) {
		}
		return sOptions;
	}

	public String getOptionsLOV(String sLOV, boolean isSearch,
			boolean isRequired, String selectedValue) {
		String sSel = "";
		String slOptions = "";
		String sOptions = "";
		String id = "";
		String val = "";
		java.util.StringTokenizer LOV = new java.util.StringTokenizer(sLOV,
				";", true);
		int i = 0;
		String old = ";";
		while (LOV.hasMoreTokens()) {
			id = LOV.nextToken();
			if (!old.equals(";") && (id.equals(";"))) {
				id = LOV.nextToken();
			} else {
				if (old.equals(";") && (id.equals(";"))) {
					id = "";
				}
			}
			if (!id.equals("")) {
				old = id;
			}

			i++;

			if (LOV.hasMoreTokens()) {
				val = LOV.nextToken();
				if (!old.equals(";") && (val.equals(";"))) {
					val = LOV.nextToken();
				} else {
					if (old.equals(";") && (val.equals(";"))) {
						val = "";
					}
				}
				if (val.equals(";")) {
					val = "";
				}
				if (!val.equals("")) {
					old = val;
				}
				i++;
			}

			if (id.compareTo(selectedValue) == 0) {
				sSel = "SELECTED";
			} else {
				sSel = "";
			}
			slOptions += "<option value=\"" + id + "\" " + sSel + ">" + val
					+ "</option>";
		}
		if ((i % 2) == 0)
			sOptions += slOptions;
		return sOptions;
	}

	public String getValFromLOV(String selectedValue, String sLOV) {
		String sRes = "";
		String id = "";
		String val = "";
		java.util.StringTokenizer LOV = new java.util.StringTokenizer(sLOV,
				";", true);
		int i = 0;
		String old = ";";
		while (LOV.hasMoreTokens()) {
			id = LOV.nextToken();
			if (!old.equals(";") && (id.equals(";"))) {
				id = LOV.nextToken();
			} else {
				if (old.equals(";") && (id.equals(";"))) {
					id = "";
				}
			}
			if (!id.equals("")) {
				old = id;
			}

			i++;

			if (LOV.hasMoreTokens()) {
				val = LOV.nextToken();
				if (!old.equals(";") && (val.equals(";"))) {
					val = LOV.nextToken();
				} else {
					if (old.equals(";") && (val.equals(";"))) {
						val = "";
					}
				}
				if (val.equals(";")) {
					val = "";
				}
				if (!val.equals("")) {
					old = val;
				}
				i++;
			}

			if (id.compareTo(selectedValue) == 0) {
				sRes = val;
			}
		}
		return sRes;
	}

	public String checkSecurity(int iLevel,
			javax.servlet.http.HttpSession session,
			javax.servlet.http.HttpServletResponse response,
			javax.servlet.http.HttpServletRequest request) {
		try {
			Object o1 = session.getAttribute("UserID");
			Object o2 = session.getAttribute("UserRights");
			boolean bRedirect = false;
			if (o1 == null || o2 == null) {
				bRedirect = true;
			}
			if (!bRedirect) {
				if ((o1.toString()).equals("")) {
					bRedirect = true;
				} else if ((new Integer(o2.toString())).intValue() < iLevel) {
					bRedirect = true;
				}
			}

			if (bRedirect) {
				response.sendRedirect("Login.jsp?querystring="
						+ toURL(request.getQueryString()) + "&ret_page="
						+ toURL(request.getRequestURI()));
				return "sendRedirect";
			}
		} catch (Exception e) {
		}
		return "";
	}

}

