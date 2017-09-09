package com.louiewh.swipemenulayout.fragemnt;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.louiewh.swipemenulayout.R;

import java.util.List;

public class ListViewFragment extends Fragment {

    private List<ApplicationInfo> mAppList;
    private AppAdapter mAdapter;
    private OnItemClickListener mOnItemClickListener;
    ListView mListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listview, container, false) ;
        mListView = (ListView)view.findViewById(R.id.list_view);
        mAppList = container.getContext().getPackageManager().getInstalledApplications(0);

        mOnItemClickListener = new OnItemClickListener() {
            @Override
            public void onItemDelClick(int position) {
                Toast toast = Toast.makeText(getContext(), "onItemDelClick: \n" + mAppList.get(position).packageName, Toast.LENGTH_SHORT);
                toast.show();

//                mAppList.remove(position);
//                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemClick(View view, int position, long id) {
                startActivity(mAdapter.getItem(position));
            }
        };

        mAdapter = new AppAdapter(mOnItemClickListener);
        mListView.setAdapter(mAdapter);

        return  view;
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


    class AppAdapter extends BaseAdapter {
        private OnItemClickListener mOnItemClickListener;


        public AppAdapter(OnItemClickListener itemClickListener) {
            mOnItemClickListener = itemClickListener;
        }

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
        public int getItemViewType(int position) {
            return position % 3;
        }

        @Override
        public int getViewTypeCount() {
            return 3;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                switch (getItemViewType(position)){
                    case 0:
                        convertView = View.inflate(parent.getContext(), R.layout.item_quick_swipemenu_left, null);
                        new ViewHolder(convertView, mOnItemClickListener);
                        break;
                    case 1:
                        convertView = View.inflate(parent.getContext(), R.layout.item_quick_swipemenu, null);
                        new ViewHolder(convertView, mOnItemClickListener);
                        break;
                    case 2:
                        convertView = View.inflate(parent.getContext(), R.layout.item_quick_swipemenu_right, null);
                        new ViewHolder(convertView, mOnItemClickListener);
                        break;
                    default:
                        break;
                }

            }

            ViewHolder holder = (ViewHolder) convertView.getTag();
            ApplicationInfo item = getItem(position);
            holder.bindView(convertView, item, position);

            return convertView;
        }
    }

    static public class ViewHolder {
        private OnItemClickListener mOnItemClickListener;

        View root;
        ImageView iv_icon;
        TextView tv_name;
        View menuLeft;
        View menuRight;

        public ViewHolder(View convertView, OnItemClickListener listener) {
            root = convertView.findViewById(R.id.swipe_menu_layout);
            menuLeft = convertView.findViewById(R.id.swipe_left_menu);
            menuRight  = convertView.findViewById(R.id.swipe_right_menu);

            iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            tv_name = (TextView) convertView.findViewById(R.id.tv_name);

            convertView.setTag(this);
            mOnItemClickListener = listener;
        }

        public void bindView(View convertView, ApplicationInfo item, final int position){
            this.iv_icon.setImageDrawable(item.loadIcon(convertView.getContext().getPackageManager()));
            this.tv_name.setText(item.loadLabel(convertView.getContext().getPackageManager()));

            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position, v.getId());
                }
            });

            if(position % 3 == 0 || position % 3 == 1) {
                menuLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemDelClick(position);
                    }
                });
            }

            if(position % 3 == 2 || position % 3 == 1) {
                menuRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemDelClick(position);
                    }
                });
            }
        }
    }

    public interface OnItemClickListener{

        void onItemDelClick(int position);

        void onItemClick(View view, int position, long id);
    }
}
