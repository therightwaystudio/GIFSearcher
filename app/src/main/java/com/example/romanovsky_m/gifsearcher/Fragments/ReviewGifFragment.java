package com.example.romanovsky_m.gifsearcher.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.romanovsky_m.gifsearcher.Activities.ScrollingActivity;
import com.example.romanovsky_m.gifsearcher.Models.GifData;
import com.example.romanovsky_m.gifsearcher.Models.UrlResourcesSingleton;
import com.example.romanovsky_m.gifsearcher.R;


/**
 * Created by Romanovsky_m on 19.11.2016.
 */

public class ReviewGifFragment extends DialogFragment{

    private ImageView mImageView;
    private ImageView mShareButton;
    private ImageView mLikeButton;
    private ProgressBar mProgressBar;
    private static final String ARG_GIF_POSITION = "gif_position";
    public static final String ARG_GIFS_TYPE_LIST = "gifs_type_list";

    private int width;
    private int height;
    private int viewHeight;
    private int viewWidth;
    private int buttonLayoutHeight;


    private boolean mIsLike = false;
    private GifData mGifData;
    private String mGifsTypeList;
    private RequestListener<String,GlideDrawable> requestListener;
    private OnLikeClickedListener mOnLikeClickedListener = null;

    public static ReviewGifFragment newInstance(int position, String typeList){
        Bundle args = new Bundle();
        args.putSerializable(ARG_GIF_POSITION, position);
        args.putSerializable(ARG_GIFS_TYPE_LIST, typeList);

        ReviewGifFragment reviewGifFragment = new ReviewGifFragment();
        reviewGifFragment.setArguments(args);
        return  reviewGifFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_review_gif,null);
        mImageView =(ImageView) view.findViewById(R.id.fragment_review_gif_image_view);
        mLikeButton = (ImageView) view.findViewById(R.id.like_button);
        mShareButton = (ImageView) view.findViewById(R.id.share_button);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar_fragment_review);

        mImageView.setEnabled(false);

        mOnLikeClickedListener = (OnLikeClickedListener) getTargetFragment();

        mGifsTypeList = (String)getArguments().getSerializable(ARG_GIFS_TYPE_LIST);
        int position = (int) getArguments().getSerializable(ARG_GIF_POSITION);

        switch (mGifsTypeList){
            case ScrollingActivity.TRENDING_GIFS_LIST_TYPE:
                mGifData = UrlResourcesSingleton.get(getContext()).getTrendingGifDataList().get(position);
                break;

            case ScrollingActivity.SEARCH_GIFS_LIST_TYPE:
                mGifData = UrlResourcesSingleton.get(getContext()).getSearchGifDataList().get(position);
                break;

            case ScrollingActivity.LIKE_GIFS_LIST_TYPE:
                mGifData = UrlResourcesSingleton.get(getContext()).getLikedGifDataList().get(position);
                break;

            default:
                break;
        }

        height = mGifData.getHeight();
        width = mGifData.getWidth();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        display.getMetrics(displayMetrics);
        int widthDisplay = displayMetrics.widthPixels;
        int heightDisplay = displayMetrics.heightPixels;

        viewWidth=widthDisplay;
        viewHeight = (height * widthDisplay / width);


        if(viewHeight>heightDisplay*7/10){
            int tmp = viewHeight;
            viewHeight = heightDisplay * 8/ 10;
            viewWidth = viewWidth*viewHeight/tmp;
        }

        buttonLayoutHeight = heightDisplay/6;

        mProgressBar.setPadding(0,viewHeight/2,0,0);

        mImageView.getLayoutParams().height = viewHeight;
        mImageView.getLayoutParams().width = viewWidth;


        if(UrlResourcesSingleton.get(getContext()).searchInLikedGifDataList(mGifData.getID())){
            mLikeButton.setImageResource(R.drawable.like);
            mIsLike = true;
        } else {
            mLikeButton.setImageResource(R.drawable.not_like);
        }
        mLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mIsLike) {
                    mLikeButton.setImageResource(R.drawable.not_like);
                    mIsLike = false;
                    UrlResourcesSingleton.get(getContext()).removeInLikedGifDataList(mGifData.getID());
                    mOnLikeClickedListener.doNotifyDataSetChanged();

                } else {
                    mLikeButton.setImageResource(R.drawable.like);
                    mIsLike = true;
                    UrlResourcesSingleton.get(getContext()).getLikedGifDataList().add(mGifData);
                    mOnLikeClickedListener.doNotifyDataSetChanged();
                }
            }
        });

        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_TEXT, mGifData.getGifUrl());
                emailIntent.setType("text/plain");
                startActivity(emailIntent);
            }
        });

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();

        requestListener = new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                mImageView.setEnabled(true);
                mProgressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                mImageView.setEnabled(false);
                mProgressBar.setVisibility(View.GONE);
                return false;
            }
        };

        Glide
                .with(this)
                .load(mGifData.getGifUrl())
                .listener(requestListener)
                .override(width,height)
                .error(R.drawable.warning)
                .into(mImageView);


        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setVisibility(View.VISIBLE);
                Glide
                        .with(getActivity())
                        .load(mGifData.getGifUrl())
                        .listener(requestListener)
                        .override(width,height)
                        .error(R.drawable.warning)
                        .into(mImageView);
            }
        });

        return alertDialog;
    }



    @Override
    public void onResume() {
        super.onResume();

        getDialog().getWindow().setLayout(viewWidth,viewHeight+buttonLayoutHeight);
    }

    public interface OnLikeClickedListener {
        public void doNotifyDataSetChanged();
    }

    public float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

}
