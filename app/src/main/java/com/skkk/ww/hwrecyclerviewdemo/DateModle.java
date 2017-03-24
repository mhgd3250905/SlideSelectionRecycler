package com.skkk.ww.hwrecyclerviewdemo;

/**
 * Created by admin on 2017/3/23.
 */
/*
* 
* 描    述：数据模型
* 作    者：ksheng
* 时    间：2017/3/23$ 22:33$.
*/
public class DateModle {

    private String title;
    private boolean checked;

    public DateModle(String title, boolean checked) {
        this.title = title;
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
