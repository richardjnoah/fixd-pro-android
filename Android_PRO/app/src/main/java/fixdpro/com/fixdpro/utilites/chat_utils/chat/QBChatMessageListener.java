package fixdpro.com.fixdpro.utilites.chat_utils.chat;

import com.quickblox.chat.QBChat;
import com.quickblox.chat.model.QBChatMessage;

public interface QBChatMessageListener {

    void onQBChatMessageReceived(QBChat chat, QBChatMessage message);

}
