package com.example.muhammed.a7gez;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

/**
 * Created by Bahaa on 22/10/2017.
 */
public class CustomPicasso {

    private Context context;
    private WeakReference<ContentResolver> contentResolverWeakReference;

    public static void loadImage(Context context, String imageUrl, ImageView imageView) {
        if (imageUrl.length() > 0 && imageUrl != null) {
            Picasso.with(context).load(imageUrl).into(imageView);
        } else {
            Picasso.with(context).load(R.drawable.default_image_place).into(imageView);
        }

    }

    public static void loadImageUser(Context context, String imageUrl, ImageView imageView) {
        if (imageUrl.length() > 0 && imageUrl != null) {
            Picasso.with(context).load(imageUrl).into(imageView);
        } else {
            Picasso.with(context).load(R.drawable.com_facebook_profile_picture_blank_portrait).into(imageView);
        }

    }

    //target to save
    public static Target getTarget(final String name, final Context context){
        Target target = new Target(){

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + name);
                try {
                    file.createNewFile();
                    FileOutputStream ostream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                    ostream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        return target;
    }
}
