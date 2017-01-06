package fixdpro.com.fixdpro;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.AmazonSNSAsyncClient;
import com.amazonaws.services.sns.model.PlatformApplicationDisabledException;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.quickblox.chat.QBChat;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBProgressCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import fixdpro.com.fixdpro.adapters.chat_adapters.AttachmentPreviewAdapter;
import fixdpro.com.fixdpro.adapters.chat_adapters.ChatAdapter;
import fixdpro.com.fixdpro.fragment.ProgressDialogFragment;
import fixdpro.com.fixdpro.utilites.ChatSingleton;
import fixdpro.com.fixdpro.utilites.Constants;
import fixdpro.com.fixdpro.utilites.JSONParser;
import fixdpro.com.fixdpro.utilites.Preferences;
import fixdpro.com.fixdpro.utilites.Utilities;
import fixdpro.com.fixdpro.utilites.chat_utils.ImagePickHelper;
import fixdpro.com.fixdpro.utilites.chat_utils.OnImagePickedListener;
import fixdpro.com.fixdpro.utilites.chat_utils.Toaster;
import fixdpro.com.fixdpro.utilites.chat_utils.chat.Chat;
import fixdpro.com.fixdpro.utilites.chat_utils.chat.ChatHelper;
import fixdpro.com.fixdpro.utilites.chat_utils.chat.GroupChatImpl;
import fixdpro.com.fixdpro.utilites.chat_utils.chat.PrivateChatImpl;
import fixdpro.com.fixdpro.utilites.chat_utils.chat.QBChatMessageListener;
import fixdpro.com.fixdpro.utilites.chat_utils.qb.PaginationHistoryListener;
import fixdpro.com.fixdpro.utilites.chat_utils.qb.QbDialogUtils;
import fixdpro.com.fixdpro.utilites.chat_utils.qb.VerboseQbChatConnectionListener;
import fixdpro.com.fixdpro.views.AttachmentPreviewAdapterView;
import fixdpro.com.fixdpro.views.StickyListHeadersListView;

public class ChatActivityNew extends BaseActivityChat implements OnImagePickedListener {
    private static final String TAG = ChatActivityNew.class.getSimpleName();
    private static final int REQUEST_CODE_ATTACHMENT = 721;
    private static final int REQUEST_CODE_SELECT_PEOPLE = 752;

    private static final String EXTRA_DIALOG = "dialog";
    private static final String PROPERTY_SAVE_TO_HISTORY = "save_to_history";

    public static final String EXTRA_MARK_READ = "markRead";
    public static final String EXTRA_DIALOG_ID = "dialogId";

    private ProgressBar progressBar,progress_message_attachment;
    private StickyListHeadersListView messagesListView;
    private EditText messageEditText;
    private TextView txtToolbar,txtBack;

    private LinearLayout attachmentPreviewContainerLayout;
    private Snackbar snackbar;

    private ChatAdapter chatAdapter;
    private AttachmentPreviewAdapter attachmentPreviewAdapter;
    private ConnectionListener chatConnectionListener;

    private Chat chat;
    private QBDialog qbDialog;
    private ArrayList<String> chatMessageIds;
    private ArrayList<QBChatMessage> unShownMessages;
    private int skipPagination = 0;
     Dialog  progressDialog ;
    int READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE,CAMERA;
    final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 101;
    ArrayList<String> endpoint_arn = new ArrayList<String>();
    String current_job_id = "";
    /**
     * Constant used in the location settings dialog.
     */

    SharedPreferences _prefs = null ;

    AmazonSNSAsync sns ;
    public static Boolean inBackground = true;
    public static void startForResult(Activity activity, int code, QBDialog dialog) {
        Intent intent = new Intent(activity, ChatActivityNew.class);
        intent.putExtra(ChatActivityNew.EXTRA_DIALOG, dialog);
        activity.startActivityForResult(intent, code);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_new);
        _prefs = Utilities.getSharedPreferences(this);
        txtToolbar = (TextView)findViewById(R.id.txtToolbar);
        txtBack = (TextView)findViewById(R.id.txtBack);

