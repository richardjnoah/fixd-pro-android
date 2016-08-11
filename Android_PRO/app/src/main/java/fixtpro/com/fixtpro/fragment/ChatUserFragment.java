package fixtpro.com.fixtpro.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.server.BaseService;

import org.json.JSONException;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;
import fixtpro.com.fixtpro.ChatActivity;
import fixtpro.com.fixtpro.ChatActivityNew;
import fixtpro.com.fixtpro.HomeScreenNew;
import fixtpro.com.fixtpro.R;
import fixtpro.com.fixtpro.adapters.UserChatAdapters;
import fixtpro.com.fixtpro.utilites.ChatService;
import fixtpro.com.fixtpro.utilites.ChatSingleton;
import fixtpro.com.fixtpro.utilites.Constants;
import fixtpro.com.fixtpro.utilites.Utilities;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatUserFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatUserFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    ListView lstUser;
    Dialog progressDialog = null;
    private static final String EXTRA_DIALOG = "dialog";
    public ChatUserFragment() {
        // Required empty public constructor
    }

    UserChatAdapters adapters;
    private CoordinatorLayout coordinatorLayout;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatUserFragment newInstance(String param1, String param2) {
        ChatUserFragment fragment = new ChatUserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        ((HomeScreenNew)getActivity()).setCurrentFragmentTag(Constants.CHATUSER_FRAGMENT);
        setupToolBar();
//        getInternetStatus();
    }
    private void setupToolBar(){
        ((HomeScreenNew)getActivity()).hideRight();
        ((HomeScreenNew)getActivity()).setTitletext("Messages");
        ((HomeScreenNew)getActivity()).setLeftToolBarImage(R.drawable.menu_icon);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_user, container, false);

        lstUser = (ListView)view.findViewById(R.id.lstUser);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id
                .coordinatorLayout);
        lstUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                QBDialog selectedDialog = (QBDialog) adapters.getItem(position);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable(ChatActivity.EXTRA_DIALOG, selectedDialog);
//                try {
//                    JSONObject jsonObject = new JSONObject(selectedDialog.getPhoto());
//                    Iterator<String> iter = jsonObject.keys();
//                    while (iter.hasNext()) {
//                        String key = iter.next();
//                        try {
//                            Object value = jsonObject.get(key);
//                            JSONObject object = new JSONObject(value.toString());
//                            bundle.putSerializable("name", object.getString("name"));
//                            break;
//                        } catch (JSONException e) {
//                            // Something went wrong!
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
////                 Open chat activity
//                ChatActivity.start(getActivity(), bundle);
                QBDialog selectedDialog = (QBDialog) parent.getItemAtPosition(position);
                Intent i = new Intent(getActivity(), ChatActivityNew.class);
                i.putExtra(EXTRA_DIALOG,selectedDialog);
                startActivity(i);
            }
        });

        adapters = new UserChatAdapters(ChatSingleton.getInstance().dataSourceUsers,getActivity());
        lstUser.setAdapter(adapters);
        if (getInternetStatus()){

            if (ChatSingleton.getInstance().dataSourceUsers.size() == 0){
                progressDialog = new Dialog(getActivity());
                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                LayoutInflater inflater1 = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = inflater1.inflate(R.layout.dialog_progress_simple, null);
                progressDialog.setContentView(customView);
                progressDialog.setCancelable(false);
                progressDialog.show();
                getChatUsers();
            }
        }


        return view;
    }
    private boolean getInternetStatus(){
        if (!Utilities.isNetworkAvailable(getActivity())){
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_LONG)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    });

            // Changing message text color
            snackbar.setActionTextColor(Color.parseColor("#fa7507"));

            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);

            snackbar.show();
            return false;
        }else {
            return true;
        }
    }
    private void getChatUsers(){
        // Get dialogs
        //

        ChatService.getInstance().getDialogs(new QBEntityCallback<ArrayList<QBDialog>>() {
            @Override
            public void onSuccess(ArrayList<QBDialog> dialogs, Bundle bundle) {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                ChatSingleton.getInstance().dataSourceUsers.addAll(dialogs);
                adapters.notifyDataSetChanged();

            }

            @Override
            public void onError(QBResponseException errors) {

                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setMessage("get dialogs errors: " + errors).create().show();
            }
        },null);
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
