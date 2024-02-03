package com.example.room

import com.example.data.MessageDataSource
import com.example.data.model.Message
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class RoomController(private val messageDataSource: MessageDataSource) {
    private val members = ConcurrentHashMap<String, Member>()

    fun onJoin(userName: String, sessionId: String, socket: WebSocketSession) {
        if (members.containsKey(userName)) {
            throw MemberAlreadyExistException()
        }

        members[userName] = Member(userName, sessionId, socket)
    }

    suspend fun sendMessage(senderUserName:String,message:String){
        members.values.forEach {member ->
            val messageEntity = Message(text = message, userName = senderUserName, timeStamp = System.currentTimeMillis())
            messageDataSource.insertMessage(messageEntity)
            val parsedMessage = Json.encodeToString(messageEntity)
            member.socket.send(Frame.Text(parsedMessage))
        }
    }

    suspend fun getAllMessages():List<Message>{
        return messageDataSource.getAllMessages()
    }

    suspend fun tryDisconnect(userName:String){
        members[userName]?.socket?.close()
        if(members.containsKey(userName)) members.remove(userName)
    }
}