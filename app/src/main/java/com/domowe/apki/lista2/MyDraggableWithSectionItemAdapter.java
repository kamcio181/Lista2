/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.domowe.apki.lista2;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

public class MyDraggableWithSectionItemAdapter
        extends RecyclerView.Adapter<MyDraggableWithSectionItemAdapter.MyViewHolder>
        implements DraggableItemAdapter<MyDraggableWithSectionItemAdapter.MyViewHolder> {
    private static final String TAG = "MyDragSectionAdapter";

    private AbstractDataProvider mProvider;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public class MyViewHolder extends AbstractDraggableItemViewHolder {
        public FrameLayout mContainer;
        public View mDragHandle;
        public CustomTextView mName;
        public CustomTextView mQuantity;
        public CustomTextView mNewItem;
        public LinearLayout mBody;

        public MyViewHolder(final View v) {
            super(v);
            mContainer = (FrameLayout) v.findViewById(R.id.container);
            mBody = (LinearLayout) v.findViewById(R.id.body);
            mDragHandle = v.findViewById(R.id.drag_handle);
            mName = (CustomTextView) v.findViewById(R.id.name);
            mQuantity = (CustomTextView) v.findViewById(R.id.quantity);
            mNewItem = (CustomTextView) v.findViewById(R.id.newItem);
            if(mBody!=null)
                mBody.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(listener != null)
                            listener.onItemClick(v, getLayoutPosition());

                    }
                });
        }
    }

    public MyDraggableWithSectionItemAdapter(Context context,AbstractDataProvider dataProvider) {
        mProvider = dataProvider;
        this.context = context;
        // DraggableItemAdapter requires stable ID, and also
        // have to implement the getItemId() method appropriately.
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return mProvider.getItem(position).getId();
    }

    @Override
    public int getItemViewType(int position) {
        return mProvider.getItem(position).getViewType();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        final View v = viewType == Constants.ITEM_VIEW_TYPE_SECTION_HEADER ? inflater.inflate(R.layout.list_section_header, parent, false) : inflater.inflate(R.layout.list_item_draggable_48, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case Constants.ITEM_VIEW_TYPE_SECTION_HEADER:
                onBindSectionHeaderViewHolder(holder, position);
                break;
            case Constants.ITEM_VIEW_TYPE_SECTION_ITEM_ACTIVE:
            case Constants.ITEM_VIEW_TYPE_SECTION_ITEM_INACTIVE:
                onBindSectionItemViewHolder(holder, position);
                break;
        }
    }

    private void onBindSectionHeaderViewHolder(MyViewHolder holder, int position) {
        final AbstractDataProvider.Data item = mProvider.getItem(position);

        if(position!=0){
            ViewGroup.LayoutParams params = holder.mName.getLayoutParams();
            params.height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, context.getResources().getDisplayMetrics());//zmiana wielkosci
            holder.mName.setLayoutParams(params);
        }
        // set text
        holder.mName.setText(item.getText());

    }

    private void onBindSectionItemViewHolder(MyViewHolder holder, final int position) {
        final AbstractDataProvider.Data item = mProvider.getItem(position);

        // set text
        holder.mName.setText(item.getText());
        holder.mQuantity.setText(item.getQuantity());
        holder.mName.setMeasure();
        holder.mQuantity.setMeasure();

        if(item.getViewType() == Constants.ITEM_VIEW_TYPE_SECTION_ITEM_INACTIVE){
            int color = ContextCompat.getColor(context, R.color.primaryColor);
            holder.mName.setStrikeEnabled(true, color);
            holder.mQuantity.setStrikeEnabled(true, color);
            holder.mNewItem.setStrikeEnabled(true, color);
            holder.mName.invalidate();
            holder.mQuantity.invalidate();
            holder.mNewItem.invalidate();
            /*holder.mName.setPaintFlags(holder.mName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.mQuantity.setPaintFlags(holder.mQuantity.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.mNewItem.setPaintFlags(holder.mNewItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);*/
        }
        if(item.isNewItem())
            holder.mNewItem.setVisibility(View.VISIBLE);

        // set background resource (target view ID: container)
        final int dragState = holder.getDragStateFlags();

        if (((dragState & RecyclerViewDragDropManager.STATE_FLAG_IS_UPDATED) != 0)) {
            int bgResId;

            if ((dragState & RecyclerViewDragDropManager.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_dragging_active_state;

                // need to clear drawable state here to get correct appearance of the dragging item.
                DrawableUtils.clearState(holder.mContainer.getForeground());
            } else if (
                    ((dragState & RecyclerViewDragDropManager.STATE_FLAG_DRAGGING) != 0) &&
                    ((dragState & RecyclerViewDragDropManager.STATE_FLAG_IS_IN_RANGE) != 0)) {
                bgResId = R.drawable.bg_item_dragging_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }

            holder.mContainer.setBackgroundResource(bgResId);

        }
    }

    @Override
    public int getItemCount() {
        return mProvider.getCount();
    }



    @Override
    public void onMoveItem(int fromPosition, int toPosition) {

        if (fromPosition == toPosition) {
            return;
        }

        mProvider.moveItem(fromPosition, toPosition);

        notifyItemMoved(fromPosition, toPosition);
    }

    public AbstractDataProvider getProvider() {
        return mProvider;
    }

    public int getLast() {
        return findLastSectionItem(1);
    }

    @Override
    public boolean onCheckCanStartDrag(MyViewHolder holder, int position, int x, int y) {
        // x, y --- relative from the itemView's top-left

        // return false if the item is a section header
        if (holder.getItemViewType() == Constants.ITEM_VIEW_TYPE_SECTION_HEADER) {
            return false;
        }

        final View containerView = holder.mContainer;
        final View dragHandleView = holder.mDragHandle;

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(MyViewHolder holder, int position) {
        final int start = findFirstSectionItem(position);
        final int end = findLastSectionItem(position);

        return new ItemDraggableRange(start, end);
    }

    private int findFirstSectionItem(int position) {
        AbstractDataProvider.Data item = mProvider.getItem(position);

        if (item.isSectionHeader()) {
            throw new IllegalStateException("section item is expected");
        }

        while (position > 0) {
            AbstractDataProvider.Data prevItem = mProvider.getItem(position - 1);

            if (prevItem.isSectionHeader()) {
                break;
            }

            position -= 1;
        }

        return position;
    }

    private int findLastSectionItem(int position) {
        AbstractDataProvider.Data item = mProvider.getItem(position);

        if (item.isSectionHeader()) {
            return 0;
            //throw new IllegalStateException("section item is expected");
        }

        final int lastIndex = getItemCount() - 1;

        while (position < lastIndex) {
            AbstractDataProvider.Data nextItem = mProvider.getItem(position + 1);

            if (nextItem.isSectionHeader()) {
                break;
            }

            position += 1;
        }

        return position;
    }
}
