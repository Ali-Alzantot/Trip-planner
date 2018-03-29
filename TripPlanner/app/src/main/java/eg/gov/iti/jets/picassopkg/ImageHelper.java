package eg.gov.iti.jets.picassopkg;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import eg.gov.iti.jets.tripplanner.R;

/**
 * Created by Anonymous on 18/03/2018.
 */

public class ImageHelper {


    public static void getImage(final Context context, final ImageView imageView, final String url, final int DefaultImage) {
        Picasso.with(context)
                .load(url)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        //Try again online if cache failed
                        Picasso.with(context)
                                .load(url)
                                .into(imageView, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        imageView.setImageResource(DefaultImage);
                                    }
                                });
                    }
                });
    }
}
