package com.fangcheng.plugin.newBusiness.newPoi.Tool;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MyFileReader {

	FileReader fr;
	BufferedReader br;

	List<String> content = new LinkedList<String>();

	public MyFileReader(String path) {
		try {
			fr = new FileReader(path);
			br = new BufferedReader(fr);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public List<String> getContent() {
		String str = "";
		StringBuffer sb = new StringBuffer("");
		try {
			while ((str = br.readLine()) != null) {
				content.add(str);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			str = null;
			sb = null;
			close();
		}
		return content;
	}

	public Iterator<String> getItContent() {
		BufferedReader br = new BufferedReader(fr);
		Iterator<String> it = new MyIterator(br);
		return it;
	}

	public void close() {
		try {
			br.close();
			fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		MyFileReader mfr = new MyFileReader("/home/wenyang/go.json");
		Iterator<String> it = mfr.getItContent();
		while (it.hasNext()) {
			System.out.println(it.next());
		}
		mfr.close();
	}

	class MyIterator implements Iterator<String> {
		BufferedReader br;

		MyIterator(BufferedReader _br) {
			br = _br;
		}

		private boolean hasNext = true;

		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return hasNext;
		}

		@Override
		public String next() {
			String temp = null;
			try {
				temp = br.readLine();
				if (temp == null) {
					hasNext = false;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return temp;
		}

		@Override
		public void remove() {
		}
	}

}