        qbDialog = (QBDialog) getIntent().getSerializableExtra(EXTRA_DIALOG);
//        try {
//            JSONObject jsonObject = new JSONObject(qbDialog.getPhoto());
//            Iterator<String> iter = jsonObject.keys();
//            while (iter.hasNext()) {
//                String key = iter.next();
//                try {
//                    Object value = jsonObject.get(key);
//                    JSONObject object = new JSONObject(value.toString());
//                    txtToolbar.setText(object.getString("name"));
//
//                    break;
//                } catch (JSONException e) {
//                    // Something went wrong!
//                }
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        try {
            JSONObject jsonObject = new JSONObject(qbDialog.getPhoto());
            Iterator<String> iter = jsonObject.keys();
            String QB_LOGIN = _prefs.getString(Preferences.QB_ACCOUNT_ID,"");
            while (iter.hasNext()) {
                String key = iter.next();
                if (key.equals("job_id")){
                    current_job_id = jsonObject.getString(key);
                }

                if (!key.equals(QB_LOGIN)){
                    try {
                        Object value = jsonObject.get(key);
                        JSONObject object = new JSONObject(value.toString());
                        txtToolbar.setText(object.getString("name"));
//                        JSONArray jsonArray = object.getJSONArray("endpoint_arn");
//                        for (int i = 0 ; i < jsonArray.length(); i++){
//                            endpoint_arn.add(jsonArray.getString(i));
//                        }
                    } catch (JSONException e) {
                        // Something went wrong!
                    }
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        chatMessageIds = new ArrayList<>();
        initChatConnectionListener();

        initViews();

        setBackCLickListner();
        try {
            sns = new AmazonSNSAsyncClient(new AWSCredentials() {
                @Override
                public String getAWSAccessKeyId() {
                    return "AKIAJZUJLFZZUEUIYDDA";
                }

                @Override
                public String getAWSSecretKey() {
                    return "d9/0TO/epEnkFzqFxEm9yGCEj//WflcDvnH0LG1Y";
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        sns.setRegion(Region.getRegion(Regions.US_WEST_2));
        getEndPointArn();
    }

    private void setBackCLickListner() {
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        if (qbDialog != null) {
            outState.putSerializable(EXTRA_DIALOG, qbDialog);
        }
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (qbDialog == null) {
            qbDialog = (QBDialog) savedInstanceState.getSerializable(EXTRA_DIALOG);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        inBackground = false;
        ChatSingleton.getInstance().setCurrentQbDialog(qbDialog);
        ChatHelper.getInstance().addConnectionListener(chatConnectionListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        inBackground = true;

        ChatSingleton.getInstance().setCurrentQbDialog(null);
        ChatHelper.getInstance().removeConnectionListener(chatConnectionListener);
    }

    @Override
    public void onBackPressed() {
        releaseChat();
        sendReadMessageId();

        super.onBackPressed();
    }

    @Override
    public void onSessionCreated(boolean success) {
        if (success) {
            initChat();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_chat, menu);
//
//        MenuItem menuItemLeave = menu.findItem(R.id.menu_chat_action_leave);
//        MenuItem menuItemAdd = menu.findItem(R.id.menu_chat_action_add);
//        MenuItem menuItemDelete = menu.findItem(R.id.menu_chat_action_delete);
//        if (qbDialog.getType() == QBDialogType.PRIVATE) {
//            menuItemLeave.setVisible(false);
//            menuItemAdd.setVisible(false);
//        } else {
//            menuItemDelete.setVisible(false);
//        }
//
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        switch (id) {
//            case R.id.menu_chat_action_info:
//                ChatInfoActivity.start(this, qbDialog);
//                return true;
//
//            case R.id.menu_chat_action_add:
//                SelectUsersActivity.startForResult(this, REQUEST_CODE_SELECT_PEOPLE, qbDialog);
//                return true;
//
//            case R.id.menu_chat_action_leave:
//                leaveGroupChat();
//                return true;
//
//            case R.id.menu_chat_action_delete:
//                deleteChat();
//                return true;
//
//            case android.R.id.home:
//                onBackPressed();
//                return true;
//
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    private void sendReadMessageId() {
        Intent result = new Intent();
        result.putExtra(EXTRA_MARK_READ, chatMessageIds);
        result.putExtra(EXTRA_DIALOG_ID, qbDialog.getDialogId());
        setResult(RESULT_OK, result);
    }

    private void leaveGroupChat() {
        ((GroupChatImpl) chat).leaveChatRoom();
        ProgressDialogFragment.show(getSupportFragmentManager());
        ChatHelper.getInstance().leaveDialog(qbDialog, new QBEntityCallback<QBDialog>() {
            @Override
            public void onSuccess(QBDialog qbDialog, Bundle bundle) {
                ProgressDialogFragment.hide(getSupportFragmentManager());
                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                ProgressDialogFragment.hide(getSupportFragmentManager());
                showErrorSnackbar(R.string.error_leave_chat, e, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        leaveGroupChat();
                    }
                });
            }
        });
    }

//    @SuppressWarnings("unchecked")
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            if (requestCode == REQUEST_CODE_SELECT_PEOPLE) {
//                ArrayList<QBUser> selectedUsers = (ArrayList<QBUser>) data.getSerializableExtra(
//                        SelectUsersActivity.EXTRA_QB_USERS);
//
//                updateDialog(selectedUsers);
//            }
//        }
//    }

    @Override
    public void onImagePicked(int requestCode, File file) {
        switch (requestCode) {
            case REQUEST_CODE_ATTACHMENT:
//                attachmentPreviewAdapter.add(file);
//                onSendChatClick(new View(this));
                uploadFile(file);
                break;
        }
    }

    @Override
    public void onImagePickError(int requestCode, Exception e) {
        showErrorSnackbar(0, e, null);
    }

    @Override
    public void onImagePickClosed(int requestCode) {
        // ignore
    }

    @Override
    protected View getSnackbarAnchorView() {
        return findViewById(R.id.list_chat_messages);
    }

    public void onSendChatClick(View view) {
        int totalAttachmentsCount = attachmentPreviewAdapter.getCount();
        Collection<QBAttachment> uploadedAttachments = attachmentPreviewAdapter.getUploadedAttachments();
        if (!uploadedAttachments.isEmpty()) {
            if (uploadedAttachments.size() == totalAttachmentsCount) {
                for (QBAttachment attachment : uploadedAttachments) {
                    sendChatMessage(null, attachment);
                }
            } else {
                Toaster.shortToast(R.string.chat_wait_for_attachments_to_upload);
            }

        }

        String text = messageEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(text)) {
            sendChatMessage(text, null);
            sendNotification(text);
        }

    }
    private  void sendNotification(String text){
        for (int i = 0 ; i < endpoint_arn.size() ; i++){

            String GCM1 = "{\"data\":{\"message\":\"New message \\\"" + text + "\\\"\"" + "," + "\"dialogId\"" +":"+ "\"" + qbDialog.getDialogId() + "\"}}";
            String APNS1 = "{\"dialogId\"" + ":" + "\"" + qbDialog.getDialogId() + "\"" + ","+"\"aps\":{\"alert\":\"New message \\\"" + text +"\\\"\"" + "," +  "\"sound\"" + ":" + "\"default\"}}";
//            String APNS1 = "{\"dialogId\"" + ":" + "\"" + qbDialog.getDialogId() + "\"" + ","+"\"aps\":{\"alert\":\"New message \\\"" + text +"\\\"\" }}";
//            String APNS1 = "{\"dialogId\""+ ":" + "\"" + qbDialog.getDialogId() + "\"" + ","+"\"aps\":{\"alert\"" + ":" + "\"" +  text  + "\"}}";
            final PublishRequest publishRequest = new PublishRequest();
            JSONObject jsonObjectMain = null;
            try {
                jsonObjectMain = new JSONObject();
                jsonObjectMain.put("default",text);
                jsonObjectMain.put("GCM",GCM1);
                jsonObjectMain.put("APNS",APNS1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            publishRequest.setMessage(jsonObjectMain.toString());
//        publishRequest.setMessage(value);
            publishRequest.setMessageStructure("json");
//        publishRequest.setTargetArn("arn:aws:sns:us-west-2:044138592271:endpoint/APNS/FixdPro/9b434c7a-b919-3df8-a9fd-9dfeab57710f");
            publishRequest.setTargetArn(endpoint_arn.get(i));

//        PublishResult publishResult = sns.publish(publishRequest);
            new AsyncTask<Void,Void,Void>(){

                @Override
                protected Void doInBackground(Void... params) {
                    try {

                        PublishResult publishResult = sns.publish(publishRequest);
                        Log.e("",""+publishResult);
                    }catch (PlatformApplicationDisabledException e){
                        e.printStackTrace();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    return null;
                }
            }.execute();

        }





    }
    private void uploadFile(final File item){
     progressDialog = new Dialog(this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.simple_progress_with_percentage, null);
        final TextView txtUploading = (TextView)customView.findViewById(R.id.txtUploading);
        progressDialog.setContentView(customView);
        progressDialog.setCancelable(false);
//        progressDialog.show();
        progress_message_attachment.setVisibility(View.VISIBLE);

        ChatHelper.getInstance().loadFileAsAttachment(item, new QBEntityCallback<QBAttachment>() {
            @Override
            public void onSuccess(QBAttachment result, Bundle params) {

                sendChatMessage(null, result);
                progress_message_attachment.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onError(QBResponseException e) {
//                progressDialog.dismiss();
//                onAttachmentUploadErrorListener.onAttachmentUploadError(e);
//                remove(item);
                progress_message_attachment.setVisibility(View.INVISIBLE);
            }
        }, new QBProgressCallback() {
            @Override
            public void onProgressUpdate(final int progress) {
                txtUploading.setText(""+progress+"%");
//                fileUploadProgressMap.put(item, progress);
//                mainThreadHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        notifyDataSetChanged();
//                    }
//                });
            }
        });
    }
    public void onAttachmentsClick(View view) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            READ_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            CAMERA = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA);
            List<String> permissionsNeeded = new ArrayList<String>();
            if (READ_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED)
                permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (WRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED)
                permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (CAMERA != PackageManager.PERMISSION_GRANTED)
                permissionsNeeded.add(Manifest.permission.CAMERA);

            if (permissionsNeeded.size() > 0) {
                requestPermissions(permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            } else {
                new ImagePickHelper().pickAnImage(this, REQUEST_CODE_ATTACHMENT);
            }
        }else{
            new ImagePickHelper().pickAnImage(this, REQUEST_CODE_ATTACHMENT);
        }
    }

    public void showMessage(QBChatMessage message) {
        if (chatAdapter != null) {
            chatAdapter.add(message);
            scrollMessageListDown();
        } else {
            if (unShownMessages == null) {
                unShownMessages = new ArrayList<>();
            }
            unShownMessages.add(message);
        }
    }

    private void initViews() {
//        actionBar.setDisplayHomeAsUpEnabled(true);

        messagesListView = _findViewById(R.id.list_chat_messages);
        messageEditText = _findViewById(R.id.edit_chat_message);
        progressBar = _findViewById(R.id.progress_chat);
        progress_message_attachment = _findViewById(R.id.progress_message_attachment);
        attachmentPreviewContainerLayout = _findViewById(R.id.layout_attachment_preview_container);

        attachmentPreviewAdapter = new AttachmentPreviewAdapter(this,
                new AttachmentPreviewAdapter.OnAttachmentCountChangedListener() {
                    @Override
                    public void onAttachmentCountChanged(int count) {
                        attachmentPreviewContainerLayout.setVisibility(count == 0 ? View.GONE : View.VISIBLE);
                    }
                },
                new AttachmentPreviewAdapter.OnAttachmentUploadErrorListener() {
                    @Override
                    public void onAttachmentUploadError(QBResponseException e) {
                        showErrorSnackbar(0, e, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onAttachmentsClick(v);
                            }
                        });
                    }
                });
        AttachmentPreviewAdapterView previewAdapterView = _findViewById(R.id.adapter_view_attachment_preview);
        previewAdapterView.setAdapter(attachmentPreviewAdapter);
    }

    private void sendChatMessage(String text, QBAttachment attachment) {
        QBChatMessage chatMessage = new QBChatMessage();
        if (attachment != null) {
            chatMessage.addAttachment(attachment);

        } else {
            chatMessage.setBody(text);
        }
        chatMessage.setProperty(PROPERTY_SAVE_TO_HISTORY, "1");
        chatMessage.setDateSent(System.currentTimeMillis() / 1000);

        try {
            chat.sendMessage(chatMessage);

            if (qbDialog.getType() == QBDialogType.PRIVATE) {
                showMessage(chatMessage);
            }

            if (attachment != null) {
                attachmentPreviewAdapter.remove(attachment);
            } else {
                messageEditText.setText("");
            }
        } catch (XMPPException | SmackException e) {
            Log.e(TAG, "Failed to send a message", e);
            Toaster.shortToast(R.string.chat_send_message_error);
        }
        if (attachment != null ){
//            progressDialog.dismiss();
            sendNotification("Image");
        }
    }

    private void initChat() {
        switch (qbDialog.getType()) {
            case GROUP:
            case PUBLIC_GROUP:
                chat = new GroupChatImpl(chatMessageListener);
                joinGroupChat();
                break;

            case PRIVATE:
                chat = new PrivateChatImpl(chatMessageListener, QbDialogUtils.getOpponentIdForPrivateDialog(qbDialog));
                loadDialogUsers();
                break;

            default:
                Toaster.shortToast(String.format("%s %s", getString(R.string.chat_unsupported_type), qbDialog.getType().name()));
                finish();
                break;
        }
    }

    private void joinGroupChat() {
        progressBar.setVisibility(View.VISIBLE);

        ((GroupChatImpl) chat).joinGroupChat(qbDialog, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void result, Bundle b) {
                if (snackbar != null) {
                    snackbar.dismiss();
                }
                loadDialogUsers();
            }

            @Override
            public void onError(QBResponseException e) {
                progressBar.setVisibility(View.GONE);
                snackbar = showErrorSnackbar(R.string.connection_error, e, null);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        initChat();
//                    }
//                },2000);
            }
        });
    }

    private void leaveGroupChatRoom() {
        if (chat != null) {
            ((GroupChatImpl) chat).leaveChatRoom();
        }
    }

    private void releaseChat() {
        try {
            if (chat != null) {
                chat.release();
            }
        } catch (XMPPException e) {
            Log.e(TAG, "Failed to release chat", e);
        }
    }

    private void updateDialog(final ArrayList<QBUser> selectedUsers) {
        ChatHelper.getInstance().updateDialogUsers(qbDialog, selectedUsers,
                new QBEntityCallback<QBDialog>() {
                    @Override
                    public void onSuccess(QBDialog dialog, Bundle args) {
                        qbDialog = dialog;
                        loadDialogUsers();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        showErrorSnackbar(R.string.chat_info_add_people_error, e,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        updateDialog(selectedUsers);
                                    }
                                });
                    }
                }
        );
    }

    private void loadDialogUsers() {
        ChatHelper.getInstance().getUsersFromDialog(qbDialog, new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> users, Bundle bundle) {
//                setChatNameToActionBar();
                loadChatHistory();
            }

            @Override
            public void onError(QBResponseException e) {
                showErrorSnackbar(R.string.chat_load_users_error, e,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadDialogUsers();
                            }
                        });
            }
        });
    }

