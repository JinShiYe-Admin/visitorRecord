package net.jiaobaowang.visitor.manage;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.jiaobaowang.visitor.R;
import net.jiaobaowang.visitor.custom_view.DatePickerFragment;
import net.jiaobaowang.visitor.entity.VisitRecord;
import net.jiaobaowang.visitor.utils.TimeFormat;

import java.io.InputStream;
import java.util.Date;

/**
 * Created by rocka on 2018/1/15.
 */

public class OffDetailFragment extends DialogFragment implements View.OnClickListener {
    private final static String EXTRA_ARGS_VISIT = "net.jiaobangwang.visitor.manage.OffDetailFragment.visit";
    private final static int REQUEST_OFF_CODE = 2;
    private final static String TAG_OFF_TIME_QUERY = "leaveTime";
    private VisitRecord mVisitRecord;
    private AlertDialog dialog;
    private TextView mOffTimeText;
    private Date mSelectLeaveTime;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.e("----onCreateDialog---", "你好");
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_off_detail, null, false);
        initialView(v);
        dialog = new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();
        return dialog;
    }

    private void initialView(View v) {
        ((TextView) v.findViewById(R.id.off_detail_name)).setText(mVisitRecord.getVisitor_name());
        ((TextView) v.findViewById(R.id.off_detail_sex)).setText(mVisitRecord.getVisitor_sex() == 0 ? "女" : "男");
        ((TextView) v.findViewById(R.id.off_detail_cardType)).setText(mVisitRecord.getCertificate_type_id() == 0 ? "身份证" : "其他证件");
        ((TextView) v.findViewById(R.id.off_detail_cardNo)).setText(mVisitRecord.getCertificate_number());
        ((TextView) v.findViewById(R.id.off_detail_signIn)).setText(mVisitRecord.getIn_time());
        mOffTimeText = v.findViewById(R.id.off_detail_offTime);
        mSelectLeaveTime = new Date();
        mOffTimeText.setText(TimeFormat.formatTime(mSelectLeaveTime));
        ((TextView) v.findViewById(R.id.off_detail_checkTime)).setText(mVisitRecord.getCreate_time());
        ((TextView) v.findViewById(R.id.off_detail_beVisitor)).setText(mVisitRecord.getInterviewee_type() == 0 ? mVisitRecord.getStudent_name() : mVisitRecord.getTeacher_name());
        ((TextView) v.findViewById(R.id.off_detail_beVisitorDepart)).setText(mVisitRecord.getDepartment_name());
        new DownloadImageTask((ImageView) v.findViewById(R.id.off_detail_portrait)).execute(mVisitRecord.getImg_url());
        v.findViewById(R.id.off_detail_sure).setOnClickListener(this);
        v.findViewById(R.id.off_detail_cancel).setOnClickListener(this);
        v.findViewById(R.id.off_detail_offTime).setOnClickListener(this);
        v.findViewById(R.id.off_detail_offTimeContainer).setOnClickListener(this);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mVisitRecord = (VisitRecord) getArguments().getSerializable(EXTRA_ARGS_VISIT);
        }
    }

    public static OffDetailFragment newInstance(VisitRecord visitRecord) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_ARGS_VISIT, visitRecord);
        OffDetailFragment fragment = new OffDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (dialog != null) {
            dialog.getWindow().setLayout(950, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.off_detail_sure:
                dialog.dismiss();
                break;
            case R.id.off_detail_cancel:
                dialog.dismiss();
                break;
            case R.id.off_detail_offTime:
                break;
            case R.id.off_detail_offTimeContainer:
//                dialog.dismiss();
                showTimePicker();
                break;
            default:
                break;
        }
    }

    private void showTimePicker() {
        FragmentManager fragmentManager = getFragmentManager();
        DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(1, mSelectLeaveTime, TimeFormat.getDateFromFormatTime(mVisitRecord.getIn_time()));
        datePickerFragment.setTargetFragment(OffDetailFragment.this, REQUEST_OFF_CODE);
        datePickerFragment.show(fragmentManager, TAG_OFF_TIME_QUERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        mSelectLeaveTime= (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
        switch (requestCode) {
            case REQUEST_OFF_CODE:
                mOffTimeText.setText(TimeFormat.formatTime(mSelectLeaveTime));
                break;
            default:
                break;
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", "获取图片错误", e);
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}