package com.fangcheng.plugin.newBusiness.newPoi.Tool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StringUtil {

	public static String inputStream2String(InputStream is) throws IOException {
		return inputStream2String(is, "utf-8");
	}

	public static String inputStream2String(InputStream is, String charset)
			throws IOException {

		ByteArrayOutputStream baos = null;

		try {
			baos = new ByteArrayOutputStream();
			int i = -1;
			while ((i = is.read()) != -1) {
				baos.write(i);
			}
			return baos.toString(charset);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != baos) {
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				baos = null;
			}
		}
		return null;

	}

}
