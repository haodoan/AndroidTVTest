package com.jindo.fpttv.M3uParser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class describes a .m3u file.
 * 
 * @author Ke
 */
public class M3UFile {
	private M3UHead mHeader;
	private ArrayList<M3UItem> mItems;

	protected M3UFile() {
		mItems = new ArrayList<M3UItem>();
	}

	public void setHeader(M3UHead header) {
		mHeader = header;
	}

	public M3UHead getHeader() {
		return mHeader;
	}

	public boolean addItem(M3UItem item) {
		return mItems.add(item);
	}

	public boolean addItems(ArrayList<M3UItem> items) {
		return mItems.addAll(items);
	}

	public ArrayList<M3UItem> getItems() {
		return mItems;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (mHeader != null) {
			sb.append(mHeader.toString());
		} else {
			sb.append("No header");
		}
		sb.append('\n');
		for (M3UItem item : mItems) {
			sb.append(item.toString());
			sb.append('\n');
		}
		return sb.toString();
	}
}
