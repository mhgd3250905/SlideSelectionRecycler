## 仿华为文件管理侧滑连选RecyclerView
>在使用我心爱的P9的时候无意发现了华为文件管理中批量选择文件时可以对列表的右侧进行滑动连续选择，感觉很有趣，于是仿制了一个，效果如下：

![滑动连续选择](http://upload-images.jianshu.io/upload_images/1698871-3badd0508fe25066.gif?imageMogr2/auto-orient/strip)


## 实现

### 第一步,基本的RecyclerView实现

**ItemView非常简单，一个TextView，一个CheckBox；**

	<?xml version="1.0" encoding="utf-8"?>
	<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
	    android:orientation="horizontal" android:layout_width="match_parent"
	    android:layout_height="wrap_content">
	    <TextView
	        android:gravity="center"
	        android:text="测试文本"
	        android:textSize="20dp"
	        android:id="@+id/tv_item"
	        android:layout_weight="1"
	        android:layout_width="match_parent"
	        android:layout_height="match_parent" />
	    <CheckBox
	        android:buttonTint="@color/colorPrimaryDark"
	        android:id="@+id/cb_item"
	        android:layout_weight="1"
	        android:layout_width="100dp"
	        android:layout_height="60dp" />
	</LinearLayout>


![](https://ws1.sinaimg.cn/mw690/006aPzcjgy1fdybwgeo1rj30bd03l747.jpg)

**然后是Adapter、ViewHolder**

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

**都是非常常规的操作~**

### 第二步，滑动连选

整理一下实现思路：

- RecyclerView的左边保持正常滑动不变
- RecyclerView的右边滑动处理为修改CheckBox勾选状态，且拦截touch事件

1.继承RecyclerView自定义控件


	public class HWRecyclerView extends RecyclerView {
	    private float x;
	    private float y;
	    private View view;
	    private CheckBox cbFind;
	    private View viewFind;
	
	
	    public void clecrCheckPositions() {
	        positions.clear();
	    }
	
	    //    private CheckBox cbFind;
	
	
	    public HWRecyclerView(Context context) {
	        super(context, null);
	    }
	
	    public HWRecyclerView(Context context, @Nullable AttributeSet attrs) {
	        super(context, attrs, 0);
	    }
	
	    public HWRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
	        super(context, attrs, defStyle);
	    }
	
	    @Override
	    public boolean onTouchEvent(MotionEvent e) {
	        switch (e.getAction()) {
	
	            case MotionEvent.ACTION_DOWN:
	                break;
	            case MotionEvent.ACTION_MOVE:
	                x = e.getX();
	                y = e.getY();
	                if (x >= (getMeasuredWidth() - 150)) {
	                    if (view != findChildViewUnder(x, y)) {
	                        viewFind = findChildViewUnder(x, y);
	                        pos = (int) viewFind.getTag();
	                        if (viewFind != null) {
	                            cbFind = (CheckBox) viewFind.findViewById(R.id.cb_item);
	
	                            if (viewFind != null) {
	                                if (cbFind != null) {
	                                    boolean checked = cbFind.isChecked();
	                                    if (checked) {
	                                        cbFind.setChecked(!checked);
	                                    } else {
	                                        cbFind.setChecked(!checked);
	                                    }
	                                    view = viewFind;
	                                }
	                            }
	                        }
	                    }
	                    return false;
	                }
	                break;
	            case MotionEvent.ACTION_UP:
	                break;
	            default:
	                break;
	        }
	
	        return super.onTouchEvent(e);
	    }
	}


**可以看到我们主要是复写了onTouch监听事件的ACTION_MOVE监听,其中使用了这个方法```findChildViewUnder(float x, float y)```是在RecyclerView获取包含这个点的ItemView，所以上方的逻辑描述起来就是：**

1.预先设置一个View来作为手指滑动到的ItemView的暂存对象，然后获取手指滑动时候的坐标，如果这个点的x是在右边（这里指定为右边150dp），那么拦截滑动操作；

2.那么我们获取这个点对应的ItemView，如果此刻即时获取的ItemView和暂存起来的一样，那么可以断定为手指还在这一个ItemView中滑动，不需要进行二次更改，而如果不一样，即可以断定为是第一次进入这个ItemView，我们就需要进行动作了；

3、获取到这个ItemView中包含的CheckBox，然后对这个CheckBox的状态进行对应的修改，修改完成后将这个ItemView暂存起来

**但是这只是完成了我们UI上的修改，并没有对其中的数据进行修改，下面我们就来解决这个问题

### 第三步，UI与数据同步

驻足思考一下实现的逻辑：
- 首先我们需要知道滑动过程中有哪些ItemView的checkBox被修改了，以及被修改为了true/false；
- 然后数据的更新我设置为每次的ACTION_UP,也就是抬手之后修改数据,并且清空数据；

**下面就是实现过程：**

1.首先我们需要一个容器来保存我们的数据<修改的Item序号，checkBox的勾选>-->```<int,boolean>```,就是酱紫，这里我选择了```SparseArray<E>```

	private SparseArray<Boolean> positions = new SparseArray<>();

	//清空数据
	public void clecrCheckPositions() {
	        positions.clear();
	    }

然后在每次修改完CheckBox之后保存对应的数据：

	case MotionEvent.ACTION_MOVE:
	                x = e.getX();
	                y = e.getY();
	                if (x >= (getMeasuredWidth() - 150)) {
	                    if (view != findChildViewUnder(x, y)) {
	                        viewFind = findChildViewUnder(x, y);
	                        pos = (int) viewFind.getTag();
	                        if (viewFind != null) {
	                            cbFind = (CheckBox) viewFind.findViewById(R.id.cb_item);
	                            if (viewFind != null) {
	                                if (cbFind != null) {
	                                    boolean checked = cbFind.isChecked();
	                                    if (checked) {
	                                        cbFind.setChecked(!checked);
	                                    } else {
	                                        cbFind.setChecked(!checked);
	                                    }
	                                    positions.put(pos, !checked);
	                                    view = viewFind;
	                                }
	                            }
	                        }
	                    }
	                    return false;
	                }
	                break;

2.因为保存数据涉及到Adapter，所以我们需要在外部调用这个数据，so~我们需要一个接口：

	interface OnMoveCheckListener {
	        void onMoveCheckListener(SparseArray<Boolean> positions);
	    }
	private OnMoveCheckListener onMoveCheckListener;
	
	public void setOnMoveCheckListener(OnMoveCheckListener onMoveCheckListener) {
	        this.onMoveCheckListener = onMoveCheckListener;
	    }

使用接口方法：

	case MotionEvent.ACTION_UP:
	                x = e.getX();
	                if (x >= getMeasuredWidth() - 150) {
	                    if (onMoveCheckListener != null) {
	                        onMoveCheckListener.onMoveCheckListener(positions);
	                    }
	                }
	                break;

也是就是在手指离开的时候触发这个方法~调用接口：   
当然在每次抬手更新数据之后，就需要将SparseArray中的数据清空掉：

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


这样就完成了UI与数据的同步啦~


如果你有更好的实现办法，请不吝赐教~


>## [***点击查看源码***](https://github.com/mhgd3250905/SlideSelectionRecycler)

