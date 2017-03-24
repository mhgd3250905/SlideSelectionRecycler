package com.skkk.ww.hwrecyclerviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.SparseArray;
import android.view.View;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private float x;
    private float y;
    private View view;
    private CheckBox cbFind;
    private View viewFind;

    private HWRecyclerView mRvMain;
    private List<DateModle> dataList;
    private HWAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRvMain= (HWRecyclerView) findViewById(R.id.rv_main);


        dataList = new ArrayList<>();

        //获取数据
        for (int i = 0; i < 40; i++) {
            dataList.add(new DateModle("第" + i + "条数据", false));
        }

        LinearLayoutManager linearManager = new LinearLayoutManager(MainActivity.this);
        mRvMain.setLayoutManager(linearManager);
        adapter = new HWAdapter(MainActivity.this, dataList);
        mRvMain.setAdapter(adapter);

        mRvMain.setOnMoveCheckListener(new HWRecyclerView.OnMoveCheckListener() {

            @Override
            public void onMoveCheckListener(SparseArray<Boolean> positions) {
                for (int i = 0; i < positions.size(); i++) {
                    dataList.get(positions.keyAt(i)).setChecked(positions.get(positions.keyAt(i)));
                    adapter.notifyItemChanged(positions.keyAt(1));
                }
                mRvMain.clecrCheckPositions();
            }
        });
    }
}
