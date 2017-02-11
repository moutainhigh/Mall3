package com.hxqc.mall.core.adapter.me;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.hxqc.mall.core.model.bill.BalanceBill;
import com.hxqc.mall.core.model.bill.BalanceBillList;
import com.hxqc.mall.core.model.bill.BalanceBillMatter;
import com.hxqc.mall.core.util.OtherUtil;
import com.hxqc.mall.core.util.activityutil.ActivitySwitcher;
import com.hxqc.mall.core.views.recyclerview.VRecyclerViewFooter;

import java.util.ArrayList;
import java.util.HashMap;

import hxqc.mall.R;

/**
 * Author:李烽
 * Date:2016-03-03
 * FIXME
 * Todo 账单列表的适配器
 */
public class MyBalanceBillListAdapter_1 extends MyBillListAdapter_1 {
    private ArrayList<BalanceBillList> mData = new ArrayList<>();
    private BalanceBill balanceBill;
    private ArrayList<HashMap<String, String>> groupName;
    private ArrayList<BalanceBillMatter> childData;


    public MyBalanceBillListAdapter_1(VRecyclerViewFooter vRecyclerViewFooter,
                                      BalanceBill balanceBill, ArrayList<BalanceBillList> mData, Context mContext) {
        super(vRecyclerViewFooter);
        this.balanceBill = balanceBill;
        this.mData = mData;
        this.mContext = mContext;
        initData();
    }

    public void notifyData(BalanceBill balanceBill, ArrayList<BalanceBillList> mData) {
        this.balanceBill = balanceBill;
        this.mData.clear();
        this.mData.addAll(mData);
        initData();
        notifyDataSetChanged();
    }

    /**
     * 数据分层
     */
    private void initData() {
        groupName = new ArrayList<>();
        childData = new ArrayList<>();
        if (mData != null)
            if (mData.size() > 0)
                for (int i = 0; i < mData.size(); i++) {
                    BalanceBillList list = mData.get(i);
                    if (i > 1) {
                        if (TextUtils.isEmpty(list.month)) {
                            mData.get(i).month = mData.get(i - 1).month;
                        }
                    }
                    if (list.matter.size() > 0)
                        for (BalanceBillMatter matter : list.matter) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("month", list.month);
                            map.put("subtotal", list.subtotal);
                            groupName.add(map);
                            childData.add(matter);
                        }
                }
    }


    @Override
    public long getHeaderId(int position) {
        if (position == 0)
            return -1;
        if (position == getItemCount() - 1)
            return -1;
        for (int i = 0; i < mData.size(); i++) {
            String month = mData.get(i).month;
            if (!TextUtils.isEmpty(month)) {
                if (month.equals(groupName.get(position - 1).get("month")))
                    return i;
            } else
                return i - 1;
        }
        return -1;
    }


    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < getItemCount() - 1) {
            ViewHeadHolder headHolder = (ViewHeadHolder) holder;
            HashMap<String, String> map = groupName.get(position - 1);
//            headHolder.month.setText(OtherUtil.singleMonth(map.get("month")));
            headHolder.month.setText(map.get("month"));
            headHolder.totalCost.setText(String.format("%s%s%s", mContext.getString(R.string.this_month_cost),
                    map.get("subtotal"), mContext.getString(R.string.yuan)));
        }
    }


    @Override
    public void onVBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        String yuan = mContext.getString(R.string.yuan);
        switch (getItemViewType(position)) {
            case TYPE_HEAD:
                HeaderHolder headerHolder = (HeaderHolder) holder;
                headerHolder.label_type.setText(mContext.getString(R.string.usable_balance));
                if (balanceBill.curPage != null)
                    if (balanceBill.curPage.equals("1")) {
//                        headerHolder.total_charge.setText(String.format("%s%s%s", mContext.getString(R.string.bill_total_charge),
//                                OtherUtil.reformatPriceNoEndZero(balanceBill.prepaidAmount), yuan));
//                        headerHolder.total_cost.setText(String.format("%s%s%s", mContext.getString(R.string.bill_total_cost),
//                                OtherUtil.reformatPriceNoEndZero(balanceBill.expendamount), yuan));
//                        if (balanceBill.prepaidAmount == -1) headerHolder.total_charge.setText("");
//                        if (balanceBill.expendamount == -1) headerHolder.total_cost.setText("");
                    }
//                if (!TextUtils.isEmpty(balanceBill.balance))
//                    headerHolder.number.setText(OtherUtil.reformatPriceNoEndZero(balanceBill.balance));
//                    headerHolder.number.setText(com.hxqc.mall.usedcar.utils.OtherUtil.formatBalance(balanceBill.balance));
                headerHolder.unit.setText(yuan);

                if (balanceBill.billCount == 0) {
                    headerHolder.empty_layout.setVisibility(View.VISIBLE);
                    headerHolder.icon_empty.setImageResource(R.drawable.ic_balance);
                    headerHolder.text_empty.setText(R.string.empty_balance);
                } else headerHolder.empty_layout.setVisibility(View.GONE);
                break;
            case TYPE_NORMAL:
                ViewChildHolder childHolder = (ViewChildHolder) holder;
                BalanceBillMatter matter = childData.get(position - 1);
                String payTime = matter.payTime;
                if (!TextUtils.isEmpty(payTime))
//                    payTime = payTime.substring(0, 10);
                    childHolder.date.setText(payTime);
                childHolder.description.setText(matter.description);
                childHolder.rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivitySwitcher.toMoneyBillDetail(mContext, childData.get(position - 1).billID + "");
                    }
                });

        /*文字的颜色*/
                if (matter.getTransactionType().equals("充值")) {
                    childHolder.number.setTextColor(Color.parseColor("#FE7143"));
                } else if (matter.getTransactionType().equals("消费")) {
                    childHolder.number.setTextColor(Color.parseColor("#66828F"));
                } else {
                    childHolder.number.setTextColor(Color.parseColor("#e02c36"));
                }
                childHolder.number.setText
                        (matter.getTransactionType() +
                                OtherUtil.reformatPriceNoEndZero(matter.number) + yuan);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return childData == null ? 0 : childData.size() + 2;
    }

}
