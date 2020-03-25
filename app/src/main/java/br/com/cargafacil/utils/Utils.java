package br.com.cargafacil.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.DecimalFormat;

import static br.com.cargafacil.utils.Mask.unmask;

public abstract class Utils {

    public final static String CONFIG_FILE = "CONFIG_FILE";
    public final static String MY_LOG_TAG = "VAPSTOR";
    public final static int MY_PERMISSION_REQUEST_USE_BLUETOOTH = 10;
    public static long elapsedTime = 0;

    final byte[][] byteCommands = {
            { 0x1b, 0x40, 0x0a },// 复位打印机
            { 0x0a }, //打印并走纸
            { 0x1b, 0x4d, 0x00 },// 标准ASCII字体
            { 0x1b, 0x4d, 0x01 },// 压缩ASCII字体
            { 0x1d, 0x21, 0x00 },// 字体不放大
            { 0x1d, 0x21, 0x11 },// 宽高加倍
            { 0x1d, 0x21, 0x22 },// 宽高加倍
            { 0x1d, 0x21, 0x33 },// 宽高加倍
            { 0x1b, 0x45, 0x00 },// 取消加粗模式
            { 0x1b, 0x45, 0x01 },// 选择加粗模式
            { 0x1b, 0x7b, 0x00 },// 取消倒置打印
            { 0x1b, 0x7b, 0x01 },// 选择倒置打印
            { 0x1d, 0x42, 0x00 },// 取消黑白反显
            { 0x1d, 0x42, 0x01 },// 选择黑白反显
            { 0x1b, 0x56, 0x00 },// 取消顺时针旋转90°
            { 0x1b, 0x56, 0x01 },// 选择顺时针旋转90°
            { 0x0a, 0x1d, 0x56, 0x42, 0x01, 0x0a },//切刀指令
            { 0x1b, 0x42, 0x03, 0x03 },//蜂鸣指令
            { 0x1b, 0x70, 0x00, 0x50, 0x50 },//钱箱指令
            { 0x10, 0x14, 0x00, 0x05, 0x05 },//实时弹钱箱指令
            { 0x1c, 0x2e },// 进入字符模式
            { 0x1c, 0x26 }, //进入中文模式
            { 0x1f, 0x11, 0x04 }, //打印自检页
            { 0x1b, 0x63, 0x35, 0x01 }, //禁止按键
            { 0x1b, 0x63, 0x35, 0x00 }, //取消禁止按键
            { 0x1b, 0x2d, 0x02, 0x1c, 0x2d, 0x02 }, //设置下划线
            { 0x1b, 0x2d, 0x00, 0x1c, 0x2d, 0x00 }, //取消下划线
            { 0x1f, 0x11, 0x03 }, //打印机进入16进制模式
    };
    /***************************条                          码***************************************************************/
    final String[] codebar = { "UPC_A", "UPC_E", "JAN13(EAN13)", "JAN8(EAN8)",
            "CODE39", "ITF", "CODABAR", "CODE93", "CODE128", "QR Code" };
    final byte[][] byteCodebar = {
            { 0x1b, 0x40 },// 复位打印机
            { 0x1b, 0x40 },// 复位打印机
            { 0x1b, 0x40 },// 复位打印机
            { 0x1b, 0x40 },// 复位打印机
            { 0x1b, 0x40 },// 复位打印机
            { 0x1b, 0x40 },// 复位打印机
            { 0x1b, 0x40 },// 复位打印机
            { 0x1b, 0x40 },// 复位打印机
            { 0x1b, 0x40 },// 复位打印机
            { 0x1b, 0x40 },// 复位打印机
    };
    /******************************************************************************************************/

    public static String sha256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String formatValuesToString(int valor) {
        try {
            String pattern = "###,###";
            DecimalFormat decimalFormat = new DecimalFormat(pattern);
            return decimalFormat.format(valor);
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public static int formatValuesToInteger(String valor) {
        try {
            return Integer.parseInt(unmask(valor));
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }



}
