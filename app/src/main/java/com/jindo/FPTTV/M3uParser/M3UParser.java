package com.jindo.FPTTV.M3uParser;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.jindo.FPTTV.data.ChannelContracts;
import com.jindo.FPTTV.models.ChannelItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to parse a .m3u file.
 * 
 * @author Ke
 */
public class M3UParser {
	private static final String PREFIX_EXTM3U = "#EXTM3U";
	private static final String PREFIX_EXTINF = "#EXTINF:";
	private static final String PREFIX_COMMENT = "#";
	private static final String EMPTY_STRING = "";
	private static final String ATTR_NAME = "name";
	private static final String ATTR_TYPE = "type";
	private static final String ATTR_DLNA_EXTRAS = "dlna_extras";
	private static final String ATTR_PLUGIN = "plugin";
	private static final String ATTR_CHANNEL_NAME = "channel_name";
	private static final String ATTR_DURATION = "duration";
	private static final String ATTR_LOGO = "logo";
	private static final String ATTR_GROUP_TITLE = "group-title";
	private static final String ATTR_TVG_PREFIX = "tvg-";
	private static final String ATTR_TVG_SUFFIX = "-tvg";
	private static final String INVALID_STREAM_URL = "http://0.0.0.0:1234";

	private static M3UParser mInstance = null;
	private ChannelItem mItem;
	private ArrayList<ChannelItem> mListItems;

	private static Context context;
	private static String authority;



	public M3UParser(Context context, String authority) {
		this.context = context;
		this.authority = authority;

		mListItems = new ArrayList<ChannelItem>();
	}

	public static final M3UParser getInstance() {
		if (mInstance == null) {
			mInstance = new M3UParser(context,authority);
		}

		return mInstance;
	}



