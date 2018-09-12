package com.xqlh.handscoordination.excele;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * Created by Administrator on 2018/3/6.
 */

public class Excele {

    private WritableWorkbook wwb;
    private String excelPath;
    private File excelFile;
    private Activity activity;

    public Excele(Activity activity, String excelPath) {
        this.excelPath = excelPath;
        this.activity = activity;
        excelFile = new File(excelPath);
        createExcel(excelFile);
    }

    // 创建excel表.
    public void createExcel(File file) {
        WritableSheet ws = null;
        try {
            if (!file.exists()) {
                wwb = Workbook.createWorkbook(file);
                ws = wwb.createSheet("sheet1", 0);
                // 在指定单元格插入数据
                Label lbl1 = new Label(0, 0, "学号");
                Label lbl2 = new Label(1, 0, "姓名");
                Label lbl3 = new Label(2, 0, "测试时间");
                Label lbl4 = new Label(3, 0, "耗时");
                Label lbl5 = new Label(4, 0, "脱轨次数");

                ws.addCell(lbl1);
                ws.addCell(lbl2);
                ws.addCell(lbl3);
                ws.addCell(lbl4);
                ws.addCell(lbl5);

                // 从内存中写入文件中
                wwb.write();
                wwb.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeToExcel(List<String> list) {
        try {
            Workbook oldWwb = Workbook.getWorkbook(excelFile);
            wwb = Workbook.createWorkbook(excelFile, oldWwb);
            WritableSheet ws = wwb.getSheet(0);
            // 当前行数
            int row = ws.getRows();
            Log.i("lz", list + "writeToExcel " + list.size());
            for (int i = 1; i <= list.size(); i++) {
                Label labi = new Label(i - 1, row, list.get(i - 1) + "");
                ws.addCell(labi);
            }
            // 从内存中写入文件中,只能刷一次.
            wwb.write();
            wwb.close();
            Toast.makeText(activity, "导出成功", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