    private void setChatNameToActionBar() {
        String chatName = QbDialogUtils.getDialogName(qbDialog);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(chatName);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
        }
    }

    private void loadChatHistory() {
        ChatHelper.getInstance().loadChatHistory(qbDialog, skipPagination, new QBEntityCallback<ArrayList<QBChatMessage>>() {
            @Override
            public void onSuccess(ArrayList<QBChatMessage> messages, Bundle args) {
                // The newest messages should be in the end of list,
                // so we need to reverse list to show messages in the right order
                Collections.reverse(messages);
                if (chatAdapter == null) {
                    chatAdapter = new ChatAdapter(ChatActivityNew.this, messages);
                    chatAdapter.setPaginationHistoryListener(new PaginationHistoryListener() {
                        @Override
                        public void downloadMore() {
                            loadChatHistory();
                        }
                    });
                    chatAdapter.setOnItemInfoExpandedListener(new ChatAdapter.OnItemInfoExpandedListener() {
                        @Override
                        public void onItemInfoExpanded(final int position) {
                            if (isLastItem(position)) {
                                // HACK need to allow info textview visibility change so posting it via handler
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        messagesListView.setSelection(position);
                                    }
                                });
                            } else {
                                messagesListView.smoothScrollToPosition(position);
                            }
                        }

                        private boolean isLastItem(int position) {
                            return position == chatAdapter.getCount() - 1;
                        }
                    });
                    if (unShownMessages != null && !unShownMessages.isEmpty()) {
                        List<QBChatMessage> chatList = chatAdapter.getList();
                        for (QBChatMessage message : unShownMessages) {
                            if (!chatList.contains(message)) {
                                chatAdapter.add(message);
                            }
                        }
                    }
                    messagesListView.setAdapter(chatAdapter);
                    messagesListView.setAreHeadersSticky(false);
                    messagesListView.setDivider(null);
                    progressBar.setVisibility(View.GONE);
                } else {
                    chatAdapter.addList(messages);
                    messagesListView.setSelection(messages.size());
                }
            }

            @Override
            public void onError(QBResponseException e) {
                progressBar.setVisibility(View.GONE);
                skipPagination -= ChatHelper.CHAT_HISTORY_ITEMS_PER_PAGE;
                snackbar = showErrorSnackbar(R.string.connection_error, e, null);
            }
        });
        skipPagination += ChatHelper.CHAT_HISTORY_ITEMS_PER_PAGE;
    }

    private void scrollMessageListDown() {
        messagesListView.setSelection(messagesListView.getCount() - 1);
    }

    private void deleteChat() {
        ChatHelper.getInstance().deleteDialog(qbDialog, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                showErrorSnackbar(R.string.dialogs_deletion_error, e,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteChat();
                            }
                        });
            }
        });
    }

    private void initChatConnectionListener() {
        chatConnectionListener = new VerboseQbChatConnectionListener(getSnackbarAnchorView()) {
            @Override
            public void connectionClosedOnError(final Exception e) {
                super.connectionClosedOnError(e);

                // Leave active room if we're in Group Chat
                if (qbDialog.getType() == QBDialogType.GROUP) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            leaveGroupChatRoom();
                        }
                    });
                }
            }

            @Override
            public void reconnectionSuccessful() {
                super.reconnectionSuccessful();
                skipPagination = 0;
                chatAdapter = null;
                switch (qbDialog.getType()) {
                    case PRIVATE:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadChatHistory();
                            }
                        });
                        break;
                    case GROUP:
                        // Join active room if we're in Group Chat
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                joinGroupChat();
                            }
                        });
                        break;
                }
            }
        };
    }

    private QBChatMessageListener chatMessageListener = new QBChatMessageListener() {
        @Override
        public void onQBChatMessageReceived(QBChat chat, QBChatMessage message) {
            chatMessageIds.add(message.getId());
            showMessage(message);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    new ImagePickHelper().pickAnImage(this, REQUEST_CODE_ATTACHMENT);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    private HashMap<String, String> getEndPointArnParams() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("api", "chatroom");
        hashMap.put("object", "jobs");
        hashMap.put("_app_id", "FIXD_ANDROID_PRO");
        hashMap.put("_company_id", "FIXD");
        hashMap.put("job_id", current_job_id);
        hashMap.put("token", _prefs.getString(Preferences.AUTH_TOKEN, ""));
        return hashMap;
    }

    private void getEndPointArn() {
        new AsyncTask<Void, Void, Void>() {
            JSONObject jsonObject = null;

            @Override
            protected Void doInBackground(Void... params) {
                JSONParser jsonParser = new JSONParser();
                jsonObject = jsonParser.makeHttpRequest(Constants.BASE_URL, "POST", getEndPointArnParams());
                if (jsonObject != null) {
                    try {
                        String STATUS = jsonObject.getString("STATUS");
                        if (STATUS.equals("SUCCESS")) {
                            JSONObject RESPONSE = jsonObject.getJSONObject("RESPONSE");
                            JSONObject endpoint = RESPONSE.getJSONObject("endpoint");
                            Iterator<String> iter = endpoint.keys();

                            while (iter.hasNext()) {

                                String key = iter.next();
                                if (!key.equals(_prefs.getString(Preferences.ID,""))) {
                                    JSONArray array = endpoint.getJSONArray(key);
                                    for (int i = 0; i < array.length() ; i++){
                                        if (!array.isNull(i))
                                        endpoint_arn.add(array.getString(i));
                                    }


                                }
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                return null;
            }
        }.execute();
    }
}
