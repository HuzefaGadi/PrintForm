package com.huzefagadi.rashida.printform.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.format.Time;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Vector;

public class Utils {
	/**
	 * ��ʾ��Ϣ��
	 */
	public static void ShowMessage(Context context, String msg){
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}

	 public static byte[] Bitmap2Bytes(Bitmap bm) {
		 ByteArrayOutputStream baos = new ByteArrayOutputStream();
		 bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		 return baos.toByteArray();
	}

	 public static Bitmap Bytes2Bimap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}
	}

	 /**����ͼƬ ��ȡ��ӡ����**/
	 public static byte[] getReadBitMapBytes(Bitmap bitmap) {
	     /***ͼƬ���ˮӡ**/
	     //bitmap = createBitmap(bitmap);
	     byte[] bytes = null;  //��ӡ����
	     int width = bitmap.getWidth();
	     int height = bitmap.getHeight();
	     System.out.println("width=" + width + ", height=" + height);
	     int heightbyte = (height - 1) / 8 + 1;
	     int bufsize = width * heightbyte;
	     int m1, n1;
	     byte[] maparray = new byte[bufsize];
	     byte[] rgb = new byte[3];
	     int []pixels = new int[width * height]; //ͨ��λͼ�Ĵ�С�������ص�����

	     bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
	       /**����ͼƬ ��ȡλͼ����**/
	     for (int j = 0;j < height; j++) {
	       for (int i = 0; i < width; i++) {
	            int pixel = pixels[width * j + i]; /**��ȡ�ңǣ�ֵ**/
	            int r = Color.red(pixel);
	             int g = Color.green(pixel);
	           int b = Color.blue(pixel);
	            //System.out.println("i=" + i + ",j=" + j + ":(" + r + ","+ g+ "," + b + ")");
	           	rgb[0] = (byte)r;
	            rgb[1] = (byte)g;
	            rgb[2] = (byte)b;
	            if (r != 255 || g != 255 || b != 255){//������ǿհ׵Ļ��ú�ɫ���    �������ͯЬҪ������ɫ�����ﴦ��
	                   m1 = (j / 8) * width + i;
	                    n1 = j - (j / 8) * 8;
	                    maparray[m1] |= (byte)(1 << 7 - ((byte)n1));
	                }
	        }
	     }
	     byte[] b = new byte[322];
	     int line = 0;
	     int j = 0;
	     ByteArrayOutputStream baos = new ByteArrayOutputStream();

	     /**��λͼ���ݽ��д���**/
	     for (int i = 0; i < maparray.length; i++) {
	         b[j] = maparray[i];
	         j++;
	       if (j == 322) {  /**  322ͼƬ�Ŀ� **/
	            if (line < ((322 - 1) / 8)) {
	                byte[] lineByte = new byte[329];
	                 byte nL = (byte) 322;
	                byte nH = (byte) (322 >> 8);
	                 int index = 5;
	                 /**��Ӵ�ӡͼƬǰ���ַ�  ÿ�е� ������8λ**/
	                lineByte[0] = 0x1B;
	               lineByte[1] = 0x2A;
	                lineByte[2] = 1;
	                lineByte[3] = nL;
	                lineByte[4] = nH;
	                /**copy ��������**/
	                System.arraycopy(b, 0, lineByte, index, b.length);

	                lineByte[lineByte.length - 2] = 0x0D;
	                 lineByte[lineByte.length - 1] = 0x0A;
	                 baos.write(lineByte, 0, lineByte.length);
	                try {
	                     baos.flush();
	                 } catch (IOException e) {
	                     e.printStackTrace();
	                }
	                 line++;
	             }
	             j = 0;
	         }
	     }
	    bytes = baos.toByteArray();
	     return bytes;
	 }

	 // ��ͼƬ���ˮӡ
	    private Bitmap createBitmap(Bitmap src) {
	        Time t = new Time();
	        t.setToNow();
	        int w = src.getWidth();
	        int h = src.getHeight();
	        String mstrTitle = t.year + " �� " + (t.month +1) + " �� " + t.monthDay+" ��";
	        Bitmap bmpTemp = Bitmap.createBitmap(w, h, Config.ARGB_8888);
	        Canvas canvas = new Canvas(bmpTemp);
	        Paint p = new Paint();
	        String familyName = "����";
	        Typeface font = Typeface.create(familyName, Typeface.BOLD);
	        p.setColor(Color.BLACK);
	        p.setTypeface(font);
	        p.setTextSize(33);
	        canvas.drawBitmap(src, 0, 0, p);
	        canvas.drawText(mstrTitle, 20, 310, p);
	        canvas.save(Canvas.ALL_SAVE_FLAG);
	        canvas.restore();
	        return bmpTemp;
	   }

	    /**
	     * �ַ����ָ�,ͨ��ָ���ķ��ŷָ��ַ���
	     */
	    public static String[] StringSplit(String original, String regex){
	        // ȡ�Ӵ�����ʼλ�á�
	        int startIndex = 0;
	        // ����������ȷ���Vector�С�
	        Vector v = new Vector();
	        // ���صĽ���ַ������顡
	        String[] str = null;
	        // �洢ȡ�Ӵ�ʱ��ʼλ�á�
	        int index = 0;
	        // ���ƥ���Ӵ���λ�á�
	        startIndex = original.indexOf(regex);
	        //
	        System.out.println("0" + startIndex);
	        // �����ʼ�ַ�����λ��С���ַ����ĳ��ȣ���֤��û��ȡ���ַ���ĩβ����
	        // -1����ȡ����ĩβ��
	        while (startIndex < original.length() && startIndex != -1)
	        {
	            String temp = original.substring(index, startIndex);
	            System.out.println(" " + startIndex);
	            // ȡ�Ӵ�����
	            v.addElement(temp);
	            // ����ȡ�Ӵ�����ʼλ�á���
	            index = startIndex + regex.length();
	            // ���ƥ���Ӵ���λ�á���
	            startIndex = original.indexOf(regex, startIndex + regex.length());
	        }
	        // ȡ�������Ӵ���
	        v.addElement(original.substring(index + 1 - regex.length()));
	        // ��Vector����ת�������顡
	        str = new String[v.size()];
	        for (int i = 0; i < v.size(); i++)
	        {
	            str[i] = (String) v.elementAt(i);
	        }
	        // �������ɵ����顡
	        return str;
	    }
}
