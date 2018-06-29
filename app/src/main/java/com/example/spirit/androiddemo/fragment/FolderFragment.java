package com.example.spirit.androiddemo.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.spirit.androiddemo.R;
import com.example.spirit.androiddemo.modle.FileBean;
import com.example.spirit.androiddemo.utils.ConstanceField;
import com.example.spirit.androiddemo.utils.Dialog;
import com.example.spirit.androiddemo.utils.ThreadUtil;
import com.example.spirit.androiddemo.utils.Util;

import java.io.File;
import java.util.ArrayList;

public class FolderFragment extends Fragment {
    private RecyclerView rvFolder;
    final private String rootName = Environment.getExternalStorageDirectory().getName() + "/";
    private File file;
    private MyAdapter myAdapter;
    private static FolderFragment folderFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = Util.inflateView(R.layout.fragment_folder);
        initView(view);
        initUI();
        return view;
    }

    private void initView(View view) {
        rvFolder = view.findViewById(R.id.rv_folder);
        folderFragment = FolderFragment.this;
    }

    private void initUI() {
        System.out.println("initUI");
        System.out.println(rootName);
        file = new File(rootName);
        update();
    }

    private void update() {
        ThreadUtil<FileBean> threadUtil = new ThreadUtil<FileBean>() {
            @Override
            protected ArrayList<FileBean> getList() {
                return Util.fileScan(file);
            }

            @Override
            protected void updateStart() {
                Dialog.createDialog(getActivity());
            }

            @Override
            protected void updateFinished(ArrayList<FileBean> list) {
//                if (list == null || list.size() == 0) {
//                    Dialog.dismiss();
//                    return;
//                }

                if (myAdapter == null) {
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    rvFolder.setLayoutManager(layoutManager);
                    myAdapter = new MyAdapter();
                    myAdapter.setFileBeans(list);
                    rvFolder.setAdapter(myAdapter);
                } else {
                    myAdapter.setFileBeans(list);
                    myAdapter.notifyDataSetChanged();

                }
                Dialog.dismiss();

            }
        };
        threadUtil.newStartThread();
    }

    public static FolderFragment getFoldFragemnt() {
        return folderFragment;
    }

    public boolean onBackPressed() {
        return null != file.getParent();
    }

    public void backRootFile() {
        file = new File(file.getParent());
        update();
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private ArrayList<FileBean> fileBeans;

        public void setFileBeans(ArrayList<FileBean> fileBeans) {
            this.fileBeans = fileBeans;
        }

        @NonNull
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .file_item_layout, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
            final FileBean fileBean = fileBeans.get(position);
            //System.out.println(fileBeans);
            Drawable drawable;
            if (fileBean.getType().equals(ConstanceField.DIRECTORY)) {
                drawable = getResources().getDrawable(R.mipmap.folder_in);
                holder.tvFile.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            } else {
                drawable = getResources().getDrawable(R.mipmap.file);
                holder.tvFile.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            }
            holder.tvFile.setText(fileBean.getFile().getName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    file = fileBean.getFile();
                    update();
                }
            });
        }

        @Override
        public int getItemCount() {
            return fileBeans == null ? 0 : fileBeans.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView tvFile;

            MyViewHolder(View itemView) {
                super(itemView);
                tvFile = itemView.findViewById(R.id.tv_file);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("onDestroy");
        myAdapter = null;
    }
}
