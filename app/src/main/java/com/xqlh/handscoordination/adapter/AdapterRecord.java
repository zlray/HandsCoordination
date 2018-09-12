package com.xqlh.handscoordination.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.xqlh.handscoordination.R;
import com.xqlh.handscoordination.activity.DetailActivity;
import com.xqlh.handscoordination.entity.Entity;
import com.xqlh.handscoordination.event.EventCheck;
import com.xqlh.handscoordination.event.EventSql;
import com.xqlh.handscoordination.utils.MySqliteOpenHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/8/30.
 */

public class AdapterRecord extends RecyclerView.Adapter<AdapterRecord.MineViewHolder> {
    private Context mContext;
    private List<Entity> mMineList;
    private List<Entity> mList = new ArrayList<>();
    SQLiteDatabase db;
    MySqliteOpenHelper mySqliteOpenHelper;
    // 存储勾选框状态的map集合
    private Map<Integer, Boolean> map = new HashMap<>();
    Entity entityData = new Entity();
    Entity entity;
    String time;

    public AdapterRecord(Context mContext, List<Entity> mMineList) {
        this.mContext = mContext;
        this.mMineList = mMineList;
        Log.i("lz", mMineList.size() + "mMineList.size()        mMineList.size()          mMineList.size()");
        initMap();
    }

    //初始化map集合,默认为不选中
    private void initMap() {
        for (int i = 0; i < mMineList.size(); i++) {
            map.put(i, false);
        }
    }

    //返回集合给MainActivity
    public Map<Integer, Boolean> getMap() {
        return map;
    }

    public Entity getEntity() {
        return entityData;
    }

    public List<Entity> getEntityList() {
        return mList;
    }

    public void clearlist() {
        mList.clear();
    }

    public List<Entity> a() {
        for (int i = 0; i < mMineList.size(); i++) {
            if (map.get(i)) {
                entityData = mMineList.get(i);
                mList.add(entityData);
            }
        }
        return mList;

    }

    @Override
    public MineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //定义一个视图
        View childView = null;

        //布局加载器
        LayoutInflater inflater = LayoutInflater.from(mContext);

        //找到子视图
        childView = inflater.inflate(R.layout.item_record, parent, false);


        //实例化mViewHolder
        MineViewHolder mViewHolder = new MineViewHolder(childView);

        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(final MineViewHolder holder, final int position) {

        //itemView设置位置
        holder.itemView.setTag(position);
        //
        entity = mMineList.get(position);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //用map集合保存
                map.put(position, isChecked);
                EventBus.getDefault().post(new EventCheck("updateCheck"));
            }
        });
        // 设置CheckBox的状态
        if (map.get(position) == null) {
            map.put(position, false);
        }
        holder.checkBox.setChecked(map.get(position));

        holder.tv_id.setText(entity.getId());
        holder.tv_name.setText(entity.getName());
        holder.tv_searchTime.setText(entity.getSearchTime());
        holder.bt_look.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "查看", Toast.LENGTH_SHORT).show();

                //时间通过位置来获取
                String detailTime = mMineList.get(position).getDetailTime(); //时间
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("detailTime", detailTime);
                mContext.startActivity(intent);
            }
        });
        holder.bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNormalDialog(mMineList.get(position));

            }
        });
    }

    private void showNormalDialog(final Entity entity) {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(mContext);
//        normalDialog.setIcon(R.drawable.icon_dialog);
        normalDialog.setTitle("删除");
        normalDialog.setMessage("确认删除？");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        mySqliteOpenHelper = new MySqliteOpenHelper(mContext);
                        db = mySqliteOpenHelper.getWritableDatabase();
                        String sql = "delete  from data where detailTime = ?";
                        db.execSQL(sql, new Object[]{entity.getDetailTime()});
                        EventBus.getDefault().post(new EventSql("updateAll"));
                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }

    @Override
    public int getItemCount() {
        if (mMineList != null) {
            return mMineList.size();
        }
        return 0;
    }

    class MineViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.checkBox)
        CheckBox checkBox;

        @Bind(R.id.tv_id)
        TextView tv_id;

        @Bind(R.id.tv_name)
        TextView tv_name;

        @Bind(R.id.tv_searchTime)
        TextView tv_searchTime;

        @Bind(R.id.bt_look)
        Button bt_look;

        @Bind(R.id.bt_clear)
        Button bt_clear;

        public MineViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
