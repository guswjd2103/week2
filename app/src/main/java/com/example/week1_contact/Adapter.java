package com.example.week1_contact;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ScaleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.week1_contact.ContactData;
import com.example.week1_contact.R;

import java.util.ArrayList;

public class Adapter extends BaseAdapter {
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<ContactData> sample;

    public Adapter(Context context, ArrayList<ContactData> data) {
        mContext = context;
        sample = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return sample.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ContactData getItem(int position) {
        return sample.get(position);
    }

    public String getItemName(int position) { return sample.get(position).getName();}

    @Override
    public View getView(int position, @Nullable View view, @Nullable ViewGroup parent) {

        view = mLayoutInflater.inflate(R.layout.fragment_contact_item, parent, false);

        ImageView imageView = (ImageView)view.findViewById(R.id.photo);
        TextView name = (TextView)view.findViewById(R.id.name);
        TextView phoneNumber = (TextView)view.findViewById(R.id.phoneNumber);

        imageView.setImageResource(R.drawable.profile);
        ContactData contacts = sample.get(position);
        Bitmap profile = loadContactPhoto(mContext.getContentResolver(),contacts.getId(), contacts.getPhoto());

        if(profile!=null) {
            if(Build.VERSION.SDK_INT >=21) {
                imageView.setBackground(new ShapeDrawable(new OvalShape()));
                imageView.setClipToOutline(true);
            }
            imageView.setImageBitmap(profile);
        } else {
            if(Build.VERSION.SDK_INT >=21) {
                imageView.setClipToOutline(false);
            }
        }
        name.setText(sample.get(position).getName());
        phoneNumber.setText(sample.get(position).getPhoneNumber());

        return view;
    }

    public Bitmap loadContactPhoto(ContentResolver cr, long id, long photo_id) {

        byte[] photoBytes = null;
        Uri photoUri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, photo_id);
        Cursor c = cr.query(photoUri, new String[]{ContactsContract.CommonDataKinds.Photo.PHOTO},
                null,null, null);
        try {
            if (c.moveToFirst())
                photoBytes = c.getBlob(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            c.close();
        }

        if (photoBytes != null) {
            return resizingBitmap(BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length));
        } else
//            Log.d("<<CONTACT_PHOTO>>", "second try also failed");

        return null;

    }

    public Bitmap resizingBitmap(Bitmap oBitmap) {
        if (oBitmap == null) {
            return null;
        }

        float width = oBitmap.getWidth();
        float height = oBitmap.getHeight();
        float resizing_size = 120;

        Bitmap rBitmap = null;
        if (width > resizing_size) {
            float mWidth = (float)(width / 100);
            float fScale = (float)(resizing_size / mWidth);
            width *= (fScale / 100);
            height *= (fScale / 100);

        } else if (height > resizing_size) {
            float mHeight = (float)(height / 100);
            float fScale = (float)(resizing_size / mHeight);

            width *= (fScale / 100);
            height *= (fScale / 100);
        }

        //Log.d("rBitmap : " + width + ", " + height);

        rBitmap = Bitmap.createScaledBitmap(oBitmap, (int)width, (int)height, true);
        return rBitmap;
    }
}
