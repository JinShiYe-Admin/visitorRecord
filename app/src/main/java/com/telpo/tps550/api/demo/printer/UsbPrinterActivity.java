package com.telpo.tps550.api.demo.printer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Hashtable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.demo.R;
import com.telpo.tps550.api.printer.UsbThermalPrinter;
import com.telpo.tps550.api.util.StringUtil;
import com.telpo.tps550.api.util.SystemUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class UsbPrinterActivity extends Activity {

	private String printVersion;
	private final int NOPAPER = 3;
	private final int LOWBATTERY = 4;
	private final int PRINTVERSION = 5;
	private final int PRINTBARCODE = 6;
	private final int PRINTQRCODE = 7;
	private final int PRINTPAPERWALK = 8;
	private final int PRINTCONTENT = 9;
	private final int CANCELPROMPT = 10;
	private final int PRINTERR = 11;
	private final int OVERHEAT = 12;
	private final int MAKER = 13;
	private final int PRINTPICTURE = 14;
	private final int NOBLACKBLOCK = 15;
	private final int PRINTSHORTCONTENT = 16;
	private final int PRINTLONGPICTURE = 17;
	private final int PRINTLONGTEXT = 18;
	private final int PRINTBLACK = 19;
	public static String printContent1 = "\n             烧烤" + "\n----------------------------"
			+ "\n日期：2015-01-01 16:18:20" + "\n卡号：12378945664" + "\n单号：1001000000000529142"
			+ "\n----------------------------" + "\n  项目    数量   单价  小计" + "\n秘制烤羊腿  1    56    56"
			+ "\n黯然牛排    2    24    48" + "\n烤火鸡      2    50    100" + "\n炭烧鳗鱼    1    40    40"
			+ "\n烤全羊      1    200   200" + "\n荔枝树烧鸡  1    50    50" + "\n冰镇乳鸽    2    23    46"
			+ "\n秘制烤羊腿  1    56    56" + "\n黯然牛排    2    24    48" + "\n烤火鸡      2    50    100"
			+ "\n炭烧鳗鱼    1    40    40" + "\n烤全羊      1    200   200" + "\n荔枝树烧鸡  1    50    50"
			+ "\n冰镇乳鸽    2    23    46" + "\n秘制烤羊腿  1    56    56" + "\n黯然牛排    2    24    48"
			+ "\n烤火鸡      2    50    100" + "\n炭烧鳗鱼    1    40    40" + "\n烤全羊      1    200   200"
			+ "\n荔枝树烧鸡  1    50    50" + "\n冰镇乳鸽    2    23    46" + "\n秘制烤羊腿  1    56    56"
			+ "\n黯然牛排    2    24    48" + "\n烤火鸡      2    50    100" + "\n炭烧鳗鱼    1    40    40"
			+ "\n烤全羊      1    200   200" + "\n荔枝树烧鸡  1    50    50" + "\n冰镇乳鸽    2    23    46"
			+ "\n冰镇乳鸽    2    23    46" + "\n秘制烤羊腿  1    56    56" + "\n黯然牛排    2    24    48"
			+ "\n烤火鸡      2    50    100" + "\n炭烧鳗鱼    1    40    40" + "\n烤全羊      1    200   200"
			+ "\n荔枝树烧鸡  1    50    50" + "\n冰镇乳鸽    2    23    46" + "\n 合计：1000：00元" + "\n----------------------------"
			+ "\n本卡金额：10000.00" + "\n累计消费：1000.00" + "\n本卡结余：9000.00" + "\n----------------------------"
			+ "\n 地址：广东省佛山市南海区桂城街道桂澜南路45号鹏瑞利广场A317.B-18号铺" + "\n欢迎您的再次光临\n";

	private LinearLayout print_text, print_pic;
	private TextView text_index, pic_index, textPrintVersion;
	MyHandler handler;
	private EditText editTextLeftDistance, editTextLineDistance, editTextWordFont, editTextPrintGray, editTextBarcode,
			editTextQrcode, editTextPaperWalk, editTextContent, edittext_maker_search_distance,
			edittext_maker_walk_distance, edittext_string_offset, edittext_short_text;
	private Button buttonBarcodePrint, buttonPaperWalkPrint, buttonContentPrint, buttonQrcodePrint,
			buttonGetExampleText, buttonGetZhExampleText, buttonGetFRExampleText, buttonClearText, button_maker,
			button_print_picture, button_print_short_text, button_print_long_picture, button_print_long_text,
			button_print_black;
	private ToggleButton button_auto_linefeed;
	private String Result;
	private Boolean nopaper = false;
	private boolean LowBattery = false;

	public static String barcodeStr;
	public static String qrcodeStr;
	public static int paperWalk;
	public static String printContent;
	private int leftDistance = 0;
	private int lineDistance;
	private int wordFont;
	private int printGray;
	public String shortContent;
	private int stringOffset;
	private ProgressDialog progressDialog;
	private final static int MAX_LEFT_DISTANCE = 255;
	ProgressDialog dialog;
	UsbThermalPrinter mUsbThermalPrinter = new UsbThermalPrinter(UsbPrinterActivity.this);
	private String picturePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/111.bmp";
	private String picturePath2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/222.png";
	private String picturePath3 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/333.png";
	private String picturePath4 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/444.png";

	private class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NOPAPER:
				noPaperDlg();
				break;
			case LOWBATTERY:
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(UsbPrinterActivity.this);
				alertDialog.setTitle(R.string.operation_result);
				alertDialog.setMessage(getString(R.string.LowBattery));
				alertDialog.setPositiveButton(getString(R.string.dialog_comfirm),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
							}
						});
				alertDialog.show();
				break;
			case NOBLACKBLOCK:
				Toast.makeText(UsbPrinterActivity.this, R.string.maker_not_find, Toast.LENGTH_SHORT).show();
				break;
			case PRINTVERSION:
				dialog.dismiss();
				if (msg.obj.equals("1")) {
					textPrintVersion.setText(printVersion);
				} else {
					Toast.makeText(UsbPrinterActivity.this, R.string.operation_fail, Toast.LENGTH_LONG).show();
				}
				break;
			case PRINTBARCODE:
				new barcodePrintThread().start();
				break;
			case PRINTQRCODE:
				new qrcodePrintThread().start();
				break;
			case PRINTPAPERWALK:
				new paperWalkPrintThread().start();
				break;
			case PRINTCONTENT:
				new contentPrintThread().start();
				break;
			case MAKER:
				new MakerThread().start();
				break;
			case PRINTPICTURE:
				new printPicture().start();
				break;
			case CANCELPROMPT:
				if (progressDialog != null && !UsbPrinterActivity.this.isFinishing()) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				break;
			case OVERHEAT:
				AlertDialog.Builder overHeatDialog = new AlertDialog.Builder(UsbPrinterActivity.this);
				overHeatDialog.setTitle(R.string.operation_result);
				overHeatDialog.setMessage(getString(R.string.overTemp));
				overHeatDialog.setPositiveButton(getString(R.string.dialog_comfirm),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
							}
						});
				overHeatDialog.show();
				break;
			case PRINTSHORTCONTENT:
				new ShortTextPrintThread().start();
				break;
			case PRINTLONGPICTURE:
				new printLongPicture().start();
				break;
			case PRINTLONGTEXT:
				new printLongText().start();
				break;
			case PRINTBLACK:
				new printBlackPicture().start();
				break;
			default:
				Toast.makeText(UsbPrinterActivity.this, "Print Error!", Toast.LENGTH_LONG).show();
				break;
			}
		}
	}

	private void initView() {
		print_text = (LinearLayout) findViewById(R.id.print_text);
		print_pic = (LinearLayout) findViewById(R.id.print_code_and_pic);
		text_index = (TextView) findViewById(R.id.index_text);
		pic_index = (TextView) findViewById(R.id.index_pic);
		edittext_string_offset = (EditText) findViewById(R.id.edittext_string_offset);
		edittext_short_text = (EditText) findViewById(R.id.edittext_short_text);
		button_print_short_text = (Button) findViewById(R.id.button_print_short_text);
		button_auto_linefeed = (ToggleButton) findViewById(R.id.auto_linefeed);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.usbprint_text);
		initView();
		savepic();
		if (!isSupportAutoBreak()) {
			button_auto_linefeed.setEnabled(isSupportAutoBreak());
			Toast.makeText(this, getString(R.string.not_support), Toast.LENGTH_SHORT).show();
		}
		handler = new MyHandler();
		buttonBarcodePrint = (Button) findViewById(R.id.print_barcode);

		IntentFilter pIntentFilter = new IntentFilter();
		pIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		pIntentFilter.addAction("android.intent.action.BATTERY_CAPACITY_EVENT");
		registerReceiver(printReceive, pIntentFilter);

		editTextLeftDistance = (EditText) findViewById(R.id.set_leftDistance);
		editTextLineDistance = (EditText) findViewById(R.id.set_lineDistance);
		editTextWordFont = (EditText) findViewById(R.id.set_wordFont);
		editTextPrintGray = (EditText) findViewById(R.id.set_printGray);
		editTextBarcode = (EditText) findViewById(R.id.set_Barcode);
		editTextPaperWalk = (EditText) findViewById(R.id.set_paperWalk);
		editTextContent = (EditText) findViewById(R.id.set_content);
		textPrintVersion = (TextView) findViewById(R.id.print_version);
		editTextQrcode = (EditText) findViewById(R.id.set_Qrcode);
		edittext_maker_search_distance = (EditText) findViewById(R.id.edittext_maker_search_distance);
		edittext_maker_walk_distance = (EditText) findViewById(R.id.edittext_maker_walk_distance);
		buttonQrcodePrint = (Button) findViewById(R.id.print_qrcode);
		if (SystemUtil.getDeviceType() == StringUtil.DeviceModelEnum.TPS900.ordinal()) {
			editTextPrintGray.setText("5");
		}

		buttonQrcodePrint.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String exditText = editTextPrintGray.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(UsbPrinterActivity.this,
							getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				printGray = Integer.parseInt(exditText);
				if (printGray < 0 || printGray > 7) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
					return;
				}
				qrcodeStr = editTextQrcode.getText().toString();
				if (qrcodeStr == null || qrcodeStr.length() == 0) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.input_print_data), Toast.LENGTH_SHORT)
							.show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						progressDialog = ProgressDialog.show(UsbPrinterActivity.this,
								getString(R.string.D_barcode_loading), getString(R.string.generate_barcode_wait));
						handler.sendMessage(handler.obtainMessage(PRINTQRCODE, 1, 0, null));
					} else {
						Toast.makeText(UsbPrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG)
								.show();
					}
				}

			}
		});
		editTextContent.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent arg1) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
		buttonBarcodePrint.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String exditText = editTextPrintGray.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(UsbPrinterActivity.this,
							getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				printGray = Integer.parseInt(exditText);
				if (printGray < 0 || printGray > 7) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
					return;
				}
				barcodeStr = editTextBarcode.getText().toString();
				if (barcodeStr == null || barcodeStr.length() == 0) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.empty), Toast.LENGTH_LONG).show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						progressDialog = ProgressDialog.show(UsbPrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));
						handler.sendMessage(handler.obtainMessage(PRINTBARCODE, 1, 0, null));
					} else {
						Toast.makeText(UsbPrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG)
								.show();
					}
				}
			}
		});

		buttonPaperWalkPrint = (Button) findViewById(R.id.print_paperWalk);
		buttonPaperWalkPrint.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String exditText;
				exditText = editTextPaperWalk.getText().toString();
				if (exditText == null || exditText.length() == 0) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.empty), Toast.LENGTH_LONG).show();
					return;
				}
				if (Integer.parseInt(exditText) < 1 || Integer.parseInt(exditText) > 255) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.walk_paper_intput_value),
							Toast.LENGTH_LONG).show();
					return;
				}
				paperWalk = Integer.parseInt(exditText);
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						progressDialog = ProgressDialog.show(UsbPrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));
						handler.sendMessage(handler.obtainMessage(PRINTPAPERWALK, 1, 0, null));
					} else {
						Toast.makeText(UsbPrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG)
								.show();
					}
				}
			}
		});

		buttonClearText = (Button) findViewById(R.id.clearText);
		buttonClearText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editTextContent.setText("");
			}
		});
		buttonGetExampleText = (Button) findViewById(R.id.getPrintExample);
		buttonGetExampleText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String str = "\n---------------------------\n" + "Print Test:\n" + "Device Base Information\n"
						+ "Printer Version:\n" + "V05.2.0.3\n" + "Printer Gray:3\n" + "Soft Version:\n"
						+ "Demo.G50.0.Build140313\n" + "Battery Level:100%\n" + "CSQ Value:24\n"
						+ "IMEI:86378902177527\n" + "---------------------------\n" + "---------------------------\n"
						+ "Print Test:\n" + "Device Base Information\n" + "Printer Version:\n" + "V05.2.0.3\n"
						+ "Printer Gray:3\n" + "Soft Version:\n" + "Demo.G50.0.Build140313\n" + "Battery Level:100%\n"
						+ "CSQ Value:24\n" + "IMEI:86378902177527\n" + "---------------------------\n"
						+ "---------------------------\n" + "Print Test:\n" + "Device Base Information\n"
						+ "Printer Version:\n" + "V05.2.0.3\n" + "Printer Gray:3\n" + "Soft Version:\n"
						+ "Demo.G50.0.Build140313\n" + "Battery Level:100%\n" + "CSQ Value:24\n"
						+ "IMEI:86378902177527\n" + "---------------------------\n";
				editTextContent.setText(str);
			}
		});

		buttonGetZhExampleText = (Button) findViewById(R.id.getZhPrintExample);
		buttonGetZhExampleText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String str = "\n             烧烤" + "\n---------------------------" + "\n日期：2015-01-01 16:18:20"
						+ "\n卡号：12378945664" + "\n单号：1001000000000529142" + "\n---------------------------"
						+ "\n    项目        数量   单价  小计" + "\n秘制烤羊腿    1      56      56"
						+ "\n烤火鸡            2      50      100" + "\n烤全羊            1      200    200"
						+ "\n秘制烤鸡腿    1      56      56" + "\n烤牛腿            2      50      100"
						+ "\n烤猪蹄            1      200    200" + "\n秘制烤牛腿    1      56      56"
						+ "\n烤火鸡            2      50      100" + "\n烤全羊            1      200    200"
						+ "\n秘制烤猪腿    1      56      56" + "\n烤火鸡            2      50      100"
						+ "\n烤全牛            1      200    200" + "\n特色烤鸭腿    1      56      56"
						+ "\n烤土鸡            2      50      100" + "\n烤全羊            1      200    200"
						+ "\n秘制烤火腿    1      56      56" + "\n烤火鸡            2      50      100"
						+ "\n烤全羊            1      200    200" + "\n秘制烤鸡腿    1      56      56"
						+ "\n烤火鸡            2      50      100" + "\n烤全羊            1      200    200"
						+ "\n秘制烤火腿    1      56      56" + "\n烤火鸡            2      50      100"
						+ "\n烤全羊            1      200    200" + "\n秘制烤牛筋    1      56      56"
						+ "\n烤土鸡            2      50      100" + "\n烤白鸽            1      200    200"
						+ "\n秘制鸭下巴    1      56      56" + "\n烤火鸡            2      50      100"
						+ "\n烤全牛            1      200    200" + "\n 合计：1000:00元" + "\n----------------------------"
						+ "\n本卡金额：10000.00" + "\n累计消费：1000.00" + "\n本卡结余：9000.00" + "\n----------------------------"
						+ "\n 地址：广东省佛山市南海区桂城街道桂澜南路45号鹏瑞利广场A317.B-18号铺" + "\n欢迎您的再次光临\n";
				editTextContent.setText(str);
			}
		});

		buttonGetFRExampleText = (Button) findViewById(R.id.getFrPrintExample);
		buttonGetFRExampleText.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String str = "\nÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóô\n";
				editTextContent.setText(str);
			}

		});

		buttonContentPrint = (Button) findViewById(R.id.print_content);
		buttonContentPrint.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String exditText;
				exditText = editTextLeftDistance.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(UsbPrinterActivity.this,
							getString(R.string.left_margin) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				leftDistance = Integer.parseInt(exditText);
				exditText = editTextLineDistance.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(UsbPrinterActivity.this,
							getString(R.string.row_space) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				lineDistance = Integer.parseInt(exditText);
				printContent = editTextContent.getText().toString();
				exditText = editTextWordFont.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(UsbPrinterActivity.this,
							getString(R.string.font_size) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				wordFont = Integer.parseInt(exditText);
				exditText = editTextPrintGray.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(UsbPrinterActivity.this,
							getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				printGray = Integer.parseInt(exditText);
				if (leftDistance > MAX_LEFT_DISTANCE) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.outOfLeft), Toast.LENGTH_LONG).show();
					return;
				}
				if (lineDistance > 255) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.outOfLine), Toast.LENGTH_LONG).show();
					return;
				}
				if (wordFont > 4 || wordFont < 1) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.outOfFont), Toast.LENGTH_LONG).show();
					return;
				}
				if (printGray < 0 || printGray > 7) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
					return;
				}
				if (printContent == null || printContent.length() == 0) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.empty), Toast.LENGTH_LONG).show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						progressDialog = ProgressDialog.show(UsbPrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));
						handler.sendMessage(handler.obtainMessage(PRINTCONTENT, 1, 0, null));
					} else {
						Toast.makeText(UsbPrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG)
								.show();
					}
				}

			}
		});

		button_maker = (Button) findViewById(R.id.button_maker);
		button_maker.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (edittext_maker_search_distance.getText().length() == 0
						|| edittext_maker_walk_distance.getText().length() == 0) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.maker_error), Toast.LENGTH_LONG).show();
					return;
				}
				if (Integer.parseInt(edittext_maker_search_distance.getText().toString()) < 0
						|| Integer.parseInt(edittext_maker_search_distance.getText().toString()) > 255) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.maker_error), Toast.LENGTH_LONG).show();
					return;
				}
				if (Integer.parseInt(edittext_maker_walk_distance.getText().toString()) < 0
						|| Integer.parseInt(edittext_maker_walk_distance.getText().toString()) > 255) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.maker_error), Toast.LENGTH_LONG).show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						progressDialog = ProgressDialog.show(UsbPrinterActivity.this, getString(R.string.maker),
								getString(R.string.printing_wait));
						handler.sendMessage(handler.obtainMessage(MAKER, 1, 0, null));
					} else {
						Toast.makeText(UsbPrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG)
								.show();
					}
				}
			}
		});

		button_print_picture = (Button) findViewById(R.id.button_print_picture);
		button_print_picture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String exditText = editTextPrintGray.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(UsbPrinterActivity.this,
							getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				printGray = Integer.parseInt(exditText);
				if (printGray < 0 || printGray > 7) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						progressDialog = ProgressDialog.show(UsbPrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));
						handler.sendMessage(handler.obtainMessage(PRINTPICTURE, 1, 0, null));
					} else {
						Toast.makeText(UsbPrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG)
								.show();
					}
				}
			}
		});

		dialog = new ProgressDialog(UsbPrinterActivity.this);
		dialog.setTitle(R.string.idcard_czz);
		dialog.setMessage(getText(R.string.watting));
		dialog.setCancelable(false);
		dialog.show();

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					mUsbThermalPrinter.start(0);
					mUsbThermalPrinter.reset();
					printVersion = mUsbThermalPrinter.getVersion();
				} catch (TelpoException e) {
					e.printStackTrace();
				} finally {
					if (printVersion != null) {
						Message message = new Message();
						message.what = PRINTVERSION;
						message.obj = "1";
						handler.sendMessage(message);
					} else {
						Message message = new Message();
						message.what = PRINTVERSION;
						message.obj = "0";
						handler.sendMessage(message);
					}
				}
			}
		}).start();
		button_print_short_text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					String offset_str = edittext_string_offset.getText().toString();
					if ("".equals(offset_str)) {
						offset_str = "0";
					}
					int offset = Integer.parseInt(offset_str);
					String text = edittext_short_text.getText().toString();
					int text_length = 0;
					if (!"".equals(text)) {
						text_length = mUsbThermalPrinter.measureText(text);
					}
					if (text_length + offset > 384) {
						button_print_short_text.setEnabled(false);
						Toast.makeText(UsbPrinterActivity.this, getString(R.string.text_too_long), Toast.LENGTH_SHORT)
								.show();
						return;
					}
				} catch (TelpoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NumberFormatException e) {
					e.printStackTrace();
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.text_format_error), Toast.LENGTH_SHORT)
							.show();
					edittext_string_offset.setText("0");
					return;
				}

				String exditText;
				exditText = edittext_string_offset.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(UsbPrinterActivity.this,
							getString(R.string.left_margin) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				stringOffset = Integer.parseInt(exditText);
				shortContent = edittext_short_text.getText().toString();
				if (stringOffset > MAX_LEFT_DISTANCE) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.outOfLeft), Toast.LENGTH_LONG).show();
					return;
				}
				if (stringOffset < 0) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.param_error), Toast.LENGTH_LONG).show();
					return;
				}
				if (stringOffset % 8 != 0) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.param_error), Toast.LENGTH_LONG).show();
					return;
				}
				if (shortContent == null || shortContent.length() == 0) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.empty), Toast.LENGTH_LONG).show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						progressDialog = ProgressDialog.show(UsbPrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));
						handler.sendMessage(handler.obtainMessage(PRINTSHORTCONTENT, 1, 0, null));
					} else {
						Toast.makeText(UsbPrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG)
								.show();
					}
				}
			}
		});

		button_print_long_picture = (Button) findViewById(R.id.button_print_long_picture);
		button_print_long_picture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String exditText = editTextPrintGray.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(UsbPrinterActivity.this,
							getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				printGray = Integer.parseInt(exditText);
				if (printGray < 0 || printGray > 12) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						progressDialog = ProgressDialog.show(UsbPrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));
						handler.sendMessage(handler.obtainMessage(PRINTLONGPICTURE, 1, 0, null));
					} else {
						Toast.makeText(UsbPrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG)
								.show();
					}
				}
			}
		});

		button_print_long_text = (Button) findViewById(R.id.button_print_long_text);
		button_print_long_text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String exditText = editTextPrintGray.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(UsbPrinterActivity.this,
							getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				printGray = Integer.parseInt(exditText);
				if (printGray < 0 || printGray > 12) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						progressDialog = ProgressDialog.show(UsbPrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));
						handler.sendMessage(handler.obtainMessage(PRINTLONGTEXT, 1, 0, null));
					} else {
						Toast.makeText(UsbPrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG)
								.show();
					}
				}
			}
		});
		
		button_print_black = (Button) findViewById(R.id.button_print_black); 
		button_print_black.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String exditText = editTextPrintGray.getText().toString();
				if (exditText == null || exditText.length() < 1) {
					Toast.makeText(UsbPrinterActivity.this,
							getString(R.string.gray_level) + getString(R.string.lengthNotEnougth), Toast.LENGTH_LONG)
							.show();
					return;
				}
				printGray = Integer.parseInt(exditText);
				if (printGray < 0 || printGray > 7) {
					Toast.makeText(UsbPrinterActivity.this, getString(R.string.outOfGray), Toast.LENGTH_LONG).show();
					return;
				}
				if (LowBattery == true) {
					handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
				} else {
					if (!nopaper) {
						progressDialog = ProgressDialog.show(UsbPrinterActivity.this, getString(R.string.bl_dy),
								getString(R.string.printing_wait));
						handler.sendMessage(handler.obtainMessage(PRINTBLACK, 1, 0, null));
					} else {
						Toast.makeText(UsbPrinterActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG)
								.show();
					}
				}
			}
		});

	}

	/* Called when the application resumes */
	@Override
	protected void onResume() {
		super.onResume();
	}

	private final BroadcastReceiver printReceive = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
				int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS,
						BatteryManager.BATTERY_STATUS_NOT_CHARGING);
				int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
				int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
				// TPS390 can not print,while in low battery,whether is charging or not charging
				if (SystemUtil.getDeviceType() == StringUtil.DeviceModelEnum.TPS390.ordinal()) {
					if (level * 5 <= scale) {
						LowBattery = true;
					} else {
						LowBattery = false;
					}
				} else {
					if (status != BatteryManager.BATTERY_STATUS_CHARGING) {
						if (level * 5 <= scale) {
							LowBattery = true;
						} else {
							LowBattery = false;
						}
					} else {
						LowBattery = false;
					}
				}
			}
			// Only use for TPS550MTK devices
			else if (action.equals("android.intent.action.BATTERY_CAPACITY_EVENT")) {
				int status = intent.getIntExtra("action", 0);
				int level = intent.getIntExtra("level", 0);
				if (status == 0) {
					if (level < 1) {
						LowBattery = true;
					} else {
						LowBattery = false;
					}
				} else {
					LowBattery = false;
				}
			}
		}
	};

	private void noPaperDlg() {
		AlertDialog.Builder dlg = new AlertDialog.Builder(UsbPrinterActivity.this);
		dlg.setTitle(getString(R.string.noPaper));
		dlg.setMessage(getString(R.string.noPaperNotice));
		dlg.setCancelable(false);
		dlg.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
			}
		});
		dlg.show();
	}

	private class paperWalkPrintThread extends Thread {
		@Override
		public void run() {
			super.run();
			try {
				mUsbThermalPrinter.reset();
				mUsbThermalPrinter.walkPaper(paperWalk);
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
					nopaper = true;
				} else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
			}
		}
	}

	private class barcodePrintThread extends Thread {
		@Override
		public void run() {
			super.run();
			try {
				mUsbThermalPrinter.reset();
				mUsbThermalPrinter.setGray(printGray);
				Bitmap bitmap = CreateCode(barcodeStr, BarcodeFormat.CODE_128, 320, 176);
				if (bitmap != null) {
					mUsbThermalPrinter.printLogo(bitmap, true);
				}
				mUsbThermalPrinter.addString(barcodeStr);
				mUsbThermalPrinter.printString();
				mUsbThermalPrinter.walkPaper(20);
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
					nopaper = true;
				} else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
			}
		}
	}

	private class qrcodePrintThread extends Thread {
		@Override
		public void run() {
			super.run();
			try {
				mUsbThermalPrinter.reset();
				mUsbThermalPrinter.setGray(printGray);
				Bitmap bitmap = CreateCode(qrcodeStr, BarcodeFormat.QR_CODE, 256, 256);
				if (bitmap != null) {
					mUsbThermalPrinter.printLogo(bitmap, true);
				}
				mUsbThermalPrinter.addString(qrcodeStr);
				mUsbThermalPrinter.printString();
				mUsbThermalPrinter.walkPaper(20);
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
					nopaper = true;
				} else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
			}
		}
	}

	private class contentPrintThread extends Thread {
		@Override
		public void run() {
			super.run();
			try {
				mUsbThermalPrinter.reset();
				mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_LEFT);
				mUsbThermalPrinter.setLeftIndent(leftDistance);
				mUsbThermalPrinter.setLineSpace(lineDistance);
				if (wordFont == 4) {
					mUsbThermalPrinter.setFontSize(2);
					mUsbThermalPrinter.enlargeFontSize(2, 2);
				} else if (wordFont == 3) {
					mUsbThermalPrinter.setFontSize(1);
					mUsbThermalPrinter.enlargeFontSize(2, 2);
				} else if (wordFont == 2) {
					mUsbThermalPrinter.setFontSize(2);
				} else if (wordFont == 1) {
					mUsbThermalPrinter.setFontSize(1);
				}
				if (isSupportAutoBreak()) {
					mUsbThermalPrinter.autoBreakSet(button_auto_linefeed.isChecked());
				}
				mUsbThermalPrinter.setGray(printGray);
				mUsbThermalPrinter.addString(printContent);
				mUsbThermalPrinter.printString();
				mUsbThermalPrinter.walkPaper(20);
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
					nopaper = true;
				} else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
			}
		}
	}

	private class MakerThread extends Thread {

		@Override
		public void run() {
			super.run();
			try {
				mUsbThermalPrinter.reset();
				mUsbThermalPrinter.searchMark(Integer.parseInt(edittext_maker_search_distance.getText().toString()),
						Integer.parseInt(edittext_maker_walk_distance.getText().toString()));
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
					nopaper = true;
				} else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else if (Result.equals("com.telpo.tps550.api.printer.BlackBlockNotFoundException")) {
					handler.sendMessage(handler.obtainMessage(NOBLACKBLOCK, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
			}
		}
	}

	private class printPicture extends Thread {

		@Override
		public void run() {
			super.run();
			try {
				mUsbThermalPrinter.reset();
				mUsbThermalPrinter.setGray(printGray);
				mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);
				File file = new File(picturePath);
				if (file.exists()) {
					mUsbThermalPrinter.printLogo(BitmapFactory.decodeFile(picturePath), false);
					mUsbThermalPrinter.walkPaper(20);
				} else {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(UsbPrinterActivity.this, getString(R.string.not_find_picture),
									Toast.LENGTH_LONG).show();
						}
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
					nopaper = true;
				} else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
			}
		}
	}

	private class ShortTextPrintThread extends Thread {
		@Override
		public void run() {
			super.run();
			try {
				mUsbThermalPrinter.reset();
				mUsbThermalPrinter.addStringOffset(stringOffset, shortContent);
				mUsbThermalPrinter.endLine();
				mUsbThermalPrinter.printString();
				mUsbThermalPrinter.walkPaper(20);
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
					nopaper = true;
				} else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
			}
		}
	}

	private class printLongPicture extends Thread {

		@Override
		public void run() {
			super.run();
			try {
				mUsbThermalPrinter.reset();
				mUsbThermalPrinter.setGray(printGray);
				mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);
				File file = new File(picturePath);
				if (file.exists()) {
					mUsbThermalPrinter.walkPaper(100);
					mUsbThermalPrinter.printLogo(BitmapFactory.decodeFile(picturePath), false);
					mUsbThermalPrinter.walkPaper(100);
				} else {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(UsbPrinterActivity.this, getString(R.string.not_find_picture),
									Toast.LENGTH_LONG).show();
						}
					});
				}

				File file2 = new File(picturePath2);
				if (file2.exists()) {
					mUsbThermalPrinter.walkPaper(100);
					mUsbThermalPrinter.printLogo(BitmapFactory.decodeFile(picturePath2), false);
					mUsbThermalPrinter.walkPaper(100);
				} else {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(UsbPrinterActivity.this, getString(R.string.not_find_picture),
									Toast.LENGTH_LONG).show();
						}
					});
				}

				File file3 = new File(picturePath3);
				if (file3.exists()) {
					mUsbThermalPrinter.walkPaper(100);
					mUsbThermalPrinter.printLogo(BitmapFactory.decodeFile(picturePath3), false);
					mUsbThermalPrinter.walkPaper(100);
				} else {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(UsbPrinterActivity.this, getString(R.string.not_find_picture),
									Toast.LENGTH_LONG).show();
						}
					});
				}

				File file4 = new File(picturePath4);
				if (file4.exists()) {
					mUsbThermalPrinter.walkPaper(100);
					mUsbThermalPrinter.printLogo(BitmapFactory.decodeFile(picturePath4), false);
					mUsbThermalPrinter.walkPaper(100);
				} else {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(UsbPrinterActivity.this, getString(R.string.not_find_picture),
									Toast.LENGTH_LONG).show();
						}
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
					nopaper = true;
				} else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
			}
		}
	}

	private class printLongText extends Thread {
		@Override
		public void run() {
			super.run();
			try {
				mUsbThermalPrinter.reset();
				mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);
				mUsbThermalPrinter.setLineSpace(5);
				if (wordFont == 4) {
					mUsbThermalPrinter.setFontSize(2);
					mUsbThermalPrinter.enlargeFontSize(2, 2);
				} else if (wordFont == 3) {
					mUsbThermalPrinter.setFontSize(1);
					mUsbThermalPrinter.enlargeFontSize(2, 2);
				} else if (wordFont == 2) {
					mUsbThermalPrinter.setFontSize(2);
				} else if (wordFont == 1) {
					mUsbThermalPrinter.setFontSize(1);
				}
				mUsbThermalPrinter.setGray(5);
				mUsbThermalPrinter.addString("1\n" + printContent1);
				mUsbThermalPrinter.addString("2\n" + printContent1);
				mUsbThermalPrinter.addString("3\n" + printContent1);
				mUsbThermalPrinter.addString("4\n" + printContent1);
				mUsbThermalPrinter.addString("5\n" + printContent1);
				mUsbThermalPrinter.addString("6\n" + printContent1);
				mUsbThermalPrinter.addString("7\n" + printContent1);
				mUsbThermalPrinter.addString("8\n" + printContent1);
				mUsbThermalPrinter.printString();
				mUsbThermalPrinter.walkPaper(50);

				mUsbThermalPrinter.reset();
				// 打印二维码
				Bitmap bitmap = CreateCode(qrcodeStr, BarcodeFormat.QR_CODE, 248, 248);
				mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);
				mUsbThermalPrinter.setGray(7);
				mUsbThermalPrinter.addString(" ");
				mUsbThermalPrinter.printString();
				if (bitmap != null) {
					mUsbThermalPrinter.printLogo(bitmap, false);
					mUsbThermalPrinter.walkPaper(50);
					mUsbThermalPrinter.addString(" ");
					mUsbThermalPrinter.printString();
					mUsbThermalPrinter.printLogo(bitmap, false);
					mUsbThermalPrinter.walkPaper(50);
				}

				mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);
				mUsbThermalPrinter.setGray(7);
				mUsbThermalPrinter.addString(" ");
				mUsbThermalPrinter.printString();
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
					nopaper = true;
				} else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
			}
		}
	}
	
	private class printBlackPicture extends Thread {

		@Override
		public void run() {
			super.run();
			try {
				mUsbThermalPrinter.reset();
				mUsbThermalPrinter.setGray(12);
				mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);
				Bitmap bitmap = Bitmap.createBitmap(384, 384, Bitmap.Config.ARGB_8888);
				bitmap.eraseColor(Color.BLACK);
				mUsbThermalPrinter.printLogo(bitmap, false);
				mUsbThermalPrinter.walkPaper(20);
			} catch (Exception e) {
				e.printStackTrace();
				Result = e.toString();
				if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
					nopaper = true;
				} else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
					handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
				} else {
					handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
				}
			} finally {
				handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
				if (nopaper) {
					handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
					nopaper = false;
					return;
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		if (progressDialog != null && !UsbPrinterActivity.this.isFinishing()) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		unregisterReceiver(printReceive);
		mUsbThermalPrinter.stop();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 生成条码
	 * 
	 * @param str
	 *            条码内容
	 * @param type
	 *            条码类型： AZTEC, CODABAR, CODE_39, CODE_93, CODE_128, DATA_MATRIX,
	 *            EAN_8, EAN_13, ITF, MAXICODE, PDF_417, QR_CODE, RSS_14,
	 *            RSS_EXPANDED, UPC_A, UPC_E, UPC_EAN_EXTENSION;
	 * @param bmpWidth
	 *            生成位图宽,宽不能大于384，不然大于打印纸宽度
	 * @param bmpHeight
	 *            生成位图高，8的倍数
	 */

	public Bitmap CreateCode(String str, com.google.zxing.BarcodeFormat type, int bmpWidth, int bmpHeight)
			throws WriterException {
		Hashtable<EncodeHintType, String> mHashtable = new Hashtable<EncodeHintType, String>();
		mHashtable.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		// 生成二维矩阵,编码时要指定大小,不要生成了图片以后再进行缩放,以防模糊导致识别失败
		BitMatrix matrix = new MultiFormatWriter().encode(str, type, bmpWidth, bmpHeight, mHashtable);
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		// 二维矩阵转为一维像素数组（一直横着排）
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (matrix.get(x, y)) {
					pixels[y * width + x] = 0xff000000;
				} else {
					pixels[y * width + x] = 0xffffffff;
				}
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		// 通过像素数组生成bitmap,具体参考api
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	public void selectIndex(View view) {
		switch (view.getId()) {
		case R.id.index_text:
			text_index.setEnabled(false);
			pic_index.setEnabled(true);
			print_text.setVisibility(View.VISIBLE);
			print_pic.setVisibility(View.GONE);

			break;

		case R.id.index_pic:

			text_index.setEnabled(true);
			pic_index.setEnabled(false);
			print_text.setVisibility(View.GONE);
			print_pic.setVisibility(View.VISIBLE);
			break;
		}
	}

	private void savepic() {
		File file = new File(picturePath);
		if (!file.exists()) {
			InputStream inputStream = null;
			FileOutputStream fos = null;
			byte[] tmp = new byte[1024];
			try {
				inputStream = getApplicationContext().getAssets().open("syhlogo.png");
				fos = new FileOutputStream(file);
				int length = 0;
				while ((length = inputStream.read(tmp)) > 0) {
					fos.write(tmp, 0, length);
				}
				fos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					inputStream.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		File file2 = new File(picturePath2);
		if (!file2.exists()) {
			InputStream inputStream = null;
			FileOutputStream fos = null;
			byte[] tmp = new byte[1024];
			try {
				inputStream = getApplicationContext().getAssets().open("op.png");
				fos = new FileOutputStream(file2);
				int length = 0;
				while ((length = inputStream.read(tmp)) > 0) {
					fos.write(tmp, 0, length);
				}
				fos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					inputStream.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		File file3 = new File(picturePath3);
		if (!file3.exists()) {
			InputStream inputStream = null;
			FileOutputStream fos = null;
			byte[] tmp = new byte[1024];
			try {
				inputStream = getApplicationContext().getAssets().open("test.bmp");
				fos = new FileOutputStream(file3);
				int length = 0;
				while ((length = inputStream.read(tmp)) > 0) {
					fos.write(tmp, 0, length);
				}
				fos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					inputStream.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		File file4 = new File(picturePath4);
		if (!file4.exists()) {
			InputStream inputStream = null;
			FileOutputStream fos = null;
			byte[] tmp = new byte[1024];
			try {
				inputStream = getApplicationContext().getAssets().open("test1.png");
				fos = new FileOutputStream(file4);
				int length = 0;
				while ((length = inputStream.read(tmp)) > 0) {
					fos.write(tmp, 0, length);
				}
				fos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					inputStream.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private boolean isSupportAutoBreak() {
		Class<?> thermalPrinter = null;
		Method method = null;
		Object obj = null;

		if (SystemUtil.getDeviceType() == StringUtil.DeviceModelEnum.TPS900.ordinal()) {
			try {
				thermalPrinter = Class.forName("com.common.sdk.thermalprinter.ThermalPrinterServiceManager");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			}
			obj = getSystemService("ThermalPrinter");
			try {
				method = thermalPrinter.getMethod("autoBreakSet", boolean.class);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			try {
				thermalPrinter = Class.forName("com.common.sdk.printer.UsbPrinterManager");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			}
			obj = getSystemService("UsbPrinter");
			try {
				method = thermalPrinter.getMethod("autoBreakSet", boolean.class);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
}
