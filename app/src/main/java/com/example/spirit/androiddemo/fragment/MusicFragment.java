package com.example.spirit.androiddemo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.spirit.androiddemo.MyApplication;
import com.example.spirit.androiddemo.R;
import com.example.spirit.androiddemo.modle.MusicBean;
import com.example.spirit.androiddemo.service.MusicService;
import com.example.spirit.androiddemo.utils.DataUtil;
import com.example.spirit.androiddemo.utils.Dialog;
import com.example.spirit.androiddemo.utils.MediaPlayerUtil;
import com.example.spirit.androiddemo.utils.SPUtil;
import com.example.spirit.androiddemo.utils.ThreadUtil;
import com.example.spirit.androiddemo.utils.Util;

import java.util.ArrayList;

public class MusicFragment extends Fragment {
    private RecyclerView rvMusic;
    private LinearLayoutManager layoutManager;
    private FragmentActivity activity;
    private MyAdapter myAdapter;
    private static MusicFragment fragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = Util.inflateView(R.layout.fragment_music);
        MyApplication.setName(getClass().getSimpleName());
        initView(view);
        initVariable();
        initUI();
        return view;
    }

    private void initView(View view) {
        rvMusic = view.findViewById(R.id.rv_music);
    }

    private void initVariable() {
        activity = getActivity();
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        fragment = MusicFragment.this;
    }

    private void initUI() {
        ThreadUtil<MusicBean> threadUtil = new ThreadUtil<MusicBean>() {
            @Override
            protected ArrayList<MusicBean> getList() {
                return DataUtil.getMusicBeans();
            }

            @Override
            protected void updateStart() {
                Dialog.createDialog(activity);
            }

            @Override
            protected void updateFinished(ArrayList<MusicBean> list) {

                if (list.size() <= 0) {
                    Dialog.dismiss();
                    Util.createDialog(getContext(), getString(R.string.noMusic));
                    return;
                }

                activity.startService(new Intent(activity, MusicService.class));
                MusicService.setMusicBeans(list);
                rvMusic.setLayoutManager(layoutManager);
                rvMusic.addItemDecoration(new DividerItemDecoration(activity,
                        DividerItemDecoration.VERTICAL));
                myAdapter = new MyAdapter(list);
                myAdapter.setCurrentItem(SPUtil.getInt(MediaPlayerUtil.CURRENT_ITEM, 0));
                rvMusic.setAdapter(myAdapter);
                rvMusic.scrollToPosition(SPUtil.getInt(MediaPlayerUtil.CURRENT_ITEM, 0));
                Dialog.dismiss();
            }
        };
        threadUtil.newStartThread();
    }

    public static MusicFragment getMusicFragment (){
        return fragment;
    }

    public void setCurrentItem(int currentItem){
        myAdapter.setCurrentItem(currentItem);
        myAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Dialog.dismiss();
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private ArrayList<MusicBean> musicBeans;
        private int currentItem;

        MyAdapter(ArrayList<MusicBean> musicBeans) {
            this.musicBeans = musicBeans;
        }

        @NonNull
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .music_item_layout, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, final int position) {
            MusicBean musicBean = musicBeans.get(position);
            holder.tvTitle.setText(musicBean.getMusicName());
            holder.tvArtist.setText(musicBean.getArtist());
            holder.ivIcon.setImageResource(R.mipmap.music2);

            if (position==currentItem){
                holder.ivIcon.setImageResource(R.mipmap.isplaying);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MediaPlayerUtil.RESTART_ACTION);
                    if (getActivity() != null) {
                        intent.putExtra(MediaPlayerUtil.CURRENT_ITEM, position);
                        getActivity().sendBroadcast(intent);
                    }
                }
            });
        }

        void setCurrentItem(int currentItem) {
            this.currentItem = currentItem;
        }

        @Override
        public int getItemCount() {
            return musicBeans.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivIcon;
            TextView tvTitle;
            TextView tvArtist;

            MyViewHolder(View itemView) {
                super(itemView);
                ivIcon = itemView.findViewById(R.id.iv_icon);
                tvTitle = itemView.findViewById(R.id.tv_title);
                tvArtist = itemView.findViewById(R.id.tv_artist);
            }
        }
    }
}
