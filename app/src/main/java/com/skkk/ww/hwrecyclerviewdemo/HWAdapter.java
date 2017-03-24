package com.skkk.ww.hwrecyclerviewdemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by admin on 2017/3/23.
 */
/*
* 
* 描    述：RecyclerView数据适配器
* 作    者：ksheng
* 时    间：2017/3/23$ 22:37$.
*/
public class HWAdapter extends RecyclerView.Adapter<HWAdapter.HWViewHolder>{
    private List<DateModle> dateModleList;
    private Context context;

    public HWAdapter(Context context, List<DateModle> dateModleList) {
        this.context = context;
        this.dateModleList = dateModleList;
    }

    @Override
    public HWViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HWViewHolder holder=new HWViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recycler,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(HWViewHolder holder, int position) {
        holder.tbItem.setText(dateModleList.get(position).getTitle());
        holder.cbItem.setChecked(dateModleList.get(position).isChecked());
        holder.itemView.setTag(position);

    }

    @Override
    public int getItemCount() {
        return dateModleList.size();
    }

    class HWViewHolder extends RecyclerView.ViewHolder{
        public TextView tbItem;
        public CheckBox cbItem;
        public HWViewHolder(View itemView) {
            super(itemView);
            tbItem= (TextView) itemView.findViewById(R.id.tv_item);
            cbItem= (CheckBox) itemView.findViewById(R.id.cb_item);
        }
    }
}
