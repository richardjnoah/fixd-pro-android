package fixtpro.com.fixtpro.utilites.chat_core;

import android.util.Log;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBPrivateChat;
import com.quickblox.chat.QBPrivateChatManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBMessageListenerImpl;
import com.quickblox.chat.listeners.QBMessageSentListener;
import com.quickblox.chat.listeners.QBPrivateChatManagerListener;
import com.quickblox.chat.model.QBChatMessage;


import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import fixtpro.com.fixtpro.ChatActivity;

public class PrivateChatImpl extends QBMessageListenerImpl<QBPrivateChat> implements Chat, QBPrivateChatManagerListener,
        QBMessageSentListener<QBPrivateChat>{

    private static final String TAG = "PrivateChatManagerImpl";

    private ChatActivity chatActivity;

    private QBPrivateChatManager privateChatManager;
    private QBPrivateChat privateChat;

    public PrivateChatImpl(ChatActivity chatActivity, Integer opponentID) {
        this.chatActivity = chatActivity;

        initManagerIfNeed();

        // initIfNeed private chat
        //
        privateChat = privateChatManager.getChat(opponentID);
        if (privateChat == null) {
            privateChat = privateChatManager.createChat(opponentID, this);
        }else{
            privateChat.addMessageListener(this);
            privateChat.addMessageSentListener(this);
        }
    }

    private void initManagerIfNeed(){
        if(privateChatManager == null){
            privateChatManager = QBChatService.getInstance().getPrivateChatManager();

            privateChatManager.addPrivateChatManagerListener(this);
        }
    }

    @Override
    public void sendMessage(QBChatMessage message) throws XMPPException, SmackException.NotConnectedException {
        privateChat.sendMessage(message);
    }

    @Override
    public void release() {
        Log.w(TAG, "release private chat");
        privateChat.removeMessageListener(this);
        privateChatManager.removePrivateChatManagerListener(this);
    }

    @Override
    public void processMessage(QBPrivateChat chat, QBChatMessage message) {
        Log.w(TAG, "new incoming message: " + message);
        chatActivity.showMessage(message);
    }

    @Override
    public void processError(QBPrivateChat chat, QBChatException error, QBChatMessage originChatMessage){

    }

    @Override
    public void chatCreated(QBPrivateChat incomingPrivateChat, boolean createdLocally) {
        if(!createdLocally){
            privateChat = incomingPrivateChat;
            privateChat.addMessageListener(PrivateChatImpl.this);
            privateChat.addMessageSentListener(PrivateChatImpl.this);
        }

        Log.w(TAG, "private chat created: " + incomingPrivateChat.getParticipant() + ", createdLocally:" + createdLocally);
    }

    @Override
    public void processMessageSent(QBPrivateChat qbPrivateChat, QBChatMessage qbChatMessage) {
        Toast.makeText(chatActivity, "message was sent to " + qbChatMessage.getRecipientId(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void processMessageFailed(QBPrivateChat qbPrivateChat, QBChatMessage qbChatMessage) {
        Toast.makeText(chatActivity, "message sent failed to " + qbChatMessage.getRecipientId(), Toast.LENGTH_LONG).show();
    }
}
