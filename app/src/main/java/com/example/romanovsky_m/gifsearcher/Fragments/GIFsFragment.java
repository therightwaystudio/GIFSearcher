package com.example.romanovsky_m.gifsearcher.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.romanovsky_m.gifsearcher.Activities.ScrollingActivity;
import com.example.romanovsky_m.gifsearcher.Models.GifData;
import com.example.romanovsky_m.gifsearcher.Models.UrlResourcesSingleton;
import com.example.romanovsky_m.gifsearcher.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Romanovsky_m on 19.11.2016.
 */

public class GIFsFragment extends Fragment implements ReviewGifFragment.OnLikeClickedListener{
    private RecyclerView mRecyclerView;
    private GifAdapter mGifAdapter;
    private String mGifsTypeList;
    private final int NUMBER_OF_COLUMNS = 2;
    public static final String ARG_GIFS_TYPE_LIST = "gifs_type_list";
    private static final String DIALOG_REVIEW_GIF = "ReviewGifFragment";
    private Fragment mFragment = this;

    public static GIFsFragment newInstance(String typeList){
        Bundle args = new Bundle();
        args.putSerializable(ARG_GIFS_TYPE_LIST, typeList);

        GIFsFragment giFsFragment = new GIFsFragment();
        giFsFragment.setArguments(args);
        return giFsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view_layout, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),NUMBER_OF_COLUMNS));
        mGifsTypeList = (String)getArguments().getSerializable(ARG_GIFS_TYPE_LIST);
        updateUI();
        return view;
    }

    @Override
    public void doNotifyDataSetChanged() {
        if(mGifAdapter != null) {
            mGifAdapter.notifyDataSetChanged();
        }
    }

    private class GifHolder extends RecyclerView.ViewHolder{
        private ImageView mImageView;
        private FrameLayout mFrameLayout;
        private GifData mGifData;
        private ProgressBar mProgressBar;

        public void bindGifData(GifData gifData){
            mGifData = gifData;
            DisplayMetrics displayMetrics = new DisplayMetrics();
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            display.getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            mFrameLayout.getLayoutParams().width=width/2;
            mFrameLayout.getLayoutParams().height=width/2;

            /*Glide
                    .with(getActivity())
                    .load(mGifData.getPreviewUrl())
                    .asBitmap()
                    .centerCrop()
                    .error(R.drawable.r3)
                    .into(mImageView);*/

            //!!!need to add error drawable
            Picasso
                    .with(getActivity())
                    .load(mGifData.getPreviewUrl())
                    .resize(width/2,width/2)
                    .centerCrop()
                    .into(mImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            mProgressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }

        public GifHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fragmentManager = getFragmentManager();
                    ReviewGifFragment reviewGifFragment = ReviewGifFragment.newInstance(getAdapterPosition(), mGifsTypeList);
                    reviewGifFragment.setTargetFragment(mFragment, 0);
                    reviewGifFragment.show(fragmentManager, DIALOG_REVIEW_GIF);

                }
            });
            mImageView = (ImageView) itemView.findViewById(R.id.item_gif_image_view);
            mFrameLayout = (FrameLayout) itemView.findViewById(R.id.item_gif_layout);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar_gifs_fragment);
        }
    }

    public class GifAdapter extends RecyclerView.Adapter<GifHolder>{
        private List<GifData> mGifDataList;

        public GifAdapter(List<GifData> gifDataList){
            mGifDataList = gifDataList;
        }

        @Override
        public GifHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.item_gif, parent, false);
            return new GifHolder(view);
        }

        @Override
        public void onBindViewHolder(GifHolder holder, int position) {
            GifData gifData = mGifDataList.get(position);
            holder.bindGifData(gifData);
        }

        @Override
        public int getItemCount() {
            return mGifDataList.size();
        }
    }

    private void  updateUI(){
        List<GifData> gifDataList=new ArrayList<>();

        switch (mGifsTypeList){
            case ScrollingActivity.TRENDING_GIFS_LIST_TYPE:
                gifDataList = UrlResourcesSingleton.get(getContext()).getTrendingGifDataList();
                break;

            case ScrollingActivity.SEARCH_GIFS_LIST_TYPE:
                gifDataList = UrlResourcesSingleton.get(getContext()).getSearchGifDataList();
                break;

            case ScrollingActivity.LIKE_GIFS_LIST_TYPE:
                gifDataList = UrlResourcesSingleton.get(getContext()).getLikedGifDataList();
                break;

            default:
                break;
        }

        if(mGifAdapter == null){
            mGifAdapter = new GifAdapter(gifDataList);
            mRecyclerView.setAdapter(mGifAdapter);
        }
        else{
            mGifAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mGifAdapter=null;
    }

}
