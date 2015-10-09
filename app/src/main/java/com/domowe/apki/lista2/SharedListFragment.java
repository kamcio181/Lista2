package com.domowe.apki.lista2;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.NinePatchDrawable;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.dropbox.client2.session.AppKeyPair;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * A simple {@link Fragment} subclass.
 */
public class SharedListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;
    private MyDraggableWithSectionItemAdapter myItemAdapter;

    DropboxAPI<AndroidAuthSession> mApi;

    public SharedListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_list_view_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().invalidateOptionsMenu();
        Log.v("privateList", "view");

        if(Utils.isOnline(getActivity())){
            getDB();
            getSharedList();
        }
    }

    public void getSharedList(){
        Toast.makeText(getActivity(), "Pobieranie listy z serwera",Toast.LENGTH_SHORT).show();
        new DownloadSharedList(getActivity(), mApi).execute();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mApi!=null && mApi.getSession().authenticationSuccessful()) {
            try {
                // Required to complete auth, sets the access token on the session
                mApi.getSession().finishAuthentication();
                String accessToken = mApi.getSession().getOAuth2AccessToken();
                storeAccessToken(accessToken);
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }
    }
    private void storeAccessToken(String aT){
        if(aT != null){
            SharedPreferences prefs = getActivity().getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(Constants.ACCESS_KEY_NAME, "oauth2:");
            edit.putString(Constants.ACCESS_SECRET_NAME, aT);
            edit.apply();
        }
    }

    public void getDB(){
        AppKeyPair appKeys = new AppKeyPair(Constants.APP_KEY, Constants.APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);

        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);

        String key = prefs.getString(Constants.ACCESS_KEY_NAME, null);
        String secret = prefs.getString(Constants.ACCESS_SECRET_NAME, null);

        if (key != null && key.equals("oauth2:")) {
            // If the key is set to "oauth2:", then we can assume the token is for OAuth 2.
            session.setOAuth2AccessToken(secret);
            mApi = new DropboxAPI<>(session);
        } else {
            mApi = new DropboxAPI<>(session);
            mApi.getSession().startOAuth2Authentication(getActivity());
        }
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
        mLayoutManager = null;

        super.onDestroyView();
    }


    public MyDraggableWithSectionItemAdapter getMyItemAdapter() {
        return myItemAdapter;
    }

    public class DownloadSharedList extends AsyncTask<Void, Long, ListDataProvider> {


        private Context mContext;
        private DropboxAPI<?> mApi;

        private FileOutputStream mFos;

        private String mErrorMsg;

        public DownloadSharedList(Context context, DropboxAPI<?> api) {
            // We set the context this way so we don't accidentally leak activities
            mContext = context.getApplicationContext();

            mApi = api;
        }

        @Override
        protected ListDataProvider doInBackground(Void... params) {
            try {
                // Get the metadata for a directory
                DropboxAPI.Entry fileMetadata = mApi.metadata(Constants.PATH_TO_FILE_ON_DROPBOX, 1, null, true, null); //TODO

                if(!fileMetadata.modified.equals("")){

                    mContext.getSharedPreferences(Constants.SHARED_LIST_NAME, Context.MODE_PRIVATE).
                            edit().putString(Constants.REV, fileMetadata.rev).
                            putString(Constants.MODIFICATION_DATE, fileMetadata.modified).apply();

                    File sharedList = Utils.getFileFromName(mContext,Constants.SHARED_LIST_NAME);

                    try {
                        mFos = new FileOutputStream(sharedList);
                    } catch (FileNotFoundException e) {
                        mErrorMsg = "Couldn't create a local file to store the list";
                        return new ListDataProvider(null);
                    }
                    mApi.getFile(Constants.PATH_TO_FILE_ON_DROPBOX,fileMetadata.rev,mFos,null);
                    return new ListDataProvider(sharedList);
                }
            } catch (DropboxUnlinkedException e) {
                // The AuthSession wasn't properly authenticated or user unlinked.
            } catch (DropboxPartialFileException e) {
                // We canceled the operation
                mErrorMsg = "Download canceled";
            } catch (DropboxServerException e) {
                // Server-side exception.  These are examples of what could happen,
                // but we don't do anything special with them here.
                if (e.error == DropboxServerException._304_NOT_MODIFIED) {
                    // won't happen since we don't pass in revision with metadata
                } else if (e.error == DropboxServerException._401_UNAUTHORIZED) {
                    // Unauthorized, so we should unlink them.  You may want to
                    // automatically log the user out in this case.
                } else if (e.error == DropboxServerException._403_FORBIDDEN) {
                    // Not allowed to access this
                } else if (e.error == DropboxServerException._404_NOT_FOUND) {
                    // path not found (or if it was the thumbnail, can't be
                    // thumbnailed)
                } else if (e.error == DropboxServerException._406_NOT_ACCEPTABLE) {
                    // too many entries to return
                } else if (e.error == DropboxServerException._415_UNSUPPORTED_MEDIA) {
                    // can't be thumbnailed
                } else if (e.error == DropboxServerException._507_INSUFFICIENT_STORAGE) {
                    // user is over quota
                } else {
                    // Something else
                }
                // This gets the Dropbox error, translated into the user's language
                mErrorMsg = e.body.userError;
                if (mErrorMsg == null) {
                    mErrorMsg = e.body.error;
                }
            } catch (DropboxIOException e) {
                // Happens all the time, probably want to retry automatically.
                mErrorMsg = "Network error.  Try again.";
            } catch (DropboxParseException e) {
                // Probably due to Dropbox server restarting, should retry
                mErrorMsg = "Dropbox error.  Try again.";
            } catch (DropboxException e) {
                // Unknown error
                mErrorMsg = "Unknown error.  Try again.";
            }
            return new ListDataProvider(null);
        }



        @Override
        protected void onPostExecute(ListDataProvider dataProvider) {

            if(dataProvider != null){
                Toast.makeText(getActivity(), "Lista pobrana",Toast.LENGTH_SHORT).show();
                ((MainActivity)getActivity()).setSharedListUpdated(true);

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
                myItemAdapter = new MyDraggableWithSectionItemAdapter(getActivity(), dataProvider);

                myItemAdapter.setOnItemClickListener(new MyDraggableWithSectionItemAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        int last = myItemAdapter.getLast();
                        AbstractDataProvider provider = myItemAdapter.getProvider();

                        provider.moveItem(position, last + 1);

                        myItemAdapter.notifyItemMoved(position, last + 1);
                        provider.getItem(last + 1).changeViewType();
                        myItemAdapter.notifyItemChanged(last + 1);

                    }
                });


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
            else
                Toast.makeText(getActivity(), "Nie udalo sie pobrac listy", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean supportsViewElevation() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }
}

