package it.uniba.dib.sms2324_16;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class IconAdapter extends BaseAdapter {
    private Context mContext;
    private int[] mIcons;
    public IconAdapter(Context context, int[] icons) {
        mContext = context;
        mIcons = icons;
    }
    @Override
    public int getCount() {
        return mIcons.length;
    }

    @Override
    public Object getItem(int position) {
        return mIcons[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(150, 150)); // Imposta le dimensioni delle icone
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mIcons[position]);
        return imageView;
    }
}
