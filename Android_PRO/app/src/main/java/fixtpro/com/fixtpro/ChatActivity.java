package fixtpro.com.fixtpro;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBProgressCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.squareup.picasso.Picasso;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;


import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import fixdpro.com.fixdpro.R;
import fixtpro.com.fixtpro.adapters.ChatAdapter;
import fixtpro.com.fixtpro.beans.RatingListModal;
import fixtpro.com.fixtpro.imageupload.ImageHelper2;
import fixtpro.com.fixtpro.utilites.ChatService;
import fixtpro.com.fixtpro.utilites.MessageSingleton;
import fixtpro.com.fixtpro.utilites.Utilities;
import fixtpro.com.fixtpro.utilites.chat_core.ApplicationSessionStateCallback;
import fixtpro.com.fixtpro.utilites.chat_core.Chat;
import fixtpro.com.fixtpro.utilites.chat_core.GroupChatImpl;
import fixtpro.com.fixtpro.utilites.chat_core.PrivateChatImpl;
import fixtpro.com.fixtpro.views.RatingBarView;


public class ChatActivity extends AppCompatActivity implements ApplicationSessionStateCallback {

    private static final String TAG = ChatActivity.class.getSimpleName();

    public static final String EXTRA_DIALOG = "dialog";
    private final String PROPERTY_SAVE_TO_HISTORY = "save_to_history";

    private EditText messageEditText;
    private ListView messagesContainer;
    private Button sendButton;
    private ImageView imageSend;
    private ProgressBar progressBar;
    private ChatAdapter adapter;
    Typeface fontfamily;
    private Chat chat;
    private QBDialog dialog;
    Context _context = this ;
    Dialog dialog1 = null;
    public String Path,path;
    public Uri selectedImageUri;
//    private View stickersFrame;
//    private boolean isStickersFrameVisible;
//    private ImageView stickerButton;
    private RelativeLayout container;
    private static final int CAMERA_REQUEST = 1;
    private static final int GALLERY_REQUEST = 2;
    ArrayList<QBChatMessage> chatlist  = new ArrayList<QBChatMessage>();
    Toolbar toolbar;
    TextView cancel;
    private CoordinatorLayout coordinatorLayout;
    TextView txtToolbar;
    public static void start(Context context, Bundle bundle) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setToolbar();
        initViews();
//        chatlist = MessageSingleton.getInstance().chatlist;
        adapter = new ChatAdapter(ChatActivity.this, chatlist);
        messagesContainer.setAdapter(adapter);
        // Init chat if the session is active
        //
        if (!getInternetStatus())
//        if (isSessionActive()) {
            initChat();
//        }

