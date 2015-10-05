package com.domowe.apki.lista2;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ScrollView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoteFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private File file;

    // TODO: Rename and change types of parameters
    private String name;
    private int id;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment NoteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NoteFragment newInstance(String param1, int param2) {
        NoteFragment fragment = new NoteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public NoteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(ARG_PARAM1);
            id = getArguments().getInt(ARG_PARAM2);
            file = Utils.getFileFromName(getActivity(), name);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().invalidateOptionsMenu();


        new LoadNote().execute(file);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(((MainActivity)getActivity()).isRemoveNote()){
            if (file.exists())
                file.delete();
            new SaveReadFile(Utils.getFileFromName(getActivity(),Constants.LIST_ITEMS_FILE)).removeItemFromList(id);
        }
        else{
            EditText editText = (EditText) getActivity().findViewById(R.id.editText2);
            new SaveReadFile(file).writeNote(editText.getText().toString());
        }
    }

    private class LoadNote extends AsyncTask<File,Void,String> {
        @Override
        protected String doInBackground(File... files) {
            ArrayList<String> resultList = new ArrayList();
            String note = "";
            if(files[0].exists()){
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(files[0])));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        resultList.add(line);
                    }
                    reader.close();
                }
                catch (IOException ex) {
                    throw new RuntimeException("Error in reading file: "+ex);
                }
            }
            for(String line : resultList)
                note = note + line +"\n";
            return note;
        }

        @Override
        protected void onPostExecute(String note) {
            super.onPostExecute(note);
            final EditText editText = (EditText) getActivity().findViewById(R.id.editText2);
            editText.setText(note);
            editText.setTextSize(getActivity().getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE).getInt(Constants.NOTE_TEXT_SIZE_KEY, 18));
            Utils.showKeyboard(getActivity(), editText);
            editText.setSelection(note.length());

            final ScrollView scrollView = (ScrollView) getActivity().findViewById(R.id.scrollView);
            scrollView.setForeground(new DrawnLines(getActivity(), scrollView));
            scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                int offset = 0;
                @Override
                public void onScrollChanged() {
                    Log.v("note", "   "+(offset+editText.getLineHeight())+"     "+scrollView.getScrollY());
                    if(scrollView.getScrollY() >=  offset+editText.getLineHeight()) {
                        scrollView.setForeground(new DrawnLines(getActivity(), scrollView));
                        offset = scrollView.getScrollY();
                        Log.v("note","true");
                    }
                }
            });
        }
    }

    public void setFile(File file) {
        this.file = file;
    }
}
class DrawnLines extends Drawable{
    private Context context;
    private ScrollView view;

    public DrawnLines(Context context, ScrollView view) {
        this.context = context;
        this.view = view;
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(context, R.color.primaryColorLight2));
        paint.setStrokeWidth(context.getResources().getDisplayMetrics().density * 1);

        EditText editText = (EditText) view.getChildAt(0);

        int paddingTop = editText.getLineHeight()-editText.getBaseline()+1;
        int lineHeight = editText.getLineHeight();
        Log.v("note",""+editText.getLineSpacingExtra() );
        int totalHeight = editText.getHeight() > view.getHeight() ? editText.getHeight() : view.getHeight();
        int linesCount = totalHeight/lineHeight;
        int paddingLeft = view.getPaddingLeft();
        int x = view.getWidth()-paddingLeft;
        int y;

        for (int i = 1; i<=linesCount; i++){
            y = i*lineHeight + paddingTop;
            canvas.drawLine(paddingLeft, y, x, y, paint);
        }
    }

    @Override
    public void setAlpha(int i) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}