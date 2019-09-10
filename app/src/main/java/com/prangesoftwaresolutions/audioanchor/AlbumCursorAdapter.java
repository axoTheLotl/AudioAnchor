package com.prangesoftwaresolutions.audioanchor;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.prangesoftwaresolutions.audioanchor.data.AnchorContract;

import java.io.File;

/**
 * CursorAdapter for the ListView in the Main Activity
 */

public class AlbumCursorAdapter extends CursorAdapter {
    private Context mContext;
    private SharedPreferences mPrefs;

    AlbumCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        mContext = context;
        // Get the base directory from the shared preferences.
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.album_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String directory = mPrefs.getString(mContext.getString(R.string.preference_filename), null);
        // Get the title of the current album and set this text to the titleTV
        TextView titleTV = view.findViewById(R.id.audio_storage_item_title);
        String title = cursor.getString(cursor.getColumnIndex(AnchorContract.AlbumEntry.COLUMN_TITLE));
        titleTV.setText(title);

        //get the progress of this album and update the view
        TextView progressTV = view.findViewById(R.id.album_info_time_album);
        int id = cursor.getInt(cursor.getColumnIndex(AnchorContract.AlbumEntry._ID));
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String prefKey = context.getString(R.string.settings_progress_percentage_key);
        String prefDefault = context.getString(R.string.settings_progress_percentage_default);
        boolean showInPercentages = pref.getBoolean(prefKey, Boolean.getBoolean(prefDefault));

        String timeStr = Utils.getAlbumCompletion(context, id, showInPercentages, context.getResources());

        progressTV.setText(timeStr);

        // Get the path of the thumbnail of the current album and set the src of the image view
        ImageView thumbnailIV = view.findViewById(R.id.audio_storage_item_thumbnail);
        String path = cursor.getString(cursor.getColumnIndex(AnchorContract.AlbumEntry.COLUMN_COVER_PATH));
        int reqSize = mContext.getResources().getDimensionPixelSize(R.dimen.album_item_height);
        if (path != null) {
            path = directory + File.separator + path;
            BitmapUtils.setImage(thumbnailIV, path, reqSize);
        } else {
            Bitmap image = BitmapUtils.decodeSampledBitmap(mContext.getResources(), R.drawable.empty_cover_grey_blue, reqSize, reqSize);
            thumbnailIV.setImageBitmap(image);
        }


        String baseDir = cursor.getString(cursor.getColumnIndex(AnchorContract.AlbumEntry.COLUMN_BASE_DIR));

        // Show the deletable image if the file does not exist anymore
        ImageView deletableIV = view.findViewById(R.id.album_item_deletable_img);
        if (baseDir != null && !(new File(baseDir, title)).exists()) {
            deletableIV.setImageResource(R.drawable.img_deletable);
        } else {
            deletableIV.setImageResource(android.R.color.transparent);
        }
    }
}