        ChatService.getInstance().addConnectionListener(chatConnectionListener);
    }
    private boolean getInternetStatus(){
        if (!Utilities.isNetworkAvailable(this)){
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
            return true ;
        }
    }
    private void setToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cancel = (TextView)toolbar.findViewById(R.id.cancel);
        txtToolbar = (TextView)toolbar.findViewById(R.id.txtToolbar);
        txtToolbar.setTypeface(fontfamily);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        ChatService.getInstance().removeConnectionListener(chatConnectionListener);
    }

    @Override
    public void onBackPressed() {
//        if (isStickersFrameVisible) {
//            setStickersFrameVisible(false);
//            stickerButton.setImageResource(R.drawable.ic_action_insert_emoticon);
//        } else {
//            try {
//                chat.release();
//            } catch (XMPPException e) {
//                Log.e(TAG, "failed to release chat", e);
//            }
//            super.onBackPressed();
//
//            Intent i = new Intent(ChatActivity.this, DialogsActivity.class);
//            startActivity(i);
            finish();
//        }
    }

    private void initViews() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageEditText = (EditText) findViewById(R.id.messageEdit);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        TextView companionLabel = (TextView) findViewById(R.id.companionLabel);
        imageSend = (ImageView)findViewById(R.id.imageSend);
        fontfamily = Typeface.createFromAsset(getAssets(), "HelveticaNeue-Thin.otf");
        // Setup opponents info
        //
        Intent intent = getIntent();
        dialog = (QBDialog) intent.getSerializableExtra(EXTRA_DIALOG);
        String name = (String) intent.getSerializableExtra("name");
        txtToolbar.setText(name);
        container = (RelativeLayout) findViewById(R.id.container);
        if (dialog.getType() == QBDialogType.GROUP) {
            TextView meLabel = (TextView) findViewById(R.id.meLabel);
            container.removeView(meLabel);
            container.removeView(companionLabel);
        } else if (dialog.getType() == QBDialogType.PRIVATE) {
            Integer opponentID = ChatService.getInstance().getOpponentIDForPrivateDialog(dialog);
            companionLabel.setText(ChatService.getInstance().getDialogsUsers().get(opponentID).getLogin());
        }

        // Send button
        //
        sendButton = (Button) findViewById(R.id.chatSendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getInternetStatus()){
                    String messageText = messageEditText.getText().toString();
                    if (TextUtils.isEmpty(messageText)) {
                        return;
                    }
                    sendChatMessage(messageText);
                }


            }
        });
        imageSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamraGalleryPopUp();
            }
        });
        messagesContainer.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                try {
                    if (listIsAtTop()) {
                        loadNextPage((chatlist.get(0).getDateSent()));
                    }
                } catch (Exception e) {

                }

            }
        });

    }
    /*Create Camra Gallery PopUP*/
    private void showCamraGalleryPopUp() {
        dialog1 = new Dialog(_context);
        dialog1 = new Dialog(_context);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.dialog_camra_gallery);
        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // set the custom dialog components - text, image and button
        ImageView img_close = (ImageView) dialog1.findViewById(R.id.img_close);
        TextView txtTakePicture = (TextView) dialog1.findViewById(R.id.txtTakePicture);
        TextView txtCamera = (TextView) dialog1.findViewById(R.id.txtCamera);
        TextView txtGallery = (TextView) dialog1.findViewById(R.id.txtGallery);
        // set the typeface...
        txtCamera.setTypeface(fontfamily);
        txtGallery.setTypeface(fontfamily);
        txtTakePicture.setTypeface(fontfamily);
        // set the click listner...
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
            }
        });
        txtCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
                openCamera();
            }
        });
        txtGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
                openGallery();
            }
        });
        dialog1.show();
    }
    private  void openCamera(){
        Intent camraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camraIntent, CAMERA_REQUEST);
    }
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_REQUEST);
    }
    private void showKeyboard() {
        ((InputMethodManager) messageEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(messageEditText, InputMethodManager.SHOW_IMPLICIT);
    }

    private void sendChatMessage(String messageText) {
        QBChatMessage chatMessage = new QBChatMessage();
        chatMessage.setBody(messageText);
        chatMessage.setProperty(PROPERTY_SAVE_TO_HISTORY, "1");
        chatMessage.setDateSent(new Date().getTime() / 1000);
        try {
            chat.sendMessage(chatMessage);

        } catch (XMPPException e) {
            Log.e(TAG, "failed to send a message", e);
        } catch (SmackException sme) {
            Log.e(TAG, "failed to send a message", sme);
        }

        messageEditText.setText("");

        if (dialog.getType() == QBDialogType.PRIVATE) {
            showMessage(chatMessage);
        }
    }

        public void setContentBottomPadding(int padding) {
        container.setPadding(0, 0, 0, padding);
    }

    private void initChat() {

        if (dialog.getType() == QBDialogType.GROUP) {
            chat = new GroupChatImpl(this);

            // Join group chat
            //
//            progressBar.setVisibility(View.VISIBLE);

            //
            joinGroupChat();

        } else if (dialog.getType() == QBDialogType.PRIVATE) {
            Integer opponentID = ChatService.getInstance().getOpponentIDForPrivateDialog(dialog);

            chat = new PrivateChatImpl(this, opponentID);

            // Load CHat history
            //
            loadChatHistory();
        }
    }

    private void joinGroupChat(){
        ((GroupChatImpl) chat).joinGroupChat(dialog, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void result, Bundle bundle) {

                // Load Chat history
                //
                loadChatHistory();
            }

            @Override
            public void onError(QBResponseException list) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ChatActivity.this);
                dialog.setMessage("error when join group chat: " + list.toString()).create().show();
            }
        });
    }

    private void loadChatHistory() {
        QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
        customObjectRequestBuilder.setLimit(10);
        customObjectRequestBuilder.sortDesc("date_sent");

        QBChatService.getDialogMessages(dialog, customObjectRequestBuilder, new QBEntityCallback<ArrayList<QBChatMessage>>() {
            @Override
            public void onSuccess(ArrayList<QBChatMessage> messages, Bundle args) {

//                for (int i = messages.size() - 1; i >= 0; --i) {
//                    QBChatMessage msg = messages.get(i);
//                    showMessage(msg);
//                    Log.e("","chatlist"+msg.getId());
//                }
                for (int i = 0; i < messages.size() - 1; i++) {
                    if (messages.get(i).getProperties().containsKey("time")) {
                        continue;
                    }
                    Calendar cal1 = Calendar.getInstance();
                    Calendar cal2 = Calendar.getInstance();
                    cal1.setTimeInMillis(messages.get(i).getDateSent() * 1000);
                    cal2.setTimeInMillis(messages.get(i + 1).getDateSent() * 1000);
                    boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
                    if (!sameDay) {
                        QBChatMessage qb = new QBChatMessage();
                        qb.setProperty("time", "time");
                        qb.setAttachments(new ArrayList<QBAttachment>());
                        messages.add(i+1, qb);

                    }

                }
                Collections.reverse(messages);
                chatlist.addAll(messages);
                handler.sendEmptyMessage(1);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(QBResponseException errors) {
                if (!ChatActivity.this.isFinishing()) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ChatActivity.this);
                    dialog.setMessage("load chat history errors: " + errors).create().show();
                }
            }
        });
    }
    private void refreshList(){
        runOnUiThread(new Runnable() {
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
    private void loadNextPage(long id){
        QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
        customObjectRequestBuilder.setLimit(10);
        customObjectRequestBuilder.sortDesc("date_sent");
        customObjectRequestBuilder.lt("date_sent", id);
        QBChatService.getDialogMessages(dialog, customObjectRequestBuilder, new QBEntityCallback<ArrayList<QBChatMessage>>() {
            @Override
            public void onSuccess(ArrayList<QBChatMessage> messages, Bundle args) {

//                adapter = new ChatAdapter(ChatActivity.this, chatlist);
//                messagesContainer.setAdapter(adapter);
//            ArrayList<QBChatMessage>  arrayList = new ArrayList<QBChatMessage>();
//                for (int i = messages.size() - 1; i >= 0; --i) {
//                    QBChatMessage msg = messages.get(i);
//                    Log.e("",""+msg.getDateSent());
//                    arrayList.add(msg);
////                    chatlist.add(0,msg);
//                }
//                ArrayList<QBChatMessage>  arrayList = messages;
                for (int i = 0 ; i < messages.size() - 1 ; i++){
                    if (messages.get(i).getProperties().containsKey("time")){
                        continue;
                    }
                    Calendar cal1   = Calendar.getInstance();
                    Calendar cal2   = Calendar.getInstance();
                    cal1.setTimeInMillis(messages.get(i).getDateSent()*1000);
                    cal2.setTimeInMillis(messages.get(i + 1).getDateSent() * 1000);
                    boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
                    if (!sameDay){
                    QBChatMessage qb = new QBChatMessage();
                    qb.setProperty("time", "time");
                    qb.setAttachments(new ArrayList<QBAttachment>());
                    messages.add(i+1,qb);

                    }

                }
//                showMessageNew(messages);
                if (messages.size() > 0){
//                    QBChatMessage qb = new QBChatMessage();
//                    qb.setProperty("time", "time");
//                    qb.setAttachments(new ArrayList<QBAttachment>());
//                    messages.add(qb);
                    Collections.reverse(messages);
                    chatlist.addAll(0, messages);
                    handler.sendEmptyMessage(0);

                }

            }

            @Override
            public void onError(QBResponseException errors) {
                if (!ChatActivity.this.isFinishing()) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ChatActivity.this);
                    dialog.setMessage("load chat history errors: " + errors).create().show();
                }
            }
        });
    }
    private boolean listIsAtTop()   {
        if(messagesContainer.getChildCount() == 0) return true;
        return messagesContainer.getChildAt(0).getTop() == 0;
    }
    public void showMessage(QBChatMessage message) {
        chatlist.add(message);


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                adapter.notifyDataSetChanged();
                handler.sendEmptyMessage(1);
//                scrollDown();
            }
        });
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){

                case 0:{
                    adapter.notifyDataSetChanged();
                    break;
                }
                case 1:{
                    adapter.notifyDataSetChanged();
                    scrollDown();
                }
            }

        }
    };
    public void showMessageNew(ArrayList<QBChatMessage> message) {


        chatlist.addAll(0,message);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
//                adapter.notifyDataSetChanged();
            }
        });

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                adapter.notifyDataSetChanged();
//            }
//        });
    }
    private void scrollDown() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }


    ConnectionListener chatConnectionListener = new ConnectionListener() {
        @Override
        public void connected(XMPPConnection connection) {
            Log.i(TAG, "connected");
        }

        @Override
        public void authenticated(XMPPConnection connection, boolean authenticated) {
            Log.i(TAG, "authenticated");
        }

        @Override
        public void connectionClosed() {
            Log.i(TAG, "connectionClosed");
        }

        @Override
        public void connectionClosedOnError(final Exception e) {
            Log.i(TAG, "connectionClosedOnError: " + e.getLocalizedMessage());

            // leave active room
            //
            if (dialog.getType() == QBDialogType.GROUP) {
                ChatActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((GroupChatImpl) chat).leave();
                    }
                });
            }
        }

        @Override
        public void reconnectingIn(final int seconds) {
            if (seconds % 5 == 0) {
                Log.i(TAG, "reconnectingIn: " + seconds);
            }
        }

        @Override
        public void reconnectionSuccessful() {
            Log.i(TAG, "reconnectionSuccessful");

            // Join active room
            //
            if (dialog.getType() == QBDialogType.GROUP) {
                ChatActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        joinGroupChat();
                    }
                });
            }
        }

        @Override
        public void reconnectionFailed(final Exception error) {
            Log.i(TAG, "reconnectionFailed: " + error.getLocalizedMessage());
        }
    };


    //
    // ApplicationSessionStateCallback
    //

    @Override
    public void onStartSessionRecreation() {

    }

    @Override
    public void onFinishSessionRecreation(final boolean success) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (success) {
                    initChat();
                }
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (requestCode == CAMERA_REQUEST) {
                try {
//                Bitmap photo = (Bitmap) data.getExtras().get("data");
                    selectedImageUri = data.getData();
//                selectedImageUri = getImageUri(this, photo);
//                if (selectedImageUri == null)
//                    return;
                    Path = ImageHelper2.compressImage(selectedImageUri, this);
//                imgDriver.setAdjustViewBounds(true);
//                    imgDriver.getLayoutParams().height = finalHeight;
//                    imgDriver.getLayoutParams().width = finalWidth;
////                    imgDriver.setImageBitmap(ImageHelper2.decodeSampledBitmapFromFile(Path, finalWidth, finalHeight));
//                    lblAddDriverLicence.setVisibility(View.INVISIBLE);
////                    driver_license_image_file = new File(ImageHelper2.getRealPathFromURI(selectedImageUri.toString(), _context));
////                imgDriver.setImageBitmap(photo);
//
////                imgDriver.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                    Picasso.with(this).load(selectedImageUri)
//                            .into(imgDriver);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == GALLERY_REQUEST) {
                Uri selectedImageUri = data.getData();
                Path = getPath(selectedImageUri);
                uploadFileTask(Path);
//                imgDriver.getLayoutParams().height = finalHeight;
//                imgDriver.getLayoutParams().width = finalWidth;
////                imgDriver.setImageBitmap(ImageHelper2.decodeSampledBitmapFromFile(Path, finalWidth, finalHeight));
//                Picasso.with(this).load(selectedImageUri)
//                        .into(imgDriver);
            }
        }
    }
    private void uploadFileTask(String path){
        File filePhoto = new File(path);
        Boolean fileIsPublic = false;
        QBContent.uploadFileTask(filePhoto, fileIsPublic, null, new QBEntityCallback<QBFile>() {

            @Override
            public void onSuccess(QBFile file, Bundle params) {
                dialog.setPhoto(file.getId().toString());
                QBAttachment atach = new QBAttachment("photo");
                atach.setId(file.getId().toString());
                atach.setUrl(file.getPrivateUrl());
                ArrayList<QBAttachment> aryatch = new ArrayList<QBAttachment>();
                aryatch.add(atach);
                QBChatMessage chatMessage = new QBChatMessage();
//                chatMessage.setBody(messageText);
                chatMessage.setAttachments(aryatch);
                chatMessage.setProperty(PROPERTY_SAVE_TO_HISTORY, "1");
                chatMessage.setDateSent(new Date().getTime() / 1000);
                try {
                    chat.sendMessage(chatMessage);

                } catch (XMPPException e) {
                    Log.e(TAG, "failed to send a message", e);
                } catch (SmackException sme) {
                    Log.e(TAG, "failed to send a message", sme);
                }

                messageEditText.setText("");

                if (dialog.getType() == QBDialogType.PRIVATE) {
//                    showMessage(chatMessage);
                    chatlist.add(chatMessage);
                    handler.sendEmptyMessage(1);
                }
            }

            @Override
            public void onError(QBResponseException errors) {
                // error
                Log.e("","errors");
            }

        }, new QBProgressCallback() {
            @Override
            public void onProgressUpdate(int i) {

            }
        });

    }
    // get path from the gallery...
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}