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
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

public class MyDraggableWithSectionItemAdapter2
        extends RecyclerView.Adapter<MyDraggableWithSectionItemAdapter2.MyViewHolder>
        implements DraggableItemAdapter<MyDraggableWithSectionItemAdapter2.MyViewHolder> {

    private IndexDataProvider mProvider;
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
        public TextView mName;
        public TextView mCreatedDate;
        public TextView mType;
        public RelativeLayout mBody;

        public MyViewHolder(final View v) {
            super(v);
            mContainer = (FrameLayout) v.findViewById(R.id.container);
            mBody = (RelativeLayout) v.findViewById(R.id.body);
            mDragHandle = v.findViewById(R.id.drag_handle);
            mName = (TextView) v.findViewById(R.id.name);
            mCreatedDate = (TextView) itemView.findViewById(R.id.date);
            mType = (TextView) itemView.findViewById(R.id.type);
            mBody.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null)
                        listener.onItemClick(view, getLayoutPosition());
                }
            });
        }
    }

    public MyDraggableWithSectionItemAdapter2(Context context, IndexDataProvider dataProvider) {
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
        return Constants.ITEM_VIEW_TYPE_SECTION_ITEM_ACTIVE;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        final View v = inflater.inflate(R.layout.list_item_draggable_64_main, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
                onBindSectionItemViewHolder(holder, position);
    }

    private void onBindSectionItemViewHolder(MyViewHolder holder, final int position) {
        final ListObject item = mProvider.getItem(position);

        /*ListObject item = mProvider.getItem(position);
        holder.mName.setText(item.getName());
        holder.mCreatedDate.setText(item.getCreateDate());
        holder.mType.setText(item.getType());*/


        // set text
        holder.mName.setText(item.getName());
        holder.mCreatedDate.setText(item.getCreateDate());
        holder.mType.setText(item.getType());


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

    public IndexDataProvider getProvider() {
        return mProvider;
    }

    @Override
    public boolean onCheckCanStartDrag(MyViewHolder holder, int position, int x, int y) {
        // x, y --- relative from the itemView's top-left

        final View containerView = holder.mContainer;
        final View dragHandleView = holder.mDragHandle;

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(MyViewHolder holder, int position) {
        return new ItemDraggableRange(0, mProvider.getCount()-1);
    }
}