	ArrayList<String> groupList;
	public void parse(String[] filename) {
		//try
		groupList = new ArrayList<>();
		groupList.clear();
		{
			//BufferedReader br = new BufferedReader(new InputStreamReader(
			//		new FileInputStream(filename)));
			//String tmp = null;
			//while ((tmp = shrink(br.readLine())) != null)
			for(String tmp : filename)
			{
				if (tmp.startsWith(PREFIX_EXTM3U)) {
//					handler.onReadEXTM3U(parseHead(shrink(tmp.replaceFirst(
//							PREFIX_EXTM3U, EMPTY_STRING))));
				} else if (tmp.startsWith(PREFIX_EXTINF)) {
					// The old item must be committed when we meet a new item.
					mItem = parseItem(shrink(tmp.replaceFirst(
							PREFIX_EXTINF, EMPTY_STRING)));
				} else if (tmp.startsWith(PREFIX_COMMENT)) {
					// Do nothing.
				} else if (tmp.equals(EMPTY_STRING)) {
					// Do nothing.
				} else { // The single line is treated as the stream URL.
					updateURL(tmp);
				}
			}
			//flush(handler);

			//br.close();
		} //catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}



	private String shrink(String str) {
		return str == null ? null : str.trim();
	}


	private void updateURL(String url) {
		if (mItem != null && !INVALID_STREAM_URL.equals(url) && !url.equals("\r")) {
			mItem.streamURL   = url.trim();

			mListItems.add(mItem);
			ChannelUpdatetoDB(mItem);
		}
	}

	private void putAttr(Map<String, String> map, String key, String value) {
		map.put(key, value);
	}

	private String getAttr(Map<String, String> map, String key) {
		String value = map.get(key);
		if (value == null) {
			value = map.get(ATTR_TVG_PREFIX + key);
			if (value == null) {
				value = map.get(key + ATTR_TVG_SUFFIX);
			}
		}
		return value;
	}


	String preChanel = "";
	private void ChannelUpdatetoDB(ChannelItem item) {

		ContentValues val = new ContentValues();
		String selection;

		if (preChanel.equals(item.channelName))
		{
			return;
		}

		preChanel = item.channelName;

		val.put(ChannelContracts.ItemTable.mChannelName, item.channelName);
		val.put(ChannelContracts.ItemTable.mGroupTitle, item.groupTitle);
		val.put(ChannelContracts.ItemTable.mLogoURL, item.logoURL);
		val.put(ChannelContracts.ItemTable.mStreamURL, item.streamURL);
		val.put(ChannelContracts.ItemTable.flag_groupChanel, item.flag_groupChanel);

		selection = ChannelContracts.ItemTable.mChannelName + " = ?" ;


		final Uri contentUri = ChannelContracts.ItemTable.getContentUri(this.authority);


		if (context.getContentResolver().update(contentUri, val,
				selection, new String[] {item.channelName}) < 1) {
			context.getContentResolver().insert(contentUri, val);
		}
	}

	private ChannelItem parseItem(String words) {
		Map<String, String> attr = parseAttributes(words);

		ChannelItem item = new ChannelItem();
		item.channelName = getAttr(attr, ATTR_CHANNEL_NAME);
		item.groupTitle = getAttr(attr, ATTR_GROUP_TITLE);
		item.logoURL    = getAttr(attr, ATTR_LOGO);

		if(!groupList.contains(item.groupTitle))
		{
			groupList.add(item.groupTitle);
			item.flag_groupChanel = 1;
		}
		else
		{
			item.flag_groupChanel = 0;
		}

		if(item.groupTitle.equals("K+ HD") || item.groupTitle.equals("Quảng Cáo") )
		{
			item.flag_groupChanel = 0;
		}
		return item;
	}

	private Map<String, String> parseAttributes(String words) {
		Map<String, String> attr = new HashMap<String, String>();
		if (words == null || words.equals(EMPTY_STRING)) {
			return attr;
		}
		Status status = Status.READY;
		String tmp = words;
		StringBuffer connector = new StringBuffer();
		int i = 0;
		char c = tmp.charAt(i);
		if (c == '-' || Character.isDigit(c)) {
			connector.append(c);
			while (++i < tmp.length()) {
				c = tmp.charAt(i);
				if (Character.isDigit(c)) {
					connector.append(c);
				} else {
					break;
				}
			}
			putAttr(attr, ATTR_DURATION, connector.toString());
			tmp = shrink(tmp.replaceFirst(connector.toString(), EMPTY_STRING));
			reset(connector);
			i = 0;
		}
		String key = EMPTY_STRING;
		boolean startWithQuota = false;
		while (i < tmp.length()) {
			c = tmp.charAt(i++);
			switch (status) {
			case READY:
				if (Character.isWhitespace(c)) {
					// Do nothing
				} else if (c == ',') {
					putAttr(attr, ATTR_CHANNEL_NAME, tmp.substring(i));
					i = tmp.length();
				} else {
					connector.append(c);
					status = Status.READING_KEY;
				}
				break;
			case READING_KEY:
				if (c == '=') {
					key = shrink(key + connector.toString());
					reset(connector);
					status = Status.KEY_READY;
				} else {
					connector.append(c);
				}
				break;
			case KEY_READY:
				if (!Character.isWhitespace(c)) {
					if (c == '"') {
						startWithQuota = true;
					} else {
						connector.append(c);
					}
					status = Status.READING_VALUE;
				}
				break;
			case READING_VALUE:
				if (startWithQuota) {
					connector.append(c);
					int end = tmp.indexOf("\"", i);
					end = end == -1 ? tmp.length() : end;
					connector.append(tmp.substring(i, end));
					startWithQuota = false;
					putAttr(attr, key, connector.toString());
					i = end + 1;
					reset(connector);
					key = EMPTY_STRING;
					status = Status.READY;
					break;
				}
				if (Character.isWhitespace(c)) {
					if (connector.length() > 0) {
						putAttr(attr, key, connector.toString());
						reset(connector);
					}
					key = EMPTY_STRING;
					status = Status.READY;
				} else {
					connector.append(c);
				}
				break;
			default:
				break;
			}
		}
		if (!key.equals(EMPTY_STRING) && connector.length() > 0) {
			putAttr(attr, key, connector.toString());
			reset(connector);
		}
		return attr;
	}

	private int convert2int(String value) {
		int ret = -1;
		try {
			ret = Integer.parseInt(value);
		} catch (Exception e) {
			ret = -1;
		}
		return ret;
	}

	private void reset(StringBuffer buffer) {
		buffer.delete(0, buffer.length());
	}

	private static enum Status {
		READY, READING_KEY, KEY_READY, READING_VALUE,
	}
}
