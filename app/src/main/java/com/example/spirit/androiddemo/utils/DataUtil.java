package com.example.spirit.androiddemo.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import com.example.spirit.androiddemo.R;
import com.example.spirit.androiddemo.fragment.CaptureFragment;
import com.example.spirit.androiddemo.fragment.ErrorFragment;
import com.example.spirit.androiddemo.fragment.MusicFragment;
import com.example.spirit.androiddemo.fragment.PicFragment;
import com.example.spirit.androiddemo.fragment.SmsFragment;
import com.example.spirit.androiddemo.fragment.SoftFragment;
import com.example.spirit.androiddemo.fragment.TelFragment;
import com.example.spirit.androiddemo.fragment.VideoFragment;
import com.example.spirit.androiddemo.fragment.WeatherFragment;
import com.example.spirit.androiddemo.modle.MusicBean;
import com.example.spirit.androiddemo.modle.PersonBean;
import com.example.spirit.androiddemo.modle.PicBean;
import com.example.spirit.androiddemo.modle.SmsBean;
import com.example.spirit.androiddemo.modle.SoftBean;
import com.example.spirit.androiddemo.modle.VideoBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DataUtil {

    private static final String[] PHONES_PROJECTION = new String[]{Phone.DISPLAY_NAME, Phone
            .NUMBER, Phone.PHOTO_ID, Phone.CONTACT_ID};

    private static ArrayList<PersonBean> personBeans;
    private static ArrayList<Fragment> fragments;
    private static ArrayList<SmsBean> smsBeans;
    private static ArrayList<SoftBean> softBeans;
    private static ArrayList<PicBean> picBeans;
    private static ArrayList<MusicBean> musicBeans;
    private static ArrayList<VideoBean> videoBeans;

    public static Fragment getFragment(int index) {
        if (fragments == null) {
            fragments = new ArrayList<>();
            fragments.add(new TelFragment());
            fragments.add(new SmsFragment());
            fragments.add(new SoftFragment());
            fragments.add(new PicFragment());
            fragments.add(new MusicFragment());
            fragments.add(new VideoFragment());
            fragments.add(new WeatherFragment());
            fragments.add(new CaptureFragment());
            fragments.add(new ErrorFragment());
        }
        return fragments.get(index);
    }

    public static ArrayList<PersonBean> getPhoneContacts() {
        if (personBeans == null) {
            personBeans = new ArrayList<>();
            ContentResolver resolver = Util.getMContext().getContentResolver();
            Cursor cursor = resolver.query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    PersonBean personBean = new PersonBean();
                    personBean.setName(cursor.getString(cursor.getColumnIndex(Phone
                            .DISPLAY_NAME_PRIMARY)));
                    int id = cursor.getInt(cursor.getColumnIndex(Phone.CONTACT_ID));
                    personBean.setEmail(getEmail(id));
                    personBean.setNumber(cursor.getString(cursor.getColumnIndex(Phone.NUMBER)));
                    personBeans.add(personBean);
                }
                cursor.close();
            }
        }
        return personBeans;
    }

    private static String getEmail(int id) {
        ContentResolver resolver = Util.getMContext().getContentResolver();
        Cursor cursor = resolver.query(CommonDataKinds.Email.CONTENT_URI, null,
                CommonDataKinds.Email.CONTACT_ID + "=" + id, null, null);
        if (cursor != null) {
            StringBuilder sb = new StringBuilder();
            while (cursor.moveToNext()) {
                sb.append(cursor.getString(cursor.getColumnIndex(CommonDataKinds.Email.DATA)));
                sb.append(" ");
            }
            cursor.close();
            return sb.toString();
        }
        return "";
    }

    public static ArrayList<SmsBean> getSmsBeans() {
        if (smsBeans == null) {
            smsBeans = new ArrayList<>();
            ContentResolver resolver = Util.getMContext().getContentResolver();
            Cursor cursor = resolver.query(Uri.parse("content://sms/"), null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    SmsBean smsBean = new SmsBean();
                    smsBean.setAddress(cursor.getString(cursor.getColumnIndex("address")));//手机号
                    smsBean.setBody(cursor.getString(cursor.getColumnIndex("person")));
                    smsBean.setDate(timeFormat(Long.parseLong(cursor.getString(cursor
                            .getColumnIndex("date")))));
                    smsBean.setBody(cursor.getString(cursor.getColumnIndex("body")));
                    smsBeans.add(smsBean);
                }
                cursor.close();
            }
        }
        return smsBeans;
    }

    public static ArrayList<SmsBean> addSmsBean() {
        if (smsBeans != null) {
            ContentResolver resolver = Util.getMContext().getContentResolver();
            Cursor cursor = resolver.query(Uri.parse("content://sms/"), null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                SmsBean smsBean = new SmsBean();
                smsBean.setAddress(cursor.getString(cursor.getColumnIndex("address")));//手机号
                smsBean.setBody(cursor.getString(cursor.getColumnIndex("person")));
                smsBean.setDate(timeFormat(Long.parseLong(cursor.getString(cursor
                        .getColumnIndex("date")))));
                smsBean.setBody(cursor.getString(cursor.getColumnIndex("body")));
                smsBeans.add(smsBean);
                cursor.close();
            }
        }
        return smsBeans;
    }

    @SuppressLint("SimpleDateFormat")
    private static String timeFormat(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Util.getMContext().getString(R
                .string.timeStyle));
        return dateFormat.format(time);
    }

    public static ArrayList<SoftBean> getSoftBeans() {
        if (softBeans == null) {
            softBeans = new ArrayList<>();
            PackageManager packageManager = Util.getMContext().getPackageManager();
            List<ApplicationInfo> applications = packageManager.getInstalledApplications
                    (PackageManager.GET_UNINSTALLED_PACKAGES);
            for (ApplicationInfo info : applications) {
                SoftBean softBean = new SoftBean();
                softBean.setIcon(info.loadIcon(packageManager));
                softBean.setName(info.loadLabel(packageManager).toString());
                softBean.setPackageName(info.packageName);
                softBeans.add(softBean);
            }
        }
        return softBeans;
    }

    public static ArrayList<PicBean> getPicBeans(int defSize) {
        if (picBeans == null) {
            picBeans = new ArrayList<>();
            ContentResolver resolver = Util.getMContext().getContentResolver();
            Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                    null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    PicBean picBean = new PicBean();
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media
                            .DATA));
                    picBean.setPath(path);
                    picBean.setBitmap(Util.scaleBitmap(path, defSize));
                    picBeans.add(picBean);
                }
            }
        }
        return picBeans;
    }

    public static ArrayList<MusicBean> getMusicBeans() {
        if (musicBeans == null) {
            musicBeans = new ArrayList<>();
            ContentResolver resolver = Util.getMContext().getContentResolver();
            Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                    null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    MusicBean musicBean = new MusicBean();
                    musicBean.setMusicName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio
                            .Media.TITLE)));
                    musicBean.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio
                            .Media.ARTIST)));
                    musicBean.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media
                            .DATA)));
                    musicBean.setDuration(cursor.getShort(cursor.getColumnIndex(MediaStore.Audio
                            .Media.DURATION)));
                    musicBeans.add(musicBean);
                }
                cursor.close();
            }
        }
        return musicBeans;
    }

    public static ArrayList<VideoBean> getVideoBeans() {
        if (videoBeans == null) {
            videoBeans = new ArrayList<>();
            ContentResolver resolver = Util.getMContext().getContentResolver();
            Cursor cursor = resolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null,
                    null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    VideoBean videoBean = new VideoBean();
                    videoBean.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Video
                            .Media.TITLE)));
                    videoBean.setDuration(cursor.getString(cursor.getColumnIndex(MediaStore.Video
                            .Media.DURATION)));
                    videoBean.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Video
                            .Media.DATA)));
                    videoBeans.add(videoBean);
                }
                cursor.close();
            }
        }
        return videoBeans;
    }
}
