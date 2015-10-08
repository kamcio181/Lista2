package com.domowe.apki.lista2;


import android.content.Context;
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

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * A simple {@link Fragment} subclass.
 */
public class SharedListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;
    private MyDraggableWithSectionItemAdapter myItemAdapter;

    private File file;

    DropboxAPI<AndroidAuthSession> mApi;
    private String rev = "";

    public SharedListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            file = Utils.getFileFromName(getActivity(), Constants.SHARED_LIST_NAME);
        }
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

        //if(Utils.isOnline(getActivity()))
            //TODO get file from dropbox and get data provider
            //((MainActivity)getActivity()).setSharedListUpdated(true);




    }

    /*@Override
    protected void onResume() {
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
            SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, "oauth2:");
            edit.putString(ACCESS_SECRET_NAME, aT);
            edit.commit();
            return;
        }
    }

    public void getDB(){
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);

        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);

        if (key != null && key.equals("oauth2:")) {
            // If the key is set to "oauth2:", then we can assume the token is for OAuth 2.
            session.setOAuth2AccessToken(secret);
            mApi = new DropboxAPI<>(session);
        } else {
            mApi = new DropboxAPI<>(session);
            mApi.getSession().startOAuth2Authentication(MainActivity2.this);
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
    }*/
    public class DownloadRandomPicture extends AsyncTask<Void, Long, ListDataProvider> {


        private Context mContext;
        private DropboxAPI<?> mApi;

        private FileOutputStream mFos;

        private String mErrorMsg;

        // Note that, since we use a single file name here for simplicity, you
        // won't be able to use this code for two simultaneous downloads.
        private final static String IMAGE_FILE_NAME = "dbroulette.png";

        public DownloadRandomPicture(Context context, DropboxAPI<?> api) {
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
                    //TODO pobierz plik i zapisz date w prefs
                    mContext.getSharedPreferences(Constants.SHARED_LIST_NAME, Context.MODE_PRIVATE).edit().putString("rev", fileMetadata.rev).putString("date", fileMetadata.modified).apply();
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
    }
    private boolean supportsViewElevation() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }
}

