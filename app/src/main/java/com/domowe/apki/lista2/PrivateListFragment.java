package com.domowe.apki.lista2;


import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class PrivateListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;
    private MyDraggableWithSectionItemAdapter myItemAdapter;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private File file;
    private String name;
    private int id;

    public static PrivateListFragment newInstance(String param1, int param2) {
        PrivateListFragment fragment = new PrivateListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PrivateListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(ARG_PARAM1);
            id = getArguments().getInt(ARG_PARAM2);
            file = Utils.getFileFromName(getActivity(), name);
            Log.v("privateList", "create");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_list_view_main,container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().invalidateOptionsMenu();
        Log.v("privateList", "view");
        ListDataProvider dataProvider = new ListDataProvider(file);

        //noinspection ConstantConditions
        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
        mRecyclerViewDragDropManager.setDraggingItemShadowDrawable(
                (NinePatchDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.material_shadow_z1_9));

        mRecyclerViewDragDropManager.setInitiateOnLongPress(true);
        mRecyclerViewDragDropManager.setInitiateOnMove(false);

        //adapter
        myItemAdapter = new MyDraggableWithSectionItemAdapter(getActivity(),dataProvider);

        myItemAdapter.setOnItemClickListener(new MyDraggableWithSectionItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                int last = myItemAdapter.getLast();
                AbstractDataProvider provider = myItemAdapter.getProvider();

                provider.moveItem(position, last + 1);

                myItemAdapter.notifyItemMoved(position, last + 1);
                provider.getItem(last+1).changeViewType();
                myItemAdapter.notifyItemChanged(last+1);

            }
        });

        mAdapter = myItemAdapter;

        mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(myItemAdapter);      // wrap for dragging

        final GeneralItemAnimator animator = new RefactoredDefaultItemAnimator();

        mRecyclerView.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter
        mRecyclerView.setItemAnimator(animator);

        // additional decorations
        //noinspection StatementWithEmptyBody
        if (supportsViewElevation()) {
            // Lollipop or later has native drop shadow feature. ItemShadowDecorator is not required.
        } else {
            mRecyclerView.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.material_shadow_z1_9)));
        }
        mRecyclerView.addItemDecoration(new SimpleListDividerDecorator(ContextCompat.getDrawable(getActivity(), R.drawable.list_divider), true));

        mRecyclerViewDragDropManager.attachRecyclerView(mRecyclerView);

    }

    @Override
    public void onPause() {
        mRecyclerViewDragDropManager.cancelDrag();
        Log.v("privateList", "pause");
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (mRecyclerViewDragDropManager != null) {
            mRecyclerViewDragDropManager.release();
            mRecyclerViewDragDropManager = null;
        }

        if (mRecyclerView != null) {
            mRecyclerView.setItemAnimator(null);
            mRecyclerView.setAdapter(null);
            mRecyclerView = null;
        }

        if (mWrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(mWrappedAdapter);
            mWrappedAdapter = null;
        }
        mAdapter = null;
        mLayoutManager = null;

        super.onDestroyView();
    }

    private boolean supportsViewElevation() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v("privateList", "stop");
        if(((MainActivity)getActivity()).isRemoveNote()){
            if (file.exists())
                file.delete();
            new SaveReadFile(Utils.getFileFromName(getActivity(),Constants.LIST_ITEMS_FILE)).removeItemFromList(id);
        }
        else{
            new SaveReadFile(file).writeProviderItems(myItemAdapter.getProvider());
        }
    }

    public MyDraggableWithSectionItemAdapter getMyItemAdapter() {
        return myItemAdapter;
    }

    public void setFile(File file) {
        this.file = file;
    }
}

