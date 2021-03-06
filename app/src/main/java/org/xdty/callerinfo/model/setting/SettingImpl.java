package org.xdty.callerinfo.model.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.WindowManager;

import com.google.gson.Gson;

import org.xdty.callerinfo.BuildConfig;
import org.xdty.callerinfo.R;
import org.xdty.callerinfo.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;

public class SettingImpl implements Setting {

    private static Gson gson = new Gson();
    private static Setting sSetting;
    private final int mScreenWidth;
    private final int mScreenHeight;
    private SharedPreferences mPrefs;
    private SharedPreferences mWindowPrefs;
    private Context mContext;

    private SettingImpl(Context context) {
        mContext = context.getApplicationContext();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        mWindowPrefs = mContext.getSharedPreferences("window", Context.MODE_PRIVATE);

        WindowManager mWindowManager =
                (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = mWindowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        mScreenWidth = point.x;
        mScreenHeight = point.y;
    }

    public static void init(Context context) {
        if (sSetting == null) {
            sSetting = new SettingImpl(context.getApplicationContext());
        }
    }

    public static Setting getInstance() {
        if (sSetting == null) {
            throw new IllegalStateException("Setting is not initialized!");
        }
        return sSetting;
    }

    @Override
    public int getScreenWidth() {
        return mScreenWidth;
    }

    @Override
    public int getScreenHeight() {
        return mScreenHeight;
    }

    @Override
    public boolean isEulaSet() {
        return mPrefs.getBoolean("eula", false);
    }

    @Override
    public int getWindowHeight() {
        return mPrefs.getInt(mContext.getString(R.string.window_height_key), getDefaultHeight());
    }

    @Override
    public int getDefaultHeight() {
        return mScreenHeight / 8;
    }

    @Override
    public boolean isShowCloseAnim() {
        return mPrefs.getBoolean(mContext.getString(R.string.window_close_anim_key), true);
    }

    @Override
    public boolean isTransBackOnly() {
        return mPrefs.getBoolean(mContext.getString(R.string.window_trans_back_only_key), true);
    }

    @Override
    public boolean isEnableTextColor() {
        return mPrefs.getBoolean(mContext.getString(R.string.window_text_color_key), false);
    }

    @Override
    public int getTextPadding() {
        return mPrefs.getInt(mContext.getString(R.string.window_text_padding_key), 0);
    }

    @Override
    public int getTextAlignment() {
        return mPrefs.getInt(mContext.getString(R.string.window_text_alignment_key), 1);
    }

    @Override
    public int getTextSize() {
        return mPrefs.getInt(mContext.getString(R.string.window_text_size_key), 20);
    }

    @Override
    public int getWindowTransparent() {
        return mPrefs.getInt(mContext.getString(R.string.window_transparent_key), 80);
    }

    @Override
    public boolean isDisableMove() {
        return mPrefs.getBoolean(mContext.getString(R.string.disable_move_key), false);
    }

    @Override
    public boolean isAutoReportEnabled() {
        return mPrefs.getBoolean(mContext.getString(R.string.auto_report_key), true);
    }

    @Override
    public boolean isMarkingEnabled() {
        return mPrefs.getBoolean(mContext.getString(R.string.enable_marking_key), true);
    }

    @Override
    public void addPaddingMark(String number) {
        String key = mContext.getString(R.string.padding_mark_numbers_key);
        ArrayList<String> list = getPaddingMarks();
        if (list.contains(number)) {
            return;
        }
        list.add(number);
        String paddingNumbers = gson.toJson(list);
        mPrefs.edit().putString(key, paddingNumbers).apply();
    }

    @Override
    public void removePaddingMark(String number) {
        String key = mContext.getString(R.string.padding_mark_numbers_key);
        ArrayList<String> list = getPaddingMarks();
        if (!list.contains(number)) {
            return;
        }
        list.remove(number);
        String paddingNumbers = gson.toJson(list);
        mPrefs.edit().putString(key, paddingNumbers).apply();
    }

    @Override
    public ArrayList<String> getPaddingMarks() {
        String key = mContext.getString(R.string.padding_mark_numbers_key);
        if (mPrefs.contains(key)) {
            String paddingNumbers = mPrefs.getString(key, null);
            return new ArrayList<>(
                    Arrays.asList(gson.fromJson(paddingNumbers, String[].class)));
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public String getUid() {
        String key = mContext.getString(R.string.uid_key);
        if (mPrefs.contains(key)) {
            return mPrefs.getString(key, "");
        } else {
            String uid = Utils.getDeviceId(mContext);
            mPrefs.edit().putString(key, uid).apply();
            return uid;
        }
    }

    @Override
    public int getTypeFromName(String name) {
        return Utils.typeFromString(mContext, name);
    }

    @Override
    public void updateLastScheduleTime() {
        updateLastScheduleTime(System.currentTimeMillis());
    }

    @Override
    public void updateLastScheduleTime(long timestamp) {
        mPrefs.edit()
                .putLong(mContext.getString(R.string.last_schedule_time_key), timestamp)
                .apply();
    }

    @Override
    public long lastScheduleTime() {
        return mPrefs.getLong(mContext.getString(R.string.last_schedule_time_key), 0);
    }

    @Override
    public boolean isHidingWhenTouch() {
        return mPrefs.getBoolean(mContext.getString(R.string.hide_when_touch_key), false);
    }

    @Override
    public void setEula() {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean("eula", true);
        editor.putInt("eula_version", 1);
        editor.putInt("version", BuildConfig.VERSION_CODE);
        editor.apply();
    }

    @Override
    public String getIgnoreRegex() {
        return mPrefs.getString(mContext.getString(R.string.ignore_regex_key), "").replace("*",
                "[0-9]").replace(" ", "|");
    }

    @Override
    public boolean isHidingOffHook() {
        return mPrefs.getBoolean(mContext.getString(R.string.hide_when_off_hook_key), false);
    }

    @Override
    public boolean isShowingOnOutgoing() {
        return mPrefs.getBoolean(mContext.getString(R.string.display_on_outgoing_key), false);
    }

    @Override
    public boolean isIgnoreKnownContact() {
        return mPrefs.getBoolean(mContext.getString(R.string.ignore_known_contact_key), false);
    }

    @Override
    public boolean isShowingContactOffline() {
        return mPrefs.getBoolean(mContext.getString(R.string.contact_offline_key), false);
    }

    @Override
    public boolean isAutoHangup() {
        return mPrefs.getBoolean(mContext.getString(R.string.auto_hangup_key), false);
    }

    @Override
    public boolean isAddingCallLog() {
        return mPrefs.getBoolean(mContext.getString(R.string.add_call_log_key), false);
    }

    @Override
    public boolean isCatchCrash() {
        return mPrefs.getBoolean(mContext.getString(R.string.catch_crash_key), false);
    }

    @Override
    public boolean isForceChinese() {
        return mPrefs.getBoolean(mContext.getString(R.string.force_chinese_key), false);
    }

    @Override
    public String getKeywords() {
        String keywordKey = mContext.getString(R.string.hangup_keyword_key);
        String keywordDefault = mContext.getString(R.string.hangup_keyword_default);
        String keywords = mPrefs.getString(keywordKey, keywordDefault).trim();
        if (keywords.isEmpty()) {
            keywords = keywordDefault;
        }
        return keywords;
    }

    @Override
    public String getGeoKeyword() {
        return mPrefs.getString(mContext.getString(R.string.hangup_geo_keyword_key), "").trim();
    }

    @Override
    public String getNumberKeyword() {
        return mPrefs.getString(mContext.getString(R.string.hangup_number_keyword_key), "").trim();
    }

    @Override
    public int getWindowX() {
        return mWindowPrefs.getInt("x", -1);
    }

    @Override
    public int getWindowY() {
        return mWindowPrefs.getInt("y", -1);
    }

    @Override
    public void setWindow(int x, int y) {
        SharedPreferences.Editor editor = mWindowPrefs.edit();
        editor.putInt("x", x);
        editor.putInt("y", y);
        editor.apply();
    }
}
