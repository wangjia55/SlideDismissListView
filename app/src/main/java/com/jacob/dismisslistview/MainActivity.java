package com.jacob.dismisslistview;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private SlideDismissListView mSlideListView;
    private List<String> mStringList;
    private LayoutListItemAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSlideListView = (SlideDismissListView) findViewById(R.id.slide_list_view);
        mSlideListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "position:" + position, Toast.LENGTH_SHORT).show();
            }
        });
        mSlideListView.setOnSlideListener(new SlideDismissListView.OnSlideListener() {
            @Override
            public void removeItem(SlideDismissListView.Direction direction, View view, int position) {
                Toast.makeText(MainActivity.this, direction.toString() + " removed " + position, Toast.LENGTH_SHORT).show();
                mStringList.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        });

        mStringList = getListData();
        mAdapter = new LayoutListItemAdapter(this);
        mSlideListView.setAdapter(mAdapter);
    }

    private List<String> getListData() {
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            stringList.add("SlideListView item " + i);
        }
        return stringList;
    }


    class LayoutListItemAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;

        public LayoutListItemAdapter(Context context) {
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mStringList.size();
        }

        @Override
        public String getItem(int position) {
            return mStringList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = layoutInflater.inflate(R.layout.layout_list_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.textViewItem = (TextView) convertView.findViewById(R.id.text_view_item);
            viewHolder.textViewItem.setText(getItem(position));
            return convertView;
        }

        protected class ViewHolder {
            private TextView textViewItem;
        }
    }

}
