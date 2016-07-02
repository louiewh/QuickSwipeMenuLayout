package com.example.louiewh.swipemenuviewapplication;

import android.support.v7.app.AppCompatActivity;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.swipemenuview.SwipeMenuLayout;

import java.util.List;

public class ListViewActivity extends AppCompatActivity implements SwipeMenuLayout.OnMenuClickListener {


    private List<ApplicationInfo> mAppList;

    private AppAdapter mAdapter;
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        mListView = (ListView)findViewById(R.id.list_view);
        mAppList = getPackageManager().getInstalledApplications(0);

        mAdapter = new AppAdapter();
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onMenuClick(View v, int position) {

        switch (v.getId()) {
            case R.id.swipe_left_menu:
                mAppList.remove(position);
                SwipeMenuLayout.clearSideView();
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.swipe_right_menu:
                mAppList.remove(position);
                SwipeMenuLayout.clearSideView();
                mAdapter.notifyDataSetChanged();
            case R.id.discard:
                mAppList.remove(position);
                SwipeMenuLayout.clearSideView();
                mAdapter.notifyDataSetChanged();
            case R.id.share:
                mAppList.remove(position);
                SwipeMenuLayout.clearSideView();
                mAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }


    class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mAppList.size();
        }

        @Override
        public ApplicationInfo getItem(int position) {
            return mAppList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_list_app_swipemenu, null);
                new ViewHolder(convertView);
            }

            ViewHolder holder = (ViewHolder) convertView.getTag();
            ApplicationInfo item = getItem(position);
            holder.iv_icon.setImageDrawable(item.loadIcon(getPackageManager()));
            holder.tv_name.setText(item.loadLabel(getPackageManager()));

            ((SwipeMenuLayout)convertView).setPosition(position);
            ((SwipeMenuLayout)convertView).setOnMenuClickListener(ListViewActivity.this);
            return convertView;
        }

        class ViewHolder {
            ImageView iv_icon;
            TextView tv_name;

            public ViewHolder(View view) {
                iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                tv_name = (TextView) view.findViewById(R.id.tv_name);

                view.setTag(this);
            }
        }
    }
}
