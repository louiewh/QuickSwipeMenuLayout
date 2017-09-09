package com.louiewh.swipemenulayout.fragemnt;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.louiewh.swipemenulayout.R;

import java.util.List;

public class RecyclerViewFragment extends Fragment {

    private List<ApplicationInfo> mAppList;
    private AppAdapter mAdapter;
    private OnItemClickListener mOnItemClickListener;
    RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, container, false) ;

        mOnItemClickListener = new OnItemClickListener() {
            @Override
            public void onItemDelClick(int position) {
                mAppList.remove(position);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemClick(View view, int position, long id) {
                startActivity(mAppList.get(position));
            }
        };
        mAppList = container.getContext().getPackageManager().getInstalledApplications(0);
        mAdapter = new AppAdapter(mAppList, mOnItemClickListener);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    private void startActivity(ApplicationInfo applicationInfo) {
        Intent mainIntent = this.getActivity().getPackageManager().getLaunchIntentForPackage(applicationInfo.packageName);
        if(mainIntent != null) {
            startActivity(mainIntent);
        } else {
            Toast toast = Toast.makeText(this.getActivity().getApplicationContext(), "onItemClick: \n" + applicationInfo.packageName, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    class AppAdapter extends RecyclerView.Adapter<AppViewHolder> {
        private OnItemClickListener mOnItemClickListener;
        private List<ApplicationInfo> mAppList;

        public AppAdapter(List<ApplicationInfo> list, OnItemClickListener itemClickListener) {
            mOnItemClickListener = itemClickListener;
            mAppList = list;
        }

        @Override
        public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_quick_swipemenu, parent, false);

            return new AppViewHolder(view, mOnItemClickListener);
        }

        @Override
        public void onBindViewHolder(AppViewHolder holder, int position) {
            holder.bindView(getContext(), mAppList.get(position), position);
        }

        @Override
        public int getItemCount() {
            return mAppList.size();
        }
    }

    static public class AppViewHolder extends RecyclerView.ViewHolder {
        private OnItemClickListener mOnItemClickListener;

        ImageView iv_icon;
        TextView tv_name;
        View menuLeft;
        View menuRight;

        public AppViewHolder(View convertView, OnItemClickListener listener) {
            super(convertView);
            menuLeft = convertView.findViewById(R.id.swipe_left_menu);
            menuRight  = convertView.findViewById(R.id.swipe_right_menu);

            iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            tv_name = (TextView) convertView.findViewById(R.id.tv_name);

            convertView.setTag(this);
            mOnItemClickListener = listener;
        }

        public void bindView(Context context, ApplicationInfo item, final int position){
            this.iv_icon.setImageDrawable(item.loadIcon(context.getPackageManager()));
            this.tv_name.setText(item.loadLabel(context.getPackageManager()));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position, v.getId());
                }
            });

            menuRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemDelClick(position);
                }
            });
        }
    }

    public interface OnItemClickListener{

        void onItemDelClick(int position);

        void onItemClick(View view, int position, long id);
    }
}
