package com.epgis.packmanage.gps;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

public final class DownloadUtil {
	/**
	 * 手机端端存放apk的路径
	 * */
	private String filePath = "mnt/sdcard/epgis/app/";
	/**
	 * 文件名截取规则
	 * */
	private String fileNameRule = "_V";
	/**
	 * 文件名
	 * */
	private String fileName;
	private Context context;

	/**
	 * 构造函数
	 * 
	 * @param context
	 * */
	public DownloadUtil(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	/**
	 * 更新文件
	 * */
	public boolean update() {
		fileName = getFileName();
		if (!fileName.equals("") && fileName != null) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(new File(filePath, fileName)),
					"application/vnd.android.package-archive");
			context.startActivity(intent);
			return true;
		}
		return false;
	}
	/**
	 * 检测程序是否需要更新
	 * */
	public boolean isUpdate() {
		String filetemp = getFileName();
		if (filetemp != null && !filetemp.equals("")) {
			return true;
		}
		return false;
		
	}
	/**
	 * 获取与程序对应的文件名
	 * */
	private String getFileName() {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			String packageName = info.packageName;
			int versions = info.versionCode;
			packageName = packageName.replace(".", "&dot");
			String virtualfileNameFromCurrent = packageName.split("&dot")[packageName
					.split("&dot").length - 1].toUpperCase()
					+ (fileNameRule + versions + "");
			String realFileNameFromSdcard = getFileNameByRule(virtualfileNameFromCurrent);
			return realFileNameFromSdcard;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";

	}

	/**
	 * 通过规则拼装文件名
	 * */
	private String getFileNameByRule(String virtualfileNameFromCurrent) 
	{
		ArrayList<String> fileNameByRule = new ArrayList<String>();
		String fileNameFormCurrent[] = virtualfileNameFromCurrent
				.split(fileNameRule);
		if (fileNameFormCurrent != null && fileNameFormCurrent.length == 2) 
		{
			File file = new File(filePath);
			String fileNamesFormSdcard[] = file.list();
			if (fileNamesFormSdcard == null || fileNamesFormSdcard.length == 0) 
			{
				return "";
			}
			for (int i = 0; i < fileNamesFormSdcard.length; i++) 
			{
				String fileNameFormSdcard[] = fileNamesFormSdcard[i]
						.split(fileNameRule);
				if (fileNameFormSdcard != null
						&& fileNameFormSdcard.length == 2) 
				{
					if (fileNameFormSdcard[0].toUpperCase().equals(fileNameFormCurrent[0])
							&& fileNameFormSdcard[1].replace(".", "").substring(0,fileNameFormSdcard[1].replace(".", "").length()-3)
									.compareTo(fileNameFormCurrent[1]) > 0) 
					{
						fileNameByRule.add(fileNamesFormSdcard[i]);
					}
				}
			}
			if (fileNameByRule != null && fileNameByRule.size() == 1) 
			{
				return fileNameByRule.get(0);
			} 
			else if (fileNameByRule.size() > 1) 
			{
				String temp = "";
				for (int i = 0; i < fileNameByRule.size() - 1; i++) 
				{
					for (int j = i + 1; j < fileNameByRule.size() - 1; j++) 
					{
						if (fileNameByRule.get(i).compareTo(
								fileNameByRule.get(j)) > 0)
						{
							temp = fileNameByRule.get(i);
						}

					}
				}
				return temp;
			}
		}
		return "";
	}
}
